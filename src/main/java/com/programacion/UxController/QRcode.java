package com.programacion.UxController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.programacion.Utility.LocationDB;
import com.programacion.UxController.UtilityUX.Tool;
import com.programacion.UxController.UtilityUX.Tool.QR_code;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class QRcode {

    public static List<QR_code> qrGenerados = new ArrayList<>();

   public static void generarQR(String ip, String texto, String ubicacion, List<LocationDB> listaUbicaciones) {
    int ancho = 300;
    int alto = 300;

    String nombreArchivo = ip.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
    String rutaArchivo = "src\\main\\resources\\serverQR" + nombreArchivo;
    File archivo = new File(rutaArchivo);

    // Verificar duplicado en memoria
    for (QR_code qr : qrGenerados) {
        if (qr.path.equals(rutaArchivo)) {
            System.out.println("QR ya registrado en memoria.");
            return;
        }
    }

    if (archivo.exists()) {
        System.out.println("QR ya existente en disco: " + rutaArchivo);

        String ipDesdeNombre = Tool.extraerIPDesdeNombreArchivo(nombreArchivo);
        LocationDB ubicacionData = Tool.obtenerUbicacionPorIP(ipDesdeNombre, listaUbicaciones);

        if (ubicacionData != null) {
            qrGenerados.add(new QR_code(ubicacionData.getNamePais(), rutaArchivo));
            System.out.println("QR existente cargado a memoria: " + rutaArchivo);
        } else {
            System.out.println("No se encontró ubicación para IP: " + ipDesdeNombre);
        }

        return;
    }

    try {
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

        Path path = Paths.get(rutaArchivo);
        Files.createDirectories(path.getParent());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        qrGenerados.add(new QR_code(ubicacion, rutaArchivo));
        System.out.println("Código QR generado en: " + rutaArchivo);

    } catch (WriterException | IOException e) {
        e.printStackTrace();
    }
}


    @FXML
    private VBox vboxRepeater;

    @FXML
    private ImageView btCloseSelect;


    @FXML
    public void initialize() {
        try {
                 List<LocationDB> listView = LocationDB.getAllLocations();
        for (LocationDB loc : listView) {
            String ip = loc.getIp();
            String urlServer = loc.getUrlServer();
            String namePais = loc.getNamePais();

            generarQR(ip, urlServer, namePais, listView);
        }

        cargarQRs(qrGenerados);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @FXML
    void cerrarVentana(MouseEvent event) {
        Stage stage = (Stage) btCloseSelect.getScene().getWindow();
        stage.close();
    }

    public void cargarQRs(List<Tool.QR_code> listViewQR) throws IOException {
        vboxRepeater.getChildren().clear();

        for (Tool.QR_code u : listViewQR) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/serverItemQR.fxml"));
            Parent item = loader.load();
            serverItemQR controller = loader.getController();


            controller.setData(u);
            controller.setMainController(this);

            vboxRepeater.getChildren().add(item);
        }
    }

   

}
