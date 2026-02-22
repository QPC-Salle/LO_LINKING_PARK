package com.example.lo_linking_park.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Utilidad para validar la conexión con Firebase Realtime Database
 * Verifica que la app está conectada correctamente a la base de datos
 */
public class FirebaseConnectionValidator {
    private static final String TAG = "FirebaseValidator";

    /**
     * Valida la conexión a Firebase Realtime Database
     * Escucha el estado de conexión en .info/connected
     */
    public static void validateConnection() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference connectedRef = database.getReference(".info/connected");

            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        boolean connected = snapshot.getValue(Boolean.class) != null &&
                                          snapshot.getValue(Boolean.class);
                        if (connected) {
                            Log.i(TAG, "✅ CONECTADO a Firebase Realtime Database");
                            Log.i(TAG, "✅ Proyecto: lo-linking-park");
                            Log.i(TAG, "✅ Timestamp: " + System.currentTimeMillis());
                        } else {
                            Log.w(TAG, "⚠️ DESCONECTADO de Firebase Realtime Database");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error al parsear estado de conexión: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "❌ Error de conexión: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Excepción en validateConnection: " + e.getMessage());
        }
    }

    /**
     * Escribe datos de prueba en la base de datos para verificar que funciona
     * Crea un nodo "test" con timestamp actual
     */
    public static void writeTestData() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference testRef = database.getReference("test");

            String testValue = "Conectado desde app a las " + System.currentTimeMillis();

            testRef.setValue(testValue)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "✅ Datos de prueba guardados exitosamente");
                        Log.i(TAG, "✅ Valor: " + testValue);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Error al guardar datos de prueba: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "❌ Excepción en writeTestData: " + e.getMessage());
        }
    }

    /**
     * Lee los datos de prueba para verificar que la lectura funciona
     */
    public static void readTestData() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference testRef = database.getReference("test");

            testRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String value = snapshot.getValue(String.class);
                        if (value != null) {
                            Log.i(TAG, "✅ Datos de prueba leídos exitosamente");
                            Log.i(TAG, "✅ Valor: " + value);
                        } else {
                            Log.w(TAG, "⚠️ No hay datos de prueba aún");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error al parsear datos de prueba: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "❌ Error al leer datos de prueba: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Excepción en readTestData: " + e.getMessage());
        }
    }

    /**
     * Test completo: valida conexión, escribe y lee datos
     */
    public static void runFullTest() {
        Log.i(TAG, "╔════════════════════════════════════════════╗");
        Log.i(TAG, "║  INICIANDO TEST DE FIREBASE REALTIME DB   ║");
        Log.i(TAG, "╚════════════════════════════════════════════╝");

        // Paso 1: Validar conexión
        Log.i(TAG, "Paso 1: Validando conexión...");
        validateConnection();

        // Paso 2: Escribir datos
        Log.i(TAG, "Paso 2: Escribiendo datos de prueba...");
        writeTestData();

        // Paso 3: Leer datos (con delay para permitir que se escriban primero)
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Log.i(TAG, "Paso 3: Leyendo datos de prueba...");
            readTestData();
        }, 1000);

        Log.i(TAG, "╔════════════════════════════════════════════╗");
        Log.i(TAG, "║  TEST INICIADO - Ver Logcat para resultados║");
        Log.i(TAG, "╚════════════════════════════════════════════╝");
    }
}
