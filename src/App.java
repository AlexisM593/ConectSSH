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
}
