package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lo_linking_park.repository.AuthRepository;
import com.example.lo_linking_park.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email))    { etEmail.setError("Email obligatori"); etEmail.requestFocus(); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Email invàlid"); etEmail.requestFocus(); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Contrasenya obligatòria"); etPassword.requestFocus(); return; }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        AuthRepository.getInstance().login(email, password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(int userId, String nom, String userEmail) {
                new SessionManager(LoginActivity.this).saveSession(userId, userEmail, nom);
                Toast.makeText(LoginActivity.this, "Benvingut, " + nom + "!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            @Override public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}