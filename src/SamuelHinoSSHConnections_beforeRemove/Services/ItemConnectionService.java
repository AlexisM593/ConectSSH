package SamuelHinoSSHConnections_beforeRemove.Services;

import java.util.HashMap;
import java.util.Map;

// Rol: Servicio de conexión de ítem
// Este servicio podría manejar la lógica de conexión de ítems, como establecer relaciones entre ítems
// o gestionar la información de conexión entre diferentes ítems en la aplicación.
public class ItemConnectionService {
    // Simula la obtención de una conexión de ítem
    public static Map<String, String> getItemConnection(String serversGeneratedId) {
        // Aquí simulas lo que devuelve ItemConnection
        Map<String, String> connection = new HashMap<>();
        connection.put("ip", "192.168.1.10,192.168.1.20");
        return connection;
    }
}
