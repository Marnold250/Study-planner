package com.example.data

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.TimeZone

data class CalendarInfo(
    val id: Long,
    val displayName: String,
    val accountName: String
)

object CalendarSyncHelper {
    private const val TAG = "CalendarSyncHelper"

    fun hasCalendarPermission(context: Context): Boolean {
        val readPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        val writePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
        return readPerm == PackageManager.PERMISSION_GRANTED && writePerm == PackageManager.PERMISSION_GRANTED
    }

    fun getAvailableCalendars(context: Context): List<CalendarInfo> {
        val calendars = mutableListOf<CalendarInfo>()
        if (!hasCalendarPermission(context)) return calendars

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME
        )

        try {
            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )
            cursor?.use {
                val idIdx = it.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                val nameIdx = it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accountIdx = it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)

                while (it.moveToNext()) {
                    val id = it.getLong(idIdx)
                    val name = it.getString(nameIdx) ?: "Local Calendar"
                    val account = it.getString(accountIdx) ?: "Local Account"
                    calendars.add(CalendarInfo(id, name, account))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying calendars: ${e.message}", e)
        }
        return calendars
    }

    fun addEventToCalendar(context: Context, task: Task, calendarId: Long): Long? {
        if (!hasCalendarPermission(context)) return null

        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, task.dueDate)
                // Event duration default to estimatedMinutes or 30 minutes
                val durationMs = task.estimatedMinutes * 60 * 1000L
                put(CalendarContract.Events.DTEND, task.dueDate + durationMs)
                put(CalendarContract.Events.TITLE, "[Study] ${task.title}")
                put(CalendarContract.Events.DESCRIPTION, "${task.description}\n\nSubject: ${task.subject}\nEstimated Time: ${task.estimatedMinutes} mins")
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()
            Log.d(TAG, "Added event to Google Calendar with ID: $eventId")
            return eventId
        } catch (e: Exception) {
            Log.e(TAG, "Error adding calendar event: ${e.message}", e)
        }
        return null
    }

    fun updateEventInCalendar(context: Context, task: Task, calendarId: Long, eventId: Long): Boolean {
        if (!hasCalendarPermission(context)) return false

        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, task.dueDate)
                val durationMs = task.estimatedMinutes * 60 * 1000L
                put(CalendarContract.Events.DTEND, task.dueDate + durationMs)
                put(CalendarContract.Events.TITLE, "[Study] ${task.title}")
                put(CalendarContract.Events.DESCRIPTION, "${task.description}\n\nSubject: ${task.subject}\nEstimated Time: ${task.estimatedMinutes} mins")
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
            }

            val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
            val rows = context.contentResolver.update(updateUri, values, null, null)
            Log.d(TAG, "Updated $rows calendar event rows for ID $eventId")
            return rows > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error updating calendar event: ${e.message}", e)
            // If the event was deleted manually in Google Calendar, we might get an error or 0 rows.
        }
        return false
    }

    fun deleteEventFromCalendar(context: Context, eventId: Long): Boolean {
        if (!hasCalendarPermission(context)) return false

        try {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
            val rows = context.contentResolver.delete(deleteUri, null, null)
            Log.d(TAG, "Deleted $rows calendar event rows for ID $eventId")
            return rows > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting calendar event: ${e.message}", e)
        }
        return false
    }
}
