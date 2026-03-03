package com.example.lo_linking_park.repository;

import com.example.lo_linking_park.network.ApiClient;
import org.json.JSONObject;

public class AuthRepository {
    private static AuthRepository instance;
    private final ApiClient api;

    private AuthRepository() { api = ApiClient.getInstance(); }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) instance = new AuthRepository();
        return instance;
    }

    public interface AuthCallback {
        void onSuccess(int userId, String nom, String email);
        void onError(String error);
    }

    public void login(String email, String password, AuthCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            api.post("login.php", body, new ApiClient.ApiCallback() {
                @Override public void onSuccess(JSONObject r) {
                    try {
                        if (r.getBoolean("success")) {
                            JSONObject u = r.getJSONObject("user");
                            callback.onSuccess(u.getInt("id"), u.getString("nom"), u.getString("email"));
                        } else { callback.onError(r.getString("message")); }
                    } catch (Exception e) { callback.onError("Error de resposta"); }
                }
                @Override public void onError(String e) { callback.onError("Error de xarxa: " + e); }
            });
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }

    public void register(String nom, String cognoms, String email, String telefon, String password, AuthCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("nom", nom); body.put("cognoms", cognoms);
            body.put("email", email); body.put("telefon", telefon);
            body.put("password", password);
            api.post("register.php", body, new ApiClient.ApiCallback() {
                @Override public void onSuccess(JSONObject r) {
                    try {
                        if (r.getBoolean("success")) {
                            JSONObject u = r.getJSONObject("user");
                            callback.onSuccess(u.getInt("id"), u.getString("nom"), u.getString("email"));
                        } else { callback.onError(r.getString("message")); }
                    } catch (Exception e) { callback.onError("Error de resposta"); }
                }
                @Override public void onError(String e) { callback.onError("Error de xarxa: " + e); }
            });
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }
}