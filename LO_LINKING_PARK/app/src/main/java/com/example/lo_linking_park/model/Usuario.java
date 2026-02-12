package com.example.lo_linking_park.model;

import java.util.Date;

public class Usuario {
    private String id;
    private String nom;
    private String cognoms;
    private String email;
    private String telefon;
    private String rol; // "admin" o "usuari"
    private boolean actiu;
    private Date creatEl;
    private Date actualitzatEl;

    // Constructor vacío requerido por Firestore
    public Usuario() {
        this.actiu = true;
        this.rol = "usuari";
    }

    public Usuario(String nom, String cognoms, String email, String telefon) {
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
        this.telefon = telefon;
        this.rol = "usuari";
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

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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

    public boolean isAdmin() {
        return "admin".equals(this.rol);
    }
}
