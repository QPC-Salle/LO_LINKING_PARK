package com.example.lo_linking_park;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lo_linking_park.network.ApiClient;
import com.example.lo_linking_park.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class HistorialActivity extends AppCompatActivity {

    private LinearLayout sessionContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        sessionContainer = findViewById(R.id.sessionContainer);

        int userId = new SessionManager(this).getUserId();
        loadSessions(userId);
    }

    private void loadSessions(int userId) {
        ApiClient.getInstance().get("sessions.php?user_id=" + userId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray sessions = response.getJSONArray("sessions");
                            if (sessions.length() == 0) {
                                TextView tv = new TextView(HistorialActivity.this);
                                tv.setText("No hi ha sessions registrades.");
                                tv.setPadding(16, 16, 16, 16);
                                sessionContainer.addView(tv);
                            } else {
                                for (int i = 0; i < sessions.length(); i++) {
                                    JSONObject s = sessions.getJSONObject(i);
                                    TextView tv = new TextView(HistorialActivity.this);
                                    String dataInici = s.optString("data_inici", "-");
                                    String vehicleId = s.optString("vehicle_id", "-");
                                    String dataFi    = s.optString("data_fi", "-");
                                    String estat     = s.optString("estat", "-");
                                    tv.setText("Data: " + dataInici + "\nVehicle: " + vehicleId +
                                            "\nFi: " + dataFi + "\nEstat: " + estat);
                                    tv.setPadding(16, 16, 16, 16);
                                    sessionContainer.addView(tv);
                                }
                            }
                        } else {
                            showError(response.optString("message", "Error"));
                        }
                    } catch (Exception e) {
                        showError("Error de resposta");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> showError("Error de xarxa: " + error));
            }
        });
    }

    private void showError(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setPadding(16, 16, 16, 16);
        sessionContainer.addView(tv);
    }
}
