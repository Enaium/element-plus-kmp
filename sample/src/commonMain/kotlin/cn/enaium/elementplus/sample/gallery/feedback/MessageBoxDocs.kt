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
fun MessageBoxDocs() {
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
}
