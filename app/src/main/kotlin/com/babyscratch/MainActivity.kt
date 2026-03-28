// app/src/main/kotlin/com/babyscratch/MainActivity.kt
// Purpose: Application entry point, handles permissions, initializes the C++ audio engine, and sets the Compose UI.
// Dependencies: Jetpack Compose, ScratchEngine, MainScreen

package com.babyscratch

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.babyscratch.audio.ScratchEngine
import com.babyscratch.ui.MainScreen
import com.babyscratch.viewmodel.ScratchViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ScratchViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Microphone permission is required to record custom scratches.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request audio recording permission immediately on launch
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        // Initialize the C++ audio engine and pass the AssetManager to load WAV files
        ScratchEngine.startEngine(assets)
        
        // Sync the ViewModel state to the newly created engine
        viewModel.syncEngineState()

        setContent {
            MainScreen(viewModel = viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up native audio resources when the activity is destroyed
        ScratchEngine.stopEngine()
    }
}
