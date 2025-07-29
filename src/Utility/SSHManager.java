package Utility;

import com.jcraft.jsch.*;
import java.util.List;
import java.util.ArrayList;

public class SSHManager {

    // Clase para representar un usuario con nombre y contraseña
    public static class Usuario {
        private String nombreUsuario;
        private String contrasena;

        public Usuario(String nombreUsuario, String contrasena) {
            this.nombreUsuario = nombreUsuario;
            this.contrasena = contrasena;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public String getContrasena() {
            return contrasena;
        }
    }

    // Método para obtener la clave SSH de una IP (debes implementar la lógica real)
    private String obtenerClaveSSH(String ip) {
        // TODO: Cambia esta implementación para obtener la clave real (ej. base de datos)
        return "tu_clave_ssh_aqui";
    }

    // Método para conectar vía SSH usando JSch
    private Session conectarSSH(String ip, String usuario, String clave) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(usuario, ip, 22);
        session.setPassword(clave);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(8000); // Timeout 8 segundos
        return session;
    }

    // Método para ejecutar un comando remoto vía SSH y esperar a que termine
    private void ejecutarComando(Session session, String comando) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(comando);
        channel.setErrStream(System.err);
        channel.connect();

        while (!channel.isClosed()) {
            Thread.sleep(100);
        }

        int exitStatus = channel.getExitStatus();
        channel.disconnect();

        if (exitStatus != 0) {
            throw new Exception("Comando falló con código: " + exitStatus);
        }
    }

    // Método para comprobar si un usuario existe en el servidor
    private boolean usuarioExiste(Session session, String nombreUsuario) throws Exception {
        try {
            ejecutarComando(session, "id -u " + nombreUsuario);
            return true;
        } catch (Exception e) {
            String msg = e.getMessage().toLowerCase();
            return msg.contains("no such user") || msg.contains("no such file or directory");
        }
    }

    /**
     * Crea o actualiza usuarios en servidores SSH
     *
     * @param ips Lista de direcciones IP de los servidores
     * @param usuarios Lista de objetos Usuario (nombre y contraseña)
     * @param vencimiento Días hasta que la cuenta expira (mínimo 1)
     * @param maxSesiones Límite máximo de sesiones simultáneas
     * @return Lista de resultados (mensajes) por cada IP/usuario procesado
     */
    public List<String> crearUsuarios(List<String> ips, List<Usuario> usuarios, int vencimiento, int maxSesiones) {
        List<String> resultados = new ArrayList<>();

        if (vencimiento < 1) {
            vencimiento = 1;
        }

        for (String ip : ips) {
            ip = ip.trim();
            if (!ip.equals("0.0.0.0")) {
                Session session = null;
                try {
                    String claveSSH = obtenerClaveSSH(ip);
                    session = conectarSSH(ip, "root", claveSSH);

                    for (Usuario usuario : usuarios) {
                        String nombre = usuario.getNombreUsuario();
                        String pass = usuario.getContrasena();

                        boolean existe = usuarioExiste(session, nombre);

                        String comandoCrear = existe ? "" : "sudo useradd -m -s /bin/false " + nombre;
                        String setPass = "echo \"" + nombre + ":" + pass + "\" | sudo chpasswd";
                        String setLimite = "echo \"" + nombre + " hard maxlogins " + maxSesiones + "\" | sudo tee -a /etc/security/limits.conf";
                        String setExpiracion = "sudo chage -E \"$(date -d '+" + vencimiento + " days' '+%Y-%m-%d')\" " + nombre;

                        String comandoCompleto = comandoCrear + " && " + setPass + " && " + setLimite + " && " + setExpiracion;

                        ejecutarComando(session, comandoCompleto);

                        resultados.add("Usuario " + nombre + " creado/actualizado en " + ip);
                    }
                } catch (Exception e) {
                    resultados.add("Error en IP " + ip + ": " + e.getMessage());
                } finally {
                    if (session != null && session.isConnected()) {
                        session.disconnect();
                    }
                }
            }
        }
        return resultados;
    }

    /**
     * Elimina un usuario remoto vía SSH
     *
     * @param usuario Nombre del usuario a eliminar
     * @param ip IP del servidor
     * @param claveSSH Clave SSH del servidor
     * @return true si se eliminó correctamente, false si hubo error
     */
    public boolean eliminarUsuario(String usuario, String ip, String claveSSH) {
        Session session = null;
        try {
            session = conectarSSH(ip, "root", claveSSH);

            String comando = "sudo userdel -r " + usuario;
            ejecutarComando(session, comando);

            return true;
        } catch (Exception e) {
            System.err.println("Error eliminando usuario: " + e.getMessage());
            return false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}