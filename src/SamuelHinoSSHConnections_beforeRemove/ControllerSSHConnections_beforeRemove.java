package SamuelHinoSSHConnections_beforeRemove;

import SamuelHinoSSHConnections_beforeRemove.Model.Item;
import SamuelHinoSSHConnections_beforeRemove.Services.ItemConnectionService;
import SamuelHinoSSHConnections_beforeRemove.Services.ItemDeletionService;
import SamuelHinoSSHConnections_beforeRemove.Services.SSHService;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ControllerSSHConnections_beforeRemove {
    public void ejecutar() {
        Item currentItem = new Item("usuarioA", "ssh", null, "srv001");

        Map<String, String> itemConect = ItemConnectionService.getItemConnection(currentItem.getServersGenerated());

        if (itemConect != null && itemConect.containsKey("ip")) {
            try {
                handleItem(currentItem, itemConect.get("ip"));
            } catch (Exception e) {
                System.err.println("Error general: " + e.getMessage());
            }
        }

        if (currentItem.getServersGenerated() != null) {
            ItemDeletionService.delete(currentItem.getServersGenerated());
        }
    }

    private static void handleItem(Item currentItem, String ipData) throws Exception {
        String typeUser = currentItem.getTypeUser();
        String hwiDs = currentItem.getHwiDs();

        try {
            if (typeUser != null && !typeUser.toLowerCase().contains("hwid")) {
                SSHService.processIPs(ipData, Collections.singletonList(currentItem.getTitle()));
            } else {
                if (hwiDs == null) {
                    throw new IllegalArgumentException("No se puede procesar: hwiDs es null");
                }
                List<String> hwidUsers = List.of(hwiDs.split(","));
                SSHService.processIPs(ipData, hwidUsers);
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("not exist")) {
                throw e;
            }
        }
    }
}
