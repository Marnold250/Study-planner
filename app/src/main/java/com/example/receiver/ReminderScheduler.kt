package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.Task

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"

    fun schedule(context: Context, task: Task) {
        val reminderTime = task.reminderTime ?: return
        if (reminderTime <= System.currentTimeMillis()) {
            Log.d(TAG, "Reminder time is in the past, skipping schedule.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_ID", task.id)
            putExtra("TASK_TITLE", task.title)
            putExtra("TASK_SUBJECT", task.subject)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
                )
            }
            Log.d(TAG, "Successfully scheduled reminder for task: ${task.title} at $reminderTime")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling alarm: ${e.message}", e)
        }
    }

    fun cancel(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Successfully cancelled reminder for task id: $taskId")
        }
    }
}
