package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int? = null,
    val taskTitle: String? = null,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true,
    val ownerUsername: String = "guest"
)
