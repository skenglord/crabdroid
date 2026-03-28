<!-- app/src/main/res/raw/README.md -->
<!-- Purpose: Instructions for sourcing/creating required audio assets. -->
<!-- Dependencies: None -->

# Audio Assets Required

To run Baby Scratch, you must provide two WAV files in this directory (`app/src/main/res/raw/`). 
Due to copyright and binary file constraints, they are not included in the source code generation.

## 1. `scratch_record.wav`
This is the audio that will be manipulated by the turntable.
- **Format:** WAV (PCM or IEEE Float)
- **Sample Rate:** 48000 Hz (recommended for lowest latency on modern Android)
- **Channels:** Stereo or Mono (the engine will automatically mix to mono for the scratch buffer)
- **Length:** 1 to 10 seconds is ideal (e.g., a classic "Ahhh" or "Fresh" vocal sample).

## 2. `beat_loop.wav`
This is the background drum loop that plays independently.
- **Format:** WAV (PCM or IEEE Float)
- **Sample Rate:** 48000 Hz
- **Channels:** Stereo or Mono
- **Length:** A seamless 1-bar, 2-bar, or 4-bar drum break.

## How to add them:
1. Create or download your WAV files.
2. Name them exactly `scratch_record.wav` and `beat_loop.wav`.
3. Place them in this folder (`app/src/main/res/raw/`).
4. Build and run the app.
