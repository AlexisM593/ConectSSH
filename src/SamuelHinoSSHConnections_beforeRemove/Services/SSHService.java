package SamuelHinoSSHConnections_beforeRemove.Services;

import java.util.Arrays;
import java.util.List;
// Rol: Servicio de lógica SSH
// Usa SSHConnectionManager para ejecutar comandos en servidores remotos.

public class SSHService {
    public static void processIPs(String ipData, List<String> users) throws Exception {
        if (ipData == null || ipData.isEmpty()) {
            throw new IllegalArgumentException("IP data no puede estar vacío");
        }

        List<String> ipArray = Arrays.asList(ipData.split(","));

        System.out.println("Procesando IPs: " + ipArray + " para usuarios: " + users);

        for (String ip : ipArray) {
            ip = ip.trim();
            if (ip.isEmpty()) continue;

            for (String user : users) {
                String command = "sudo deluser " + user;
                try {
                    String result = SSHConnectionManager.executeCommand("tu_usuario", ip, command);
                    System.out.println("Resultado en " + ip + ": " + result);

                    if (result.contains("Error")) {
                        throw new Exception("Error en IP " + ip + ": " + result);
                    }
                } catch (Exception e) {
                    System.err.println("Error procesando IP " + ip + ": " + e.getMessage());
                    throw e;
                }
            }
        }
    }
}

// Código a ejecutar en main(App.java) o un controlador de eventos

// import java.util.Collections;

// public class MainApp {
//     public static void main(String[] args) {
//         // Simula datos de entrada
//         String ipData = "192.168.1.10,192.168.1.20";
//         String currentTitle = "usuarioA";

//         try {
//             SSHService.processIPs(ipData, Collections.singletonList(currentTitle));
//         } catch (Exception e) {
//             System.err.println("Fallo al ejecutar SSH: " + e.getMessage());
//         }
//     }
// }
