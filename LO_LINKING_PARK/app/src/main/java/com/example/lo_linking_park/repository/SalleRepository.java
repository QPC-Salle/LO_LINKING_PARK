package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Salle;

import java.util.ArrayList;
import java.util.List;

/**
 * SalleRepository - Repositorio para gestionar Salles
 * NOTA: Firebase ha sido eliminado. Este repositorio necesita ser conectado a MariaDB
 */
public class SalleRepository {
    private static final String TAG = "SalleRepository";
    private static SalleRepository instance;

    private SalleRepository() {
        // TODO: Inicializar conexión a MariaDB
    }

    public static synchronized SalleRepository getInstance() {
        if (instance == null) {
            instance = new SalleRepository();
        }
        return instance;
    }

    public interface SalleCallback {
        void onSuccess(String salleId);
        void onError(String error);
    }

    public interface SalleListCallback {
        void onSuccess(List<Salle> salles);
        void onError(String error);
    }

    public interface SalleDataCallback {
        void onSuccess(Salle salle);
        void onError(String error);
    }

    /**
     * Obtiene todas las Salles desde la base de datos
     * TODO: Implementar con MariaDB
     */
    public void getAllSalles(SalleListCallback callback) {
        // Por ahora, retorna una lista vacía
        Log.w(TAG, "getAllSalles() no implementado - requiere conexión a MariaDB");
        callback.onSuccess(new ArrayList<>());
    }

    /**
     * Obtiene una Salle por ID
     * TODO: Implementar con MariaDB
     */
    public void getSalleById(String salleId, SalleDataCallback callback) {
        Log.w(TAG, "getSalleById() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    /**
     * Crea una nueva Salle
     * TODO: Implementar con MariaDB
     */
    public void createSalle(Salle salle, SalleCallback callback) {
        Log.w(TAG, "createSalle() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    /**
     * Actualiza una Salle existente
     * TODO: Implementar con MariaDB
     */
    public void updateSalle(String salleId, Salle salle, SalleCallback callback) {
        Log.w(TAG, "updateSalle() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    /**
     * Elimina una Salle
     * TODO: Implementar con MariaDB
     */
    public void deleteSalle(String salleId, SalleCallback callback) {
        Log.w(TAG, "deleteSalle() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }
}

