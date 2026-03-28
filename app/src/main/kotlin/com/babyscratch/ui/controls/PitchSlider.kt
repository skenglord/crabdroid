// app/src/main/kotlin/com/babyscratch/ui/controls/PitchSlider.kt
// Purpose: Compose slider for adjusting playback pitch/speed (0.5x to 2.0x).
// Dependencies: Jetpack Compose UI, Material3

package com.babyscratch.ui.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PitchSlider(
    pitch: Float,
    onPitchChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PITCH", 
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Format to 2 decimal places
            val formattedPitch = ((pitch * 100.0).roundToInt() / 100.0).toString()
            Text(
                text = "${formattedPitch}x", 
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Slider(
            value = pitch,
            onValueChange = onPitchChanged,
            valueRange = 0.5f..2.0f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
