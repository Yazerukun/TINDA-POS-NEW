package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserAccount
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfacePrimary
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.DangerSoftRed
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.InStockGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.TindaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LoginMethod {
    QUICK_PIN,
    USERNAME_PASSWORD
}

@Composable
fun LoginScreen(
    viewModel: TindaViewModel,
    modifier: Modifier = Modifier
) {
    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()
    val allActiveUsers by viewModel.allActiveUsers.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    var selectedUser by remember { mutableStateOf<UserAccount?>(null) }
    var loginMethod by remember { mutableStateOf(LoginMethod.QUICK_PIN) }

    // PIN State
    var enteredPin by remember { mutableStateOf("") }
    var isPinError by remember { mutableStateOf(false) }

    // Username & Password State
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Current formatted date/time
    val currentDateStr = remember {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    // Auto-select first active user if none selected
    LaunchedEffect(allActiveUsers) {
        if (selectedUser == null && allActiveUsers.isNotEmpty()) {
            selectedUser = allActiveUsers.first()
            usernameInput = allActiveUsers.first().username
        }
    }

    // When a user is selected, sync username and clear PIN
    fun onSelectUser(user: UserAccount) {
        selectedUser = user
        usernameInput = user.username
        enteredPin = ""
        isPinError = false
        viewModel.clearLoginError()
    }

    // Auto validate PIN when 4 digits reached
    LaunchedEffect(enteredPin) {
        if (enteredPin.length == 4) {
            val user = selectedUser
            if (user != null) {
                val success = viewModel.loginWithPin(user, enteredPin)
                if (!success) {
                    isPinError = true
                    enteredPin = ""
                } else {
                    isPinError = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurfacePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = storeProfile.storeName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "POS Cashier Terminal • $currentDateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // User Selection Avatar Cards
                if (allActiveUsers.isNotEmpty()) {
                    Text(
                        text = "Select User Account",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allActiveUsers.forEach { user ->
                            val isSelected = selectedUser?.id == user.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectUser(user) }
                                    .testTag("user_chip_${user.username}"),
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else BrandSurfaceSoft,
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isSelected) EmeraldPrimary else BorderSubtle
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) EmeraldPrimary else TextMuted.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.displayName.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            fontSize = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = user.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) EmeraldInteractive else TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (user.role.equals("ADMIN", ignoreCase = true)) {
                                            WarningAmber.copy(alpha = 0.15f)
                                        } else {
                                            EmeraldPrimary.copy(alpha = 0.15f)
                                        },
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = user.role.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user.role.equals("ADMIN", ignoreCase = true)) WarningAmber else InStockGreen,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Tab Switcher: Quick PIN vs Username & Password
                TabRow(
                    selectedTabIndex = loginMethod.ordinal,
                    containerColor = BrandSurfaceSoft,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[loginMethod.ordinal]),
                            color = EmeraldPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = loginMethod == LoginMethod.QUICK_PIN,
                        onClick = {
                            loginMethod = LoginMethod.QUICK_PIN
                            viewModel.clearLoginError()
                        },
                        icon = { Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        text = { Text("Quick PIN", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = loginMethod == LoginMethod.USERNAME_PASSWORD,
                        onClick = {
                            loginMethod = LoginMethod.USERNAME_PASSWORD
                            viewModel.clearLoginError()
                        },
                        icon = { Icon(Icons.Default.Password, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        text = { Text("Username / Pass", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                // Error Message banner
                AnimatedVisibility(
                    visible = loginError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = DangerSoftRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DangerSoftRed.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = loginError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = DangerSoftRed,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )
                            IconButton(
                                onClick = { viewModel.clearLoginError() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Dismiss", tint = DangerSoftRed, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (loginMethod) {
                    LoginMethod.QUICK_PIN -> {
                        // Quick PIN View
                        Text(
                            text = "Enter 4-Digit PIN for ${selectedUser?.displayName ?: "User"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 4 PIN Dots indicator
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until 4) {
                                val isFilled = i < enteredPin.length
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isPinError -> DangerSoftRed
                                                isFilled -> EmeraldPrimary
                                                else -> BrandSurfaceElevated
                                            }
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isPinError) DangerSoftRed else if (isFilled) EmeraldPrimary else TextMuted.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 3x4 Number Keypad
                        val pinKeys = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "⌫")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            pinKeys.forEach { rowKeys ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowKeys.forEach { key ->
                                        Surface(
                                            modifier = Modifier
                                                .size(68.dp, 52.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    viewModel.clearLoginError()
                                                    isPinError = false
                                                    when (key) {
                                                        "C" -> enteredPin = ""
                                                        "⌫" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                        else -> if (enteredPin.length < 4) enteredPin += key
                                                    }
                                                }
                                                .testTag("pin_key_$key"),
                                            shape = RoundedCornerShape(16.dp),
                                            color = when (key) {
                                                "C" -> DangerSoftRed.copy(alpha = 0.08f)
                                                "⌫" -> BrandSurfaceSoft
                                                else -> BrandSurfaceSoft
                                            },
                                            border = BorderStroke(1.dp, BorderSubtle)
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                when (key) {
                                                    "⌫" -> Icon(
                                                        Icons.Default.Backspace,
                                                        contentDescription = "Backspace",
                                                        tint = TextSecondary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    "C" -> Text(
                                                        text = "CLR",
                                                        fontWeight = FontWeight.Bold,
                                                        color = DangerSoftRed,
                                                        fontSize = 14.sp
                                                    )
                                                    else -> Text(
                                                        text = key,
                                                        fontSize = 22.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Demo hint banner for store owner
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandSurfaceSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Default PIN: Admin = 1234 • Cashier = 0000",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    LoginMethod.USERNAME_PASSWORD -> {
                        // Username & Password Form
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = {
                                    usernameInput = it
                                    viewModel.clearLoginError()
                                },
                                label = { Text("Username") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_username"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    cursorColor = EmeraldPrimary
                                )
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = {
                                    passwordInput = it
                                    viewModel.clearLoginError()
                                },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    viewModel.loginWithCredentials(usernameInput, passwordInput) { success ->
                                        if (success) {
                                            passwordInput = ""
                                        }
                                    }
                                }),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_password"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    cursorColor = EmeraldPrimary
                                )
                            )

                            Button(
                                onClick = {
                                    viewModel.loginWithCredentials(usernameInput, passwordInput) { success ->
                                        if (success) {
                                            passwordInput = ""
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_login_submit"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign In to POS", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandSurfaceSoft,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Default Login: admin / admin123 • cashier / cashier123",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
