package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE ownerUsername = :username ORDER BY dueDate ASC")
    fun getTasksForUser(username: String): Flow<List<Task>>

    @Query("SELECT * FROM focus_sessions WHERE ownerUsername = :username ORDER BY timestamp DESC")
    fun getFocusSessionsForUser(username: String): Flow<List<FocusSession>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession): Long

    @Query("DELETE FROM focus_sessions")
    suspend fun clearAllFocusSessions()
}
