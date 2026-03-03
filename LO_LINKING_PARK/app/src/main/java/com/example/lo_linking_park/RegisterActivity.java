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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNom, etCognoms, etEmail, etTelefon, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNom           = findViewById(R.id.etNom);
        etCognoms       = findViewById(R.id.etCognoms);
        etEmail         = findViewById(R.id.etEmail);
        etTelefon       = findViewById(R.id.etTelefon);
        etPassword      = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister     = findViewById(R.id.btnRegister);
        progressBar     = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String nom             = etNom.getText().toString().trim();
        String cognoms         = etCognoms.getText().toString().trim();
        String email           = etEmail.getText().toString().trim();
        String telefon         = etTelefon.getText().toString().trim();
        String password        = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(nom))      { etNom.setError("Nom obligatori"); etNom.requestFocus(); return; }
        if (TextUtils.isEmpty(cognoms))  { etCognoms.setError("Cognoms obligatoris"); etCognoms.requestFocus(); return; }
        if (TextUtils.isEmpty(email))    { etEmail.setError("Email obligatori"); etEmail.requestFocus(); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Email invàlid"); etEmail.requestFocus(); return; }
        if (TextUtils.isEmpty(telefon))  { etTelefon.setError("Telèfon obligatori"); etTelefon.requestFocus(); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Contrasenya obligatòria"); etPassword.requestFocus(); return; }
        if (password.length() < 6)       { etPassword.setError("Mínim 6 caràcters"); etPassword.requestFocus(); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Les contrasenyes no coincideixen"); etConfirmPassword.requestFocus(); return; }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        AuthRepository.getInstance().register(nom, cognoms, email, telefon, password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess(int userId, String userName, String userEmail) {
                new SessionManager(RegisterActivity.this).saveSession(userId, userEmail, userName);
                Toast.makeText(RegisterActivity.this, "Benvingut, " + userName + "!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            @Override public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}