// app/src/test/kotlin/com/babyscratch/viewmodel/ScratchViewModelTest.kt
package com.babyscratch.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ScratchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScratchViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `togglePlay updates state and sets base velocity`() {
        assertEquals(false, viewModel.uiState.value.isPlaying)
        
        viewModel.togglePlay()
        assertEquals(true, viewModel.uiState.value.isPlaying)
        assertEquals(1.0f, viewModel.uiState.value.velocity) // Base forward velocity

        viewModel.togglePlay()
        assertEquals(false, viewModel.uiState.value.isPlaying)
        assertEquals(0.0f, viewModel.uiState.value.velocity) // Stopped
    }

    @Test
    fun `setPitch updates state correctly`() {
        viewModel.setPitch(1.5f)
        assertEquals(1.5f, viewModel.uiState.value.pitch)
    }

    @Test
    fun `setTransformMuted updates state correctly`() {
        viewModel.setTransformMuted(true)
        assertEquals(true, viewModel.uiState.value.isTransformMuted)
    }
}
