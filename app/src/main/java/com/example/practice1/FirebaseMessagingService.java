package com.example.practice1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    private static final String CHANNEL_ID = "default_channel"; // 채널 ID
    private FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived called");

        if (remoteMessage.getData().size() > 0) {
            // 데이터 메시지 처리
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String userId = remoteMessage.getData().get("userId");

            Log.d(TAG, "Data message - Title: " + title);
            Log.d(TAG, "Data message - Body: " + body);

            // 알림 저장 및 표시
            saveNotificationToFirestore(title, body, userId);
            sendNotification(title, body);
        } else {
            Log.d(TAG, "No data in message");
        }
    }

    private void saveNotificationToFirestore(String title, String msg, String userId) {
        NotificationModel notification = new NotificationModel(title, msg, System.currentTimeMillis(), userId);  // userId 포함
        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding notification", e);
                });
    }

    private void sendNotification(String title, String msg) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 1000})
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, mBuilder.build());
    }
}