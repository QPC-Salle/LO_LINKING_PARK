package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class FirebaseAuthRepository {
    private static final String TAG = "FirebaseAuthRepository";
    private static FirebaseAuthRepository instance;
    private FirebaseAuth mAuth;
    private RealtimeDatabaseManager rtdb;

    private FirebaseAuthRepository() {
        mAuth = FirebaseAuth.getInstance();
        rtdb = RealtimeDatabaseManager.getInstance();
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
        rtdb.isEmailRegistered(email, new RealtimeDatabaseManager.ExistsCallback() {
            @Override
            public void onResult(boolean exists) {
                if (exists) {
                    callback.onError("El email ya está registrado");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                usuario.setId(userId);
                                usuario.setEmail(email);

                                rtdb.saveUserToRealtimeDatabase(usuario, new RealtimeDatabaseManager.UserCallback() {
                                    @Override
                                    public void onSuccess(String userId) {
                                        Log.d(TAG, "Usuario creado en RTDB: " + userId);
                                        callback.onSuccess(userId);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "Error al guardar usuario en RTDB", new RuntimeException(error));
                                        callback.onError("Error al guardar datos: " + error);
                                    }
                                });
                            } else {
                                callback.onError("No se pudo obtener el usuario autenticado");
                            }
                        } else {
                            String error = task.getException() != null ?
                                task.getException().getMessage() : "Error desconocido";
                            Log.e(TAG, "Error al registrar usuario", task.getException());
                            callback.onError(error);
                        }
                    });
            }

            @Override
            public void onError(String error) {
                callback.onError("Error al validar email: " + error);
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
        rtdb.getUsuario(userId, new RealtimeDatabaseManager.UserDataCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                callback.onSuccess(usuario);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Actualizar datos del usuario
    public void updateUserData(String userId, Map<String, Object> updates, AuthCallback callback) {
        rtdb.updateUsuarioFields(userId, updates, new RealtimeDatabaseManager.UserCallback() {
            @Override
            public void onSuccess(String uid) {
                Log.d(TAG, "Usuario actualizado: " + uid);
                callback.onSuccess(uid);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error al actualizar usuario", new RuntimeException(error));
                callback.onError(error);
            }
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
