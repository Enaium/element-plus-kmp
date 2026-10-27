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
            ElButton(onClick = { ElMessage.show("Success message", ElMessageType.Success) }, text = "Success")
            ElButton(onClick = { ElMessage.show("Warning message", ElMessageType.Warning) }, text = "Warning")
            ElButton(onClick = { ElMessage.show("Info message", ElMessageType.Info) }, text = "Info")
            ElButton(onClick = { ElMessage.show("Error message", ElMessageType.Error) }, text = "Error")
        }
        ElButton(onClick = { ElMessage.show("Centered message", center = true, showClose = true) }, text = "Centered")
    }
}
