// app/src/main/kotlin/com/babyscratch/ui/controls/VolumeFader.kt
// Purpose: Compose vertical fader for the scratch record output level (0.0 to 1.0).
// Dependencies: Jetpack Compose UI, Material3

package com.babyscratch.ui.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VolumeFader(
    volume: Float,
    onVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = "VOL",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // To create a vertical slider in Compose, we rotate a standard horizontal slider
        // and swap its width and height constraints in the parent Box.
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = volume,
                onValueChange = onVolumeChanged,
                valueRange = 0f..1f,
                modifier = Modifier
                    .requiredWidth(160.dp)
                    .requiredHeight(40.dp)
                    .graphicsLayer {
                        rotationZ = -90f
                    }
            )
        }
    }
}
