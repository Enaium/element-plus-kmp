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
import kotlinx.coroutines.delay

@Composable
fun MessageBoxDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Alert", "An alert message box has a single confirm action.") {
        ElButton(
            onClick = {
                ElMessageBox.alert(
                    message = "This is an alert message.",
                    title = "Alert",
                    type = ElMessageBoxType.Info,
                    onConfirm = { ElMessage.show("Alert confirmed") },
                )
            },
            text = "Open alert",
        )
    }
    DemoExample("Confirm", "Ask for confirmation before a destructive action.") {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "Are you sure you want to delete this item?",
                    title = "Warning",
                    type = ElMessageBoxType.Warning,
                    onConfirm = { ElMessage.show("Deleted", ElMessageType.Success) },
                    onCancel = { ElMessage.show("Cancelled") },
                )
            },
            text = "Open confirm",
            type = ElType.Warning,
        )
    }
    DemoExample("Prompt", "Collect a text value before confirming.") {
        ElButton(
            onClick = {
                ElMessageBox.prompt(
                    message = "Please enter a value.",
                    title = "Prompt",
                    type = ElMessageBoxType.Info,
                    inputPlaceholder = "Type here",
                    onConfirm = { value -> ElMessage.show("Entered: $value") },
                )
            },
            text = "Open prompt",
            type = ElType.Primary,
        )
    }

    DemoExample("Customization", "Customize copy, type and action labels.") {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "This will permanently remove the selected item.",
                    title = "Delete item",
                    type = ElMessageBoxType.Warning,
                    confirmText = "Delete",
                    cancelText = "Cancel",
                    onConfirm = { ElMessage.show("Deleted", ElMessageType.Success) },
                )
            },
            text = "Customized confirm",
            type = ElType.Danger,
        )
    }

    DemoExample(
        "Before close with loading",
        "Gate the action through a suspending beforeClose; the confirm button shows a loading state until it completes.",
    ) {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "This action takes a moment. Continue?",
                    title = "Message",
                    type = ElMessageBoxType.Info,
                    confirmText = "OK",
                    cancelText = "Cancel",
                    beforeClose = { action ->
                        if (action == ElMessageBoxAction.Confirm) {
                            delay(2000)
                            true
                        } else {
                            true
                        }
                    },
                    onConfirm = { ElMessage.show("Action confirmed", ElMessageType.Success) },
                    onCancel = { ElMessage.show("Action cancelled", ElMessageType.Info) },
                )
            },
            text = "Open message box with beforeClose",
            type = ElType.Primary,
        )
    }

    DemoExample("Input validation", "Validates prompt input against a pattern before confirming.") {
        ElButton(
            onClick = {
                ElMessageBox.prompt(
                    message = "Please input your e-mail.",
                    title = "Tip",
                    type = ElMessageBoxType.Info,
                    inputPlaceholder = "name@example.com",
                    inputPattern = Regex("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?"),
                    inputErrorMessage = "Invalid Email",
                    onConfirm = { value -> ElMessage.show("Your email is: $value", ElMessageType.Success) },
                    onCancel = { ElMessage.show("Input canceled", ElMessageType.Info) },
                )
            },
            text = "Open validated prompt",
            type = ElType.Primary,
        )
    }

    DemoExample("Customized icon", "Override the icon shown next to the title.") {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "It will permanently delete the file. Continue?",
                    title = "Warning",
                    type = ElMessageBoxType.Warning,
                    confirmText = "OK",
                    cancelText = "Cancel",
                    icon = ElIcons.Delete,
                    closeIcon = ElIcons.CloseBold,
                    onConfirm = { ElMessage.show("Delete completed", ElMessageType.Success) },
                    onCancel = { ElMessage.show("Delete canceled", ElMessageType.Info) },
                )
            },
            text = "Open message box with custom icon",
            type = ElType.Warning,
        )
    }

    DemoExample(
        "Distinguish cancel and close",
        "onCancel and onClose fire separately so the caller can tell discarding from dismissing.",
    ) {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "You have unsaved changes, save and proceed?",
                    title = "Confirm",
                    confirmText = "Save",
                    cancelText = "Discard Changes",
                    onConfirm = { ElMessage.show("Changes saved. Proceeding to a new route.", ElMessageType.Success) },
                    onCancel = { ElMessage.show("Changes discarded. Proceeding to a new route.", ElMessageType.Info) },
                    onClose = { ElMessage.show("Stay in the current route", ElMessageType.Info) },
                )
            },
            text = "Open confirm with distinct actions",
            type = ElType.Primary,
        )
    }

    DemoExample("Centered content", "Center the title, message and action buttons.") {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "This message box centers its content and buttons.",
                    title = "Warning",
                    type = ElMessageBoxType.Warning,
                    center = true,
                    roundButton = true,
                    onConfirm = { ElMessage.show("Confirmed", ElMessageType.Success) },
                )
            },
            text = "Open centered message box",
            type = ElType.Warning,
        )
    }

    DemoExample(
        "Rich content",
        "Pass a composable message body; the Compose equivalent of HTML/VNode messages.",
    ) {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "",
                    title = "Message",
                    type = ElMessageBoxType.Info,
                    messageContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BasicText(
                                text = "Message can be ",
                                style = theme.typography.base.copy(color = colors.textRegular),
                            )
                            BasicText(
                                text = "rich content",
                                style = theme.typography.base.copy(color = colors.primary),
                            )
                        }
                    },
                    onConfirm = { ElMessage.show("Confirmed", ElMessageType.Success) },
                    onCancel = { ElMessage.show("Cancelled", ElMessageType.Info) },
                )
            },
            text = "Open message box with rich content",
            type = ElType.Primary,
        )
    }

    DemoExample("Draggable", "Drag the message box by its title bar.") {
        ElButton(
            onClick = {
                ElMessageBox.confirm(
                    message = "Drag me by the title bar. Continue?",
                    title = "Draggable",
                    type = ElMessageBoxType.Info,
                    draggable = true,
                    onConfirm = { ElMessage.show("Confirmed", ElMessageType.Success) },
                )
            },
            text = "Open draggable message box",
            type = ElType.Primary,
        )
    }

    DemoExample("Custom action buttons", "Hide the confirm or cancel button per call.") {
        DemoRow {
            ElButton(
                onClick = {
                    ElMessageBox.confirm(
                        message = "There is no confirm button; dismiss only.",
                        title = "Cancel only",
                        type = ElMessageBoxType.Info,
                        showConfirm = false,
                        onClose = { ElMessage.show("Dismissed", ElMessageType.Info) },
                    )
                },
                text = "Cancel-only confirm",
                size = ElSize.Small,
            )
            ElButton(
                onClick = {
                    ElMessageBox.confirm(
                        message = "There is no cancel button; confirm only.",
                        title = "Confirm only",
                        type = ElMessageBoxType.Warning,
                        showCancel = false,
                        onConfirm = { ElMessage.show("Confirmed", ElMessageType.Success) },
                    )
                },
                text = "Confirm-only confirm",
                size = ElSize.Small,
            )
        }
    }
}
