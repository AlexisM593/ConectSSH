package Utility;

import java.util.Map;
import java.util.List;

public class LocationImageManager {

    private Map<String, TypeConnection> typeConnects;
    private Map<String, String> ipVecStorage;

    public LocationImageManager(Map<String, TypeConnection> typeConnects, Map<String, String> ipVecStorage) {
        this.typeConnects = typeConnects;
        this.ipVecStorage = ipVecStorage;
    }

    public void selectLocationImg(String typeConn, List<String> arrayPais) {
        int numty = typeConn.equals("XRay") ? 0 : 1;
        TypeConnection conn = typeConnects.get(String.valueOf(numty));
        String vecL = conn.getVecL();
        String inpName = conn.getInpName();
        String ipVec = conn.getIpVec();

        for (int i = 1; i <= 3; i++) {
            String selector = vecL + i;
            String storageKey = ipVec + i;

            try {
                // Aquí debes usar algún tipo de UI manager o listener
                System.out.println("Esperando evento de click para: " + selector);
                // Simula evento o integra con interfaz gráfica si usas JavaFX/Swing
                // ejemplo: cuando ocurra eventoClick(selector) => llama callWindows(...)
            } catch (Exception e) {
                System.out.println("Elemento " + selector + " no disponible aún");
            }
        }
    }

    public void updateImage(Item item, String vectorIM, String ip, List<String> arrayPais, String searchId, DatabaseService dbService) {
        try {
            if (ip != null && !ip.isEmpty()) {
                List<Item> result = dbService.queryServersGeneratedByOwner(ip); // Ajusta si el modelo cambia

                if (!result.isEmpty()) {
                    Item found = result.get(0); // Supón que item tiene imagenPais

                    String imagenPaisId = found.getType(); // o getImagenPais() si lo agregas
                    if (imagenPaisId != null) {
                        // Aquí simula la obtención de la imagen de "ImagenesPais"
                        String imageURL = "http://ejemplo.com/img/" + imagenPaisId + ".png";
                        System.out.println("Imagen actualizada para " + vectorIM + ": " + imageURL);
                        // Si tienes UI, ahí pones: item.setImagen(imageURL);
                    } else {
                        System.out.println("No se encontró imagenPais.");
                    }
                } else {
                    System.out.println("No se encontraron datos para la IP.");
                }
            } else {
                System.out.println("IP no proporcionada.");
            }

            // Llama a la función adicional si deseas: ArrayLocation(arrayPais, searchId, vectorIM, ip);

        } catch (Exception e) {
            System.out.println("Error al actualizar imagen: " + e.getMessage());
        }
    }

    // Clase auxiliar para representar la estructura de conexión
    public static class TypeConnection {
        private String vecL;
        private String inpName;
        private String ipVec;

        public TypeConnection(String vecL, String inpName, String ipVec) {
            this.vecL = vecL;
            this.inpName = inpName;
            this.ipVec = ipVec;
        }

        public String getVecL() {
            return vecL;
        }

        public String getInpName() {
            return inpName;
        }

        public String getIpVec() {
            return ipVec;
        }
    }
}
