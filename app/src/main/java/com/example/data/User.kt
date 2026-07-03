package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val email: String,
    val passwordHash: String, // Plaintext or basic security string
    val createdAt: Long = System.currentTimeMillis()
)
