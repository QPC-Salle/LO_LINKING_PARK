package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Session;
import com.example.lo_linking_park.model.Usuario;
import com.example.lo_linking_park.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * RealtimeDatabaseManager - Gestor de base de datos en tiempo real
 * NOTA: Firebase ha sido eliminado. Este manager necesita ser conectado a MariaDB
 */
public class RealtimeDatabaseManager {
    private static final String TAG = "RealtimeDatabaseManager";
    private static RealtimeDatabaseManager instance;

    private RealtimeDatabaseManager() {
        // TODO: Inicializar conexión a MariaDB
    }

    public static synchronized RealtimeDatabaseManager getInstance() {
        if (instance == null) {
            instance = new RealtimeDatabaseManager();
        }
        return instance;
    }

    // Callbacks
    public interface UserCallback {
        void onSuccess(String userId);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface VehicleCallback {
        void onSuccess(Vehicle vehicle);
        void onError(String error);
    }

    public interface VehicleListCallback {
        void onSuccess(List<Vehicle> vehicles);
        void onError(String error);
    }

    public interface SessionCallback {
        void onSuccess(Session session);
        void onError(String error);
    }

    public interface ExistsCallback {
        void onResult(boolean exists);
        void onError(String error);
    }

    public interface VehicleAdapterInterface {
        void onVehicleUpdated(Vehicle vehicle);
        void onVehicleDeleted(String vehicleId);
        void onError(String error);
    }

    public interface ErrorCallback {
        void onError(String error);
    }

    public interface ParkingSessionCallback {
        void onSessionCreated(String sessionId);
        void onSessionUpdated(Session session);
        void onError(String error);
    }

    public interface ActiveSessionCallback {
        void onSuccess(Session session);
        void onNoActiveSession();
        void onError(String error);
    }

    public interface UserDataCallback {
        void onSuccess(Usuario usuario);
        void onError(String error);
    }

    // Métodos stub - TODO: Implementar con MariaDB

    public void isEmailRegistered(String email, ExistsCallback callback) {
        Log.w(TAG, "isEmailRegistered() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void createUser(String uid, String email, String nombre, UserCallback callback) {
        Log.w(TAG, "createUser() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void getUserData(String uid, UserDataCallback callback) {
        Log.w(TAG, "getUserData() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void updateUser(String uid, Usuario usuario, SimpleCallback callback) {
        Log.w(TAG, "updateUser() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void addVehicle(String uid, Vehicle vehicle, VehicleCallback callback) {
        Log.w(TAG, "addVehicle() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void getVehicles(String uid, VehicleListCallback callback) {
        Log.w(TAG, "getVehicles() no implementado - requiere conexión a MariaDB");
        callback.onSuccess(new ArrayList<>());
    }

    public void updateVehicle(String uid, String vehicleId, Vehicle vehicle, SimpleCallback callback) {
        Log.w(TAG, "updateVehicle() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void deleteVehicle(String uid, String vehicleId, SimpleCallback callback) {
        Log.w(TAG, "deleteVehicle() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void createParkingSession(String uid, Session session, ParkingSessionCallback callback) {
        Log.w(TAG, "createParkingSession() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void getActiveSession(String uid, ActiveSessionCallback callback) {
        Log.w(TAG, "getActiveSession() no implementado - requiere conexión a MariaDB");
        callback.onNoActiveSession();
    }

    public void updateSession(String uid, String sessionId, Session session, SimpleCallback callback) {
        Log.w(TAG, "updateSession() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }

    public void endSession(String uid, String sessionId, SimpleCallback callback) {
        Log.w(TAG, "endSession() no implementado - requiere conexión a MariaDB");
        callback.onError("No implementado");
    }
}

