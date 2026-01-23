package com.example.lo_linking_park;

public class Persona {
    private String name,surname,DNI;
    private int Admin;
    //Constructors
    public Persona(String name, String surname, String DNI) {
        this.name = name;
        this.surname = surname;
        this.DNI = DNI;
    }
    public Persona(){
        this.Admin = 0;
    }
    public Persona(String name){
        this.name = name;
        this.Admin = 0;
        if (name.equals("admin")) this.Admin = 1;
    }
    //Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }
    public int getAdmin(){
        return this.Admin;
    }
    public boolean validarDNI(String dni) {
        if (dni.length() != 9) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            if (!Character.isDigit(dni.charAt(i))) {
                return false;
            }
        }
        char letra = dni.charAt(8);
        if (!Character.isLetter(letra)) {
            return false;
        }
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(dni.substring(0, 8));
        char letraCorrecta = letras.charAt(numero % 23);

        return Character.toUpperCase(letra) == letraCorrecta;
    }
}
