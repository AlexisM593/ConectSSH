package com.programacion.UxController;

import java.io.IOException;
import java.util.List;

import com.programacion.Utility.LocationDB;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SelectorController {

    @FXML
    private VBox vboxRepeater;

    @FXML
    private ImageView btCloseSelect;

    private String ipSeleccionada;
    private List<LocationDB> listViewIPs;
    private String initialIP;
    public String getIpSeleccionada() {
        return ipSeleccionada;
    }

    @FXML
    public void initialize() {
        try {
            listViewIPs = LocationDB.getAllLocations();
            cargarIPs(listViewIPs);
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



    public void cargarIPs(List<LocationDB> listViewIPs) throws IOException {
        vboxRepeater.getChildren().clear();

        for (LocationDB u : listViewIPs) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/selectLocationItem.fxml"));
            Parent item = loader.load();
            SelectIPcontroller controller = loader.getController();


            controller.setData(u);
            controller.setMainController(this); 

            vboxRepeater.getChildren().add(item);
        }
    }

    public void setIpSeleccionada(String ip) {
        this.ipSeleccionada = ip;
        System.out.println("IP seleccionada: " + ip);
    }


    public void setInitialIP(String ip) {
      this.initialIP = ip;
    }

}
