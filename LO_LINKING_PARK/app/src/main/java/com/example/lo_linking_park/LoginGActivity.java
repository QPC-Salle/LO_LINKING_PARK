package com.example.lo_linking_park;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lo_linking_park.repository.FirebaseAuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginGActivity extends AppCompatActivity {

    private static final String TAG = "LoginGActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuthRepository authRepository;
    private SignInButton signInButton;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Log.w(TAG, "Google sign in failed", e);
                        Toast.makeText(this, "Error de inicio de sesión con Google", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loging);

        authRepository = FirebaseAuthRepository.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        authRepository.loginWithGoogle(acct, new FirebaseAuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                Toast.makeText(LoginGActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                // Navigate to main screen
                // Example: startActivity(new Intent(LoginGActivity.this, MenuActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginGActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
