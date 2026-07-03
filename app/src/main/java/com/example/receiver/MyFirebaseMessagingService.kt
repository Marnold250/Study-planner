package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMessaging"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM push token: $token")
        // In a real production environment, we would upload this token to our backend server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Push Notification received from: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            val title = it.title ?: "Study Reminder"
            val body = it.body ?: "You have a task reminder!"
            showPushNotification(title, body)
        } ?: run {
            // Check if message contains a data payload (for silent / custom pushes)
            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"] ?: "Study Reminder"
                val body = remoteMessage.data["body"] ?: "You have a task reminder!"
                showPushNotification(title, body)
            }
        }
    }

    private fun showPushNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val channelId = "push_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Push Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming Study Task push updates"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val iconRes = applicationInfo.icon
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(if (iconRes != 0) iconRes else android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
