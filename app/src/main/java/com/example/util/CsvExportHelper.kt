package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.Product
import com.example.data.SaleTransaction
import com.example.data.ZReadReport
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExportHelper {

    fun exportInventoryToCsv(context: Context, products: List<Product>): File {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(exportDir, "Tinda_Inventory_$timestamp.csv")

        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        file.bufferedWriter().use { writer ->
            // Header
            writer.write("ID,Name,Category,Barcode,Stock Quantity,Unit,Cost Price (PHP),Selling Price (PHP),Potential Profit (PHP),Low Stock Threshold,Expiry Date\n")
            for (p in products) {
                val expiryStr = if (p.expiryDate != null && p.expiryDate > 0) dateFmt.format(Date(p.expiryDate)) else "None"
                val escapedName = escapeCsv(p.name)
                val escapedCategory = escapeCsv(p.category)
                val escapedBarcode = escapeCsv(p.barcode)
                val escapedUnit = escapeCsv(p.unit)
                val profitPerUnit = p.sellingPrice - p.costPrice
                writer.write("${p.id},$escapedName,$escapedCategory,$escapedBarcode,${p.stockQuantity},$escapedUnit,${String.format(Locale.US, "%.2f", p.costPrice)},${String.format(Locale.US, "%.2f", p.sellingPrice)},${String.format(Locale.US, "%.2f", profitPerUnit)},${p.lowStockThreshold},$expiryStr\n")
            }
        }
        return file
    }

    fun exportSalesReportToCsv(context: Context, periodName: String, sales: List<SaleTransaction>): File {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val sanitizedPeriod = periodName.replace(" ", "_")
        val file = File(exportDir, "Tinda_Sales_${sanitizedPeriod}_$timestamp.csv")

        val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        file.bufferedWriter().use { writer ->
            // Header
            writer.write("Receipt #,Date & Time,Items Breakdown,Payment Method,Customer Name,Subtotal (PHP),Discount Amount (PHP),Discount Type,Final Total (PHP),Amount Paid (PHP),Change (PHP),Status\n")
            for (s in sales) {
                val dateStr = dateFmt.format(Date(s.timestamp))
                val itemsEscaped = escapeCsv(s.itemsSummary)
                val customerEscaped = escapeCsv(s.customerName ?: "Walk-in")
                val statusStr = if (s.isVoided) "VOIDED" else "COMPLETED"
                writer.write("${s.receiptNumber},$dateStr,$itemsEscaped,${s.paymentMethod},$customerEscaped,${String.format(Locale.US, "%.2f", s.totalAmount)},${String.format(Locale.US, "%.2f", s.discountAmount)},${s.discountType},${String.format(Locale.US, "%.2f", s.finalAmount)},${String.format(Locale.US, "%.2f", s.amountPaid)},${String.format(Locale.US, "%.2f", s.changeAmount)},$statusStr\n")
            }
        }
        return file
    }

    fun exportZReadReportsToCsv(context: Context, zReports: List<ZReadReport>): File {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(exportDir, "Tinda_ZRead_History_$timestamp.csv")

        val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        file.bufferedWriter().use { writer ->
            writer.write("Z-Read #,Generated At,Shift Start,Shift End,Opening Float (PHP),Gross Sales (PHP),Discounts (PHP),Net Sales (PHP),Profit (PHP),Transactions,Cash Sales (PHP),GCash Sales (PHP),Credit Sales (PHP),Debt Collected (PHP),Expected Cash (PHP),Actual Cash Count (PHP),Shortage/Over (PHP),First Receipt,Last Receipt,Notes\n")
            for (z in zReports) {
                val genAt = dateFmt.format(Date(z.generatedAt))
                val pStart = dateFmt.format(Date(z.periodStart))
                val pEnd = dateFmt.format(Date(z.periodEnd))
                val actCashStr = if (z.actualCashCounted != null) String.format(Locale.US, "%.2f", z.actualCashCounted) else "N/A"
                val varStr = if (z.cashShortageOver != null) String.format(Locale.US, "%.2f", z.cashShortageOver) else "0.00"
                val notesEscaped = escapeCsv(z.notes)
                writer.write("${z.zReadNumber},$genAt,$pStart,$pEnd,${String.format(Locale.US, "%.2f", z.openingFloat)},${String.format(Locale.US, "%.2f", z.grossSales)},${String.format(Locale.US, "%.2f", z.totalDiscounts)},${String.format(Locale.US, "%.2f", z.netSales)},${String.format(Locale.US, "%.2f", z.totalProfit)},${z.transactionCount},${String.format(Locale.US, "%.2f", z.cashSales)},${String.format(Locale.US, "%.2f", z.gcashSales)},${String.format(Locale.US, "%.2f", z.creditSales)},${String.format(Locale.US, "%.2f", z.customerDebtPaymentsCollected)},${String.format(Locale.US, "%.2f", z.expectedCashInDrawer)},$actCashStr,$varStr,${z.firstReceiptNumber},${z.lastReceiptNumber},$notesEscaped\n")
            }
        }
        return file
    }

    fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, chooserTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
