package com.programacion.UxController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShowAlertController {
    @FXML
    private Label messageAlert;
   

    public void setMessageAlert(String messageAlert) {
        this.messageAlert.setText(messageAlert);
    }

    

}
