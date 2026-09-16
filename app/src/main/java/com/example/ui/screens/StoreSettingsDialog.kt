package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OutOfStockRed
import com.example.util.CsvExportHelper
import com.example.util.JsonBackupHelper
import com.example.viewmodel.StoreProfile
import com.example.viewmodel.TindaViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun StoreSettingsDialog(
    initialProfile: StoreProfile,
    viewModel: TindaViewModel,
    onDismiss: () -> Unit,
    onLaunchWizard: () -> Unit,
    onResetToStarterPack: () -> Unit,
    onClearAllData: () -> Unit,
    onSave: (name: String, owner: String, phone: String, address: String, message: String, floatAmount: Double) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var storeName by remember { mutableStateOf(initialProfile.storeName) }
    var ownerName by remember { mutableStateOf(initialProfile.ownerName) }
    var storePhone by remember { mutableStateOf(initialProfile.storePhone) }
    var storeAddress by remember { mutableStateOf(initialProfile.storeAddress) }
    var receiptMessage by remember { mutableStateOf(initialProfile.receiptMessage) }
    var startingCashStr by remember {
        mutableStateOf(
            if (initialProfile.startingCashDrawer > 0)
                String.format(Locale.US, "%.0f", initialProfile.startingCashDrawer)
            else "500"
        )
    }

    var showResetConfirm by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showRestoreSuccessDialog by remember { mutableStateOf<String?>(null) }
    var isBackingUp by remember { mutableStateOf(false) }

    // File picker for restoring JSON backup
    val restoreFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = JsonBackupHelper.readJsonFromUri(context, uri)
                val backupData = JsonBackupHelper.parseBackupJson(jsonString)
                viewModel.restoreStoreBackupData(backupData) {
                    showRestoreSuccessDialog = "Successfully restored ${backupData.products.size} products, ${backupData.customers.size} customer accounts, and ${backupData.sales.size} sales transactions!"
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error restoring backup: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Store Profile & Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Setup Wizard trigger button
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onLaunchWizard()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Launch Setup Wizard", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(2.dp))

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Store Name *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_store_name")
                )

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner / Cashier Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = storePhone,
                    onValueChange = { storePhone = it },
                    label = { Text("Contact Phone / Payment Mobile") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = storeAddress,
                    onValueChange = { storeAddress = it },
                    label = { Text("Store Location / Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = startingCashStr,
                    onValueChange = { startingCashStr = it },
                    label = { Text("Starting Cash Drawer Float (₱)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = receiptMessage,
                    onValueChange = { receiptMessage = it },
                    label = { Text("Receipt Footer Message") },
                    placeholder = { Text("e.g. Thank you for your purchase! Please come again.") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))

                // Data Backup & Restore Section
                Text(
                    text = "Data Backup & Restore (JSON)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!isBackingUp) {
                                isBackingUp = true
                                coroutineScope.launch {
                                    try {
                                        val backupData = viewModel.exportStoreBackupData()
                                        val file = JsonBackupHelper.createBackupFile(context, backupData)
                                        CsvExportHelper.shareFile(
                                            context = context,
                                            file = file,
                                            mimeType = "application/json",
                                            chooserTitle = "Share Store Backup"
                                        )
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isBackingUp = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("backup_data_btn"),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isBackingUp
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                        Text("Backup JSON", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            restoreFileLauncher.launch("application/json")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("restore_data_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                        Text("Restore JSON", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Database & Catalog Management",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_starter_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Reset Starter", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("clear_all_data_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OutOfStockRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Clear All", fontSize = 11.sp, color = OutOfStockRed)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cash = startingCashStr.toDoubleOrNull() ?: 500.0
                    onSave(storeName, ownerName, storePhone, storeAddress, receiptMessage, cash)
                },
                enabled = storeName.isNotBlank(),
                modifier = Modifier.testTag("save_store_settings_btn")
            ) {
                Text("Save Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showRestoreSuccessDialog != null) {
        AlertDialog(
            onDismissRequest = {
                showRestoreSuccessDialog = null
                onDismiss()
            },
            title = { Text("Database Restored Successfully") },
            text = { Text(showRestoreSuccessDialog ?: "") },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreSuccessDialog = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset to Starter Pack?") },
            text = {
                Text("This will restore the standard starter retail products and sample records. Are you sure you want to proceed?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToStarterPack()
                        showResetConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Reset Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Store Data?") },
            text = {
                Text("This will permanently remove all products, customer credit accounts, and sales history so you can start completely from scratch. Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OutOfStockRed)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

