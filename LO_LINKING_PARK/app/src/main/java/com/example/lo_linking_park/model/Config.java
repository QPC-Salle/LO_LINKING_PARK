package com.example.lo_linking_park.model;

import java.util.Date;

public class Configuracio {
    private String id;
    private String clau;
    private String valor;
    private String descripcio;
    private String tipusDada;
    private String modificablePer;
    private Date actualitzatEl;

    public Configuracio() {
        this.tipusDada = "string";
        this.modificablePer = "admin";
    }

    public Configuracio(String clau, String valor, String descripcio, String tipusDada, String modificablePer) {
        this.clau = clau;
        this.valor = valor;
        this.descripcio = descripcio;
        this.tipusDada = tipusDada;
        this.modificablePer = modificablePer;
        this.actualitzatEl = new Date();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClau() { return clau; }
    public void setClau(String clau) { this.clau = clau; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public String getDescripcio() { return descripcio; }
    public void setDescripcio(String descripcio) { this.descripcio = descripcio; }
    public String getTipusDada() { return tipusDada; }
    public void setTipusDada(String tipusDada) { this.tipusDada = tipusDada; }
    public String getModificablePer() { return modificablePer; }
    public void setModificablePer(String modificablePer) { this.modificablePer = modificablePer; }
    public Date getActualitzatEl() { return actualitzatEl; }
    public void setActualitzatEl(Date actualitzatEl) { this.actualitzatEl = actualitzatEl; }

    public int getValorInt() {
        try { return Integer.parseInt(valor); } catch (NumberFormatException e) { return 0; }
    }

    public double getValorDouble() {
        try { return Double.parseDouble(valor); } catch (NumberFormatException e) { return 0.0; }
    }

    public boolean getValorBoolean() {
        return Boolean.parseBoolean(valor);
    }
}
