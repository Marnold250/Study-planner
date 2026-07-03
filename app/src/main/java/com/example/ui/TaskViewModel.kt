package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Task
import com.example.data.TaskRepository
import com.example.data.CalendarSyncHelper
import com.example.data.FocusSession
import com.example.receiver.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

enum class SortOption {
    DUE_DATE,
    PRIORITY,
    TITLE
}

enum class StatusFilter {
    ALL,
    PENDING,
    COMPLETED,
    OVERDUE
}

enum class RegistrationResult {
    SUCCESS,
    USERNAME_TAKEN,
    EMAIL_TAKEN,
    EMPTY_FIELDS
}

enum class LoginResult {
    SUCCESS,
    USER_NOT_FOUND,
    INCORRECT_PASSWORD,
    EMPTY_FIELDS
}

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val currentUser = MutableStateFlow("guest")

    fun setCurrentUser(username: String) {
        currentUser.value = username
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = currentUser.flatMapLatest { username ->
        repository.getTasksForUser(username)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val focusSessions: StateFlow<List<FocusSession>> = currentUser.flatMapLatest { username ->
        repository.getFocusSessionsForUser(username)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedSubject = MutableStateFlow<String?>(null)
    val selectedPriority = MutableStateFlow<Int?>(null)
    val statusFilter = MutableStateFlow(StatusFilter.ALL)
    val sortBy = MutableStateFlow(SortOption.DUE_DATE)

    // Reactive filtered list
    val filteredTasks: StateFlow<List<Task>> = combine(
        tasks,
        searchQuery,
        selectedSubject,
        selectedPriority,
        combine(statusFilter, sortBy) { status, sort -> Pair(status, sort) }
    ) { allTasks, query, subject, priority, statusSort ->
        val status = statusSort.first
        val sort = statusSort.second
        var list = allTasks

        // Apply Search
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        // Apply Subject Filter
        if (subject != null) {
            list = list.filter { it.subject.equals(subject, ignoreCase = true) }
        }

        // Apply Priority Filter
        if (priority != null) {
            list = list.filter { it.priority == priority }
        }

        // Apply Status Filter
        val now = System.currentTimeMillis()
        list = when (status) {
            StatusFilter.ALL -> list
            StatusFilter.PENDING -> list.filter { !it.isCompleted }
            StatusFilter.COMPLETED -> list.filter { it.isCompleted }
            StatusFilter.OVERDUE -> list.filter { !it.isCompleted && it.dueDate < now }
        }

        // Apply Sorting
        list = when (sort) {
            SortOption.DUE_DATE -> list.sortedWith(compareBy<Task> { it.isCompleted }.thenBy { it.dueDate })
            SortOption.PRIORITY -> list.sortedWith(compareBy<Task> { it.isCompleted }.thenByDescending { it.priority })
            SortOption.TITLE -> list.sortedWith(compareBy<Task> { it.isCompleted }.thenBy { it.title.lowercase() })
        }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactive subjects list
    val availableSubjects: StateFlow<List<String>> = tasks.map { allTasks ->
        allTasks.map { it.subject }.distinct().filter { it.isNotBlank() }.sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Statistics & Analytics
    val totalTasksCount: StateFlow<Int> = tasks.map { allTasks ->
        allTasks.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTasksCount: StateFlow<Int> = tasks.map { allTasks ->
        allTasks.count { it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingTasksCount: StateFlow<Int> = tasks.map { allTasks ->
        allTasks.count { !it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overdueTasksCount: StateFlow<Int> = tasks.map { allTasks ->
        val now = System.currentTimeMillis()
        allTasks.count { !it.isCompleted && it.dueDate < now }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStudyMinutesCompleted: StateFlow<Int> = tasks.map { allTasks ->
        allTasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Operations
    val selectedCalendarId = MutableStateFlow<Long?>(null)

    fun syncAllTasksWithCalendar(context: Context, calendarId: Long) {
        viewModelScope.launch {
            selectedCalendarId.value = calendarId
            val currentTasks = tasks.value
            for (task in currentTasks) {
                if (task.calendarEventId != null) {
                    val success = CalendarSyncHelper.updateEventInCalendar(context, task, calendarId, task.calendarEventId)
                    if (!success) {
                        val newEventId = CalendarSyncHelper.addEventToCalendar(context, task, calendarId)
                        if (newEventId != null) {
                            repository.updateTask(task.copy(calendarEventId = newEventId))
                        }
                    }
                } else {
                    val newEventId = CalendarSyncHelper.addEventToCalendar(context, task, calendarId)
                    if (newEventId != null) {
                        repository.updateTask(task.copy(calendarEventId = newEventId))
                    }
                }
            }
        }
    }

    fun disconnectCalendar() {
        selectedCalendarId.value = null
    }

    fun addTask(
        title: String,
        description: String,
        subject: String,
        dueDate: Long,
        priority: Int,
        estimatedMinutes: Int,
        reminderTime: Long?,
        reminderOffsetMinutes: Int,
        reminderRepeat: Boolean,
        reminderSound: String,
        context: Context
    ) {
        viewModelScope.launch {
            var task = Task(
                title = title,
                description = description,
                subject = subject,
                dueDate = dueDate,
                priority = priority,
                estimatedMinutes = estimatedMinutes,
                reminderTime = reminderTime,
                isCompleted = false,
                reminderOffsetMinutes = reminderOffsetMinutes,
                reminderRepeat = reminderRepeat,
                reminderSound = reminderSound,
                ownerUsername = currentUser.value
            )
            val generatedId = repository.insertTask(task)
            var savedTask = task.copy(id = generatedId.toInt())

            // Sync to Google Calendar if configured
            val calId = selectedCalendarId.value
            if (calId != null) {
                val eventId = CalendarSyncHelper.addEventToCalendar(context, savedTask, calId)
                if (eventId != null) {
                    savedTask = savedTask.copy(calendarEventId = eventId)
                    repository.updateTask(savedTask)
                }
            }

            if (reminderTime != null && reminderTime > System.currentTimeMillis()) {
                ReminderScheduler.schedule(context, savedTask)
            }
        }
    }

    fun updateTask(task: Task, context: Context) {
        viewModelScope.launch {
            var updatedTask = task
            val calId = selectedCalendarId.value
            if (calId != null) {
                if (task.calendarEventId != null) {
                    val success = CalendarSyncHelper.updateEventInCalendar(context, task, calId, task.calendarEventId)
                    if (!success) {
                        val newEventId = CalendarSyncHelper.addEventToCalendar(context, task, calId)
                        updatedTask = task.copy(calendarEventId = newEventId)
                    }
                } else {
                    val newEventId = CalendarSyncHelper.addEventToCalendar(context, task, calId)
                    updatedTask = task.copy(calendarEventId = newEventId)
                }
            }

            repository.updateTask(updatedTask)

            if (updatedTask.isCompleted) {
                ReminderScheduler.cancel(context, updatedTask.id)
            } else if (updatedTask.reminderTime != null) {
                ReminderScheduler.schedule(context, updatedTask)
            } else {
                ReminderScheduler.cancel(context, updatedTask.id)
            }
        }
    }

    fun toggleTaskCompletion(task: Task, context: Context) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updatedTask)

            // Update calendar event if present
            val calId = selectedCalendarId.value
            if (calId != null && updatedTask.calendarEventId != null) {
                CalendarSyncHelper.updateEventInCalendar(context, updatedTask, calId, updatedTask.calendarEventId)
            }

            if (updatedTask.isCompleted) {
                ReminderScheduler.cancel(context, task.id)
            } else if (updatedTask.reminderTime != null) {
                ReminderScheduler.schedule(context, updatedTask)
            }
        }
    }

    fun deleteTask(task: Task, context: Context) {
        viewModelScope.launch {
            if (task.calendarEventId != null) {
                CalendarSyncHelper.deleteEventFromCalendar(context, task.calendarEventId)
            }
            repository.deleteTask(task)
            ReminderScheduler.cancel(context, task.id)
        }
    }

    fun logFocusSession(taskId: Int?, taskTitle: String?, durationMinutes: Int, isCompleted: Boolean = true) {
        viewModelScope.launch {
            val session = FocusSession(
                taskId = taskId,
                taskTitle = taskTitle,
                durationMinutes = durationMinutes,
                isCompleted = isCompleted,
                ownerUsername = currentUser.value
            )
            repository.insertFocusSession(session)
        }
    }

    suspend fun registerUser(username: String, email: String, passwordHash: String): RegistrationResult {
        if (username.isBlank() || email.isBlank() || passwordHash.isBlank()) {
            return RegistrationResult.EMPTY_FIELDS
        }
        val existingUser = repository.getUserByUsername(username.trim())
        if (existingUser != null) {
            return RegistrationResult.USERNAME_TAKEN
        }
        val existingEmail = repository.getUserByEmail(email.trim())
        if (existingEmail != null) {
            return RegistrationResult.EMAIL_TAKEN
        }
        val newUser = com.example.data.User(
            username = username.trim(),
            email = email.trim(),
            passwordHash = passwordHash
        )
        repository.registerUser(newUser)
        return RegistrationResult.SUCCESS
    }

    suspend fun loginUser(username: String, passwordHash: String): LoginResult {
        if (username.isBlank() || passwordHash.isBlank()) {
            return LoginResult.EMPTY_FIELDS
        }
        val user = repository.getUserByUsername(username.trim()) ?: return LoginResult.USER_NOT_FOUND
        if (user.passwordHash != passwordHash) {
            return LoginResult.INCORRECT_PASSWORD
        }
        return LoginResult.SUCCESS
    }

    fun clearFocusSessionHistory() {
        viewModelScope.launch {
            repository.clearAllFocusSessions()
        }
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
