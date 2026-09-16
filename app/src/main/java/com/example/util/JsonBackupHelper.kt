package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.CustomerDebt
import com.example.data.DebtRecord
import com.example.data.Product
import com.example.data.SaleTransaction
import com.example.data.UserAccount
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StoreBackupData(
    val exportDate: Long,
    val storeName: String,
    val ownerName: String,
    val storePhone: String,
    val storeAddress: String,
    val receiptMessage: String,
    val startingCashDrawer: Double,
    val products: List<Product>,
    val customers: List<CustomerDebt>,
    val sales: List<SaleTransaction>,
    val debtRecords: List<DebtRecord>,
    val users: List<UserAccount> = emptyList()
)

object JsonBackupHelper {

    fun backupToJsonString(backup: StoreBackupData): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", backup.exportDate)
        root.put("storeName", backup.storeName)
        root.put("ownerName", backup.ownerName)
        root.put("storePhone", backup.storePhone)
        root.put("storeAddress", backup.storeAddress)
        root.put("receiptMessage", backup.receiptMessage)
        root.put("startingCashDrawer", backup.startingCashDrawer)

        // Products
        val productsArray = JSONArray()
        for (p in backup.products) {
            val pObj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("category", p.category)
                put("barcode", p.barcode)
                put("costPrice", p.costPrice)
                put("sellingPrice", p.sellingPrice)
                put("stockQuantity", p.stockQuantity)
                put("unit", p.unit)
                put("lowStockThreshold", p.lowStockThreshold)
                if (p.expiryDate != null) put("expiryDate", p.expiryDate)
                put("lastUpdated", p.lastUpdated)
            }
            productsArray.put(pObj)
        }
        root.put("products", productsArray)

        // Customers
        val customersArray = JSONArray()
        for (c in backup.customers) {
            val cObj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("phone", c.phone)
                put("addressOrNote", c.addressOrNote)
                put("totalDebt", c.totalDebt)
                put("createdAt", c.createdAt)
                put("lastTransactionAt", c.lastTransactionAt)
            }
            customersArray.put(cObj)
        }
        root.put("customers", customersArray)

        // Sales
        val salesArray = JSONArray()
        for (s in backup.sales) {
            val sObj = JSONObject().apply {
                put("id", s.id)
                put("receiptNumber", s.receiptNumber)
                put("timestamp", s.timestamp)
                put("totalAmount", s.totalAmount)
                put("discountAmount", s.discountAmount)
                put("discountType", s.discountType)
                put("finalAmount", s.finalAmount)
                put("amountPaid", s.amountPaid)
                put("changeAmount", s.changeAmount)
                put("paymentMethod", s.paymentMethod)
                if (s.customerId != null) put("customerId", s.customerId)
                if (s.customerName != null) put("customerName", s.customerName)
                put("totalCost", s.totalCost)
                put("profit", s.profit)
                put("itemsSummary", s.itemsSummary)
                put("isVoided", s.isVoided)
            }
            salesArray.put(sObj)
        }
        root.put("sales", salesArray)

        // Debt Records
        val debtArray = JSONArray()
        for (d in backup.debtRecords) {
            val dObj = JSONObject().apply {
                put("id", d.id)
                put("customerId", d.customerId)
                put("amount", d.amount)
                put("type", d.type)
                put("notes", d.notes)
                put("balanceAfter", d.balanceAfter)
                put("date", d.date)
            }
            debtArray.put(dObj)
        }
        root.put("debtRecords", debtArray)

        // Users
        val usersArray = JSONArray()
        for (u in backup.users) {
            val uObj = JSONObject().apply {
                put("id", u.id)
                put("username", u.username)
                put("displayName", u.displayName)
                put("role", u.role)
                put("pin", u.pin)
                put("password", u.password)
                put("isActive", u.isActive)
                put("createdAt", u.createdAt)
            }
            usersArray.put(uObj)
        }
        root.put("users", usersArray)

        return root.toString(2)
    }

    fun createBackupFile(context: Context, backup: StoreBackupData): File {
        val backupDir = File(context.cacheDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(backupDir, "Tinda_Backup_$timestamp.json")

        file.writeText(backupToJsonString(backup))
        return file
    }

    fun parseBackupJson(jsonString: String): StoreBackupData {
        val root = JSONObject(jsonString)
        val exportDate = root.optLong("exportDate", System.currentTimeMillis())
        val storeName = root.optString("storeName", "Tinda Retail Store")
        val ownerName = root.optString("ownerName", "Store Manager")
        val storePhone = root.optString("storePhone", "0917-123-4567")
        val storeAddress = root.optString("storeAddress", "124 Main Street")
        val receiptMessage = root.optString("receiptMessage", "Thank you for your purchase! Please come again.")
        val startingCashDrawer = root.optDouble("startingCashDrawer", 500.0)

        // Parse Products
        val productsList = mutableListOf<Product>()
        val productsArray = root.optJSONArray("products")
        if (productsArray != null) {
            for (i in 0 until productsArray.length()) {
                val p = productsArray.getJSONObject(i)
                productsList.add(
                    Product(
                        id = p.optLong("id", 0L),
                        name = p.getString("name"),
                        category = p.optString("category", "General"),
                        barcode = p.optString("barcode", ""),
                        costPrice = p.optDouble("costPrice", 0.0),
                        sellingPrice = p.optDouble("sellingPrice", 0.0),
                        stockQuantity = p.optInt("stockQuantity", 0),
                        unit = p.optString("unit", "pcs"),
                        lowStockThreshold = p.optInt("lowStockThreshold", 5),
                        expiryDate = if (p.has("expiryDate")) p.getLong("expiryDate") else null,
                        lastUpdated = p.optLong("lastUpdated", System.currentTimeMillis())
                    )
                )
            }
        }

        // Parse Customers
        val customersList = mutableListOf<CustomerDebt>()
        val customersArray = root.optJSONArray("customers")
        if (customersArray != null) {
            for (i in 0 until customersArray.length()) {
                val c = customersArray.getJSONObject(i)
                customersList.add(
                    CustomerDebt(
                        id = c.optLong("id", 0L),
                        name = c.getString("name"),
                        phone = c.optString("phone", ""),
                        addressOrNote = c.optString("addressOrNote", c.optString("notes", "")),
                        totalDebt = c.optDouble("totalDebt", 0.0),
                        createdAt = c.optLong("createdAt", System.currentTimeMillis()),
                        lastTransactionAt = c.optLong("lastTransactionAt", System.currentTimeMillis())
                    )
                )
            }
        }

        // Parse Sales
        val salesList = mutableListOf<SaleTransaction>()
        val salesArray = root.optJSONArray("sales")
        if (salesArray != null) {
            for (i in 0 until salesArray.length()) {
                val s = salesArray.getJSONObject(i)
                salesList.add(
                    SaleTransaction(
                        id = s.optLong("id", 0L),
                        receiptNumber = s.getString("receiptNumber"),
                        timestamp = s.optLong("timestamp", System.currentTimeMillis()),
                        totalAmount = s.optDouble("totalAmount", 0.0),
                        discountAmount = s.optDouble("discountAmount", 0.0),
                        discountType = s.optString("discountType", "NONE"),
                        finalAmount = s.optDouble("finalAmount", 0.0),
                        amountPaid = s.optDouble("amountPaid", 0.0),
                        changeAmount = s.optDouble("changeAmount", 0.0),
                        paymentMethod = s.optString("paymentMethod", "CASH"),
                        customerId = if (s.has("customerId")) s.getLong("customerId") else null,
                        customerName = if (s.has("customerName")) s.getString("customerName") else null,
                        totalCost = s.optDouble("totalCost", 0.0),
                        profit = s.optDouble("profit", 0.0),
                        itemsSummary = s.optString("itemsSummary", s.optString("itemsJson", "")),
                        isVoided = s.optBoolean("isVoided", false)
                    )
                )
            }
        }

        // Parse Debt Records
        val debtList = mutableListOf<DebtRecord>()
        val debtArray = root.optJSONArray("debtRecords")
        if (debtArray != null) {
            for (i in 0 until debtArray.length()) {
                val d = debtArray.getJSONObject(i)
                debtList.add(
                    DebtRecord(
                        id = d.optLong("id", 0L),
                        customerId = d.getLong("customerId"),
                        amount = d.optDouble("amount", 0.0),
                        type = d.optString("type", "BORROW"),
                        notes = d.optString("notes", ""),
                        balanceAfter = d.optDouble("balanceAfter", 0.0),
                        date = d.optLong("date", System.currentTimeMillis())
                    )
                )
            }
        }

        // Parse Users
        val usersList = mutableListOf<UserAccount>()
        val usersArray = root.optJSONArray("users")
        if (usersArray != null) {
            for (i in 0 until usersArray.length()) {
                val u = usersArray.getJSONObject(i)
                usersList.add(
                    UserAccount(
                        id = u.optLong("id", 0L),
                        username = u.optString("username", "cashier"),
                        displayName = u.optString("displayName", "Staff"),
                        role = u.optString("role", "CASHIER"),
                        pin = u.optString("pin", "0000"),
                        password = u.optString("password", "cashier123"),
                        isActive = u.optBoolean("isActive", true),
                        createdAt = u.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        return StoreBackupData(
            exportDate = exportDate,
            storeName = storeName,
            ownerName = ownerName,
            storePhone = storePhone,
            storeAddress = storeAddress,
            receiptMessage = receiptMessage,
            startingCashDrawer = startingCashDrawer,
            products = productsList,
            customers = customersList,
            sales = salesList,
            debtRecords = debtList,
            users = usersList
        )
    }

    fun readJsonFromUri(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().readText()
        } ?: throw IllegalStateException("Unable to read backup file")
    }
}
