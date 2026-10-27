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
fun PopconfirmDocs() {
    DemoExample("Basic usage", "Ask for confirmation inside a popover.") {
        ElPopconfirm(
            title = "Delete this item?",
            message = "This action cannot be undone.",
            onConfirm = { ElMessage.show("Deleted", ElMessageType.Success) },
            onCancel = { ElMessage.show("Cancelled") },
            triggerContent = { ElButton(onClick = {}, text = "Delete", type = ElType.Danger, plain = true) },
        )
    }
    DemoExample("Placement", "Attach the confirmation above or below.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ElPopconfirm(
                title = "Confirm?",
                placement = cn.enaium.elementplus.internal.ElPlacement.Top,
                onConfirm = {},
                triggerContent = { ElButton(onClick = {}, text = "Top") },
            )
            ElPopconfirm(
                title = "Confirm?",
                placement = cn.enaium.elementplus.internal.ElPlacement.Bottom,
                onConfirm = {},
                triggerContent = { ElButton(onClick = {}, text = "Bottom") },
            )
        }
    }
    DemoExample("Custom text", "Customize the confirm and cancel labels.") {
        ElPopconfirm(
            title = "Save changes?",
            confirmText = "Save",
            cancelText = "Discard",
            type = ElType.Primary,
            onConfirm = { ElMessage.show("Saved") },
            triggerContent = { ElButton(onClick = {}, text = "Save", type = ElType.Primary) },
        )
    }

    DemoExample("Customize", "Customize the title, text and action labels.") {
        ElPopconfirm(
            title = "Delete this item?",
            message = "This action cannot be undone.",
            confirmText = "Delete",
            cancelText = "Keep",
            type = ElType.Danger,
            onConfirm = { ElMessage.show("Deleted", ElMessageType.Success) },
        ) {
            ElButton(onClick = {}, text = "Delete", type = ElType.Danger)
        }
    }
}
