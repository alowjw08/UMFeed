package com.example.umfeed; // Make sure this matches your package name

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the notification message
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d("FCM", "New token: " + token);
        // Handle the new token (e.g., send to your server)
    }
}
