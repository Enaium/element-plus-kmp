package cn.enaium.elementplus

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElColorPicker
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.components.ElImage
import cn.enaium.elementplus.components.ElInfiniteScroll
import cn.enaium.elementplus.components.ElTimePicker
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElColorFormat
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlinx.datetime.LocalTime
import kotlin.test.Test

/**
 * Small parity tests for the remaining leaf components: icon, image,
 * infinite-scroll, time-picker formatting and color-picker formatting.
 */
@OptIn(ExperimentalTestApi::class)
class MiscComponentsParityTest {
    @Test
    fun iconExposesAccessibilityDescription() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElIcon(ElIcons.Check, contentDescription = "Selected icon")
            }
        }
        onNodeWithContentDescription("Selected icon").assertExists()
    }

    @Test
    fun imageRendersItsContentDescription() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElImage(
                    painter = ColorPainter(Color.Red),
                    contentDescription = "Header image",
                    modifier = Modifier.width(100.dp).height(80.dp),
                )
            }
        }
        onNodeWithContentDescription("Header image").assertExists()
    }

    @Test
    fun infiniteScrollShowsLoadingFooter() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElInfiniteScroll(
                    modifier = Modifier.width(300.dp).height(160.dp),
                    loading = true,
                    loadingText = "Loading more",
                    onLoadMore = {},
                ) {
                    item { BasicText("First row") }
                }
            }
        }
        onNodeWithText("First row").assertExists()
        onNodeWithText("Loading more").assertExists()
    }

    @Test
    fun timePickerFormatsTheControlledValue() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElTimePicker(
                    value = LocalTime(9, 5, 7),
                    onValueChange = {},
                    placeholder = "Pick time",
                )
            }
        }
        onNodeWithText("09:05:07").assertExists()
    }

    @Test
    fun colorPickerFormatsTheControlledColor() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElColorPicker(
                    value = Color.Red,
                    onValueChange = {},
                    format = ElColorFormat.Hex,
                    showAlpha = false,
                )
            }
        }
        onNodeWithText("#FF0000").assertExists()
    }
}
