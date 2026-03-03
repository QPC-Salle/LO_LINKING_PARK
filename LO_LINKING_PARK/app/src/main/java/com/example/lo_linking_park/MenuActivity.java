package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.lo_linking_park.utils.SessionManager;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SessionManager sm = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (tvWelcome != null) tvWelcome.setText("Hola, " + sm.getUserNom() + "!");

        // Botó enrere = tancar sessió
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                sm.clearSession();
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });
        }

        // Card 1 → Parquímetre (Mapa)
        CardView c1 = findViewById(R.id.cardOption1);
        if (c1 != null) c1.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));

        // Card 2 → Historial
        CardView c2 = findViewById(R.id.cardOption2);
        if (c2 != null) c2.setOnClickListener(v -> startActivity(new Intent(this, HistorialActivity.class)));

        // Card 3 → Carregadors
        CardView c3 = findViewById(R.id.cardOption3);
        if (c3 != null) c3.setOnClickListener(v -> startActivity(new Intent(this, MapCargadorsActivity.class)));

        // Card 4 → Garatge (vehicles)
        CardView c4 = findViewById(R.id.cardOption4);
        if (c4 != null) c4.setOnClickListener(v -> startActivity(new Intent(this, GaratgeActivity.class)));
    }
}