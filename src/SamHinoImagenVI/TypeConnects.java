package SamHinoImagenVI;

import java.util.ArrayList;
import java.util.List;

public class TypeConnects {
    private static List<TypeConnect> connects = new ArrayList<>();

    static {
        connects.add(new TypeConnect("txtXR", "inpXR", "vecXR", "ipVecXR"));
        connects.add(new TypeConnect("txtOther", "inpOther", "vecOther", "ipVecOther"));
    }

    public static TypeConnect get(int index) {
        return connects.get(index);
    }
}
