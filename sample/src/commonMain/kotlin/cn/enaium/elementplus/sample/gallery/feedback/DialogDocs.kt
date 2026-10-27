package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun DialogDocs() {
    DemoExample("Basic usage", "A modal dialog with a title, body and footer.") {
        DialogSample()
    }
    DemoExample("Align center", "Center the dialog vertically.") {
        DialogSample(center = true)
    }
    DemoExample("Fullscreen", "A fullscreen dialog is useful for editors.") {
        DialogSample(fullscreen = true)
    }

    DemoExample("Custom header", "Provide a fully custom header slot.") {
        var visible by remember { mutableStateOf(false) }
        ElButton(onClick = { visible = true }, text = "Open custom header", type = ElType.Primary)
        ElDialog(
            visible = visible,
            onClose = { visible = false },
            header = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText("Custom header", style = LocalElTheme.current.typography.medium, modifier = Modifier.weight(1f))
                    ElText("new", type = ElType.Success)
                }
            },
        ) {
            BasicText("Dialog content with a custom header.", style = LocalElTheme.current.typography.base)
        }
    }

    DemoExample("Nested dialog", "Open a second dialog from inside the first.") {
        var outer by remember { mutableStateOf(false) }
        var inner by remember { mutableStateOf(false) }
        ElButton(onClick = { outer = true }, text = "Open outer", type = ElType.Primary)
        ElDialog(
            visible = outer,
            onClose = { outer = false },
            title = "Outer dialog",
            footer = { ElButton(onClick = { outer = false }, text = "Close") },
        ) {
            BasicText("Click the button to open a nested dialog.", style = LocalElTheme.current.typography.base)
            Spacer(Modifier.height(10.dp))
            ElButton(onClick = { inner = true }, text = "Open nested", type = ElType.Primary)
        }
        ElDialog(
            visible = inner,
            onClose = { inner = false },
            title = "Nested dialog",
            width = 360.dp,
            footer = { ElButton(onClick = { inner = false }, text = "Done", type = ElType.Primary) },
        ) {
            BasicText("Nested content", style = LocalElTheme.current.typography.base)
        }
    }

    DemoExample("Modal", "Turn off the modal scrim while keeping the dialog panel.") {
        var visible by remember { mutableStateOf(false) }
        ElButton(onClick = { visible = true }, text = "Open non-modal", type = ElType.Primary)
        ElDialog(
            visible = visible,
            onClose = { visible = false },
            title = "Non-modal dialog",
            modal = false,
            footer = { ElButton(onClick = { visible = false }, text = "Close") },
        ) {
            BasicText("The page behind this dialog is not dimmed.", style = LocalElTheme.current.typography.base)
        }
    }
}

@Composable
private fun DialogSample(center: Boolean = false, fullscreen: Boolean = false) {
    var visible by remember { mutableStateOf(false) }
    ElButton(onClick = { visible = true }, text = "Open dialog", type = ElType.Primary)
    ElDialog(
        visible = visible,
        onClose = { visible = false },
        title = "Dialog title",
        center = center,
        fullscreen = fullscreen,
        footer = {
            ElButton(onClick = { visible = false }, text = "Cancel")
            androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
            ElButton(onClick = { visible = false }, text = "Confirm", type = ElType.Primary)
        },
    ) {
        BasicText("This is the dialog body. It can contain any composable.", style = LocalElTheme.current.typography.base)
    }
}
