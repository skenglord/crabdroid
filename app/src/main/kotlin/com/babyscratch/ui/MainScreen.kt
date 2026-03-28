// app/src/main/kotlin/com/babyscratch/ui/MainScreen.kt
// Purpose: Compose root layout assembling the TurntableView and all controls.
// Dependencies: Jetpack Compose, ScratchViewModel, TurntableView, custom controls

package com.babyscratch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.babyscratch.ui.controls.BeatLoopButton
import com.babyscratch.ui.controls.PitchSlider
import com.babyscratch.ui.controls.TransformButton
import com.babyscratch.ui.controls.VolumeFader
import com.babyscratch.viewmodel.ScratchViewModel

@Composable
fun MainScreen(
    viewModel: ScratchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "BABY SCRATCH",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Turntable View (Custom Android View)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { context ->
                        TurntableView(context)
                    },
                    update = { view ->
                        view.onVelocityChanged = { velocity ->
                            viewModel.setScratchVelocity(velocity)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Pitch Slider
            PitchSlider(
                pitch = uiState.pitch,
                onPitchChanged = { viewModel.setPitch(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Main Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Volume Fader
                VolumeFader(
                    volume = uiState.volume,
                    onVolumeChanged = { viewModel.setVolume(it) }
                )

                // Play/Stop Button
                Button(
                    onClick = { viewModel.togglePlay() },
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isPlaying) Color.Green.copy(alpha = 0.7f) else Color.DarkGray
                    )
                ) {
                    Text(
                        text = if (uiState.isPlaying) "■" else "▶",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // Transform (Cut) Button
                TransformButton(
                    isMuted = uiState.isTransformMuted,
                    onMuteChanged = { viewModel.setTransformMuted(it) }
                )

                // Mic Record Button (Momentary)
                val micColor = if (uiState.isRecording) Color.Red else Color.DarkGray
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(micColor)
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                viewModel.setRecordPressed(true)
                                waitForUpOrCancellation()
                                viewModel.setRecordPressed(false)
                            }
                        }
                ) {
                    Text(
                        text = "MIC",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Beat Loop Toggle
            BeatLoopButton(
                isLoopPlaying = uiState.isLoopPlaying,
                onToggle = { viewModel.toggleBeatLoop() },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
