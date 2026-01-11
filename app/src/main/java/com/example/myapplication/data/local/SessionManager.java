package com.example.myapplication.data.local;

import com.google.firebase.auth.FirebaseAuth;

public class SessionManager {

    public static String getFarmerId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }
}