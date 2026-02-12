package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lo_linking_park.utils.DataMigrationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    //Init
        Button btnRegistrat = findViewById(R.id.btnRegistrat);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
    //Funcions
        btnRegistrat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginGActivity.class);
                startActivity(i);
            }
        });

        // PASO 5: Poblar Datos Iniciales
        DataMigrationHelper migrationHelper = new DataMigrationHelper();
        migrationHelper.checkIfDataExists(new DataMigrationHelper.CheckDataCallback() {
            @Override
            public void onResult(boolean exists) {
                if (!exists) {
                    // Migrar datos iniciales
                    migrationHelper.migrateAll(new DataMigrationHelper.MigrationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this,
                                "Datos iniciales cargados", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("MainActivity", "Error al cargar datos: " + error);
                        }
                    });
                } else {
                    Log.d("MainActivity", "Los datos iniciales ya existen. No se requiere migración.");
                }
            }
        });
    }
}
