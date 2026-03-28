// app/src/main/kotlin/com/babyscratch/viewmodel/ScratchViewModel.kt
// Purpose: StateFlow for all UI state, bridging Compose UI events to the C++ audio engine via ScratchEngine.
// Dependencies: ScratchEngine.kt, MicRecorder.kt, ViewModel, StateFlow

package com.babyscratch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyscratch.audio.MicRecorder
import com.babyscratch.audio.ScratchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScratchUiState(
    val isPlaying: Boolean = false,
    val isLoopPlaying: Boolean = false,
    val isTransformMuted: Boolean = false,
    val isRecording: Boolean = false,
    val pitch: Float = 1.0f,
    val volume: Float = 1.0f,
    val velocity: Float = 0.0f
)

class ScratchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScratchUiState())
    val uiState: StateFlow<ScratchUiState> = _uiState.asStateFlow()

    private val micRecorder = MicRecorder()

    /**
     * Toggles the main turntable motor play/stop state.
     */
    fun togglePlay() {
        val newPlayingState = !_uiState.value.isPlaying
        _uiState.value = _uiState.value.copy(isPlaying = newPlayingState)
        
        ScratchEngine.setPlaying(newPlayingState)
        
        // If the motor is turned on, set base velocity to 1.0 (forward).
        // If turned off, set to 0.0 (stopped).
        if (newPlayingState) {
            setScratchVelocity(1.0f)
        } else {
            setScratchVelocity(0.0f)
        }
    }

    /**
     * Toggles the background beat loop.
     */
    fun toggleBeatLoop() {
        val newLoopState = !_uiState.value.isLoopPlaying
        _uiState.value = _uiState.value.copy(isLoopPlaying = newLoopState)
        ScratchEngine.setLoopPlaying(newLoopState)
    }

    /**
     * Momentary transform (cut) button. Mutes audio when held.
     */
    fun setTransformMuted(isMuted: Boolean) {
        _uiState.value = _uiState.value.copy(isTransformMuted = isMuted)
        ScratchEngine.setMuted(isMuted)
    }

    /**
     * Sets the pitch multiplier (0.5x to 2.0x).
     */
    fun setPitch(pitch: Float) {
        _uiState.value = _uiState.value.copy(pitch = pitch)
        ScratchEngine.setPitch(pitch)
    }

    /**
     * Sets the scratch output volume (0.0 to 1.0).
     */
    fun setVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(volume = volume)
        ScratchEngine.setVolume(volume)
    }

    /**
     * Updates the angular velocity of the record (driven by TurntableView touch events).
     */
    fun setScratchVelocity(velocity: Float) {
        _uiState.value = _uiState.value.copy(velocity = velocity)
        ScratchEngine.setVelocity(velocity)
    }

    /**
     * Momentary record button. Captures mic input while held and swaps buffer on release.
     */
    fun setRecordPressed(isPressed: Boolean) {
        _uiState.value = _uiState.value.copy(isRecording = isPressed)
        if (isPressed) {
            micRecorder.startRecording(viewModelScope)
        } else {
            micRecorder.stopRecording()
        }
    }

    /**
     * Synchronizes the current ViewModel state with the native C++ engine.
     * Useful when the Activity/Engine is recreated but the ViewModel survives.
     */
    fun syncEngineState() {
        val state = _uiState.value
        ScratchEngine.setPlaying(state.isPlaying)
        ScratchEngine.setLoopPlaying(state.isLoopPlaying)
        ScratchEngine.setMuted(state.isTransformMuted)
        ScratchEngine.setPitch(state.pitch)
        ScratchEngine.setVolume(state.volume)
        ScratchEngine.setVelocity(state.velocity)
    }

    override fun onCleared() {
        super.onCleared()
        micRecorder.stopRecording()
    }
}
