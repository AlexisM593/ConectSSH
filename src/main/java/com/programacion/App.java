package com.programacion;

import java.io.IOException;

import com.programacion.Utility.LocationDB;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("FXL mvc scene");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(LocationDB.getAllLocations());
        System.out.println(LocationDB.getImageByIP("186.68.104.139"));

        launch(args);
    }

}