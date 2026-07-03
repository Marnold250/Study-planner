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
import androidx.compose.material.icons.filled.ExitToApp
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
        Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "App Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "My Study Hub",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Logged in as: $loggedInUsername",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val userToLogout = loggedInUsername
                            sharedPrefs.edit()
                                .putBoolean("user_logged_in", false)
                                .putString("logged_in_username", "guest")
                                .apply()
                            isLoggedIn = false
                            viewModel.setCurrentUser("guest")
                            RealtimeNotificationHelper.showNotification(
                                context = context,
                                title = "Logged Out",
                                message = "Goodbye, $userToLogout! You have been successfully logged out."
                            )
                        },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
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
        },
        floatingActionButton = {
            if (currentTab == 0 || currentTab == 1) {
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
                    }
                )
                4 -> PomodoroTimerScreen(viewModel = viewModel)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
                Column {
                    Text(
                        text = "Study Planner",
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = "Academic Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
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
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Hero Image Background
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
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
                            text = "KNOWLEDGE IS POWER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your focus determines your reality. Let's make progress today!",
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

    Column(
        modifier = Modifier
            .fillMaxSize()
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

            // Search textfield
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_tasks_input"),
                placeholder = { Text("Search title, study guides, notes...") },
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

            // Status Filter Row (Horizontal Scrolling List)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusFilterChip(
                    label = "All",
                    selected = statusFilter == StatusFilter.ALL,
                    onClick = { viewModel.statusFilter.value = StatusFilter.ALL }
                )
                StatusFilterChip(
                    label = "Pending",
                    selected = statusFilter == StatusFilter.PENDING,
                    onClick = { viewModel.statusFilter.value = StatusFilter.PENDING }
                )
                StatusFilterChip(
                    label = "Completed",
                    selected = statusFilter == StatusFilter.COMPLETED,
                    onClick = { viewModel.statusFilter.value = StatusFilter.COMPLETED }
                )
                StatusFilterChip(
                    label = "Overdue",
                    selected = statusFilter == StatusFilter.OVERDUE,
                    onClick = { viewModel.statusFilter.value = StatusFilter.OVERDUE }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Sort Dropdown button
                Box {
                    IconButton(
                        onClick = { showFilterMenu = !showFilterMenu },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Sort Option Menu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
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
// COMPOSABLE: TASK LIST ITEM CARD (HIGH POLISH)
// ==========================================
@Composable
fun TaskListItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val cardAlpha = if (task.isCompleted) 0.65f else 1f
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
            .clickable(onClick = onClick)
            .testTag("task_card_item_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
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
                // Interactive Checkbox with standard target height
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary,
                        uncheckedColor = if (isHighPriorityActive) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
                    ),
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
    onRequestCalendarPermission: () -> Unit
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Google Calendar Sync",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Integrate your study tasks with your Google Calendar to stay organized and synchronized across your devices.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
fun PomodoroTimerScreen(viewModel: TaskViewModel) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()

    // Pomodoro defaults
    var focusMins by remember { mutableStateOf(25) }
    var shortBreakMins by remember { mutableStateOf(5) }
    var longBreakMins by remember { mutableStateOf(15) }

    // Session states
    var currentSessionType by remember { mutableStateOf("Focus") } // "Focus", "Short Break", "Long Break"
    var timerSecondsRemaining by remember { mutableStateOf(focusMins * 60) }
    var timerTotalSeconds by remember { mutableStateOf(focusMins * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Task selection
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // UI Configuration / Settings
    var showSettings by remember { mutableStateOf(false) }
    var playSoundOnComplete by remember { mutableStateOf(true) }

    // Active color scheme based on mode
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

    // Effect for the running countdown timer
    LaunchedEffect(isTimerRunning, timerSecondsRemaining) {
        if (isTimerRunning && timerSecondsRemaining > 0) {
            delay(1000L)
            timerSecondsRemaining -= 1
        } else if (isTimerRunning && timerSecondsRemaining == 0) {
            isTimerRunning = false

            // 1. Play alert sound
            if (playSoundOnComplete) {
                try {
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(context, notification)
                    r.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 2. Log focus session if completed Focus mode
            if (currentSessionType == "Focus") {
                viewModel.logFocusSession(
                    taskId = selectedTask?.id,
                    taskTitle = selectedTask?.title,
                    durationMinutes = focusMins
                )
                Toast.makeText(context, "🏆 Pomodoro completed! Logged $focusMins mins focus.", Toast.LENGTH_LONG).show()
                RealtimeNotificationHelper.showNotification(
                    context = context,
                    title = "🏆 Study Session Completed",
                    message = "Congratulations! You completed your $focusMins mins focus session for '${selectedTask?.title ?: "General Study"}'."
                )
                
                // Switch to break mode
                currentSessionType = "Short Break"
                timerTotalSeconds = shortBreakMins * 60
                timerSecondsRemaining = timerTotalSeconds
            } else {
                Toast.makeText(context, "Break over! Ready to focus?", Toast.LENGTH_SHORT).show()
                RealtimeNotificationHelper.showNotification(
                    context = context,
                    title = "⏰ Break Completed",
                    message = "Your break has ended. Let's start the next Pomodoro focus session!"
                )
                currentSessionType = "Focus"
                timerTotalSeconds = focusMins * 60
                timerSecondsRemaining = timerTotalSeconds
            }
        }
    }

    // Handle session type switching
    fun switchSessionMode(mode: String) {
        isTimerRunning = false
        currentSessionType = mode
        val mins = when (mode) {
            "Focus" -> focusMins
            "Short Break" -> shortBreakMins
            "Long Break" -> longBreakMins
            else -> 25
        }
        timerTotalSeconds = mins * 60
        timerSecondsRemaining = timerTotalSeconds
    }

    // Handle configuration changes
    fun updateDurations(focus: Int, short: Int, long: Int) {
        focusMins = focus
        shortBreakMins = short
        longBreakMins = long
        
        // Reset timer if not running
        if (!isTimerRunning) {
            val mins = when (currentSessionType) {
                "Focus" -> focusMins
                "Short Break" -> shortBreakMins
                "Long Break" -> longBreakMins
                else -> 25
            }
            timerTotalSeconds = mins * 60
            timerSecondsRemaining = timerTotalSeconds
        }
    }

    // Reset current timer
    fun resetTimer() {
        isTimerRunning = false
        val mins = when (currentSessionType) {
            "Focus" -> focusMins
            "Short Break" -> shortBreakMins
            "Long Break" -> longBreakMins
            else -> 25
        }
        timerTotalSeconds = mins * 60
        timerSecondsRemaining = timerTotalSeconds
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
                                .clickable { switchSessionMode(mode) }
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
                            IconButton(onClick = { selectedTask = null }) {
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
                                                selectedTask = task
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
                    onClick = { resetTimer() },
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
                    onClick = { isTimerRunning = !isTimerRunning },
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

                // Settings Button (durations)
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

    // Settings Customization Dialog
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
                            Text("$focusMins mins", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = focusMins.toFloat(),
                            onValueChange = { focusMins = it.toInt() },
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
                            Text("$shortBreakMins mins", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = shortBreakMins.toFloat(),
                            onValueChange = { shortBreakMins = it.toInt() },
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
                            Text("$longBreakMins mins", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = longBreakMins.toFloat(),
                            onValueChange = { longBreakMins = it.toInt() },
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
                            checked = playSoundOnComplete,
                            onCheckedChange = { playSoundOnComplete = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateDurations(focusMins, shortBreakMins, longBreakMins)
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
