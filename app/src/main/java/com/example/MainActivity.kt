package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LendingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.PosScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.StoreSettingsDialog
import com.example.ui.screens.StoreSetupWizardDialog
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfacePrimary
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.LowStockOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.TindaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TindaAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TindaAppRoot(viewModel: TindaViewModel = viewModel()) {
    // Destinations: 0 = Home, 1 = POS, 2 = Inventory, 3 = Reports, 4 = More, 5 = Dedicated Customer Credit
    var currentTab by remember { mutableIntStateOf(0) }
    var previousTab by remember { mutableIntStateOf(0) }
    var showStoreSettingsDialog by remember { mutableStateOf(false) }

    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()
    val showSetupWizard by viewModel.showSetupWizard.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.quantity }

    if (currentUser == null) {
        LoginScreen(viewModel = viewModel)
        return
    }

    // Intercept system back gestures to return to Home or previous screen smoothly
    BackHandler(enabled = currentTab != 0 || showStoreSettingsDialog) {
        if (showStoreSettingsDialog) {
            showStoreSettingsDialog = false
        } else if (currentTab == 5) {
            currentTab = previousTab
        } else {
            currentTab = 0
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Tablet Navigation Rail
            if (isTablet) {
                NavigationRail(
                    containerColor = BrandSurfacePrimary,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "TP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TINDA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp,
                                color = EmeraldInteractive
                            )
                        }
                    },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TindaNavItems.forEach { item ->
                        val isSelected = currentTab == item.index || (item.index == 4 && currentTab == 5)
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = {
                                if (currentTab == 5) previousTab = 4
                                currentTab = item.index
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.index == 1 && cartCount > 0) {
                                            Badge(containerColor = EmeraldPrimary, contentColor = Color.White) {
                                                Text("$cartCount")
                                            }
                                        } else if (item.index == 2 && lowStockProducts.isNotEmpty()) {
                                            Badge(containerColor = LowStockOrange, contentColor = Color.White) {
                                                Text("${lowStockProducts.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                }
                            },
                            label = { Text(item.label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = EmeraldInteractive,
                                selectedTextColor = EmeraldInteractive,
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.18f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { showStoreSettingsDialog = true },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Store, contentDescription = "Settings", tint = TextSecondary)
                    }
                }
            }

            // Main Content Area with TopAppBar and BottomBar (on phones)
            Scaffold(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                containerColor = BrandBackground,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentTab == 5) {
                                    IconButton(onClick = { currentTab = previousTab }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = TextPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Customer Credit (Utang)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "TP",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "TINDA POS",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = storeProfile.storeName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            // Current User & Quick Lock
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .clickable { viewModel.lockScreen() }
                                    .testTag("topbar_user_lock_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = currentUser?.displayName?.take(1)?.uppercase() ?: "U",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentUser?.displayName ?: "User",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldInteractive,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock Terminal",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))

                            if (!storeProfile.isSetupCompleted) {
                                AssistChip(
                                    onClick = { viewModel.openSetupWizard() },
                                    label = { Text("Setup Store", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.AutoFixHigh,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = EmeraldInteractive
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                        labelColor = EmeraldInteractive
                                    ),
                                    border = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            IconButton(
                                onClick = { showStoreSettingsDialog = true },
                                modifier = Modifier.testTag("open_store_settings_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = "Store Settings",
                                    tint = TextSecondary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = BrandSurfacePrimary
                        )
                    )
                },
                bottomBar = {
                    if (!isTablet) {
                        NavigationBar(
                            containerColor = BrandSurfacePrimary,
                            tonalElevation = 6.dp
                        ) {
                            TindaNavItems.forEach { item ->
                                val isSelected = currentTab == item.index || (item.index == 4 && currentTab == 5)
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (currentTab == 5) previousTab = 4
                                        currentTab = item.index
                                    },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (item.index == 1 && cartCount > 0) {
                                                    Badge(containerColor = EmeraldPrimary, contentColor = Color.White) {
                                                        Text("$cartCount")
                                                    }
                                                } else if (item.index == 2 && lowStockProducts.isNotEmpty()) {
                                                    Badge(containerColor = LowStockOrange, contentColor = Color.White) {
                                                        Text("${lowStockProducts.size}")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldInteractive,
                                        selectedTextColor = EmeraldInteractive,
                                        indicatorColor = EmeraldPrimary.copy(alpha = 0.16f),
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("tab_${item.tag}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(BrandBackground)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(180)) + slideInVertically(
                                animationSpec = tween(180),
                                initialOffsetY = { 8 }
                            )).togetherWith(
                                fadeOut(animationSpec = tween(140))
                            )
                        },
                        label = "tabTransition"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToPos = { currentTab = 1 },
                                onNavigateToInventory = { currentTab = 2 },
                                onNavigateToReports = { currentTab = 3 },
                                onNavigateToCredit = {
                                    previousTab = 0
                                    currentTab = 5
                                }
                            )
                            1 -> PosScreen(viewModel = viewModel)
                            2 -> InventoryScreen(viewModel = viewModel)
                            3 -> ReportsScreen(viewModel = viewModel)
                            4 -> MoreScreen(
                                viewModel = viewModel,
                                onNavigateToCredit = {
                                    previousTab = 4
                                    currentTab = 5
                                },
                                onNavigateToReports = { currentTab = 3 },
                                onOpenStoreSettings = { showStoreSettingsDialog = true },
                                onOpenSetupWizard = { viewModel.openSetupWizard() }
                            )
                            5 -> LendingScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    if (showSetupWizard) {
        StoreSetupWizardDialog(
            initialProfile = storeProfile,
            onDismiss = { viewModel.dismissSetupWizard() },
            onCompleteSetup = { name, owner, phone, address, msg, cash, loadStarter ->
                viewModel.completeStoreSetup(name, owner, phone, address, msg, cash, loadStarter)
            }
        )
    }

    if (showStoreSettingsDialog) {
        StoreSettingsDialog(
            initialProfile = storeProfile,
            viewModel = viewModel,
            onDismiss = { showStoreSettingsDialog = false },
            onLaunchWizard = {
                showStoreSettingsDialog = false
                viewModel.openSetupWizard()
            },
            onResetToStarterPack = {
                viewModel.resetToStarterPack()
            },
            onClearAllData = {
                viewModel.clearAllDataForFreshStart()
            },
            onSave = { name, owner, phone, address, msg, cash ->
                viewModel.completeStoreSetup(name, owner, phone, address, msg, cash, loadStarterPack = false)
                showStoreSettingsDialog = false
            }
        )
    }
}

private data class TindaNavItem(
    val index: Int,
    val tag: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val TindaNavItems = listOf(
    TindaNavItem(0, "home", "Home", Icons.Default.Home, Icons.Outlined.Home),
    TindaNavItem(1, "pos", "POS", Icons.Default.PointOfSale, Icons.Outlined.PointOfSale),
    TindaNavItem(2, "inventory", "Inventory", Icons.Default.Inventory2, Icons.Outlined.Inventory2),
    TindaNavItem(3, "reports", "Reports", Icons.Default.BarChart, Icons.Outlined.BarChart),
    TindaNavItem(4, "more", "More", Icons.Default.Menu, Icons.Outlined.Menu)
)

