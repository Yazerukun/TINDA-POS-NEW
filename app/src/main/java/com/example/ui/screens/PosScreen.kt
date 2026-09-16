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
                        .size(44.dp)
                        .clip(CircleShape)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandSurfaceElevated)
                                .border(1.dp, BorderSubtle, CircleShape)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandSurfaceElevated)
                                .border(1.dp, BorderSubtle, CircleShape)
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
                <td style="padding: 3px 0; font-weight: bold; text-align: left;">${item.name}</td>
                <td style="padding: 3px 0; text-align: center;">${item.quantity}</td>
                <td style="padding: 3px 0; text-align: right;">₱${String.format(Locale.US, "%.2f", item.unitPrice)}</td>
                <td style="padding: 3px 0; text-align: right; font-weight: bold;">₱${String.format(Locale.US, "%.2f", item.subtotal)}</td>
            </tr>
            """.trimIndent()
        }
    } else {
        """
        <tr>
            <td colspan="4" style="padding: 4px 0;">${sale.itemsSummary}</td>
        </tr>
        """.trimIndent()
    }

    val customerRow = if (!sale.customerName.isNullOrBlank()) {
        """<div class="row"><span>Customer:</span><span style="font-weight: bold;">${sale.customerName}</span></div>"""
    } else ""

    val discountRow = if (sale.discountAmount > 0) {
        """<div class="row"><span>Discount (${sale.discountType}):</span><span>-₱${String.format(Locale.US, "%.2f", sale.discountAmount)}</span></div>"""
    } else ""

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <title>Receipt ${sale.receiptNumber}</title>
        <style>
            @page {
                size: 80mm auto;
                margin: 4mm;
            }
            body {
                font-family: 'Courier New', Courier, monospace;
                font-size: 11px;
                color: #000;
                margin: 0;
                padding: 4px;
                line-height: 1.3;
            }
            .header {
                text-align: center;
                margin-bottom: 6px;
            }
            .store-name {
                font-size: 16px;
                font-weight: bold;
                text-transform: uppercase;
                margin-bottom: 2px;
            }
            .sub-text {
                font-size: 10px;
                color: #333;
            }
            .divider {
                border-top: 1px dashed #000;
                margin: 6px 0;
            }
            .double-divider {
                border-top: 2px solid #000;
                margin: 6px 0;
            }
            .row {
                display: flex;
                justify-content: space-between;
                margin: 2px 0;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 4px 0;
            }
            th {
                border-bottom: 1px dashed #000;
                padding: 4px 0;
                font-size: 10px;
            }
            .total-row {
                display: flex;
                justify-content: space-between;
                font-size: 14px;
                font-weight: bold;
                margin: 4px 0;
                border-top: 1px dashed #000;
                border-bottom: 1px dashed #000;
                padding: 4px 0;
            }
            .footer {
                text-align: center;
                margin-top: 10px;
                font-size: 10px;
                font-style: italic;
            }
            .barcode-box {
                text-align: center;
                margin: 8px 0;
            }
            .barcode-lines {
                letter-spacing: 3px;
                font-family: monospace;
                font-weight: bold;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <div class="header">
            <div class="store-name">$storeName</div>
            ${if (storeAddress.isNotBlank()) "<div class=\"sub-text\">$storeAddress</div>" else ""}
            ${if (storePhone.isNotBlank()) "<div class=\"sub-text\">Tel: $storePhone</div>" else ""}
        </div>
        <div class="double-divider"></div>
        <div class="row"><span>Receipt #:</span><span style="font-weight: bold;">${sale.receiptNumber}</span></div>
        <div class="row"><span>Date:</span><span>$dateStr</span></div>
        $customerRow
        <div class="divider"></div>
        <table>
            <thead>
                <tr>
                    <th style="text-align: left;">ITEM</th>
                    <th style="text-align: center;">QTY</th>
                    <th style="text-align: right;">PRICE</th>
                    <th style="text-align: right;">TOTAL</th>
                </tr>
            </thead>
            <tbody>
                $itemsRowsHtml
            </tbody>
        </table>
        <div class="divider"></div>
        <div class="row"><span>Subtotal:</span><span>₱${String.format(Locale.US, "%.2f", sale.totalAmount)}</span></div>
        $discountRow
        <div class="total-row">
            <span>TOTAL AMOUNT:</span>
            <span>₱${String.format(Locale.US, "%.2f", sale.finalAmount)}</span>
        </div>
        <div class="row"><span>Payment (${sale.paymentMethod}):</span><span>₱${String.format(Locale.US, "%.2f", sale.amountPaid)}</span></div>
        <div class="row"><span style="font-weight: bold;">Change:</span><span style="font-weight: bold;">₱${String.format(Locale.US, "%.2f", sale.changeAmount)}</span></div>
        <div class="divider"></div>
        <div class="barcode-box">
            <div class="barcode-lines">||||| | |||| ||||| || ||||||</div>
            <div style="font-size: 10px;">${sale.receiptNumber}</div>
        </div>
        <div class="footer">
            <div>$receiptMessage</div>
            <div style="margin-top: 4px; font-size: 9px; color: #555;">Powered by Tinda POS</div>
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
                            .testTag("receipt_print_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onShare(receiptPlainText) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("receipt_share_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
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
                            .testTag("receipt_copy_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp)
                    }
                }

                // Printable Thermal Receipt Canvas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Perforated Top Guide
                        Text(
                            text = "• • • • • • • • • • • • • • • • • • • • • • •",
                            color = Color(0xFFBDBDBD),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Store Header
                        Text(
                            text = storeName,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        if (storeAddress.isNotBlank()) {
                            Text(
                                text = storeAddress,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (storePhone.isNotBlank()) {
                            Text(
                                text = "Tel: $storePhone",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "================================",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Transaction Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Receipt #:", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                            Text(sale.receiptNumber, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Date & Time:", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                            Text(dateStr, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        }
                        if (!sale.customerName.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Customer:", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                                Text(sale.customerName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "--------------------------------",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        // Itemized Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ITEM", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("QTY", modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("PRICE", modifier = Modifier.weight(1.1f), textAlign = TextAlign.Right, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("TOTAL", modifier = Modifier.weight(1.2f), textAlign = TextAlign.Right, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        Text(
                            text = "--------------------------------",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        // Itemized List
                        if (items.isNotEmpty()) {
                            items.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "    ${item.quantity} x ₱${String.format(Locale.US, "%.2f", item.unitPrice)}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF424242),
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "₱${String.format(Locale.US, "%.2f", item.subtotal)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = sale.itemsSummary,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "--------------------------------",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        // Subtotal & Discounts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text("₱${String.format(Locale.US, "%.2f", sale.totalAmount)}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        if (sale.discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount (${sale.discountType}):", fontSize = 12.sp, color = LowStockOrange, fontFamily = FontFamily.Monospace)
                                Text("-₱${String.format(Locale.US, "%.2f", sale.discountAmount)}", fontSize = 12.sp, color = LowStockOrange, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "================================",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        // Grand Total
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL AMOUNT:", fontWeight = FontWeight.Black, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", sale.finalAmount)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = EmeraldPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "================================",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        // Payment Tender & Change
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment (${sale.paymentMethod}):", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text("₱${String.format(Locale.US, "%.2f", sale.amountPaid)}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Change Due:", fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "₱${String.format(Locale.US, "%.2f", sale.changeAmount)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = EmeraldPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Simulated Barcode
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(28.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "||||| | |||| ||||| || |||||| | |||||",
                                    fontSize = 14.sp,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = sale.receiptNumber,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.DarkGray
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Footer Note
                        Text(
                            text = receiptMessage,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Perforated Bottom Guide
                        Text(
                            text = "• • • • • • • • • • • • • • • • • • • • • • •",
                            color = Color(0xFFBDBDBD),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
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
                    .testTag("receipt_done_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Done / New Sale", fontWeight = FontWeight.Bold)
            }
        }
    )
}
