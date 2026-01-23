package com.example.lo_linking_park;
public class Coche {
    private String plate,color,brand;
    private Persona Propietari;

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Persona getPropietari() {
        return Propietari;
    }

    public void setPropietari(Persona propietari) {
        Propietari = propietari;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
