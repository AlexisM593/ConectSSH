package com.programacion.UxController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.programacion.Protocol.SecureShell_DB;
import com.programacion.Utility.LocationDB;
import com.programacion.UxController.UtilityUX.LocationOption;
import com.programacion.UxController.UtilityUX.VectorIP;
import com.programacion.UxController.UtilityUX.VectorItem;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;


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
    private FlowPane tagsHWID;
        @FXML
    private CheckBox switchStatus;

    private SecureShell_DB item;

    List<List<Object>> arrayPaisSSH = new ArrayList<>();
    Map<String, List<VectorItem>> ipVecStorage = new HashMap<>();
 List<LocationOption> locationServer = List.of(
            new LocationOption("186.68.104.139", "EC", "Ecuador"));
         

    public void initVector() {

        ipVecStorage.put("ipVecSH1", new ArrayList<>());
        ipVecStorage.put("ipVecSH2", new ArrayList<>());
        ipVecStorage.put("ipVecSH3", new ArrayList<>());

    }

     @FXML
    public void initialize() {
        initVector();
        btSSHsaveItem.setVisible(false);
        btHwidConfDelete.setVisible(false);
        btSSHconfDelete.setVisible(false);
        tagsHWID.setVisible(false);
        tagsHWID.setManaged(false);
        inpSSHwid.setDisable(true);
        boxHwid.setVisible(false);
        boxHwid.setManaged(false);
        inpNickName.setDisable(true);
        dateSSH.setDisable(true);

    }

      @FXML
      public void handleSaveItem(){

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

            tagsHWID.setVisible(true);
            tagsHWID.setManaged(true);
            
            btSSHsaveItem.setVisible(true);
            btSSHshowDelete.setVisible(false);

            inpSSHwid.setDisable(false);
            inpSSHwid.setText("");

            btHwidAdd.setDisable(true);
          

        } else {
            // Modo edición desactivado
            btSSHshowDelete.setText("Eliminar");
            btSSHmodify.setText("Modificar");
            switchStatus.setDisable(true);
            boxHwid.setManaged(false);
            boxHwid.setVisible(false);
            btSSHsaveItem.setVisible(false);
            btSSHshowDelete.setVisible(true);
            dateSSH.setDisable(true);
            btSSHconfDelete.setVisible(false);
            tagsHWID.setVisible(false);
            tagsHWID.setManaged(false);
            inpSSHwid.setDisable(true);
        }
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

            switchStatus.setDisable(true);
            switchStatus.setSelected(true);

            tagsHWID.setVisible(true);
            tagsHWID.setManaged(true);

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
                        itemMap, // tu objeto o vista actual
                        ipArray.get(i), // IP limpia
                        imageSelectors[i],
                        item.getUsuarioItem(),
                        arrayPaisSSH // lista acumuladora
                );

            }
        }

        inicializarText(ipString, locationServer);
    }

    public void imagenVI(Map<String, Node> itemMap,
            String ip,
            String vectorIM,
            String searchId,
            List<List<Object>> arrayPais) {

        int index = -1;
        for (int i = 0; i < locationServer.size(); i++) {
            if (locationServer.get(i).getValue().equals(ip)) {
                index = i;
                break;
            }
        }

        updateImage(itemMap, vectorIM, ip, arrayPais, searchId);

        String txt = "textSSHubi";
        String inpName = "dateSSH";
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
            imageView.setImage(new Image("src\\main\\resources\\imagen\\circle-button.png")); // Cambia al path correcto

            TextField input = (TextField) itemMap.get(inpName);
            if (!input.isDisabled()) {
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate fechaVigencia;

    if (fechaTexto == null || fechaTexto.isEmpty()) {
        fechaVigencia = LocalDate.now();
    } else {
        try {
            fechaVigencia = LocalDate.parse(fechaTexto, formatter);
        } catch (DateTimeParseException e) {
            fechaVigencia = LocalDate.now(); // por defecto si hay error
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
            List<List<Object>> arrayPais,
            String searchId) {

        try {

            Map<String, LocationDB> locationMap = new HashMap<>();
            System.out.println("IM PRIMIENTO IP  "+ip);
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

           arrayLocation(arrayPais, searchId, vectorIM, ip);

        } catch (Exception e) {
            System.err.println("Error al actualizar la imagen por IP: " + e.getMessage());
        }
    }

    public void arrayLocation(List<List<Object>> arrayPais, String searchId, String vectorIM, String ip) {
        for (List<Object> subArray : arrayPais) {
            if (subArray.size() > 0 && searchId.equals(subArray.get(0))) {
                for (Object obj : subArray) {
                    if (obj instanceof VectorIP) {
                        VectorIP vectorIP = (VectorIP) obj;
                        if (vectorIP.vector.equals(vectorIM)) {
                            vectorIP.ip = (ip != null && !ip.isEmpty()) ? ip : "0.0.0.0";
                            System.out.println("IP actualizada para " + vectorIM + ": " + vectorIP.ip);
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

                tag.setOnMouseClicked(e -> {
                    inpSSHwid.setText(value);
                    highlightTag(tagsHWID, tag);
                });

                tagsHWID.getChildren().add(tag);
            }

            inpSSHwid.setText(primerValor);
            highlightTag(tagsHWID, (Label) tagsHWID.getChildren().get(0));

        } else {
            tagsHWID.setVisible(false);
            tagsHWID.setManaged(false);
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
                "-fx-cursor: hand;"
            );
        }
    }

    selected.setStyle(
        "-fx-background-color: #8a2be2; " +
        "-fx-text-fill: white; " +
        "-fx-padding: 5 10; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-cursor: hand;"
    );

  

}
}
