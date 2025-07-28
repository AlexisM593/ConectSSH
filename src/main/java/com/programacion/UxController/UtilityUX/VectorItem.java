package com.programacion.UxController.UtilityUX;

public class VectorItem {
    public String id;
    public String value;

    public VectorItem(String id, String value) {
        this.id = id;
        this.value = value;
    }


      public String getId() {
        return id;
    }

       public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

}

