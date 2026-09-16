package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ZReadReport
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.InStockGreen
import com.example.ui.theme.OutOfStockRed
import com.example.ui.theme.UtangAmber
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.StoreProfile
import com.example.viewmodel.XReadSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun XReadDialog(
    snapshot: XReadSnapshot,
    storeProfile: StoreProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val timeFmt = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    val dateOnlyFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val receiptPlainText = buildString {
        appendLine("================================")
        appendLine("           X-READING            ")
        appendLine("      (MID-DAY AUDIT SLIP)      ")
        appendLine("================================")
        appendLine(storeProfile.storeName)
        if (storeProfile.storeAddress.isNotBlank()) appendLine(storeProfile.storeAddress)
        if (storeProfile.storePhone.isNotBlank()) appendLine("Tel: ${storeProfile.storePhone}")
        appendLine("Date: ${timeFmt.format(Date(snapshot.generatedAt))}")
        appendLine("Shift Start: ${timeFmt.format(Date(snapshot.periodStart))}")
        appendLine("First Tx: #${snapshot.firstReceiptNumber} | Last: #${snapshot.lastReceiptNumber}")
        appendLine("--------------------------------")
        appendLine("GROSS SALES:      PHP ${String.format(Locale.US, "%.2f", snapshot.grossSales)}")
        appendLine("DISCOUNTS:        PHP ${String.format(Locale.US, "%.2f", snapshot.totalDiscounts)}")
        appendLine("NET SALES:        PHP ${String.format(Locale.US, "%.2f", snapshot.netSales)}")
        appendLine("EST. PROFIT:      PHP ${String.format(Locale.US, "%.2f", snapshot.totalProfit)}")
        appendLine("TRANSACTION COUNT: ${snapshot.transactionCount}")
        appendLine("--------------------------------")
        appendLine("PAYMENT TENDER BREAKDOWN:")
        appendLine(" Cash Sales:      PHP ${String.format(Locale.US, "%.2f", snapshot.cashSales)}")
        appendLine(" GCash / Maya:    PHP ${String.format(Locale.US, "%.2f", snapshot.gcashSales)}")
        appendLine(" Credit (Utang):  PHP ${String.format(Locale.US, "%.2f", snapshot.creditSales)}")
        appendLine("--------------------------------")
        appendLine("CASH DRAWER AUDIT:")
        appendLine(" (+) Opening Float:    PHP ${String.format(Locale.US, "%.2f", snapshot.openingFloat)}")
        appendLine(" (+) Cash Sales:       PHP ${String.format(Locale.US, "%.2f", snapshot.cashSales)}")
        appendLine(" (+) Credit Collected: PHP ${String.format(Locale.US, "%.2f", snapshot.customerDebtPaymentsCollected)}")
        appendLine(" (=) EXPECTED CASH:    PHP ${String.format(Locale.US, "%.2f", snapshot.expectedCashInDrawer)}")
        appendLine("--------------------------------")
        appendLine("VOIDED TRANSACTIONS: ${snapshot.voidedCount} (PHP ${String.format(Locale.US, "%.2f", snapshot.voidedTotal)})")
        appendLine("================================")
        appendLine("NOTICE: This reading does not")
        appendLine("reset sales counters.")
        appendLine("Printed: ${timeFmt.format(Date())}")
        appendLine("================================")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "X-Reading (Mid-Day Audit)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Non-resetting shift snapshot",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Thermal Slip Paper Simulation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFC)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Store branding
                        Text(
                            text = storeProfile.storeName,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (storeProfile.storeAddress.isNotBlank()) {
                            Text(
                                text = storeProfile.storeAddress,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "*** X-READ AUDIT REPORT ***",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldPrimary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Shift Since: ${timeFmt.format(Date(snapshot.periodStart))}",
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.DarkGray,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ThermalReportRow("Gross Sales", "₱${String.format(Locale.US, "%.2f", snapshot.grossSales)}", isBold = true)
                        ThermalReportRow("Less Discounts", "-₱${String.format(Locale.US, "%.2f", snapshot.totalDiscounts)}")
                        ThermalReportRow("Net Sales Total", "₱${String.format(Locale.US, "%.2f", snapshot.netSales)}", isBold = true, color = EmeraldPrimary)
                        ThermalReportRow("Est. Profit Margin", "₱${String.format(Locale.US, "%.2f", snapshot.totalProfit)}", color = InStockGreen)
                        ThermalReportRow("Valid Transactions", "${snapshot.transactionCount} orders")
                        ThermalReportRow("Rcpt Range", "#${snapshot.firstReceiptNumber} to #${snapshot.lastReceiptNumber}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "PAYMENT METHOD SUMMARY:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        ThermalReportRow("  Cash Sales", "₱${String.format(Locale.US, "%.2f", snapshot.cashSales)}")
                        ThermalReportRow("  GCash / Maya", "₱${String.format(Locale.US, "%.2f", snapshot.gcashSales)}")
                        ThermalReportRow("  Credit (Utang)", "₱${String.format(Locale.US, "%.2f", snapshot.creditSales)}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "CASH DRAWER RECONCILIATION:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        ThermalReportRow("  (+) Opening Float", "₱${String.format(Locale.US, "%.2f", snapshot.openingFloat)}")
                        ThermalReportRow("  (+) Cash Sales", "₱${String.format(Locale.US, "%.2f", snapshot.cashSales)}")
                        ThermalReportRow("  (+) Debt Collected", "₱${String.format(Locale.US, "%.2f", snapshot.customerDebtPaymentsCollected)}")
                        ThermalReportRow(
                            label = "  (=) Expected Cash In Drawer",
                            value = "₱${String.format(Locale.US, "%.2f", snapshot.expectedCashInDrawer)}",
                            isBold = true,
                            color = CashGreen
                        )

                        if (snapshot.voidedCount > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            ThermalReportRow(
                                label = "Voided Sales (${snapshot.voidedCount})",
                                value = "₱${String.format(Locale.US, "%.2f", snapshot.voidedTotal)}",
                                color = OutOfStockRed
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Generated at: ${timeFmt.format(Date(snapshot.generatedAt))}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Print, Share, Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            printThermalReport(
                                context = context,
                                reportTitle = "TINDA_X_READ",
                                printHtml = generateHtmlSlip("X-READ AUDIT REPORT", receiptPlainText, storeProfile)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print Slip", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptPlainText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share X-Read Report"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ZReadPromptDialog(
    snapshot: XReadSnapshot,
    storeProfile: StoreProfile,
    onDismiss: () -> Unit,
    onConfirmZRead: (actualCash: Double?, notes: String) -> Unit
) {
    var cashCountInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    val actualCash = cashCountInput.toDoubleOrNull()
    val shortageOver = if (actualCash != null) actualCash - snapshot.expectedCashInDrawer else null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OutOfStockRed.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = OutOfStockRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "End of Day (Z-Reading)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Close register & save official record",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Warning banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Performing a Z-Reading permanently saves the shift financials into history and establishes a new shift baseline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Shift Financial Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ThermalReportRow("Net Sales Total", "₱${String.format(Locale.US, "%.2f", snapshot.netSales)}", isBold = true, color = EmeraldPrimary)
                        ThermalReportRow("Total Profit", "₱${String.format(Locale.US, "%.2f", snapshot.totalProfit)}", color = InStockGreen)
                        ThermalReportRow("Transaction Count", "${snapshot.transactionCount} sales")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ThermalReportRow("Cash POS Sales", "₱${String.format(Locale.US, "%.2f", snapshot.cashSales)}")
                        ThermalReportRow("Credit Collected", "+₱${String.format(Locale.US, "%.2f", snapshot.customerDebtPaymentsCollected)}")
                        ThermalReportRow("Opening Float", "+₱${String.format(Locale.US, "%.2f", snapshot.openingFloat)}")
                        ThermalReportRow("Expected Cash in Drawer", "₱${String.format(Locale.US, "%.2f", snapshot.expectedCashInDrawer)}", isBold = true, color = CashGreen)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actual Cash Count Input
                Text(
                    text = "Cash Drawer Count (Optional Reconciliation)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = cashCountInput,
                    onValueChange = { cashCountInput = it },
                    label = { Text("Actual Cash Counted in Drawer (₱)") },
                    placeholder = { Text(String.format(Locale.US, "%.2f", snapshot.expectedCashInDrawer)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("zread_actual_cash_input"),
                    singleLine = true
                )

                if (shortageOver != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val isShort = shortageOver < -0.01
                    val isOver = shortageOver > 0.01
                    val statusText = when {
                        isShort -> "SHORTAGE: -₱${String.format(Locale.US, "%.2f", kotlin.math.abs(shortageOver))}"
                        isOver -> "OVERAGE: +₱${String.format(Locale.US, "%.2f", shortageOver)}"
                        else -> "EXACT MATCH (Balanced)"
                    }
                    val statusColor = when {
                        isShort -> OutOfStockRed
                        isOver -> GCashBlue
                        else -> InStockGreen
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Closing Notes / Cashier Shift (Optional)") },
                    placeholder = { Text("e.g. Shift 1 closed by Jane") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onConfirmZRead(actualCash, notesInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OutOfStockRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("zread_confirm_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Execute Z-Read")
                    }
                }
            }
        }
    }
}

@Composable
fun ZReadDetailDialog(
    report: ZReadReport,
    storeProfile: StoreProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val timeFmt = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    val receiptPlainText = buildString {
        appendLine("================================")
        appendLine("           Z-READING            ")
        appendLine("     OFFICIAL SHIFT REPORT      ")
        appendLine("================================")
        appendLine(storeProfile.storeName)
        if (storeProfile.storeAddress.isNotBlank()) appendLine(storeProfile.storeAddress)
        if (storeProfile.storePhone.isNotBlank()) appendLine("Tel: ${storeProfile.storePhone}")
        appendLine("Z-Read No: ${report.zReadNumber}")
        appendLine("Closed At: ${timeFmt.format(Date(report.generatedAt))}")
        appendLine("Period: ${timeFmt.format(Date(report.periodStart))}")
        appendLine("     to ${timeFmt.format(Date(report.periodEnd))}")
        appendLine("First Tx: #${report.firstReceiptNumber} | Last: #${report.lastReceiptNumber}")
        appendLine("--------------------------------")
        appendLine("GROSS SALES:      PHP ${String.format(Locale.US, "%.2f", report.grossSales)}")
        appendLine("DISCOUNTS:        PHP ${String.format(Locale.US, "%.2f", report.totalDiscounts)}")
        appendLine("NET SALES:        PHP ${String.format(Locale.US, "%.2f", report.netSales)}")
        appendLine("NET PROFIT:       PHP ${String.format(Locale.US, "%.2f", report.totalProfit)}")
        appendLine("TRANSACTION COUNT: ${report.transactionCount}")
        appendLine("--------------------------------")
        appendLine("TENDER BREAKDOWN:")
        appendLine(" Cash Sales:      PHP ${String.format(Locale.US, "%.2f", report.cashSales)}")
        appendLine(" GCash / Maya:    PHP ${String.format(Locale.US, "%.2f", report.gcashSales)}")
        appendLine(" Credit (Utang):  PHP ${String.format(Locale.US, "%.2f", report.creditSales)}")
        appendLine("--------------------------------")
        appendLine("DRAWER RECONCILIATION:")
        appendLine(" (+) Opening Float:    PHP ${String.format(Locale.US, "%.2f", report.openingFloat)}")
        appendLine(" (+) Cash Sales:       PHP ${String.format(Locale.US, "%.2f", report.cashSales)}")
        appendLine(" (+) Debt Collected:   PHP ${String.format(Locale.US, "%.2f", report.customerDebtPaymentsCollected)}")
        appendLine(" (=) EXPECTED CASH:    PHP ${String.format(Locale.US, "%.2f", report.expectedCashInDrawer)}")
        if (report.actualCashCounted != null) {
            appendLine(" Actual Count:         PHP ${String.format(Locale.US, "%.2f", report.actualCashCounted)}")
            val diff = report.cashShortageOver ?: 0.0
            val sign = if (diff >= 0) "+" else ""
            appendLine(" Variance:             PHP $sign${String.format(Locale.US, "%.2f", diff)}")
        }
        if (report.voidedCount > 0) {
            appendLine("--------------------------------")
            appendLine("VOIDED TX: ${report.voidedCount} (PHP ${String.format(Locale.US, "%.2f", report.voidedTotal)})")
        }
        if (report.notes.isNotBlank()) {
            appendLine("--------------------------------")
            appendLine("Notes: ${report.notes}")
        }
        appendLine("================================")
        appendLine("     SHIFT OFFICIALLY CLOSED    ")
        appendLine("================================")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Z-Reading: ${report.zReadNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = timeFmt.format(Date(report.generatedAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slip presentation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFC)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = storeProfile.storeName,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "*** OFFICIAL Z-READ REPORT ***",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = OutOfStockRed,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Report No: ${report.zReadNumber}",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ThermalReportRow("Period Start", timeFmt.format(Date(report.periodStart)))
                        ThermalReportRow("Period End", timeFmt.format(Date(report.periodEnd)))
                        ThermalReportRow("Gross Sales", "₱${String.format(Locale.US, "%.2f", report.grossSales)}")
                        ThermalReportRow("Total Discounts", "-₱${String.format(Locale.US, "%.2f", report.totalDiscounts)}")
                        ThermalReportRow("Net Sales", "₱${String.format(Locale.US, "%.2f", report.netSales)}", isBold = true, color = EmeraldPrimary)
                        ThermalReportRow("Net Profit", "₱${String.format(Locale.US, "%.2f", report.totalProfit)}", color = InStockGreen)
                        ThermalReportRow("Transactions", "${report.transactionCount} completed")
                        ThermalReportRow("Receipts", "#${report.firstReceiptNumber} to #${report.lastReceiptNumber}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text("PAYMENT METHOD TOTALS:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        ThermalReportRow("  Cash Sales", "₱${String.format(Locale.US, "%.2f", report.cashSales)}")
                        ThermalReportRow("  GCash / Maya", "₱${String.format(Locale.US, "%.2f", report.gcashSales)}")
                        ThermalReportRow("  Credit (Utang)", "₱${String.format(Locale.US, "%.2f", report.creditSales)}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - -",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            color = Color.LightGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text("CASH AUDIT & RECONCILIATION:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        ThermalReportRow("  Opening Float", "₱${String.format(Locale.US, "%.2f", report.openingFloat)}")
                        ThermalReportRow("  Cash POS Sales", "₱${String.format(Locale.US, "%.2f", report.cashSales)}")
                        ThermalReportRow("  Credit Payments", "₱${String.format(Locale.US, "%.2f", report.customerDebtPaymentsCollected)}")
                        ThermalReportRow("  Expected In Drawer", "₱${String.format(Locale.US, "%.2f", report.expectedCashInDrawer)}", isBold = true, color = CashGreen)

                        if (report.actualCashCounted != null) {
                            ThermalReportRow("  Actual Cash Count", "₱${String.format(Locale.US, "%.2f", report.actualCashCounted)}")
                            val diff = report.cashShortageOver ?: 0.0
                            val varColor = if (diff < -0.01) OutOfStockRed else if (diff > 0.01) GCashBlue else InStockGreen
                            val prefix = if (diff > 0) "+" else ""
                            ThermalReportRow("  Variance (Short/Over)", "$prefix₱${String.format(Locale.US, "%.2f", diff)}", isBold = true, color = varColor)
                        }

                        if (report.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Note: ${report.notes}", fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            printThermalReport(
                                context = context,
                                reportTitle = "TINDA_${report.zReadNumber}",
                                printHtml = generateHtmlSlip("OFFICIAL Z-READ REPORT", receiptPlainText, storeProfile)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptPlainText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Z-Read Report"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Done", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ZReadHistoryDialog(
    zReports: List<ZReadReport>,
    storeProfile: StoreProfile,
    onSelectReport: (ZReadReport) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateFmt = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Z-Read History Log",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${zReports.size} closed shift records archived",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (zReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Z-Reading records yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Execute a Z-Read at the end of the day or shift to archive sales.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(zReports) { report ->
                            Card(
                                onClick = { onSelectReport(report) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = report.zReadNumber,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${report.transactionCount} sales",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dateFmt.format(Date(report.generatedAt)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (report.notes.isNotBlank()) {
                                            Text(
                                                text = report.notes,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₱${String.format(Locale.US, "%.2f", report.netSales)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                        Text(
                                            text = "Exp: ₱${String.format(Locale.US, "%.2f", report.expectedCashInDrawer)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CashGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (zReports.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                val csvFile = com.example.util.CsvExportHelper.exportZReadReportsToCsv(context, zReports)
                                com.example.util.CsvExportHelper.shareFile(context, csvFile, "text/csv", "Export Z-Read History")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export CSV")
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ThermalReportRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (color != Color.Unspecified) color else Color.DarkGray,
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (color != Color.Unspecified) color else Color.Black,
            maxLines = 1
        )
    }
}

private fun generateHtmlSlip(title: String, plainText: String, storeProfile: StoreProfile): String {
    val linesHtml = plainText.lines().joinToString("<br/>") { line ->
        line.replace(" ", "&nbsp;")
    }
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <style>
                body {
                    font-family: 'Courier New', Courier, monospace;
                    font-size: 12px;
                    line-height: 1.35;
                    width: 280px;
                    margin: 0 auto;
                    padding: 10px;
                    color: #000;
                }
                .slip {
                    white-space: pre-wrap;
                    word-wrap: break-word;
                }
            </style>
        </head>
        <body>
            <div class="slip">$linesHtml</div>
        </body>
        </html>
    """.trimIndent()
}

private fun printThermalReport(context: Context, reportTitle: String, printHtml: String) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
    if (printManager == null) return

    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printAdapter = webView.createPrintDocumentAdapter(reportTitle)
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A7)
                .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                .build()
            printManager.print(reportTitle, printAdapter, printAttributes)
        }
    }
    webView.loadDataWithBaseURL(null, printHtml, "text/html", "UTF-8", null)
}
