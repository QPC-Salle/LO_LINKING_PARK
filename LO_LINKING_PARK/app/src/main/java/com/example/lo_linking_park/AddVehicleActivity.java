package com.example.lo_linking_park;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lo_linking_park.model.Vehicle;
import com.example.lo_linking_park.repository.VehicleRepository;
import com.example.lo_linking_park.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class AddVehicleActivity extends AppCompatActivity {

    private TextInputEditText etMatricula, etMarca, etModel, etColor, etAny;
    private Button btnSave;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        etMatricula = findViewById(R.id.etMatricula);
        etMarca     = findViewById(R.id.etMarca);
        etModel     = findViewById(R.id.etModel);
        etColor     = findViewById(R.id.etColor);
        etAny       = findViewById(R.id.etAny);
        btnSave     = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveVehicle());
    }

    private void saveVehicle() {
        String matricula = etMatricula.getText().toString().trim().toUpperCase();
        String marca     = etMarca.getText().toString().trim();
        String model     = etModel.getText().toString().trim();
        String color     = etColor.getText().toString().trim();
        String anyStr    = etAny.getText().toString().trim();

        if (TextUtils.isEmpty(matricula)) { etMatricula.setError("Matrícula obligatòria"); etMatricula.requestFocus(); return; }
        if (TextUtils.isEmpty(marca))     { etMarca.setError("Marca obligatòria"); etMarca.requestFocus(); return; }
        if (TextUtils.isEmpty(model))     { etModel.setError("Model obligatori"); etModel.requestFocus(); return; }
        if (TextUtils.isEmpty(color))     { etColor.setError("Color obligatori"); etColor.requestFocus(); return; }

        int anyFabricacio = 0;
        if (!TextUtils.isEmpty(anyStr)) {
            try { anyFabricacio = Integer.parseInt(anyStr); } catch (Exception ignored) {}
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        Vehicle vehicle = new Vehicle();
        vehicle.setMatricula(matricula);
        vehicle.setMarca(marca);
        vehicle.setModel(model);
        vehicle.setColor(color);
        vehicle.setAnyFabricacio(anyFabricacio);

        int userId = new SessionManager(this).getUserId();
        VehicleRepository.getInstance().addVehicle(userId, vehicle, new VehicleRepository.VehicleCallback() {
            @Override public void onSuccess(String vehicleId) {
                Toast.makeText(AddVehicleActivity.this, "Vehicle afegit correctament!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(AddVehicleActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}