# Troubleshooting & FAQ

## 1. Build Errors

### "Oboe library not found"
**Cause:** The git submodule was not initialized.
**Fix:** Run `git submodule update --init --recursive` in the project root.

### "CMake Error: CMAKE_C_COMPILER not found"
**Cause:** Android NDK is not installed or not linked in `local.properties`.
**Fix:** Ensure `ndk.dir` is set in your `local.properties` file or install the NDK via Android Studio SDK Manager.

## 2. Audio Issues

### "Audio is crackling or popping"
**Cause:** The CPU is being throttled or the buffer size is too small for the device.
**Fix:** 
- Ensure you are testing on a physical device, not an emulator.
- Check if `oboe::PerformanceMode::LowLatency` is successfully granted by the OS (check Logcat for Oboe logs).
- Close background apps that might be hogging CPU.

### "High latency (Delay between touch and sound)"
**Cause:** The device might not support AAudio or is falling back to OpenSL ES.
**Fix:** Baby Scratch requires `minSdk 26` to use AAudio. Ensure your test device is running Android 8.0 or higher.

## 3. Microphone Issues

### "Recording doesn't work"
**Cause:** Permission denied or `AudioRecord` failed to initialize.
**Fix:**
- Check `Settings > Apps > Baby Scratch > Permissions` to ensure Microphone is allowed.
- Ensure no other app (like a voice assistant) is holding the Microphone resource.

## 4. Performance

### "The record looks laggy"
**Cause:** The `TurntableView` is doing too much work in `onDraw`.
**Fix:** Ensure you are not allocating objects (like `Paint` or `Rect`) inside the `onDraw` method. Use pre-allocated member variables.
