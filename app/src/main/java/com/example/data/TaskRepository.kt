package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao, private val userDao: UserDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksForUser(username: String): Flow<List<Task>> = taskDao.getTasksForUser(username)

    fun getFocusSessionsForUser(username: String): Flow<List<FocusSession>> = taskDao.getFocusSessionsForUser(username)

    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun registerUser(user: User): Long = userDao.registerUser(user)

    suspend fun getUserCount(): Int = userDao.getUserCount()

    suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)

    val allFocusSessions: Flow<List<FocusSession>> = taskDao.getAllFocusSessions()

    suspend fun insertFocusSession(session: FocusSession): Long = taskDao.insertFocusSession(session)

    suspend fun clearAllFocusSessions() = taskDao.clearAllFocusSessions()
}
