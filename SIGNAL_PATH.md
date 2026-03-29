# Signal Path & Component Map

## 1. Control Signal Path (User Input ➔ Sound)
This path describes how a physical touch on the screen changes the sound in real-time.

1.  **`TurntableView.kt` (UI Layer)**: 
    *   Captures `MotionEvent`.
    *   Calculates `deltaAngle` (change in rotation).
    *   Converts `deltaAngle` to `velocity`.
    *   Triggers `onVelocityChanged(velocity)` callback.
2.  **`MainScreen.kt` (Compose Layer)**:
    *   Receives callback from the `AndroidView` wrapper.
    *   Calls `viewModel.setScratchVelocity(velocity)`.
3.  **`ScratchViewModel.kt` (State Layer)**:
    *   Updates `ScratchUiState` (for UI feedback).
    *   Calls `ScratchEngine.setVelocity(velocity)` (JNI Bridge).
4.  **`ScratchEngine.kt` (JNI Bridge)**:
    *   Marshals the float value to the C++ layer.
5.  **`ScratchEngine.cpp` (Native Layer)**:
    *   Updates `std::atomic<float> velocity_`. 
    *   *Note: Using atomics ensures the UI thread doesn't block the Audio thread.*
6.  **`ScratchEngine::onAudioReady` (Audio Thread)**:
    *   Reads `velocity_` and `pitch_`.
    *   Increments `scratchPlayhead_` by `(velocity * pitch)`.
    *   Calls `Resampler::resampleAt()` to fetch the interpolated sample.

---

## 2. Audio Signal Path (Memory ➔ Speaker)
This path describes the internal mixing and DSP logic.

1.  **Native Heap**: 
    *   `scratchBuffer_`: Holds the vinyl audio data.
    *   `loopBuffer_`: Holds the background beat data.
2.  **`Resampler.h` (DSP)**:
    *   Performs **4-point Hermite Interpolation**.
    *   Allows smooth playback at any speed (even 0.01x or -2.0x) without digital "stepping" artifacts.
3.  **`ScratchEngine.cpp` (Mixer)**:
    *   **Scratch Branch**: `sample * volume * (muted ? 0 : 1)`.
    *   **Loop Branch**: `sample` (always 1.0x speed).
    *   **Summation**: `out[i] = scratchSample + loopSample`.
    *   **Hard Clipper**: Clamps the summed output to `[-1.0f, 1.0f]` to prevent digital distortion.
4.  **Oboe / AAudio (Hardware)**:
    *   Streams the interleaved stereo float buffer to the Android Open Audio system.

---

## 3. Recording Signal Path (Mic ➔ Scratch Buffer)
This path describes how custom sounds are captured and injected into the engine.

1.  **`MicRecorder.kt` (Kotlin)**:
    *   Opens `AudioRecord` at 44.1kHz.
    *   Reads 16-bit PCM chunks.
    *   **Normalization**: Converts `Short` (-32768 to 32767) to `Float` (-1.0 to 1.0).
2.  **JNI Swap**:
    *   Calls `ScratchEngine.swapBuffer(floatArray)`.
3.  **`ScratchEngine.cpp` (Memory Management)**:
    *   Acquires `bufferMutex_`.
    *   Swaps the `scratchBuffer_` pointer to the new `float[]` buffer.
    *   Resets `scratchPlayhead_` to 0.
    *   Releases `bufferMutex_`.
    *   **Optimization**: Deletes the old `scratchBuffer_` from the native heap *outside* the lock to prevent blocking the audio thread.

---

## 4. Component File Map

| File | Responsibility | Language |
| :--- | :--- | :--- |
| `MainActivity.kt` | App Lifecycle & Permissions | Kotlin |
| `MainScreen.kt` | UI Layout & Event Routing | Kotlin (Compose) |
| `ScratchViewModel.kt` | Business Logic & State Management | Kotlin |
| `TurntableView.kt` | Touch Physics & Visual Rendering | Kotlin (Canvas) |
| `MicRecorder.kt` | Background Audio Capture | Kotlin |
| `ScratchEngine.kt` | JNI Interface Definitions | Kotlin |
| `ScratchEngine.cpp` | Audio Engine, Atomics, & Mixing | C++ |
| `AudioFileLoader.cpp` | Asset Loading & WAV Decoding | C++ |
| `Resampler.h` | Hermite Interpolation Math | C++ |
| `dr_wav.h` | Low-level WAV Parsing | C |
| `CMakeLists.txt` | Build Configuration & NDK Linking | CMake |
