package cn.enaium.elementplus.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.elementplus.sample.gallery.SampleApp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SampleInteractionTest {

    @Test
    fun sidebarMenuSelectsAComponent() = runComposeUiTest {
        setContent { SampleApp() }
        onNodeWithText("Border").performClick()
        onNodeWithText("Border, radius and shadow tokens for surfaces.").assertExists()
        onNodeWithText("Radius").assertExists()
    }

    @Test
    fun searchFiltersTheComponentMenu() = runComposeUiTest {
        setContent { SampleApp() }
        onNode(hasSetTextAction()).performTextInput("tooltip")
        onNodeWithText("Tooltip").performClick()
        onNodeWithText("Short hints on hover.").assertExists()
        onNodeWithText("Placement").assertExists()
    }

    @Test
    fun githubLinkOpensTheRepository() = runComposeUiTest {
        var openedUrl: String? = null
        setContent {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        openedUrl = uri
                    }
                },
            ) {
                SampleApp()
            }
        }
        onNodeWithText("GitHub").performClick()
        assertEquals("https://github.com/Enaium/element-plus-kmp", openedUrl)
    }

    @Test
    fun narrowWindowDrawerShowsTheComponentMenu() = runComposeUiTest {
        setContent {
            Box(Modifier.width(600.dp).fillMaxHeight()) {
                SampleApp(initialDrawerOpen = true)
            }
        }
        onNodeWithText("Scrollbar").assertIsDisplayed()
    }

    @Test
    fun switchingToFrenchRemovesChineseFromTheMenu() = runComposeUiTest {
        setContent { SampleApp() }
        onNodeWithText("English").performClick()
        onNodeWithText("French").performClick()
        onNodeWithText("Border").assertExists()
        onNodeWithText("Border 边框").assertDoesNotExist()
    }

    @Test
    fun componentExampleIsInteractive() = runComposeUiTest {
        setContent { SampleApp() }
        onNodeWithText("Click to load").performScrollTo().performClick()
        onNodeWithText("Click to load").assertDoesNotExist()
    }
}
