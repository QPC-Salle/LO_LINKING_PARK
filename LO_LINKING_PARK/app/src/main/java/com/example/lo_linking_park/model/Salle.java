package com.example.lo_linking_park.model;

import java.util.Date;

public class Salle {
    private String id;
    private String nom;
    private String ciutat;
    private String adreca;
    private double latitud;
    private double longitud;
    private int placesTotals;
    private int placesDisponibles;
    private boolean actiu;
    private Date creatEl;
    private Date actualitzatEl;

    // Constructor vacío requerido por Firestore
    public Salle() {
        this.actiu = true;
    }

    public Salle(String nom, String ciutat, String adreca, double latitud, double longitud, int placesTotals) {
        this.nom = nom;
        this.ciutat = ciutat;
        this.adreca = adreca;
        this.latitud = latitud;
        this.longitud = longitud;
        this.placesTotals = placesTotals;
        this.placesDisponibles = placesTotals;
        this.actiu = true;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCiutat() {
        return ciutat;
    }

    public void setCiutat(String ciutat) {
        this.ciutat = ciutat;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getPlacesTotals() {
        return placesTotals;
    }

    public void setPlacesTotals(int placesTotals) {
        this.placesTotals = placesTotals;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public boolean isActiu() {
        return actiu;
    }

    public void setActiu(boolean actiu) {
        this.actiu = actiu;
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
