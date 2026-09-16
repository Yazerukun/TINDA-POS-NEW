package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SaleTransaction
import com.example.ui.components.MoneyText
import com.example.ui.components.TindaButton
import com.example.ui.components.TindaCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.CashGreen
import com.example.ui.theme.DangerSoftRed
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UtangAmber
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.TindaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: TindaViewModel,
    onNavigateToPos: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToCredit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()
    val reportsState by viewModel.realtimeReports.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val expiredProducts by viewModel.expiredProducts.collectAsStateWithLifecycle()
    val expiringSoonProducts by viewModel.expiringSoonProducts.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val totalDebtAmount = allCustomers.sumOf { it.totalDebt }

    val attentionCount = lowStockProducts.size + expiredProducts.size + expiringSoonProducts.size
    val recentSales = remember(reportsState.filteredSales) {
        reportsState.filteredSales.sortedByDescending { it.timestamp }.take(5)
    }

    var selectedSaleForReceipt by remember { mutableStateOf<SaleTransaction?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("home_screen")
    ) {
        val isTablet = maxWidth >= 720.dp

        if (isTablet) {
            // Adaptive Two-Column Tablet Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left Column: Hero Overview & Primary POS Launch
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeHeroCard(
                        grossSales = reportsState.grossSales,
                        transactionCount = reportsState.filteredSales.size,
                        netProfit = reportsState.netProfit,
                        onOpenPos = onNavigateToPos
                    )

                    // Operations Grid
                    HomeOperationsGrid(
                        totalDebtAmount = totalDebtAmount,
                        startingCashDrawer = storeProfile.startingCashDrawer,
                        onNavigateToInventory = onNavigateToInventory,
                        onNavigateToCredit = onNavigateToCredit,
                        onNavigateToReports = onNavigateToReports
                    )
                }

                // Right Column: Inventory Health & Recent Sales Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HomeInventoryHealthCard(
                            attentionCount = attentionCount,
                            lowStockCount = lowStockProducts.size,
                            expiredCount = expiredProducts.size,
                            expiringSoonCount = expiringSoonProducts.size,
                            onClick = onNavigateToInventory
                        )
                    }

                    item {
                        HomeRecentSalesSection(
                            recentSales = recentSales,
                            onSaleClick = { selectedSaleForReceipt = it },
                            onViewAll = onNavigateToReports
                        )
                    }
                }
            }
        } else {
            // Phone Single-Column Layout
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Card: Today's Sales & Primary CTA
                item {
                    HomeHeroCard(
                        grossSales = reportsState.grossSales,
                        transactionCount = reportsState.filteredSales.size,
                        netProfit = reportsState.netProfit,
                        onOpenPos = onNavigateToPos
                    )
                }

                // Inventory Health Banner
                item {
                    HomeInventoryHealthCard(
                        attentionCount = attentionCount,
                        lowStockCount = lowStockProducts.size,
                        expiredCount = expiredProducts.size,
                        expiringSoonCount = expiringSoonProducts.size,
                        onClick = onNavigateToInventory
                    )
                }

                // Quick Access 2x2 Retail Hub
                item {
                    HomeOperationsGrid(
                        totalDebtAmount = totalDebtAmount,
                        startingCashDrawer = storeProfile.startingCashDrawer,
                        onNavigateToInventory = onNavigateToInventory,
                        onNavigateToCredit = onNavigateToCredit,
                        onNavigateToReports = onNavigateToReports
                    )
                }

                // Today's Recent Sales Activity Feed
                item {
                    HomeRecentSalesSection(
                        recentSales = recentSales,
                        onSaleClick = { selectedSaleForReceipt = it },
                        onViewAll = onNavigateToReports
                    )
                }
            }
        }
    }

    if (selectedSaleForReceipt != null) {
        val sale = selectedSaleForReceipt!!
        DigitalReceiptDialog(
            sale = sale,
            storeName = storeProfile.storeName,
            storePhone = storeProfile.storePhone,
            storeAddress = storeProfile.storeAddress,
            receiptMessage = storeProfile.receiptMessage,
            onDismiss = { selectedSaleForReceipt = null },
            onShare = { receiptText: String ->
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, receiptText)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Receipt")
                context.startActivity(shareIntent)
            }
        )
    }
}

@Composable
private fun HomeHeroCard(
    grossSales: Double,
    transactionCount: Int,
    netProfit: Double,
    onOpenPos: () -> Unit
) {
    TindaCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = BrandSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Sales",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MoneyText(
                        amount = grossSales,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = null,
                        tint = EmeraldInteractive,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickMetricTile(
                    label = "Transactions",
                    value = "$transactionCount",
                    modifier = Modifier.weight(1f)
                )
                QuickMetricTile(
                    label = "Est. Profit",
                    value = "₱${String.format(Locale.US, "%,.2f", netProfit)}",
                    valueColor = SuccessEmerald,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            TindaButton(
                text = "Open POS Terminal",
                onClick = onOpenPos,
                icon = Icons.Default.AddShoppingCart,
                modifier = Modifier.fillMaxWidth(),
                testTag = "home_start_sale_btn"
            )
        }
    }
}

@Composable
private fun HomeInventoryHealthCard(
    attentionCount: Int,
    lowStockCount: Int,
    expiredCount: Int,
    expiringSoonCount: Int,
    onClick: () -> Unit
) {
    if (attentionCount > 0) {
        TindaCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = WarningAmber.copy(alpha = 0.10f),
            borderColor = WarningAmber.copy(alpha = 0.35f),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarningAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$attentionCount items need attention",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$lowStockCount low stock, $expiredCount expired, $expiringSoonCount expiring soon",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    } else {
        TindaCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SuccessEmerald.copy(alpha = 0.08f),
            borderColor = SuccessEmerald.copy(alpha = 0.25f),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessEmerald,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Inventory is healthy",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "All stock levels and dates are well within normal ranges",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeOperationsGrid(
    totalDebtAmount: Double,
    startingCashDrawer: Double,
    onNavigateToInventory: () -> Unit,
    onNavigateToCredit: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "QUICK OPERATIONS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Inventory",
                subtitle = "Stock & Items",
                icon = Icons.Default.Inventory2,
                iconTint = EmeraldInteractive,
                onClick = onNavigateToInventory,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Credit / Utang",
                subtitle = "₱${String.format(Locale.US, "%,.0f", totalDebtAmount)} balance",
                icon = Icons.Default.AccountBalanceWallet,
                iconTint = WarningAmber,
                onClick = onNavigateToCredit,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Analytics",
                subtitle = "Reports & Profit",
                icon = Icons.Default.QueryStats,
                iconTint = Color(0xFF60A5FA),
                onClick = onNavigateToReports,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Cash Drawer",
                subtitle = "Float ₱${String.format(Locale.US, "%,.0f", startingCashDrawer)}",
                icon = Icons.Default.Store,
                iconTint = Color(0xFFA78BFA),
                onClick = onNavigateToReports,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HomeRecentSalesSection(
    recentSales: List<SaleTransaction>,
    onSaleClick: (SaleTransaction) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT SALES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp,
                color = TextMuted
            )
            if (recentSales.isNotEmpty()) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldInteractive,
                    modifier = Modifier.clickable(onClick = onViewAll)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (recentSales.isEmpty()) {
            TindaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrandSurfaceElevated
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No sales recorded yet today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Start adding items in the POS terminal to see transactions here",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.US) }

                recentSales.forEach { sale ->
                    TindaCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = BrandSurfaceElevated,
                        onClick = { onSaleClick(sale) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BrandSurfaceSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "#${sale.receiptNumber}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${timeFormat.format(Date(sale.timestamp))} • ${sale.paymentMethod.replace("_", " ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                MoneyText(
                                    amount = sale.finalAmount,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sale.isVoided) DangerSoftRed else EmeraldInteractive
                                )
                                if (sale.isVoided) {
                                    Text(
                                        text = "VOIDED",
                                        color = DangerSoftRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
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
private fun QuickMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = BrandSurfaceSoft
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TindaCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
