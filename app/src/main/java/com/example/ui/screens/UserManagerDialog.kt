package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserAccount
import com.example.ui.theme.BorderSubtle
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

@Composable
fun UserManagerDialog(
    viewModel: TindaViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showAddUserDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserAccount?>(null) }
    var userToDelete by remember { mutableStateOf<UserAccount?>(null) }
    var revealedPins by remember { mutableStateOf(setOf<Long>()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = BrandSurfacePrimary,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "User Accounts & Security",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Manage POS staff, quick PINs, and roles",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Bar: Add User button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${allUsers.size} Users Registered",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    Button(
                        onClick = { showAddUserDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("btn_add_user_account")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add User", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of Users
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    allUsers.forEach { user ->
                        val isSelf = currentUser?.id == user.id
                        val isPinShown = revealedPins.contains(user.id)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandSurfaceSoft),
                            border = BorderStroke(
                                1.dp,
                                if (isSelf) EmeraldPrimary.copy(alpha = 0.5f) else BorderSubtle
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Avatar & Info
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (user.role.equals("ADMIN", ignoreCase = true)) {
                                                    WarningAmber.copy(alpha = 0.18f)
                                                } else {
                                                    EmeraldPrimary.copy(alpha = 0.18f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.displayName.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = if (user.role.equals("ADMIN", ignoreCase = true)) WarningAmber else InStockGreen
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.displayName,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            if (isSelf) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = "YOU",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = EmeraldInteractive,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "@${user.username} • Role: ${user.role}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // PIN and Password badges
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = BrandSurfaceElevated,
                                                modifier = Modifier.clickable {
                                                    revealedPins = if (isPinShown) revealedPins - user.id else revealedPins + user.id
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Pin,
                                                        contentDescription = null,
                                                        tint = TextSecondary,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (isPinShown) "PIN: ${user.pin}" else "PIN: ••••",
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isPinShown) EmeraldInteractive else TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Action buttons: Edit & Delete
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingUser = user },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { userToDelete = user },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerSoftRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // Add User Sub-dialog
    if (showAddUserDialog) {
        UserFormDialog(
            userToEdit = null,
            onDismiss = { showAddUserDialog = false },
            onSave = { newUser ->
                viewModel.addUser(newUser) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        showAddUserDialog = false
                    }
                }
            }
        )
    }

    // Edit User Sub-dialog
    if (editingUser != null) {
        UserFormDialog(
            userToEdit = editingUser,
            onDismiss = { editingUser = null },
            onSave = { updatedUser ->
                viewModel.updateUser(updatedUser) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        editingUser = null
                    }
                }
            }
        )
    }

    // Delete Confirmation
    if (userToDelete != null) {
        val target = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete User Account") },
            text = {
                Text("Are you sure you want to delete user '${target.displayName}' (@${target.username})? This user will no longer be able to log in to POS.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(target) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            userToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerSoftRed)
                ) {
                    Text("Delete User")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserFormDialog(
    userToEdit: UserAccount?,
    onDismiss: () -> Unit,
    onSave: (UserAccount) -> Unit
) {
    val isEditing = userToEdit != null
    var displayName by remember { mutableStateOf(userToEdit?.displayName ?: "") }
    var username by remember { mutableStateOf(userToEdit?.username ?: "") }
    var role by remember { mutableStateOf(userToEdit?.role ?: "CASHIER") }
    var pin by remember { mutableStateOf(userToEdit?.pin ?: "") }
    var password by remember { mutableStateOf(userToEdit?.password ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit User Account" else "Add New Staff Account",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = DangerSoftRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = {
                        displayName = it
                        errorMessage = null
                    },
                    label = { Text("Display Name (e.g. Juan Dela Cruz)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it.lowercase().filter { char -> char.isLetterOrDigit() || char == '_' }
                        errorMessage = null
                    },
                    label = { Text("Username (lowercase login name)") },
                    enabled = !isEditing, // cannot change username once created
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Role selection chips
                Column {
                    Text(
                        text = "Account Role",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = role == "CASHIER",
                            onClick = { role = "CASHIER" },
                            label = { Text("Cashier") },
                            leadingIcon = if (role == "CASHIER") {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = role == "ADMIN",
                            onClick = { role = "ADMIN" },
                            label = { Text("Admin / Owner") },
                            leadingIcon = if (role == "ADMIN") {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                OutlinedTextField(
                    value = pin,
                    onValueChange = { input ->
                        if (input.length <= 4 && input.all { it.isDigit() }) {
                            pin = input
                            errorMessage = null
                        }
                    },
                    label = { Text("4-Digit Quick PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Used for quick terminal unlocking (${pin.length}/4 digits)") }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Account Password") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Used for username/password login") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (displayName.isBlank()) {
                        errorMessage = "Please enter a display name."
                        return@Button
                    }
                    if (username.isBlank()) {
                        errorMessage = "Please enter a username."
                        return@Button
                    }
                    if (pin.length != 4) {
                        errorMessage = "Quick PIN must be exactly 4 digits."
                        return@Button
                    }
                    if (password.length < 4) {
                        errorMessage = "Password must be at least 4 characters."
                        return@Button
                    }

                    val user = UserAccount(
                        id = userToEdit?.id ?: 0L,
                        username = username.trim(),
                        displayName = displayName.trim(),
                        role = role,
                        pin = pin.trim(),
                        password = password,
                        isActive = true,
                        createdAt = userToEdit?.createdAt ?: System.currentTimeMillis()
                    )
                    onSave(user)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(if (isEditing) "Save Changes" else "Create User")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
