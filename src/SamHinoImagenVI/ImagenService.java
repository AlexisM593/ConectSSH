package SamHinoImagenVI;

import java.util.List;

public class ImagenService {
    public static void imagenVI(
            UIHandler itemHandler, // interfaz para manejar elementos de UI como $item
            String ip,
            String vectorIM,
            String searchId,
            String typeConn,
            List<LocationOption> arrayPais
    ) {
        List<LocationOption> options = LocationServer.getOptions(); // Simula locationServer
        int index = findIndex(options, ip);

        GlobalFunctions.updateImage(itemHandler, vectorIM, ip, arrayPais, searchId);

        int numty = typeConn.equals("XRay") ? 0 : 1;

        String txt = TypeConnects.get(numty).getTxt();
        String inpName = TypeConnects.get(numty).getInpName();
        String vecL = TypeConnects.get(numty).getVecL();
        String ipVec = TypeConnects.get(numty).getIpVec();

        if (index != -1) {
            for (int i = 1; i <= 3; i++) {
                if (vectorIM.equals(vecL + i)) {
                    GlobalFunctions.establishVector(IpVecStorage.get(ipVec + i), options, searchId, index);
                    itemHandler.setText(txt + i, options.get(index).getLabel());
                }
            }
        } else {
            itemHandler.setSrc(vectorIM, "wix:vector://v1/11062b_0f72fc8fda12403cb1be3ee15a3ea67f.svg/C%C3%ADrculo%20interior%20c%C3%ADrculo.svg");
            if (itemHandler.isEnabled(inpName)) {
                for (int i = 1; i <= 3; i++) {
                    if (vectorIM.equals(vecL + i)) {
                        itemHandler.setText(txt + i, "Añadir ➕");
                    }
                }
            }
        }

        System.out.println("La IP del servicio es: " + ip);
    }

    private static int findIndex(List<LocationOption> options, String ip) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getValue().equals(ip)) {
                return i;
            }
        }
        return -1;
    }
}
