package com.juliovazquez.hsclinic.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.cloudmessaging.CloudMessagingReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.MessagingAnalytics;
import com.google.firebase.messaging.RemoteMessage;
import com.juliovazquez.hsclinic.Activities.Usuario.Activity_Splash_Screen;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Service_Firebase_Messaging extends FirebaseMessagingService {
    String token = "";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        System.out.println(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String id = remoteMessage.getNotification().getColor();
        System.out.println(title);
        System.out.println(body);
        System.out.println("eventoid="+id);
        sendNotification(title, body, id);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendNotification (String title, String body, String id) {
        Intent intent = new Intent(this, Activity_Splash_Screen.class);
        intent.putExtra("PUSH", true);
        intent.putExtra("ID", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String channelId = getString(R.string.default_notification_channel_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notification.build());
    }
}
