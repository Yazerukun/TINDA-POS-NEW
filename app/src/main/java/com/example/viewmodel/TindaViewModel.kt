package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CartItem
import com.example.data.CustomerDebt
import com.example.data.DebtRecord
import com.example.data.Product
import com.example.data.SaleTransaction
import com.example.data.StockMovement
import com.example.data.TindaRepository
import com.example.data.UserAccount
import com.example.data.ZReadReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class StoreProfile(
    val storeName: String = "Tinda Retail Store",
    val ownerName: String = "Store Manager",
    val storePhone: String = "0917-123-4567",
    val storeAddress: String = "124 Main Street",
    val receiptMessage: String = "Thank you for your purchase! Please come again.",
    val startingCashDrawer: Double = 500.0,
    val isSetupCompleted: Boolean = false
)

enum class ReportPeriod {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    ALL_TIME
}

data class TopSellingItem(
    val productName: String,
    val quantitySold: Int,
    val totalRevenue: Double,
    val percentageOfSales: Double
)

data class HourlySalesBucket(
    val timeSlot: String,
    val salesAmount: Double,
    val transactionCount: Int
)

data class RealtimeReportsState(
    val period: ReportPeriod = ReportPeriod.TODAY,
    val filteredSales: List<SaleTransaction> = emptyList(),
    val grossSales: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMarginPercent: Double = 0.0,
    val transactionsCount: Int = 0,
    val averageBasket: Double = 0.0,
    val cashSales: Double = 0.0,
    val gcashSales: Double = 0.0,
    val utangSales: Double = 0.0,
    val cashOnHandToday: Double = 0.0,
    val utangCollectedToday: Double = 0.0,
    val totalOutstandingDebt: Double = 0.0,
    val topSellingItems: List<TopSellingItem> = emptyList(),
    val hourlyBreakdown: List<HourlySalesBucket> = emptyList(),
    val inventoryRetailValue: Double = 0.0,
    val inventoryCostValue: Double = 0.0,
    val projectedInventoryProfit: Double = 0.0,
    val lastTransactionTimestamp: Long? = null,
    val totalSalesRowsInDb: Int = 0
)

data class XReadSnapshot(
    val generatedAt: Long = System.currentTimeMillis(),
    val periodStart: Long = 0L,
    val periodEnd: Long = System.currentTimeMillis(),
    val openingFloat: Double = 0.0,
    val grossSales: Double = 0.0,
    val netSales: Double = 0.0,
    val totalProfit: Double = 0.0,
    val transactionCount: Int = 0,
    val cashSales: Double = 0.0,
    val gcashSales: Double = 0.0,
    val creditSales: Double = 0.0,
    val customerDebtPaymentsCollected: Double = 0.0,
    val totalDiscounts: Double = 0.0,
    val voidedCount: Int = 0,
    val voidedTotal: Double = 0.0,
    val expectedCashInDrawer: Double = 0.0,
    val firstReceiptNumber: String = "N/A",
    val lastReceiptNumber: String = "N/A"
)

class TindaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val repository = TindaRepository(database)

    private val prefs = application.getSharedPreferences("tinda_store_prefs", Context.MODE_PRIVATE)

    // Store Profile
    private val _storeProfile = MutableStateFlow(
        StoreProfile(
            storeName = prefs.getString("store_name", "Tinda Retail Store") ?: "Tinda Retail Store",
            ownerName = prefs.getString("owner_name", "Store Manager") ?: "Store Manager",
            storePhone = prefs.getString("store_phone", "0917-123-4567") ?: "0917-123-4567",
            storeAddress = prefs.getString("store_address", "124 Main Street") ?: "124 Main Street",
            receiptMessage = prefs.getString("receipt_message", "Thank you for your purchase! Please come again.") ?: "Thank you for your purchase! Please come again.",
            startingCashDrawer = prefs.getFloat("starting_cash", 500f).toDouble(),
            isSetupCompleted = prefs.getBoolean("is_setup_completed", false)
        )
    )
    val storeProfile: StateFlow<StoreProfile> = _storeProfile.asStateFlow()

    private val _showSetupWizard = MutableStateFlow(!_storeProfile.value.isSetupCompleted)
    val showSetupWizard: StateFlow<Boolean> = _showSetupWizard.asStateFlow()

    fun openSetupWizard() {
        _showSetupWizard.value = true
    }

    fun dismissSetupWizard() {
        _showSetupWizard.value = false
    }

    fun completeStoreSetup(
        storeName: String,
        ownerName: String,
        phone: String,
        address: String,
        message: String,
        startingCash: Double,
        loadStarterPack: Boolean = true
    ) {
        val updated = StoreProfile(
            storeName = storeName.ifBlank { "Tinda Retail Store" },
            ownerName = ownerName.ifBlank { "Store Manager" },
            storePhone = phone,
            storeAddress = address,
            receiptMessage = message.ifBlank { "Thank you for your purchase! Please come again." },
            startingCashDrawer = startingCash,
            isSetupCompleted = true
        )
        _storeProfile.value = updated
        prefs.edit()
            .putString("store_name", updated.storeName)
            .putString("owner_name", updated.ownerName)
            .putString("store_phone", updated.storePhone)
            .putString("store_address", updated.storeAddress)
            .putString("receipt_message", updated.receiptMessage)
            .putFloat("starting_cash", updated.startingCashDrawer.toFloat())
            .putBoolean("is_setup_completed", true)
            .apply()

        _showSetupWizard.value = false

        viewModelScope.launch {
            if (loadStarterPack) {
                repository.initDefaultDataIfNeeded()
            }
        }
    }

    fun resetToStarterPack() {
        viewModelScope.launch {
            repository.resetDatabaseToStarterPack()
        }
    }

    fun clearAllDataForFreshStart() {
        viewModelScope.launch {
            repository.clearAllDataForFreshStart()
        }
    }

    // User Management & Authentication
    val allActiveUsers: StateFlow<List<UserAccount>> = repository.allActiveUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserAccount>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun clearLoginError() {
        _loginError.value = null
    }

    fun loginWithPin(user: UserAccount, enteredPin: String): Boolean {
        if (user.pin == enteredPin.trim()) {
            _currentUser.value = user
            _loginError.value = null
            prefs.edit().putLong("last_logged_in_user_id", user.id).apply()
            return true
        } else {
            _loginError.value = "Incorrect PIN for ${user.displayName}. Please try again."
            return false
        }
    }

    fun loginWithCredentials(username: String, enteredPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username.trim())
            if (user != null && user.isActive && user.password == enteredPassword) {
                _currentUser.value = user
                _loginError.value = null
                prefs.edit().putLong("last_logged_in_user_id", user.id).apply()
                onResult(true)
            } else {
                _loginError.value = "Invalid username or password."
                onResult(false)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
        prefs.edit().remove("last_logged_in_user_id").apply()
    }

    fun lockScreen() {
        _currentUser.value = null
        _loginError.value = null
    }

    fun addUser(user: UserAccount, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(user.username.trim())
            if (existing != null) {
                onComplete(false, "Username '${user.username}' is already taken.")
                return@launch
            }
            repository.insertUser(user)
            triggerAutoSafetyBackup()
            onComplete(true, "User ${user.displayName} created successfully.")
        }
    }

    fun updateUser(user: UserAccount, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repository.updateUser(user)
            if (_currentUser.value?.id == user.id) {
                _currentUser.value = user
            }
            triggerAutoSafetyBackup()
            onComplete(true, "User updated successfully.")
        }
    }

    fun deleteUser(user: UserAccount, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val all = repository.getAllUsersList()
            val admins = all.filter { it.role.equals("ADMIN", ignoreCase = true) }
            if (user.role.equals("ADMIN", ignoreCase = true) && admins.size <= 1) {
                onComplete(false, "Cannot delete the only Admin account.")
                return@launch
            }
            repository.deleteUser(user)
            if (_currentUser.value?.id == user.id) {
                logout()
            }
            triggerAutoSafetyBackup()
            onComplete(true, "User deleted.")
        }
    }

    suspend fun exportStoreBackupData(): com.example.util.StoreBackupData {
        val profile = _storeProfile.value
        val products = repository.getAllProductsList()
        val customers = repository.getAllCustomersList()
        val sales = repository.getAllSalesList()
        val debts = repository.getAllDebtRecordsList()
        val users = repository.getAllUsersList()

        return com.example.util.StoreBackupData(
            exportDate = System.currentTimeMillis(),
            storeName = profile.storeName,
            ownerName = profile.ownerName,
            storePhone = profile.storePhone,
            storeAddress = profile.storeAddress,
            receiptMessage = profile.receiptMessage,
            startingCashDrawer = profile.startingCashDrawer,
            products = products,
            customers = customers,
            sales = sales,
            debtRecords = debts,
            users = users
        )
    }

    fun restoreStoreBackupData(backup: com.example.util.StoreBackupData, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            completeStoreSetup(
                storeName = backup.storeName,
                ownerName = backup.ownerName,
                phone = backup.storePhone,
                address = backup.storeAddress,
                message = backup.receiptMessage,
                startingCash = backup.startingCashDrawer,
                loadStarterPack = false
            )
            repository.restoreDatabaseFromBackup(
                products = backup.products,
                customers = backup.customers,
                sales = backup.sales,
                debtRecords = backup.debtRecords,
                users = backup.users
            )
            onComplete()
        }
    }

    fun updateStoreProfile(name: String, phone: String, address: String, message: String) {
        completeStoreSetup(
            storeName = name,
            ownerName = _storeProfile.value.ownerName,
            phone = phone,
            address = address,
            message = message,
            startingCash = _storeProfile.value.startingCashDrawer,
            loadStarterPack = false
        )
    }

    // Products & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _searchQuery,
        _selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            val matchesCategory = category == "All" || product.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.barcode.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expiration Alerts: Expired, Expiring Soon, Need Date Review
    val expiredProducts: StateFlow<List<Product>> = allProducts.map { products ->
        val now = System.currentTimeMillis()
        products.filter { it.expiryDate != null && it.expiryDate > 0L && it.expiryDate < now }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiringSoonProducts: StateFlow<List<Product>> = allProducts.map { products ->
        val now = System.currentTimeMillis()
        val thirtyDaysFromNow = now + (30L * 24 * 60 * 60 * 1000)
        products.filter { it.expiryDate != null && it.expiryDate > 0L && it.expiryDate >= now && it.expiryDate <= thirtyDaysFromNow }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val needDateReviewProducts: StateFlow<List<Product>> = allProducts.map { products ->
        products.filter { it.expiryDate == null || it.expiryDate == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customers & Utang
    val allCustomers: StateFlow<List<CustomerDebt>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCustomer = MutableStateFlow<CustomerDebt?>(null)
    val selectedCustomer: StateFlow<CustomerDebt?> = _selectedCustomer.asStateFlow()

    private val _customerRecords = MutableStateFlow<List<DebtRecord>>(emptyList())
    val customerRecords: StateFlow<List<DebtRecord>> = _customerRecords.asStateFlow()

    // Sales & Movements
    val allSales: StateFlow<List<SaleTransaction>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentStockMovements: StateFlow<List<StockMovement>> = repository.recentStockMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart & POS Checkout State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _discountType = MutableStateFlow("NONE") // "NONE", "SENIOR_PWD", "CUSTOM"
    val discountType: StateFlow<String> = _discountType.asStateFlow()

    private val _customDiscountAmount = MutableStateFlow(0.0)
    val customDiscountAmount: StateFlow<Double> = _customDiscountAmount.asStateFlow()

    private val _paymentMethod = MutableStateFlow("CASH") // "CASH", "GCASH_MAYA", "UTANG_LENDING"
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    private val _cashTendered = MutableStateFlow("")
    val cashTendered: StateFlow<String> = _cashTendered.asStateFlow()

    private val _selectedDebtor = MutableStateFlow<CustomerDebt?>(null)
    val selectedDebtor: StateFlow<CustomerDebt?> = _selectedDebtor.asStateFlow()

    private val _lastCompletedSale = MutableStateFlow<SaleTransaction?>(null)
    val lastCompletedSale: StateFlow<SaleTransaction?> = _lastCompletedSale.asStateFlow()

    // Reports filter
    private val _reportPeriod = MutableStateFlow(ReportPeriod.TODAY)
    val reportPeriod: StateFlow<ReportPeriod> = _reportPeriod.asStateFlow()

    val allDebtRecords: StateFlow<List<DebtRecord>> = repository.allDebtRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allZReads: StateFlow<List<ZReadReport>> = repository.allZReads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val realtimeReports: StateFlow<RealtimeReportsState> = combine(
        _reportPeriod,
        allSales,
        allProducts,
        allCustomers,
        allDebtRecords
    ) { period, sales, products, customers, debtRecords ->
        val validSales = sales.filter { !it.isVoided }
        val calendar = Calendar.getInstance()
        val now = System.currentTimeMillis()

        calendar.timeInMillis = now
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis

        val periodFilteredSales = when (period) {
            ReportPeriod.TODAY -> validSales.filter { it.timestamp >= startOfToday }
            ReportPeriod.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startOfWeek = calendar.timeInMillis
                validSales.filter { it.timestamp >= startOfWeek }
            }
            ReportPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = calendar.timeInMillis
                validSales.filter { it.timestamp >= startOfMonth }
            }
            ReportPeriod.ALL_TIME -> validSales
        }

        val grossSales = periodFilteredSales.sumOf { it.finalAmount }
        val netProfit = periodFilteredSales.sumOf { it.profit }
        val marginPercent = if (grossSales > 0) (netProfit / grossSales) * 100.0 else 0.0
        val txCount = periodFilteredSales.size
        val avgBasket = if (txCount > 0) grossSales / txCount else 0.0

        val cashSales = periodFilteredSales.filter { it.paymentMethod == "CASH" }.sumOf { it.finalAmount }
        val gcashSales = periodFilteredSales.filter { it.paymentMethod == "GCASH_MAYA" }.sumOf { it.finalAmount }
        val utangSales = periodFilteredSales.filter { it.paymentMethod == "UTANG_LENDING" }.sumOf { it.finalAmount }

        // Today's cash drawer: Cash POS sales today + debt payments received today
        val todayCashSales = validSales.filter { it.timestamp >= startOfToday && it.paymentMethod == "CASH" }.sumOf { it.finalAmount }
        val todayDebtPayments = debtRecords.filter { it.date >= startOfToday && it.type == "PAYMENT" }.sumOf { it.amount }
        val cashOnHandToday = todayCashSales + todayDebtPayments

        val totalOutstandingDebt = customers.sumOf { it.totalDebt }

        // Top selling items parsed from itemsSummary
        val itemQtyMap = mutableMapOf<String, Int>()
        val itemRevenueMap = mutableMapOf<String, Double>()
        val itemRegex = Regex("""(.+?)\s+x(\d+)\s+\(₱([\d.]+)\)""")

        periodFilteredSales.forEach { sale ->
            val entries = sale.itemsSummary.split(", ")
            for (entry in entries) {
                val match = itemRegex.matchEntire(entry.trim())
                if (match != null) {
                    val name = match.groupValues[1].trim()
                    val qty = match.groupValues[2].toIntOrNull() ?: 1
                    val rev = match.groupValues[3].toDoubleOrNull() ?: 0.0
                    itemQtyMap[name] = (itemQtyMap[name] ?: 0) + qty
                    itemRevenueMap[name] = (itemRevenueMap[name] ?: 0.0) + rev
                } else if (entry.isNotBlank()) {
                    val parts = entry.split(" x")
                    val name = parts.firstOrNull()?.trim() ?: entry
                    itemQtyMap[name] = (itemQtyMap[name] ?: 0) + 1
                }
            }
        }

        val topSellingItems = itemQtyMap.entries
            .sortedByDescending { it.value }
            .take(6)
            .map { (name, qty) ->
                val revenue = itemRevenueMap[name] ?: 0.0
                val pct = if (grossSales > 0) (revenue / grossSales) * 100.0 else 0.0
                TopSellingItem(
                    productName = name,
                    quantitySold = qty,
                    totalRevenue = revenue,
                    percentageOfSales = pct
                )
            }

        // Hourly breakdown
        val morningSales = periodFilteredSales.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour in 5..10
        }
        val noonSales = periodFilteredSales.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour in 11..14
        }
        val afternoonSales = periodFilteredSales.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour in 15..17
        }
        val eveningSales = periodFilteredSales.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour in 18..23
        }

        val hourlyBreakdown = listOf(
            HourlySalesBucket("Morning (5am - 10am)", morningSales.sumOf { it.finalAmount }, morningSales.size),
            HourlySalesBucket("Lunch Peak (11am - 2pm)", noonSales.sumOf { it.finalAmount }, noonSales.size),
            HourlySalesBucket("Merienda (3pm - 5pm)", afternoonSales.sumOf { it.finalAmount }, afternoonSales.size),
            HourlySalesBucket("Evening (6pm - 11pm)", eveningSales.sumOf { it.finalAmount }, eveningSales.size)
        )

        // Inventory valuation
        val retailVal = products.sumOf { it.sellingPrice * it.stockQuantity }
        val costVal = products.sumOf { it.costPrice * it.stockQuantity }
        val projProfit = (retailVal - costVal).coerceAtLeast(0.0)

        val latestTimestamp = validSales.maxOfOrNull { it.timestamp }

        RealtimeReportsState(
            period = period,
            filteredSales = periodFilteredSales,
            grossSales = grossSales,
            netProfit = netProfit,
            profitMarginPercent = marginPercent,
            transactionsCount = txCount,
            averageBasket = avgBasket,
            cashSales = cashSales,
            gcashSales = gcashSales,
            utangSales = utangSales,
            cashOnHandToday = cashOnHandToday,
            utangCollectedToday = todayDebtPayments,
            totalOutstandingDebt = totalOutstandingDebt,
            topSellingItems = topSellingItems,
            hourlyBreakdown = hourlyBreakdown,
            inventoryRetailValue = retailVal,
            inventoryCostValue = costVal,
            projectedInventoryProfit = projProfit,
            lastTransactionTimestamp = latestTimestamp,
            totalSalesRowsInDb = sales.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RealtimeReportsState())

    fun triggerAutoSafetyBackup() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val backup = exportStoreBackupData()
                com.example.util.PersistentSafetyVault.saveVault(getApplication(), backup)
            } catch (e: Exception) {
                // Background safety backup caught silently
            }
        }
    }

    init {
        viewModelScope.launch {
            if (com.example.util.PersistentSafetyVault.hasVault(getApplication())) {
                val pCount = repository.getProductCount()
                if (pCount == 0) {
                    val vaultData = com.example.util.PersistentSafetyVault.loadVault(getApplication())
                    if (vaultData != null && (vaultData.products.isNotEmpty() || vaultData.sales.isNotEmpty())) {
                        restoreStoreBackupData(vaultData)
                        return@launch
                    }
                }
            }
            repository.initDefaultDataIfNeeded()
        }
    }

    // POS Cart Operations
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(product = product, quantity = 1))
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(productId: Long, quantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            if (quantity <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = quantity)
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(productId: Long) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _discountType.value = "NONE"
        _customDiscountAmount.value = 0.0
        _paymentMethod.value = "CASH"
        _cashTendered.value = ""
        _selectedDebtor.value = null
    }

    fun setDiscountType(type: String, customAmount: Double = 0.0) {
        _discountType.value = type
        _customDiscountAmount.value = customAmount
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun setCashTendered(amountStr: String) {
        _cashTendered.value = amountStr
    }

    fun selectDebtor(customer: CustomerDebt?) {
        _selectedDebtor.value = customer
    }

    fun dismissReceipt() {
        _lastCompletedSale.value = null
    }

    fun scanBarcodeAndAddToCart(barcode: String): Boolean {
        val product = allProducts.value.find { it.barcode.equals(barcode.trim(), ignoreCase = true) }
        return if (product != null) {
            addToCart(product)
            true
        } else {
            false
        }
    }

    // Checkout Calculation
    fun getCartSubtotal(): Double {
        return _cartItems.value.sumOf { it.subtotal }
    }

    fun getDiscountAmount(): Double {
        val subtotal = getCartSubtotal()
        return when (_discountType.value) {
            "SENIOR_PWD" -> subtotal * 0.20 // 20% Senior/PWD discount
            "CUSTOM" -> _customDiscountAmount.value.coerceAtMost(subtotal)
            else -> 0.0
        }
    }

    fun getFinalAmount(): Double {
        val subtotal = getCartSubtotal()
        val discount = getDiscountAmount()
        return (subtotal - discount).coerceAtLeast(0.0)
    }

    fun getChangeAmount(): Double {
        val finalAmount = getFinalAmount()
        val tendered = _cashTendered.value.toDoubleOrNull() ?: 0.0
        return (tendered - finalAmount).coerceAtLeast(0.0)
    }

    fun completeCheckout(onSuccess: (SaleTransaction) -> Unit) {
        val items = _cartItems.value
        if (items.isEmpty()) return

        val subtotal = getCartSubtotal()
        val discount = getDiscountAmount()
        val finalAmount = getFinalAmount()
        val method = _paymentMethod.value

        val amountPaid = when (method) {
            "CASH" -> _cashTendered.value.toDoubleOrNull() ?: finalAmount
            else -> finalAmount
        }
        val change = if (method == "CASH") (amountPaid - finalAmount).coerceAtLeast(0.0) else 0.0

        val customer = _selectedDebtor.value

        viewModelScope.launch {
            val sale = repository.processCheckout(
                cartItems = items,
                totalAmount = subtotal,
                discountAmount = discount,
                discountType = _discountType.value,
                finalAmount = finalAmount,
                amountPaid = amountPaid,
                changeAmount = change,
                paymentMethod = method,
                customerId = customer?.id,
                customerName = customer?.name
            )
            _lastCompletedSale.value = sale
            clearCart()
            triggerAutoSafetyBackup()
            onSuccess(sale)
        }
    }

    // Inventory Product Actions
    fun saveProduct(
        id: Long = 0,
        name: String,
        barcode: String,
        category: String,
        costPrice: Double,
        sellingPrice: Double,
        stockQuantity: Int,
        unit: String,
        lowStockThreshold: Int,
        expiryDate: Long? = null
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                repository.insertProduct(
                    Product(
                        name = name.trim(),
                        barcode = barcode.trim(),
                        category = category.trim(),
                        costPrice = costPrice,
                        sellingPrice = sellingPrice,
                        stockQuantity = stockQuantity,
                        unit = unit.trim().ifBlank { "pc" },
                        lowStockThreshold = lowStockThreshold,
                        expiryDate = expiryDate
                    )
                )
            } else {
                repository.updateProduct(
                    Product(
                        id = id,
                        name = name.trim(),
                        barcode = barcode.trim(),
                        category = category.trim(),
                        costPrice = costPrice,
                        sellingPrice = sellingPrice,
                        stockQuantity = stockQuantity,
                        unit = unit.trim().ifBlank { "pc" },
                        lowStockThreshold = lowStockThreshold,
                        expiryDate = expiryDate,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            }
            triggerAutoSafetyBackup()
        }
    }

    fun updateProductExpiryDate(productId: Long, expiryDate: Long?) {
        viewModelScope.launch {
            repository.updateProductExpiry(productId, expiryDate)
            triggerAutoSafetyBackup()
        }
    }

    fun restockProduct(productId: Long, addedQty: Int, notes: String) {
        viewModelScope.launch {
            repository.restockProduct(productId, addedQty, notes)
            triggerAutoSafetyBackup()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            triggerAutoSafetyBackup()
        }
    }

    // Lending Actions
    fun selectCustomer(customer: CustomerDebt?) {
        _selectedCustomer.value = customer
        if (customer != null) {
            viewModelScope.launch {
                repository.getRecordsForCustomer(customer.id).collect { records ->
                    _customerRecords.value = records
                }
            }
        } else {
            _customerRecords.value = emptyList()
        }
    }

    fun saveCustomer(name: String, phone: String, note: String) {
        viewModelScope.launch {
            repository.insertCustomer(
                CustomerDebt(
                    name = name.trim(),
                    phone = phone.trim(),
                    addressOrNote = note.trim()
                )
            )
            triggerAutoSafetyBackup()
        }
    }

    fun recordCustomerPayment(customerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.recordCustomerPayment(customerId, amount, notes)
            // Refresh customer
            val updated = database.customerDao().getCustomerById(customerId)
            _selectedCustomer.value = updated
            triggerAutoSafetyBackup()
        }
    }

    fun addCustomerUtang(customerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.addCustomerDebt(customerId, amount, notes)
            val updated = database.customerDao().getCustomerById(customerId)
            _selectedCustomer.value = updated
            triggerAutoSafetyBackup()
        }
    }

    // Reports & Analytics
    fun setReportPeriod(period: ReportPeriod) {
        _reportPeriod.value = period
    }

    fun getFilteredSales(): List<SaleTransaction> {
        val sales = allSales.value.filter { !it.isVoided }
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        return when (_reportPeriod.value) {
            ReportPeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis
                sales.filter { it.timestamp >= startOfDay }
            }
            ReportPeriod.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfWeek = calendar.timeInMillis
                sales.filter { it.timestamp >= startOfWeek }
            }
            ReportPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfMonth = calendar.timeInMillis
                sales.filter { it.timestamp >= startOfMonth }
            }
            ReportPeriod.ALL_TIME -> sales
        }
    }

    fun voidTransaction(saleId: Long) {
        viewModelScope.launch {
            repository.voidSale(saleId)
        }
    }

    fun addQuickTestSale(paymentMethod: String = "CASH") {
        viewModelScope.launch {
            val products = allProducts.value
            if (products.isNotEmpty()) {
                val sampleProduct = products.shuffled().first()
                val quantity = (1..3).random()
                val cartItem = CartItem(product = sampleProduct, quantity = quantity)
                repository.processCheckout(
                    cartItems = listOf(cartItem),
                    totalAmount = cartItem.subtotal,
                    discountAmount = 0.0,
                    discountType = "NONE",
                    finalAmount = cartItem.subtotal,
                    amountPaid = cartItem.subtotal,
                    changeAmount = 0.0,
                    paymentMethod = paymentMethod,
                    customerId = null,
                    customerName = null
                )
            }
        }
    }

    suspend fun generateCurrentXRead(): XReadSnapshot {
        val latestZ = repository.getLatestZRead()
        val periodStart = latestZ?.periodEnd ?: run {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
        val periodEnd = System.currentTimeMillis()

        val allSalesList = repository.getAllSalesList()
        val shiftSales = allSalesList.filter { it.timestamp >= periodStart && it.timestamp <= periodEnd }
        val validSales = shiftSales.filter { !it.isVoided }
        val voidedSales = shiftSales.filter { it.isVoided }

        val allDebts = repository.getAllDebtRecordsList()
        val shiftDebtPayments = allDebts.filter { it.date >= periodStart && it.date <= periodEnd && it.type == "PAYMENT" }
        val debtCollected = shiftDebtPayments.sumOf { it.amount }

        val floatAmt = _storeProfile.value.startingCashDrawer
        val grossSales = validSales.sumOf { it.totalAmount }
        val discounts = validSales.sumOf { it.discountAmount }
        val netSales = validSales.sumOf { it.finalAmount }
        val totalProfit = validSales.sumOf { it.profit }

        val cashSales = validSales.filter { it.paymentMethod == "CASH" }.sumOf { it.finalAmount }
        val gcashSales = validSales.filter { it.paymentMethod == "GCASH_MAYA" }.sumOf { it.finalAmount }
        val creditSales = validSales.filter { it.paymentMethod == "UTANG_LENDING" }.sumOf { it.finalAmount }

        val expectedCash = floatAmt + cashSales + debtCollected

        val sortedShiftSales = shiftSales.sortedBy { it.timestamp }
        val firstRcpt = sortedShiftSales.firstOrNull()?.receiptNumber ?: "None"
        val lastRcpt = sortedShiftSales.lastOrNull()?.receiptNumber ?: "None"

        return XReadSnapshot(
            generatedAt = periodEnd,
            periodStart = periodStart,
            periodEnd = periodEnd,
            openingFloat = floatAmt,
            grossSales = grossSales,
            netSales = netSales,
            totalProfit = totalProfit,
            transactionCount = validSales.size,
            cashSales = cashSales,
            gcashSales = gcashSales,
            creditSales = creditSales,
            customerDebtPaymentsCollected = debtCollected,
            totalDiscounts = discounts,
            voidedCount = voidedSales.size,
            voidedTotal = voidedSales.sumOf { it.finalAmount },
            expectedCashInDrawer = expectedCash,
            firstReceiptNumber = firstRcpt,
            lastReceiptNumber = lastRcpt
        )
    }

    fun performZRead(
        actualCash: Double?,
        notes: String = "",
        onCompleted: (ZReadReport) -> Unit = {}
    ) {
        viewModelScope.launch {
            val snapshot = generateCurrentXRead()
            val nextZNumber = "Z-" + String.format(Locale.US, "%04d", repository.allZReads.let {
                // Determine next sequential index
                repository.getAllZReadsList().size + 1
            })

            val shortageOver = if (actualCash != null) actualCash - snapshot.expectedCashInDrawer else null

            val zReport = ZReadReport(
                zReadNumber = nextZNumber,
                generatedAt = System.currentTimeMillis(),
                periodStart = snapshot.periodStart,
                periodEnd = snapshot.periodEnd,
                openingFloat = snapshot.openingFloat,
                grossSales = snapshot.grossSales,
                netSales = snapshot.netSales,
                totalProfit = snapshot.totalProfit,
                transactionCount = snapshot.transactionCount,
                cashSales = snapshot.cashSales,
                gcashSales = snapshot.gcashSales,
                creditSales = snapshot.creditSales,
                customerDebtPaymentsCollected = snapshot.customerDebtPaymentsCollected,
                totalDiscounts = snapshot.totalDiscounts,
                voidedCount = snapshot.voidedCount,
                voidedTotal = snapshot.voidedTotal,
                expectedCashInDrawer = snapshot.expectedCashInDrawer,
                actualCashCounted = actualCash,
                cashShortageOver = shortageOver,
                notes = notes,
                firstReceiptNumber = snapshot.firstReceiptNumber,
                lastReceiptNumber = snapshot.lastReceiptNumber
            )

            val id = repository.insertZRead(zReport)
            val savedReport = zReport.copy(id = id)
            triggerAutoSafetyBackup()
            onCompleted(savedReport)
        }
    }
}
