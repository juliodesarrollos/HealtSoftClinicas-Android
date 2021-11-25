package com.juliovazquez.hsclinic.Tools;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

public class Tool_Firebase extends FirebaseMessagingService {
    String token = "";
    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);

    }
}
