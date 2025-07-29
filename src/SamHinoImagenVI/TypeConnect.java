package SamHinoImagenVI;

public class TypeConnect {
    private String txt;
    private String inpName;
    private String vecL;
    private String ipVec;

    public TypeConnect(String txt, String inpName, String vecL, String ipVec) {
        this.txt = txt;
        this.inpName = inpName;
        this.vecL = vecL;
        this.ipVec = ipVec;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
    
    public void setInpName(String inpName) {
        this.inpName = inpName;
    }

    public void setVecL(String vecL) {
        this.vecL = vecL;
    }

    public void setIpVec(String ipVec) {
        this.ipVec = ipVec;
    }

    private String getTxt() { return txt; }
    public String getInpName() { return inpName; }
    public String getVecL() { return vecL; }
    public String getIpVec() { return ipVec; }

    
}
