package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val subject: String,
    val dueDate: Long,
    val reminderTime: Long? = null,
    val isCompleted: Boolean = false,
    val priority: Int = 1, // 0 = Low, 1 = Medium, 2 = High
    val estimatedMinutes: Int = 30,
    val completedAt: Long? = null,
    val reminderOffsetMinutes: Int = 15,
    val reminderRepeat: Boolean = false,
    val reminderSound: String = "Default",
    val calendarEventId: Long? = null,
    val ownerUsername: String = "guest"
)
