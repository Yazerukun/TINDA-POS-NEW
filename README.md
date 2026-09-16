# TINDA POS (Point of Sale & Inventory Management)

A modern, offline-first Android Point of Sale (POS) and inventory management system designed for neighborhood sari-sari stores, retail shops, and small businesses.

## How to Get / Download the APK

### 1. Directly from GitHub Releases (Recommended)
1. In your GitHub repository, click on the **Releases** tab on the right sidebar (or navigate to `https://github.com/<your-username>/<repo-name>/releases`).
2. Under the latest release (**Tinda POS v1.0.0**), go to the **Assets** section.
3. Download **`Tinda-POS-v1.0.0.apk`** (or `Tinda-POS-Latest.apk`).
4. Install directly on any Android phone or tablet.

### 2. Export Directly from AI Studio
1. Open the top navigation menu in Google AI Studio.
2. Click **Export** or **Download APK**.

### 3. Build Locally with Gradle
```bash
# Restore debug keystore
base64 -d debug.keystore.base64 > debug.keystore

# Build APK
gradle assembleDebug
```
The output will be generated under `app/build/outputs/apk/debug/app-debug.apk`.
