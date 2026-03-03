package com.example.lo_linking_park;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    // [nom, adreca, lat, lng, salleId]
    private static final String[][] SALLES = {
        {"La Salle Campus Barcelona", "Carrer dels Quatre Camins, 30, Barcelona", "41.4156", "2.1745", "1"},
        {"La Salle Bonanova", "Passeig de Sant Gervasi, 41, Barcelona", "41.4089", "2.1364", "2"},
        {"La Salle Gràcia", "Carrer del Prat, 15, Barcelona", "41.3942", "2.1656", "3"},
        {"La Salle Tarragona", "Carrer de Sant Pau, 3, Tarragona", "41.1189", "1.2445", "4"},
        {"La Salle Girona", "Carrer dels Pares Salesians, 1, Girona", "41.9794", "2.8214", "5"},
        {"La Salle Lleida", "Avinguda del Segre, 3, Lleida", "41.6132", "0.6245", "6"},
        {"La Salle Mollerussa", "Carrer de la Font, 1, Mollerussa", "41.6310", "0.8980", "7"},
        {"La Salle Manresa", "Carrer del Remei, 12, Manresa", "41.7286", "1.8280", "8"},
        {"La Salle Sabadell", "Carrer de Sant Joan, 10, Sabadell", "41.5431", "2.1097", "9"},
        {"La Salle Vic", "Carrer de la Riera, 5, Vic", "41.9301", "2.2547", "10"}
    };

    private final Map<Marker, String[]> markerSalleMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_parquimetre);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        for (String[] salle : SALLES) {
            LatLng pos = new LatLng(Double.parseDouble(salle[2]), Double.parseDouble(salle[3]));
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(salle[0])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if (marker != null) {
                markerSalleMap.put(marker, salle);
            }
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.5, 1.5), 8f));

        map.setOnMarkerClickListener(marker -> {
            String[] salle = markerSalleMap.get(marker);
            if (salle != null) {
                showSalleBottomSheet(salle);
            }
            return true;
        });
    }

    private void showSalleBottomSheet(String[] salle) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_salle, null);

        TextView tvNom = view.findViewById(R.id.tvSalleNomBS);
        TextView tvAdreca = view.findViewById(R.id.tvSalleAdrecaBS);
        TextView tvPlaces = view.findViewById(R.id.tvPlacesDisponiblesBS);
        Button btnIniciar = view.findViewById(R.id.btnIniciarParquimetre);

        tvNom.setText(salle[0]);
        tvAdreca.setText(salle[1]);
        tvPlaces.setText("Places disponibles: N/A");

        int salleId = Integer.parseInt(salle[4]);
        btnIniciar.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, ParkingSessionActivity.class);
            intent.putExtra("salleId", salleId);
            intent.putExtra("salleNom", salle[0]);
            intent.putExtra("salleAdreca", salle[1]);
            startActivity(intent);
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
