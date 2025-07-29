package SamuelHinoSSHConnections_beforeRemove.Services;
import java.io.InputStream;

import com.jcraft.jsch.*;

//  Rol: Gestor de conexión SSH

// Usa la librería JSCH para conectarse por SSH a un servidor remoto.

// Ejecuta un comando (por ejemplo, deluser nombreUsuario) y devuelve la salida.

// Encapsula toda la lógica de conexión, autenticación y ejecución remota.

public class SSHConnectionManager {
    public static String executeCommand(String username, String host, String command) throws Exception {
        int port = 22;
        String privateKeyPath = "/ruta/a/tu/llave/id_rsa"; // o usa password

        JSch jsch = new JSch();
        jsch.addIdentity(privateKeyPath);

        Session session = jsch.getSession(username, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();

        StringBuilder output = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                output.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) break;
            Thread.sleep(100);
        }

        channel.disconnect();
        session.disconnect();

        return output.toString();
    }
}
