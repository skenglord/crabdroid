// app/src/main/kotlin/com/babyscratch/audio/ScratchEngine.kt
// Purpose: JNI declarations matching ScratchEngine.cpp extern "C" signatures.
// Dependencies: None

package com.babyscratch.audio

import android.content.res.AssetManager

object ScratchEngine {

    init {
        System.loadLibrary("babyscratch")
    }

    /**
     * Initializes the Oboe audio stream and loads the default WAV assets.
     */
    external fun startEngine(assetManager: AssetManager)

    /**
     * Stops the Oboe audio stream and cleans up native resources.
     */
    external fun stopEngine()

    /**
     * Sets the playback velocity (1.0 = normal speed, negative = reverse).
     */
    external fun setVelocity(velocity: Float)

    /**
     * Sets the pitch multiplier (0.5 to 2.0).
     */
    external fun setPitch(pitch: Float)

    /**
     * Mutes or unmutes the scratch audio (used for the transform/cut button).
     */
    external fun setMuted(muted: Boolean)

    /**
     * Sets the output volume of the scratch audio (0.0 to 1.0).
     */
    external fun setVolume(volume: Float)

    /**
     * Starts or stops the scratch playhead.
     */
    external fun setPlaying(playing: Boolean)

    /**
     * Starts or stops the background beat loop.
     */
    external fun setLoopPlaying(playing: Boolean)

    /**
     * Swaps the current scratch buffer with a newly recorded microphone buffer.
     * The native engine takes ownership of the data.
     */
    external fun swapBuffer(buffer: FloatArray)
}
