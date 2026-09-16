package com.example.util

import android.content.Context
import android.util.Log
import java.io.File

/**
 * PersistentSafetyVault ensures that store data (inventory, customer credit, sales records,
 * and store settings) is safely preserved across APK updates, reinstalls, and system migrations.
 *
 * Saves copies in both persistent external app storage and internal files dir.
 */
object PersistentSafetyVault {
    private const val TAG = "PersistentSafetyVault"
    private const val VAULT_FILE_NAME = "tinda_pos_safety_vault.json"

    private fun getVaultFiles(context: Context): List<File> {
        val files = mutableListOf<File>()
        try {
            val extDir = context.getExternalFilesDir(null)
            if (extDir != null) {
                files.add(File(extDir, VAULT_FILE_NAME))
            }
        } catch (e: Exception) {
            Log.w(TAG, "External dir unavailable: ${e.message}")
        }
        files.add(File(context.filesDir, VAULT_FILE_NAME))
        return files
    }

    /**
     * Saves a full store snapshot into persistent safety storage.
     */
    fun saveVault(context: Context, data: StoreBackupData) {
        try {
            val jsonString = JsonBackupHelper.backupToJsonString(data)
            for (file in getVaultFiles(context)) {
                try {
                    file.parentFile?.mkdirs()
                    file.writeText(jsonString)
                    Log.d(TAG, "Saved safety vault to ${file.absolutePath} (${file.length()} bytes)")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed writing to ${file.absolutePath}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating vault JSON: ${e.message}", e)
        }
    }

    /**
     * Checks if a valid persistent vault exists with data.
     */
    fun hasVault(context: Context): Boolean {
        for (file in getVaultFiles(context)) {
            if (file.exists() && file.length() > 50) {
                return true
            }
        }
        return false
    }

    /**
     * Loads the store data from the most recently modified vault file.
     */
    fun loadVault(context: Context): StoreBackupData? {
        val candidates = getVaultFiles(context)
            .filter { it.exists() && it.length() > 50 }
            .sortedByDescending { it.lastModified() }

        for (file in candidates) {
            try {
                val jsonString = file.readText()
                val data = JsonBackupHelper.parseBackupJson(jsonString)
                if (data.products.isNotEmpty() || data.sales.isNotEmpty() || data.customers.isNotEmpty()) {
                    Log.i(TAG, "Successfully loaded safety vault from ${file.absolutePath}")
                    return data
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed parsing vault from ${file.absolutePath}: ${e.message}")
            }
        }
        return null
    }

    /**
     * Returns a human readable summary of the latest vault if present.
     */
    fun getVaultSummary(context: Context): String? {
        val data = loadVault(context) ?: return null
        return "${data.products.size} Products, ${data.sales.size} Sales, ${data.customers.size} Credit Accounts"
    }
}
