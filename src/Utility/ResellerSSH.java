package Utility;

public class ResellerSSH {
private String id;
    private int limite;
    private Double usoPrecio;

    public ResellerSSH(String id, int limite, Double usoPrecio) {
        this.id = id;
        this.limite = limite;
        this.usoPrecio = usoPrecio;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getLimite() {
        return limite;
    }
    public void setLimite(int limite) {
        this.limite = limite;
    }
    public Double getUsoPrecio() {
        return usoPrecio;
    }
    public void setUsoPrecio(Double usoPrecio) {
        this.usoPrecio = usoPrecio;
    }
}
