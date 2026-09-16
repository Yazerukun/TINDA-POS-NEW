package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Product
import com.example.data.SampleData
import com.example.ui.components.TindaCard
import com.example.ui.theme.BorderElevated
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.InStockGreen
import com.example.ui.theme.LowStockOrange
import com.example.ui.theme.OutOfStockRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TindaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: TindaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val expiredProducts by viewModel.expiredProducts.collectAsStateWithLifecycle()
    val expiringSoonProducts by viewModel.expiringSoonProducts.collectAsStateWithLifecycle()
    val needDateReviewProducts by viewModel.needDateReviewProducts.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    var productToReviewDate by remember { mutableStateOf<Product?>(null) }

    var showRestockDialog by remember { mutableStateOf(false) }
    var productToRestock by remember { mutableStateOf<Product?>(null) }

    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val currentList = when (selectedTabIndex) {
        1 -> lowStockProducts
        2 -> expiredProducts
        3 -> expiringSoonProducts
        4 -> needDateReviewProducts
        else -> allProducts
    }.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.barcode.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    val totalCostValue = allProducts.sumOf { it.costPrice * it.stockQuantity }
    val totalRetailValue = allProducts.sumOf { it.sellingPrice * it.stockQuantity }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // KPI Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InventoryKpiCard(
                    title = "Total Items",
                    value = "${allProducts.size}",
                    subtitle = "${allProducts.sumOf { it.stockQuantity }} units on hand",
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )

                InventoryKpiCard(
                    title = "Low Stock Alert",
                    value = "${lowStockProducts.size}",
                    subtitle = "Needs restock",
                    color = if (lowStockProducts.isNotEmpty()) LowStockOrange else InStockGreen,
                    modifier = Modifier.weight(1f)
                )

                InventoryKpiCard(
                    title = "Stock Value",
                    value = "₱${String.format(Locale.US, "%.0f", totalRetailValue)}",
                    subtitle = "Cost: ₱${String.format(Locale.US, "%.0f", totalCostValue)}",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1.2f)
                )
            }

            // Expiration Alerts Row: "0 expired", "0 expiring soon", "0 need date review"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpirationAlertCard(
                    count = expiredProducts.size,
                    label = "expired",
                    isSelected = selectedTabIndex == 2,
                    badgeColor = OutOfStockRed,
                    icon = Icons.Default.Cancel,
                    onClick = {
                        selectedTabIndex = if (selectedTabIndex == 2) 0 else 2
                    },
                    modifier = Modifier.weight(1f)
                )

                ExpirationAlertCard(
                    count = expiringSoonProducts.size,
                    label = "expiring soon",
                    isSelected = selectedTabIndex == 3,
                    badgeColor = LowStockOrange,
                    icon = Icons.Default.AccessTime,
                    onClick = {
                        selectedTabIndex = if (selectedTabIndex == 3) 0 else 3
                    },
                    modifier = Modifier.weight(1.15f)
                )

                ExpirationAlertCard(
                    count = needDateReviewProducts.size,
                    label = "need date review",
                    isSelected = selectedTabIndex == 4,
                    badgeColor = Color(0xFF1976D2),
                    icon = Icons.Default.EditCalendar,
                    onClick = {
                        selectedTabIndex = if (selectedTabIndex == 4) 0 else 4
                    },
                    modifier = Modifier.weight(1.35f)
                )
            }

            // Search Bar & CSV Export Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("inventory_search_input"),
                    placeholder = { Text("Search inventory by name, barcode...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        val csvFile = com.example.util.CsvExportHelper.exportInventoryToCsv(
                            context = context,
                            products = allProducts
                        )
                        com.example.util.CsvExportHelper.shareFile(
                            context = context,
                            file = csvFile,
                            mimeType = "text/csv",
                            chooserTitle = "Export Inventory CSV"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp),
                    modifier = Modifier.testTag("export_inventory_csv_btn")
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Export Inventory CSV",
                        modifier = Modifier.size(16.dp),
                        tint = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                }
            }

            // Scrollable Tabs: All, Low Stock, Expired, Expiring Soon, Need Date Review
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("All (${allProducts.size})") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (lowStockProducts.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = LowStockOrange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Low Stock (${lowStockProducts.size})")
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (expiredProducts.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = OutOfStockRed,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Expired (${expiredProducts.size})")
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (expiringSoonProducts.isNotEmpty()) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = LowStockOrange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Expiring Soon (${expiringSoonProducts.size})")
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (needDateReviewProducts.isNotEmpty()) {
                                Icon(
                                    Icons.Default.EditCalendar,
                                    contentDescription = null,
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Need Review (${needDateReviewProducts.size})")
                        }
                    }
                )
            }

            // Product List
            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val emptyMessage = when (selectedTabIndex) {
                            1 -> "No low stock items! Great inventory balance."
                            2 -> "No expired products! All goods are fresh."
                            3 -> "No products expiring within 30 days."
                            4 -> "All products have expiration dates reviewed!"
                            else -> "No products found"
                        }
                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("inventory_product_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentList, key = { it.id }) { product ->
                        InventoryProductCard(
                            product = product,
                            onRestock = {
                                productToRestock = product
                                showRestockDialog = true
                            },
                            onEdit = {
                                productToEdit = product
                                showAddEditDialog = true
                            },
                            onDelete = {
                                productToDelete = product
                            },
                            onReviewExpiry = {
                                productToReviewDate = product
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Floating Action Button to Add Product
        FloatingActionButton(
            onClick = {
                productToEdit = null
                showAddEditDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_product_fab"),
            containerColor = EmeraldPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add New Product")
        }
    }

    // Add / Edit Product Dialog
    if (showAddEditDialog) {
        AddEditProductDialog(
            initialProduct = productToEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, barcode, category, cost, selling, stock, unit, threshold, expiryDate ->
                viewModel.saveProduct(
                    id = productToEdit?.id ?: 0L,
                    name = name,
                    barcode = barcode,
                    category = category,
                    costPrice = cost,
                    sellingPrice = selling,
                    stockQuantity = stock,
                    unit = unit,
                    lowStockThreshold = threshold,
                    expiryDate = expiryDate
                )
                showAddEditDialog = false
            }
        )
    }

    // Quick Date Review Dialog
    if (productToReviewDate != null) {
        QuickDateReviewDialog(
            product = productToReviewDate!!,
            onDismiss = { productToReviewDate = null },
            onSave = { newExpiry ->
                viewModel.updateProductExpiryDate(productToReviewDate!!.id, newExpiry)
                productToReviewDate = null
            }
        )
    }

    // Restock Dialog
    if (showRestockDialog && productToRestock != null) {
        val prod = productToRestock!!
        var addQtyStr by remember { mutableStateOf("10") }
        var notes by remember { mutableStateOf("Supplier restock") }

        AlertDialog(
            onDismissRequest = { showRestockDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stock In / Restock")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(prod.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Current on-hand: ${prod.stockQuantity} ${prod.unit}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = addQtyStr,
                        onValueChange = { addQtyStr = it },
                        label = { Text("Quantity to Add *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Restock Note / Supplier") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = addQtyStr.toIntOrNull() ?: 0
                        if (qty > 0) {
                            viewModel.restockProduct(prod.id, qty, notes)
                            showRestockDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_restock_btn")
                ) {
                    Text("Confirm Restock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product?") },
            text = { Text("Are you sure you want to remove '${prod.name}' from your inventory?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(prod)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OutOfStockRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InventoryKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    TindaCard(
        modifier = modifier,
        backgroundColor = BrandSurfaceElevated,
        borderColor = BorderElevated
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

fun formatExpiryDate(timestamp: Long?): String {
    if (timestamp == null || timestamp <= 0L) return "No date"
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return sdf.format(Date(timestamp))
}

@Composable
fun ExpirationAlertCard(
    count: Int,
    label: String,
    isSelected: Boolean,
    badgeColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TindaCard(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("alert_${label.replace(' ', '_')}"),
        backgroundColor = if (isSelected) badgeColor.copy(alpha = 0.18f) else BrandSurfaceElevated,
        borderColor = if (isSelected) badgeColor else BorderSubtle
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (count > 0) badgeColor else TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) badgeColor else TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun InventoryProductCard(
    product: Product,
    onRestock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReviewExpiry: () -> Unit
) {
    val now = System.currentTimeMillis()
    val thirtyDays = now + (30L * 24 * 60 * 60 * 1000)
    val expiry = product.expiryDate
    val isExpired = expiry != null && expiry > 0L && expiry < now
    val isExpiringSoon = expiry != null && expiry > 0L && expiry >= now && expiry <= thirtyDays
    val needsReview = expiry == null || expiry == 0L

    TindaCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${product.id}"),
        backgroundColor = BrandSurfaceElevated,
        borderColor = BorderElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        if (product.barcode.isNotBlank()) {
                            Text(" • ", color = TextMuted)
                            Text(
                                text = product.barcode,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Stock Badge
                val isLow = product.stockQuantity <= product.lowStockThreshold
                val isOut = product.stockQuantity <= 0
                val badgeBg = when {
                    isOut -> OutOfStockRed.copy(alpha = 0.15f)
                    isLow -> LowStockOrange.copy(alpha = 0.15f)
                    else -> InStockGreen.copy(alpha = 0.15f)
                }
                val badgeText = when {
                    isOut -> OutOfStockRed
                    isLow -> LowStockOrange
                    else -> InStockGreen
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${product.stockQuantity} ${product.unit}",
                        color = badgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Expiry Status Banner
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when {
                    isExpired -> {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(OutOfStockRed.copy(alpha = 0.15f))
                                .clickable(onClick = onReviewExpiry)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = OutOfStockRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Expired: ${formatExpiryDate(expiry)}",
                                color = OutOfStockRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    isExpiringSoon -> {
                        val daysLeft = ((expiry!! - now) / (24 * 3600 * 1000)).coerceAtLeast(0)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LowStockOrange.copy(alpha = 0.15f))
                                .clickable(onClick = onReviewExpiry)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = LowStockOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Expiring soon: ${formatExpiryDate(expiry)} (${daysLeft}d left)",
                                color = LowStockOrange,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    needsReview -> {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E3A5F).copy(alpha = 0.5f))
                                .clickable(onClick = onReviewExpiry)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Need date review",
                                color = Color(0xFF60A5FA),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    else -> {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandSurfaceSoft)
                                .clickable(onClick = onReviewExpiry)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Exp: ${formatExpiryDate(expiry)}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onReviewExpiry,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldInteractive)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Date", fontSize = 11.sp, color = EmeraldInteractive, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Margins
            val profitMargin = if (product.sellingPrice > 0) {
                ((product.sellingPrice - product.costPrice) / product.sellingPrice) * 100
            } else 0.0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Selling Price", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            "₱${String.format(Locale.US, "%.2f", product.sellingPrice)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldInteractive
                        )
                    }

                    Column {
                        Text("Cost Price", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            "₱${String.format(Locale.US, "%.2f", product.costPrice)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Column {
                        Text("Margin", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            "${String.format(Locale.US, "%.0f", profitMargin)}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (profitMargin > 15) InStockGreen else LowStockOrange
                        )
                    }
                }

                // Actions: Restock, Edit, Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onRestock,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("restock_btn_${product.id}"),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Stock", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp), tint = TextSecondary)
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = OutOfStockRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickDateReviewDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Long?) -> Unit
) {
    val context = LocalContext.current
    var selectedExpiry by remember { mutableStateOf(product.expiryDate) }
    val now = System.currentTimeMillis()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EditCalendar, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Expiry Date Review", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Category: ${product.category} • Current stock: ${product.stockQuantity} ${product.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text("Expiration Date:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedExpiry == null || selectedExpiry == 0L) "No Date (Non-perishable)" else formatExpiryDate(selectedExpiry),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedExpiry == null || selectedExpiry == 0L) Color(0xFF1976D2) else MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            selectedExpiry?.let { cal.timeInMillis = it }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selected = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, 23, 59, 59)
                                    }
                                    selectedExpiry = selected.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Date", fontSize = 12.sp)
                    }
                }

                Text("Quick Presets:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = selectedExpiry == null,
                        onClick = { selectedExpiry = null },
                        label = { Text("None", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { selectedExpiry = now + (30L * 24 * 3600 * 1000) },
                        label = { Text("+1 Mo", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { selectedExpiry = now + (90L * 24 * 3600 * 1000) },
                        label = { Text("+3 Mo", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { selectedExpiry = now + (180L * 24 * 3600 * 1000) },
                        label = { Text("+6 Mo", fontSize = 11.sp) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedExpiry)
                }
            ) {
                Text("Save Date")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEditProductDialog(
    initialProduct: Product?,
    onDismiss: () -> Unit,
    onSave: (name: String, barcode: String, category: String, cost: Double, selling: Double, stock: Int, unit: String, threshold: Int, expiryDate: Long?) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var barcode by remember { mutableStateOf(initialProduct?.barcode ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "Beverages") }
    var costStr by remember { mutableStateOf(if (initialProduct != null) String.format(Locale.US, "%.2f", initialProduct.costPrice) else "") }
    var sellingStr by remember { mutableStateOf(if (initialProduct != null) String.format(Locale.US, "%.2f", initialProduct.sellingPrice) else "") }
    var stockStr by remember { mutableStateOf(initialProduct?.stockQuantity?.toString() ?: "10") }
    var unit by remember { mutableStateOf(initialProduct?.unit ?: "pc") }
    var thresholdStr by remember { mutableStateOf(initialProduct?.lowStockThreshold?.toString() ?: "5") }
    var expiryDateMillis by remember { mutableStateOf(initialProduct?.expiryDate) }

    val isEditing = initialProduct != null
    val now = System.currentTimeMillis()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditing) "Edit Product" else "Add New Product", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode / SKU") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            barcode = "480" + (10000000..99999999).random()
                        }
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Generate Barcode", tint = EmeraldPrimary)
                    }
                }

                // Category selection
                Text("Category", style = MaterialTheme.typography.labelSmall)
                LazyColumn(modifier = Modifier.height(100.dp)) {
                    items(SampleData.categories.filter { it != "All" }) { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { category = cat }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cat, style = MaterialTheme.typography.bodySmall)
                            if (category == cat) {
                                Text("✓", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = costStr,
                        onValueChange = { costStr = it },
                        label = { Text("Cost Price ₱") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = sellingStr,
                        onValueChange = { sellingStr = it },
                        label = { Text("Selling Price ₱ *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_selling_price_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("Stock Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (pc/can/kg)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = thresholdStr,
                    onValueChange = { thresholdStr = it },
                    label = { Text("Low Stock Alert Threshold") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Expiration Date Section
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Expiration Date", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (expiryDateMillis == null || expiryDateMillis == 0L) "No Expiry (Non-perishable)" else formatExpiryDate(expiryDateMillis),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (expiryDateMillis == null || expiryDateMillis == 0L) Color(0xFF1976D2) else MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            expiryDateMillis?.let { cal.timeInMillis = it }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selected = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, 23, 59, 59)
                                    }
                                    expiryDateMillis = selected.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Date", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = expiryDateMillis == null,
                        onClick = { expiryDateMillis = null },
                        label = { Text("None", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expiryDateMillis = now + (30L * 24 * 3600 * 1000) },
                        label = { Text("+1 Mo", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expiryDateMillis = now + (90L * 24 * 3600 * 1000) },
                        label = { Text("+3 Mo", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expiryDateMillis = now + (180L * 24 * 3600 * 1000) },
                        label = { Text("+6 Mo", fontSize = 11.sp) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selling = sellingStr.toDoubleOrNull() ?: 0.0
                    val cost = costStr.toDoubleOrNull() ?: 0.0
                    val stock = stockStr.toIntOrNull() ?: 0
                    val threshold = thresholdStr.toIntOrNull() ?: 5
                    if (name.isNotBlank() && selling > 0) {
                        onSave(name, barcode, category, cost, selling, stock, unit, threshold, expiryDateMillis)
                    }
                },
                enabled = name.isNotBlank() && (sellingStr.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("save_product_btn")
            ) {
                Text(if (isEditing) "Save Changes" else "Add Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
