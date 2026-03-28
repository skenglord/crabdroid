// app/src/androidTest/kotlin/com/babyscratch/ui/MainScreenTest.kt
package com.babyscratch.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.down
import androidx.compose.ui.test.up
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPlayButtonTogglesState() {
        composeTestRule.setContent { MainScreen() }

        // Initial state should be stopped (▶)
        composeTestRule.onNodeWithText("▶").assertExists()

        // Click play
        composeTestRule.onNodeWithText("▶").performClick()

        // State should change to playing (■)
        composeTestRule.onNodeWithText("■").assertExists()
    }

    @Test
    fun testBeatLoopButtonToggles() {
        composeTestRule.setContent { MainScreen() }

        composeTestRule.onNodeWithText("BEAT LOOP: OFF").assertExists()
        composeTestRule.onNodeWithText("BEAT LOOP: OFF").performClick()
        composeTestRule.onNodeWithText("BEAT LOOP: ON").assertExists()
    }

    @Test
    fun testTransformButtonMomentaryAction() {
        composeTestRule.setContent { MainScreen() }

        val cutButton = composeTestRule.onNodeWithText("CUT")
        
        // Simulate holding the button down
        cutButton.performTouchInput { down(center) }
        
        // Release
        cutButton.performTouchInput { up() }
    }
}
