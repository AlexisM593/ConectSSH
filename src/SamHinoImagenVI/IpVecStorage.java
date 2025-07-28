package SamHinoImagenVI;

import java.util.HashMap;
import java.util.Map;

public class IpVecStorage {
    private static Map<String, String> storage = new HashMap<>();

    static {
        storage.put("ipVecXR1", "192.168.0.101");
        storage.put("ipVecXR2", "192.168.0.102");
        storage.put("ipVecXR3", "192.168.0.103");
        // etc.
    }

    public static String get(String key) {
        return storage.getOrDefault(key, "");
    }
}
