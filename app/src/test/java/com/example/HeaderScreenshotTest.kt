package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.components.UniversalAccessibilityHeader
import com.example.ui.theme.MosquiAlertaTheme
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
class HeaderScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun header_screenshot() {
    composeTestRule.setContent {
      MosquiAlertaTheme {
        UniversalAccessibilityHeader(
          isEasyMode = false,
          onToggleEasyMode = {},
          unreadAlertsCount = 2,
          onOpenAlerts = {},
          onOpenWhatsApp = {},
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/header.png")
  }
}
