package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun MessageDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Show transient messages at the top of the screen.") {
        DemoRow {
            ElButton(onClick = { ElMessage.show("An info message") }, text = "Info")
            ElButton(onClick = { ElMessage.show("Success message", ElMessageType.Success) }, text = "Success", type = ElType.Success)
            ElButton(onClick = { ElMessage.show("Warning message", ElMessageType.Warning) }, text = "Warning", type = ElType.Warning)
            ElButton(onClick = { ElMessage.show("Error message", ElMessageType.Error) }, text = "Error", type = ElType.Danger)
        }
    }
    DemoExample("Closable", "Allow the user to dismiss a message.") {
        ElButton(onClick = { ElMessage.show("Closable message", showClose = true, duration = 0) }, text = "Show closable message")
    }
    DemoExample("Close all", "Programmatically clear the message stack.") {
        ElButton(onClick = { ElMessage.closeAll() }, text = "Close all messages")
    }

    DemoExample("Types and centering", "Show success, warning, info and error messages.") {
        DemoRow {
            ElButton(
                onClick = { ElMessage.show("This is a primary message.", ElMessageType.Primary) },
                text = "Primary",
                type = ElType.Primary,
            )
            ElButton(onClick = { ElMessage.show("Success message", ElMessageType.Success) }, text = "Success")
            ElButton(onClick = { ElMessage.show("Warning message", ElMessageType.Warning) }, text = "Warning")
            ElButton(onClick = { ElMessage.show("Info message", ElMessageType.Info) }, text = "Info")
            ElButton(onClick = { ElMessage.show("Error message", ElMessageType.Error) }, text = "Error")
        }
        ElButton(onClick = { ElMessage.show("Centered message", center = true, showClose = true) }, text = "Centered")
    }

    DemoExample("Placement", "Place messages at any of the six viewport positions.") {
        DemoRow {
            ElMessagePlacement.entries.forEach { placement ->
                ElButton(
                    onClick = {
                        ElMessage.show(
                            message = "This is a message from ${placement.name.lowercase()}.",
                            placement = placement,
                        )
                    },
                    text = placement.name,
                    size = ElSize.Small,
                )
            }
        }
    }

    DemoExample("Custom offset", "Offset a placement from the viewport edge.") {
        DemoRow {
            ElButton(
                onClick = {
                    ElMessage.show(
                        message = "This message sits 100dp from the top.",
                        placement = ElMessagePlacement.Top,
                        offset = 100.dp,
                    )
                },
                text = "Offset from top",
            )
            ElButton(
                onClick = {
                    ElMessage.show(
                        message = "This message sits 100dp from the bottom.",
                        placement = ElMessagePlacement.BottomRight,
                        offset = 100.dp,
                        type = ElMessageType.Warning,
                    )
                },
                text = "Offset from bottom",
                type = ElType.Warning,
            )
        }
    }

    DemoExample("Grouping", "Repeated messages collapse into one with a repetition badge.") {
        var count by remember { mutableStateOf(0) }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElButton(
                onClick = {
                    count++
                    ElMessage.show(
                        message = "This message is grouped.",
                        type = ElMessageType.Success,
                        grouping = true,
                    )
                },
                text = "Show grouped message (clicked $count times)",
                type = ElType.Success,
            )
            ElButton(
                onClick = {
                    ElMessage.show(
                        message = "This message starts with 5 repetitions.",
                        type = ElMessageType.Warning,
                        grouping = true,
                        repeatNum = 5,
                    )
                },
                text = "Grouped message with initial repeat count",
                type = ElType.Warning,
            )
        }
    }

    DemoExample("Plain", "Plain messages use a neutral surface with a shadow.") {
        DemoRow {
            ElButton(
                onClick = { ElMessage.show("This is a primary message.", ElMessageType.Primary, plain = true) },
                text = "Primary",
                type = ElType.Primary,
            )
            ElButton(
                onClick = { ElMessage.show("Congrats, this is a success message.", ElMessageType.Success, plain = true) },
                text = "Success",
                type = ElType.Success,
            )
            ElButton(
                onClick = { ElMessage.show("Warning, this is a warning message.", ElMessageType.Warning, plain = true) },
                text = "Warning",
                type = ElType.Warning,
            )
            ElButton(
                onClick = { ElMessage.show("Oops, this is an error message.", ElMessageType.Error, plain = true) },
                text = "Error",
                type = ElType.Danger,
            )
        }
    }

    DemoExample("Custom icon", "Override the default type icon.") {
        ElButton(
            onClick = {
                ElMessage.show(
                    message = "A custom bell icon.",
                    icon = ElIcons.Bell,
                    type = ElMessageType.Success,
                )
            },
            text = "Show message with custom icon",
        )
    }

    DemoExample("Clickable message", "React when the user clicks a message.") {
        ElButton(
            onClick = {
                ElMessage.show(
                    message = "Click me!",
                    type = ElMessageType.Info,
                    showClose = true,
                    onClick = { ElMessage.show("The message was clicked", ElMessageType.Success) },
                )
            },
            text = "Show clickable message",
        )
    }

    DemoExample(
        "Rich content",
        "Pass a composable body; the Compose equivalent of VNode/HTML messages.",
    ) {
        ElButton(
            onClick = {
                ElMessage.show(
                    message = "",
                    type = ElMessageType.Success,
                    messageContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            BasicText(
                                text = "Message can be",
                                style = LocalElTheme.current.typography.small.copy(color = colors.textRegular),
                            )
                            BasicText(
                                text = "rich content",
                                style = LocalElTheme.current.typography.small.copy(color = colors.primary),
                            )
                        }
                    },
                )
            },
            text = "Show rich content message",
            type = ElType.Success,
        )
    }
}
