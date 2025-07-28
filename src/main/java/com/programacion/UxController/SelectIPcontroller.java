package com.programacion.UxController;

import com.programacion.Utility.LocationDB;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SelectIPcontroller {
    @FXML
    private Label namePais;
    @FXML
    private ImageView imagePais;
    private LocationDB location;
    private String selectedIP;

    public String getSelectedIP() {
        return selectedIP;
    }

    public void setData(LocationDB item) {
        this.location = item;
        String ip = item.getIp();
        String pathImage = LocationDB.getImageByIP(ip);
        Image image = new Image(getClass().getResourceAsStream(pathImage));
        imagePais.setImage(image);
        namePais.setText(item.getNamePais());
    }

    @FXML
    void seleccionarIP(MouseEvent event) {
        if (mainController != null && location != null) {
            mainController.setIpSeleccionada(location.getIp());
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private SelectorController mainController;

    public void setMainController(SelectorController mainController) {
        this.mainController = mainController;
    }

}
