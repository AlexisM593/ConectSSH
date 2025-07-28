package SamHinoCreateUserSSH;
import java.util.HashMap;
import java.util.Map;

public class VPSServiceManager {
    private Map<String, VPSservice> vpsData;

    public VPSServiceManager() {
        // Simula una base de datos con IP y clave SSH
        vpsData = new HashMap<>();
        vpsData.put("192.168.1.100", new VPSservice("192.168.1.100", "claveDePrueba123"));
        vpsData.put("192.168.1.101", new VPSservice("192.168.1.101", "claveDePrueba456"));
    }

    public String getSSHKey(String ip) throws Exception {
        VPSservice registro = vpsData.get(ip);
        if (registro == null) {
            throw new Exception("No se encontró la clave para la IP: " + ip);
        }
        return registro.getClave();
    }

}
