// app/src/main/kotlin/com/babyscratch/ui/controls/TransformButton.kt
// Purpose: Compose momentary button for the DJ "transformer scratch" technique.
// Dependencies: Jetpack Compose UI, Foundation, Material3

package com.babyscratch.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TransformButton(
    isMuted: Boolean,
    onMuteChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // The transform button is a momentary switch:
    // When held down, the audio is muted (cut).
    // When released, the audio plays.
    val backgroundColor = if (isMuted) Color.Red else Color.DarkGray
    val textColor = if (isMuted) Color.White else Color.LightGray

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .pointerInput(Unit) {
                awaitEachGesture {
                    // Wait for the user to press down
                    awaitFirstDown(requireUnconsumed = false)
                    onMuteChanged(true)

                    // Wait for the user to release or cancel the touch
                    waitForUpOrCancellation()
                    onMuteChanged(false)
                }
            }
            .padding(8.dp)
    ) {
        Text(
            text = "CUT",
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
