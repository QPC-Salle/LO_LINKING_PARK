package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.lo_linking_park.repository.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Inicializar repositorio
        AuthRepository.getInstance();

        // Configurar botón de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        // Obtener valores
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validaciones
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("El email es obligatorio");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("La contraseña es obligatoria");
            etPassword.requestFocus();
            return;
        }

        // Mostrar progreso
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Login en API REST
        AuthRepository.getInstance().login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(int userId, String nom, String email) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Bienvenido " + nom, Toast.LENGTH_SHORT).show();

                // Navegar al menú principal
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
