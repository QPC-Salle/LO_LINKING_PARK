package com.example.lo_linking_park.repository;

import android.util.Log;

import com.example.lo_linking_park.model.Session;
import com.example.lo_linking_park.model.Usuario;
import com.example.lo_linking_park.model.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealtimeDatabaseManager {
    private static final String TAG = "RealtimeDatabaseManager";
    private static RealtimeDatabaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference rootRef;

    private RealtimeDatabaseManager() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
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

    // Helpers
    private FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    private String getCurrentUid() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Verifica si un email ya existe en /users
    public void isEmailRegistered(String email, ExistsCallback callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError("Email inválido");
            return;
        }

        rootRef.child("users")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    callback.onResult(snapshot.exists());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    callback.onError(error.getMessage());
                }
            });
    }

    // 1) createUser: escribe los datos del usuario en Realtime Database bajo /users/{uid}
    public void createUser(Usuario usuario, UserCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }
        usuario.setId(uid);
        DatabaseReference userRef = rootRef.child("users").child(uid);
        userRef.setValue(usuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Usuario guardado en RTDB: " + uid);
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Error al guardar usuario en RTDB", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // 2) addVehicle: añade un vehículo usando push() para generar id
    public void addVehicle(Vehicle vehicle, VehicleCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }
        vehicle.setUsuariId(uid);

        DatabaseReference vehiclesRef = rootRef.child("vehicles");
        DatabaseReference newVehicleRef = vehiclesRef.push();
        String key = newVehicleRef.getKey();
        vehicle.setId(key);

        newVehicleRef.setValue(vehicle)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Vehículo añadido en RTDB: " + key);
                    callback.onSuccess(vehicle);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Error al añadir vehículo en RTDB", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // 3) getVehicles: obtiene los vehículos del usuario autenticado
    public void getVehicles(VehicleListCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }

        DatabaseReference vehiclesRef = rootRef.child("vehicles");
        vehiclesRef.orderByChild("usuariId").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Vehicle> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Vehicle v = child.getValue(Vehicle.class);
                            if (v != null) {
                                // Asegurar id
                                if (v.getId() == null) v.setId(child.getKey());
                                list.add(v);
                            }
                        }
                        callback.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error al obtener vehículos", error.toException());
                        callback.onError(error.getMessage());
                    }
                });
    }

    // 4) createParkingSession: crea una sesión de parking usando push()
    public void createParkingSession(Session session, SessionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }
        session.setUsuariId(uid);
        DatabaseReference sessionsRef = rootRef.child("sessions");
        DatabaseReference newSessionRef = sessionsRef.push();
        String key = newSessionRef.getKey();
        session.setId(key);

        newSessionRef.setValue(session)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Sesión creada en RTDB: " + key);
                    callback.onSuccess(session);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Error al crear sesión en RTDB", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // 5) finishParkingSession: marca la sesión como finalizada (dataFi, estat)
    public void finishParkingSession(String sessionId, String tipusFinalitzacio, SessionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }
        if (sessionId == null || sessionId.isEmpty()) {
            callback.onError("sessionId inválido");
            return;
        }

        DatabaseReference sessionRef = rootRef.child("sessions").child(sessionId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("dataFi", System.currentTimeMillis());
        updates.put("estat", "finalitzat");
        updates.put("tipusFinalitzacio", tipusFinalitzacio != null ? tipusFinalitzacio : "manual");
        updates.put("actualitzatEl", System.currentTimeMillis());

        sessionRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    // Leer la sesión actualizada y devolverla
                    sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Session s = snapshot.getValue(Session.class);
                            if (s != null) {
                                s.setId(snapshot.getKey());
                                callback.onSuccess(s);
                            } else {
                                callback.onError("No se pudo leer la sesión tras actualizar");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            callback.onError(error.getMessage());
                        }
                    });
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Error al finalizar sesión", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // PROMPT 3: Guardar usuario en RTDB después de createUserWithEmailAndPassword
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * PROMPT 3: Después de FirebaseAuth.createUserWithEmailAndPassword() exitoso,
     * guardar los datos del usuario en Realtime Database bajo: users/{uid}
     *
     * @param usuario Usuario object con datos (nom, cognoms, email, telefon, etc.)
     * @param callback Callback para notificar éxito/error
     */
    public void saveUserToRealtimeDatabase(Usuario usuario, UserCallback callback) {
        String uid = getCurrentUid();

        if (uid == null) {
            Log.e(TAG, "No hay usuario autenticado");
            callback.onError("No hay usuario autenticado");
            return;
        }

        usuario.setId(uid);
        usuario.setCreatEl(System.currentTimeMillis());
        usuario.setActualitzatEl(System.currentTimeMillis());

        DatabaseReference userRef = rootRef.child("users").child(uid);

        userRef.setValue(usuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Usuario guardado en RTDB: " + uid);
                    Log.d(TAG, "✓ Ruta: users/" + uid);
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al guardar usuario en RTDB", exception);
                    callback.onError("Error: " + exception.getMessage());
                });
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // PROMPT 4: Leer vehículos con ValueEventListener y actualizar RecyclerView
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * PROMPT 4: Lee vehículos desde vehicles/{uid} con ValueEventListener real-time
     * Convierte DataSnapshot a Vehicle objects y actualiza RecyclerView adapter
     *
     * @param uid UID del usuario
     * @param adapter Adapter que implementa VehicleAdapterInterface
     * @param callback Callback para notificar errores
     * @return ValueEventListener para poder remover después en onDestroy
     */
    public ValueEventListener loadVehiclesWithRealTimeListener(
            String uid,
            VehicleAdapterInterface adapter,
            ErrorCallback callback) {

        if (uid == null) {
            callback.onError("UID inválido");
            return null;
        }

        DatabaseReference vehiclesRef = rootRef.child("vehicles");

        ValueEventListener vehicleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Vehicle> vehicleList = new ArrayList<>();

                for (DataSnapshot vehicleSnapshot : snapshot.getChildren()) {
                    try {
                        Vehicle vehicle = vehicleSnapshot.getValue(Vehicle.class);
                        if (vehicle != null) {
                            if (vehicle.getId() == null) {
                                vehicle.setId(vehicleSnapshot.getKey());
                            }

                            // Filtrar solo vehículos del usuario actual
                            if (vehicle.getUsuariId() != null && vehicle.getUsuariId().equals(uid)) {
                                vehicleList.add(vehicle);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al convertir Vehicle", e);
                    }
                }

                Log.d(TAG, "✓ Vehículos en tiempo real: " + vehicleList.size());
                adapter.updateVehicles(vehicleList); // Actualizar RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "✗ Error al escuchar vehículos", error.toException());
                callback.onError(error.getMessage());
            }
        };

        // Agregar listener con query ordenado
        vehiclesRef.orderByChild("usuariId").equalTo(uid)
                .addValueEventListener(vehicleListener);

        Log.d(TAG, "✓ Listener ACTIVO para vehículos del usuario: " + uid);
        return vehicleListener;
    }

    /**
     * Remover listener de vehículos (llamar en onDestroy para evitar memory leaks)
     */
    public void stopListeningForVehicles(String uid, ValueEventListener listener) {
        if (listener != null && uid != null) {
            DatabaseReference vehiclesRef = rootRef.child("vehicles");
            vehiclesRef.orderByChild("usuariId").equalTo(uid)
                    .removeEventListener(listener);
            Log.d(TAG, "✓ Listener de vehículos removido");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // PROMPT 5: Crear sesión de parking con cálculo de precio
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * PROMPT 5: Calcula endTime desde durationMinutes,
     * calcula price basado en duración,
     * y guarda ParkingSession en: parkingSessions/{uid}/{sessionId}
     *
     * @param vehicleId ID del vehículo
     * @param salleId ID de la salle/parking
     * @param durationMinutes Duración en minutos
     * @param tarifaPerHora Tarifa por hora (en €)
     * @param latitudeStart Latitud de inicio
     * @param longitudeStart Longitud de inicio
     * @param callback Callback que retorna sesión y precio
     */
    public void createParkingSessionWithPrice(
            String vehicleId,
            String salleId,
            int durationMinutes,
            double tarifaPerHora,
            double latitudeStart,
            double longitudeStart,
            ParkingSessionCallback callback) {

        String uid = getCurrentUid();

        if (uid == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        // ═ CALCULO DE TIMESTAMPS
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (durationMinutes * 60 * 1000L);

        // ═ CALCULO DE PRECIO
        double hours = durationMinutes / 60.0;
        double totalPrice = hours * tarifaPerHora;

        // ═ CREAR SESSION
        Session session = new Session();
        session.setUsuariId(uid);
        session.setVehicleId(vehicleId);
        session.setSalleId(salleId);
        session.setDataInici(startTime);
        session.setDataFi(endTime);
        session.setTempsMaximMinuts(durationMinutes);
        session.setAvisoTempsMinuts(15);
        session.setLatitudInici(latitudeStart);
        session.setLongitudInici(longitudeStart);
        session.setEstat("ACTIVE");
        session.setAvisoEnviat(false);
        session.setAvisoFinalEnviat(false);
        session.setCreatEl(startTime);
        session.setActualitzatEl(startTime);
        session.setTipusFinalitzacio("MANUAL");

        // ═ GENERAR ID CON PUSH()
        DatabaseReference sessionsRef = rootRef.child("parkingSessions").child(uid);
        String sessionId = sessionsRef.push().getKey();

        if (sessionId == null) {
            callback.onError("Error al generar ID");
            return;
        }

        session.setId(sessionId);

        // ═ GUARDAR EN RTDB
        sessionsRef.child(sessionId).setValue(session)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Sesión creada: " + sessionId);
                    Log.d(TAG, "✓ Ruta: parkingSessions/" + uid + "/" + sessionId);
                    Log.d(TAG, "✓ Duración: " + durationMinutes + " min = €" + String.format("%.2f", totalPrice));
                    callback.onSuccess(session, totalPrice);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al crear sesión", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // PROMPT 6: Listener real-time para sesiones ACTIVE
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * PROMPT 6: Crea un listener real-time que escucha parkingSessions/{uid}
     * donde status == "ACTIVE" y actualiza la UI cuando la sesión cambia
     *
     * @param uid UID del usuario
     * @param callback Callback para recibir actualizaciones
     * @return ValueEventListener para poder remover después en onDestroy
     */
    public ValueEventListener listenForActiveParkingSession(
            String uid,
            ActiveSessionCallback callback) {

        if (uid == null) {
            callback.onError("UID inválido");
            return null;
        }

        DatabaseReference userSessionsRef = rootRef.child("parkingSessions").child(uid);

        ValueEventListener sessionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Session activeSession = null;

                // Buscar primera sesión con status ACTIVE
                for (DataSnapshot sessionSnapshot : snapshot.getChildren()) {
                    try {
                        Session session = sessionSnapshot.getValue(Session.class);
                        if (session != null && "ACTIVE".equals(session.getEstat())) {
                            if (session.getId() == null) {
                                session.setId(sessionSnapshot.getKey());
                            }
                            activeSession = session;
                            break;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al convertir Session", e);
                    }
                }

                if (activeSession != null) {
                    long remaining = calculateRemainingTime(activeSession);
                    Log.d(TAG, "✓ Sesión ACTIVE detectada: " + activeSession.getId());
                    Log.d(TAG, "✓ Tiempo restante: " + remaining + " minutos");
                    callback.onSessionUpdate(activeSession);
                } else {
                    Log.d(TAG, "✓ No hay sesión ACTIVE");
                    callback.onNoActiveSession();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "✗ Error escuchando sesiones", error.toException());
                callback.onError(error.getMessage());
            }
        };

        userSessionsRef.addValueEventListener(sessionListener);

        Log.d(TAG, "✓ Listener ACTIVO para sesiones ACTIVE de: " + uid);
        return sessionListener;
    }

    /**
     * Remover listener de sesión activa (llamar en onDestroy)
     */
    public void stopListeningForActiveSession(String uid, ValueEventListener listener) {
        if (listener != null && uid != null) {
            DatabaseReference userSessionsRef = rootRef.child("parkingSessions").child(uid);
            userSessionsRef.removeEventListener(listener);
            Log.d(TAG, "✓ Listener de sesión activa removido");
        }
    }

    /**
     * Método auxiliar: Calcular tiempo restante en minutos
     */
    private long calculateRemainingTime(Session session) {
        long now = System.currentTimeMillis();
        long endTime = session.getDataFi();
        long remainingMillis = endTime - now;
        return remainingMillis <= 0 ? 0 : remainingMillis / (60 * 1000);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // INTERFACES DE CALLBACK
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * Interfaz para adapters de RecyclerView con vehículos
     */
    public interface VehicleAdapterInterface {
        void updateVehicles(List<Vehicle> vehicles);
    }

    /**
     * Interfaz de callback para errores
     */
    public interface ErrorCallback {
        void onError(String error);
    }

    /**
     * Interfaz de callback para sesiones de parking con precio
     */
    public interface ParkingSessionCallback {
        void onSuccess(Session session, double price);
        void onError(String error);
    }

    /**
     * Interfaz de callback para sesiones activas
     */
    public interface ActiveSessionCallback {
        void onSessionUpdate(Session session);
        void onNoActiveSession();
        void onError(String error);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // FUNCIONES CRUD ADICIONALES
    // ═══════════════════════════════════════════════════════════════════════════════

    // ✅ READ: Obtener un usuario específico
    public void getUsuario(String uid, UserDataCallback callback) {
        DatabaseReference userRef = rootRef.child("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        usuario.setId(snapshot.getKey());
                        callback.onSuccess(usuario);
                    } else {
                        callback.onError("Error al convertir usuario");
                    }
                } else {
                    callback.onError("Usuario no encontrado");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al obtener usuario", error.toException());
                callback.onError(error.getMessage());
            }
        });
    }

    // ✅ READ: Obtener un vehículo específico
    public void getVehicle(String vehicleId, VehicleCallback callback) {
        DatabaseReference vehicleRef = rootRef.child("vehicles").child(vehicleId);
        vehicleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Vehicle vehicle = snapshot.getValue(Vehicle.class);
                    if (vehicle != null) {
                        vehicle.setId(snapshot.getKey());
                        callback.onSuccess(vehicle);
                    } else {
                        callback.onError("Error al convertir vehículo");
                    }
                } else {
                    callback.onError("Vehículo no encontrado");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al obtener vehículo", error.toException());
                callback.onError(error.getMessage());
            }
        });
    }

    // ✅ READ: Obtener una sesión específica
    public void getParkingSession(String sessionId, SessionCallback callback) {
        DatabaseReference sessionRef = rootRef.child("parkingSessions").child(getCurrentUid()).child(sessionId);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Session session = snapshot.getValue(Session.class);
                    if (session != null) {
                        session.setId(snapshot.getKey());
                        callback.onSuccess(session);
                    } else {
                        callback.onError("Error al convertir sesión");
                    }
                } else {
                    callback.onError("Sesión no encontrada");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al obtener sesión", error.toException());
                callback.onError(error.getMessage());
            }
        });
    }

    // ✅ UPDATE: Actualizar datos del usuario
    public void updateUsuario(String uid, Usuario usuario, UserCallback callback) {
        usuario.setId(uid);
        usuario.setActualitzatEl(System.currentTimeMillis());

        DatabaseReference userRef = rootRef.child("users").child(uid);
        userRef.setValue(usuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Usuario actualizado: " + uid);
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al actualizar usuario", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ UPDATE: Actualizar campos del usuario
    public void updateUsuarioFields(String uid, Map<String, Object> updates, UserCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onError("UID inválido");
            return;
        }

        if (updates == null || updates.isEmpty()) {
            callback.onError("No hay cambios para guardar");
            return;
        }

        updates.put("actualitzatEl", System.currentTimeMillis());

        DatabaseReference userRef = rootRef.child("users").child(uid);
        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Usuario actualizado (fields): " + uid);
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al actualizar usuario (fields)", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ UPDATE: Actualizar datos del vehículo
    public void updateVehicle(String vehicleId, Vehicle vehicle, VehicleCallback callback) {
        vehicle.setId(vehicleId);
        vehicle.setActualitzatEl(System.currentTimeMillis());

        DatabaseReference vehicleRef = rootRef.child("vehicles").child(vehicleId);
        vehicleRef.setValue(vehicle)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Vehículo actualizado: " + vehicleId);
                    callback.onSuccess(vehicle);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al actualizar vehículo", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ UPDATE: Cancelar sesión de parking
    public void cancelParkingSession(String sessionId, SessionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }

        DatabaseReference sessionRef = rootRef.child("parkingSessions").child(uid).child(sessionId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("estat", "cancel·lat");
        updates.put("actualitzatEl", System.currentTimeMillis());

        sessionRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Sesión cancelada: " + sessionId);
                    sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Session session = snapshot.getValue(Session.class);
                            if (session != null) {
                                session.setId(snapshot.getKey());
                                callback.onSuccess(session);
                            } else {
                                callback.onError("No se pudo leer la sesión");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            callback.onError(error.getMessage());
                        }
                    });
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al cancelar sesión", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ DELETE: Eliminar usuario
    public void deleteUsuario(String uid, SimpleCallback callback) {
        DatabaseReference userRef = rootRef.child("users").child(uid);
        userRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Usuario eliminado: " + uid);
                    callback.onSuccess();
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al eliminar usuario", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ DELETE: Eliminar vehículo
    public void deleteVehicle(String vehicleId, SimpleCallback callback) {
        DatabaseReference vehicleRef = rootRef.child("vehicles").child(vehicleId);
        vehicleRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Vehículo eliminado: " + vehicleId);
                    callback.onSuccess();
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al eliminar vehículo", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ DELETE: Eliminar sesión de parking
    public void deleteParkingSession(String sessionId, SimpleCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("No hay usuario autenticado");
            return;
        }

        DatabaseReference sessionRef = rootRef.child("parkingSessions").child(uid).child(sessionId);
        sessionRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ Sesión eliminada: " + sessionId);
                    callback.onSuccess();
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "✗ Error al eliminar sesión", exception);
                    callback.onError(exception.getMessage());
                });
    }

    // ✅ Interfaz de callback para obtener datos de usuario
    public interface UserDataCallback {
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
}
