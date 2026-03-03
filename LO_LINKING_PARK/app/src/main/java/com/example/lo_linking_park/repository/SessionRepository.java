package com.example.lo_linking_park.repository;

import com.example.lo_linking_park.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private static SessionRepository instance;
    private final ApiClient api;

    private SessionRepository() { api = ApiClient.getInstance(); }

    public static synchronized SessionRepository getInstance() {
        if (instance == null) instance = new SessionRepository();
        return instance;
    }

    public interface SessionListCallback {
        void onSuccess(List<JSONObject> sessions);
        void onError(String error);
    }

    public interface SessionCallback {
        void onSuccess(int sessionId);
        void onError(String error);
    }

    public void getSessionsByUser(int userId, SessionListCallback callback) {
        api.get("sessions.php?user_id=" + userId, new ApiClient.ApiCallback() {
            @Override public void onSuccess(JSONObject r) {
                try {
                    if (r.getBoolean("success")) {
                        JSONArray arr = r.getJSONArray("sessions");
                        List<JSONObject> list = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) list.add(arr.getJSONObject(i));
                        callback.onSuccess(list);
                    } else { callback.onError(r.optString("message", "Error")); }
                } catch (Exception e) { callback.onError("Error de resposta"); }
            }
            @Override public void onError(String e) { callback.onError("Error de xarxa: " + e); }
        });
    }

    public void createSession(int userId, int vehicleId, Integer salleId,
                              int tempsMaxim, int avisoTemps,
                              double lat, double lng, SessionCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("action", "create");
            body.put("user_id", userId);
            body.put("vehicle_id", vehicleId);
            if (salleId != null) body.put("salle_id", salleId);
            body.put("temps_maxim_minuts", tempsMaxim);
            body.put("aviso_temps_minuts", avisoTemps);
            body.put("latitud_inici", lat);
            body.put("longitud_inici", lng);
            api.post("sessions.php", body, new ApiClient.ApiCallback() {
                @Override public void onSuccess(JSONObject r) {
                    try {
                        if (r.getBoolean("success")) {
                            callback.onSuccess(r.getInt("session_id"));
                        } else { callback.onError(r.optString("message")); }
                    } catch (Exception e) { callback.onError("Error de resposta"); }
                }
                @Override public void onError(String e) { callback.onError("Error de xarxa: " + e); }
            });
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }

    public void finishSession(int sessionId, String tipus, SessionCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("action", "finish");
            body.put("session_id", sessionId);
            body.put("tipus_finalitzacio", tipus);
            api.post("sessions.php", body, new ApiClient.ApiCallback() {
                @Override public void onSuccess(JSONObject r) {
                    try {
                        if (r.getBoolean("success")) callback.onSuccess(sessionId);
                        else callback.onError(r.optString("message"));
                    } catch (Exception e) { callback.onError("Error de resposta"); }
                }
                @Override public void onError(String e) { callback.onError("Error de xarxa: " + e); }
            });
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }
}