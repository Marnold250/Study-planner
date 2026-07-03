package com.example.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", 0)
        if (taskId == 0) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // Fetch task from database in IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val task = db.taskDao().getTaskById(taskId) ?: return@launch

            // Create an Intent to launch MainActivity when clicking the notification
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                taskId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val iconRes = context.applicationInfo.icon
            val notification = NotificationCompat.Builder(context, "student_task_reminders")
                .setSmallIcon(if (iconRes != 0) iconRes else android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Study Reminder: ${task.title}")
                .setContentText("${task.subject} task is due soon! Let's get focused.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(taskId, notification)

            // Play the customized predefined notification sound
            playNotificationSound(context, task.reminderSound)

            // Handle repeating reminders: Reschedule 24 hours in the future
            if (task.reminderRepeat && !task.isCompleted) {
                val nextReminderTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
                val updatedTask = task.copy(reminderTime = nextReminderTime)
                db.taskDao().updateTask(updatedTask)
                ReminderScheduler.schedule(context, updatedTask)
                Log.d("ReminderReceiver", "Rescheduled repeating task ${task.id} for $nextReminderTime")
            }
        }
    }

    private fun playNotificationSound(context: Context, soundName: String) {
        try {
            Log.d("ReminderReceiver", "Playing custom sound: $soundName")
            when (soundName) {
                "Digital Chime" -> {
                    val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
                }
                "Classic Bell" -> {
                    val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                    toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 500)
                }
                "Gentle Harp" -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                        toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                        delay(200)
                        toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                        delay(200)
                        toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
                    }
                }
                else -> {
                    // Default notification sound
                    val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(context, defaultUri)
                    r?.play()
                }
            }
        } catch (e: Exception) {
            Log.e("ReminderReceiver", "Error playing notification sound: ${e.message}", e)
        }
    }
}
