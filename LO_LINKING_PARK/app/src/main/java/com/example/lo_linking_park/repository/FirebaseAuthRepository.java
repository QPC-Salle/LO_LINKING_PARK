package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthRepository {
    private static final String TAG = "FirebaseAuthRepository";
    private static FirebaseAuthRepository instance;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseAuthRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseAuthRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthRepository();
        }
        return instance;
    }

    public interface AuthCallback {
        void onSuccess(String userId);
        void onError(String error);
    }

    // Registro de usuario
    public void registerUser(String email, String password, Usuario usuario, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();
                        usuario.setId(userId);
                        usuario.setEmail(email);

                        // Guardar datos del usuario en Firestore
                        db.collection("users").document(userId)
                            .set(usuario)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Usuario creado en Firestore: " + userId);
                                callback.onSuccess(userId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al crear usuario en Firestore", e);
                                callback.onError("Error al guardar datos: " + e.getMessage());
                            });
                    }
                } else {
                    String error = task.getException() != null ?
                        task.getException().getMessage() : "Error desconocido";
                    Log.e(TAG, "Error al registrar usuario", task.getException());
                    callback.onError(error);
                }
            });
    }

    // Login de usuario
    public void loginUser(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Log.d(TAG, "Login exitoso: " + user.getUid());
                        callback.onSuccess(user.getUid());
                    }
                } else {
                    String error = task.getException() != null ?
                        task.getException().getMessage() : "Error desconocido";
                    Log.e(TAG, "Error al iniciar sesión", task.getException());
                    callback.onError(error);
                }
            });
    }

    // Cerrar sesión
    public void logout() {
        mAuth.signOut();
        Log.d(TAG, "Usuario cerró sesión");
    }

    // Obtener usuario actual
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Verificar si hay usuario autenticado
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    // Obtener ID del usuario actual
    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Obtener datos del usuario desde Firestore
    public void getUserData(String userId, UserDataCallback callback) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        usuario.setId(documentSnapshot.getId());
                        callback.onSuccess(usuario);
                    } else {
                        callback.onError("Error al convertir datos");
                    }
                } else {
                    callback.onError("Usuario no encontrado");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener datos del usuario", e);
                callback.onError(e.getMessage());
            });
    }

    // Actualizar datos del usuario
    public void updateUserData(String userId, Map<String, Object> updates, AuthCallback callback) {
        updates.put("actualitzatEl", new Date());

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Usuario actualizado: " + userId);
                callback.onSuccess(userId);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al actualizar usuario", e);
                callback.onError(e.getMessage());
            });
    }

    // Cambiar contraseña
    public void changePassword(String newPassword, AuthCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Contraseña actualizada");
                        callback.onSuccess(user.getUid());
                    } else {
                        String error = task.getException() != null ?
                            task.getException().getMessage() : "Error desconocido";
                        callback.onError(error);
                    }
                });
        } else {
            callback.onError("No hay usuario autenticado");
        }
    }

    // Callback para obtener datos de usuario
    public interface UserDataCallback {
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
}
