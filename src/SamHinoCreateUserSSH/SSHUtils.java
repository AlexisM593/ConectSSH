package SamHinoCreateUserSSH;
import com.jcraft.jsch.*;
import java.io.InputStream;

public class SSHUtils {
    public static Session connectSSH(String host, String username, String password, int timeout) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        // Evitar verificación estricta del host (solo para pruebas)
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect(timeout);
        return session;
    }

    public static String executeCommand(Session session, String command, String ip, int retries, int delayTime, int timeout) throws Exception {
        for (int attempt = 0; attempt <= retries; attempt++) {
            ChannelExec channel = null;
            InputStream in = null;
            InputStream err = null;
            try {
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                channel.setInputStream(null);
                in = channel.getInputStream();
                err = channel.getErrStream();
                channel.connect(timeout);

                // Leer salida estándar
                StringBuilder output = new StringBuilder();
                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        output.append(new String(tmp, 0, i));
                    }
                    if (channel.isClosed()) {
                        if (in.available() > 0) continue;
                        int exitStatus = channel.getExitStatus();
                        if (exitStatus == 0) {
                            return output.toString();
                        } else {
                            // Leer error
                            StringBuilder errOutput = new StringBuilder();
                            while (err.available() > 0) {
                                int i = err.read(tmp, 0, 1024);
                                if (i < 0) break;
                                errOutput.append(new String(tmp, 0, i));
                            }
                            throw new Exception("El comando falló con código de salida " + exitStatus + ". Error: " + errOutput.toString());
                        }
                    }
                    Thread.sleep(100);
                }

            } catch (Exception e) {
                if (attempt < retries) {
                    Thread.sleep(delayTime);
                } else {
                    throw new Exception("Error al ejecutar el comando: " + e.getMessage());
                }
            } finally {
                if (channel != null && !channel.isClosed()) {
                    channel.disconnect();
                }
            }
        }
        throw new Exception("Error inesperado al ejecutar el comando.");
    }

    public static boolean checkUserExists(Session session, String username, String ip) throws Exception {
        String command = "id -u " + username;
        try {
            executeCommand(session, command, ip, 0, 0, 4000);
            return true;
        } catch (Exception e) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("no such user") || msg.contains("no such file or directory") || (msg.contains("id:") && msg.contains("no such"))) {
                return false;
            }
            throw new Exception("Error al comprobar existencia del usuario: " + e.getMessage());
        }
    }
}
