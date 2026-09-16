package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SaleTransaction
import com.example.data.ZReadReport
import com.example.ui.components.TindaCard
import com.example.ui.theme.BorderElevated
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.InStockGreen
import com.example.ui.theme.OutOfStockRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UtangAmber
import com.example.viewmodel.HourlySalesBucket
import com.example.viewmodel.ReportPeriod
import com.example.viewmodel.TindaViewModel
import com.example.viewmodel.TopSellingItem
import com.example.viewmodel.XReadSnapshot
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: TindaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()
    val reportsState by viewModel.realtimeReports.collectAsStateWithLifecycle()

    var selectedSaleForReceipt by remember { mutableStateOf<SaleTransaction?>(null) }
    var saleToVoid by remember { mutableStateOf<SaleTransaction?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val allZReads by viewModel.allZReads.collectAsStateWithLifecycle()

    var activeXReadSnapshot by remember { mutableStateOf<XReadSnapshot?>(null) }
    var showZReadPrompt by remember { mutableStateOf<XReadSnapshot?>(null) }
    var selectedZReadReport by remember { mutableStateOf<ZReadReport?>(null) }
    var showZReadHistory by remember { mutableStateOf(false) }

    // Pulsing live indicator animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Real-time Database Status Header
        item {
            TindaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrandSurfaceElevated,
                borderColor = BorderElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(InStockGreen)
                                .alpha(pulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "REAL-TIME DATABASE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = InStockGreen,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldPrimary.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${reportsState.totalSalesRowsInDb} sales in DB",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                            val lastSyncStr = reportsState.lastTransactionTimestamp?.let {
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it))
                            } ?: "No transactions yet"
                            Text(
                                text = "Live synced with Room • Last sale: $lastSyncStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.addQuickTestSale("CASH") },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("test_realtime_sale_button")
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Sale", fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Report Period Selector & CSV Export Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        FilterChip(
                            selected = reportsState.period == ReportPeriod.TODAY,
                            onClick = { viewModel.setReportPeriod(ReportPeriod.TODAY) },
                            label = { Text("Today") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = reportsState.period == ReportPeriod.THIS_WEEK,
                            onClick = { viewModel.setReportPeriod(ReportPeriod.THIS_WEEK) },
                            label = { Text("This Week") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = reportsState.period == ReportPeriod.THIS_MONTH,
                            onClick = { viewModel.setReportPeriod(ReportPeriod.THIS_MONTH) },
                            label = { Text("This Month") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = reportsState.period == ReportPeriod.ALL_TIME,
                            onClick = { viewModel.setReportPeriod(ReportPeriod.ALL_TIME) },
                            label = { Text("All Time") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        val periodLabel = when (reportsState.period) {
                            ReportPeriod.TODAY -> "Today"
                            ReportPeriod.THIS_WEEK -> "This_Week"
                            ReportPeriod.THIS_MONTH -> "This_Month"
                            ReportPeriod.ALL_TIME -> "All_Time"
                        }
                        val csvFile = com.example.util.CsvExportHelper.exportSalesReportToCsv(
                            context = context,
                            periodName = periodLabel,
                            sales = reportsState.filteredSales
                        )
                        com.example.util.CsvExportHelper.shareFile(
                            context = context,
                            file = csvFile,
                            mimeType = "text/csv",
                            chooserTitle = "Export Sales CSV"
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("export_sales_csv_btn")
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Export CSV",
                        modifier = Modifier.size(16.dp),
                        tint = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                }
            }
        }

        // Top KPI Summary Cards (Gross Sales & Net Profit)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportMetricCard(
                    title = "Gross Sales",
                    value = "₱${String.format(Locale.US, "%.2f", reportsState.grossSales)}",
                    subtitle = "${reportsState.transactionsCount} completed sales",
                    badge = if (reportsState.grossSales > 0) "+${String.format(Locale.US, "%.1f", reportsState.profitMarginPercent)}% Margin" else null,
                    accentColor = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )

                ReportMetricCard(
                    title = "Net Profit",
                    value = "₱${String.format(Locale.US, "%.2f", reportsState.netProfit)}",
                    subtitle = "Sales revenue minus actual costs",
                    badge = "Real-time",
                    accentColor = InStockGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Transactions Count & Average Basket
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportMetricCard(
                    title = "Total Transactions",
                    value = "${reportsState.transactionsCount}",
                    subtitle = "Valid checkouts recorded",
                    badge = null,
                    accentColor = GCashBlue,
                    modifier = Modifier.weight(1f)
                )

                ReportMetricCard(
                    title = "Average Basket",
                    value = "₱${String.format(Locale.US, "%.2f", reportsState.averageBasket)}",
                    subtitle = "Average revenue per customer",
                    badge = null,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Cash Drawer & Utang Financial Reconciliation Card
        item {
            TindaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrandSurfaceElevated,
                borderColor = BorderElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = CashGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cash Drawer Reconciliation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CashGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Realtime", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CashGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = "Cash on Hand in Drawer (Today)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", reportsState.cashOnHandToday)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = CashGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = "Credit Collections Today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "+₱${String.format(Locale.US, "%.2f", reportsState.utangCollectedToday)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InStockGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Active Receivables (Credit)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₱${String.format(Locale.US, "%.2f", reportsState.totalOutstandingDebt)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = UtangAmber,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // X-Read & Z-Read Quick Action Bar
                    Text(
                        text = "POS Audit & Shift Registers",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    val snapshot = viewModel.generateCurrentXRead()
                                    activeXReadSnapshot = snapshot
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_x_read"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text("X-Reading", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldPrimary)
                                Text("Mid-Day Audit", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val snapshot = viewModel.generateCurrentXRead()
                                    showZReadPrompt = snapshot
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_z_read"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OutOfStockRed),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text("Z-Reading", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                Text("End of Day", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                showZReadHistory = true
                            },
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("btn_z_read_history"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text("Z History", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("(${allZReads.size})", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }

        // Top Selling Items (Bestsellers) Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with balanced alignment and prominent LIVE RANK pill badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Top Selling Products",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Bestsellers by revenue & volume",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Prominent Live Rank indicator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "LIVE RANK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (reportsState.topSellingItems.isEmpty()) {
                        Text(
                            text = "No sales items recorded for this period yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        reportsState.topSellingItems.forEachIndexed { index, item ->
                            TopSellingItemRow(rank = index + 1, item = item)
                            if (index < reportsState.topSellingItems.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

        // Hourly Sales Distribution (Peak Hours / Oras sa Halin)
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Peak Hours (Oras sa Halin)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Time of Day",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    reportsState.hourlyBreakdown.forEach { bucket ->
                        HourlyBucketRow(bucket = bucket, totalSales = reportsState.grossSales)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Real-time Inventory Valuation Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Inventory Valuation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Current Stocks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Stock Retail Value",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", reportsState.inventoryRetailValue)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cost Invested",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", reportsState.inventoryCostValue)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Projected Profit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", reportsState.projectedInventoryProfit)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InStockGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Payment Methods Breakdown Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment Methods Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentMethodRow(
                        label = "Cash",
                        amount = reportsState.cashSales,
                        total = reportsState.grossSales,
                        color = CashGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentMethodRow(
                        label = "GCash / Maya",
                        amount = reportsState.gcashSales,
                        total = reportsState.grossSales,
                        color = GCashBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentMethodRow(
                        label = "Customer Credit",
                        amount = reportsState.utangSales,
                        total = reportsState.grossSales,
                        color = UtangAmber
                    )
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions (${reportsState.filteredSales.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap to view receipt",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Transactions List
        if (reportsState.filteredSales.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No sales recorded for this period",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(reportsState.filteredSales.take(30), key = { it.id }) { sale ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(sale.timestamp))
                val methodColor = when (sale.paymentMethod) {
                    "CASH" -> CashGreen
                    "GCASH_MAYA" -> GCashBlue
                    else -> UtangAmber
                }

                TindaCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSaleForReceipt = sale }
                        .testTag("sale_item_${sale.id}"),
                    backgroundColor = BrandSurfaceElevated,
                    borderColor = BorderElevated
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Text(
                                    text = sale.receiptNumber,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(methodColor.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = when (sale.paymentMethod) {
                                            "CASH" -> "CASH"
                                            "GCASH_MAYA" -> "GCASH"
                                            else -> "UTANG"
                                        },
                                        color = methodColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", sale.finalAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = sale.itemsSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Profit: ₱${String.format(Locale.US, "%.2f", sale.profit)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InStockGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { selectedSaleForReceipt = sale },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Receipt", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                TextButton(
                                    onClick = { saleToVoid = sale },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Text("Void", color = OutOfStockRed, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Receipt viewer
    if (selectedSaleForReceipt != null) {
        val sale = selectedSaleForReceipt!!
        DigitalReceiptDialog(
            sale = sale,
            storeName = storeProfile.storeName,
            storePhone = storeProfile.storePhone,
            storeAddress = storeProfile.storeAddress,
            receiptMessage = storeProfile.receiptMessage,
            onDismiss = { selectedSaleForReceipt = null },
            onShare = { text ->
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Receipt")
                context.startActivity(shareIntent)
            }
        )
    }

    // Void sale confirmation dialog
    if (saleToVoid != null) {
        val sale = saleToVoid!!
        AlertDialog(
            onDismissRequest = { saleToVoid = null },
            title = { Text("Void Transaction?") },
            text = {
                Text("Are you sure you want to void Receipt #${sale.receiptNumber} (₱${String.format(Locale.US, "%.2f", sale.finalAmount)})? This will remove it from sales totals and reverse any customer credit balance adjustments.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.voidTransaction(sale.id)
                        saleToVoid = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OutOfStockRed)
                ) {
                    Text("Confirm Void")
                }
            },
            dismissButton = {
                TextButton(onClick = { saleToVoid = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // X-Reading Dialog
    if (activeXReadSnapshot != null) {
        XReadDialog(
            snapshot = activeXReadSnapshot!!,
            storeProfile = storeProfile,
            onDismiss = { activeXReadSnapshot = null }
        )
    }

    // Z-Reading Prompt / Actual Reconciliation Dialog
    if (showZReadPrompt != null) {
        ZReadPromptDialog(
            snapshot = showZReadPrompt!!,
            storeProfile = storeProfile,
            onDismiss = { showZReadPrompt = null },
            onConfirmZRead = { actualCash, notes ->
                viewModel.performZRead(actualCash, notes) { generatedZReport ->
                    showZReadPrompt = null
                    selectedZReadReport = generatedZReport
                }
            }
        )
    }

    // Z-Reading Detail & Print Slip Dialog
    if (selectedZReadReport != null) {
        ZReadDetailDialog(
            report = selectedZReadReport!!,
            storeProfile = storeProfile,
            onDismiss = { selectedZReadReport = null }
        )
    }

    // Z-Reading Historical Archive Viewer
    if (showZReadHistory) {
        ZReadHistoryDialog(
            zReports = allZReads,
            storeProfile = storeProfile,
            onSelectReport = { report ->
                selectedZReadReport = report
            },
            onDismiss = { showZReadHistory = false }
        )
    }
}

@Composable
fun TopSellingItemRow(rank: Int, item: TopSellingItem) {
    val (rankBadgeBg, rankBadgeBorder, rankBadgeText) = when (rank) {
        1 -> Triple(Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFFB45309)) // Gold
        2 -> Triple(Color(0xFFF1F5F9), Color(0xFF94A3B8), Color(0xFF334155)) // Silver
        3 -> Triple(Color(0xFFFFEDD5), Color(0xFFFB923C), Color(0xFFC2410C)) // Bronze
        else -> Triple(Color(0xFFF8FAFC), Color(0xFFCBD5E1), Color(0xFF64748B))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Badge + Product Name & Volume
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = rankBadgeBg,
                        border = BorderStroke(1.dp, rankBadgeBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "#$rank",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = rankBadgeText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.quantitySold} units sold  •  ${String.format(Locale.US, "%.1f", item.percentageOfSales)}% share",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Prominent Gross Sales Column (Crystal clear & high readability)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Gross Sales",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "₱${String.format(Locale.US, "%,.2f", item.totalRevenue)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Percentage bar
            LinearProgressIndicator(
                progress = { (item.percentageOfSales / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EmeraldPrimary,
                trackColor = EmeraldPrimary.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
fun HourlyBucketRow(bucket: HourlySalesBucket, totalSales: Double) {
    val progress = if (totalSales > 0) (bucket.salesAmount / totalSales).toFloat().coerceIn(0f, 1f) else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bucket.timeSlot,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "₱${String.format(Locale.US, "%.2f", bucket.salesAmount)} (${bucket.transactionCount} orders)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (bucket.salesAmount > 0) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = EmeraldPrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun ReportMetricCard(
    title: String,
    value: String,
    subtitle: String,
    badge: String? = null,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    TindaCard(
        modifier = modifier,
        backgroundColor = BrandSurfaceElevated,
        borderColor = BorderElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PaymentMethodRow(
    label: String,
    amount: Double,
    total: Double,
    color: Color
) {
    val progress = if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f
    val percent = if (total > 0) (amount / total) * 100 else 0.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "₱${String.format(Locale.US, "%.2f", amount)} (${String.format(Locale.US, "%.0f", percent)}%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

