package com.example.lo_linking_park.model;

public class Usuario {
    private String id;
    private String nom;
    private String cognoms;
    private String email;
    private String telefon;
    private String rol; // "admin" o "usuari"
    private boolean actiu;
    private long creatEl;
    private long actualitzatEl;

    // Constructor vacío requerido por Firebase Realtime Database
    public Usuario(String nom, String cognoms, String email, String telefon, String password) {
        this.actiu = true;
        this.rol = "usuari";
    }

    // Constructor completo
    public Usuario(String id, String nom, String cognoms, String email, String telefon, String rol, boolean actiu, long creatEl, long actualitzatEl) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
        this.telefon = telefon;
        this.rol = rol;
        this.actiu = actiu;
        this.creatEl = creatEl;
        this.actualitzatEl = actualitzatEl;
    }

    // Constructor simplificado (sin id ni timestamps)
    public Usuario(String nom, String cognoms, String email, String telefon) {
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
        this.telefon = telefon;
        this.rol = "usuari";
        this.actiu = true;
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

    public boolean isAdmin() {
        return "admin".equals(this.rol);
    }
}
