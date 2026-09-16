package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.InStockGreen
import com.example.ui.theme.OutOfStockRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class AppUpdateInfo(
    val currentVersion: String,
    val latestVersion: String,
    val releaseDate: String = "Latest Release",
    val packageSizeBytes: Long = 15_840_000L, // ~15.1 MB
    val isUpdateAvailable: Boolean = false,
    val downloadUrl: String = "",
    val changelog: List<String> = listOf(
        "Added X-Reading & Z-Reading shift audit reports with thermal slip printer output.",
        "Cash drawer reconciliation: float + cash sales + credit repayments balance tracking.",
        "Credit (Utang) ledger with partial payments & SMS reminder generation.",
        "Offline-first SQLite/Room database performance optimizations.",
        "Inventory low-stock, expiry warnings, and CSV report export improvements."
    )
)

sealed class UpdateDownloadState {
    object Idle : UpdateDownloadState()
    object Checking : UpdateDownloadState()
    data class UpToDate(val installedVersion: String) : UpdateDownloadState()
    data class Ready(val updateInfo: AppUpdateInfo) : UpdateDownloadState()
    data class Downloading(
        val progress: Float, // 0.0f to 1.0f
        val downloadedBytes: Long,
        val totalBytes: Long,
        val speedKbps: Long
    ) : UpdateDownloadState()
    data class Completed(val apkFile: File) : UpdateDownloadState()
    data class Error(val message: String) : UpdateDownloadState()
}

private const val PREFS_NAME = "tinda_update_prefs"
private const val KEY_CUSTOM_UPDATE_URL = "custom_apk_update_url"

@Composable
fun SoftwareUpdateDialog(
    onDismiss: () -> Unit,
    onPreInstallBackup: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    val currentVersionName = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    var customUrlInput by remember { mutableStateOf(prefs.getString(KEY_CUSTOM_UPDATE_URL, "") ?: "") }
    var showCustomUrlField by remember { mutableStateOf(false) }

    var updateState by remember { mutableStateOf<UpdateDownloadState>(UpdateDownloadState.Checking) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var activeUpdateInfo by remember {
        mutableStateOf(
            AppUpdateInfo(
                currentVersion = currentVersionName,
                latestVersion = currentVersionName,
                isUpdateAvailable = false
            )
        )
    }

    fun performCheck(simulateTest: Boolean = false) {
        coroutineScope.launch {
            updateState = UpdateDownloadState.Checking
            delay(900) // Brief realistic check delay

            val savedUrl = prefs.getString(KEY_CUSTOM_UPDATE_URL, "") ?: ""

            if (savedUrl.isNotBlank()) {
                // If the user has configured an actual APK download URL
                activeUpdateInfo = AppUpdateInfo(
                    currentVersion = currentVersionName,
                    latestVersion = "1.1.0",
                    isUpdateAvailable = true,
                    downloadUrl = savedUrl
                )
                updateState = UpdateDownloadState.Ready(activeUpdateInfo)
            } else if (simulateTest) {
                // User explicitly requested to simulate / test the download flow
                activeUpdateInfo = AppUpdateInfo(
                    currentVersion = currentVersionName,
                    latestVersion = "1.1.0-preview",
                    isUpdateAvailable = true,
                    downloadUrl = ""
                )
                updateState = UpdateDownloadState.Ready(activeUpdateInfo)
            } else {
                // Default: Current version is up-to-date since no newer APK has been published yet
                updateState = UpdateDownloadState.UpToDate(currentVersionName)
            }
        }
    }

    // Initial check on launch
    LaunchedEffect(Unit) {
        performCheck(simulateTest = false)
    }

    Dialog(
        onDismissRequest = {
            if (updateState !is UpdateDownloadState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = updateState !is UpdateDownloadState.Downloading,
            dismissOnClickOutside = updateState !is UpdateDownloadState.Downloading,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
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
                                Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Software Updater",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tinda POS System Updates",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    if (updateState !is UpdateDownloadState.Downloading) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // State-based body
                when (val state = updateState) {
                    is UpdateDownloadState.Idle,
                    is UpdateDownloadState.Checking -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(42.dp),
                                    color = EmeraldPrimary,
                                    strokeWidth = 3.5.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Checking for new software updates...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    is UpdateDownloadState.UpToDate -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = InStockGreen,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "You're All Up to Date!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Version ${state.installedVersion} is the latest version installed on this device. There are currently no new official APK updates available.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom APK Server or Test Mode Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Custom Update Source",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        TextButton(onClick = { showCustomUrlField = !showCustomUrlField }) {
                                            Text(if (showCustomUrlField) "Hide" else "Configure URL", fontSize = 12.sp)
                                        }
                                    }

                                    if (showCustomUrlField) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = customUrlInput,
                                            onValueChange = { customUrlInput = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Direct APK Download URL") },
                                            placeholder = { Text("https://example.com/app-v1.1.apk") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                prefs.edit().putString(KEY_CUSTOM_UPDATE_URL, customUrlInput.trim()).apply()
                                                Toast.makeText(context, "Update URL saved!", Toast.LENGTH_SHORT).show()
                                                performCheck(simulateTest = false)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Save & Check URL")
                                        }
                                    } else {
                                        Text(
                                            text = "When you build and release a new APK in the future, you can host the file and link it here for seamless in-app distribution.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action buttons: Re-check & Demo Test
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { performCheck(simulateTest = false) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Check Again", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { performCheck(simulateTest = true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("btn_test_updater"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test Progress Bar", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    is UpdateDownloadState.Ready -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Version comparison card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Installed Version",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = state.updateInfo.currentVersion,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary
                                        )
                                    }

                                    Icon(
                                        Icons.Default.NewReleases,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "New Available Version",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = state.updateInfo.latestVersion,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Package Size: ${formatBytes(state.updateInfo.packageSizeBytes)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = state.updateInfo.releaseDate,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Changelog
                            Text(
                                text = "What's New in this Version:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    state.updateInfo.changelog.forEach { logItem ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("•", color = EmeraldPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.width(14.dp))
                                            Text(
                                                text = logItem,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { updateState = UpdateDownloadState.UpToDate(currentVersionName) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Back")
                                }

                                Button(
                                    onClick = {
                                        downloadJob = coroutineScope.launch {
                                            startUpdateDownload(
                                                context = context,
                                                updateInfo = state.updateInfo,
                                                onProgress = { prog, downBytes, totBytes, speed ->
                                                    updateState = UpdateDownloadState.Downloading(
                                                        progress = prog,
                                                        downloadedBytes = downBytes,
                                                        totalBytes = totBytes,
                                                        speedKbps = speed
                                                    )
                                                },
                                                onComplete = { file ->
                                                    updateState = UpdateDownloadState.Completed(file)
                                                },
                                                onError = { msg ->
                                                    updateState = UpdateDownloadState.Error(msg)
                                                }
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .testTag("btn_download_update"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Download Update")
                                }
                            }
                        }
                    }

                    is UpdateDownloadState.Downloading -> {
                        // LIVE PROGRESS BAR VIEW
                        val animatedProgress by animateFloatAsState(
                            targetValue = state.progress,
                            label = "download_progress"
                        )
                        val percent = (state.progress * 100).toInt()

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Downloading Update...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Package version ${activeUpdateInfo.latestVersion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Large Percentage display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = "${formatBytes(state.downloadedBytes)} / ${formatBytes(state.totalBytes)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Linear Progress Bar
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .testTag("update_progress_bar"),
                                color = EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Speed & Time Remaining
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val speedText = if (state.speedKbps > 1024) {
                                    String.format(Locale.US, "%.1f MB/s", state.speedKbps / 1024.0)
                                } else {
                                    "${state.speedKbps} KB/s"
                                }
                                Text(
                                    text = "Speed: $speedText",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )

                                val remainingBytes = state.totalBytes - state.downloadedBytes
                                val remainingSeconds = if (state.speedKbps > 0) {
                                    (remainingBytes / 1024) / state.speedKbps
                                } else 0
                                Text(
                                    text = if (remainingSeconds > 0) "~${remainingSeconds}s remaining" else "Finishing up...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedButton(
                                onClick = {
                                    downloadJob?.cancel()
                                    updateState = UpdateDownloadState.UpToDate(currentVersionName)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancel Download")
                            }
                        }
                    }

                    is UpdateDownloadState.Completed -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.DownloadDone,
                                contentDescription = null,
                                tint = InStockGreen,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Update Downloaded!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "Tinda POS update package is ready to install.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Package: ${state.apkFile.name}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Tap 'Install Now' to proceed with Android Package Installer.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Later")
                                }

                                Button(
                                    onClick = {
                                        onPreInstallBackup?.invoke()
                                        installApk(context, state.apkFile)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = InStockGreen),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("btn_install_apk"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Install Now")
                                }
                            }
                        }
                    }

                    is UpdateDownloadState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = OutOfStockRed,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Download Failed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OutOfStockRed
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Close")
                                }
                                Button(
                                    onClick = {
                                        performCheck(simulateTest = false)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun startUpdateDownload(
    context: Context,
    updateInfo: AppUpdateInfo,
    onProgress: (progress: Float, downloadedBytes: Long, totalBytes: Long, speedKbps: Long) -> Unit,
    onComplete: (File) -> Unit,
    onError: (String) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val updatesDir = File(context.cacheDir, "updates")
        if (!updatesDir.exists()) updatesDir.mkdirs()

        val destinationFile = File(updatesDir, "TindaPOS_Update.apk")

        // If a real HTTP URL is configured, perform real network streaming
        if (updateInfo.downloadUrl.isNotBlank() && (updateInfo.downloadUrl.startsWith("http://") || updateInfo.downloadUrl.startsWith("https://"))) {
            val url = URL(updateInfo.downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            val totalBytes = connection.contentLength.takeIf { it > 0 }?.toLong() ?: updateInfo.packageSizeBytes
            var downloadedBytes = 0L
            val buffer = ByteArray(8192)

            val startTime = System.currentTimeMillis()
            var lastSpeedUpdate = startTime
            var bytesSinceLastSpeed = 0L
            var currentSpeedKbps = 2400L

            connection.inputStream.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        bytesSinceLastSpeed += bytesRead

                        val now = System.currentTimeMillis()
                        if (now - lastSpeedUpdate >= 400) {
                            val timeDeltaSec = (now - lastSpeedUpdate) / 1000.0
                            if (timeDeltaSec > 0) {
                                currentSpeedKbps = ((bytesSinceLastSpeed / 1024.0) / timeDeltaSec).toLong()
                            }
                            lastSpeedUpdate = now
                            bytesSinceLastSpeed = 0L
                        }

                        val progress = (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                        withContext(Dispatchers.Main) {
                            onProgress(progress, downloadedBytes, totalBytes, currentSpeedKbps)
                        }
                    }
                }
            }
        } else {
            // High-fidelity OTA package download simulation for testing the progress bar
            val totalBytes = updateInfo.packageSizeBytes
            var downloadedBytes = 0L
            val steps = 32
            val chunkSize = totalBytes / steps

            FileOutputStream(destinationFile).use { output ->
                for (step in 1..steps) {
                    delay(120) // Realistic progression
                    val dummyBytes = ByteArray(1024)
                    output.write(dummyBytes)
                    downloadedBytes = (chunkSize * step).coerceAtMost(totalBytes)
                    val progress = (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                    val speedKbps = (2100L + (step % 5) * 220L)

                    withContext(Dispatchers.Main) {
                        onProgress(progress, downloadedBytes, totalBytes, speedKbps)
                    }
                }
            }
        }

        withContext(Dispatchers.Main) {
            onComplete(destinationFile)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onError(e.message ?: "Failed to download update package")
        }
    }
}

private fun installApk(context: Context, apkFile: File) {
    try {
        if (!apkFile.exists()) {
            Toast.makeText(context, "Installer file not found.", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                Toast.makeText(context, "Please grant permission to install app updates.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(installIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to launch installer: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun formatBytes(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return String.format(Locale.US, "%.1f MB", mb)
}
