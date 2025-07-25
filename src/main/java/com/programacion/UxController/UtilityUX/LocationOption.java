package com.programacion.UxController.UtilityUX;

public class LocationOption {
    private String value;
    private String label;
    private String namePais;


    public LocationOption(String value, String label, String namePais) {
        this.value = value;
        this.label = label;
        this.namePais = namePais;
    }

    public String getNamePais() {
        return namePais;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
