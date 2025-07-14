package Utility;

import java.util.ArrayList;
import java.util.List;

public class GestorDataBase {

    private DatabaseService dbService;

    public GestorDataBase(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public Item protocolBeforeInsert(Item item, Context context) throws Exception {
        // Equivalente a item.userEnable = true;
        item.setUserEnable(true);

        // item.record = []
        item.setRecord(new ArrayList<>());

        // item.owner = context.userId;
        item.setOwner(context.getUserId());

        // Consulta ResellerSSH con filtro por userId
        List<ResellerSSH> resultRS = dbService.queryResellerSSHByUserId(context.getUserId());

        // Consulta ServersGenerated con filtro por owner
        List<Item> resultUser = dbService.queryServersGeneratedByOwner(context.getUserId());

        // Función boolean anidada convertida a variable booleana
        boolean limiteOK = false;
        if (!resultRS.isEmpty()) {
            ResellerSSH resellItem = resultRS.get(0);
            if (resellItem.getLimite() > resultUser.size()) {
                limiteOK = true;
            } else {
                limiteOK = false;
            }
        } else {
            if (20 > resultUser.size()) {
                limiteOK = true;
            } else {
                limiteOK = false;
            }
        }

        if (!limiteOK) {
            throw new Exception("Limite alcanzado.");
        }

        if (item.getType() != null && item.getType().contains("Ridge")) {
            if (!resultRS.isEmpty()) {
                ResellerSSH resellItem = resultRS.get(0);
                double costo = 0.003;
                if (resellItem.getUsoPrecio() != null) {
                    costo += resellItem.getUsoPrecio();
                }
                resellItem.setUsoPrecio(Math.round(costo * 10000.0) / 10000.0);

                try {
                    dbService.updateResellerSSH(resellItem);
                    System.out.println("Item guardado correctamente: " + resellItem.getId());
                } catch (Exception e) {
                    System.out.println("Ocurrió un error: " + e.getMessage());
                }
            } else {
                System.out.println("No se encontraron resultados para el reseller con userId: " + context.getUserId());
            }
        }

        return item;
    }
}
