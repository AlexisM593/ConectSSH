package com.programacion.Protocol;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.programacion.Utility.IModificar;
import com.programacion.Utility.LocationDB;
import com.programacion.UxController.UtilityUX.Tool.Result;

public class SecureShell_metod implements IModificar {
    public static String executeCommand(Session session, String command) throws Exception {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("La sesión SSH no está conectada.");
        }

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        InputStream in = channel.getInputStream();

        // Prueba con ISO_8859_1 si ves caracteres raros con UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));

        channel.connect(4000);

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitStatus = channel.getExitStatus();
        channel.disconnect();

        if (exitStatus != 0) {
            throw new RuntimeException("El comando falló con código: " + exitStatus + "\n" + output);
        }

        return output.toString().trim();
    }

    public static CompletableFuture<Void> crearCuentaAsync(List<String> ips, List<String> users, int vigencia,
            boolean habilitar) {
        return CompletableFuture.runAsync(() -> {
            crearCuenta(ips, users, vigencia, habilitar);
        });
    }

    public static List<Result> crearCuenta(List<String> ips, List<String> users, int vigencia, boolean habilitar) {
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
                    String sshKey = LocationDB.getSSHKeyFromDB(ip);
                    String userLogin = LocationDB.getUserLoginFromDB(ip);
                    System.out.println("imprimiendo usaerLO " + sshKey + userLogin + ip);
                    session = conectarSSH(ip, userLogin, sshKey);
                    for (String userObj : users) {

                        String user = userObj.trim().replaceAll("[^a-zA-Z0-9_-]", "");


                        // boolean exists = checkUserExists(session, user);
                        String createUserCmd = "sudo useradd -m -s /bin/false " + user;
                        String setPasswordCmd = "echo \"" + user + ":" + user + "\" | sudo chpasswd";
                        String setSessionLimit = "echo \"" + user
                                + " hard maxlogins 3\" | sudo tee -a /etc/security/limits.conf";
                        String setExpiration = "sudo chage -E \"$(date -d '+" + vigenciaF + " days' '+%Y-%m-%d')\" "
                                + user;

                        String comandoHabilitar = "";
                        if (habilitar) {
                            comandoHabilitar = "sudo passwd -u " + user;
                        } else {
                            comandoHabilitar = "sudo passwd -l " + user;
                        }

                        String fullCommand = String.join(" && ",
                                createUserCmd,
                                setPasswordCmd,
                                setSessionLimit,
                                setExpiration,
                                comandoHabilitar);

                        fullCommand = "bash -c \"" + fullCommand + "\"";
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

    public static CompletableFuture<Void> modificarCuentaAsync(List<String> ips, List<String> users, int vigencia,
            Boolean habilitar) {
        return CompletableFuture.runAsync(() -> {
            modificarCuenta(ips, users, vigencia, habilitar);
        });
    }

    public static List<String> modificarCuenta(List<String> ips, List<String> users, int vigencia,
            Boolean habilitar) {
        List<String> resultadosGlobales = new ArrayList<>();

        final int vigenciaF = vigencia < 1 ? 1 : vigencia;

        for (String ip : ips) {
            ip = ip.trim();
            if (!ip.equals("0.0.0.0")) {
                Session sesion = null;
                try {
                    String claveSSH = LocationDB.getSSHKeyFromDB(ip);
                    String userLogin = LocationDB.getUserLoginFromDB(ip);

                    sesion = conectarSSH(ip, userLogin, claveSSH);

                    for (String userHiwd : users) {

                        // Comandos individuales
                        List<String> comandos = new ArrayList<>();

                        if (!checkUserExists(sesion, userHiwd)) {
                            comandos.add("sudo useradd -m -s /bin/false " + userHiwd);
                        }

                        comandos.add("echo \"" + userHiwd + ":" + userHiwd + "\" | sudo chpasswd");

                        comandos.add(
                                "echo \"" + userHiwd + " hard maxlogins 3\" | sudo tee -a /etc/security/limits.conf");

                        comandos.add("sudo chage -E \"$(date -d '" + vigenciaF + " days' '+%Y-%m-%d')\" " + userHiwd);

                        String comandoHabilitar;
                        if (habilitar) {
                            comandoHabilitar = "sudo passwd -u " + userHiwd;
                        } else {
                            comandoHabilitar = "sudo passwd -l " + userHiwd;
                        }
                        comandos.add(comandoHabilitar);

                        // Unir comandos con &&
                        String comandoCompleto = "bash -c \"" + String.join(" && ", comandos) + "\"";

                        System.out.println("MODIFICANDO CUENTA 2");

                        executeCommand(sesion, comandoCompleto);
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

    public static CompletableFuture<Void> eliminarCuentaAsync(List<String> users, List<String> ips) {
        return CompletableFuture.runAsync(() -> {
            eliminarCuenta(ips, users);
        });
    }

    public static List<Result> eliminarCuenta(List<String> users, List<String> ips) {
        List<Result> resultados = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(ips.size());

        List<Callable<Void>> tareas = new ArrayList<>();

        for (String ip : ips) {
            if (ip.equals("0.0.0.0"))
                continue;

            tareas.add(() -> {
                Session session = null;
                try {
                    String clave = LocationDB.getSSHKeyFromDB(ip); // Debes tener esto implementado
                    session = conectarSSH(ip, "root", clave);

                    for (String user : users) {
                        Result res = new Result();
                        res.ip = ip;
                        res.hwid = user;

                        try {
                            if (checkUserExists(session, user)) {
                                try {
                                    executeCommand(session, "sudo timeout 3s pkill -u " + user);
                                } catch (Exception e) {
                                    System.out.println("No hay procesos para " + user);
                                }

                                executeCommand(session, "sudo deluser --remove-home " + user);

                                boolean sigueExistiendo = checkUserExists(session, user);

                                if (sigueExistiendo) {
                                    res.result = "Error: El usuario aún existe.";
                                } else {
                                    res.result = "Usuario eliminado con éxito.";
                                }

                            } else {
                                res.result = "El usuario ya no existe.";
                            }

                        } catch (Exception e) {
                            res.error = e.getMessage();
                        }

                        resultados.add(res);
                    }

                } catch (Exception e) {
                    for (String user : users) {
                        Result res = new Result();
                        res.ip = ip;
                        res.hwid = user;
                        res.error = "Fallo de conexión SSH: " + e.getMessage();
                        resultados.add(res);
                    }

                } finally {
                    if (session != null && session.isConnected()) {
                        session.disconnect();
                    }
                }
                return null;
            });
        }

        try {
            executor.invokeAll(tareas);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return resultados;
    }

    private static Session conectarSSH(String ip, String usuario, String clave) throws JSchException {
        JSch jsch = new JSch();
        Session sesion = jsch.getSession(usuario, ip, 22);
        sesion.setPassword(clave);
        sesion.setConfig("StrictHostKeyChecking", "no");
        sesion.connect(8000); // Tiempo de espera: 8 segundos
        return sesion;
    }

}
