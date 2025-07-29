import Protocol.SecureShell;

public class App {
    public static void main(String[] args) {
        // Crear una instancia de SecureShell
        SecureShell ssh = new SecureShell();

        // Parámetros de prueba (reemplaza por valores reales si vas a probarlo de verdad)
        String usuarioAEliminar = "usuario123";
        String ipServidor = "192.168.1.100"; // Cambia por la IP real del servidor
        String claveRoot = "tu_clave_root";  // Cambia por la clave SSH real

        // Llamar al método para eliminar el usuario
        boolean eliminado = ssh.eliminarUsuario(usuarioAEliminar, ipServidor, claveRoot);

        // Mostrar resultado
        if (eliminado) {
            System.out.println("✅ Usuario eliminado correctamente por SSH.");
        } else {
            System.out.println("❌ No se pudo eliminar el usuario.");
        }
    }

    
    // Samuel Hinojosa
    // List<String> ips = List.of("192.168.1.100", "192.168.1.101");
    //     List<User> users = List.of(
    //         new User("samuel", "1234"),
    //         new User("sofia", "abcd")
    //     );

    //     int vencimiento = 2; // días
    //     int maxLogins = 3;

    //     List<String> resultados = SSHTaskProcessor.processTasks(ips, users, vencimiento, maxLogins);

    //     for (String resultado : resultados) {
    //         System.out.println(resultado);
    //     }
    // }
}


/* 
ESto corresponde a la clase GestorDataBase.java que se encuentra en el paquete Utility.
import Utility.SSHManager;
import Utility.SSHManager.Usuario;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        SSHManager sshManager = new SSHManager();

        // Ejemplo crear usuarios
        try {
            var resultados = sshManager.crearUsuarios(
                Arrays.asList("192.168.1.100", "192.168.1.101"),
                Arrays.asList(new Usuario("usuario1", "pass1"), new Usuario("usuario2", "pass2")),
                7, // vencimiento 7 días
                3  // max sesiones
            );

            resultados.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ejemplo eliminar usuario
        boolean eliminado = sshManager.eliminarUsuario("usuario1", "192.168.1.100", "tu_clave_ssh");
        System.out.println("Eliminado: " + eliminado);
    }
}
*/