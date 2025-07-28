package com.programacion.UxController;

import com.programacion.UxController.UtilityUX.Tool;
import com.programacion.UxController.UtilityUX.Tool.QR_code;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class serverItemQR {
    @FXML
    private Label nameLocation;
    @FXML
    private ImageView imageServer;

    public void setData(QR_code item) {
        String newPath = Tool.convertirRutaRelativa(item.path);
        System.out.println("Imprimiendo new Path " + newPath);
        Image image = new Image("file:" + item.path.replace("\\", "/"));
        imageServer.setImage(image);
        nameLocation.setText(item.ubicacion);
    }

    private QRcode mainController;

    public void setMainController(QRcode qRcode) {
        this.mainController = qRcode;
    }

}
