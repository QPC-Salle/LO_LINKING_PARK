package com.example.lo_linking_park.repository;

import android.util.Log;
import com.example.lo_linking_park.model.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private static final String TAG = "SessionRepository";
    private static final String COLLECTION = "sessions";
    private final FirebaseFirestore db;

    public SessionRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface SessionListCallback {
        void onSuccess(List<Session> sessions);
        void onError(String error);
    }

    public interface SessionCallback {
        void onSuccess(Session session);
        void onError(String error);
    }

    public void getSessionsByUser(String userId, SessionListCallback callback) {
        db.collection(COLLECTION)
                .whereEqualTo("usuariId", userId)
                .orderBy("dataInici", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Session> sessions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Session session = doc.toObject(Session.class);
                        session.setId(doc.getId());
                        sessions.add(session);
                    }
                    callback.onSuccess(sessions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting sessions", e);
                    callback.onError(e.getMessage());
                });
    }

    public void createSession(Session session, SessionCallback callback) {
        db.collection(COLLECTION)
                .add(session)
                .addOnSuccessListener(docRef -> {
                    session.setId(docRef.getId());
                    callback.onSuccess(session);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating session", e);
                    callback.onError(e.getMessage());
                });
    }

    public void updateSession(Session session, SessionCallback callback) {
        if (session.getId() == null) {
            if (callback != null) callback.onError("Session ID is null");
            return;
        }
        db.collection(COLLECTION)
                .document(session.getId())
                .set(session)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(session);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating session", e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }
}