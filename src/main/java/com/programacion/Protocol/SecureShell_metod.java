package com.programacion.Protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.programacion.Utility.IModificar;

public class SecureShell_metod implements IModificar {

    private static String getSSHKeyFromDB(String ip) {

        return "claveSSH123";
    }

    public static String executeCommand(Session session, String command) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        InputStream in = channel.getInputStream();
        InputStream err = channel.getErrStream();

        channel.connect(4000);

        byte[] tmp = new byte[1024];
        StringBuilder output = new StringBuilder();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
                output.append(new String(tmp, 0, i));
            }

            if (channel.isClosed())
                break;
            Thread.sleep(100);
        }

        int exitStatus = channel.getExitStatus();
        channel.disconnect();

        if (exitStatus != 0) {
            throw new RuntimeException("El comando falló con código: " + exitStatus + "\n" + output);
        }

        return output.toString();
    }

    public static class Result {
        public String ip;
        public String hwid;
        public String result;
        public String error;

        @Override
        public String toString() {
            return "___________\nUbicación: " + ip +
                    "\nHWID: " + hwid +
                    (result != null ? "\nResultado: " + result : "\nError: " + error) + "\n";
        }
    }

    public static List<Result> crearCuenta(List<String> ips, List<String> users, int vigencia, int logIn) {
        final int vigenciaF = vigencia < 1 ? 1 : vigencia;

        List<Result> overallResults = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(ips.size());

        List<Callable<Void>> tasks = new ArrayList<>();

        for (String ipn : ips) {
            final String ip = ipn.trim();
            if (ip.equals("0.0.0.0"))
                continue;

            tasks.add(() -> {
                Session session = null;
                try {
                    String sshKey = getSSHKeyFromDB(ip); // debes implementar esto
                    session = conectarSSH(ip, "root", sshKey);
                    for (String userObj : users) {
                        String user = userObj;
                        String password = userObj;

                        boolean exists = checkUserExists(session, user);

                        String createUserCmd = exists ? "" : "sudo useradd -m -s /bin/false " + user + " && ";
                        String setPasswordCmd = "echo \"" + user + ":" + password + "\" | sudo chpasswd";
                        String setSessionLimit = "echo \"" + user + " hard maxlogins " + 3
                                + "\" | sudo tee -a /etc/security/limits.conf";
                        String setExpiration = "sudo chage -E \"$(date -d '+" + vigenciaF + " days' '+%Y-%m-%d')\" "
                                + user;
                        String fullCommand = createUserCmd + setPasswordCmd + " && " + setSessionLimit + " && "
                                + setExpiration;

                        try {
                            String output = executeCommand(session, fullCommand);
                            Result res = new Result();
                            res.ip = ip;
                            res.hwid = user;
                            res.result = output;
                            overallResults.add(res);
                        } catch (Exception e) {
                            Result res = new Result();
                            res.ip = ip;
                            res.hwid = user;
                            res.error = e.getMessage();
                            overallResults.add(res);
                        }
                    }
                } catch (Exception e) {
                    Result res = new Result();
                    res.ip = ip;
                    res.error = e.getMessage();
                    overallResults.add(res);
                } finally {
                    if (session != null && session.isConnected()) {
                        session.disconnect();
                    }
                }
                return null;
            });
        }

        try {
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (int i = 0; i < futures.size(); i++) {
                try {
                    futures.get(i).get();
                    System.out.println("Tarea " + (i + 1) + " completada con éxito");
                } catch (Exception e) {
                    System.err.println("Tarea " + (i + 1) + " falló: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return overallResults;
    }

    private static boolean checkUserExists(Session session, String username) throws Exception {
        try {
            executeCommand(session, "id -u " + username);
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("no such user") || e.getMessage().contains("id:")) {
                return false;
            }
            throw e;
        }
    }

    public List<String> gjActualizarUsuario(List<String> ips, List<String> users, int vigencia, int inicioSesion,
            Boolean habilitar) {
        List<String> resultadosGlobales = new ArrayList<>();

        final int vigenciaF = vigencia < 1 ? 1 : vigencia;

        for (String ip : ips) {
            ip = ip.trim();
            if (!ip.equals("0.0.0.0")) {
                Session sesion = null;
                try {
                    String claveSSH = getSSHKeyFromDB(ip);
                    sesion = conectarSSH(ip, "root", claveSSH);

                    for (String userHiwd : users) {

                        String comandoCrearUsuario = "";
                        if (!comprobarUsuarioExiste(sesion, userHiwd)) {
                            comandoCrearUsuario = "sudo useradd -m -s /bin/false " + userHiwd;
                        }

                        String comandoHabilitarUsuario = habilitar != null
                                ? (habilitar ? "sudo passwd -u " + userHiwd : "sudo passwd -l " + userHiwd)
                                : "";
                        String comandoEstablecerContrasena = "echo \"" + userHiwd + ":" + userHiwd
                                + "\" | sudo chpasswd";
                        String comandoLimitarSesiones = "echo \"" + userHiwd + " hard maxlogins " + inicioSesion
                                + "\" | sudo tee -a /etc/security/limits.conf";
                        String comandoEstablecerExpiracion = "sudo chage -E \"$(date -d '" + vigenciaF
                                + " days' '+%Y-%m-%d')\" " + userHiwd;

                        String comandoCompleto = String.join(" && ", comandoCrearUsuario, comandoEstablecerContrasena,
                                comandoLimitarSesiones, comandoEstablecerExpiracion, comandoHabilitarUsuario);

                        ejecutarComando(sesion, comandoCompleto);
                        resultadosGlobales.add("Usuario actualizado correctamente: " + userHiwd + " en " + ip);
                    }
                } catch (Exception e) {
                    resultadosGlobales.add("Error al procesar la IP: " + ip + " - " + e.getMessage());
                } finally {
                    if (sesion != null && sesion.isConnected()) {
                        sesion.disconnect();
                    }
                }
            }
        }
        return resultadosGlobales;
    }

    public boolean eliminarHwid(String Hwid, String ip, String claveSSH) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession("root", ip, 22);
            session.setPassword(claveSSH);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            System.out.println("Conectando al servidor SSH...");
            session.connect(5000);

            String comando = "sudo userdel -r " + Hwid;
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(comando);
            channel.connect();

            System.out.println("Comando ejecutado: " + comando);

            channel.disconnect();
            session.disconnect();
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    private static Session conectarSSH(String ip, String usuario, String clave) throws JSchException {
        JSch jsch = new JSch();
        Session sesion = jsch.getSession(usuario, ip, 22);
        sesion.setPassword(clave);
        sesion.setConfig("StrictHostKeyChecking", "no");
        sesion.connect(8000); // Tiempo de espera: 8 segundos
        return sesion;
    }

    private boolean comprobarUsuarioExiste(Session sesion, String Hwid) throws Exception {
        String comandoVerificar = "id -u " + Hwid;
        try {
            ejecutarComando(sesion, comandoVerificar);
            return true;
        } catch (Exception e) {
            String mensajeError = e.getMessage().toLowerCase();
            return mensajeError.contains("Hwid no encontrado")
                    || mensajeError.contains("id:") && mensajeError.contains("Hwid no existe");
        }
    }

    private void ejecutarComando(Session sesion, String comando) throws Exception {
        ChannelExec canal = (ChannelExec) sesion.openChannel("exec");
        canal.setCommand(comando);
        canal.setErrStream(System.err);
        canal.connect();

        try {
            canal.disconnect();
        } catch (Exception e) {
            throw new Exception("Error ejecutando comando: " + e.getMessage());
        }
    }

}
