# Baby Scratch - Android NDK Audio App

A high-performance, low-latency DJ scratch simulator built with Android NDK, Oboe, and Jetpack Compose.

> **Note on Web Preview:** Because this is a native Android application (Kotlin/C++), it cannot be run directly in the web browser preview. The web preview instead displays a React-based landing page that outlines the project structure and compilation instructions.

## 🛠 Compilation Guide

Because this project relies on C++ (NDK) and CMake, it requires a specific build environment. Below are instructions for compiling in cloud/mobile environments.

### Option A: GitHub Codespaces (Recommended)

GitHub Codespaces provides a full Ubuntu environment. We will use a Docker container pre-configured with the Android SDK and NDK.

**1. Setup the Devcontainer**
Create a file at `.devcontainer/devcontainer.json` in your repository:
```json
{
    "name": "Android NDK",
    "image": "mcr.microsoft.com/devcontainers/java:17",
    "features": {
        "ghcr.io/devcontainers/features/android:1": {}
    },
    "postCreateCommand": "yes | sdkmanager 'ndk;25.1.8937393' 'cmake;3.22.1'"
}
```

**2. Build the App**
1. Open the repository in GitHub Codespaces.
2. Wait for the container to build (it will automatically download the Android SDK, NDK, and CMake).
3. Ensure the Oboe submodule is loaded:
   `git submodule update --init --recursive`
4. Build the APK:
   `./gradlew assembleDebug`
5. The compiled APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`. You can right-click and download it to your physical device.

---

### Option B: Termux (On-Device Compilation)

Compiling an NDK project directly on an Android device via Termux requires setting up a Linux `proot` environment, as the native Termux environment lacks the standard glibc required by the official Android SDK/NDK binaries.

**1. Install Ubuntu in Termux**
Open Termux and run:
```bash
pkg update && pkg upgrade -y
pkg install proot-distro wget git unzip -y
proot-distro install ubuntu
proot-distro login ubuntu
```

**2. Install Dependencies (Inside Ubuntu PRoot)**
```bash
apt update && apt upgrade -y
apt install openjdk-17-jdk wget unzip git build-essential -y
```

**3. Install Android SDK & NDK**
```bash
# Setup directories
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools

# Download Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-10406996_latest.zip
unzip commandlinetools-linux-*_latest.zip
mv cmdline-tools latest

# Set Environment Variables (Add these to ~/.bashrc for future use)
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses and install NDK, CMake, and Build Tools
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "ndk;25.1.8937393" "cmake;3.22.1"
```

**4. Clone and Build**
```bash
cd ~
git clone <your-repo-url> babyscratch
cd babyscratch
git submodule update --init --recursive

# Build the APK
./gradlew assembleDebug`
```

**5. File Storage Recommendations (Termux)**
To access the compiled APK outside of Termux (so you can install it):
1. Exit the Ubuntu proot: `exit`
2. Grant Termux storage access: `termux-setup-storage`
3. Copy the APK to your phone's Downloads folder:
   `cp .proot-distro/ubuntu/root/babyscratch/app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/`
4. Open your phone's File Manager, navigate to Downloads, and tap the APK to install.
