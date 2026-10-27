package cn.enaium.elementplus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElContainer
import cn.enaium.elementplus.components.ElHeader
import cn.enaium.elementplus.components.ElMain
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ContainerLayoutTest {
    @Test
    fun mainFillsTheContainerWidth() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Box(Modifier.width(400.dp).height(200.dp)) {
                    ElContainer(
                        header = {
                            ElHeader(Modifier.padding(horizontal = 12.dp)) {
                                BasicText("Header")
                            }
                        },
                    ) {
                        ElMain(Modifier.testTag("container-main")) {
                            BasicText("Main")
                        }
                    }
                }
            }
        }

        val bounds = onNodeWithTag("container-main").getUnclippedBoundsInRoot()
        assertEquals(400.dp, bounds.right - bounds.left)
    }
}
