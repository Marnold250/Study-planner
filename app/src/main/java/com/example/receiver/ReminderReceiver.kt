package com.example.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_DISMISS_ALARM = "com.example.receiver.ACTION_DISMISS_ALARM"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val taskId = intent.getIntExtra("TASK_ID", 0)
        Log.d("ReminderReceiver", "onReceive action=$action, taskId=$taskId")

        if (action == ACTION_DISMISS_ALARM) {
            AlarmSoundManager.stopAlarm()
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (taskId != 0) {
                notificationManager?.cancel(taskId)
            }
            return
        }

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

            // Create Intent for Dismiss Action button in Notification
            val dismissIntent = Intent(context, ReminderReceiver::class.java).apply {
                this.action = ACTION_DISMISS_ALARM
                putExtra("TASK_ID", taskId)
            }
            val dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                taskId + 100000, // Unique request code
                dismissIntent,
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
                .addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Dismiss Alarm",
                    dismissPendingIntent
                )
                .setOngoing(true) // Keep notification on until dismissed!
                .build()

            notificationManager.notify(taskId, notification)

            // Play selected alarm sound looping until dismissed
            AlarmSoundManager.startAlarm(context, taskId, task.title, task.reminderSound)

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
}
