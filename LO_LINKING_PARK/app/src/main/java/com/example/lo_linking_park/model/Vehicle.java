package com.example.lo_linking_park.model;

import java.util.Date;

public class Vehicle {
    private String id;
    private String usuariId;
    private String matricula;
    private String marca;
    private String model;
    private String color;
    private int anyFabricacio;
    private boolean predeterminat;
    private boolean actiu;
    private Date creatEl;
    private Date actualitzatEl;

    // Constructor vacío requerido por Firestore
    public Vehicle() {
        this.actiu = true;
        this.predeterminat = false;
    }

    public Vehicle(String usuariId, String matricula, String marca, String model, String color, int anyFabricacio) {
        this.usuariId = usuariId;
        this.matricula = matricula;
        this.marca = marca;
        this.model = model;
        this.color = color;
        this.anyFabricacio = anyFabricacio;
        this.actiu = true;
        this.predeterminat = false;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAnyFabricacio() {
        return anyFabricacio;
    }

    public void setAnyFabricacio(int anyFabricacio) {
        this.anyFabricacio = anyFabricacio;
    }

    public boolean isPredeterminat() {
        return predeterminat;
    }

    public void setPredeterminat(boolean predeterminat) {
        this.predeterminat = predeterminat;
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
