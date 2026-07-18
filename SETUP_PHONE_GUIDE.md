# 📱 Study-Planner: Complete Setup Guide for Android Phone

A comprehensive guide to build, transfer, and install the Study-Planner Kotlin application on your Android phone using USB and terminal commands.

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Part 1: Setup on PC with VS Code](#part-1-setup-on-pc-with-vs-code)
3. [Part 2: Build the APK](#part-2-build-the-apk)
4. [Part 3: Enable USB Debugging on Phone](#part-3-enable-usb-debugging-on-phone)
5. [Part 4: Transfer APK to Phone](#part-4-transfer-apk-to-phone)
6. [Part 5: Install APK on Phone](#part-5-install-apk-on-phone)
7. [Part 6: Verify Installation](#part-6-verify-installation)
8. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Software (Install on PC)

- **Android Studio** (includes Android SDK, emulator, and build tools)
- **VS Code** (for code editing)
- **Java Development Kit (JDK)** - Required for building Android apps
- **Android SDK** - Included with Android Studio
- **Git** (optional, for version control)

### Android Phone Requirements

- **OS:** Android 5.0 or higher
- **USB Cable:** Standard USB cable to connect phone to PC
- **Storage:** Minimum 100MB free space
- **USB Debugging:** Enabled (explained in Part 3)

---

## 🚀 Part 1: Setup on PC with VS Code

### Step 1.1: Install Android Studio

1. Download from: **https://developer.android.com/studio**
2. Install Android Studio with default settings
3. Launch Android Studio and complete the setup wizard
4. During setup, allow it to install the Android SDK

### Step 1.2: Install VS Code Extensions for Kotlin

1. Open **VS Code**
2. Go to **Extensions** (Ctrl+Shift+X or Cmd+Shift+X)
3. Search and install:
   - **Kotlin Language** (by fwcd)
   - **Android Tools** (by Kasper Bolvig)
   - **Gradle Extension Pack** (by Microsoft)
   - **GitHub Copilot** (optional, for AI assistance)

### Step 1.3: Open Your Study-Planner Project

```bash
# Clone the repository (if not already done)
git clone https://github.com/Marnold250/Study-planner.git
cd Study-planner

# Open in VS Code
code .
```

### Step 1.4: Verify Project Structure

Your project should look like this:

```
Study-planner/
├── app/                          # Main app code
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/          # Kotlin source files
│   │   │   ├── res/             # Resources (layouts, strings, etc.)
│   │   │   └── AndroidManifest.xml
│   │   └── androidTest/
│   └── build.gradle
├── gradle/                       # Gradle wrapper
├── build.gradle                  # Root gradle configuration
├── settings.gradle
├── gradlew                       # Gradle wrapper (Linux/Mac)
├── gradlew.bat                   # Gradle wrapper (Windows)
└── README.md
```

---

## 🔨 Part 2: Build the APK

### Step 2.1: Open Terminal in VS Code

1. In VS Code, press **Ctrl + `** (backtick) to open the integrated terminal
2. Or go to **View → Terminal**

### Step 2.2: Build Debug APK (Recommended for Testing)

For testing purposes, build a debug APK:

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

**Output location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

### Step 2.3: Build Release APK (Optional - for Production)

For a release build:

```bash
# Windows
gradlew.bat assembleRelease

# Linux/Mac
./gradlew assembleRelease
```

**Output location:**
```
app/build/outputs/apk/release/app-release.apk
```

**Note:** Debug APK is recommended for initial testing. Release APK requires signing, which is more complex.

### Step 2.4: Wait for Build to Complete

The build process may take 2-5 minutes. You'll see:
```
BUILD SUCCESSFUL
```

---

## 📱 Part 3: Enable USB Debugging on Phone

### Step 3.1: Access Developer Options

**On Android 10 and above:**

1. Open **Settings** on your phone
2. Go to **About Phone**
3. Scroll down to **Build Number**
4. Tap **Build Number** 7 times repeatedly
5. You'll see a message: *"You are now a developer!"*

**On Android 9 and below:**

1. Open **Settings**
2. Go to **Developer Options** (or System → Developer Options)
3. If you don't see it, follow steps 1-4 above

### Step 3.2: Enable USB Debugging

1. Go back to **Settings**
2. Navigate to **Developer Options** (now visible after tapping Build Number)
3. Scroll down and find **USB Debugging**
4. Toggle it **ON** (it will turn blue/green)
5. A dialog may appear asking to allow USB debugging - tap **Allow** or **OK**

### Step 3.3: Enable USB File Transfer (Optional)

1. Go to **Settings → Storage** or **Settings → About Phone**
2. Look for **USB Connection** option
3. Select **File Transfer** or **MTP** (Media Transfer Protocol)

---

## 🔗 Part 4: Transfer APK to Phone

### Step 4.1: Connect Phone to PC via USB

1. Use a USB cable to connect your phone to your PC
2. On your phone, you'll see a notification asking about USB connection
3. Tap the notification and select **File Transfer** or **MTP**
4. The phone will appear as a storage device on your PC

### Step 4.2: Check Connection with ADB

ADB (Android Debug Bridge) comes with Android Studio. In VS Code terminal, run:

```bash
# Windows
adb devices

# Linux/Mac
./adb devices
```

**Expected output:**
```
List of attached devices
emulator-5554          device
```

If you see `device`, your phone is properly connected!

### Step 4.3: Transfer APK Using ADB (Automatic)

This is the easiest method:

```bash
# Windows
adb install app\build\outputs\apk\debug\app-debug.apk

# Linux/Mac
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Skip to Part 5 if the transfer was successful.**

---

### Alternative: Manual File Transfer

If ADB installation fails, transfer the APK manually:

1. **Open File Explorer** on your PC
2. **Locate the APK:** `app/build/outputs/apk/debug/app-debug.apk`
3. **Right-click** and select **Copy**
4. **Open your phone's storage** in File Explorer (usually shows as a removable drive)
5. **Navigate to:** `Phone → Download` folder (or any accessible folder)
6. **Right-click** and select **Paste**
7. Wait for the file to transfer completely

---

## 📥 Part 5: Install APK on Phone

### Option A: Install via ADB (Automatic - Recommended)

Run this command in your terminal:

```bash
# Windows
adb install app\build\outputs\apk\debug\app-debug.apk

# Linux/Mac
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Expected output:**
```
Success
```

If successful, the app will be installed automatically!

### Option B: Install Manually from Phone

If you transferred the APK manually:

1. **Disconnect USB** from your PC
2. On your phone, open **File Manager** or **Files** app
3. Navigate to where you transferred the APK (usually **Downloads** folder)
4. **Long-press** on the `app-debug.apk` file
5. Select **Open** or **Install**
6. A dialog appears asking for permission - tap **Install**
7. Wait for installation to complete
8. You'll see **"App installed successfully"**

---

## ✅ Part 6: Verify Installation

### Step 6.1: Check on Phone

1. Go to **Settings → Apps** or **App Manager**
2. Look for **"Study-Planner"** or the app name
3. If it appears in the list, the installation was successful!

### Step 6.2: Launch the App

1. Go to your phone's **Home Screen**
2. Look for the **Study-Planner app icon**
3. Tap to open the app
4. If it opens without errors, congratulations! 🎉

### Step 6.3: Check via Terminal (Optional)

```bash
# List installed packages
adb shell pm list packages | grep study

# Or get package info
adb shell dumpsys package com.yourcompany.studyplanner
```

---

## 📦 Part 7: Installing Additional Packages

If your project uses external libraries or packages, they're automatically downloaded during the build process. However, if you need to add new packages:

### Adding Dependencies in Android Studio

1. Open **build.gradle** file in the `app` folder
2. In the `dependencies` block, add new packages:

```gradle
dependencies {
    // Existing dependencies
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Add new packages like this:
    implementation 'com.google.code.gson:gson:2.10.1'  // Example: JSON parsing
}
```

3. Click **"Sync Now"** button that appears at the top
4. Wait for Gradle to download and sync
5. Rebuild the APK: `gradlew build`
6. Reinstall on phone using the steps above

---

## 🐛 Troubleshooting

### Issue 1: "Command 'adb' not found"

**Solution:**
- ADB is installed with Android Studio but may not be in PATH
- Use full path: 
  ```bash
  # Windows
  "C:\Users\YourUsername\AppData\Local\Android\Sdk\platform-tools\adb" devices
  
  # Linux/Mac
  ~/Android/Sdk/platform-tools/adb devices
  ```

### Issue 2: Phone not detected by ADB

**Solution:**
1. Check if USB Debugging is **enabled** (Part 3)
2. Disconnect and reconnect the USB cable
3. Select **File Transfer** mode on phone
4. On phone, you may see a dialog asking to allow USB debugging - tap **Allow**
5. Run: `adb devices` again

### Issue 3: "Installation failed" or "App not installed"

**Solution:**
- The APK may be corrupted. Rebuild it:
  ```bash
  # Clean previous build
  gradlew clean
  # Rebuild
  gradlew build
  ```
- Try installing again with: `adb install app/build/outputs/apk/debug/app-debug.apk`

### Issue 4: Build fails with "Build tools not found"

**Solution:**
1. Open Android Studio
2. Go to **File → Project Structure → SDK Location**
3. Ensure Android SDK is correctly installed
4. Download missing build tools:
   - **Tools → SDK Manager**
   - Install required SDK Platform and Build Tools

### Issue 5: Gradle sync fails

**Solution:**
1. Delete the `.gradle` folder:
   ```bash
   # Windows
   rmdir /s .gradle
   
   # Linux/Mac
   rm -rf .gradle
   ```
2. Resync: Click **"Sync Now"** in Android Studio

### Issue 6: Phone shows "Unknown App" warning

**Solution:**
- This is normal for debug APKs
- Tap **Install anyway** or **Allow installation from unknown sources**
- Go to **Settings → Security → Unknown Sources** and enable it

### Issue 7: APK file not found after build

**Solution:**
- Ensure build was successful (you saw "BUILD SUCCESSFUL")
- Check output path:
  ```bash
  # Windows
  dir app\build\outputs\apk\debug\
  
  # Linux/Mac
  ls -la app/build/outputs/apk/debug/
  ```

---

## 📞 Quick Command Reference

```bash
# Clone repository
git clone https://github.com/Marnold250/Study-planner.git

# Navigate to project
cd Study-planner

# Clean previous builds
gradlew clean

# Build debug APK
gradlew build

# Check phone connection
adb devices

# Install APK on connected phone
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall app from phone
adb uninstall com.yourcompany.studyplanner

# Open phone shell (for advanced debugging)
adb shell
```

---

## 🎯 Summary Checklist

- [ ] Android Studio installed
- [ ] VS Code with Kotlin extensions installed
- [ ] Project cloned and opened in VS Code
- [ ] USB Debugging enabled on phone
- [ ] Phone connected via USB cable
- [ ] APK successfully built
- [ ] APK installed on phone via ADB
- [ ] App opens and runs without errors

---

## 📚 Useful Links

- **Android Studio:** https://developer.android.com/studio
- **Kotlin Documentation:** https://kotlinlang.org/docs/
- **Android Developer Docs:** https://developer.android.com/docs
- **ADB Command Reference:** https://developer.android.com/studio/command-line/adb

---

## 💡 Pro Tips

1. **Use Debug APK for Testing:** Always use debug APK during development. It's unsigned and installs faster.
2. **Keep USB Connection:** During development, keep your phone connected via USB to quickly test changes.
3. **Use Logcat:** In Android Studio, view real-time logs:
   ```bash
   adb logcat
   ```
4. **Wireless Debugging:** After initial USB setup, you can use wireless debugging:
   ```bash
   adb tcpip 5555
   adb connect <your-phone-ip>:5555
   ```

---

## ❓ Need Help?

If you encounter issues not covered here:

1. Check the **Android Studio Logcat** for error messages
2. Visit **Stack Overflow** with the error message
3. Check **Android Developer Documentation**
4. Post an issue on the GitHub repository: https://github.com/Marnold250/Study-planner/issues

---

**Last Updated:** July 2026  
**Android Version:** 5.0+  
**Kotlin Version:** 1.x+

Happy coding! 🚀