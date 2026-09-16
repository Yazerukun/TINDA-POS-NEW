package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MoneyText
import com.example.ui.components.TindaCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfacePrimary
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.DangerSoftRed
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.util.CsvExportHelper
import com.example.util.JsonBackupHelper
import com.example.viewmodel.TindaViewModel
import kotlinx.coroutines.launch

@Composable
fun MoreScreen(
    viewModel: TindaViewModel,
    onNavigateToCredit: () -> Unit,
    onNavigateToReports: () -> Unit,
    onOpenStoreSettings: () -> Unit,
    onOpenSetupWizard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val totalActiveDebtors = allCustomers.count { it.totalDebt > 0 }
    val totalReceivables = allCustomers.sumOf { it.totalDebt }

    var isBackingUp by remember { mutableStateOf(false) }
    var restoreSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    // Backup restore launcher
    val restoreFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = JsonBackupHelper.readJsonFromUri(context, uri)
                val backupData = JsonBackupHelper.parseBackupJson(jsonString)
                viewModel.restoreStoreBackupData(backupData) {
                    restoreSuccessMessage = "Restored ${backupData.products.size} products, ${backupData.customers.size} customer credit accounts, and ${backupData.sales.size} sales transactions."
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to restore: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Store Header Card
        item {
            TindaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrandSurfaceElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = EmeraldInteractive,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = storeProfile.storeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Proprietor: ${storeProfile.ownerName.ifEmpty { "Store Owner" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        if (storeProfile.storeAddress.isNotEmpty()) {
                            Text(
                                text = storeProfile.storeAddress,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onOpenStoreSettings,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Edit", fontSize = 12.sp, color = EmeraldInteractive, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: BUSINESS
        item {
            MoreSectionTitle("BUSINESS")
            TindaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MoreNavRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = WarningAmber,
                        title = "Customer Credit (Utang Ledger)",
                        subtitle = if (totalActiveDebtors > 0) "$totalActiveDebtors active balances (₱${String.format("%,.2f", totalReceivables)})" else "No outstanding debts",
                        onClick = onNavigateToCredit,
                        testTag = "more_nav_credit"
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))
                    MoreNavRow(
                        icon = Icons.Default.People,
                        iconTint = EmeraldInteractive,
                        title = "Customer Directory",
                        subtitle = "${allCustomers.size} registered customers",
                        onClick = onNavigateToCredit
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))
                    MoreNavRow(
                        icon = Icons.Default.Payments,
                        iconTint = Color(0xFF60A5FA),
                        title = "Expenses & Float Reconcile",
                        subtitle = "Starting drawer: ₱${String.format("%,.2f", storeProfile.startingCashDrawer)}",
                        onClick = onNavigateToReports
                    )
                }
            }
        }

        // Section: ACTIVITY
        item {
            MoreSectionTitle("ACTIVITY & TRANSACTIONS")
            TindaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MoreNavRow(
                        icon = Icons.Default.ReceiptLong,
                        iconTint = EmeraldInteractive,
                        title = "Transaction Log & History",
                        subtitle = "View completed, printed & voided sales",
                        onClick = onNavigateToReports,
                        testTag = "more_nav_transactions"
                    )
                }
            }
        }

        // Section: SYSTEM
        item {
            MoreSectionTitle("SYSTEM & DATA")
            TindaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MoreNavRow(
                        icon = Icons.Default.Backup,
                        iconTint = EmeraldInteractive,
                        title = "Backup Database (JSON)",
                        subtitle = "Export complete store database to JSON file",
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
                                            chooserTitle = "Export Store Backup"
                                        )
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isBackingUp = false
                                    }
                                }
                            }
                        },
                        testTag = "more_nav_backup"
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))
                    MoreNavRow(
                        icon = Icons.Default.Restore,
                        iconTint = Color(0xFF38BDF8),
                        title = "Restore Database (JSON)",
                        subtitle = "Import and restore catalog, sales and debt ledger",
                        onClick = { restoreFileLauncher.launch("application/json") },
                        testTag = "more_nav_restore"
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))
                    MoreNavRow(
                        icon = Icons.Default.AutoFixHigh,
                        iconTint = WarningAmber,
                        title = "Store Setup Wizard",
                        subtitle = "Reconfigure store name, phone, receipt notes and float",
                        onClick = onOpenSetupWizard
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))
                    MoreNavRow(
                        icon = Icons.Default.SystemUpdate,
                        iconTint = EmeraldInteractive,
                        title = "Software Updater",
                        subtitle = "Check for new versions with download progress",
                        onClick = { showUpdateDialog = true },
                        testTag = "more_nav_updater"
                    )
                }
            }
        }

        // Database reset actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Load Starter Pack", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showClearConfirm = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerSoftRed)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp), tint = DangerSoftRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear All Data", fontSize = 12.sp, color = DangerSoftRed)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (restoreSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = { restoreSuccessMessage = null },
            title = { Text("Database Restored") },
            text = { Text(restoreSuccessMessage ?: "") },
            confirmButton = {
                Button(
                    onClick = { restoreSuccessMessage = null },
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
            text = { Text("This will reload sample Philippine sari-sari store products (Beverages, Snacks, Canned Goods, Condiments, Household) and clear demo transactions.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        viewModel.resetToStarterPack()
                        Toast.makeText(context, "Starter pack loaded successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Confirm Reset")
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
            title = { Text("Clear All Data?", color = DangerSoftRed) },
            text = { Text("Warning: This will permanently delete all products, sales history, customer records, and credit ledger data. Are you sure?") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirm = false
                        viewModel.clearAllDataForFreshStart()
                        Toast.makeText(context, "All data has been cleared.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerSoftRed)
                ) {
                    Text("Clear Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUpdateDialog) {
        SoftwareUpdateDialog(
            onDismiss = { showUpdateDialog = false },
            onPreInstallBackup = {
                viewModel.triggerAutoSafetyBackup()
            }
        )
    }
}

@Composable
private fun MoreSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        fontSize = 11.sp,
        color = TextMuted,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun MoreNavRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    var rowModifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 14.dp)
    if (testTag != null) {
        rowModifier = rowModifier.testTag(testTag)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}
