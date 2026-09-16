# TINDA POS (Point of Sale & Inventory Management)

A modern, offline-first Android Point of Sale (POS) and inventory management system designed for neighborhood sari-sari stores, retail shops, and small businesses.

## How to Get / Download the APK

### 1. Directly in Google AI Studio (Fastest)
1. Open the **Settings / Export** menu in the top navigation of AI Studio.
2. Select **Download APK** (or **Export APK / AAB**).
3. The compiled `.apk` will download directly to your computer or phone.

### 2. From GitHub Actions (Automated Builds)
Whenever code is pushed to this GitHub repository:
1. Go to the **Actions** tab in your GitHub repository.
2. Click on the latest run under **Build Android APK**.
3. Under the **Artifacts** section at the bottom, download **`TindaPOS-Debug-APK`**.
4. Extract the zip file to find `app-debug.apk` ready to install on your Android device.

### 3. Build Locally with Gradle
If you have cloned this repository locally:
```bash
# Restore debug keystore
base64 -d debug.keystore.base64 > debug.keystore

# Build Debug APK
gradle assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`
