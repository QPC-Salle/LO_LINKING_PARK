package com.example.lo_linking_park;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lo_linking_park.model.Session;
import com.example.lo_linking_park.repository.SessionRepository;
import com.example.lo_linking_park.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialActivity extends AppCompatActivity {

    private LinearLayout llHistorial;
    private TextView tvEmpty;
    private SessionManager sessionManager;
    private SessionRepository sessionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        sessionManager = new SessionManager(this);
        sessionRepository = new SessionRepository();

        llHistorial = findViewById(R.id.llHistorial);
        tvEmpty = findViewById(R.id.tvEmpty);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        carregarHistorial();
    }

    private void carregarHistorial() {
        int rawUserId = sessionManager.getUserId();
        if (rawUserId == -1) {
            Toast.makeText(this, "Sessió no vàlida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = String.valueOf(rawUserId);

        sessionRepository.getSessionsByUser(userId, new SessionRepository.SessionListCallback() {
            @Override
            public void onSuccess(List<Session> sessions) {
                if (sessions == null || sessions.isEmpty()) {
                    if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
                    if (llHistorial != null) llHistorial.setVisibility(View.GONE);
                    return;
                }
                if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
                if (llHistorial != null) {
                    llHistorial.setVisibility(View.VISIBLE);
                    llHistorial.removeAllViews();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    for (Session session : sessions) {
                        TextView tv = new TextView(HistorialActivity.this);
                        String dataFiText = session.getDataFi() > 0
                                ? sdf.format(new Date(session.getDataFi()))
                                : "En curs";
                        String text = "Vehicle: " + (session.getVehicleId() != null ? session.getVehicleId() : "-")
                                + "\nInici: " + (session.getDataInici() > 0 ? sdf.format(new Date(session.getDataInici())) : "-")
                                + "\nFi: " + dataFiText
                                + "\nEstat: " + (session.getEstat() != null ? session.getEstat() : "-");
                        tv.setText(text);
                        tv.setPadding(16, 16, 16, 16);
                        tv.setTextSize(14f);
                        llHistorial.addView(tv);
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HistorialActivity.this, "Error carregant historial: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
