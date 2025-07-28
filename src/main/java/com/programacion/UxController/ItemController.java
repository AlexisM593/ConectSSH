package com.programacion.UxController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.programacion.Protocol.SecureShell_DB;
import com.programacion.Utility.HooksDataBase;
import com.programacion.Utility.LocationDB;
import com.programacion.UxController.UtilityUX.LocationOption;
import com.programacion.UxController.UtilityUX.Tool;
import com.programacion.UxController.UtilityUX.VectorItem;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

public class ItemController {

    @FXML
    private Label lbDiasRestantes, textSSHubi1, textSSHubi2, textSSHubi3;
    @FXML
    private Button btSSHsaveItem, btSSHmodify, btHwidAdd, btHwidConfDelete, btSSHconfDelete,
            btHwidShowDelete, btSSHshowDelete;
    @FXML
    private DatePicker dateSSH;

    @FXML
    private TextField inpSSHwid, inpNickName;
    @FXML
    private StackPane boxHwid;
    @FXML
    private FlowPane tagsHWID, NodoRaiz;
    @FXML
    private CheckBox switchStatus;
    @FXML
    private Separator lineTag1, lineTag2;
    private String vector = "";
    private final int MAX_HWID = 5;

    private SecureShell_DB item;

    private DashBoard mainController;

    public Node getRootNode() {
        return NodoRaiz;
    }

    public void setMainController(DashBoard controller) {
        this.mainController = controller;
    }

    List<List<Object>> arrayPaisSSH = new ArrayList<>();
    Map<String, List<VectorItem>> ipVecStorage = new HashMap<>();
    List<LocationDB> allLocations = LocationDB.getAllLocations();

    List<LocationOption> locationServer = allLocations.stream()
            .map(loc -> new LocationOption(loc.getIp(), loc.getIdPais(), loc.getNamePais()))
            .collect(Collectors.toList());

    public void initVector() {

        ipVecStorage.put("ipVecSH1", new ArrayList<>());
        ipVecStorage.put("ipVecSH2", new ArrayList<>());
        ipVecStorage.put("ipVecSH3", new ArrayList<>());

    }

    public String getUsuarioItem() {
        return item.getUsuarioItem();
    }

    @FXML
    public void initialize() {
        initVector();
        inpSSHwid.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean existe = verificarExistenciaEnTags(newValue);
            btHwidAdd.setDisable(existe || newValue.trim().isEmpty());
        });

        dateSSH.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                LocalDate fecha = newValue;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String fechaStr = fecha.format(formatter);

                restantMod(fechaStr);
            }
        });

        btSSHsaveItem.setVisible(false);
        btHwidConfDelete.setVisible(false);
        btSSHconfDelete.setVisible(false);
        tagsShow(false);

        inpSSHwid.setDisable(true);
        boxHwid.setVisible(false);
        boxHwid.setManaged(false);
        inpNickName.setDisable(true);
        dateSSH.setDisable(true);

    }

    private boolean verificarExistenciaEnTags(String texto) {
        for (Node node : tagsHWID.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equalsIgnoreCase(texto)) {
                    return true;
                }
            }
        }
        return false;
    }

    @FXML
    public void handlRemoveItem(ActionEvent event) {
        try {
            HooksDataBase.beforeRemoveSSH(item);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SecureShell_DB.eliminarItemPorUsuario(item.getUsuarioItem());

        Pair<ShowAlertController, Stage> alerta = showAlert("Eliminando Item...");
        Stage stage = alerta.getValue();
        Button boton = (Button) event.getSource();

        Node itemNode = getRootNode();
        mainController.eliminarVisualmente(itemNode);
        ocultarMensajeConDelay(5, stage);

    }

    public void handleSaveItem(Map<String, Node> itemMap) {

        btSSHsaveItem.setOnMouseClicked(event -> {

            Pair<ShowAlertController, Stage> alerta = showAlert("Guardando Item...");
            ShowAlertController controllerAlert = alerta.getKey();
            Stage stage = alerta.getValue();

            Platform.runLater(() -> {

                // Obtener el número de HWIDs seleccionados
                @SuppressWarnings("unchecked")

                int cantidad = tagsHWID.getChildren().size();
                String itemID = ((TextField) itemMap.get("inpNickName")).getText();
                // Obtener las IPs desde arrayPaisSSH
                List<String> objip = new ArrayList<>();

                arrayIndexLocation(arrayPaisSSH, itemID, objip);
                System.out.println("IMprimiendo onjIPS " + objip);

                // Filtrar IPs diferentes a 0.0.0.0
                List<String> filteredIPs = objip.stream()
                        .filter(ip -> !ip.equals("0.0.0.0"))
                        .collect(Collectors.toList());

                // Eliminar duplicados
                List<String> uniqueIPs = filteredIPs.stream()
                        .distinct()
                        .collect(Collectors.toList());

                String ips = String.join(",", objip);

                List<String> listaTags = getTagsFromFlowPane();
                System.out.println("IMprimiendo tags " + listaTags);

                aplicarCambiosItem(ips, listaTags);

                boolean estadoVery = verifyLimit(uniqueIPs, filteredIPs, objip);
                if (estadoVery && cantidad > 0) {

                    Runnable guardarOperacion = () -> {
                        try {
                            if (SecureShell_DB.existeUsuario(itemID)) {
                                System.out.println("Guardando el elemento...");
                                SecureShell_DB previousItem = SecureShell_DB
                                        .getItemByUsuarioItem(item.getUsuarioItem());
                                if (item != null) {
                                    HooksDataBase.beforeUpdateSSH(item, previousItem);
                                    SecureShell_DB.actualizarItem(item);
                                } else {
                                    System.out.println("No se encontró el item.");
                                }

                            } else {
                                int cantidadItemsGenerados = SecureShell_DB
                                        .contarItemsPorOwner(DashBoard.getCurrentOwner());

                                if (cantidadItemsGenerados < 8) {
                                    SecureShell_DB itemInsertDB = HooksDataBase.beforeInsertSSH(item);
                                    SecureShell_DB.insertarItem(itemInsertDB);
                                }
                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e); // Esto permite capturarlo en onError
                        }
                    };

                    // Acción al guardar con éxito
                    Consumer<String> onSuccess = mensaje -> {
                        controllerAlert.setMessageAlert("Recurso guardado con éxito: " + mensaje);
                        afterSaveItem();
                    };

                    // Acción si hay error
                    Consumer<Exception> onError = exception -> {
                        controllerAlert.setMessageAlert("Error al guardar el recurso: " + exception.getMessage());
                        exception.printStackTrace();
                    };

                    guardarElementoConReintento(
                            1,
                            guardarOperacion,
                            onSuccess,
                            onError,
                            controllerAlert,
                            stage);
                } else {
                    controllerAlert
                            .setMessageAlert("Valores inválidos detectados. Verifique los datos e intente nuevamente.");
                }

            });
        });

    }

    private void aplicarCambiosItem(String ips, List<String> listaTags) {
        LocalDate fechaSeleccionada = dateSSH.getValue();
        if (fechaSeleccionada != null) {
            String vigencia = fechaSeleccionada.toString();
            item.setVigencia(vigencia);
        } else {
            System.out.println("No se ha seleccionado ninguna fecha");
        }
        item.sethwiDs(listaTags);
        item.setIp(ips);
        item.setEstado(switchStatus.isSelected());
        item.setUsuarioItem(inpNickName.getText());
    }

    public List<String> getTagsFromFlowPane() {
        List<String> tags = new ArrayList<>();

        for (Node node : tagsHWID.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                tags.add(label.getText());
            }
        }

        return tags;
    }

    public boolean verifyLimit(List<String> uniqueIPs, List<String> filteredIPs, List<String> objip) {
        try {

            boolean isValid = (uniqueIPs.size() == filteredIPs.size()) &&
                    objip.stream().anyMatch(ip -> !"0.0.0.0".equals(ip));

            if (isValid) {

                return true;
            } else {

                return false;
            }

        } catch (Exception e) {
            System.err.println("Error en verifyLimit: " + e.getMessage());

            return false;
        }
    }

    public static int arrayIndexLocation(List<List<Object>> arrayPais, String id, List<String> objip) {
        for (int i = 0; i < arrayPais.size(); i++) {
            List<Object> itemArray = arrayPais.get(i);

            if (!itemArray.isEmpty()) {
                Object key = itemArray.get(0);
                if (key == null) {
                    itemArray.set(0, id);
                    key = id;
                }
                if (id.equals(key)) {
                    for (int j = 1; j < itemArray.size(); j++) {
                        Object obj = itemArray.get(j);
                        if (obj instanceof Map) {
                            Map<?, ?> map = (Map<?, ?>) obj;
                            Object ipValue = map.get("ip");
                            if (ipValue instanceof String && ipValue != null) {
                                objip.add((String) ipValue);
                            }
                        }
                    }
                    return i;
                }
            }
        }

        System.out.println("ID no encontrado en arrayPais.");
        return -1; // No se encontró el ID
    }

    public void guardarElementoConReintento(
            int reintento,
            Runnable guardarOperacion,
            Consumer<String> onSuccess,
            Consumer<Exception> onError,
            ShowAlertController controllerAlert,
            Stage stageToClose) {
        new Thread(() -> {
            try {
                guardarOperacion.run();

                Platform.runLater(() -> {
                    controllerAlert.setMessageAlert("Item guardado con éxito.");
                    ocultarMensajeConDelay(5, stageToClose);
                    if (onSuccess != null) {
                        onSuccess.accept("Guardado exitoso");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

                if (reintento > 0) {
                    Platform.runLater(() -> {
                        controllerAlert.setMessageAlert("Reintentando guardar Item...");
                        guardarElementoConReintento(
                                reintento - 1,
                                guardarOperacion,
                                onSuccess,
                                onError,
                                controllerAlert,
                                stageToClose);
                    });
                } else {
                    Platform.runLater(() -> {
                        String mensajeError;
                        if (e.getMessage().contains("stack")) {
                            mensajeError = "Tiempo de espera agotado, vuelve a intentarlo más tarde";
                        } else if (e.getMessage().toLowerCase().contains("field")) {
                            mensajeError = "Valores inválidos. Verifica la información e inténtalo de nuevo";
                        } else {
                            mensajeError = "Error al guardar";
                        }

                        controllerAlert.setMessageAlert("Error al guardar: " + mensajeError);
                        ocultarMensajeConDelay(5, stageToClose);

                        if (onError != null) {
                            onError.accept(e);
                        }
                    });
                }
            }
        }).start();
    }

    private void ocultarMensajeConDelay(int segundos, Stage stageToClose) {
        PauseTransition delay = new PauseTransition(Duration.seconds(segundos));
        delay.setOnFinished(e -> stageToClose.close());
        delay.play();
    }

    public Pair<ShowAlertController, Stage> showAlert(String messageAlert) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/showAlert.fxml"));
            Parent root = loader.load();

            ShowAlertController controller = loader.getController();
            controller.setMessageAlert(messageAlert);

            Stage stageAlert = new Stage(); // 🔄 Stage LOCAL

            Scene scene = new Scene(root);
            stageAlert.setScene(scene);
            stageAlert.initModality(Modality.NONE); // ⚠️ NO bloquea eventos de otras ventanas
            stageAlert.initOwner(btHwidAdd.getScene().getWindow());
            stageAlert.initStyle(StageStyle.UNDECORATED);
            stageAlert.setAlwaysOnTop(true);

            // Mostrar primero en el hilo de JavaFX
            Platform.runLater(() -> {
                stageAlert.show(); // ⬅️ Mostrar antes de obtener dimensiones

                // Posicionar luego de mostrarse para tener ancho/alto real
                Rectangle2D bounds = Screen.getPrimary().getBounds();
                double x = bounds.getMinX() + 5; // Izquierda
                double y = bounds.getMaxY() - stageAlert.getHeight(); // Abajo

                stageAlert.setX(x);
                stageAlert.setY(y);

                // Cerrar automáticamente después de 5 segundos
                PauseTransition delay = new PauseTransition(Duration.seconds(5));
                delay.setOnFinished(e -> stageAlert.close());
                delay.play();
            });

            return new Pair<>(controller, stageAlert);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void showBtConfDelete() {
        if (btSSHconfDelete.isVisible()) {
            // Ocultar botón de confirmación y mostrar el campo de nombre
            btSSHconfDelete.setVisible(false);
            inpNickName.setVisible(true);
            btSSHshowDelete.setText("Eliminar");
        } else {
            // Mostrar botón de confirmación y ocultar el campo de nombre
            btSSHconfDelete.setVisible(true);
            inpNickName.setVisible(false);
            btSSHshowDelete.setText("Cancelar");

        }
    }

    @FXML
    private void onAddHwidClick() {
        String hwidnew = inpSSHwid.getText().trim();

        if (!hwidnew.isEmpty() && !verificarExistenciaEnTags(hwidnew) && tagsHWID.getChildren().size() < MAX_HWID) {

            Label tag = new Label(hwidnew);
            tag.setStyle(
                    "-fx-background-color: #dcdcdc; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            tag.setCursor(Cursor.HAND);

            tag.setOnMouseClicked(e -> {
                inpSSHwid.setText(hwidnew);
                highlightTag(tagsHWID, tag);
            });

            tagsHWID.getChildren().add(tag);

            inpSSHwid.clear();
            btHwidAdd.setDisable(true);
            btHwidShowDelete.setDisable(true);

            if (!tagsHWID.isVisible() && !tagsHWID.getChildren().isEmpty()) {
                tagsShow(true);
            }
        }
    }

    public void selectLocationImg(Map<String, Node> itemMap) {
        String imgSelect = "vectSSHseLocation";
        String ipVec = "ipVecSH";

        for (int i = 1; i <= 3; i++) {
            String selectorId = imgSelect + i;
            String storageKey = ipVec + i;

            String idItem = ((TextField) itemMap.get("inpNickName")).getText();

            Node selectorNode = itemMap.get(selectorId); // buscar directamente en el mapa
            if (selectorNode != null) {
                selectorNode.setOnMouseClicked(event -> {
                    TextField input = (TextField) itemMap.get("inpSSHwid"); // también desde el mapa

                    if (input != null && !input.isDisabled()) {
                        vector = selectorId;
                        List<VectorItem> vectorItems = ipVecStorage.get(storageKey);

                        callWindows(itemMap, idItem, vectorItems); // ahora mandas el mapa
                    }
                });
            } else {
                System.out.println("Elemento " + selectorId + " no está disponible todavía.");
            }
        }
    }

    private void callWindows(Map<String, Node> item, String itemID, List<VectorItem> VectorIp) {
        String ip = VectorIp.stream()
                .filter(v -> v.getId().equals(itemID))
                .map(VectorItem::getValue)
                .findFirst()
                .orElse(null);

        System.out.println("IMPTIMIENDO IP de vectpr IP  " + ip);
        for (VectorItem itemd : VectorIp) {
            System.out.println(" - " + itemd.getId() + ": " + itemd.getValue());
        }
        String ipSeleccionada = abrirVentanaSeleccionIP(ip); // retornar desde la ventana

        if (ipSeleccionada != null) {
            if (ip != null && ip.equals(ipSeleccionada)) {
                int index = Tool.findIndexById(VectorIp, itemID);
                ipSeleccionada = "0.0.0.0";
                VectorIp.get(index).setValue("0.0.0.0");
            }

            imagenVI(item, ipSeleccionada, vector, itemID);
        }

    }

    @FXML
    public void btViewServer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/serverViewQR.fxml"));
            Parent root = loader.load();

            QRcode controller = loader.getController();
            // controller.setInitialIP(initialIP); // Descomenta si es necesario

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btHwidAdd.getScene().getWindow());
            stage.initStyle(StageStyle.UNDECORATED);

            stage.sizeToScene(); // Se adapta automáticamente al FXML

            // Posicionar la ventana a la derecha arriba
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            double anchoVentana = stage.getWidth();

            stage.setX(bounds.getMaxX() - anchoVentana - 20);
            stage.setY(bounds.getMinY() + 150);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String abrirVentanaSeleccionIP(String initialIP) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SelectLocation.fxml"));
            Parent root = loader.load();

            SelectorController controller = loader.getController();
            controller.setInitialIP(initialIP);
            // Crear ventana secundaria
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // ✅ MODAL: Bloquea la ventana principal hasta que esta se cierre
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btHwidAdd.getScene().getWindow());
            stage.initStyle(StageStyle.UNDECORATED);

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            double anchoVentana = 300;
            double altoVentana = 300;

            stage.setWidth(anchoVentana);
            stage.setHeight(altoVentana);
            stage.setX(bounds.getMaxX() - anchoVentana - 20); // Derecha
            stage.setY(bounds.getMinY() + 150);

            stage.showAndWait();

            return controller.getIpSeleccionada();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void handleModify() {
        btHwidShowDelete.setVisible(false);
        if (!btSSHsaveItem.isVisible()) {
            // Modo edición activado
            btSSHmodify.setText("Cerrar");

            boxHwid.setManaged(true);
            boxHwid.setVisible(true);
            btSSHconfDelete.setVisible(false);
            dateSSH.setDisable(false);
            switchStatus.setDisable(false);

            tagsShow(true);

            btSSHsaveItem.setVisible(true);
            btSSHshowDelete.setVisible(false);

            inpSSHwid.setDisable(false);
            inpSSHwid.setText("");

            btHwidAdd.setDisable(true);

        } else {
            desactivarEdicionItem();
        }
    }

    public void afterSaveItem() {
        if (btSSHmodify.isDisabled()) {
            inpNickName.setDisable(true);
            btSSHmodify.setDisable(false);
        }
        desactivarEdicionItem();
    }

    public void tagsShow(boolean enable) {
        tagsHWID.setVisible(enable);
        tagsHWID.setManaged(enable);
        lineTag1.setVisible(enable);
        lineTag1.setManaged(enable);
        lineTag2.setVisible(enable);
        lineTag2.setManaged(enable);
    }

    public void desactivarEdicionItem() {
        btSSHshowDelete.setText("Eliminar");
        btSSHmodify.setText("Modificar");
        switchStatus.setDisable(true);
        boxHwid.setManaged(false);
        boxHwid.setVisible(false);
        btSSHsaveItem.setVisible(false);
        btSSHshowDelete.setVisible(true);
        dateSSH.setDisable(true);
        btSSHconfDelete.setVisible(false);
        tagsShow(false);

        inpSSHwid.setDisable(true);
    }

    public List<VectorItem> getCurrentVector(int index) {
        List<VectorItem> vector = ipVecStorage.get("ipVecSH" + index);
        return vector;
    }

    public void setData(SecureShell_DB item, Map<String, Node> itemMap) {
        this.item = item;

        if (item.getUsuarioItem() == null) {
            btSSHmodify.setDisable(true);
            inpNickName.setDisable(false);

            boxHwid.setManaged(true);
            boxHwid.setVisible(true);
            btSSHconfDelete.setVisible(false);
            dateSSH.setDisable(false);

            switchStatus.setSelected(true);
            switchStatus.setDisable(false);

            btSSHsaveItem.setVisible(true);
            btSSHshowDelete.setVisible(false);

            inpSSHwid.setDisable(false);
            inpSSHwid.setText("");

            btHwidAdd.setDisable(true);
            tagsHWID.getChildren().clear();
            item.setIp("186.68.104.139,0.0.0.0,0.0.0.0");

        }

        String[] ipArray = item.getIp().split(",");

        List<String> ipList = new ArrayList<>(Arrays.asList(ipArray));
        while (ipList.size() < 3) {
            ipList.add("0.0.0.0");
        }
        Map<String, String> vector1 = new HashMap<>();
        vector1.put("vector", "vectSSHseLocation1");
        vector1.put("ip", ipList.get(0));

        Map<String, String> vector2 = new HashMap<>();
        vector2.put("vector", "vectSSHseLocation2");
        vector2.put("ip", ipList.get(1));

        Map<String, String> vector3 = new HashMap<>();
        vector3.put("vector", "vectSSHseLocation3");
        vector3.put("ip", ipList.get(2));

        List<Object> objitem = new ArrayList<>();
        objitem.add(item.getUsuarioItem()); // key
        objitem.add(vector1);
        objitem.add(vector2);
        objitem.add(vector3);

        arrayPaisSSH.add(objitem);

        processItemSSH(item, itemMap);

    }

    public void processItemSSH(SecureShell_DB item, Map<String, Node> itemMap) {

        switchStatus.setSelected(item.isEstado());
        inpNickName.setText(item.getUsuarioItem());
        dateSSH.setPromptText(item.getVigencia());
        restantMod(item.getVigencia());
        startHwidTag(item.gethwiDs());

        String ipString = item.getIp();

        if (ipString != null && !ipString.isEmpty()) {
            String[] rawIps = ipString.split(",");
            List<String> ipArray = Arrays.stream(rawIps)
                    .map(String::trim)
                    .collect(Collectors.toList());

            String[] imageSelectors = {
                    "vectSSHseLocation1",
                    "vectSSHseLocation2",
                    "vectSSHseLocation3"
            };

            for (int i = 0; i < ipArray.size() && i < imageSelectors.length; i++) {

                imagenVI(
                        itemMap,
                        ipArray.get(i),
                        imageSelectors[i],
                        item.getUsuarioItem());

            }
        }

        inicializarText(ipString, locationServer);
        selectLocationImg(itemMap);
        handleSaveItem(itemMap);

    }

    public void imagenVI(Map<String, Node> itemMap,
            String ip,
            String vectorIM,
            String searchId) {

        int index = -1;
        for (int i = 0; i < locationServer.size(); i++) {
            if (locationServer.get(i).getValue().equals(ip)) {
                index = i;
                break;
            }
        }

        updateImage(itemMap, vectorIM, ip, searchId);

        String txt = "textSSHubi";
        String dpName = "dateSSH";
        String vecL = "vectSSHseLocation";
        if (index != -1) {
            for (int i = 1; i <= 3; i++) {
                if ((vecL + i).equals(vectorIM)) {

                    List<VectorItem> vector = getCurrentVector(i);
                    establishVector(vector, locationServer, searchId, index);
                    Label label = (Label) itemMap.get(txt + i);
                    label.setText(locationServer.get(index).getNamePais());
                }
            }
        } else {
            ImageView imageView = (ImageView) itemMap.get(vectorIM);
            imageView.setImage(new Image("/imagen/circle-button.png")); // Cambia al path correcto

            DatePicker dateP = (DatePicker) itemMap.get(dpName);
            if (!dateP.isDisabled()) {
                for (int i = 1; i <= 3; i++) {
                    if ((vecL + i).equals(vectorIM)) {
                        Label label = (Label) itemMap.get(txt + i);
                        label.setText("Añadir ➕");
                    }
                }
            }
        }

        System.out.println("La IP del servicio es: " + ip);
    }

    public void restantMod(String fechaTexto) {
        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        LocalDate fechaVigencia = LocalDate.now(); // valor por defecto

        if (fechaTexto != null && !fechaTexto.isEmpty()) {
            for (DateTimeFormatter formatter : formatos) {
                try {
                    fechaVigencia = LocalDate.parse(fechaTexto, formatter);
                    break; // si se parsea correctamente, sal del bucle
                } catch (DateTimeParseException ignored) {
                    // intentar con el siguiente formato
                }
            }
        }

        LocalDate hoy = LocalDate.now();
        long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaVigencia);

        if (diasRestantes < 0) {
            lbDiasRestantes.setText("⌛ Venció hace " + Math.abs(diasRestantes) + " días");
        } else {
            lbDiasRestantes.setText(diasRestantes + " días restantes");
        }
    }

    public void establishVector(List<VectorItem> vector,
            List<LocationOption> options,
            String searchId,
            int index) {
        if (index < 0 || index >= options.size()) {
            System.err.println("Índice fuera de rango para options.");
            return;
        }

        String value = options.get(index).getValue();
        System.out.println("Valor encontrado: " + value);

        int indexv = -1;
        for (int i = 0; i < vector.size(); i++) {
            if (vector.get(i).id.equals(searchId)) {
                indexv = i;
                break;
            }
        }

        if (indexv >= 0) {
            vector.get(indexv).value = value;
        } else {
            vector.add(new VectorItem(searchId, value));
        }
    }

    public void updateImage(Map<String, Node> itemMap,
            String vectorIM,
            String ip,
            String searchId) {

        try {

            Map<String, LocationDB> locationMap = new HashMap<>();
            System.out.println("IM PRIMIENTO IP  " + ip);
            for (LocationDB loc : LocationDB.getAllLocations()) {
                locationMap.put(loc.ip, loc);
            }

            if (ip != null && !ip.isEmpty()) {
                LocationDB loc = locationMap.get(ip);

                if (loc != null) {
                    String path = loc.getImageByIP(ip);

                    if (path != null) {

                        ImageView view = (ImageView) itemMap.get(vectorIM);
                        Image image = new Image(getClass().getResource(path).toExternalForm());
                        view.setImage(image);
                        System.out.println("Imagen actualizada para " + vectorIM + ": " + path);
                    } else {
                        System.out.println("Imagen no encontrada o vacía en ImagenPais.");
                    }
                } else {

                    System.out.println("El campo 'imagenPais' no está definido o no se encontró IP.");
                }
            } else {
                System.out.println("IP no proporcionada o inválida.");
            }

            arrayLocation(searchId, vectorIM, ip);

        } catch (Exception e) {
            System.err.println("Error al actualizar la imagen por IP: " + e.getMessage());
        }
    }

    public void arrayLocation(String searchId, String vectorIM, String ip) {
        for (List<Object> subArray : arrayPaisSSH) {
            if (subArray.size() > 0 && searchId.equals(subArray.get(0))) {
                for (Object obj : subArray) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> vectorMap = (Map<String, String>) obj;

                        if (vectorMap.get("vector") != null && vectorMap.get("vector").equals(vectorIM)) {
                            vectorMap.put("ip", (ip != null && !ip.isEmpty()) ? ip : "0.0.0.0");
                            System.out.println("IP actualizada para " + vectorIM + ": " + vectorMap.get("ip"));
                            return;
                        }
                    }

                }
                System.out.println("Vector no encontrado dentro del sub-array.");
                return;
            }
        }
        System.out.println("ID no encontrado en arrayPais.");
    }

    public void inicializarText(String ip, List<LocationOption> locationServer) {
        if (ip == null || ip.isEmpty()) {
            if (!locationServer.isEmpty()) {
                ip = locationServer.get(0).getValue();
            } else {
                return; // No hay IP ni datos
            }
        }

        String[] ipArray = ip.split(",");

        if (ipArray.length > 0) {
            String firstIP = ipArray[0].trim();
            textSSHubi1.setText(encontrarLabelPorIP(firstIP, locationServer));
            System.out.println("La primera IP es: " + firstIP);
        }

        if (ipArray.length > 1) {
            String secondIP = ipArray[1].trim();
            textSSHubi2.setText(encontrarLabelPorIP(secondIP, locationServer));
            System.out.println("La segunda IP es: " + secondIP);
        }

        if (ipArray.length > 2) {
            String thirdIP = ipArray[2].trim();
            textSSHubi3.setText(encontrarLabelPorIP(thirdIP, locationServer));
            System.out.println("La tercera IP es: " + thirdIP);
        }
    }

    public String encontrarLabelPorIP(String ip, List<LocationOption> locationServer) {
        try {
            if (locationServer == null || locationServer.isEmpty()) {
                return "";
            }

            for (int i = 0; i < locationServer.size(); i++) {
                LocationOption option = locationServer.get(i);
                if (option.getValue().equals(ip)) {
                    System.out.println("LOCATION ENCONTRADA: " + option.getLabel());
                    return option.getNamePais();
                }
            }

            System.out.println("Etiqueta no encontrada");
            return "";
        } catch (Exception e) {
            System.err.println("Error al buscar label por IP: " + e.getMessage());
            return "";
        }
    }

    @FXML
    private void showHwidDelete() {
        if (btHwidConfDelete.isVisible()) {
            btHwidConfDelete.setVisible(false);
            btHwidAdd.setVisible(true);
            btHwidShowDelete.setText("Eliminar");
        } else {
            btHwidConfDelete.setVisible(true);
            btHwidAdd.setVisible(false);
            btHwidShowDelete.setText("Cancelar");
        }
    }

    @FXML
    private void eliminarTagPorTexto() {
        String textoAEliminar = inpSSHwid.getText();

        if (textoAEliminar == null || textoAEliminar.trim().isEmpty()) {
            return;
        }

        Label tagAEliminar = null;

        for (Node node : tagsHWID.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(textoAEliminar)) {
                    tagAEliminar = label;
                    break;
                }
            }
        }

        if (tagAEliminar != null) {
            tagsHWID.getChildren().remove(tagAEliminar);
            inpSSHwid.clear();

            if (!tagsHWID.getChildren().isEmpty()) {
                Node nuevo = tagsHWID.getChildren().get(0);
                if (nuevo instanceof Label) {
                    inpSSHwid.setText(((Label) nuevo).getText());
                    highlightTag(tagsHWID, (Label) nuevo);
                }
            }
        }
    }

    public void startHwidTag(List<String> options) {
        tagsHWID.getChildren().clear();
        tagsHWID.setHgap(10);
        tagsHWID.setVgap(10);
        tagsHWID.setAlignment(Pos.CENTER); //

        if (options != null && !options.isEmpty()) {
            String primerValor = options.get(0);

            if (options.size() == 1 && primerValor.equalsIgnoreCase("cargando")) {

                tagsHWID.setVisible(false);
                tagsHWID.setManaged(false);
                return;
            }

            for (int i = 0; i < options.size(); i++) {
                String value = options.get(i);

                Label tag = new Label(value);
                tag.setStyle(
                        "-fx-background-color: #dcdcdc; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-margin: 5;");
                tag.setCursor(Cursor.HAND);

                Platform.runLater(() -> {
                    tag.setOnMouseClicked(e -> {
                        btHwidShowDelete.setVisible(true);
                        btHwidShowDelete.setDisable(false);
                        inpSSHwid.setText(value);
                        highlightTag(tagsHWID, tag);
                    });
                });

                tagsHWID.getChildren().add(tag);
            }

            inpSSHwid.setText(primerValor);
            highlightTag(tagsHWID, (Label) tagsHWID.getChildren().get(0));

        } else {
            lineTag1.setVisible(true);
            lineTag1.setManaged(true);
            tagsHWID.setVisible(false);
            tagsHWID.setManaged(false);
            lineTag2.setVisible(false);
            lineTag2.setManaged(false);
        }
    }

    private void highlightTag(FlowPane container, Label selected) {
        for (Node node : container.getChildren()) {
            if (node instanceof Label) {
                node.setStyle(
                        "-fx-background-color: #dcdcdc; " +
                                "-fx-padding: 5 10; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-cursor: hand;");
            }
        }

        selected.setStyle(
                "-fx-background-color: #8a2be2; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5 10; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

    }
}
