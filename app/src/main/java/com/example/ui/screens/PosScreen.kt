package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CartItem
import com.example.data.CustomerDebt
import com.example.data.Product
import com.example.data.SaleTransaction
import com.example.data.SampleData
import com.example.ui.components.MoneyText
import com.example.ui.components.ProductStockBadge
import com.example.ui.components.TindaButton
import com.example.ui.components.TindaCard
import com.example.ui.components.TindaEmptyState
import com.example.ui.theme.BorderElevated
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfacePrimary
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.InStockGreen
import com.example.ui.theme.LowStockOrange
import com.example.ui.theme.OutOfStockRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UtangAmber
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.TindaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    viewModel: TindaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val discountType by viewModel.discountType.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val cashTendered by viewModel.cashTendered.collectAsStateWithLifecycle()
    val selectedDebtor by viewModel.selectedDebtor.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val lastSale by viewModel.lastCompletedSale.collectAsStateWithLifecycle()
    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()

    var showCartSheet by remember { mutableStateOf(false) }
    var showBarcodeDialog by remember { mutableStateOf(false) }
    var barcodeInput by remember { mutableStateOf("") }
    var barcodeError by remember { mutableStateOf<String?>(null) }
    var showAddDebtorDialog by remember { mutableStateOf(false) }

    val cartCount = cartItems.sumOf { it.quantity }
    val cartSubtotal = viewModel.getCartSubtotal()
    val finalTotal = viewModel.getFinalAmount()

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(BrandBackground)) {
        val isTabletSplit = maxWidth >= 720.dp

        if (isTabletSplit) {
            // Dual-pane tablet layout: Catalog left (60%), Cart & Checkout right (40%)
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                ) {
                    PosSearchAndCategories(
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.onSearchQueryChange(it) },
                        selectedCategory = selectedCategory,
                        onCategorySelect = { viewModel.onCategorySelect(it) },
                        onScanBarcode = {
                            barcodeInput = ""
                            barcodeError = null
                            showBarcodeDialog = true
                        }
                    )

                    PosProductGrid(
                        products = products,
                        cartItems = cartItems,
                        onAddToCart = { prod ->
                            if (prod.stockQuantity > 0) viewModel.addToCart(prod)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = BorderSubtle,
                    thickness = 1.dp
                )

                Surface(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight(),
                    color = BrandSurfacePrimary
                ) {
                    CheckoutSheetContent(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        discountType = discountType,
                        finalTotal = finalTotal,
                        paymentMethod = paymentMethod,
                        cashTendered = cashTendered,
                        selectedDebtor = selectedDebtor,
                        allCustomers = allCustomers,
                        storeProfile = storeProfile,
                        onUpdateQty = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onRemove = { viewModel.removeFromCart(it) },
                        onClear = { viewModel.clearCart() },
                        onSetDiscount = { type, amt -> viewModel.setDiscountType(type, amt) },
                        onSetPaymentMethod = { viewModel.setPaymentMethod(it) },
                        onSetCashTendered = { viewModel.setCashTendered(it) },
                        onSelectDebtor = { viewModel.selectDebtor(it) },
                        onAddNewDebtor = { showAddDebtorDialog = true },
                        onConfirmCheckout = {
                            viewModel.completeCheckout {}
                        }
                    )
                }
            }
        } else {
            // Phone single-pane layout with floating cart summary bar
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (cartCount > 0) 86.dp else 0.dp)
            ) {
                PosSearchAndCategories(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.onSearchQueryChange(it) },
                    selectedCategory = selectedCategory,
                    onCategorySelect = { viewModel.onCategorySelect(it) },
                    onScanBarcode = {
                        barcodeInput = ""
                        barcodeError = null
                        showBarcodeDialog = true
                    }
                )

                PosProductGrid(
                    products = products,
                    cartItems = cartItems,
                    onAddToCart = { prod ->
                        if (prod.stockQuantity > 0) viewModel.addToCart(prod)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Floating Cart Summary Bar for Phones
            if (cartCount > 0) {
                TindaCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("pos_cart_bar"),
                    onClick = { showCartSheet = true },
                    backgroundColor = BrandSurfaceElevated,
                    borderColor = EmeraldInteractive.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(targetState = cartCount, label = "cart_badge") { count ->
                                    Text(
                                        text = "$count",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Current Order",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                                MoneyText(
                                    amount = finalTotal,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldInteractive
                                )
                            }
                        }

                        TindaButton(
                            text = "Checkout",
                            onClick = { showCartSheet = true },
                            modifier = Modifier.testTag("view_cart_btn")
                        )
                    }
                }
            }
        }
    }

    // Cart Bottom Sheet / Checkout Modal
    if (showCartSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = sheetState
        ) {
            CheckoutSheetContent(
                cartItems = cartItems,
                subtotal = cartSubtotal,
                discountType = discountType,
                finalTotal = finalTotal,
                paymentMethod = paymentMethod,
                cashTendered = cashTendered,
                selectedDebtor = selectedDebtor,
                allCustomers = allCustomers,
                storeProfile = storeProfile,
                onUpdateQty = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                onRemove = { viewModel.removeFromCart(it) },
                onClear = {
                    viewModel.clearCart()
                    showCartSheet = false
                },
                onSetDiscount = { type, amt -> viewModel.setDiscountType(type, amt) },
                onSetPaymentMethod = { viewModel.setPaymentMethod(it) },
                onSetCashTendered = { viewModel.setCashTendered(it) },
                onSelectDebtor = { viewModel.selectDebtor(it) },
                onAddNewDebtor = { showAddDebtorDialog = true },
                onConfirmCheckout = {
                    viewModel.completeCheckout {
                        showCartSheet = false
                    }
                }
            )
        }
    }

    // Barcode Quick Scan Dialog
    if (showBarcodeDialog) {
        AlertDialog(
            onDismissRequest = { showBarcodeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Barcode Scanner")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Enter product barcode or tap a quick-scan product below:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = barcodeInput,
                        onValueChange = {
                            barcodeInput = it
                            barcodeError = null
                        },
                        label = { Text("Barcode Number") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("barcode_input_field"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    if (barcodeError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = barcodeError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Quick Barcode Presets:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.height(150.dp)) {
                        items(allProducts.take(6)) { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        barcodeInput = p.barcode
                                        val found = viewModel.scanBarcodeAndAddToCart(p.barcode)
                                        if (found) {
                                            showBarcodeDialog = false
                                        } else {
                                            barcodeError = "Product not found"
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = p.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = p.barcode,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = EmeraldPrimary
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (barcodeInput.isNotBlank()) {
                            val found = viewModel.scanBarcodeAndAddToCart(barcodeInput)
                            if (found) {
                                showBarcodeDialog = false
                            } else {
                                barcodeError = "No product matching barcode: $barcodeInput"
                            }
                        }
                    },
                    modifier = Modifier.testTag("barcode_confirm_btn")
                ) {
                    Text("Add to Cart")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBarcodeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add New Debtor Quick Dialog
    if (showAddDebtorDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDebtorDialog = false },
            title = { Text("Add Customer Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Customer Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Address / Remarks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.saveCustomer(name, phone, note)
                            showAddDebtorDialog = false
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Save Customer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDebtorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Digital Thermal Receipt Dialog
    if (lastSale != null) {
        val sale = lastSale!!
        DigitalReceiptDialog(
            sale = sale,
            storeName = storeProfile.storeName,
            storePhone = storeProfile.storePhone,
            storeAddress = storeProfile.storeAddress,
            receiptMessage = storeProfile.receiptMessage,
            onDismiss = { viewModel.dismissReceipt() },
            onShare = { receiptText ->
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
fun PosSearchAndCategories(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onScanBarcode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Search & Barcode Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("pos_search_input"),
                placeholder = { Text("Search product name, category...", color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldInteractive,
                    unfocusedBorderColor = BorderElevated,
                    focusedContainerColor = BrandSurfaceElevated,
                    unfocusedContainerColor = BrandSurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onScanBarcode,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandSurfaceElevated)
                    .border(1.dp, BorderElevated, RoundedCornerShape(12.dp))
                    .testTag("pos_barcode_scan_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan Barcode",
                    tint = EmeraldInteractive
                )
            }
        }

        // Categories Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SampleData.categories) { category ->
                val isSelected = category.equals(selectedCategory, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = BrandSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) EmeraldInteractive else BorderSubtle,
                        enabled = true,
                        selected = isSelected
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }
    }
}

@Composable
fun PosProductGrid(
    products: List<Product>,
    cartItems: List<CartItem>,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            TindaEmptyState(
                icon = Icons.Default.PointOfSale,
                title = "No products found",
                subtitle = "Try adjusting your search keywords or switching category filters."
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = modifier
                .fillMaxSize()
                .testTag("pos_product_grid"),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                val itemInCart = cartItems.find { it.product.id == product.id }
                ProductGridCard(
                    product = product,
                    inCartQty = itemInCart?.quantity ?: 0,
                    onAddToCart = { onAddToCart(product) }
                )
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    inCartQty: Int,
    onAddToCart: () -> Unit
) {
    val isOutOfStock = product.stockQuantity <= 0

    TindaCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        onClick = if (!isOutOfStock) onAddToCart else null,
        backgroundColor = if (isOutOfStock) BrandSurfaceElevated.copy(alpha = 0.6f) else BrandSurfaceElevated,
        borderColor = if (inCartQty > 0) EmeraldInteractive.copy(alpha = 0.5f) else BorderElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Category & In-Cart Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.category.uppercase(Locale.US),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (inCartQty > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${inCartQty} in cart",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Product Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isOutOfStock) TextMuted else TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stock Status Badge
            ProductStockBadge(
                stockQuantity = product.stockQuantity,
                minStockAlert = product.lowStockThreshold,
                unit = product.unit
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoneyText(
                    amount = product.sellingPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isOutOfStock) TextMuted else EmeraldInteractive
                )

                IconButton(
                    onClick = onAddToCart,
                    enabled = !isOutOfStock,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (!isOutOfStock) EmeraldPrimary
                            else BrandSurfaceSoft
                        )
                        .testTag("add_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add ${product.name} to cart",
                        tint = if (!isOutOfStock) Color.White else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutSheetContent(
    cartItems: List<CartItem>,
    subtotal: Double,
    discountType: String,
    finalTotal: Double,
    paymentMethod: String,
    cashTendered: String,
    selectedDebtor: CustomerDebt?,
    allCustomers: List<CustomerDebt>,
    storeProfile: com.example.viewmodel.StoreProfile,
    onUpdateQty: (Long, Int) -> Unit,
    onRemove: (Long) -> Unit,
    onClear: () -> Unit,
    onSetDiscount: (String, Double) -> Unit,
    onSetPaymentMethod: (String) -> Unit,
    onSetCashTendered: (String) -> Unit,
    onSelectDebtor: (CustomerDebt?) -> Unit,
    onAddNewDebtor: () -> Unit,
    onConfirmCheckout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = EmeraldInteractive, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Cart Checkout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${cartItems.sumOf { it.quantity }} items in order",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
            if (cartItems.isNotEmpty()) {
                TextButton(onClick = onClear) {
                    Text("Clear All", color = OutOfStockRed, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

        // Cart Items List
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Cart is currently empty", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            cartItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        MoneyText(
                            amount = item.product.sellingPrice,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextMuted
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onUpdateQty(item.product.id, item.quantity - 1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandSurfaceElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                contentDescription = "Decrease",
                                modifier = Modifier.size(16.dp),
                                tint = if (item.quantity == 1) OutOfStockRed else TextSecondary
                            )
                        }

                        Text(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        IconButton(
                            onClick = {
                                if (item.quantity < item.product.stockQuantity) {
                                    onUpdateQty(item.product.id, item.quantity + 1)
                                }
                            },
                            enabled = item.quantity < item.product.stockQuantity,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandSurfaceElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                modifier = Modifier.size(16.dp),
                                tint = if (item.quantity < item.product.stockQuantity) EmeraldInteractive else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        MoneyText(
                            amount = item.subtotal,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.width(68.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

        // Discounts
        Text(
            text = "Discounts",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = discountType == "NONE",
                onClick = { onSetDiscount("NONE", 0.0) },
                label = { Text("None") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = BrandSurfaceElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (discountType == "NONE") EmeraldInteractive else BorderSubtle,
                    enabled = true,
                    selected = discountType == "NONE"
                )
            )
            FilterChip(
                selected = discountType == "SENIOR_PWD",
                onClick = { onSetDiscount("SENIOR_PWD", 0.0) },
                label = { Text("Senior / PWD (20%)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = BrandSurfaceElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (discountType == "SENIOR_PWD") EmeraldInteractive else BorderSubtle,
                    enabled = true,
                    selected = discountType == "SENIOR_PWD"
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = paymentMethod == "CASH",
                onClick = { onSetPaymentMethod("CASH") },
                label = { Text("Cash") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CashGreen,
                    selectedLabelColor = Color.White,
                    containerColor = BrandSurfaceElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (paymentMethod == "CASH") CashGreen else BorderSubtle,
                    enabled = true,
                    selected = paymentMethod == "CASH"
                ),
                modifier = Modifier.testTag("pay_method_cash")
            )
            FilterChip(
                selected = paymentMethod == "GCASH_MAYA",
                onClick = { onSetPaymentMethod("GCASH_MAYA") },
                label = { Text("GCash / Maya") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GCashBlue,
                    selectedLabelColor = Color.White,
                    containerColor = BrandSurfaceElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (paymentMethod == "GCASH_MAYA") GCashBlue else BorderSubtle,
                    enabled = true,
                    selected = paymentMethod == "GCASH_MAYA"
                ),
                modifier = Modifier.testTag("pay_method_gcash")
            )
            FilterChip(
                selected = paymentMethod == "UTANG_LENDING",
                onClick = { onSetPaymentMethod("UTANG_LENDING") },
                label = { Text("Customer Credit") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = UtangAmber,
                    selectedLabelColor = Color.White,
                    containerColor = BrandSurfaceElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (paymentMethod == "UTANG_LENDING") UtangAmber else BorderSubtle,
                    enabled = true,
                    selected = paymentMethod == "UTANG_LENDING"
                ),
                modifier = Modifier.testTag("pay_method_utang")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cash Tendered & Change (if Cash selected)
        if (paymentMethod == "CASH") {
            TindaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrandSurfaceElevated,
                borderColor = BorderElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Cash Received (₱)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = cashTendered,
                        onValueChange = onSetCashTendered,
                        placeholder = { Text("Enter cash amount", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cash_tendered_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldInteractive,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = BrandSurfaceSoft,
                            unfocusedContainerColor = BrandSurfaceSoft
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Fast Cash Shortcuts:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 1: Exact, ₱20, ₱50, ₱100
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSetCashTendered(String.format(Locale.US, "%.2f", finalTotal)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("fast_cash_exact"),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = CashGreen.copy(alpha = 0.15f)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CashGreen))
                        ) {
                            Text("Exact", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CashGreen)
                        }
                        listOf(20.0, 50.0, 100.0).forEach { denom ->
                            OutlinedButton(
                                onClick = { onSetCashTendered(String.format(Locale.US, "%.0f", denom)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fast_cash_${denom.toInt()}"),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                            ) {
                                Text("₱${denom.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 2: ₱200, ₱500, ₱1,000, +₱50 Next Round
                    val nextHundred = (kotlin.math.ceil(finalTotal / 100.0) * 100.0).coerceAtLeast(finalTotal)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(200.0, 500.0, 1000.0).forEach { denom ->
                            OutlinedButton(
                                onClick = { onSetCashTendered(String.format(Locale.US, "%.0f", denom)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fast_cash_${denom.toInt()}"),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                            ) {
                                Text("₱${denom.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                        OutlinedButton(
                            onClick = { onSetCashTendered(String.format(Locale.US, "%.0f", nextHundred)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("fast_cash_round"),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                        ) {
                            Text("₱${nextHundred.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }

                    val tenderedVal = cashTendered.toDoubleOrNull() ?: 0.0
                    val change = (tenderedVal - finalTotal).coerceAtLeast(0.0)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandSurfaceSoft)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Change Due:", fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        AnimatedContent(targetState = change, label = "change_text") { currentChange ->
                            MoneyText(
                                amount = currentChange,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (currentChange > 0) CashGreen else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // GCash / Maya QR Code Display Card (if GCASH_MAYA selected)
        if (paymentMethod == "GCASH_MAYA") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gcash_maya_card"),
                colors = CardDefaults.cardColors(containerColor = GCashBlue.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(GCashBlue, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GCash & Maya QR Payment",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GCashBlue
                                )
                                Text(
                                    text = "Show QR to customer or scan at counter",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // QR Visual Container
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(2.dp, GCashBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "QR Code",
                                tint = GCashBlue,
                                modifier = Modifier.size(90.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SCAN TO PAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GCashBlue,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Account & Merchant Details
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Store Name:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(storeProfile.storeName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("GCash / Maya Mobile:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(storeProfile.storePhone, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GCashBlue)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount to Transfer:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    "₱${String.format(Locale.US, "%.2f", finalTotal)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Utang Customer Picker (if Utang selected)
        if (paymentMethod == "UTANG_LENDING") {
            TindaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrandSurfaceElevated,
                borderColor = UtangAmber.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Customer for Credit:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        TextButton(onClick = onAddNewDebtor) {
                            Text("+ New Customer", color = UtangAmber, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (allCustomers.isEmpty()) {
                        Text(
                            text = "No customers saved. Tap '+ New Customer' above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allCustomers) { customer ->
                                val isSelected = selectedDebtor?.id == customer.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectDebtor(if (isSelected) null else customer) },
                                    label = {
                                        Column {
                                            Text(customer.name, fontWeight = FontWeight.SemiBold)
                                            Text("Bal: ₱${String.format(Locale.US, "%.2f", customer.totalDebt)}", fontSize = 10.sp)
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = UtangAmber,
                                        selectedLabelColor = Color.White,
                                        containerColor = BrandSurfaceSoft,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) UtangAmber else BorderSubtle,
                                        enabled = true,
                                        selected = isSelected
                                    )
                                )
                            }
                        }
                    }

                    if (selectedDebtor != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val newBal = selectedDebtor.totalDebt + finalTotal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(UtangAmber.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current: ₱${String.format(Locale.US, "%.2f", selectedDebtor.totalDebt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "New Bal: ₱${String.format(Locale.US, "%.2f", newBal)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = UtangAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Bill & Checkout Button
        TindaCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = BrandSurfaceElevated,
            borderColor = BorderElevated
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (subtotal != finalTotal) {
                        Text(
                            text = "Subtotal: ₱${String.format(Locale.US, "%.2f", subtotal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "Total Payable",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    MoneyText(
                        amount = finalTotal,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldInteractive
                    )
                }

                val canCheckout = when (paymentMethod) {
                    "CASH" -> {
                        val tenderedVal = cashTendered.toDoubleOrNull()
                        tenderedVal == null || tenderedVal >= finalTotal
                    }
                    "UTANG_LENDING" -> selectedDebtor != null
                    else -> true
                }

                TindaButton(
                    text = "Complete Sale",
                    onClick = onConfirmCheckout,
                    enabled = canCheckout,
                    modifier = Modifier.testTag("confirm_checkout_btn")
                )
            }
        }
    }
}

data class ReceiptItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

fun parseReceiptItems(itemsSummary: String): List<ReceiptItem> {
    if (itemsSummary.isBlank()) return emptyList()
    val regex = Regex("""^(.*?)\s+x(\d+)\s+\(₱?([0-9.,]+)\)$""")
    val rawList = itemsSummary.split(", ")
    return rawList.mapNotNull { itemStr ->
        val trimmed = itemStr.trim()
        if (trimmed.isEmpty()) return@mapNotNull null
        val match = regex.find(trimmed)
        if (match != null) {
            val name = match.groupValues[1].trim()
            val qty = match.groupValues[2].toIntOrNull() ?: 1
            val total = match.groupValues[3].replace(",", "").toDoubleOrNull() ?: 0.0
            val unitPrice = if (qty > 0) total / qty else total
            ReceiptItem(name, qty, unitPrice, total)
        } else {
            ReceiptItem(trimmed, 1, 0.0, 0.0)
        }
    }
}

fun generateReceiptHtml(
    sale: SaleTransaction,
    storeName: String,
    storePhone: String,
    storeAddress: String,
    receiptMessage: String,
    items: List<ReceiptItem>
): String {
    val dateStr = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date(sale.timestamp))
    val itemsRowsHtml = if (items.isNotEmpty()) {
        items.joinToString("") { item ->
            """
            <tr>
                <td colspan="3" class="item-title">${item.name}</td>
            </tr>
            <tr class="item-detail-row">
                <td class="item-qty-price">${item.quantity} &times; ₱${String.format(Locale.US, "%.2f", item.unitPrice)}</td>
                <td class="item-space"></td>
                <td class="item-subtotal">₱${String.format(Locale.US, "%.2f", item.subtotal)}</td>
            </tr>
            """.trimIndent()
        }
    } else {
        """
        <tr>
            <td colspan="3" style="padding: 6px 0; font-size: 13px; font-weight: 600; color: #000;">${sale.itemsSummary}</td>
        </tr>
        """.trimIndent()
    }

    val customerRow = if (!sale.customerName.isNullOrBlank()) {
        """<div class="row"><span class="lbl">Customer:</span><span class="val" style="font-weight: 800;">${sale.customerName}</span></div>"""
    } else ""

    val discountRow = if (sale.discountAmount > 0) {
        """<div class="row discount-row"><span class="lbl">Discount (${sale.discountType}):</span><span class="val">-₱${String.format(Locale.US, "%.2f", sale.discountAmount)}</span></div>"""
    } else ""

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Receipt ${sale.receiptNumber}</title>
        <style>
            @page {
                size: 80mm auto;
                margin: 3mm 4mm;
            }
            * {
                box-sizing: border-box;
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
            }
            body {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                font-size: 13px;
                line-height: 1.4;
                color: #000000 !important;
                background-color: #ffffff;
                margin: 0 auto;
                padding: 6px 4px;
                max-width: 320px;
                -webkit-font-smoothing: antialiased;
                text-rendering: optimizeLegibility;
            }
            .header {
                text-align: center;
                margin-bottom: 8px;
            }
            .store-name {
                font-size: 18px;
                font-weight: 900;
                letter-spacing: 0.5px;
                text-transform: uppercase;
                margin-bottom: 3px;
                color: #000000;
            }
            .sub-text {
                font-size: 12px;
                font-weight: 600;
                color: #111111;
                margin-bottom: 2px;
            }
            .divider-solid {
                border-top: 2px solid #000000;
                margin: 8px 0;
            }
            .divider-dashed {
                border-top: 1.5px dashed #000000;
                margin: 8px 0;
            }
            .row {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin: 3px 0;
                font-size: 12.5px;
                color: #000000;
            }
            .lbl {
                font-weight: 600;
                color: #222222;
            }
            .val {
                font-weight: 700;
                color: #000000;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 6px 0;
            }
            th {
                border-bottom: 2px solid #000000;
                padding: 4px 0;
                font-size: 12px;
                font-weight: 900;
                color: #000000;
                letter-spacing: 0.5px;
            }
            .item-title {
                padding-top: 5px;
                padding-bottom: 2px;
                font-size: 13.5px;
                font-weight: 800;
                color: #000000;
            }
            .item-detail-row td {
                padding-bottom: 5px;
            }
            .item-qty-price {
                font-size: 12.5px;
                font-weight: 600;
                color: #222222;
                padding-left: 8px;
                text-align: left;
            }
            .item-space {
                width: 10px;
            }
            .item-subtotal {
                font-size: 13.5px;
                font-weight: 800;
                color: #000000;
                text-align: right;
            }
            .total-banner {
                margin: 8px 0;
                border-top: 2.5px solid #000000;
                border-bottom: 2.5px solid #000000;
                padding: 7px 0;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .total-label {
                font-size: 15px;
                font-weight: 900;
                color: #000000;
                letter-spacing: 0.5px;
            }
            .total-val {
                font-size: 19px;
                font-weight: 900;
                color: #000000;
            }
            .payment-row {
                font-size: 13px;
                margin: 3px 0;
            }
            .change-row {
                font-size: 14.5px;
                font-weight: 900;
                margin: 4px 0;
                color: #000000;
            }
            .discount-row {
                color: #000000;
                font-weight: 700;
            }
            .barcode-box {
                text-align: center;
                margin: 12px 0 6px 0;
            }
            .barcode-lines {
                letter-spacing: 4px;
                font-family: monospace;
                font-weight: 900;
                font-size: 17px;
                color: #000000;
            }
            .barcode-text {
                font-size: 11px;
                font-weight: 700;
                letter-spacing: 1px;
                color: #000000;
                margin-top: 2px;
            }
            .footer {
                text-align: center;
                margin-top: 10px;
                font-size: 12px;
                font-weight: 600;
                color: #222222;
                font-style: italic;
            }
        </style>
    </head>
    <body>
        <div class="header">
            <div class="store-name">$storeName</div>
            ${if (storeAddress.isNotBlank()) "<div class=\"sub-text\">$storeAddress</div>" else ""}
            ${if (storePhone.isNotBlank()) "<div class=\"sub-text\">Tel: $storePhone</div>" else ""}
        </div>
        <div class="divider-solid"></div>
        <div class="row"><span class="lbl">Receipt #:</span><span class="val" style="font-size: 13.5px; font-weight: 900;">${sale.receiptNumber}</span></div>
        <div class="row"><span class="lbl">Date:</span><span class="val">$dateStr</span></div>
        $customerRow
        <div class="divider-dashed"></div>
        <table>
            <thead>
                <tr>
                    <th style="text-align: left;">ITEM PARTICULARS</th>
                    <th></th>
                    <th style="text-align: right;">TOTAL</th>
                </tr>
            </thead>
            <tbody>
                $itemsRowsHtml
            </tbody>
        </table>
        <div class="divider-dashed"></div>
        <div class="row"><span class="lbl">Subtotal:</span><span class="val">₱${String.format(Locale.US, "%.2f", sale.totalAmount)}</span></div>
        $discountRow
        <div class="total-banner">
            <span class="total-label">TOTAL AMOUNT:</span>
            <span class="total-val">₱${String.format(Locale.US, "%.2f", sale.finalAmount)}</span>
        </div>
        <div class="row payment-row"><span class="lbl">Payment (${sale.paymentMethod}):</span><span class="val">₱${String.format(Locale.US, "%.2f", sale.amountPaid)}</span></div>
        <div class="row change-row"><span class="lbl" style="font-weight: 900; color: #000000;">Change Due:</span><span class="val" style="font-size: 15.5px; font-weight: 900;">₱${String.format(Locale.US, "%.2f", sale.changeAmount)}</span></div>
        <div class="divider-dashed"></div>
        <div class="barcode-box">
            <div class="barcode-lines">||||| | |||| ||||| || ||||||</div>
            <div class="barcode-text">${sale.receiptNumber}</div>
        </div>
        <div class="footer">
            <div>$receiptMessage</div>
            <div style="margin-top: 4px; font-size: 10.5px; font-weight: 700; color: #444;">Tinda POS - Official Sale Slip</div>
        </div>
    </body>
    </html>
    """.trimIndent()
}

fun triggerPrintReceipt(
    context: Context,
    sale: SaleTransaction,
    storeName: String,
    storePhone: String,
    storeAddress: String,
    receiptMessage: String,
    items: List<ReceiptItem>
) {
    try {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Print service is unavailable on this device", Toast.LENGTH_SHORT).show()
            return
        }
        val webView = WebView(context)
        val htmlContent = generateReceiptHtml(sale, storeName, storePhone, storeAddress, receiptMessage, items)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Receipt_${sale.receiptNumber}")
                val printAttributes = PrintAttributes.Builder()
                    .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A6)
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("Tinda_Receipt_${sale.receiptNumber}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    } catch (e: Exception) {
        Toast.makeText(context, "Print error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun buildThermalPlainTextReceipt(
    sale: SaleTransaction,
    storeName: String,
    storePhone: String,
    storeAddress: String,
    receiptMessage: String,
    items: List<ReceiptItem>,
    dateStr: String
): String = buildString {
    appendLine("================================")
    appendLine("       $storeName       ")
    if (storeAddress.isNotBlank()) appendLine("   $storeAddress   ")
    if (storePhone.isNotBlank()) appendLine("   Tel: $storePhone   ")
    appendLine("================================")
    appendLine("Receipt #: ${sale.receiptNumber}")
    appendLine("Date: $dateStr")
    if (!sale.customerName.isNullOrBlank()) {
        appendLine("Customer: ${sale.customerName}")
    }
    appendLine("--------------------------------")
    appendLine(String.format(Locale.US, "%-14s %3s %5s %6s", "ITEM", "QTY", "PRICE", "TOTAL"))
    appendLine("--------------------------------")
    if (items.isNotEmpty()) {
        for (item in items) {
            val truncatedName = if (item.name.length > 30) item.name.take(28) + ".." else item.name
            appendLine(truncatedName)
            appendLine(String.format(Locale.US, "%14s %3d %5.2f %6.2f", "", item.quantity, item.unitPrice, item.subtotal))
        }
    } else {
        appendLine(sale.itemsSummary)
    }
    appendLine("--------------------------------")
    appendLine(String.format(Locale.US, "%-20s ₱%8.2f", "Subtotal:", sale.totalAmount))
    if (sale.discountAmount > 0) {
        appendLine(String.format(Locale.US, "%-20s-₱%8.2f", "Discount (${sale.discountType}):", sale.discountAmount))
    }
    appendLine("--------------------------------")
    appendLine(String.format(Locale.US, "%-20s ₱%8.2f", "TOTAL AMOUNT:", sale.finalAmount))
    appendLine("--------------------------------")
    appendLine(String.format(Locale.US, "%-20s ₱%8.2f", "Payment (${sale.paymentMethod}):", sale.amountPaid))
    appendLine(String.format(Locale.US, "%-20s ₱%8.2f", "Change Due:", sale.changeAmount))
    appendLine("================================")
    appendLine("    ||||| | |||| ||||| || ||||||    ")
    appendLine("          ${sale.receiptNumber}          ")
    appendLine("================================")
    appendLine("     $receiptMessage     ")
    appendLine("================================")
}

@Composable
fun DigitalReceiptDialog(
    sale: SaleTransaction,
    storeName: String,
    storePhone: String,
    storeAddress: String,
    receiptMessage: String,
    onDismiss: () -> Unit,
    onShare: (String) -> Unit
) {
    val context = LocalContext.current
    val dateStr = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date(sale.timestamp))
    val items = remember(sale.itemsSummary) { parseReceiptItems(sale.itemsSummary) }
    val receiptPlainText = remember(sale, storeName, storePhone, storeAddress, receiptMessage, items) {
        buildThermalPlainTextReceipt(sale, storeName, storePhone, storeAddress, receiptMessage, items, dateStr)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sale Receipt",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = sale.receiptNumber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Quick Action Bar: Print, Share, Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            triggerPrintReceipt(
                                context = context,
                                sale = sale,
                                storeName = storeName,
                                storePhone = storePhone,
                                storeAddress = storeAddress,
                                receiptMessage = receiptMessage,
                                items = items
                            )
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("receipt_print_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onShare(receiptPlainText) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("receipt_share_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderElevated),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = BrandSurfaceElevated,
                            contentColor = TextPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldInteractive)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("Receipt ${sale.receiptNumber}", receiptPlainText)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "Receipt copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("receipt_copy_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderElevated),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = BrandSurfaceElevated,
                            contentColor = TextPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldInteractive)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                // Printable Thermal Receipt Canvas (High-Definition, Crisp Contrast)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Perforated Top Guide
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - - - - - - -",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Store Header
                        Text(
                            text = storeName,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        if (storeAddress.isNotBlank()) {
                            Text(
                                text = storeAddress,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )
                        }
                        if (storePhone.isNotBlank()) {
                            Text(
                                text = "Tel: $storePhone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFF0F172A), thickness = 2.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Transaction Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Receipt #:", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                            Text(sale.receiptNumber, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Date & Time:", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                            Text(dateStr, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        if (!sale.customerName.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Customer:", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                                Text(sale.customerName, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF0F172A), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Itemized Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ITEM PARTICULARS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF0F172A))
                            Text("TOTAL", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF0F172A))
                        }

                        HorizontalDivider(color = Color(0xFF0F172A), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Itemized List
                        if (items.isNotEmpty()) {
                            items.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "   ${item.quantity} × ₱${String.format(Locale.US, "%.2f", item.unitPrice)}",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF334155)
                                        )
                                        Text(
                                            text = "₱${String.format(Locale.US, "%.2f", item.subtotal)}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = sale.itemsSummary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFF0F172A), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Subtotal & Discounts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                            Text("₱${String.format(Locale.US, "%.2f", sale.totalAmount)}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        if (sale.discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount (${sale.discountType}):", fontSize = 13.sp, color = LowStockOrange, fontWeight = FontWeight.Bold)
                                Text("-₱${String.format(Locale.US, "%.2f", sale.discountAmount)}", fontSize = 13.5.sp, color = LowStockOrange, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFF0F172A), thickness = 2.dp)

                        // Grand Total Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL AMOUNT:", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", sale.finalAmount)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                color = EmeraldPrimary
                            )
                        }

                        HorizontalDivider(color = Color(0xFF0F172A), thickness = 2.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Payment Tender & Change
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment (${sale.paymentMethod}):", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                            Text("₱${String.format(Locale.US, "%.2f", sale.amountPaid)}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Change Due:", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", sale.changeAmount)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color(0xFF059669)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simulated Barcode
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(30.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "||||| | |||| ||||| || |||||| | |||||",
                                    fontSize = 16.sp,
                                    letterSpacing = 2.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = sale.receiptNumber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Footer Note
                        Text(
                            text = receiptMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Perforated Bottom Guide
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - - - - - - - -",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("receipt_done_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Done / New Sale", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    )
}
