package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val barcode: String = "",
    val category: String,
    val costPrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Int,
    val unit: String = "pc",
    val lowStockThreshold: Int = 5,
    val expiryDate: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class CustomerDebt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val addressOrNote: String = "",
    val totalDebt: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastTransactionAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "debt_records")
data class DebtRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val type: String, // "BORROW" or "PAYMENT"
    val amount: Double,
    val notes: String = "",
    val balanceAfter: Double,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "sale_transactions")
data class SaleTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val discountType: String = "NONE", // "NONE", "SENIOR_PWD", "CUSTOM"
    val finalAmount: Double,
    val amountPaid: Double,
    val changeAmount: Double,
    val paymentMethod: String, // "CASH", "GCASH_MAYA", "UTANG_LENDING"
    val customerName: String? = null,
    val customerId: Long? = null,
    val totalCost: Double = 0.0,
    val profit: Double = 0.0,
    val itemsSummary: String, // format: "Item Name xQty (₱Subtotal), ..."
    val timestamp: Long = System.currentTimeMillis(),
    val isVoided: Boolean = false
)

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // "RESTOCK", "SALE", "ADJUSTMENT"
    val quantityDelta: Int,
    val quantityAfter: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double
        get() = product.sellingPrice * quantity

    val subtotalCost: Double
        get() = product.costPrice * quantity
}
