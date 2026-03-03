package com.example.lo_linking_park.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ParkingSession";
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(int userId, String email, String nom) {
        editor.putInt("userId", userId);
        editor.putString("userEmail", email);
        editor.putString("userNom", nom);
        editor.putBoolean("isLogged", true);
        editor.apply();
    }

    public int     getUserId()    { return prefs.getInt("userId", -1); }
    public String  getUserEmail() { return prefs.getString("userEmail", ""); }
    public String  getUserNom()   { return prefs.getString("userNom", ""); }
    public boolean isLoggedIn()   { return prefs.getBoolean("isLogged", false); }

    public void clearSession() { editor.clear(); editor.apply(); }

    // ── Active parking session ──────────────────────────────────────────────

    public void saveActiveSession(int sessionId, long startTime, int tempsMaxim, int salleId) {
        editor.putInt("activeSessionId", sessionId);
        editor.putLong("sessionStartTime", startTime);
        editor.putInt("sessionTempsMaxim", tempsMaxim);
        editor.putInt("sessionSalleId", salleId);
        editor.putBoolean("hasActiveSession", true);
        editor.apply();
    }

    public int     getActiveSessionId()    { return prefs.getInt("activeSessionId", -1); }
    public long    getSessionStartTime()   { return prefs.getLong("sessionStartTime", 0); }
    public int     getSessionTempsMaxim()  { return prefs.getInt("sessionTempsMaxim", 60); }
    public int     getSessionSalleId()     { return prefs.getInt("sessionSalleId", -1); }
    public boolean hasActiveSession()      { return prefs.getBoolean("hasActiveSession", false); }

    public void clearActiveSession() {
        editor.remove("activeSessionId");
        editor.remove("sessionStartTime");
        editor.remove("sessionTempsMaxim");
        editor.remove("sessionSalleId");
        editor.putBoolean("hasActiveSession", false);
        editor.apply();
    }
}