<!-- app/src/main/assets/README.md -->
<!-- Purpose: Instructions for sourcing/creating required audio assets. -->

# Audio Assets Directory

To run Baby Scratch, you must provide two WAV files in the subdirectories here.
Android's `assets/` folder is used instead of `res/raw/` because it supports subdirectories, allowing us to keep our beats and scratch samples organized.

## 1. Scratch Samples (`app/src/main/assets/scratch/`)
This is the audio that will be manipulated by the turntable.
- **Required File:** `scratch/scratchy_seal_3d_side_a.wav`
- **Format:** WAV (PCM or IEEE Float)
- **Sample Rate:** 48000 Hz (recommended for lowest latency)
- **Channels:** Stereo or Mono
- **Length:** 1 to 10 seconds is ideal (e.g., a classic "Ahhh" or "Fresh" vocal sample).

## 2. Backing Beats (`app/src/main/assets/beat/`)
This is the background drum loop that plays independently.
- **Required File:** `beat/qbert_02_side_b.wav`
- **Format:** WAV (PCM or IEEE Float)
- **Sample Rate:** 48000 Hz
- **Channels:** Stereo or Mono
- **Length:** A seamless 1-bar, 2-bar, or 4-bar drum break.

## How to add them:
1. Create or download your WAV files.
2. Name them exactly `scratchy_seal_3d_side_a.wav` and `qbert_02_side_b.wav`.
3. Place them in their respective folders:
   - `app/src/main/assets/scratch/scratchy_seal_3d_side_a.wav`
   - `app/src/main/assets/beat/qbert_02_side_b.wav`
4. Build and run the app.
