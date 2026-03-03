package com.example.lo_linking_park;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapCargadorsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    // [nom, tipus connector, lat, lng]
    private static final Object[][] CHARGERS = {
        {"Supercharger Tesla Barcelona", "DC", 41.3906, 2.1586},
        {"Endesa X Tarragona",           "AC", 41.1171, 1.2546},
        {"Iberdrola Girona",             "DC", 41.9811, 2.8195},
        {"CEPSA Lleida",                 "AC", 41.6178, 0.6261},
        {"Repsol Manresa",               "AC", 41.7320, 1.8305},
        {"Zunder Sabadell",              "DC", 41.5468, 2.1132},
        {"Endesa X Vic",                 "AC", 41.9310, 2.2541},
        {"Ionity A-2 La Jonquera",       "DC", 42.4245, 2.8624}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cargadors);

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

        for (Object[] charger : CHARGERS) {
            String nom   = (String) charger[0];
            String tipus = (String) charger[1];
            double lat   = (double) charger[2];
            double lng   = (double) charger[3];
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(nom)
                    .snippet("Tipus: " + tipus)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.5, 1.5), 8f));

        map.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });
    }
}
