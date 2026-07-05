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
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import com.example.ui.RealtimeNotificationHelper

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

    // --- POMODORO & SETTINGS STATE FLOWS ---
    val focusMins = MutableStateFlow(25)
    val shortBreakMins = MutableStateFlow(5)
    val longBreakMins = MutableStateFlow(15)

    val currentSessionType = MutableStateFlow("Focus") // "Focus", "Short Break", "Long Break"
    val timerSecondsRemaining = MutableStateFlow(25 * 60)
    val timerTotalSeconds = MutableStateFlow(25 * 60)
    val isTimerRunning = MutableStateFlow(false)
    val selectedFocusTask = MutableStateFlow<Task?>(null)

    // Accessibility/UX configurations
    val isTimerFullscreen = MutableStateFlow(false)
    val playSoundOnComplete = MutableStateFlow(true)
    val fullScreenOnStartFocus = MutableStateFlow(true)
    
    // Custom Ringtone / Alarm states
    val selectedRingtoneName = MutableStateFlow("Default")
    val customRingtoneUri = MutableStateFlow<String?>(null)

    private var timerJob: Job? = null

    fun startTimer(context: Context) {
        if (isTimerRunning.value) return
        isTimerRunning.value = true
        
        if (fullScreenOnStartFocus.value) {
            isTimerFullscreen.value = true
        }

        saveTimerStateToPrefs(context)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isTimerRunning.value && timerSecondsRemaining.value > 0) {
                delay(1000L)
                timerSecondsRemaining.value -= 1
                
                // Save current timer countdown state periodically to handle exit
                val sharedPrefs = context.getSharedPreferences("study_hub_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putInt("pomodoro_seconds_remaining", timerSecondsRemaining.value).apply()
                
                if (timerSecondsRemaining.value == 0) {
                    onTimerComplete(context)
                }
            }
        }
    }

    fun pauseTimer(context: Context) {
        isTimerRunning.value = false
        timerJob?.cancel()
        
        val sharedPrefs = context.getSharedPreferences("study_hub_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean("pomodoro_is_running", false)
            .putLong("pomodoro_end_timestamp", 0L)
            .putInt("pomodoro_seconds_remaining", timerSecondsRemaining.value)
            .apply()
    }

    fun resetTimer(context: Context) {
        pauseTimer(context)
        val mins = when (currentSessionType.value) {
            "Focus" -> focusMins.value
            "Short Break" -> shortBreakMins.value
            "Long Break" -> longBreakMins.value
            else -> 25
        }
        timerTotalSeconds.value = mins * 60
        timerSecondsRemaining.value = mins * 60
        
        val sharedPrefs = context.getSharedPreferences("study_hub_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putInt("pomodoro_seconds_remaining", timerSecondsRemaining.value)
            .putInt("pomodoro_total_seconds", timerTotalSeconds.value)
            .apply()
    }

    fun restoreTimerState(context: Context) {
        val sharedPrefs = context.getSharedPreferences("study_hub_prefs", Context.MODE_PRIVATE)
        val isRunning = sharedPrefs.getBoolean("pomodoro_is_running", false)
        val endTimestamp = sharedPrefs.getLong("pomodoro_end_timestamp", 0L)
        
        focusMins.value = sharedPrefs.getInt("pomodoro_focus_mins", 25)
        shortBreakMins.value = sharedPrefs.getInt("pomodoro_short_break_mins", 5)
        longBreakMins.value = sharedPrefs.getInt("pomodoro_long_break_mins", 15)
        
        val sessionType = sharedPrefs.getString("pomodoro_session_type", "Focus") ?: "Focus"
        currentSessionType.value = sessionType
        
        val totalSecs = sharedPrefs.getInt("pomodoro_total_seconds", focusMins.value * 60)
        timerTotalSeconds.value = totalSecs
        
        playSoundOnComplete.value = sharedPrefs.getBoolean("pomodoro_play_sound", true)
        fullScreenOnStartFocus.value = sharedPrefs.getBoolean("pomodoro_fullscreen_on_start", true)
        selectedRingtoneName.value = sharedPrefs.getString("pomodoro_ringtone_name", "Default") ?: "Default"
        customRingtoneUri.value = sharedPrefs.getString("pomodoro_custom_ringtone_uri", null)

        val savedSeconds = sharedPrefs.getInt("pomodoro_seconds_remaining", focusMins.value * 60)

        if (isRunning && endTimestamp > System.currentTimeMillis()) {
            val remaining = ((endTimestamp - System.currentTimeMillis()) / 1000).toInt()
            if (remaining > 0) {
                timerSecondsRemaining.value = remaining
                isTimerRunning.value = true
                
                timerJob?.cancel()
                timerJob = viewModelScope.launch {
                    while (isTimerRunning.value && timerSecondsRemaining.value > 0) {
                        delay(1000L)
                        timerSecondsRemaining.value -= 1
                        
                        sharedPrefs.edit().putInt("pomodoro_seconds_remaining", timerSecondsRemaining.value).apply()
                        
                        if (timerSecondsRemaining.value == 0) {
                            onTimerComplete(context)
                        }
                    }
                }
            } else {
                timerSecondsRemaining.value = 0
                isTimerRunning.value = false
                onTimerComplete(context)
            }
        } else if (isRunning && endTimestamp <= System.currentTimeMillis() && endTimestamp > 0L) {
            timerSecondsRemaining.value = 0
            isTimerRunning.value = false
            onTimerComplete(context)
        } else {
            timerSecondsRemaining.value = savedSeconds
            isTimerRunning.value = false
        }
    }

    private fun saveTimerStateToPrefs(context: Context) {
        val sharedPrefs = context.getSharedPreferences("study_hub_prefs", Context.MODE_PRIVATE)
        val endTimestamp = if (isTimerRunning.value) {
            System.currentTimeMillis() + (timerSecondsRemaining.value * 1000L)
        } else {
            0L
        }
        
        sharedPrefs.edit()
            .putBoolean("pomodoro_is_running", isTimerRunning.value)
            .putLong("pomodoro_end_timestamp", endTimestamp)
            .putInt("pomodoro_seconds_remaining", timerSecondsRemaining.value)
            .putInt("pomodoro_total_seconds", timerTotalSeconds.value)
            .putString("pomodoro_session_type", currentSessionType.value)
            .putInt("pomodoro_focus_mins", focusMins.value)
            .putInt("pomodoro_short_break_mins", shortBreakMins.value)
            .putInt("pomodoro_long_break_mins", longBreakMins.value)
            .putBoolean("pomodoro_play_sound", playSoundOnComplete.value)
            .putBoolean("pomodoro_fullscreen_on_start", fullScreenOnStartFocus.value)
            .putString("pomodoro_ringtone_name", selectedRingtoneName.value)
            .putString("pomodoro_custom_ringtone_uri", customRingtoneUri.value)
            .apply()
    }

    private fun onTimerComplete(context: Context) {
        isTimerRunning.value = false
        timerJob?.cancel()
        
        if (playSoundOnComplete.value) {
            playRingtoneSound(context)
        }

        val type = currentSessionType.value
        if (type == "Focus") {
            val task = selectedFocusTask.value
            logFocusSession(
                taskId = task?.id,
                taskTitle = task?.title,
                durationMinutes = focusMins.value
            )
            
            currentSessionType.value = "Short Break"
            timerTotalSeconds.value = shortBreakMins.value * 60
            timerSecondsRemaining.value = shortBreakMins.value * 60
            
            RealtimeNotificationHelper.showNotification(
                context = context,
                title = "🏆 Study Session Completed",
                message = "Congratulations! You completed your ${focusMins.value} mins focus session."
            )
        } else {
            currentSessionType.value = "Focus"
            timerTotalSeconds.value = focusMins.value * 60
            timerSecondsRemaining.value = focusMins.value * 60
            
            RealtimeNotificationHelper.showNotification(
                context = context,
                title = "⏰ Break Completed",
                message = "Your break has ended. Let's start the next Focus session!"
            )
        }
        
        saveTimerStateToPrefs(context)
    }

    fun playRingtoneSound(context: Context) {
        val soundName = selectedRingtoneName.value
        val uriString = customRingtoneUri.value
        try {
            if (soundName == "Custom" && !uriString.isNullOrBlank()) {
                val mp = android.media.MediaPlayer().apply {
                    setDataSource(context, android.net.Uri.parse(uriString))
                    setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                    prepare()
                    start()
                }
                mp.setOnCompletionListener { it.release() }
            } else {
                when (soundName) {
                    "Digital Chime" -> {
                        val toneG = android.media.ToneGenerator(android.media.AudioManager.STREAM_ALARM, 100)
                        toneG.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
                    }
                    "Classic Bell" -> {
                        val toneG = android.media.ToneGenerator(android.media.AudioManager.STREAM_ALARM, 100)
                        toneG.startTone(android.media.ToneGenerator.TONE_CDMA_HIGH_L, 500)
                    }
                    "Gentle Harp" -> {
                        viewModelScope.launch {
                            val toneG = android.media.ToneGenerator(android.media.AudioManager.STREAM_ALARM, 85)
                            toneG.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 150)
                            delay(200)
                            toneG.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 150)
                            delay(200)
                            toneG.startTone(android.media.ToneGenerator.TONE_PROP_BEEP2, 250)
                        }
                    }
                    else -> {
                        val defaultUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                            ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                        val r = android.media.RingtoneManager.getRingtone(context, defaultUri)
                        r?.play()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val defaultUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                val r = android.media.RingtoneManager.getRingtone(context, defaultUri)
                r?.play()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun setCustomRingtone(context: Context, name: String, uri: String?) {
        selectedRingtoneName.value = name
        customRingtoneUri.value = uri
        saveTimerStateToPrefs(context)
    }

    fun updatePomodoroSettings(context: Context, focus: Int, short: Int, long: Int, playSound: Boolean, fsOnStart: Boolean) {
        focusMins.value = focus
        shortBreakMins.value = short
        longBreakMins.value = long
        playSoundOnComplete.value = playSound
        fullScreenOnStartFocus.value = fsOnStart
        
        if (!isTimerRunning.value) {
            val mins = when (currentSessionType.value) {
                "Focus" -> focus
                "Short Break" -> short
                "Long Break" -> long
                else -> focus
            }
            timerTotalSeconds.value = mins * 60
            timerSecondsRemaining.value = mins * 60
        }
        
        saveTimerStateToPrefs(context)
    }

    fun switchSessionMode(context: Context, mode: String) {
        pauseTimer(context)
        currentSessionType.value = mode
        val mins = when (mode) {
            "Focus" -> focusMins.value
            "Short Break" -> shortBreakMins.value
            "Long Break" -> longBreakMins.value
            else -> 25
        }
        timerTotalSeconds.value = mins * 60
        timerSecondsRemaining.value = mins * 60
        saveTimerStateToPrefs(context)
    }

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
