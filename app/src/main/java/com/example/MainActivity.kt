package com.example

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.media.RingtoneManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Switch
import com.example.data.CalendarSyncHelper
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.Task
import com.example.data.FocusSession
import com.example.data.TaskRepository
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.SortOption
import com.example.ui.StatusFilter
import com.example.ui.TaskViewModel
import com.example.ui.TaskViewModelFactory
import com.example.ui.OnboardingScreen
import com.example.ui.LoginScreen
import com.example.ui.CreatorInfoDialog
import com.example.ui.RealtimeNotificationHelper
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material3.RadioButton
import android.net.Uri
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.border
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create the notification channel
        createNotificationChannel()

        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao(), database.userDao())
        val viewModelFactory = TaskViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModelFactory)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "student_task_reminders"
            val channelName = "Task Study Reminders"
            val channelDescription = "Reminds students of upcoming study tasks and assignments"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}

// Global Formatter Helpers
fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatDateOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatTimeOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(factory: TaskViewModelFactory) {
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel(factory = factory)

    // Dynamic Permission requesting for POST_NOTIFICATIONS (Android 13+)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Study Reminders Enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied. Reminders will be silent.", Toast.LENGTH_LONG).show()
        }
    }

    var hasCalendarPermission by remember {
        mutableStateOf(CalendarSyncHelper.hasCalendarPermission(context))
    }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCalendarPermission = permissions.values.all { it }
        if (hasCalendarPermission) {
            Toast.makeText(context, "Calendar Sync Enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Calendar access denied. Sync disabled.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var currentTab by remember { mutableStateOf(0) } // 0 = Dashboard, 1 = Tasks, 2 = Analytics, 3 = Calendar
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val sharedPrefs = remember { context.getSharedPreferences("study_hub_prefs", Context.MODE_PRIVATE) }
    var hasCompletedOnboarding by remember {
        mutableStateOf(sharedPrefs.getBoolean("onboarding_completed", false))
    }
    var isLoggedIn by remember {
        mutableStateOf(sharedPrefs.getBoolean("user_logged_in", false))
    }

    val loggedInUsername = remember(isLoggedIn) {
        sharedPrefs.getString("logged_in_username", "guest") ?: "guest"
    }

    LaunchedEffect(isLoggedIn, loggedInUsername) {
        if (isLoggedIn) {
            viewModel.setCurrentUser(loggedInUsername)
        } else {
            viewModel.setCurrentUser("guest")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.restoreTimerState(context)
    }

    if (!hasCompletedOnboarding) {
        OnboardingScreen(
            onFinished = {
                sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                hasCompletedOnboarding = true
            }
        )
    } else if (!isLoggedIn) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { username ->
                sharedPrefs.edit()
                    .putBoolean("user_logged_in", true)
                    .putString("logged_in_username", username)
                    .apply()
                isLoggedIn = true
            }
        )
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth > 720.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 24.dp, horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "App Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Reminder",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                if (currentTab == 0 || currentTab == 1) {
                                    FloatingActionButton(
                                        onClick = { showAddDialog = true },
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.testTag("rail_fab_add_task")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("side_nav_rail")
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        NavigationRailItem(
                            selected = currentTab == 0,
                            onClick = { currentTab = 0 },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Study Hub") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_rail_dashboard")
                        )
                        NavigationRailItem(
                            selected = currentTab == 1,
                            onClick = { currentTab = 1 },
                            icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Tasks") },
                            label = { Text("My Tasks") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_rail_tasks")
                        )
                        NavigationRailItem(
                            selected = currentTab == 2,
                            onClick = { currentTab = 2 },
                            icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                            label = { Text("Analytics") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_rail_analytics")
                        )
                        NavigationRailItem(
                            selected = currentTab == 3,
                            onClick = { currentTab = 3 },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar Sync") },
                            label = { Text("Calendar") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_rail_calendar")
                        )
                        NavigationRailItem(
                            selected = currentTab == 4,
                            onClick = { currentTab = 4 },
                            icon = { Icon(Icons.Default.Timer, contentDescription = "Focus Timer") },
                            label = { Text("Focus") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_rail_focus")
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.weight(1f),
                    topBar = {
                        val appBarTitle = when (currentTab) {
                            0 -> "Student Task Reminder"
                            1 -> "My Assignments"
                            2 -> "Study Analytics"
                            3 -> "Calendar Sync"
                            4 -> "Focus Pomodoro"
                            5 -> "App Settings"
                            else -> "Student Task Reminder"
                        }
                        
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!isWideScreen && currentTab != 0) {
                                        Icon(
                                            imageVector = when (currentTab) {
                                                1 -> Icons.Default.FormatListBulleted
                                                2 -> Icons.Default.Analytics
                                                3 -> Icons.Default.CalendarMonth
                                                4 -> Icons.Default.Timer
                                                5 -> Icons.Default.Settings
                                                else -> Icons.Default.School
                                            },
                                            contentDescription = "Screen Icon",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Text(
                                        text = appBarTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            navigationIcon = {
                                if (currentTab == 5) {
                                    IconButton(onClick = { currentTab = 4 }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (currentTab == 4) {
                                    IconButton(
                                        onClick = { currentTab = 5 },
                                        modifier = Modifier.testTag("settings_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Settings",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    bottomBar = {
                        if (!isWideScreen) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == 0,
                                    onClick = { currentTab = 0 },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                    label = { Text("Study Hub") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_dashboard")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 1,
                                    onClick = { currentTab = 1 },
                                    icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Tasks") },
                                    label = { Text("My Tasks") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_tasks")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 2,
                                    onClick = { currentTab = 2 },
                                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                                    label = { Text("Analytics") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_analytics")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 3,
                                    onClick = { currentTab = 3 },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar Sync") },
                                    label = { Text("Calendar") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_calendar")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 4,
                                    onClick = { currentTab = 4 },
                                    icon = { Icon(Icons.Default.Timer, contentDescription = "Focus Timer") },
                                    label = { Text("Focus") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("nav_focus")
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (!isWideScreen && (currentTab == 0 || currentTab == 1)) {
                            FloatingActionButton(
                                onClick = { showAddDialog = true },
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .testTag("fab_add_task")
                                    .padding(bottom = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Task")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (currentTab) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                onTaskEdit = { task -> editingTask = task },
                                onTabChange = { currentTab = it },
                                hasNotificationPermission = hasNotificationPermission,
                                onRequestNotificationPermission = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            )
                            1 -> TasksListScreen(
                                viewModel = viewModel,
                                onTaskEdit = { task -> editingTask = task }
                            )
                            2 -> AnalyticsScreen(viewModel = viewModel)
                            3 -> CalendarSyncScreen(
                                viewModel = viewModel,
                                hasCalendarPermission = hasCalendarPermission,
                                onRequestCalendarPermission = {
                                    calendarPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.READ_CALENDAR,
                                            android.Manifest.permission.WRITE_CALENDAR
                                        )
                                    )
                                },
                                onTaskEdit = { task -> editingTask = task }
                            )
                            4 -> PomodoroTimerScreen(viewModel = viewModel)
                            5 -> SettingsScreen(
                                viewModel = viewModel,
                                loggedInUsername = loggedInUsername,
                                onLogout = {
                                    val userToLogout = loggedInUsername
                                    sharedPrefs.edit()
                                        .putBoolean("user_logged_in", false)
                                        .putString("logged_in_username", "guest")
                                        .apply()
                                    isLoggedIn = false
                                    viewModel.setCurrentUser("guest")
                                    currentTab = 0
                                    RealtimeNotificationHelper.showNotification(
                                        context = context,
                                        title = "Logged Out",
                                        message = "Goodbye, $userToLogout! You have been successfully logged out."
                                    )
                                }
                            )
                        }

                        // Dialog for Add Task
                        if (showAddDialog) {
                            TaskFormDialog(
                                onDismiss = { showAddDialog = false },
                                onSave = { title, desc, subject, dueDate, priority, mins, reminder, offsetMins, repeat, sound ->
                                    viewModel.addTask(title, desc, subject, dueDate, priority, mins, reminder, offsetMins, repeat, sound, context)
                                    showAddDialog = false
                                }
                            )
                        }

                        // Dialog for Edit Task
                        editingTask?.let { task ->
                            TaskFormDialog(
                                task = task,
                                onDismiss = { editingTask = null },
                                onSave = { title, desc, subject, dueDate, priority, mins, reminder, offsetMins, repeat, sound ->
                                    viewModel.updateTask(
                                        task.copy(
                                            title = title,
                                            description = desc,
                                            subject = subject,
                                            dueDate = dueDate,
                                            priority = priority,
                                            estimatedMinutes = mins,
                                            reminderTime = reminder,
                                            reminderOffsetMinutes = offsetMins,
                                            reminderRepeat = repeat,
                                            reminderSound = sound
                                        ),
                                        context
                                    )
                                    editingTask = null
                                }
                            )
                        }

                        // Dialog for Active Task Alarm Pop-up in the Center of the Screen
                        val activeAlarmId = com.example.receiver.AlarmSoundManager.activeAlarmTaskId.value
                        val activeAlarmTitle = com.example.receiver.AlarmSoundManager.activeAlarmTaskTitle.value
                        if (activeAlarmId != null) {
                            AlertDialog(
                                onDismissRequest = { /* Force explicit dismiss click */ },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Alarm Active",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                    )
                                },
                                title = {
                                    Text(
                                        text = "⏰ Task Alarm Triggered!",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                },
                                text = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = activeAlarmTitle ?: "Study Task Time!",
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Your scheduled study reminder has reached its time. Let's get focused!",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            com.example.receiver.AlarmSoundManager.stopAlarm()
                                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                                            notificationManager?.cancel(activeAlarmId)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        ),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("Dismiss Alarm", fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 1: DASHBOARD
// ==========================================
@Composable
fun DashboardScreen(
    viewModel: TaskViewModel,
    onTaskEdit: (Task) -> Unit,
    onTabChange: (Int) -> Unit,
    hasNotificationPermission: Boolean,
    onRequestNotificationPermission: () -> Unit
) {
    val context = LocalContext.current
    val totalMins by viewModel.totalStudyMinutesCompleted.collectAsState()
    val pendingCount by viewModel.pendingTasksCount.collectAsState()
    val overdueCount by viewModel.overdueTasksCount.collectAsState()
    val filteredTasks by viewModel.filteredTasks.collectAsState()

    // Get only top 3 high priority pending tasks
    val upcomingUrgentTasks = remember(filteredTasks) {
        filteredTasks.filter { !it.isCompleted && it.priority == 2 }.take(3)
    }

    val slides = remember {
        listOf(
            Triple(
                R.drawable.img_hero_banner,
                "KNOWLEDGE IS POWER",
                "Your focus determines your reality. Let's make progress today!"
            ),
            Triple(
                R.drawable.img_onboarding_focus_1783067327516,
                "STAY FOCUSED",
                "Block distractions and lock into your Pomodoro study flow."
            ),
            Triple(
                R.drawable.img_onboarding_organize_1783067310393,
                "ORGANIZE WORK",
                "Prioritize assignments, set deadlines, and achieve more."
            ),
            Triple(
                R.drawable.img_onboarding_sync_1783067343559,
                "CALENDAR SYNC",
                "Seamlessly sync your study reminders with your Google Calendar."
            )
        )
    }
    var currentSlideIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(4000)
            currentSlideIndex = (currentSlideIndex + 1) % slides.size
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 850.dp)
                .testTag("dashboard_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Student Task Reminder",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track assignments, prepare, achieve.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Notification Permission Banner (If denied/not allowed on API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alert",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Study Reminders Off",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Enable notifications so you never miss due times and exam prep alerts.",
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onRequestNotificationPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                contentColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Enable", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Hero Motivational Banner Card (Generously Spaced & Highly Polished)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { currentSlideIndex = (currentSlideIndex + 1) % slides.size },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.animation.Crossfade(
                        targetState = currentSlideIndex,
                        animationSpec = androidx.compose.animation.core.tween(1000),
                        label = "Banner Crossfade"
                    ) { index ->
                        val slide = slides[index]
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Hero Image Background
                            Image(
                                painter = painterResource(id = slide.first),
                                contentDescription = "Motivational student desk study scene",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay Dark Gradient for clear text overlay contrast
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f)
                                            ),
                                            startY = 50f
                                        )
                                    )
                            )

                            // Text & Callouts
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = slide.second,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp
                                    ),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = slide.third,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 20.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Stats Metrics Panel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Focus time completed Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onTabChange(2) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Focus Time",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "$totalMins",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "m",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        Text(
                            text = "Tasks completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }

                // Pending assignments Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onTabChange(1) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "$pendingCount",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "tasks",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        Text(
                            text = if (overdueCount > 0) "⚠️ $overdueCount overdue!" else "No overdue work",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (overdueCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = if (overdueCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Section Title: High Priority
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "High Priority Tasks",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { onTabChange(1) }) {
                    Text("See All", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // List of Urgent high-priority Tasks
        if (upcomingUrgentTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "All Clear!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No urgent high priority tasks. Keep up the excellent work!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 2
                        )
                    }
                }
            }
        } else {
            items(upcomingUrgentTasks, key = { it.id }) { task ->
                TaskListItem(
                    task = task,
                    onToggleComplete = { viewModel.toggleTaskCompletion(task, context) },
                    onDelete = { viewModel.deleteTask(task, context) },
                    onClick = { onTaskEdit(task) }
                )
            }
        }
    }
}
}

// ==========================================
// SCREEN 2: ALL TASKS LIST
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasksListScreen(viewModel: TaskViewModel, onTaskEdit: (Task) -> Unit) {
    val context = LocalContext.current
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedPriority by viewModel.selectedPriority.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var showFilterMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 850.dp)
                .testTag("tasks_list_screen")
        ) {
        // Search & Quick Filter Block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "My Assignments",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )

            // Search textfield & Sort in a Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_tasks_input"),
                    placeholder = { Text("Search title, notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Sort Dropdown button Box
                Box {
                    IconButton(
                        onClick = { showFilterMenu = !showFilterMenu },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Sort Option Menu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        Text(
                            text = "  Sort By",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("Due Date ${if (sortBy == SortOption.DUE_DATE) "✓" else ""}") },
                            onClick = {
                                viewModel.sortBy.value = SortOption.DUE_DATE
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Priority ${if (sortBy == SortOption.PRIORITY) "✓" else ""}") },
                            onClick = {
                                viewModel.sortBy.value = SortOption.PRIORITY
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Title ${if (sortBy == SortOption.TITLE) "✓" else ""}") },
                            onClick = {
                                viewModel.sortBy.value = SortOption.TITLE
                                showFilterMenu = false
                            }
                        )

                        DropdownMenuSeparator()

                        Text(
                            text = "  Filter Priority",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("All Priorities ${if (selectedPriority == null) "✓" else ""}") },
                            onClick = {
                                viewModel.selectedPriority.value = null
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("High ${if (selectedPriority == 2) "✓" else ""}") },
                            onClick = {
                                viewModel.selectedPriority.value = 2
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Medium ${if (selectedPriority == 1) "✓" else ""}") },
                            onClick = {
                                viewModel.selectedPriority.value = 1
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Low ${if (selectedPriority == 0) "✓" else ""}") },
                            onClick = {
                                viewModel.selectedPriority.value = 0
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            // Status Filter TabRow (Responsive ScrollableTabRow)
            androidx.compose.material3.ScrollableTabRow(
                selectedTabIndex = when (statusFilter) {
                    StatusFilter.ALL -> 0
                    StatusFilter.PENDING -> 1
                    StatusFilter.COMPLETED -> 2
                    StatusFilter.OVERDUE -> 3
                },
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().testTag("tasks_status_tabs")
            ) {
                val tabsList = listOf(
                    StatusFilter.ALL to "All",
                    StatusFilter.PENDING to "Pending",
                    StatusFilter.COMPLETED to "Completed",
                    StatusFilter.OVERDUE to "Overdue"
                )
                tabsList.forEachIndexed { index, (filter, label) ->
                    Tab(
                        selected = statusFilter == filter,
                        onClick = { viewModel.statusFilter.value = filter },
                        text = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (statusFilter == filter) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    )
                }
            }

            // Subject Tag Row (Active Filters)
            if (availableSubjects.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SubjectFilterTag(
                        subject = "All Courses",
                        selected = selectedSubject == null,
                        onClick = { viewModel.selectedSubject.value = null }
                    )
                    availableSubjects.forEach { subject ->
                        SubjectFilterTag(
                            subject = subject,
                            selected = selectedSubject?.equals(subject, ignoreCase = true) == true,
                            onClick = { viewModel.selectedSubject.value = subject }
                        )
                    }
                }
            }
        }

        // Active Tasks Grid list
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (filteredTasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Search empty state",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Tasks Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try adjusting your search criteria or add a new assignment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskListItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task, context) },
                            onDelete = { viewModel.deleteTask(task, context) },
                            onClick = { onTaskEdit(task) }
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun DropdownMenuSeparator() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            .padding(vertical = 4.dp)
    )
}

// Minimal Chip for Status Select
@Composable
fun StatusFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

// Tag filter for Subjects
@Composable
fun SubjectFilterTag(subject: String, selected: Boolean, onClick: () -> Unit) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = subject,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

// ==========================================
// COMPOSABLE: ANIMATED TASK CHECKBOX (HIGH POLISH)
// ==========================================
@Composable
fun AnimatedTaskCheckbox(
    checked: Boolean,
    isLoading: Boolean,
    isHighPriority: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val checkColor = if (isHighPriority) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.primary
    }

    val uncheckedColor = if (isHighPriority) {
        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.outline
    }

    val containerColor by animateColorAsState(
        targetValue = if (checked) checkColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(26.dp)
            .clip(CircleShape)
            .border(
                width = if (checked) 0.dp else 2.dp,
                color = if (checked) Color.Transparent else uncheckedColor,
                shape = CircleShape
            )
            .background(containerColor)
            .clickable(enabled = !isLoading, onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = if (checked) MaterialTheme.colorScheme.onPrimary else checkColor,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(16.dp)
            )
        } else {
            androidx.compose.animation.AnimatedVisibility(
                visible = checked,
                enter = fadeIn() + scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = if (isHighPriority) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ==========================================
// COMPOSABLE: TASK LIST ITEM CARD (HIGH POLISH)
// ==========================================
@Composable
fun TaskListItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isUpdating by remember(task.id, task.isCompleted) { mutableStateOf(false) }

    val checkCelebrationScale = remember { Animatable(1f) }
    LaunchedEffect(task.isCompleted) {
        if (task.isCompleted) {
            checkCelebrationScale.animateTo(
                targetValue = 1.05f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
            checkCelebrationScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium)
            )
        }
    }

    val cardAlpha = if (task.isCompleted) 0.65f else 1f
    val animatedAlpha by animateFloatAsState(
        targetValue = cardAlpha,
        animationSpec = tween(durationMillis = 350)
    )

    val isOverdue = !task.isCompleted && task.dueDate < System.currentTimeMillis()
    val isHighPriorityActive = task.priority == 2 && !task.isCompleted

    val priorityColor = when (task.priority) {
        2 -> MaterialTheme.colorScheme.error
        1 -> Color(0xFFF59E0B) // Amber for Medium
        else -> MaterialTheme.colorScheme.primary
    }

    val priorityLabel = when (task.priority) {
        2 -> "High"
        1 -> "Medium"
        else -> "Low"
    }

    // Adapt background and border colors according to the Vibrant Palette theme guidelines
    val cardBg = if (isHighPriorityActive) {
        MaterialTheme.colorScheme.errorContainer
    } else if (task.isCompleted) {
        MaterialTheme.colorScheme.surface.copy(alpha = cardAlpha)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val cardBgAnimated by animateColorAsState(
        targetValue = cardBg,
        animationSpec = tween(durationMillis = 350)
    )

    val cardBorder = if (isHighPriorityActive) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f))
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }

    val titleColor = if (isHighPriorityActive) {
        MaterialTheme.colorScheme.onErrorContainer
    } else if (task.isCompleted) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val descriptionColor = if (isHighPriorityActive) {
        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
    } else if (task.isCompleted) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(checkCelebrationScale.value)
            .graphicsLayer(alpha = animatedAlpha)
            .clickable(onClick = onClick)
            .testTag("task_card_item_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = cardBgAnimated
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp
        ),
        shape = RoundedCornerShape(24.dp),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Priority Indicators color ribbon strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .drawBehind {
                        drawRect(priorityColor)
                    }
                    .align(Alignment.CenterVertically)
            )

            // Content Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Checkbox with standard target height and animations
                AnimatedTaskCheckbox(
                    checked = task.isCompleted,
                    isLoading = isUpdating,
                    isHighPriority = isHighPriorityActive,
                    onToggle = {
                        coroutineScope.launch {
                            isUpdating = true
                            delay(500) // Aesthetic delay simulating database saving progress
                            onToggleComplete()
                            isUpdating = false
                        }
                    },
                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Mid Texts Block
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Subject Capsule tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.subject,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Priority Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(priorityColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = priorityLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        }

                        // Estimations Hour/Min
                        Text(
                            text = "⏳ ${task.estimatedMinutes}m",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = descriptionColor.copy(alpha = 0.7f)
                        )
                    }

                    // Task Title
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        ),
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Task Description (Optional render if not empty)
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = descriptionColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Date & Alarm details footer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Due Date",
                            tint = if (isOverdue) MaterialTheme.colorScheme.error else descriptionColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Due: ${formatDateTime(task.dueDate)}",
                            fontSize = 10.sp,
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOverdue) MaterialTheme.colorScheme.error else descriptionColor.copy(alpha = 0.6f)
                        )

                        if (task.reminderTime != null && !task.isCompleted) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alarm Active",
                                tint = if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Reminder set",
                                fontSize = 10.sp,
                                color = if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Delete Action trigger Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_task_button_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// SCREEN 3: ANALYTICS (EXQUISITE COMPANION LAYOUT)
// ==========================================
@Composable
fun AnalyticsScreen(viewModel: TaskViewModel) {
    val totalTasks by viewModel.totalTasksCount.collectAsState()
    val completedCount by viewModel.completedTasksCount.collectAsState()
    val pendingCount by viewModel.pendingTasksCount.collectAsState()
    val overdueCount by viewModel.overdueTasksCount.collectAsState()
    val focusMins by viewModel.totalStudyMinutesCompleted.collectAsState()
    val tasksList by viewModel.tasks.collectAsState()

    // Calculate Completion Rate percentage
    val completionPercentage = remember(totalTasks, completedCount) {
        if (totalTasks == 0) 0f else (completedCount.toFloat() / totalTasks) * 100f
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 850.dp)
                .testTag("analytics_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Stats title banner
        item {
            Column {
                Text(
                    text = "Academic Analytics",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Insight into your studying patterns and focus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        // Circular doughnut progress visualization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Semester Completion",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "You finished $completedCount out of $totalTasks study targets.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔥 Focus Time: ${focusMins}m",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Dynamic Custom Canvas Ring Chart
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .weight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        val animatedStrokePercentage by animateFloatAsState(
                            targetValue = completionPercentage / 100f,
                            animationSpec = tween(durationMillis = 1000)
                        )

                        val trackColor = MaterialTheme.colorScheme.surfaceVariant
                        val progressColor = MaterialTheme.colorScheme.primary

                        Canvas(modifier = Modifier.size(90.dp)) {
                            // Track circle
                            drawCircle(
                                color = trackColor,
                                radius = size.minDimension / 2,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Progress arc
                            drawArc(
                                color = progressColor,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedStrokePercentage,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${completionPercentage.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        // Metrics breakdown Grid panel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Activity Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalyticsMiniCard(
                        modifier = Modifier.weight(1f),
                        label = "Completed",
                        value = "$completedCount",
                        color = MaterialTheme.colorScheme.primary
                    )
                    AnalyticsMiniCard(
                        modifier = Modifier.weight(1f),
                        label = "Pending",
                        value = "$pendingCount",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    AnalyticsMiniCard(
                        modifier = Modifier.weight(1f),
                        label = "Overdue",
                        value = "$overdueCount",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Horizontal Stats Bar Graph of subjects (Drawn natively with pure Compose Box layouts)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Courses Task Workload",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    val subjectsGroupedCount = remember(tasksList) {
                        tasksList.groupBy { it.subject }
                            .mapValues { entry -> entry.value.size }
                            .toList()
                            .sortedByDescending { it.second }
                            .take(5)
                    }

                    if (subjectsGroupedCount.isEmpty()) {
                        Text(
                            text = "No course task data yet. Build tasks with subjects assigned to view charts here.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    } else {
                        val maxCount = remember(subjectsGroupedCount) {
                            subjectsGroupedCount.maxOfOrNull { it.second } ?: 1
                        }

                        subjectsGroupedCount.forEach { (subject, count) ->
                            val barWidthPercentage = count.toFloat() / maxCount.toFloat()

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (subject.isBlank()) "Uncategorized" else subject,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "$count ${if (count == 1) "task" else "tasks"}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }

                                // Bar graphic
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(barWidthPercentage)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun AnalyticsMiniCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 20.sp,
                color = color,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ==========================================
// DIALOG: ADD/EDIT FORM (HIGH POLISH & NATIVE PICKERS)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskFormDialog(
    task: Task? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        subject: String,
        dueDate: Long,
        priority: Int,
        estimatedMinutes: Int,
        reminderTime: Long?,
        reminderOffsetMinutes: Int,
        reminderRepeat: Boolean,
        reminderSound: String
    ) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var subject by remember { mutableStateOf(task?.subject ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: 1) } // 0 = Low, 1 = Med, 2 = High
    var estimatedMinutes by remember { mutableStateOf(task?.estimatedMinutes?.toString() ?: "30") }

    val calendar = remember {
        Calendar.getInstance().apply {
            if (task != null) {
                timeInMillis = task.dueDate
            } else {
                add(Calendar.DAY_OF_YEAR, 1) // default tomorrow
            }
        }
    }

    var selectedDueDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showReminderTime by remember { mutableStateOf(task?.reminderTime != null) }

    var reminderOffsetMinutes by remember { mutableStateOf(task?.reminderOffsetMinutes ?: 15) }
    var reminderRepeat by remember { mutableStateOf(task?.reminderRepeat ?: false) }
    var reminderSound by remember { mutableStateOf(task?.reminderSound ?: "Default") }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            reminderSound = it.toString()
            Toast.makeText(context, "Set custom sound: ${getFileName(context, it) ?: "Selected Sound"}", Toast.LENGTH_SHORT).show()
        }
    }

    val reminderCalendar = remember {
        Calendar.getInstance().apply {
            if (task?.reminderTime != null) {
                timeInMillis = task.reminderTime
            } else {
                timeInMillis = calendar.timeInMillis
                add(Calendar.HOUR_OF_DAY, -1) // default 1 hour before due
            }
        }
    }
    var selectedReminderTime by remember { mutableStateOf(task?.reminderTime ?: reminderCalendar.timeInMillis) }

    val commonSubjects = listOf("Mathematics", "Physics", "Computer Science", "Chemistry", "Biology", "English Literature", "History")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (task == null) "New Assignment" else "Edit Assignment",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Please enter a task title", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (subject.isBlank()) {
                        Toast.makeText(context, "Please select or type a subject", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val mins = estimatedMinutes.toIntOrNull() ?: 30

                    onSave(
                        title,
                        description,
                        subject,
                        selectedDueDate,
                        priority,
                        mins,
                        if (showReminderTime) {
                            if (reminderOffsetMinutes != -1) {
                                selectedDueDate - (reminderOffsetMinutes * 60 * 1000L)
                            } else {
                                selectedReminderTime
                            }
                        } else null,
                        reminderOffsetMinutes,
                        reminderRepeat,
                        reminderSound
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_form_dialog_column"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title") },
                        placeholder = { Text("e.g., Midterm Exam Prep, Calculus HW") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Description
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notes / Study Guides") },
                        placeholder = { Text("e.g., Read chapters 4-5. Practice problem sets...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_desc_input"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Course / Subject Auto-Chip Selection or Custom Input
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Course / Subject") },
                            placeholder = { Text("e.g., Physics, Literature") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_subject_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = "Quick Select Course:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            commonSubjects.forEach { name ->
                                val isSelected = subject.equals(name, ignoreCase = true)
                                val containerColor by animateColorAsState(
                                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(containerColor)
                                        .clickable { subject = name }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Priority segmented selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Priority Level",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0, 1, 2).forEach { pLevel ->
                                val pLabel = when (pLevel) {
                                    2 -> "High 🔴"
                                    1 -> "Med 🟡"
                                    else -> "Low 🔵"
                                }
                                val pActiveColor = when (pLevel) {
                                    2 -> Color(0xFFEF4444)
                                    1 -> Color(0xFFF59E0B)
                                    else -> Color(0xFF3B82F6)
                                }
                                val isSelected = priority == pLevel
                                val containerColor = if (isSelected) pActiveColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                val strokeColor = if (isSelected) pActiveColor else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(containerColor)
                                        .clickable { priority = pLevel }
                                        .drawBehind {
                                            if (isSelected) {
                                                drawRoundRect(
                                                    color = strokeColor,
                                                    style = Stroke(width = 2.dp.toPx())
                                                )
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) pActiveColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Due Date Pickers
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Due Date & Time",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Date selector
                            OutlinedButton(
                                onClick = {
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            calendar.set(Calendar.YEAR, y)
                                            calendar.set(Calendar.MONTH, m)
                                            calendar.set(Calendar.DAY_OF_MONTH, d)
                                            selectedDueDate = calendar.timeInMillis
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", modifier = Modifier.size(16.dp))
                                    Text(formatDateOnly(selectedDueDate), fontSize = 11.sp, maxLines = 1)
                                }
                            }

                            // Time selector
                            OutlinedButton(
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, h, min ->
                                            calendar.set(Calendar.HOUR_OF_DAY, h)
                                            calendar.set(Calendar.MINUTE, min)
                                            calendar.set(Calendar.SECOND, 0)
                                            selectedDueDate = calendar.timeInMillis
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        false
                                    ).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick Time", modifier = Modifier.size(16.dp))
                                    Text(formatTimeOnly(selectedDueDate), fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                // Estimated minutes duration
                item {
                    OutlinedTextField(
                        value = estimatedMinutes,
                        onValueChange = { estimatedMinutes = it },
                        label = { Text("Estimated Study Duration (minutes)") },
                        placeholder = { Text("e.g., 30, 45, 60, 120") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_minutes_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Reminder notification switch
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (showReminderTime) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Notifications Switch icon",
                                    tint = if (showReminderTime) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Set Study Reminder",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Checkbox(
                                checked = showReminderTime,
                                onCheckedChange = { showReminderTime = it },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        AnimatedVisibility(
                            visible = showReminderTime,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                val offsets = listOf(
                                    Pair(0, "At due time"),
                                    Pair(5, "5 min before"),
                                    Pair(15, "15 min before"),
                                    Pair(30, "30 min before"),
                                    Pair(60, "1 hr before"),
                                    Pair(120, "2 hr before"),
                                    Pair(1440, "1 day before"),
                                    Pair(-1, "Custom Time")
                                )
                                val sounds = listOf("Default", "Digital Chime", "Classic Bell", "Gentle Harp")

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Time Before Due Date:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        offsets.forEach { (mins, label) ->
                                            val isSelected = (mins == reminderOffsetMinutes)
                                            val containerColor by animateColorAsState(
                                                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(containerColor)
                                                    .clickable {
                                                        reminderOffsetMinutes = mins
                                                        if (mins != -1) {
                                                            selectedReminderTime = selectedDueDate - (mins * 60 * 1000L)
                                                        }
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 10.sp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }

                                if (reminderOffsetMinutes == -1) {
                                    // Custom date time picker
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Reminder Date picker
                                        OutlinedButton(
                                            onClick = {
                                                DatePickerDialog(
                                                    context,
                                                    { _, y, m, d ->
                                                        reminderCalendar.set(Calendar.YEAR, y)
                                                        reminderCalendar.set(Calendar.MONTH, m)
                                                        reminderCalendar.set(Calendar.DAY_OF_MONTH, d)
                                                        selectedReminderTime = reminderCalendar.timeInMillis
                                                    },
                                                    reminderCalendar.get(Calendar.YEAR),
                                                    reminderCalendar.get(Calendar.MONTH),
                                                    reminderCalendar.get(Calendar.DAY_OF_MONTH)
                                                ).show()
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Text(formatDateOnly(selectedReminderTime), fontSize = 10.sp, maxLines = 1)
                                        }

                                        // Reminder Time picker
                                        OutlinedButton(
                                            onClick = {
                                                TimePickerDialog(
                                                    context,
                                                    { _, h, min ->
                                                        reminderCalendar.set(Calendar.HOUR_OF_DAY, h)
                                                        reminderCalendar.set(Calendar.MINUTE, min)
                                                        reminderCalendar.set(Calendar.SECOND, 0)
                                                        selectedReminderTime = reminderCalendar.timeInMillis
                                                    },
                                                    reminderCalendar.get(Calendar.HOUR_OF_DAY),
                                                    reminderCalendar.get(Calendar.MINUTE),
                                                    false
                                                ).show()
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Text(formatTimeOnly(selectedReminderTime), fontSize = 10.sp, maxLines = 1)
                                        }
                                    }
                                }

                                // Sound Selection
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Notification Sound:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        sounds.forEach { name ->
                                            val isSelected = (name == reminderSound)
                                            val containerColor by animateColorAsState(
                                                targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(containerColor)
                                                    .clickable { reminderSound = name }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = name,
                                                    fontSize = 10.sp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }

                                        // Custom sound picker option
                                        val isCustomSelected = reminderSound.startsWith("content://") || reminderSound.startsWith("file://") || reminderSound.startsWith("android.resource://") || reminderSound == "Custom"
                                        val customLabel = if (isCustomSelected && reminderSound.startsWith("content://")) {
                                            try {
                                                getFileName(context, android.net.Uri.parse(reminderSound)) ?: "Custom Sound"
                                            } catch (e: Exception) {
                                                "Custom Sound"
                                            }
                                        } else {
                                            "Custom..."
                                        }
                                        val customContainerColor by animateColorAsState(
                                            targetValue = if (isCustomSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(customContainerColor)
                                                .clickable {
                                                    try {
                                                        audioPickerLauncher.launch("audio/*")
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Error opening file picker", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = "Pick sound",
                                                    modifier = Modifier.size(12.dp),
                                                    tint = if (isCustomSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = customLabel,
                                                    fontSize = 10.sp,
                                                    color = if (isCustomSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }

                                // Repeat toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Repeat Reminder Daily",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                    Switch(
                                        checked = reminderRepeat,
                                        onCheckedChange = { reminderRepeat = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CalendarSyncScreen(
    viewModel: TaskViewModel,
    hasCalendarPermission: Boolean,
    onRequestCalendarPermission: () -> Unit,
    onTaskEdit: (Task) -> Unit
) {
    val context = LocalContext.current
    val selectedCalendarId by viewModel.selectedCalendarId.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    // Query available calendars if permission is granted
    val calendars = remember(hasCalendarPermission) {
        if (hasCalendarPermission) {
            CalendarSyncHelper.getAvailableCalendars(context)
        } else {
            emptyList()
        }
    }

    var selectedCal by remember(selectedCalendarId, calendars) {
        mutableStateOf(calendars.find { it.id == selectedCalendarId })
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 850.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            Text(
                text = "Calendar Reminder Companion",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Interactive visual calendar mapping your tasks to days with colorful indicators. Sync with Google Calendar for automatic device updates.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            MonthlyCalendarView(
                tasks = tasks,
                onTaskClick = onTaskEdit
            )
        }

        if (!hasCalendarPermission) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar Warning",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Calendar Access Required",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "To synchronize your study tasks with Google Calendar, the app requires read and write calendar permissions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onRequestCalendarPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Grant Access")
                        }
                    }
                }
            }
        } else {
            // Permission is granted
            val isLinked = selectedCalendarId != null
            val linkedCalendar = calendars.find { it.id == selectedCalendarId }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLinked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isLinked) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Sync State Icon",
                                    tint = if (isLinked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isLinked) "Google Calendar Linked" else "Calendar Not Linked",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (isLinked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Active Sync",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (isLinked && linkedCalendar != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Connected Calendar: ${linkedCalendar.displayName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Google Account: ${linkedCalendar.accountName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val syncedCount = tasks.count { it.calendarEventId != null }
                                Text(
                                    text = "Synced Tasks: $syncedCount / ${tasks.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.syncAllTasksWithCalendar(context, linkedCalendar.id)
                                        Toast.makeText(context, "Synchronization Complete!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Sync, contentDescription = "Sync Now", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sync Now", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.disconnectCalendar()
                                        Toast.makeText(context, "Calendar Disconnected", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Disconnect", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "Select a Google/system calendar below to begin automatic background synchronization.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (calendars.isEmpty()) {
                                Text(
                                    text = "No calendars found on this device. Please make sure a Google account is logged in and calendar sync is active.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                var expanded by remember { mutableStateOf(false) }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Available Calendars:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Box {
                                        OutlinedButton(
                                            onClick = { expanded = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(selectedCal?.let { "${it.displayName} (${it.accountName})" } ?: "Select Calendar")
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier.fillMaxWidth(0.9f)
                                        ) {
                                            calendars.forEach { cal ->
                                                DropdownMenuItem(
                                                    text = { Text("${cal.displayName} (${cal.accountName})") },
                                                    onClick = {
                                                        selectedCal = cal
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            selectedCal?.let { cal ->
                                                viewModel.syncAllTasksWithCalendar(context, cal.id)
                                                Toast.makeText(context, "Google Calendar connected and synced!", Toast.LENGTH_SHORT).show()
                                            } ?: Toast.makeText(context, "Please select a calendar first", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Connect & Sync Now")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLinked) {
                item {
                    Text(
                        text = "Synchronized Study Events",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                val syncedTasks = tasks.filter { it.calendarEventId != null }
                if (syncedTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No synchronized events yet. Use 'Sync Now' or add a new task to push it to Google Calendar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(syncedTasks) { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Due: " + formatDateOnly(task.dueDate) + " at " + formatTimeOnly(task.dueDate),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Synced State",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Synced",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun PomodoroTimerScreen(viewModel: TaskViewModel) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()

    // Pomodoro settings and states collected directly from ViewModel for background persistence
    val focusMins by viewModel.focusMins.collectAsState()
    val shortBreakMins by viewModel.shortBreakMins.collectAsState()
    val longBreakMins by viewModel.longBreakMins.collectAsState()
    
    val currentSessionType by viewModel.currentSessionType.collectAsState()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val timerTotalSeconds by viewModel.timerTotalSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val selectedTask by viewModel.selectedFocusTask.collectAsState()
    val isTimerFullscreen by viewModel.isTimerFullscreen.collectAsState()
    val playSoundOnComplete by viewModel.playSoundOnComplete.collectAsState()
    val fullScreenOnStartFocus by viewModel.fullScreenOnStartFocus.collectAsState()

    // Task dropdown association selection
    var dropdownExpanded by remember { mutableStateOf(false) }

    // UI configuration
    var showSettings by remember { mutableStateOf(false) }

    // Local sliders state for AlertDialog editing (temp values before save)
    var editFocusMins by remember { mutableStateOf(focusMins) }
    var editShortBreakMins by remember { mutableStateOf(shortBreakMins) }
    var editLongBreakMins by remember { mutableStateOf(longBreakMins) }
    var editPlaySoundOnComplete by remember { mutableStateOf(playSoundOnComplete) }
    var editFullScreenOnStartFocus by remember { mutableStateOf(fullScreenOnStartFocus) }

    // Sync temp values whenever settings dialog is shown
    LaunchedEffect(showSettings) {
        if (showSettings) {
            editFocusMins = focusMins
            editShortBreakMins = shortBreakMins
            editLongBreakMins = longBreakMins
            editPlaySoundOnComplete = playSoundOnComplete
            editFullScreenOnStartFocus = fullScreenOnStartFocus
        }
    }

    // Active color scheme based on current session mode
    val activeColor = when (currentSessionType) {
        "Focus" -> MaterialTheme.colorScheme.error // Red/coral
        "Short Break" -> MaterialTheme.colorScheme.primary // Green/Teal
        "Long Break" -> MaterialTheme.colorScheme.tertiary // Blue
        else -> MaterialTheme.colorScheme.primary
    }

    val activeContainerColor = when (currentSessionType) {
        "Focus" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        "Short Break" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        "Long Break" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    }

    // Calculate formatting
    val minutesText = String.format("%02d", timerSecondsRemaining / 60)
    val secondsText = String.format("%02d", timerSecondsRemaining % 60)
    val progressFraction = if (timerTotalSeconds > 0) {
        timerSecondsRemaining.toFloat() / timerTotalSeconds.toFloat()
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(500),
        label = "Timer Progress"
    )

    // Pending tasks for selection
    val pendingTasks = remember(tasks) { tasks.filter { !it.isCompleted } }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 850.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Hero Banner
            item {
                var showCreatorDialog by remember { mutableStateOf(false) }

                if (showCreatorDialog) {
                    CreatorInfoDialog(onDismiss = { showCreatorDialog = false })
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Focus Timer",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Button(
                            onClick = { showCreatorDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("see_us_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "See Us icon",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "See Us",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Boost your academic performance using the classic Pomodoro Technique.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Mode Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf("Focus", "Short Break", "Long Break")
                        modes.forEach { mode ->
                            val isSelected = currentSessionType == mode
                            val btnColor = if (isSelected) activeColor else Color.Transparent
                            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(btnColor)
                                    .clickable { viewModel.switchSessionMode(context, mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }

            // 3. Task Association Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Focused Study Task",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Task association",
                                tint = activeColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (selectedTask != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(activeContainerColor, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedTask!!.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Subject: ${selectedTask!!.subject} • Est. ${selectedTask!!.estimatedMinutes}m",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.selectedFocusTask.value = null }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear selected task",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else {
                            // Dropdown selection trigger
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { dropdownExpanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Select a study task to associate...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Open dropdown",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    if (pendingTasks.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No pending study tasks found") },
                                            onClick = { dropdownExpanded = false },
                                            enabled = false
                                        )
                                    } else {
                                        pendingTasks.forEach { task ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(task.title, fontWeight = FontWeight.Bold)
                                                        Text("Subject: ${task.subject}", fontSize = 11.sp, color = Color.Gray)
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.selectedFocusTask.value = task
                                                    dropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Circular Progress Indicator Timer
            item {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background Track Circle and Progress Circle in Canvas
                    Canvas(modifier = Modifier.size(210.dp)) {
                        // Track Circle
                        drawCircle(
                            color = activeColor.copy(alpha = 0.12f),
                            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Active progress ring arc
                        drawArc(
                            color = activeColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$minutesText:$secondsText",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentSessionType) {
                                "Focus" -> "FOCUS SESSION"
                                "Short Break" -> "SHORT BREAK"
                                "Long Break" -> "LONG BREAK"
                                else -> "READY"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = activeColor
                        )
                    }
                }
            }

            // 5. Timer Controls Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset Button
                    IconButton(
                        onClick = { viewModel.resetTimer(context) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Reset Timer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Play / Pause FAB
                    FloatingActionButton(
                        onClick = {
                            if (isTimerRunning) {
                                viewModel.pauseTimer(context)
                            } else {
                                viewModel.startTimer(context)
                            }
                        },
                        containerColor = activeColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isTimerRunning) "Pause focus" else "Start focus",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Settings Button (durations shortcut)
                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Timer settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 6. Stats & History Overview Cards
            item {
                val totalSessions = focusSessions.size
                val totalFocusMins = focusSessions.sumOf { it.durationMinutes }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalSessions",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Sessions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${totalFocusMins}m",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Focus Time",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val streak = remember(focusSessions) { calculateFocusStreak(focusSessions) }
                            Text(
                                text = "$streak",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Streak Days",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 7. Focus History List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Focus Sessions Log",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (focusSessions.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.clickable { viewModel.clearFocusSessionHistory() }
                        )
                    }
                }
            }

            if (focusSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No focus sessions completed yet. Start your first Pomodoro timer above to track your studying!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(focusSessions) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Session",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = session.taskTitle ?: "General Study Session",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = formatSessionTime(session.timestamp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "+${session.durationMinutes}m",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Dynamic Background Full-screen focus overlay triggered automatically or manually
    AnimatedVisibility(
        visible = isTimerFullscreen,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when (currentSessionType) {
                        "Focus" -> Color(0xFF1E1010) // Deep dark focus red
                        "Short Break" -> Color(0xFF0F1B17) // Deep dark green
                        "Long Break" -> Color(0xFF0D141D) // Deep dark blue
                        else -> Color(0xFF121212)
                    }
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Indicator & Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.pauseTimer(context) },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause Timer",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = when (currentSessionType) {
                            "Focus" -> "STUDYING"
                            "Short Break" -> "SHORT BREAK"
                            "Long Break" -> "LONG BREAK"
                            else -> "FOCUSING"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = activeColor.copy(alpha = 0.9f),
                        letterSpacing = 2.sp
                    )
                    
                    TextButton(
                        onClick = { viewModel.isTimerFullscreen.value = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
                    ) {
                        Text("Exit Fullscreen", fontSize = 12.sp)
                    }
                }
                
                // Centered Timer Circular Indicator
                Box(
                    modifier = Modifier
                        .size(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(280.dp)) {
                        drawCircle(
                            color = activeColor.copy(alpha = 0.12f),
                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = activeColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$minutesText:$secondsText",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 72.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MINUTES REMAINING",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                    }
                }
                
                // Bottom Details & Controls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.navigationBarsPadding().padding(bottom = 24.dp)
                ) {
                    if (selectedTask != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(activeColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedTask!!.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "General Study Focus",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.resetTimer(context) },
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Reset",
                                tint = Color.White
                            )
                        }
                        
                        FloatingActionButton(
                            onClick = {
                                if (isTimerRunning) {
                                    viewModel.pauseTimer(context)
                                } else {
                                    viewModel.startTimer(context)
                                }
                            },
                            containerColor = activeColor,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause focus",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Durations Customization Dialog Shortcut
    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            title = {
                Text(
                    text = "Customize Focus Durations",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Focus Mins Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Focus Duration", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("$editFocusMins mins", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = editFocusMins.toFloat(),
                            onValueChange = { editFocusMins = it.toInt() },
                            valueRange = 5f..60f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Short Break Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Short Break", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("$editShortBreakMins mins", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = editShortBreakMins.toFloat(),
                            onValueChange = { editShortBreakMins = it.toInt() },
                            valueRange = 1f..15f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Long Break Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Long Break", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("$editLongBreakMins mins", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = editLongBreakMins.toFloat(),
                            onValueChange = { editLongBreakMins = it.toInt() },
                            valueRange = 5f..45f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Sound Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Play Alert Sound on End", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = editPlaySoundOnComplete,
                            onCheckedChange = { editPlaySoundOnComplete = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePomodoroSettings(
                            context,
                            editFocusMins,
                            editShortBreakMins,
                            editLongBreakMins,
                            editPlaySoundOnComplete,
                            editFullScreenOnStartFocus
                        )
                        showSettings = false
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun formatSessionTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun calculateFocusStreak(sessions: List<FocusSession>): Int {
    if (sessions.isEmpty()) return 0
    
    // Extract unique dates of sessions in yyyyMMdd format
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val dates = sessions.map { sdf.format(Date(it.timestamp)) }.distinct().sortedDescending()
    
    if (dates.isEmpty()) return 0
    
    val today = sdf.format(Date())
    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
    val yesterday = sdf.format(calendar.time)
    
    // Streak starts if the most recent session is today or yesterday
    val firstDate = dates[0]
    if (firstDate != today && firstDate != yesterday) {
        return 0
    }
    
    var streak = 1
    val currentCal = java.util.Calendar.getInstance()
    // Parse the first date
    val firstDateObj = sdf.parse(firstDate) ?: return 1
    currentCal.time = firstDateObj
    
    for (i in 1 until dates.size) {
        currentCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val expectedDate = sdf.format(currentCal.time)
        if (dates[i] == expectedDate) {
            streak++
        } else {
            break
        }
    }
    return streak
}

@Composable
fun MonthlyCalendarView(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit
) {
    var calendarState by remember { mutableStateOf(java.util.Calendar.getInstance()) }
    val currentYear = calendarState.get(java.util.Calendar.YEAR)
    val currentMonth = calendarState.get(java.util.Calendar.MONTH) // 0-indexed
    
    var selectedDayCalendar by remember { mutableStateOf(java.util.Calendar.getInstance()) }
    var selectedDayNum by remember { mutableStateOf(selectedDayCalendar.get(java.util.Calendar.DAY_OF_MONTH)) }
    
    // We can list days in current month
    val daysInMonth = remember(currentYear, currentMonth) {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, currentYear)
            set(java.util.Calendar.MONTH, currentMonth)
            set(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        val maxDays = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val startDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon ...
        List(startDayOfWeek - 1) { null } + List(maxDays) { it + 1 }
    }
    
    val monthName = remember(currentMonth) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendarState.time)
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        // Month navigation header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val newCal = java.util.Calendar.getInstance().apply {
                    time = calendarState.time
                    add(java.util.Calendar.MONTH, -1)
                }
                calendarState = newCal
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            IconButton(onClick = {
                val newCal = java.util.Calendar.getInstance().apply {
                    time = calendarState.time
                    add(java.util.Calendar.MONTH, 1)
                }
                calendarState = newCal
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Days of week header
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Days Grid
        val chunkedDays = daysInMonth.chunked(7)
        chunkedDays.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                week.forEach { dayNum ->
                    if (dayNum == null) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val isToday = remember(dayNum, currentYear, currentMonth) {
                            val today = java.util.Calendar.getInstance()
                            today.get(java.util.Calendar.YEAR) == currentYear &&
                                today.get(java.util.Calendar.MONTH) == currentMonth &&
                                today.get(java.util.Calendar.DAY_OF_MONTH) == dayNum
                        }
                        
                        val isSelected = remember(dayNum, currentYear, currentMonth, selectedDayNum, selectedDayCalendar) {
                            selectedDayCalendar.get(java.util.Calendar.YEAR) == currentYear &&
                                selectedDayCalendar.get(java.util.Calendar.MONTH) == currentMonth &&
                                selectedDayNum == dayNum
                        }
                        
                        // Check tasks on this day
                        val tasksOnDay = remember(tasks, dayNum, currentYear, currentMonth) {
                            tasks.filter { task ->
                                val taskCal = java.util.Calendar.getInstance().apply {
                                    timeInMillis = task.dueDate
                                }
                                taskCal.get(java.util.Calendar.YEAR) == currentYear &&
                                    taskCal.get(java.util.Calendar.MONTH) == currentMonth &&
                                    taskCal.get(java.util.Calendar.DAY_OF_MONTH) == dayNum
                            }
                        }
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else if (isToday) MaterialTheme.colorScheme.secondaryContainer
                                    else Color.Transparent
                                )
                                .clickable {
                                    val newSel = java.util.Calendar.getInstance().apply {
                                        set(java.util.Calendar.YEAR, currentYear)
                                        set(java.util.Calendar.MONTH, currentMonth)
                                        set(java.util.Calendar.DAY_OF_MONTH, dayNum)
                                    }
                                    selectedDayCalendar = newSel
                                    selectedDayNum = dayNum
                                }
                                .padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dayNum.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else if (isToday) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                            
                            // Visual dot/line when there is a task on day
                            if (tasksOnDay.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 6.dp else 4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                                            else {
                                                val hasHigh = tasksOnDay.any { it.priority == 2 }
                                                if (hasHigh) MaterialTheme.colorScheme.error
                                                else MaterialTheme.colorScheme.primary
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
                // Fill up row with spacers if less than 7 days
                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // List of tasks for the selected date
    val selectedDayTasks = remember(tasks, selectedDayCalendar, selectedDayNum) {
        tasks.filter { task ->
            val taskCal = java.util.Calendar.getInstance().apply {
                timeInMillis = task.dueDate
            }
            taskCal.get(java.util.Calendar.YEAR) == selectedDayCalendar.get(java.util.Calendar.YEAR) &&
                taskCal.get(java.util.Calendar.MONTH) == selectedDayCalendar.get(java.util.Calendar.MONTH) &&
                taskCal.get(java.util.Calendar.DAY_OF_MONTH) == selectedDayNum
        }
    }
    
    val dateString = remember(selectedDayCalendar, selectedDayNum) {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(selectedDayCalendar.time)
    }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tasks for $dateString",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (selectedDayTasks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks due on this day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                selectedDayTasks.forEach { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTaskClick(task) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (task.priority) {
                                            2 -> MaterialTheme.colorScheme.error
                                            1 -> Color(0xFFF59E0B)
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = task.subject,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 6: SETTINGS & RINGTONE
// ==========================================
@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    loggedInUsername: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    
    // Collect Pomodoro settings from TaskViewModel
    val focusMins by viewModel.focusMins.collectAsState()
    val shortBreakMins by viewModel.shortBreakMins.collectAsState()
    val longBreakMins by viewModel.longBreakMins.collectAsState()
    val playSoundOnComplete by viewModel.playSoundOnComplete.collectAsState()
    val fullScreenOnStartFocus by viewModel.fullScreenOnStartFocus.collectAsState()
    val selectedRingtoneName by viewModel.selectedRingtoneName.collectAsState()
    val customRingtoneUri by viewModel.customRingtoneUri.collectAsState()

    // Pick custom audio from device
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable permission if possible (to avoid losing access on app reboot)
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Query display name
            val fileName = getFileName(context, it) ?: "Selected audio file"
            viewModel.setCustomRingtone(context, "Custom", it.toString())
            android.widget.Toast.makeText(context, "Set custom ringtone: $fileName", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. User Profile Section (Logged In As) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Logged in as:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = loggedInUsername,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("settings_logout_btn")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Out")
                    }
                }
            }
        }

        // --- 2. Pomodoro Timer Durations Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Pomodoro Timer Durations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Focus Mins Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Focus Session Length")
                            Text("${focusMins} mins", fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = focusMins.toFloat(),
                            onValueChange = { viewModel.updatePomodoroSettings(context, it.toInt(), shortBreakMins, longBreakMins, playSoundOnComplete, fullScreenOnStartFocus) },
                            valueRange = 5f..60f,
                            steps = 11
                        )
                    }

                    // Short Break Mins Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Short Break Length")
                            Text("${shortBreakMins} mins", fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = shortBreakMins.toFloat(),
                            onValueChange = { viewModel.updatePomodoroSettings(context, focusMins, it.toInt(), longBreakMins, playSoundOnComplete, fullScreenOnStartFocus) },
                            valueRange = 1f..20f,
                            steps = 19
                        )
                    }

                    // Long Break Mins Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Long Break Length")
                            Text("${longBreakMins} mins", fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = longBreakMins.toFloat(),
                            onValueChange = { viewModel.updatePomodoroSettings(context, focusMins, shortBreakMins, it.toInt(), playSoundOnComplete, fullScreenOnStartFocus) },
                            valueRange = 5f..45f,
                            steps = 8
                        )
                    }
                }
            }
        }

        // --- 3. Focus Mode Settings ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Focus Mode Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Full Screen Focus Overlay", fontWeight = FontWeight.Bold)
                            Text("Open fullscreen overlay automatically when starting a focus session", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = fullScreenOnStartFocus,
                            onCheckedChange = { viewModel.updatePomodoroSettings(context, focusMins, shortBreakMins, longBreakMins, playSoundOnComplete, it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Play Alert Sound on Complete", fontWeight = FontWeight.Bold)
                            Text("Play a ringtone when your focus session or break ends", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = playSoundOnComplete,
                            onCheckedChange = { viewModel.updatePomodoroSettings(context, focusMins, shortBreakMins, longBreakMins, it, fullScreenOnStartFocus) }
                        )
                    }
                }
            }
        }

        // --- 4. Custom Ringtone Selection Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Alert Sounds & Ringtone",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Choose the alert sound for study session and task completion reminders:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    val soundOptions = listOf("Default", "Digital Chime", "Classic Bell", "Gentle Harp", "Custom")
                    
                    soundOptions.forEach { option ->
                        val isSelected = (option == selectedRingtoneName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
                                .clickable {
                                    if (option == "Custom") {
                                        if (customRingtoneUri != null) {
                                            viewModel.setCustomRingtone(context, "Custom", customRingtoneUri)
                                        } else {
                                            audioPickerLauncher.launch("audio/*")
                                        }
                                    } else {
                                        viewModel.setCustomRingtone(context, option, null)
                                    }
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    if (option == "Custom") {
                                        if (customRingtoneUri != null) {
                                            viewModel.setCustomRingtone(context, "Custom", customRingtoneUri)
                                        } else {
                                            audioPickerLauncher.launch("audio/*")
                                        }
                                    } else {
                                        viewModel.setCustomRingtone(context, option, null)
                                    }
                                }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (option == "Custom" && !customRingtoneUri.isNullOrBlank()) {
                                        val displayUriName = getFileName(context, Uri.parse(customRingtoneUri)) ?: "Custom sound file"
                                        "Custom: $displayUriName"
                                    } else {
                                        option
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (option == "Custom") {
                                    Text(
                                        text = "Add/Select an audio file from your device",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (isSelected) {
                                IconButton(
                                    onClick = { viewModel.playRingtoneSound(context) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Test sound",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    if (selectedRingtoneName == "Custom") {
                        Button(
                            onClick = { audioPickerLauncher.launch("audio/*") },
                            modifier = Modifier.fillMaxWidth().testTag("pick_custom_ringtone_btn")
                        ) {
                            Icon(Icons.Default.AudioFile, contentDescription = "Add Ringtone")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Choose Custom Audio File")
                        }
                    }
                }
            }
        }
    }
}

// Helper to get friendly filename from URI
fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = cursor.getString(index)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

