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
    private static String currentOwner;

    public void handleLogout() throws IOException {
        System.out.println("Logout presionado");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
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

    @FXML
    public void agregarNuevoItemEnBlanco() throws IOException {
        // Verificar si ya hay un item en blanco
        for (Node node : vboxRepetidor.getChildren()) {
            ItemController controllerExistente = (ItemController) node.getProperties().get("controller");
            if (controllerExistente != null && controllerExistente.getUsuarioItem() == null) {
                // Ya existe un item sin usuario asignado
                System.out.println("Ya existe un item en blanco. No se puede agregar otro.");
                return;
            }
        }

        // Cargar nuevo item.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item.fxml"));
        Parent item = loader.load();
        ItemController controller = loader.getController();

        Map<String, Node> itemMap = new HashMap<>();
        mapAllNodes(item, itemMap);

        SecureShell_DB nuevo = new SecureShell_DB(currentOwner);
        controller.setData(nuevo, itemMap);

        // Guardar el controlador en las propiedades del nodo
        item.getProperties().put("controller", controller);

        // Agregar el nuevo item en blanco
        vboxRepetidor.getChildren().add(0, item);
    }

    public void cargarUsuarios() throws IOException {
        List<SecureShell_DB> items = SecureShell_DB.getData();
        vboxRepetidor.getChildren().clear(); // Limpia todo antes

        for (SecureShell_DB u : items) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item.fxml"));
            Parent item = loader.load();
            ItemController controller = loader.getController();
            controller.setMainController(this);
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
        this.currentOwner = cedula;
    }

    public static String getCurrentOwner() {
        return currentOwner;
    }

    public void eliminarVisualmente(Node item) {
        vboxRepetidor.getChildren().remove(item); // lo elimina del VBox
    }

}
