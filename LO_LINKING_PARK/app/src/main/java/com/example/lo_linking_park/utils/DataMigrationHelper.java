package com.example.lo_linking_park.utils;

import com.example.lo_linking_park.model.Configuracio;
import com.example.lo_linking_park.model.Salle;

import java.util.ArrayList;
import java.util.List;

/**
 * DataMigrationHelper - Helper para preparar datos para migración a MariaDB
 * Este archivo prepara los datos iniciales que deberán insertarse en la base de datos
 */
public class DataMigrationHelper {

    /**
     * Obtiene la lista de Salles predefinidas
     */
    public static List<Salle> getSallesData() {
        List<Salle> salles = new ArrayList<>();

        // La Salle Campus Barcelona
        Salle salle1 = new Salle(
            "La Salle Campus Barcelona",
            "Barcelona",
            "Carrer de Sant Joan de la Salle, 42, 08022 Barcelona",
            41.41560000,
            2.17450000,
            100
        );
        salles.add(salle1);

        // La Salle Bonanova
        Salle salle2 = new Salle(
            "La Salle Bonanova",
            "Barcelona",
            "Passeig de la Bonanova, 8, 08022 Barcelona",
            41.40890000,
            2.13640000,
            75
        );
        salles.add(salle2);

        // La Salle Gràcia
        Salle salle3 = new Salle(
            "La Salle Gràcia",
            "Barcelona",
            "Carrer de Girona, 24-26, 08010 Barcelona",
            41.39420000,
            2.16560000,
            50
        );
        salles.add(salle3);

        // La Salle Tarragona
        Salle salle4 = new Salle(
            "La Salle Tarragona",
            "Tarragona",
            "Carrer de la Salle, 1, 43001 Tarragona",
            41.11890000,
            1.24450000,
            60
        );
        salles.add(salle4);

        // La Salle Girona
        Salle salle5 = new Salle(
            "La Salle Girona",
            "Girona",
            "Avinguda de Montilivi, 16, 17003 Girona",
            41.97940000,
            2.82140000,
            80
        );
        salles.add(salle5);

        return salles;
    }

    /**
     * Obtiene la lista de configuraciones predefinidas
     */
    public static List<Configuracio> getConfigurationData() {
        List<Configuracio> configs = new ArrayList<>();

        configs.add(new Configuracio("tarifa_per_hora", "2.50",
            "Tarifa per hora en euros", "decimal", "admin"));
        configs.add(new Configuracio("temps_maxim_default", "120",
            "Temps màxim per defecte en minuts", "int", "admin"));
        configs.add(new Configuracio("temps_aviso_default", "15",
            "Temps d'avís per defecte abans de finalitzar (minuts)", "int", "admin"));
        configs.add(new Configuracio("max_vehicles_per_usuari", "5",
            "Nombre màxim de vehicles per usuari", "int", "sistema"));
        configs.add(new Configuracio("radi_localitzacio_metres", "100",
            "Radi de proximitat per activar parquímetre (metres)", "int", "admin"));

        return configs;
    }
}
