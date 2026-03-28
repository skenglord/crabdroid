// app/src/main/kotlin/com/babyscratch/ui/controls/BeatLoopButton.kt
// Purpose: Compose toggle button that starts or stops the background drum loop.
// Dependencies: Jetpack Compose UI, Material3

package com.babyscratch.ui.controls

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BeatLoopButton(
    isLoopPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isLoopPlaying) MaterialTheme.colorScheme.primary else Color.DarkGray
    val textColor = if (isLoopPlaying) MaterialTheme.colorScheme.onPrimary else Color.LightGray

    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = if (isLoopPlaying) "BEAT LOOP: ON" else "BEAT LOOP: OFF",
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
