package com.example.lo_linking_park;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lo_linking_park.api.ApiClient;

import org.json.JSONObject;

/**
 * EJEMPLO DE LOGIN CON API
 *
 * Este es un ejemplo de cómo implementar login usando ApiClient.
 * Copia este código a tu LoginActivity existente.
 */
public class LoginActivityExample extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Click en botón login
        btnLogin.setOnClickListener(v -> doLogin());

        // Click en botón registro
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivityExample.this, RegisterActivity.class));
        });

        // Verificar si ya hay sesión activa
        if (isUserLoggedIn()) {
            goToMainActivity();
        }
    }

    /**
     * Realiza el login llamando a la API
     */
    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones
        if (email.isEmpty()) {
            etEmail.setError("Email requerido");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Contraseña requerida");
            etPassword.requestFocus();
            return;
        }

        // Mostrar loading
        setLoading(true);

        try {
            // Crear JSON con datos de login
            JSONObject data = new JSONObject();
            data.put("email", email);
            data.put("password", password);

            // Llamar a API
            ApiClient.post("login.php", data, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    setLoading(false);

                    try {
                        boolean success = response.getBoolean("success");

                        if (success) {
                            // Login exitoso
                            JSONObject user = response.getJSONObject("user");
                            String userId = user.getString("id");
                            String userName = user.getString("nom");
                            String userSurname = user.optString("cognoms", "");
                            String userEmail = user.getString("email");

                            // Guardar sesión
                            saveUserSession(userId, userName, userSurname, userEmail);

                            // Mensaje de éxito
                            Toast.makeText(LoginActivityExample.this,
                                "Bienvenido " + userName,
                                Toast.LENGTH_SHORT).show();

                            // Ir a MainActivity
                            goToMainActivity();

                        } else {
                            // Login fallido
                            String message = response.optString("message", "Credenciales incorrectas");
                            Toast.makeText(LoginActivityExample.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(LoginActivityExample.this,
                            "Error al procesar respuesta: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    setLoading(false);
                    Toast.makeText(LoginActivityExample.this,
                        "Error de conexión: " + error,
                        Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Guarda la sesión del usuario en SharedPreferences
     */
    private void saveUserSession(String userId, String userName, String userSurname, String userEmail) {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit()
            .putString("userId", userId)
            .putString("userName", userName)
            .putString("userSurname", userSurname)
            .putString("userEmail", userEmail)
            .putBoolean("isLoggedIn", true)
            .apply();
    }

    /**
     * Verifica si el usuario ya tiene sesión activa
     */
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    /**
     * Obtiene el ID del usuario logueado
     */
    public static String getUserId(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    /**
     * Cierra la sesión del usuario
     */
    public static void logout(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    /**
     * Navega a MainActivity
     */
    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivityExample.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Muestra u oculta el loading
     */
    private void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
        }
    }
}

