# Contributing to Baby Scratch

Thank you for your interest in improving Baby Scratch! Because this is a high-performance audio application, we maintain strict standards for both Kotlin and C++ code.

## ⚠️ The Golden Rule of Audio Programming
The C++ `onAudioReady` callback in `ScratchEngine.cpp` runs on a **Real-Time Priority Thread**. 

**Inside this callback, you MUST NOT:**
1. **Allocate memory** (`new`, `malloc`, `std::vector::push_back`, etc.).
2. **Lock mutexes** (unless using `try_lock()`).
3. **Perform I/O** (logging, file reading, network calls).
4. **Call JNI** (calling back into Java/Kotlin).

Failure to follow this rule will cause "audio dropouts" (crackling) as the thread misses its hardware deadline.

## Kotlin Style
- Use **Jetpack Compose** for all UI elements.
- Maintain a strict **Unidirectional Data Flow (UDF)**: UI -> ViewModel -> ScratchEngine.
- Use `StateFlow` for state observation.

## C++ Style
- Use `std::atomic` for all variables shared between the UI thread and the Audio thread.
- Prefer `float` over `double` for DSP calculations to utilize NEON SIMD instructions on ARM processors.
- Keep the `Resampler.h` logic inline for compiler optimization.

## Branching Strategy
1. Feature branches: `feature/your-feature-name`
2. Bug fixes: `fix/issue-description`
3. Always run `./gradlew test` and `./gradlew connectedAndroidTest` before submitting a PR.
