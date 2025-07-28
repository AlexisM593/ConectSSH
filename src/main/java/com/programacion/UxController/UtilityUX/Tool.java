package com.programacion.UxController.UtilityUX;

import java.util.Date;
import java.util.List;

import com.programacion.Utility.LocationDB;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class Tool {

    @FXML
    private static Pane boxImageView;

    @FXML
    private static StackPane boxHwid;

    public static int findIndexById(List<VectorItem> vectorip, String id) {
        for (int i = 0; i < vectorip.size(); i++) {
            if (vectorip.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static Node boxViewlookupNodeById(String id) {
        return boxImageView.lookup("#" + id);
    }

    public static Node stackPanelookupNodeByIdI(String id) {
        return boxHwid.lookup("#" + id);
    }

    public static class Result {
        public String ip;
        public String hwid;
        public String result;
        public String error;

        @Override
        public String toString() {
            return "___________\nUbicación: " + ip +
                    "\nHWID: " + hwid +
                    (result != null ? "\nResultado: " + result : "\nError: " + error) + "\n";
        }
    }

    public static class QR_code {
        public String ubicacion;
        public String path;

        public QR_code(String ubicacion, String path) {
            this.path = path;
            this.ubicacion = ubicacion;
        }
    }

    public static int calculateRemainingDays(String fechaVencimientoStr) {
        Date hoy = new Date();
        Date fechaVencimiento = java.sql.Date.valueOf(fechaVencimientoStr);
        long tiempoRestante = fechaVencimiento.getTime() - hoy.getTime();
        int diasRestantes = (int) Math.ceil((double) tiempoRestante / (1000 * 60 * 60 * 24));
        return Math.max(diasRestantes, 1);
    }


    public static String convertirRutaRelativa(String rutaOriginal) {
    // Normaliza separadores de Windows a Unix
    String rutaNormalizada = rutaOriginal.replace("\\", "/");

    // Busca el índice de "/serverQR"
    int indice = rutaNormalizada.indexOf("/serverQR");

    // Si lo encuentra, recorta desde ahí
    if (indice != -1) {
        return rutaNormalizada.substring(indice); // Devuelve desde "/serverQR" en adelante
    }

    // Si no se encuentra, se devuelve la original como fallback
    return rutaNormalizada;
}

public static String extraerIPDesdeNombreArchivo(String nombreArchivo) {
    // Ejemplo: serverQR45_170_248_137.png -> 45.170.248.137
    String sinExtension = nombreArchivo.replace(".png", "");
    String sinPrefijo = sinExtension.replace("serverQR", "");
    return sinPrefijo.replaceAll("_", ".");
}

public static LocationDB obtenerUbicacionPorIP(String ip, List<LocationDB> lista) {
    for (LocationDB loc : lista) {
        if (loc.getIp().equals(ip)) {
            return loc;
        }
    }
    return null;
}


}
