package com.example.lo_linking_park.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ApiClient - Cliente HTTP para conectar con API PHP en WAMP
 *
 * Configuración de URL según dispositivo:
 * - Emulador Android Studio: http://10.0.2.2/parking_api/
 * - Dispositivo Real: http://TU_IP_LOCAL/parking_api/ (ej: http://192.168.1.100/parking_api/)
 * - Genymotion: http://10.0.3.2/parking_api/
 */
public class ApiClient {
    private static final String TAG = "ApiClient";

    // ⚠️ CONFIGURACIÓN IMPORTANTE ⚠️
    // Cambiar según tu configuración:
    // - Para emulador: usa 10.0.2.2
    // - Para dispositivo real: usa tu IP local (obtener con 'ipconfig' en cmd)
    private static final String BASE_URL = "http://10.0.2.2/parking_api/";

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();

    /**
     * Callback para respuestas de API
     */
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    /**
     * Realiza un request POST
     *
     * @param endpoint Endpoint de la API (ej: "login.php")
     * @param data Datos a enviar en formato JSON
     * @param callback Callback para manejar respuesta
     */
    public static void post(String endpoint, JSONObject data, ApiCallback callback) {
        RequestBody body = RequestBody.create(
            data.toString(),
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build();

        executeRequest(request, callback);
    }

    /**
     * Realiza un request GET
     *
     * @param endpoint Endpoint de la API (ej: "salles.php")
     * @param callback Callback para manejar respuesta
     */
    public static void get(String endpoint, ApiCallback callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .get()
            .addHeader("Accept", "application/json")
            .build();

        executeRequest(request, callback);
    }

    /**
     * Realiza un request GET con parámetros
     *
     * @param endpoint Endpoint de la API
     * @param params Parámetros query string (ej: "?id=1&actiu=1")
     * @param callback Callback para manejar respuesta
     */
    public static void get(String endpoint, String params, ApiCallback callback) {
        String url = BASE_URL + endpoint;
        if (params != null && !params.isEmpty()) {
            url += (params.startsWith("?") ? "" : "?") + params;
        }

        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build();

        executeRequest(request, callback);
    }

    /**
     * Ejecuta el request y maneja la respuesta
     */
    private static void executeRequest(Request request, ApiCallback callback) {
        Log.d(TAG, "Request: " + request.method() + " " + request.url());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + request.url(), e);
                String errorMsg = "Error de conexión";

                // Mensajes de error más específicos
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "No se puede conectar al servidor. Verifica que WAMP esté activo.";
                    } else if (e.getMessage().contains("timeout")) {
                        errorMsg = "Tiempo de espera agotado. Verifica tu conexión.";
                    } else {
                        errorMsg = "Error: " + e.getMessage();
                    }
                }

                String finalError = errorMsg;
                runOnMainThread(() -> callback.onError(finalError));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Response (" + response.code() + "): " + responseBody);

                try {
                    JSONObject json = new JSONObject(responseBody);
                    runOnMainThread(() -> {
                        if (response.isSuccessful()) {
                            callback.onSuccess(json);
                        } else {
                            String error = json.optString("message", "Error del servidor (código: " + response.code() + ")");
                            callback.onError(error);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "JSON parse error: " + responseBody, e);
                    runOnMainThread(() -> callback.onError("Error al procesar respuesta del servidor"));
                }
            }
        });
    }

    /**
     * Ejecuta código en el hilo principal (UI thread)
     */
    private static void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * Obtiene la URL base configurada
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Verifica si la conexión con el servidor está disponible
     * Útil para testing
     */
    public static void testConnection(ApiCallback callback) {
        get("db.php", callback);
    }
}

