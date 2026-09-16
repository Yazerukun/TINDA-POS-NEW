package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProductsList(): List<Product>

    @Query("SELECT * FROM products WHERE stockQuantity <= lowStockThreshold ORDER BY stockQuantity ASC")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :delta WHERE id = :id")
    suspend fun updateStock(id: Long, delta: Int)

    @Query("UPDATE products SET expiryDate = :expiryDate, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateProductExpiry(id: Long, expiryDate: Long?, lastUpdated: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY totalDebt DESC, name ASC")
    fun getAllCustomers(): Flow<List<CustomerDebt>>

    @Query("SELECT * FROM customers ORDER BY totalDebt DESC, name ASC")
    suspend fun getAllCustomersList(): List<CustomerDebt>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): CustomerDebt?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomers(query: String): Flow<List<CustomerDebt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerDebt): Long

    @Update
    suspend fun updateCustomer(customer: CustomerDebt)

    @Delete
    suspend fun deleteCustomer(customer: CustomerDebt)

    @Query("UPDATE customers SET totalDebt = totalDebt + :delta, lastTransactionAt = :timestamp WHERE id = :customerId")
    suspend fun adjustDebt(customerId: Long, delta: Double, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM customers")
    suspend fun getCustomerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerDebt>)

    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()
}

@Dao
interface DebtRecordDao {
    @Query("SELECT * FROM debt_records WHERE customerId = :customerId ORDER BY date DESC")
    fun getRecordsForCustomer(customerId: Long): Flow<List<DebtRecord>>

    @Query("SELECT * FROM debt_records ORDER BY date DESC LIMIT 100")
    fun getAllRecentRecords(): Flow<List<DebtRecord>>

    @Query("SELECT * FROM debt_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<DebtRecord>>

    @Query("SELECT * FROM debt_records ORDER BY date DESC")
    suspend fun getAllRecordsList(): List<DebtRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DebtRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DebtRecord): Long

    @Query("DELETE FROM debt_records")
    suspend fun deleteAllDebtRecords()
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sale_transactions ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sale_transactions ORDER BY timestamp DESC")
    suspend fun getAllSalesList(): List<SaleTransaction>

    @Query("SELECT * FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getSalesByDateRange(startTime: Long, endTime: Long): Flow<List<SaleTransaction>>

    @Query("SELECT COUNT(*) FROM sale_transactions")
    suspend fun getSaleCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sales: List<SaleTransaction>)

    @Query("UPDATE sale_transactions SET isVoided = 1 WHERE id = :id")
    suspend fun voidSale(id: Long)

    @Query("SELECT * FROM sale_transactions WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): SaleTransaction?

    @Query("DELETE FROM sale_transactions")
    suspend fun deleteAllSales()
}

@Dao
interface StockMovementDao {
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC LIMIT 100")
    fun getRecentMovements(): Flow<List<StockMovement>>

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun getMovementsForProduct(productId: Long): Flow<List<StockMovement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: StockMovement): Long

    @Query("DELETE FROM stock_movements")
    suspend fun deleteAllMovements()
}
