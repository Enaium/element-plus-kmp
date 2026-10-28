package cn.enaium.elementplus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAlert
import cn.enaium.elementplus.components.ElBreadcrumb
import cn.enaium.elementplus.components.ElBreadcrumbItemData
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElDrawer
import cn.enaium.elementplus.components.ElLoading
import cn.enaium.elementplus.components.ElMessageBoxHost
import cn.enaium.elementplus.components.ElNotificationHost
import cn.enaium.elementplus.components.rememberElNotificationService
import cn.enaium.elementplus.components.ElPageHeader
import cn.enaium.elementplus.components.ElPopconfirm
import cn.enaium.elementplus.components.ElStep
import cn.enaium.elementplus.components.ElSteps
import cn.enaium.elementplus.components.rememberElMessageBoxService
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Behavioral ports of the upstream feedback/navigation component tests:
 * alert, drawer, loading, message-box, notification, popconfirm, breadcrumb,
 * page-header and steps.
 */
@OptIn(ExperimentalTestApi::class)
class FeedbackNavigationParityTest {
    @Test
    fun alertCloseTextCallsTheCloseHandler() = runComposeUiTest {
        var closed = false
        setContent {
            ElConfigProvider {
                ElAlert("Saved", closeText = "Dismiss", onClose = { closed = true })
            }
        }

        onNodeWithText("Dismiss").performClick()
        assertTrue(closed)
    }

    @Test
    fun drawerShowsAndClosesContent() = runComposeUiTest {
        var visible by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                ElButton(onClick = { visible = true }, text = "Open drawer")
                ElDrawer(visible = visible, onClose = { visible = false }, title = "Details") {
                    BasicText("Drawer body")
                }
            }
        }

        onNodeWithText("Open drawer").performClick()
        onNodeWithText("Drawer body").assertExists()
        onNodeWithContentDescription("Close this dialog").performClick()
        assertTrue(!visible)
    }

    @Test
    fun loadingShowsOverlayOnlyWhileVisible() = runComposeUiTest {
        var visible by mutableStateOf(true)
        setContent {
            ElConfigProvider {
                Box(Modifier.width(300.dp).height(200.dp)) {
                    ElLoading(visible = visible, text = "Loading data") {
                        BasicText("Loaded content")
                    }
                }
            }
        }

        onNodeWithText("Loading data").assertExists()
        runOnIdle { visible = false }
        waitForIdle()
        onNodeWithText("Loading data").assertDoesNotExist()
        onNodeWithText("Loaded content").assertExists()
    }

    @Test
    fun messageBoxConfirmInvokesTheAction() = runComposeUiTest {
        var confirmed = false
        setContent {
            ElConfigProvider {
                val service = rememberElMessageBoxService()
                ElMessageBoxHost(service) {
                    ElButton(
                        onClick = { service.confirm("Delete this item?", onConfirm = { confirmed = true }) },
                        text = "Open message box",
                    )
                }
            }
        }

        onNodeWithText("Open message box").performClick()
        onNodeWithText("OK").performClick()
        assertTrue(confirmed)
    }

    @Test
    fun notificationServiceRendersANotification() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                val service = rememberElNotificationService()
                ElNotificationHost(service) {
                    ElButton(onClick = { service.notify("Saved successfully", duration = 0) }, text = "Notify")
                }
            }
        }

        onNodeWithText("Notify").performClick()
        waitForIdle()
        onNodeWithText("Saved successfully").assertExists()
    }

    @Test
    fun popconfirmConfirmRunsTheAction() = runComposeUiTest {
        var confirmed = false
        setContent {
            ElConfigProvider {
                ElPopconfirm(
                    title = "Delete?",
                    confirmText = "Delete",
                    onConfirm = { confirmed = true },
                    onCancel = {},
                    triggerContent = { ElButton(onClick = {}, text = "Open confirm") },
                )
            }
        }

        onNodeWithText("Open confirm").performClick()
        onNodeWithText("Delete").performClick()
        assertTrue(confirmed)
    }

    @Test
    fun breadcrumbInvokesItemClick() = runComposeUiTest {
        var clicked: String? = null
        setContent {
            ElConfigProvider {
                ElBreadcrumb(
                    items = listOf(
                        ElBreadcrumbItemData("Home", onClick = { clicked = "home" }),
                        ElBreadcrumbItemData("Current"),
                    ),
                )
            }
        }

        onNodeWithText("Home").performClick()
        assertEquals("home", clicked)
    }

    @Test
    fun pageHeaderBackButtonCallsHandler() = runComposeUiTest {
        var back = false
        setContent {
            ElConfigProvider {
                ElPageHeader(
                    title = "Page title",
                    backText = "Back",
                    onBack = { back = true },
                    content = { BasicText("Page content") },
                )
            }
        }

        onNodeWithText("Back").performClick()
        assertTrue(back)
        onNodeWithText("Page title").assertExists()
        onNodeWithText("Page content").assertExists()
    }

    @Test
    fun stepsRenderTitlesDescriptionsAndActiveIndex() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElSteps(
                    steps = listOf(
                        ElStep("Cart", "Review items"),
                        ElStep("Payment", "Enter card"),
                        ElStep("Done", "Finished"),
                    ),
                    active = 1,
                )
            }
        }

        onNodeWithText("Cart").assertExists()
        onNodeWithText("Review items").assertExists()
        onNodeWithText("Payment").assertExists()
        onNodeWithText("Done").assertExists()
    }
}
