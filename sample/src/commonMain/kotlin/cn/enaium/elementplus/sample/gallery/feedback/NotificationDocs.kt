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
fun NotificationDocs() {
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
}
