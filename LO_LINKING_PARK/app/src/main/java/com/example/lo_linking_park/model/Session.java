package com.example.lo_linking_park.model;

import java.util.Date;

public class Session {
    private String id;
    private String usuariId;
    private String vehicleId;
    private String salleId;
    private Date dataInici;
    private Date dataFi;
    private int tempsMaximMinuts;
    private int avisoTempsMinuts;
    private String tipusFinalitzacio; // "manual", "temps", "admin"
    private double latitudInici;
    private double longitudInici;
    private String estat; // "actiu", "finalitzat", "cancel·lat"
    private boolean avisoEnviat;
    private boolean avisoFinalEnviat;
    private Date creatEl;
    private Date actualitzatEl;

    // Constructor vacío requerido por Firestore
    public Session() {
        this.estat = "actiu";
        this.avisoEnviat = false;
        this.avisoFinalEnviat = false;
    }

    public Session(String usuariId, String vehicleId, String salleId, int tempsMaximMinuts,
                   int avisoTempsMinuts, double latitudInici, double longitudInici) {
        this.usuariId = usuariId;
        this.vehicleId = vehicleId;
        this.salleId = salleId;
        this.dataInici = new Date();
        this.tempsMaximMinuts = tempsMaximMinuts;
        this.avisoTempsMinuts = avisoTempsMinuts;
        this.latitudInici = latitudInici;
        this.longitudInici = longitudInici;
        this.estat = "actiu";
        this.avisoEnviat = false;
        this.avisoFinalEnviat = false;
        this.creatEl = new Date();
        this.actualitzatEl = new Date();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuariId() {
        return usuariId;
    }

    public void setUsuariId(String usuariId) {
        this.usuariId = usuariId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getSalleId() {
        return salleId;
    }

    public void setSalleId(String salleId) {
        this.salleId = salleId;
    }

    public Date getDataInici() {
        return dataInici;
    }

    public void setDataInici(Date dataInici) {
        this.dataInici = dataInici;
    }

    public Date getDataFi() {
        return dataFi;
    }

    public void setDataFi(Date dataFi) {
        this.dataFi = dataFi;
    }

    public int getTempsMaximMinuts() {
        return tempsMaximMinuts;
    }

    public void setTempsMaximMinuts(int tempsMaximMinuts) {
        this.tempsMaximMinuts = tempsMaximMinuts;
    }

    public int getAvisoTempsMinuts() {
        return avisoTempsMinuts;
    }

    public void setAvisoTempsMinuts(int avisoTempsMinuts) {
        this.avisoTempsMinuts = avisoTempsMinuts;
    }

    public String getTipusFinalitzacio() {
        return tipusFinalitzacio;
    }

    public void setTipusFinalitzacio(String tipusFinalitzacio) {
        this.tipusFinalitzacio = tipusFinalitzacio;
    }

    public double getLatitudInici() {
        return latitudInici;
    }

    public void setLatitudInici(double latitudInici) {
        this.latitudInici = latitudInici;
    }

    public double getLongitudInici() {
        return longitudInici;
    }

    public void setLongitudInici(double longitudInici) {
        this.longitudInici = longitudInici;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public boolean isAvisoEnviat() {
        return avisoEnviat;
    }

    public void setAvisoEnviat(boolean avisoEnviat) {
        this.avisoEnviat = avisoEnviat;
    }

    public boolean isAvisoFinalEnviat() {
        return avisoFinalEnviat;
    }

    public void setAvisoFinalEnviat(boolean avisoFinalEnviat) {
        this.avisoFinalEnviat = avisoFinalEnviat;
    }

    public Date getCreatEl() {
        return creatEl;
    }

    public void setCreatEl(Date creatEl) {
        this.creatEl = creatEl;
    }

    public Date getActualitzatEl() {
        return actualitzatEl;
    }

    public void setActualitzatEl(Date actualitzatEl) {
        this.actualitzatEl = actualitzatEl;
    }
}
