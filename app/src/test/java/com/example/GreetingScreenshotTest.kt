package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.MatchEntity
import com.example.ui.screens.MatchRowItem
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleMatch = MatchEntity(
        id = 1,
        teamA = "México",
        teamB = "EE.UU.",
        stage = "Fase de Grupos",
        stadium = "Estadio Azteca, Cd. de México",
        groupName = "Grupo A",
        matchTimeMillis = 1781136000000L,
        goalsA = 2,
        goalsB = 1,
        status = "LIVE",
        minute = 75,
        alertsEnabled = true,
        subscribed = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        Scaffold { innerPadding ->
          Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            MatchRowItem(
                match = sampleMatch,
                isSimulating = true,
                onRowClick = {},
                onAlertToggle = {}
            )
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
