package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Salle;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalleRepository {
    private static final String TAG = "SalleRepository";
    private static SalleRepository instance;
    private FirebaseFirestore db;

    private SalleRepository() {
        db = FirebaseFirestore.getInstance();
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

    // Obtener todas las salles activas
    public void getAllActiveSalles(SalleListCallback callback) {
        db.collection("salles")
            .whereEqualTo("actiu", true)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Salle> salles = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Salle salle = document.toObject(Salle.class);
                    salle.setId(document.getId());
                    salles.add(salle);
                }
                Log.d(TAG, "Salles obtenidas: " + salles.size());
                callback.onSuccess(salles);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener salles", e);
                callback.onError(e.getMessage());
            });
    }

    // Obtener salle por ID
    public void getSalleById(String salleId, SalleDataCallback callback) {
        db.collection("salles").document(salleId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Salle salle = documentSnapshot.toObject(Salle.class);
                    if (salle != null) {
                        salle.setId(documentSnapshot.getId());
                        callback.onSuccess(salle);
                    } else {
                        callback.onError("Error al convertir datos");
                    }
                } else {
                    callback.onError("Salle no encontrada");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener salle", e);
                callback.onError(e.getMessage());
            });
    }

    // Actualizar plazas disponibles (incrementar cuando termina sesión)
    public void incrementPlacesDisponibles(String salleId, SalleCallback callback) {
        db.collection("salles").document(salleId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Salle salle = documentSnapshot.toObject(Salle.class);
                    if (salle != null) {
                        int placesActuales = salle.getPlacesDisponibles();
                        int placesTotals = salle.getPlacesTotals();

                        if (placesActuales < placesTotals) {
                            db.collection("salles").document(salleId)
                                .update("placesDisponibles", placesActuales + 1,
                                       "actualitzatEl", new Date())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Plazas incrementadas en salle: " + salleId);
                                    callback.onSuccess(salleId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al incrementar plazas", e);
                                    callback.onError(e.getMessage());
                                });
                        } else {
                            callback.onSuccess(salleId); // Ya está al máximo
                        }
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener salle", e);
                callback.onError(e.getMessage());
            });
    }

    // Decrementar plazas disponibles (cuando inicia sesión)
    public void decrementPlacesDisponibles(String salleId, SalleCallback callback) {
        db.collection("salles").document(salleId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Salle salle = documentSnapshot.toObject(Salle.class);
                    if (salle != null) {
                        int placesActuales = salle.getPlacesDisponibles();

                        if (placesActuales > 0) {
                            db.collection("salles").document(salleId)
                                .update("placesDisponibles", placesActuales - 1,
                                       "actualitzatEl", new Date())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Plazas decrementadas en salle: " + salleId);
                                    callback.onSuccess(salleId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al decrementar plazas", e);
                                    callback.onError(e.getMessage());
                                });
                        } else {
                            callback.onError("No hay plazas disponibles");
                        }
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener salle", e);
                callback.onError(e.getMessage());
            });
    }

    // Verificar si hay plazas disponibles
    public void checkPlacesDisponibles(String salleId, CheckPlacesCallback callback) {
        db.collection("salles").document(salleId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Salle salle = documentSnapshot.toObject(Salle.class);
                    if (salle != null) {
                        callback.onResult(salle.getPlacesDisponibles() > 0);
                    } else {
                        callback.onResult(false);
                    }
                } else {
                    callback.onResult(false);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al verificar plazas", e);
                callback.onResult(false);
            });
    }

    // Callback auxiliar
    public interface CheckPlacesCallback {
        void onResult(boolean hasPlaces);
    }
}
