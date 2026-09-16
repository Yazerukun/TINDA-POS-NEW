package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Product::class,
        CustomerDebt::class,
        DebtRecord::class,
        SaleTransaction::class,
        StockMovement::class,
        ZReadReport::class,
        UserAccount::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun debtRecordDao(): DebtRecordDao
    abstract fun saleDao(): SaleDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun zReadReportDao(): ZReadReportDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Version 1 to 2 migration
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `z_reads` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `zReadNumber` TEXT NOT NULL,
                        `periodStart` INTEGER NOT NULL,
                        `periodEnd` INTEGER NOT NULL,
                        `generatedAt` INTEGER NOT NULL,
                        `openingFloat` REAL NOT NULL,
                        `grossSales` REAL NOT NULL,
                        `netSales` REAL NOT NULL,
                        `totalProfit` REAL NOT NULL,
                        `transactionCount` INTEGER NOT NULL,
                        `cashSales` REAL NOT NULL,
                        `gcashSales` REAL NOT NULL,
                        `creditSales` REAL NOT NULL,
                        `customerDebtPaymentsCollected` REAL NOT NULL,
                        `totalDiscounts` REAL NOT NULL,
                        `expectedCashInDrawer` REAL NOT NULL,
                        `actualCashCounted` REAL,
                        `cashShortageOver` REAL,
                        `firstReceiptNumber` TEXT NOT NULL,
                        `lastReceiptNumber` TEXT NOT NULL,
                        `voidedCount` INTEGER NOT NULL,
                        `voidedTotal` REAL NOT NULL,
                        `notes` TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_accounts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `username` TEXT NOT NULL,
                        `displayName` TEXT NOT NULL,
                        `role` TEXT NOT NULL,
                        `pin` TEXT NOT NULL,
                        `password` TEXT NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tinda_pos_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigrationOnDowngrade(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
