package com.example.lo_linking_park.utils;

import android.util.Log;

import com.example.lo_linking_park.model.Config;
import com.example.lo_linking_park.model.Salle;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataMigrationHelper {
    private static final String TAG = "DataMigrationHelper";
    private FirebaseFirestore db;

    public DataMigrationHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface MigrationCallback {
        void onSuccess();
        void onError(String error);
    }

    // Migrar todas las salles desde SQL a Firestore
    public void migrateSalles(MigrationCallback callback) {
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

        // Insertar todas las salles
        int[] completed = {0};
        int total = salles.size();

        for (Salle salle : salles) {
            db.collection("salles")
                .add(salle)
                .addOnSuccessListener(documentReference -> {
                    completed[0]++;
                    Log.d(TAG, "Salle migrada: " + salle.getNom());

                    if (completed[0] == total) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al migrar salle: " + salle.getNom(), e);
                    callback.onError("Error al migrar salles: " + e.getMessage());
                });
        }
    }

    // Migrar configuración desde SQL a Firestore
    public void migrateConfiguration(MigrationCallback callback) {
        List<Config> configs = new ArrayList<>();

        configs.add(new Config("tarifa_per_hora", "2.50",
            "Tarifa per hora en euros", "decimal", "admin"));
        configs.add(new Config("temps_maxim_default", "120",
            "Temps màxim per defecte en minuts", "int", "admin"));
        configs.add(new Config("temps_aviso_default", "15",
            "Temps d'avís per defecte abans de finalitzar (minuts)", "int", "admin"));
        configs.add(new Config("max_vehicles_per_usuari", "5",
            "Nombre màxim de vehicles per usuari", "int", "sistema"));
        configs.add(new Config("radi_localitzacio_metres", "100",
            "Radi de proximitat per activar parquímetre (metres)", "int", "admin"));

        int[] completed = {0};
        int total = configs.size();

        for (Config config : configs) {
            db.collection("configuration")
                .document(config.getClau())
                .set(config)
                .addOnSuccessListener(aVoid -> {
                    completed[0]++;
                    Log.d(TAG, "Configuració migrada: " + config.getClau());

                    if (completed[0] == total) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al migrar configuració: " + config.getClau(), e);
                    callback.onError("Error al migrar configuració: " + e.getMessage());
                });
        }
    }

    // Migrar todo de una vez
    public void migrateAll(MigrationCallback callback) {
        migrateSalles(new MigrationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Salles migradas correctamente");

                migrateConfiguration(new MigrationCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Configuración migrada correctamente");
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Verificar si ya se han migrado los datos
    public void checkIfDataExists(CheckDataCallback callback) {
        db.collection("salles")
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean exists = !queryDocumentSnapshots.isEmpty();
                callback.onResult(exists);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al verificar datos", e);
                callback.onResult(false);
            });
    }

    public interface CheckDataCallback {
        void onResult(boolean exists);
    }
}
