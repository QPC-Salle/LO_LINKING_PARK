package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginGActivity - Login con Google (actualmente deshabilitado)
 * Para habilitar Google Sign-In, se necesita configurar Firebase o Google Identity Services
 */
public class LoginGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mostrar mensaje de que Google Login no está disponible
        Toast.makeText(this, "Login con Google no disponible. Use login con email.", Toast.LENGTH_LONG).show();

        // Redirigir al login normal
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}