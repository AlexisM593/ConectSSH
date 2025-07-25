package com.programacion.UxController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.programacion.Protocol.SecureShell_DB;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashBoard {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logoutBtn;

    @FXML
    private VBox vboxRepetidor;

    public void handleLogout() throws IOException {
        System.out.println("Logout presionado");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setTitle("login");
        Scene nuevaScene = new Scene(root);

        stage.setScene(nuevaScene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

    }

    public void initialize() {
        try {
            cargarUsuarios();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, Node> itemMap = new HashMap<>();

    public void cargarUsuarios() throws IOException {
        List<SecureShell_DB> items = SecureShell_DB.getData();
        vboxRepetidor.getChildren().clear(); // Limpia todo antes

        for (SecureShell_DB u : items) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/item.fxml"));
            Parent item = loader.load();
            ItemController controller = loader.getController();

            Map<String, Node> itemMap = new HashMap<>();
            mapAllNodes(item, itemMap);
            controller.setData(u, itemMap);
            vboxRepetidor.getChildren().add(item);
        }

    }

    public void mapAllNodes(Node root, Map<String, Node> itemMap) {
        if (root.getId() != null) {
            itemMap.put(root.getId(), root);
        }
        if (root instanceof Parent) {
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                mapAllNodes(child, itemMap); // llamada recursiva
            }
        }
    }

    public void setWelcomeText(String cedula) {
        welcomeLabel.setText(cedula);
    }

}

