package com.example.lo_linking_park;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.example.lo_linking_park.model.Session;
import com.example.lo_linking_park.model.Vehicle;
import com.example.lo_linking_park.repository.SessionRepository;
import com.example.lo_linking_park.repository.VehicleRepository;
import com.example.lo_linking_park.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParkingSessionActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "parking_channel";
    private static final double TARIFA = 2.50;
    private static final int MIN_TEMPS_MAXIM = 15;
    private static final int MAX_TEMPS_MAXIM = 240;
    private static final int MIN_AVIS_TEMPS  = 5;
    private static final int MAX_AVIS_TEMPS  = 60;

    private int salleId;
    private String salleNom;
    private String salleAdreca;
    private int sessionId = -1;
    private Session activeSession;
    private SessionRepository sessionRepository;
    private long startTime;
    private CountDownTimer countDownTimer;
    private Handler notificationHandler;
    private Runnable notificationRunnable;
    private boolean sessionActive = false;

    private Spinner spinnerVehicle;
    private EditText etTempsMaxim;
    private EditText etAvisTemps;
    private TextView tvEstat;
    private TextView tvTempsRestant;
    private TextView tvPreu;
    private Button btnIniciarSessio;
    private Button btnFinalitzar;

    private List<Vehicle> vehicleList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_session);

        sessionManager = new SessionManager(this);
        sessionRepository = new SessionRepository();

        salleId    = getIntent().getIntExtra("salleId", -1);
        salleNom   = getIntent().getStringExtra("salleNom");
        salleAdreca = getIntent().getStringExtra("salleAdreca");

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> handleBack());

        TextView tvSalleNom   = findViewById(R.id.tvSalleNom);
        TextView tvSalleAdreca = findViewById(R.id.tvSalleAdreca);
        spinnerVehicle  = findViewById(R.id.spinnerVehicle);
        etTempsMaxim    = findViewById(R.id.etTempsMaxim);
        etAvisTemps     = findViewById(R.id.etAvisTemps);
        tvEstat         = findViewById(R.id.tvEstat);
        tvTempsRestant  = findViewById(R.id.tvTempsRestant);
        tvPreu          = findViewById(R.id.tvPreu);
        btnIniciarSessio = findViewById(R.id.btnIniciarSessio);
        btnFinalitzar   = findViewById(R.id.btnFinalitzar);

        if (salleNom == null && sessionManager.hasActiveSession()) {
            salleId    = sessionManager.getSessionSalleId();
            salleNom   = "";
            salleAdreca = "";
        }

        if (tvSalleNom != null)   tvSalleNom.setText(salleNom != null ? salleNom : "");
        if (tvSalleAdreca != null) tvSalleAdreca.setText(salleAdreca != null ? salleAdreca : "");

        if (etTempsMaxim != null) etTempsMaxim.setText("60");
        if (etAvisTemps  != null) etAvisTemps.setText("15");

        loadVehicles();
        createNotificationChannel();

        if (btnIniciarSessio != null) btnIniciarSessio.setOnClickListener(v -> iniciarSessio());
        if (btnFinalitzar    != null) btnFinalitzar.setOnClickListener(v -> finalitzarSessio());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });

        if (sessionManager.hasActiveSession()) {
            restoreSession();
        } else {
            if (tvEstat        != null) tvEstat.setText("Sense sessió activa");
            if (tvTempsRestant != null) tvTempsRestant.setVisibility(View.GONE);
            if (btnFinalitzar  != null) btnFinalitzar.setVisibility(View.GONE);
        }
    }

    private void loadVehicles() {
        int userId = sessionManager.getUserId();
        VehicleRepository.getInstance().getVehiclesByUser(userId,
                new VehicleRepository.VehicleListCallback() {
            @Override
            public void onSuccess(List<Vehicle> vehicles) {
                vehicleList = vehicles;
                List<String> names = new ArrayList<>();
                for (Vehicle v : vehicles) {
                    names.add(v.getMarca() + " " + v.getModel() + " (" + v.getMatricula() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ParkingSessionActivity.this,
                        android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if (spinnerVehicle != null) spinnerVehicle.setAdapter(adapter);
            }
            @Override
            public void onError(String error) { /* spinner stays empty */ }
        });
    }

    private void iniciarSessio() {
        if (vehicleList.isEmpty()) {
            Toast.makeText(this, "No hi ha vehicles. Afegeix un vehicle primer.", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedPos = spinnerVehicle != null ? spinnerVehicle.getSelectedItemPosition() : -1;
        if (selectedPos < 0 || selectedPos >= vehicleList.size()) {
            Toast.makeText(this, "Selecciona un vehicle.", Toast.LENGTH_SHORT).show();
            return;
        }
        int tempsMaxim, avisoTemps;
        try {
            tempsMaxim = Integer.parseInt(etTempsMaxim.getText().toString());
            avisoTemps = Integer.parseInt(etAvisTemps.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Temps no vàlid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tempsMaxim < MIN_TEMPS_MAXIM || tempsMaxim > MAX_TEMPS_MAXIM) {
            Toast.makeText(this, "El temps màxim ha de ser entre 15 i 240 minuts.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (avisoTemps < MIN_AVIS_TEMPS || avisoTemps > MAX_AVIS_TEMPS) {
            Toast.makeText(this, "El temps d'avís ha de ser entre 5 i 60 minuts.", Toast.LENGTH_SHORT).show();
            return;
        }
        Vehicle vehicle = vehicleList.get(selectedPos);
        int userId = sessionManager.getUserId();
        int finalTempsMaxim = tempsMaxim;
        int finalAvisoTemps = avisoTemps;

        Session session = new Session(String.valueOf(userId), vehicle.getId(),
                String.valueOf(salleId), tempsMaxim, avisoTemps, 0.0, 0.0);
        sessionRepository.createSession(session, new SessionRepository.SessionCallback() {
            @Override
            public void onSuccess(Session createdSession) {
                activeSession = createdSession;
                onSessionCreated(finalTempsMaxim, finalAvisoTemps);
            }
            @Override
            public void onError(String error) {
                // Start session locally even if backend fails
                onSessionCreated(finalTempsMaxim, finalAvisoTemps);
            }
        });
    }

    private void onSessionCreated(int tempsMaxim, int avisoTemps) {
        startTime  = System.currentTimeMillis();
        sessionActive = true;
        sessionManager.saveActiveSession(sessionId, startTime, tempsMaxim, salleId);
        startCountdown(tempsMaxim * 60L * 1000L, avisoTemps);
        updateUISessionActive();
    }

    private void startCountdown(long totalMs, int avisoTemps) {
        long avisoMs = totalMs - (avisoTemps * 60L * 1000L);
        if (avisoMs > 0) {
            notificationHandler = new Handler(Looper.getMainLooper());
            notificationRunnable = this::sendWarningNotification;
            notificationHandler.postDelayed(notificationRunnable, avisoMs);
        }
        countDownTimer = new CountDownTimer(totalMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long elapsed = totalMs - millisUntilFinished;
                long sec = millisUntilFinished / 1000;
                if (tvTempsRestant != null)
                    tvTempsRestant.setText(String.format(Locale.getDefault(), "%02d:%02d", sec / 60, sec % 60));
                if (tvPreu != null)
                    tvPreu.setText(String.format(Locale.getDefault(), "%.2f€", (elapsed / 3600000.0) * TARIFA));
            }
            @Override
            public void onFinish() { autoFinishSession(totalMs); }
        }.start();
    }

    private void updateUISessionActive() {
        if (tvEstat        != null) tvEstat.setText("Sessió activa");
        if (tvTempsRestant != null) tvTempsRestant.setVisibility(View.VISIBLE);
        if (btnIniciarSessio != null) btnIniciarSessio.setVisibility(View.GONE);
        if (btnFinalitzar  != null) btnFinalitzar.setVisibility(View.VISIBLE);
    }

    private void restoreSession() {
        sessionId  = sessionManager.getActiveSessionId();
        startTime  = sessionManager.getSessionStartTime();
        int tempsMaxim = sessionManager.getSessionTempsMaxim();
        salleId    = sessionManager.getSessionSalleId();
        sessionActive = true;

        long totalMs  = tempsMaxim * 60L * 1000L;
        long elapsed  = System.currentTimeMillis() - startTime;
        long remaining = totalMs - elapsed;

        if (remaining <= 0) {
            autoFinishSession(totalMs);
        } else {
            updateUISessionActive();
            startCountdown(remaining, 0);
        }
    }

    private void autoFinishSession(long totalMs) {
        sessionActive = false;
        cancelTimers();
        long elapsed = System.currentTimeMillis() - startTime;
        String preuStr = String.format(Locale.getDefault(), "%.2f€", (elapsed / 3600000.0) * TARIFA);
        if (activeSession != null) {
            activeSession.setEstat("finalitzat");
            activeSession.setTipusFinalitzacio("temps");
            activeSession.setDataFi(System.currentTimeMillis());
            sessionRepository.updateSession(activeSession, null);
            activeSession = null;
        }
        sessionManager.clearActiveSession();
        new AlertDialog.Builder(this)
                .setTitle("Sessió finalitzada")
                .setMessage("El temps ha acabat. Total: " + preuStr)
                .setPositiveButton("OK", (d, w) -> {
                    startActivity(new Intent(this, HistorialActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void finalitzarSessio() {
        if (!sessionActive) return;
        cancelTimers();
        sessionActive = false;
        long elapsed = System.currentTimeMillis() - startTime;
        String preuStr = String.format(Locale.getDefault(), "%.2f€", (elapsed / 3600000.0) * TARIFA);
        if (activeSession != null) {
            activeSession.setEstat("finalitzat");
            activeSession.setTipusFinalitzacio("manual");
            activeSession.setDataFi(System.currentTimeMillis());
            sessionRepository.updateSession(activeSession, null);
            activeSession = null;
        }
        sessionManager.clearActiveSession();
        Toast.makeText(this, "Sessió finalitzada. Total: " + preuStr, Toast.LENGTH_LONG).show();
        finish();
    }

    private void cancelTimers() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (notificationHandler != null && notificationRunnable != null) {
            notificationHandler.removeCallbacks(notificationRunnable);
        }
    }

    private void handleBack() {
        if (sessionActive) {
            new AlertDialog.Builder(this)
                    .setTitle("Sessió activa")
                    .setMessage("Hi ha una sessió activa. Vols sortir igualment?")
                    .setPositiveButton("Sí", (d, w) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            finish();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Parquímetre", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Avisos de parquímetre");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private void sendWarningNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Avís de parquímetre")
                .setContentText("El temps de pàrquing s'acaba aviat!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        nm.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimers();
    }
}
