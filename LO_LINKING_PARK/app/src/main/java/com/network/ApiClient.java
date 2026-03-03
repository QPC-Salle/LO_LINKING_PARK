package com.example.lo_linking_park.network;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class ApiClient {
    // 10.0.2.2 = localhost per a l'emulador Android
    // Canviar per la IP del PC si s'usa un dispositiu físic (ex: 192.168.1.X)
    public static final String BASE_URL = "http://10.0.2.2/parking_api/";
    public static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private static ApiClient instance;
    private final OkHttpClient client;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private ApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        executor = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public void post(String endpoint, JSONObject body, ApiCallback callback) {
        executor.execute(() -> {
            RequestBody requestBody = RequestBody.create(body.toString(), JSON_TYPE);
            Request request = new Request.Builder()
                    .url(BASE_URL + endpoint)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String rb = response.body() != null ? response.body().string() : "{}";
                JSONObject json = new JSONObject(rb);
                mainHandler.post(() -> callback.onSuccess(json));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void get(String endpoint, ApiCallback callback) {
        executor.execute(() -> {
            Request request = new Request.Builder()
                    .url(BASE_URL + endpoint)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String rb = response.body() != null ? response.body().string() : "{}";
                JSONObject json = new JSONObject(rb);
                mainHandler.post(() -> callback.onSuccess(json));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}