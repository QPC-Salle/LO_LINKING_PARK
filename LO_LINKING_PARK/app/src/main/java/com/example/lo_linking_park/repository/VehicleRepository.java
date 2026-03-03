package com.example.lo_linking_park.repository;

import com.example.lo_linking_park.model.Vehicle;
import com.example.lo_linking_park.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    private static VehicleRepository instance;
    private final ApiClient api;

    private VehicleRepository() { api = ApiClient.getInstance(); }

    public static synchronized VehicleRepository getInstance() {
        if (instance == null) instance = new VehicleRepository();
        return instance;
    }

    public interface VehicleListCallback {
        void onSuccess(List<Vehicle> vehicles);
        void onError(String error);
    }

    public interface VehicleCallback {
        void onSuccess(String vehicleId);
        void onError(String error);
    }

    public void getVehiclesByUser(int userId, VehicleListCallback callback) {
        api.get("vehicles.php?user_id=" + userId, new ApiClient.ApiCallback() {
            @Override public void onSuccess(JSONObject r) {
                try {
                    List<Vehicle> list = new ArrayList<>();
                    if (r.getBoolean("success")) {
                        JSONArray arr = r.getJSONArray("vehicles");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Vehicle v = new Vehicle();
                            v.setId(String.valueOf(obj.getInt("id")));
                            v.setMatricula(obj.getString("matricula"));
                            v.setMarca(obj.optString("marca", ""));
                            v.setModel(obj.optString("model", ""));
                            v.setColor(obj.optString("color", ""));
                            list.add(v);
                        }
                    }
                    callback.onSuccess(list);
                } catch (Exception e) { callback.onError(e.getMessage()); }
            }
            @Override public void onError(String e) { callback.onError(e); }
        });
    }

    public void addVehicle(int userId, Vehicle vehicle, VehicleCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id", userId);
            body.put("matricula", vehicle.getMatricula());
            body.put("marca", vehicle.getMarca());
            body.put("model", vehicle.getModel());
            body.put("color", vehicle.getColor());
            body.put("any_fabricacio", vehicle.getAnyFabricacio());
            api.post("vehicles.php", body, new ApiClient.ApiCallback() {
                @Override public void onSuccess(JSONObject r) {
                    try {
                        if (r.getBoolean("success")) callback.onSuccess(String.valueOf(r.getInt("id")));
                        else callback.onError(r.getString("message"));
                    } catch (Exception e) { callback.onError(e.getMessage()); }
                }
                @Override public void onError(String e) { callback.onError(e); }
            });
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }

    public void deleteVehicle(String vehicleId, VehicleCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("action", "delete");
            body.put("id", vehicleId);
            api.post("vehicles.php", body, new ApiClient.ApiCallback() {
                @Override public void onSuccess(JSONObject r) {
                    try {
                        if (r.getBoolean("success")) callback.onSuccess(vehicleId);
                        else callback.onError(r.getString("message"));
                    } catch (Exception e) { callback.onError(e.getMessage()); }
                }
                @Override public void onError(String e) { callback.onError(e); }
            });
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }
}