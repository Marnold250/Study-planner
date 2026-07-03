package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var currentStep by rememberSaveable { mutableStateOf(1) }

    val totalSteps = 3

    val title = when (currentStep) {
        1 -> "Organize Your Academic Life"
        2 -> "Boost Focus with Pomodoro"
        else -> "Sync with Google Calendar"
    }

    val description = when (currentStep) {
        1 -> "Manage your courses, lectures, assignments, and tasks all in one unified, modern hub."
        2 -> "Supercharge your concentration using the custom study timer. Track your study intervals and streaks effortlessly."
        else -> "Integrate your study timetable and homework reminders with your Google Calendar automatically."
    }

    val imageRes = when (currentStep) {
        1 -> R.drawable.img_onboarding_organize_1783067310393
        2 -> R.drawable.img_onboarding_focus_1783067327516
        else -> R.drawable.img_onboarding_sync_1783067343559
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row (Skip & Step Count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step $currentStep of $totalSteps",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                TextButton(
                    onClick = onFinished,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Central Image and Text Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Image Frame with soft glow
                Card(
                    modifier = Modifier
                        .size(260.dp)
                        .padding(12.dp)
                        .testTag("onboarding_image_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Onboarding Illustration",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Animated text change for elegant onboarding transitions
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .testTag("onboarding_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag("onboarding_desc")
                )
            }

            // Bottom Navigation Indicators & Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..totalSteps) {
                        val isSelected = i == currentStep
                        val width = if (isSelected) 24.dp else 8.dp
                        val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .weight(1f)
                                .padding(end = 8.dp)
                                .testTag("onboarding_back")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go back",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Next/Start Button
                    Button(
                        onClick = {
                            if (currentStep < totalSteps) {
                                currentStep += 1
                            } else {
                                onFinished()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .height(52.dp)
                            .weight(1f)
                            .padding(start = 8.dp)
                            .testTag("onboarding_next")
                    ) {
                        Text(
                            text = if (currentStep == totalSteps) "Get Started" else "Next",
                            fontWeight = FontWeight.Bold
                        )
                        if (currentStep < totalSteps) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next step",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: TaskViewModel,
    onLoginSuccess: (username: String) -> Unit
) {
    var isSignUpMode by rememberSaveable { mutableStateOf(false) }

    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun handleAuthentication() {
        // Reset Error fields
        usernameError = null
        emailError = null
        passwordError = null
        confirmPasswordError = null

        if (isSignUpMode) {
            var isValid = true
            if (username.trim().length < 3) {
                usernameError = "Username must be at least 3 characters"
                isValid = false
            }
            if (email.trim().isBlank() || !email.contains("@")) {
                emailError = "Please enter a valid email address"
                isValid = false
            }
            if (password.length < 6) {
                passwordError = "Password must be at least 6 characters"
                isValid = false
            }
            if (password != confirmPassword) {
                confirmPasswordError = "Passwords do not match"
                isValid = false
            }

            if (isValid) {
                coroutineScope.launch {
                    val result = viewModel.registerUser(username, email, password)
                    when (result) {
                        RegistrationResult.SUCCESS -> {
                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            // Real-time notification
                            RealtimeNotificationHelper.showNotification(
                                context = context,
                                title = "🎉 Account Verified",
                                message = "Welcome, $username! Your account has been created. Please sign in."
                            )
                            isSignUpMode = false
                            password = ""
                            confirmPassword = ""
                        }
                        RegistrationResult.USERNAME_TAKEN -> {
                            usernameError = "Username is already taken"
                        }
                        RegistrationResult.EMAIL_TAKEN -> {
                            emailError = "Email is already registered"
                        }
                        RegistrationResult.EMPTY_FIELDS -> {
                            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            var isValid = true
            if (username.trim().isBlank()) {
                usernameError = "Username cannot be empty"
                isValid = false
            }
            if (password.trim().isBlank()) {
                passwordError = "Password cannot be empty"
                isValid = false
            }

            if (isValid) {
                coroutineScope.launch {
                    val result = viewModel.loginUser(username, password)
                    when (result) {
                        LoginResult.SUCCESS -> {
                            Toast.makeText(context, "Welcome, $username!", Toast.LENGTH_SHORT).show()
                            // Real-time notification
                            RealtimeNotificationHelper.showNotification(
                                context = context,
                                title = "⚡ Access Authorized",
                                message = "Welcome back to your Study Hub, $username! Ready to focus?"
                            )
                            onLoginSuccess(username.trim())
                        }
                        LoginResult.USER_NOT_FOUND -> {
                            usernameError = "Username is not registered"
                        }
                        LoginResult.INCORRECT_PASSWORD -> {
                            passwordError = "Incorrect password"
                        }
                        LoginResult.EMPTY_FIELDS -> {
                            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Branding Card
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp)
                    .testTag("login_brand_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "App Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSignUpMode) "Create Student Account" else "Welcome to My Study Hub",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("login_header")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isSignUpMode) "Register your unique student details to secure your focus tracking." else "Securely sign in to access your customized Pomodoro timer & tasks.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Inputs Form
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (it.isNotBlank()) usernameError = null
                    },
                    label = { Text("Username") },
                    placeholder = { Text("e.g. student123") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User icon"
                        )
                    },
                    isError = usernameError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_username_input")
                )
                if (usernameError != null) {
                    Text(
                        text = usernameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Email (Sign Up only)
                if (isSignUpMode) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (it.isNotBlank()) emailError = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. student@example.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email icon"
                            )
                        },
                        isError = emailError != null,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (it.isNotBlank()) passwordError = null
                    },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock icon"
                        )
                    },
                    trailingIcon = {
                        val icon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(imageVector = icon, contentDescription = "Toggle password")
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (!isSignUpMode) {
                            focusManager.clearFocus()
                            handleAuthentication()
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input")
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Confirm Password (Sign Up only)
                if (isSignUpMode) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (it.isNotBlank()) confirmPasswordError = null
                        },
                        label = { Text("Confirm Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock icon"
                            )
                        },
                        trailingIcon = {
                            val icon = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(imageVector = icon, contentDescription = "Toggle password")
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPasswordError != null,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            handleAuthentication()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_confirm_password_input")
                    )
                    if (confirmPasswordError != null) {
                        Text(
                            text = confirmPasswordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {
                    focusManager.clearFocus()
                    handleAuthentication()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_submit_button")
            ) {
                Text(
                    text = if (isSignUpMode) "Register Student Account" else "Log In to Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch Screen mode Button
            TextButton(
                onClick = {
                    isSignUpMode = !isSignUpMode
                    // clear fields and errors
                    usernameError = null
                    emailError = null
                    passwordError = null
                    confirmPasswordError = null
                    password = ""
                    confirmPassword = ""
                },
                modifier = Modifier.testTag("toggle_sign_up_mode")
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account? Log In" else "Need an account? Create one here",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Demo Autofill helper
            Row(
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            // Automatically insert/register demo credentials into DB to guarantee it exists
                            viewModel.registerUser("Marnold", "arnoldmugisha08@gmail.com", "demo_password")
                            username = "Marnold"
                            password = "demo_password"
                            email = "arnoldmugisha08@gmail.com"
                            confirmPassword = "demo_password"
                            usernameError = null
                            passwordError = null
                            emailError = null
                            confirmPasswordError = null
                            Toast.makeText(context, "Demo account loaded!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Hint info",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tap here to autofill with developer credentials",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Small popup dialog describing the creator Marnold
@Composable
fun CreatorInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Creator Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "About the Creator",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Designed and developed with 💡 and Jetpack Compose by Marnold.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Detail rows with descriptive labels
                CreatorContactRow(icon = Icons.Default.Person, label = "Creator Name", value = "Marnold")
                CreatorContactRow(icon = Icons.Default.Star, label = "Instagram", value = "marnold2k")
                CreatorContactRow(icon = Icons.Default.Phone, label = "Telephone", value = "0794797910")
                CreatorContactRow(icon = Icons.Default.Code, label = "GitHub", value = "Marnold250")
                CreatorContactRow(icon = Icons.Default.Email, label = "Email", value = "arnoldmugisha08@gmail.com")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Awesome!")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("creator_info_dialog")
    )
}

@Composable
fun CreatorContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
