package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TindaRepository(private val database: AppDatabase) {
    private val productDao = database.productDao()
    private val customerDao = database.customerDao()
    private val debtRecordDao = database.debtRecordDao()
    private val saleDao = database.saleDao()
    private val stockMovementDao = database.stockMovementDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()
    val allCustomers: Flow<List<CustomerDebt>> = customerDao.getAllCustomers()
    val allSales: Flow<List<SaleTransaction>> = saleDao.getAllSales()
    val allDebtRecords: Flow<List<DebtRecord>> = debtRecordDao.getAllRecords()
    val recentStockMovements: Flow<List<StockMovement>> = stockMovementDao.getRecentMovements()

    suspend fun initDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        if (productDao.getProductCount() == 0) {
            productDao.insertAll(SampleData.initialProducts)
        }
        if (customerDao.getCustomerCount() == 0) {
            customerDao.insertAll(SampleData.initialCustomers)
        }
        if (saleDao.getSaleCount() == 0) {
            saleDao.insertAll(SampleData.generateInitialSales())
        }
    }

    suspend fun resetDatabaseToStarterPack() = withContext(Dispatchers.IO) {
        productDao.deleteAllProducts()
        customerDao.deleteAllCustomers()
        debtRecordDao.deleteAllDebtRecords()
        saleDao.deleteAllSales()
        stockMovementDao.deleteAllMovements()

        productDao.insertAll(SampleData.initialProducts)
        customerDao.insertAll(SampleData.initialCustomers)
        saleDao.insertAll(SampleData.generateInitialSales())
    }

    suspend fun clearAllDataForFreshStart() = withContext(Dispatchers.IO) {
        productDao.deleteAllProducts()
        customerDao.deleteAllCustomers()
        debtRecordDao.deleteAllDebtRecords()
        saleDao.deleteAllSales()
        stockMovementDao.deleteAllMovements()
    }

    suspend fun getAllProductsList(): List<Product> = withContext(Dispatchers.IO) {
        productDao.getAllProductsList()
    }

    suspend fun getAllCustomersList(): List<CustomerDebt> = withContext(Dispatchers.IO) {
        customerDao.getAllCustomersList()
    }

    suspend fun getAllSalesList(): List<SaleTransaction> = withContext(Dispatchers.IO) {
        saleDao.getAllSalesList()
    }

    suspend fun getAllDebtRecordsList(): List<DebtRecord> = withContext(Dispatchers.IO) {
        debtRecordDao.getAllRecordsList()
    }

    suspend fun restoreDatabaseFromBackup(
        products: List<Product>,
        customers: List<CustomerDebt>,
        sales: List<SaleTransaction>,
        debtRecords: List<DebtRecord>
    ) = withContext(Dispatchers.IO) {
        productDao.deleteAllProducts()
        customerDao.deleteAllCustomers()
        debtRecordDao.deleteAllDebtRecords()
        saleDao.deleteAllSales()
        stockMovementDao.deleteAllMovements()

        if (products.isNotEmpty()) productDao.insertAll(products)
        if (customers.isNotEmpty()) customerDao.insertAll(customers)
        if (sales.isNotEmpty()) saleDao.insertAll(sales)
        if (debtRecords.isNotEmpty()) debtRecordDao.insertAll(debtRecords)
    }

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    suspend fun getProductByBarcode(barcode: String): Product? = withContext(Dispatchers.IO) {
        productDao.getProductByBarcode(barcode)
    }

    suspend fun insertProduct(product: Product): Long = withContext(Dispatchers.IO) {
        val id = productDao.insertProduct(product)
        stockMovementDao.insertMovement(
            StockMovement(
                productId = id,
                productName = product.name,
                type = "RESTOCK",
                quantityDelta = product.stockQuantity,
                quantityAfter = product.stockQuantity,
                notes = "Initial stock entry"
            )
        )
        id
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun updateProductExpiry(productId: Long, expiryDate: Long?) = withContext(Dispatchers.IO) {
        productDao.updateProductExpiry(productId, expiryDate)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    suspend fun restockProduct(productId: Long, addedQty: Int, notes: String) = withContext(Dispatchers.IO) {
        val current = productDao.getProductById(productId) ?: return@withContext
        val newQty = current.stockQuantity + addedQty
        productDao.updateProduct(current.copy(stockQuantity = newQty, lastUpdated = System.currentTimeMillis()))
        stockMovementDao.insertMovement(
            StockMovement(
                productId = productId,
                productName = current.name,
                type = "RESTOCK",
                quantityDelta = addedQty,
                quantityAfter = newQty,
                notes = notes
            )
        )
    }

    // Checkout processing
    suspend fun processCheckout(
        cartItems: List<CartItem>,
        totalAmount: Double,
        discountAmount: Double,
        discountType: String,
        finalAmount: Double,
        amountPaid: Double,
        changeAmount: Double,
        paymentMethod: String,
        customerId: Long?,
        customerName: String?
    ): SaleTransaction = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val dateStr = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(now))
        val receiptNumber = "REC-$dateStr"

        var totalCost = 0.0
        val itemsSummaryBuilder = StringBuilder()

        for (item in cartItems) {
            val subtotal = item.subtotal
            val subtotalCost = item.subtotalCost
            totalCost += subtotalCost

            if (itemsSummaryBuilder.isNotEmpty()) itemsSummaryBuilder.append(", ")
            itemsSummaryBuilder.append("${item.product.name} x${item.quantity} (₱${String.format(Locale.US, "%.2f", subtotal)})")

            // Deduct stock
            val prod = productDao.getProductById(item.product.id)
            if (prod != null) {
                val newQty = (prod.stockQuantity - item.quantity).coerceAtLeast(0)
                productDao.updateProduct(prod.copy(stockQuantity = newQty, lastUpdated = now))
                stockMovementDao.insertMovement(
                    StockMovement(
                        productId = prod.id,
                        productName = prod.name,
                        type = "SALE",
                        quantityDelta = -item.quantity,
                        quantityAfter = newQty,
                        notes = "Sale #$receiptNumber"
                    )
                )
            }
        }

        val profit = finalAmount - totalCost

        val sale = SaleTransaction(
            receiptNumber = receiptNumber,
            totalAmount = totalAmount,
            discountAmount = discountAmount,
            discountType = discountType,
            finalAmount = finalAmount,
            amountPaid = amountPaid,
            changeAmount = changeAmount,
            paymentMethod = paymentMethod,
            customerId = customerId,
            customerName = customerName,
            totalCost = totalCost,
            profit = profit,
            itemsSummary = itemsSummaryBuilder.toString(),
            timestamp = now,
            isVoided = false
        )

        val saleId = saleDao.insertSale(sale)

        // If Utang / Lending, charge to customer
        if (paymentMethod == "UTANG_LENDING" && customerId != null) {
            val customer = customerDao.getCustomerById(customerId)
            if (customer != null) {
                val newDebt = customer.totalDebt + finalAmount
                customerDao.adjustDebt(customerId, finalAmount, now)
                debtRecordDao.insertRecord(
                    DebtRecord(
                        customerId = customerId,
                        type = "BORROW",
                        amount = finalAmount,
                        notes = "POS Utang #$receiptNumber",
                        balanceAfter = newDebt,
                        date = now
                    )
                )
            }
        }

        sale.copy(id = saleId)
    }

    suspend fun voidSale(saleId: Long) = withContext(Dispatchers.IO) {
        val sale = saleDao.getSaleById(saleId) ?: return@withContext
        if (sale.isVoided) return@withContext
        saleDao.voidSale(saleId)

        // If utang, deduct debt
        if (sale.paymentMethod == "UTANG_LENDING" && sale.customerId != null) {
            val customer = customerDao.getCustomerById(sale.customerId)
            if (customer != null) {
                val newDebt = (customer.totalDebt - sale.finalAmount).coerceAtLeast(0.0)
                customerDao.adjustDebt(sale.customerId, -sale.finalAmount)
                debtRecordDao.insertRecord(
                    DebtRecord(
                        customerId = sale.customerId,
                        type = "PAYMENT",
                        amount = sale.finalAmount,
                        notes = "Voided Transaction #${sale.receiptNumber}",
                        balanceAfter = newDebt
                    )
                )
            }
        }
    }

    // Customer & Lending
    suspend fun insertCustomer(customer: CustomerDebt): Long = withContext(Dispatchers.IO) {
        customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: CustomerDebt) = withContext(Dispatchers.IO) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: CustomerDebt) = withContext(Dispatchers.IO) {
        customerDao.deleteCustomer(customer)
    }

    fun getRecordsForCustomer(customerId: Long): Flow<List<DebtRecord>> =
        debtRecordDao.getRecordsForCustomer(customerId)

    suspend fun recordCustomerPayment(
        customerId: Long,
        paymentAmount: Double,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val customer = customerDao.getCustomerById(customerId) ?: return@withContext
        val newDebt = (customer.totalDebt - paymentAmount).coerceAtLeast(0.0)
        customerDao.adjustDebt(customerId, -paymentAmount)
        debtRecordDao.insertRecord(
            DebtRecord(
                customerId = customerId,
                type = "PAYMENT",
                amount = paymentAmount,
                notes = if (notes.isBlank()) "Payment received" else notes,
                balanceAfter = newDebt
            )
        )
    }

    suspend fun addCustomerDebt(
        customerId: Long,
        borrowAmount: Double,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val customer = customerDao.getCustomerById(customerId) ?: return@withContext
        val newDebt = customer.totalDebt + borrowAmount
        customerDao.adjustDebt(customerId, borrowAmount)
        debtRecordDao.insertRecord(
            DebtRecord(
                customerId = customerId,
                type = "BORROW",
                amount = borrowAmount,
                notes = if (notes.isBlank()) "Manual utang entry" else notes,
                balanceAfter = newDebt
            )
        )
    }
}
