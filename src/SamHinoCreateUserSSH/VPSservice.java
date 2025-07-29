package SamHinoCreateUserSSH;

public class VPSservice {
    private String ip;
    private String clave;

    public VPSservice(String ip, String clave) {
        this.ip = ip;
        this.clave = clave;
    }

    public String getIp() {
        return ip;
    }

    public String getClave() {
        return clave;
    }
}
