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
import androidx.compose.ui.graphics.Brush
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
fun NotificationDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Notifications appear at a corner of the screen.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "Notification",
                    message = "This is a notification from Element Plus KMP.",
                    type = ElNotificationType.Info,
                )
            },
            text = "Show notification",
        )
    }
    DemoExample("Types", "Success, warning and error notifications.") {
        DemoRow {
            ElButton(
                onClick = { ElNotification.notify("Success", "The operation completed.", ElNotificationType.Success) },
                text = "Success",
                type = ElType.Success,
            )
            ElButton(
                onClick = { ElNotification.notify("Warning", "Check the form fields.", ElNotificationType.Warning) },
                text = "Warning",
                type = ElType.Warning,
            )
            ElButton(
                onClick = { ElNotification.notify("Error", "Something went wrong.", ElNotificationType.Error) },
                text = "Error",
                type = ElType.Danger,
            )
        }
    }
    DemoExample("Position", "Place notifications in any corner.") {
        DemoRow {
            ElNotificationPosition.entries.forEach { position ->
                ElButton(
                    onClick = {
                        ElNotification.notify(
                            title = position.name,
                            message = "Notification at ${position.name}.",
                            position = position,
                        )
                    },
                    text = position.name,
                    size = ElSize.Small,
                )
            }
        }
    }

    DemoExample("No close button", "Keep the notification until its duration expires.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "No close",
                    message = "This notification has no close button.",
                    showClose = false,
                )
            },
            text = "Show no-close notification",
        )
    }

    DemoExample("Persistent notification", "Set duration to 0 so the notification stays until closed.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "Prompt",
                    message = "This is a message that does not automatically close.",
                    duration = 0,
                )
            },
            text = "Show persistent notification",
        )
    }

    DemoExample("Offset", "Offset notifications from the viewport edge.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "Success",
                    message = "This notification sits 100dp from the top.",
                    type = ElNotificationType.Success,
                    offset = 100.dp,
                )
            },
            text = "Notification with offset",
            type = ElType.Success,
        )
    }

    DemoExample("Progress bar", "Show a countdown bar; hover pauses it by default.") {
        DemoRow {
            ElButton(
                onClick = {
                    ElNotification.notify(
                        title = "Progress",
                        message = "Hover to pause the timer and progress bar.",
                        type = ElNotificationType.Info,
                        duration = 6000,
                        progress = true,
                    )
                },
                text = "Default progress",
            )
            ElButton(
                onClick = {
                    ElNotification.notify(
                        title = "Custom color",
                        message = "A gradient progress bar in the notification type color.",
                        type = ElNotificationType.Success,
                        duration = 6000,
                        progress = true,
                        progressBrush = Brush.horizontalGradient(
                            listOf(colors.success, colors.primary)
                        ),
                    )
                },
                text = "Custom color",
                type = ElType.Success,
            )
            ElButton(
                onClick = {
                    ElNotification.notify(
                        title = "No pause on hover",
                        message = "The timer keeps running even when hovering.",
                        duration = 6000,
                        progress = true,
                        pauseOnHover = false,
                    )
                },
                text = "No pause on hover",
            )
        }
    }

    DemoExample("Clickable notification", "React when the user clicks the notification.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "Clickable",
                    message = "Click this notification.",
                    type = ElNotificationType.Success,
                    onClick = { ElMessage.show("Notification clicked", ElMessageType.Success) },
                )
            },
            text = "Show clickable notification",
            type = ElType.Success,
        )
    }

    DemoExample("Custom icon", "Override the icon for a notification.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "Reminder",
                    message = "This notification uses a bell icon.",
                    icon = ElIcons.Bell,
                )
            },
            text = "Show custom icon notification",
        )
    }

    DemoExample("Rich content", "Pass a composable message body instead of a string.") {
        ElButton(
            onClick = {
                ElNotification.notify(
                    title = "Use rich content",
                    message = null,
                    messageContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            BasicText(
                                text = "Message can be",
                                style = LocalElTheme.current.typography.small.copy(color = colors.textSecondary),
                            )
                            BasicText(
                                text = "rich content",
                                style = LocalElTheme.current.typography.small.copy(color = colors.primary),
                            )
                        }
                    },
                )
            },
            text = "Show rich content notification",
        )
    }
}
