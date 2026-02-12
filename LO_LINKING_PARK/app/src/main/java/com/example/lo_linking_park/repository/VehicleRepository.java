package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Vehicle;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VehicleRepository {
    private static final String TAG = "VehicleRepository";
    private static final int MAX_VEHICLES_PER_USER = 5;
    private static VehicleRepository instance;
    private FirebaseFirestore db;

    private VehicleRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized VehicleRepository getInstance() {
        if (instance == null) {
            instance = new VehicleRepository();
        }
        return instance;
    }

    public interface VehicleCallback {
        void onSuccess(String vehicleId);
        void onError(String error);
    }

    public interface VehicleListCallback {
        void onSuccess(List<Vehicle> vehicles);
        void onError(String error);
    }

    // Añadir vehículo
    public void addVehicle(Vehicle vehicle, VehicleCallback callback) {
        // Primero verificar que el usuario no tenga más de 5 vehículos activos
        checkMaxVehicles(vehicle.getUsuariId(), canAdd -> {
            if (canAdd) {
                // Verificar que la matrícula no exista
                checkMatriculaExists(vehicle.getMatricula(), exists -> {
                    if (!exists) {
                        vehicle.setCreatEl(new Date());
                        vehicle.setActualitzatEl(new Date());

                        db.collection("vehicles")
                            .add(vehicle)
                            .addOnSuccessListener(documentReference -> {
                                String vehicleId = documentReference.getId();
                                Log.d(TAG, "Vehículo añadido: " + vehicleId);
                                callback.onSuccess(vehicleId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al añadir vehículo", e);
                                callback.onError(e.getMessage());
                            });
                    } else {
                        callback.onError("La matrícula ya existe");
                    }
                });
            } else {
                callback.onError("Un usuario no puede tener más de " + MAX_VEHICLES_PER_USER + " vehículos activos");
            }
        });
    }

    // Verificar máximo de vehículos por usuario
    private void checkMaxVehicles(String usuariId, CheckCallback callback) {
        db.collection("vehicles")
            .whereEqualTo("usuariId", usuariId)
            .whereEqualTo("actiu", true)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int count = queryDocumentSnapshots.size();
                callback.onResult(count < MAX_VEHICLES_PER_USER);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al verificar máximo de vehículos", e);
                callback.onResult(false);
            });
    }

    // Verificar si matrícula existe
    private void checkMatriculaExists(String matricula, CheckCallback callback) {
        db.collection("vehicles")
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                callback.onResult(!queryDocumentSnapshots.isEmpty());
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al verificar matrícula", e);
                callback.onResult(false);
            });
    }

    // Obtener vehículos de un usuario
    public void getUserVehicles(String usuariId, VehicleListCallback callback) {
        db.collection("vehicles")
            .whereEqualTo("usuariId", usuariId)
            .whereEqualTo("actiu", true)
            .orderBy("predeterminat", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Vehicle> vehicles = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    vehicle.setId(document.getId());
                    vehicles.add(vehicle);
                }
                Log.d(TAG, "Vehículos obtenidos: " + vehicles.size());
                callback.onSuccess(vehicles);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener vehículos", e);
                callback.onError(e.getMessage());
            });
    }

    // Obtener vehículo predeterminado
    public void getDefaultVehicle(String usuariId, VehicleCallback callback) {
        db.collection("vehicles")
            .whereEqualTo("usuariId", usuariId)
            .whereEqualTo("predeterminat", true)
            .whereEqualTo("actiu", true)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    String vehicleId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    callback.onSuccess(vehicleId);
                } else {
                    callback.onError("No hay vehículo predeterminado");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener vehículo predeterminado", e);
                callback.onError(e.getMessage());
            });
    }

    // Establecer vehículo como predeterminado
    public void setDefaultVehicle(String usuariId, String vehicleId, VehicleCallback callback) {
        // Primero quitar predeterminado de todos los vehículos del usuario
        db.collection("vehicles")
            .whereEqualTo("usuariId", usuariId)
            .whereEqualTo("predeterminat", true)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    db.collection("vehicles").document(document.getId())
                        .update("predeterminat", false);
                }

                // Ahora establecer el nuevo predeterminado
                db.collection("vehicles").document(vehicleId)
                    .update("predeterminat", true, "actualitzatEl", new Date())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Vehículo predeterminado establecido: " + vehicleId);
                        callback.onSuccess(vehicleId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al establecer vehículo predeterminado", e);
                        callback.onError(e.getMessage());
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al quitar vehículo predeterminado anterior", e);
                callback.onError(e.getMessage());
            });
    }

    // Actualizar vehículo
    public void updateVehicle(String vehicleId, Vehicle vehicle, VehicleCallback callback) {
        vehicle.setActualitzatEl(new Date());

        db.collection("vehicles").document(vehicleId)
            .set(vehicle)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Vehículo actualizado: " + vehicleId);
                callback.onSuccess(vehicleId);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al actualizar vehículo", e);
                callback.onError(e.getMessage());
            });
    }

    // Desactivar vehículo (borrado lógico)
    public void deactivateVehicle(String vehicleId, VehicleCallback callback) {
        db.collection("vehicles").document(vehicleId)
            .update("actiu", false, "actualitzatEl", new Date())
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Vehículo desactivado: " + vehicleId);
                callback.onSuccess(vehicleId);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al desactivar vehículo", e);
                callback.onError(e.getMessage());
            });
    }

    // Callback auxiliar
    private interface CheckCallback {
        void onResult(boolean result);
    }
}
