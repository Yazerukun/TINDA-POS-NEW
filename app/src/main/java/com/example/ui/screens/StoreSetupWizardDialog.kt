package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.InStockGreen
import com.example.viewmodel.StoreProfile
import java.util.Locale

@Composable
fun StoreSetupWizardDialog(
    initialProfile: StoreProfile,
    onDismiss: () -> Unit,
    onCompleteSetup: (
        storeName: String,
        ownerName: String,
        phone: String,
        address: String,
        message: String,
        startingCash: Double,
        loadStarterPack: Boolean
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

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
    var loadStarterPack by remember { mutableStateOf(true) }

    val totalSteps = 4
    val progress = step.toFloat() / totalSteps.toFloat()

    Dialog(
        onDismissRequest = {
            if (initialProfile.isSetupCompleted) {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("store_setup_wizard_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with Store Icon, Title, and Close (if already setup)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Store,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Store Setup Wizard",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "Step $step of $totalSteps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    if (initialProfile.isSetupCompleted) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Step Progress Indicator
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldPrimary,
                    trackColor = EmeraldPrimary.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Step Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "WizardStepTransition"
                    ) { currentStep ->
                        when (currentStep) {
                            1 -> Step1StoreDetails(
                                storeName = storeName,
                                onStoreNameChange = { storeName = it },
                                ownerName = ownerName,
                                onOwnerNameChange = { ownerName = it },
                                storePhone = storePhone,
                                onStorePhoneChange = { storePhone = it },
                                storeAddress = storeAddress,
                                onStoreAddressChange = { storeAddress = it }
                            )
                            2 -> Step2CashDrawerAndReceipt(
                                startingCash = startingCashStr,
                                onStartingCashChange = { startingCashStr = it },
                                receiptMessage = receiptMessage,
                                onReceiptMessageChange = { receiptMessage = it }
                            )
                            3 -> Step3CatalogChoice(
                                loadStarterPack = loadStarterPack,
                                onChoiceChange = { loadStarterPack = it }
                            )
                            4 -> Step4SummaryAndReady(
                                storeName = storeName,
                                ownerName = ownerName,
                                storePhone = storePhone,
                                storeAddress = storeAddress,
                                startingCash = startingCashStr.toDoubleOrNull() ?: 500.0,
                                receiptMessage = receiptMessage,
                                loadStarterPack = loadStarterPack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (step < totalSteps) {
                        Button(
                            onClick = { step++ },
                            enabled = storeName.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.testTag("wizard_next_btn")
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                val cash = startingCashStr.toDoubleOrNull() ?: 500.0
                                onCompleteSetup(
                                    storeName,
                                    ownerName,
                                    storePhone,
                                    storeAddress,
                                    receiptMessage,
                                    cash,
                                    loadStarterPack
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InStockGreen),
                            modifier = Modifier.testTag("wizard_finish_btn")
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Launch & Start Selling", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1StoreDetails(
    storeName: String,
    onStoreNameChange: (String) -> Unit,
    ownerName: String,
    onOwnerNameChange: (String) -> Unit,
    storePhone: String,
    onStorePhoneChange: (String) -> Unit,
    storeAddress: String,
    onStoreAddressChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = EmeraldPrimary.copy(alpha = 0.08f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "STEP 1: STORE PROFILE & DETAILS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your store information. This will appear on digital receipts, reports, and the main dashboard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = storeName,
            onValueChange = onStoreNameChange,
            label = { Text("Store Name *") },
            placeholder = { Text("e.g. Sunrise Retail & Convenience Store") },
            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wizard_store_name_input")
        )

        OutlinedTextField(
            value = ownerName,
            onValueChange = onOwnerNameChange,
            label = { Text("Owner / Cashier Name") },
            placeholder = { Text("e.g. Maria Santos") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = storePhone,
            onValueChange = onStorePhoneChange,
            label = { Text("Contact Phone / Mobile Payment") },
            placeholder = { Text("e.g. 0917-123-4567") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = storeAddress,
            onValueChange = onStoreAddressChange,
            label = { Text("Store Address / Location") },
            placeholder = { Text("e.g. 124 Main Street, Downtown") },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Step2CashDrawerAndReceipt(
    startingCash: String,
    onStartingCashChange: (String) -> Unit,
    receiptMessage: String,
    onReceiptMessageChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = CashGreen.copy(alpha = 0.08f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "STEP 2: CASH DRAWER FLOAT & RECEIPT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CashGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set your opening cash float to accurately reconcile cash drawer balances at the end of the day.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Starting Cash Drawer Float (₱)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("300", "500", "1000", "2000").forEach { amount ->
                val isSelected = startingCash == amount
                FilterChip(
                    selected = isSelected,
                    onClick = { onStartingCashChange(amount) },
                    label = { Text("₱$amount") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CashGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        OutlinedTextField(
            value = startingCash,
            onValueChange = onStartingCashChange,
            label = { Text("Custom Float Amount (₱)") },
            leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Receipt Footer Message",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = receiptMessage,
            onValueChange = onReceiptMessageChange,
            label = { Text("Greeting / Closing Message") },
            placeholder = { Text("e.g. Thank you for your purchase! Please come again.") },
            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        // Quick suggestions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextButton(
                onClick = { onReceiptMessageChange("Thank you for your purchase! Please come again.") },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("“Thank you! Come again”", fontSize = 11.sp)
            }
            TextButton(
                onClick = { onReceiptMessageChange("We appreciate your business! Have a blessed day.") },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("“Have a blessed day”", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun Step3CatalogChoice(
    loadStarterPack: Boolean,
    onChoiceChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = GCashBlue.copy(alpha = 0.08f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "STEP 3: INVENTORY CATALOG",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GCashBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose how you would like to initialize your product inventory:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Choice 1: Starter Pack (Recommended)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (loadStarterPack) 2.dp else 1.dp,
                    color = if (loadStarterPack) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onChoiceChange(true) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (loadStarterPack) EmeraldPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (loadStarterPack) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        tint = if (loadStarterPack) Color.White else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Load Retail Starter Catalog",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (loadStarterPack) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recommended! Preloads 20+ top-selling retail goods (Lucky Me, Kopiko, Silver Swan, Nescafe, Bear Brand, rice, canned goods, beverages, snacks) with realistic cost prices and retail SRP.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Choice 2: Blank slate
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (!loadStarterPack) 2.dp else 1.dp,
                    color = if (!loadStarterPack) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onChoiceChange(false) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (!loadStarterPack) EmeraldPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (!loadStarterPack) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (!loadStarterPack) Color.White else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start with Blank Catalog",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!loadStarterPack) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start fresh from zero. Add your products individually using the barcode camera scanner or manual product entry under the Inventory tab.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun Step4SummaryAndReady(
    storeName: String,
    ownerName: String,
    storePhone: String,
    storeAddress: String,
    startingCash: Double,
    receiptMessage: String,
    loadStarterPack: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = InStockGreen.copy(alpha = 0.10f)
            )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = InStockGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "STORE READY TO OPERATE!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = InStockGreen
                    )
                    Text(
                        text = "Profile, cash float, and catalog preferences configured successfully.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Store Summary Card
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configuration Summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Store Name:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(storeName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Owner / Cashier:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(ownerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Contact Phone:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(storePhone.ifBlank { "N/A" }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Opening Cash Float:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text("₱${String.format(Locale.US, "%.2f", startingCash)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = CashGreen)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Product Catalog:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(if (loadStarterPack) "Starter Pack (20+ items)" else "Empty Catalog", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = EmeraldPrimary)
                }
            }
        }

        // Quick feature guide
        Text(
            text = "Quick Start Operational Guide",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        QuickGuideItem(
            icon = "🛒",
            title = "POS & Fast Checkout",
            desc = "Tap products or scan barcodes with the camera. The cart automatically calculates totals, changes, and updates stock."
        )

        QuickGuideItem(
            icon = "📖",
            title = "Customer Credit & Ledger",
            desc = "Select 'Charge to Credit' at checkout to log customer balances and send SMS statements directly."
        )

        QuickGuideItem(
            icon = "⚠️",
            title = "Low Stock & Expiry Tracking",
            desc = "Real-time alerts for low stock and items expiring within 30 days to protect your inventory value."
        )

        QuickGuideItem(
            icon = "📊",
            title = "Real-Time Sales & Profit Reports",
            desc = "Gross sales, net profit margins, hourly peak times, and cash drawer reconciliations computed directly from Room DB."
        )
    }
}

@Composable
private fun QuickGuideItem(icon: String, title: String, desc: String) {
    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
