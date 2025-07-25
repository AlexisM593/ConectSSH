package com.programacion.Protocol;

public  class Protocol {
   private  String usuarioItem;
    private  String ip;
    private  String vigencia;
    private  String fechaCreacion;
    private  String owner;
    private  boolean estado;
 
    public Protocol(String usuarioItem, String ip, String vigencia, String fechaCreacion, String owner, boolean estado) {
        this.usuarioItem = usuarioItem;
        this.ip = ip;
        this.vigencia = vigencia;
        this.fechaCreacion = fechaCreacion;
        this.owner = owner;
        this.estado = estado;
    }


        public Protocol(String usuarioItem, String ip, String vigencia, boolean estado) {
        this.usuarioItem = usuarioItem;
        this.ip = ip;
        this.vigencia = vigencia;
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getUsuarioItem() {
        return usuarioItem;
    }

    public void setUsuarioItem(String usuarioItem) {
        this.usuarioItem = usuarioItem;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


}
