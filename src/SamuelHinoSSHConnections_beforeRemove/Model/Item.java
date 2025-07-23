package SamuelHinoSSHConnections_beforeRemove.Model;

// Representa el ítem que estás procesando (equivalente a context.currentItem en JavaScript).
// Contiene información como tipo de usuario, HWI DS, título y servidores generados.
// Este modelo es utilizado para manejar los datos de los ítems en la aplicación.

// Rol: Modelo de datos
public class Item {
    private String typeUser;
    private String hwiDs;
    private String title;
    private String serversGenerated;

    // Constructor
    public Item(String title, String typeUser, String hwiDs, String serversGenerated) {
        this.title = title;
        this.typeUser = typeUser;
        this.hwiDs = hwiDs;
        this.serversGenerated = serversGenerated;
    }

    // Getters
    public String getTypeUser() { return typeUser; }
    public String getHwiDs() { return hwiDs; }
    public String getTitle() { return title; }
    public String getServersGenerated() { return serversGenerated; }
}
