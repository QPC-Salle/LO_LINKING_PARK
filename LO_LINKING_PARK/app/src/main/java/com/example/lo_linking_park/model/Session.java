package com.example.lo_linking_park.model;

public class Session {
    private String id;
    private String usuariId;
    private String vehicleId;
    private String salleId;
    private long dataInici;
    private long dataFi;
    private int tempsMaximMinuts;
    private int avisoTempsMinuts;
    private String tipusFinalitzacio; // "manual", "temps", "admin"
    private double latitudInici;
    private double longitudInici;
    private String estat; // "actiu", "finalitzat", "cancel·lat"
    private boolean avisoEnviat;
    private boolean avisoFinalEnviat;
    private long creatEl;
    private long actualitzatEl;

    // Constructor vacío requerido por Firebase Realtime Database
    public Session() {
        this.estat = "actiu";
        this.avisoEnviat = false;
        this.avisoFinalEnviat = false;
    }

    // Constructor completo
    public Session(String id, String usuariId, String vehicleId, String salleId, long dataInici, long dataFi, int tempsMaximMinuts, int avisoTempsMinuts, String tipusFinalitzacio, double latitudInici, double longitudInici, String estat, boolean avisoEnviat, boolean avisoFinalEnviat, long creatEl, long actualitzatEl) {
        this.id = id;
        this.usuariId = usuariId;
        this.vehicleId = vehicleId;
        this.salleId = salleId;
        this.dataInici = dataInici;
        this.dataFi = dataFi;
        this.tempsMaximMinuts = tempsMaximMinuts;
        this.avisoTempsMinuts = avisoTempsMinuts;
        this.tipusFinalitzacio = tipusFinalitzacio;
        this.latitudInici = latitudInici;
        this.longitudInici = longitudInici;
        this.estat = estat;
        this.avisoEnviat = avisoEnviat;
        this.avisoFinalEnviat = avisoFinalEnviat;
        this.creatEl = creatEl;
        this.actualitzatEl = actualitzatEl;
    }

    // Constructor simplificado (sin id ni timestamps)
    public Session(String usuariId, String vehicleId, String salleId, int tempsMaximMinuts, int avisoTempsMinuts, double latitudInici, double longitudInici) {
        this.usuariId = usuariId;
        this.vehicleId = vehicleId;
        this.salleId = salleId;
        this.dataInici = System.currentTimeMillis();
        this.tempsMaximMinuts = tempsMaximMinuts;
        this.avisoTempsMinuts = avisoTempsMinuts;
        this.latitudInici = latitudInici;
        this.longitudInici = longitudInici;
        this.estat = "actiu";
        this.avisoEnviat = false;
        this.avisoFinalEnviat = false;
        this.creatEl = System.currentTimeMillis();
        this.actualitzatEl = System.currentTimeMillis();
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

    public long getDataInici() {
        return dataInici;
    }

    public void setDataInici(long dataInici) {
        this.dataInici = dataInici;
    }

    public long getDataFi() {
        return dataFi;
    }

    public void setDataFi(long dataFi) {
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

    public long getCreatEl() {
        return creatEl;
    }

    public void setCreatEl(long creatEl) {
        this.creatEl = creatEl;
    }

    public long getActualitzatEl() {
        return actualitzatEl;
    }

    public void setActualitzatEl(long actualitzatEl) {
        this.actualitzatEl = actualitzatEl;
    }
}
