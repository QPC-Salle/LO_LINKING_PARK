package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lo_linking_park.model.Vehicle;
import com.example.lo_linking_park.repository.VehicleRepository;
import com.example.lo_linking_park.utils.SessionManager;
import java.util.List;

public class GaratgeActivity extends AppCompatActivity {

    private LinearLayout vehicleContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garatje);

        vehicleContainer = findViewById(R.id.vehicleContainer);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        Button btnAddVehicle = findViewById(R.id.btnAddVehicle);
        if (btnAddVehicle != null)
            btnAddVehicle.setOnClickListener(v -> startActivity(new Intent(this, AddVehicleActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVehicles();
    }

    private void loadVehicles() {
        int userId = new SessionManager(this).getUserId();
        VehicleRepository.getInstance().getVehiclesByUser(userId, new VehicleRepository.VehicleListCallback() {
            @Override public void onSuccess(List<Vehicle> vehicles) {
                vehicleContainer.removeAllViews();
                if (vehicles.isEmpty()) {
                    TextView tv = new TextView(GaratgeActivity.this);
                    tv.setText("No tens vehicles registrats.\nPrem '+Afegir vehicle' per afegir-ne un.");
                    tv.setPadding(16, 32, 16, 16);
                    tv.setTextSize(16);
                    vehicleContainer.addView(tv);
                } else {
                    for (Vehicle v : vehicles) addVehicleCard(v);
                }
            }
            @Override public void onError(String error) {
                Toast.makeText(GaratgeActivity.this, "Error carregant vehicles: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addVehicleCard(Vehicle vehicle) {
        View card = getLayoutInflater().inflate(R.layout.item_vehicle, vehicleContainer, false);
        TextView tvMatricula = card.findViewById(R.id.tvMatricula);
        TextView tvModel     = card.findViewById(R.id.tvModel);
        Button  btnDelete    = card.findViewById(R.id.btnDeleteVehicle);

        if (tvMatricula != null) tvMatricula.setText(vehicle.getMatricula());
        if (tvModel != null)     tvModel.setText(vehicle.getMarca() + " " + vehicle.getModel());
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                VehicleRepository.getInstance().deleteVehicle(vehicle.getId(), new VehicleRepository.VehicleCallback() {
                    @Override public void onSuccess(String id) {
                        Toast.makeText(GaratgeActivity.this, "Vehicle eliminat", Toast.LENGTH_SHORT).show();
                        loadVehicles();
                    }
                    @Override public void onError(String error) {
                        Toast.makeText(GaratgeActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
        vehicleContainer.addView(card);
    }
}