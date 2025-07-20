package Utility;

import java.util.ArrayList;
import java.util.List;

public class GestorDataBase {

    private DatabaseService dbService;

    public GestorDataBase(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public Item protocolBeforeInsert(Item item, Context context) throws Exception {
        item.setUserEnable(true);
        item.setRecord(new ArrayList<>());
        item.setOwner(context.getUserId());

        List<ResellerSSH> resultRS = dbService.queryResellerSSHByUserId(context.getUserId());
        List<Item> resultUser = dbService.queryServersGeneratedByOwner(context.getUserId());

        boolean limiteOK = false;
        if (!resultRS.isEmpty()) {
            ResellerSSH resellItem = resultRS.get(0);
            limiteOK = resellItem.getLimite() > resultUser.size();
        } else {
            limiteOK = 20 > resultUser.size();
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

        Item itemPredeterminado = new Item();
        itemPredeterminado.setOwner("defaultOwner");
        itemPredeterminado.setType("defaultType");
        itemPredeterminado.setUserEnable(false);

        List<String> excluir = List.of("record");
        List<String> proteger = List.of("owner");

        // Llamada directa sin método wrapper
        ItemUtils.copiarCamposSiVacios(item, itemPredeterminado, excluir, proteger);

        return item;
    }
}
