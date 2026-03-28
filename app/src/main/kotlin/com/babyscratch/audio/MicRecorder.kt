// app/src/main/kotlin/com/babyscratch/audio/MicRecorder.kt
// Purpose: AudioRecord wrapper using coroutines to capture mic input and atomically swap the scratch buffer.
// Dependencies: ScratchEngine.kt, kotlinx.coroutines

package com.babyscratch.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MicRecorder {

    private var recordingJob: Job? = null
    
    // Specifications per FEATURE_SPEC.md
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    /**
     * Starts recording audio from the microphone on a background IO coroutine.
     * Requires RECORD_AUDIO permission to have been granted already.
     */
    @SuppressLint("MissingPermission")
    fun startRecording(scope: CoroutineScope) {
        if (recordingJob?.isActive == true) return

        recordingJob = scope.launch(Dispatchers.IO) {
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord.release()
                return@launch
            }

            try {
                audioRecord.startRecording()
            } catch (e: IllegalStateException) {
                audioRecord.release()
                return@launch
            }
            
            // OPTIMIZATION: Use primitive array to prevent GC thrashing from Float boxing
            var recordedData = FloatArray(sampleRate * 10) // Pre-allocate 10 seconds
            var totalSamples = 0

            try {
                val audioBuffer = ShortArray(bufferSize)
                while (isActive) {
                    val readResult = audioRecord.read(audioBuffer, 0, audioBuffer.size)
                    if (readResult > 0) {
                        // Expand array if needed
                        if (totalSamples + readResult > recordedData.size) {
                            recordedData = recordedData.copyOf(recordedData.size * 2)
                        }
                        for (i in 0 until readResult) {
                            recordedData[totalSamples++] = audioBuffer[i] / 32768.0f
                        }
                    }
                }
            } finally {
                // Cleanup AudioRecord resources
                if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop()
                }
                audioRecord.release()

                // Once recording stops (button released), swap the buffer in the C++ engine
                if (totalSamples > 0) {
                    ScratchEngine.swapBuffer(recordedData.copyOfRange(0, totalSamples))
                }
            }
        }
    }

    /**
     * Cancels the recording coroutine, which triggers the finally block to stop recording
     * and swap the buffer in the audio engine.
     */
    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
    }
}
