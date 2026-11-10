package io.gilmok.daejang

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.Route
import io.gilmok.daejang.ui.RouteScreen
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** 모든 화면을 1700×1184 크기로 그려 build/screens 폴더에 저장해요. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w1700dp-h1184dp-land-mdpi")
class ScreensCaptureTest {

    @Test
    fun captureAll() {
        val voice = CoachVoice(ApplicationProvider.getApplicationContext())
        Route.entries.filter { it != Route.SPLASH }.forEach { r ->
            captureRoboImage("build/screens/${r.ordinal.toString().padStart(2, '0')}_${r.name}.png") {
                CompositionLocalProvider(LocalDensity provides Density(1f, 1f)) {
                    RouteScreen(r, Nav(), voice)
                }
            }
        }
    }
}
