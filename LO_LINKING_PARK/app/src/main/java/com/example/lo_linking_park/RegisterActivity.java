package com.example.lo_linking_park;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lo_linking_park.model.Usuario;
import com.example.lo_linking_park.repository.FirebaseAuthRepository;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNom, etCognoms, etEmail, etTelefon, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar vistas
        etNom = findViewById(R.id.etNom);
        etCognoms = findViewById(R.id.etCognoms);
        etEmail = findViewById(R.id.etEmail);
        etTelefon = findViewById(R.id.etTelefon);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Inicializar repositorio
        authRepository = FirebaseAuthRepository.getInstance();

        // Configurar botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Obtener valores
        String nom = etNom.getText().toString().trim();
        String cognoms = etCognoms.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefon = etTelefon.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validaciones
        if (TextUtils.isEmpty(nom)) {
            etNom.setError("El nombre es obligatorio");
            etNom.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cognoms)) {
            etCognoms.setError("Los apellidos son obligatorios");
            etCognoms.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("El email es obligatorio");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(telefon)) {
            etTelefon.setError("El teléfono es obligatorio");
            etTelefon.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("La contraseña es obligatoria");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        // Mostrar progreso
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Crear objeto Usuario
        Usuario usuario = new Usuario(nom, cognoms, email, telefon);

        // Registrar en Firebase
        authRepository.registerUser(email, password, usuario, new FirebaseAuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                finish(); // Volver a la pantalla anterior
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
