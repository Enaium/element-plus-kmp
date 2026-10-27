package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

@Composable
fun DialogDocs() {
    DemoExample("Basic usage", "A modal dialog with a title, body and footer.") {
        DialogSample()
    }
    DemoExample("Align center", "Center the dialog vertically.") {
        DialogSample(center = true)
    }
    DemoExample("Centered content", "Center the header title and footer actions, with a percentage width.") {
        DialogSample(center = true, centerContent = true, widthPercent = 0.5f)
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

    DemoExample("Customized content", "Nest a table or a form inside the dialog body.") {
        CustomContentDialogSample()
    }

    DemoExample("Destroy on close", "Body content is created only when opened and disposed as soon as closing starts.") {
        DestroyOnCloseSample()
    }

    DemoExample("Draggable dialog", "Drag the header to move the panel; overflow lets it leave the viewport.") {
        DraggableDialogSample()
    }

    DemoExample("Custom animation", "Switch between fade, scale, slide and bounce panel transitions.") {
        CustomAnimationSample()
    }

    DemoExample("Async close with beforeClose", "The close icon waits for an async operation before the dialog closes.") {
        BeforeCloseSample()
    }

    DemoExample("Events", "Observe open, opened, close and closed lifecycle events.") {
        EventsSample()
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
private fun DialogSample(
    center: Boolean = false,
    fullscreen: Boolean = false,
    centerContent: Boolean = false,
    widthPercent: Float? = null,
) {
    var visible by remember { mutableStateOf(false) }
    ElButton(onClick = { visible = true }, text = "Open dialog", type = ElType.Primary)
    ElDialog(
        visible = visible,
        onClose = { visible = false },
        title = "Dialog title",
        center = center,
        fullscreen = fullscreen,
        centerContent = centerContent,
        widthPercent = widthPercent,
        footer = {
            DialogFooter(onCancel = { visible = false }, onConfirm = { visible = false })
        },
    ) {
        BasicText("This is the dialog body. It can contain any composable.", style = LocalElTheme.current.typography.base)
    }
}

@Composable
private fun DialogFooter(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmLoading: Boolean = false,
    confirmText: String = "Confirm",
) {
    ElButton(onClick = onCancel, text = "Cancel")
    Spacer(Modifier.width(8.dp))
    ElButton(onClick = onConfirm, text = confirmText, type = ElType.Primary, loading = confirmLoading)
}

private data class ShippingAddress(val date: String, val name: String, val address: String)

@Composable
private fun CustomContentDialogSample() {
    var tableVisible by remember { mutableStateOf(false) }
    var formVisible by remember { mutableStateOf(false) }
    var formName by remember { mutableStateOf("") }
    var formRegion by remember { mutableStateOf<String?>(null) }
    val theme = LocalElTheme.current
    val rows = remember {
        listOf(
            ShippingAddress("2016-05-02", "John Smith", "No.1518, Jinshajiang Road, Putuo District"),
            ShippingAddress("2016-05-04", "John Smith", "No.1518, Jinshajiang Road, Putuo District"),
            ShippingAddress("2016-05-01", "John Smith", "No.1518, Jinshajiang Road, Putuo District"),
            ShippingAddress("2016-05-03", "John Smith", "No.1518, Jinshajiang Road, Putuo District"),
        )
    }
    val columns = remember {
        listOf(
            ElTableColumn<ShippingAddress>("Date", width = 140.dp, property = { it.date }),
            ElTableColumn<ShippingAddress>("Name", width = 140.dp, property = { it.name }),
            ElTableColumn<ShippingAddress>("Address", property = { it.address }),
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ElButton(onClick = { tableVisible = true }, text = "Open a table dialog", plain = true)
        ElButton(onClick = { formVisible = true }, text = "Open a form dialog", plain = true)
    }
    ElDialog(
        visible = tableVisible,
        onClose = { tableVisible = false },
        title = "Shipping address",
        widthPercent = 0.8f,
    ) {
        ElTable(data = rows, columns = columns, height = 260.dp)
    }
    ElDialog(
        visible = formVisible,
        onClose = { formVisible = false },
        title = "Shipping address",
        width = 500.dp,
        footer = { DialogFooter(onCancel = { formVisible = false }, onConfirm = { formVisible = false }) },
    ) {
        ElForm(labelWidth = 120.dp) {
            ElFormItem(label = "Promotion name") {
                ElInput(value = formName, onValueChange = { formName = it }, placeholder = "Please input")
            }
            ElFormItem(label = "Zones") {
                ElSelect(
                    value = formRegion,
                    onValueChange = { formRegion = it },
                    options = listOf(
                        ElSelectOption("shanghai", "Zone No.1"),
                        ElSelectOption("beijing", "Zone No.2"),
                    ),
                    placeholder = "Please select a zone",
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        BasicText(
            "Dialog bodies can host any form control.",
            style = theme.typography.small.copy(color = theme.colors.textSecondary),
        )
    }
}

@Composable
private fun DestroyOnCloseSample() {
    var visible by remember { mutableStateOf(false) }
    var mounts by remember { mutableStateOf(0) }
    val theme = LocalElTheme.current
    ElButton(onClick = { visible = true }, text = "Open destroy-on-close dialog", type = ElType.Primary)
    ElDialog(
        visible = visible,
        onClose = { visible = false },
        title = "Notice",
        center = true,
        centerContent = true,
        widthPercent = 0.5f,
        destroyOnClose = true,
        footer = { DialogFooter(onCancel = { visible = false }, onConfirm = { visible = false }) },
    ) {
        LaunchedEffect(Unit) { mounts++ }
        BasicText(
            "Before the first open this content is not rendered, and it is destroyed as soon as the dialog starts closing.",
            style = theme.typography.base,
        )
        Spacer(Modifier.height(8.dp))
        BasicText(
            "Content mounted $mounts time(s).",
            style = theme.typography.small.copy(color = theme.colors.textSecondary),
        )
    }
}

@Composable
private fun DraggableDialogSample() {
    var plainVisible by remember { mutableStateOf(false) }
    var overflowVisible by remember { mutableStateOf(false) }
    var customVisible by remember { mutableStateOf(false) }
    val theme = LocalElTheme.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ElButton(onClick = { plainVisible = true }, text = "Draggable dialog", plain = true)
        ElButton(onClick = { overflowVisible = true }, text = "Overflow draggable dialog", plain = true)
        ElButton(onClick = { customVisible = true }, text = "Custom dragging style", plain = true)
    }
    ElDialog(
        visible = plainVisible,
        onClose = { plainVisible = false },
        title = "Tips",
        width = 500.dp,
        draggable = true,
        footer = { DialogFooter(onCancel = { plainVisible = false }, onConfirm = { plainVisible = false }) },
    ) {
        BasicText("It's a draggable dialog. Drag the header to move it.", style = theme.typography.base)
    }
    ElDialog(
        visible = overflowVisible,
        onClose = { overflowVisible = false },
        title = "Tips",
        width = 500.dp,
        draggable = true,
        overflow = true,
        footer = { DialogFooter(onCancel = { overflowVisible = false }, onConfirm = { overflowVisible = false }) },
    ) {
        BasicText("This dialog can be dragged partially outside the viewport.", style = theme.typography.base)
    }
    ElDialog(
        visible = customVisible,
        onClose = { customVisible = false },
        title = "Custom Dragging Style",
        width = 500.dp,
        draggable = true,
        header = {
            Row(
                Modifier
                    .background(theme.colors.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    "Custom Dragging Style",
                    style = theme.typography.medium.copy(color = theme.colors.primary),
                )
            }
        },
        footer = { DialogFooter(onCancel = { customVisible = false }, onConfirm = { customVisible = false }) },
    ) {
        BasicText("This dialog has custom dragging styles. Try dragging the header.", style = theme.typography.base)
    }
}

@Composable
private fun CustomAnimationSample() {
    var visible by remember { mutableStateOf(false) }
    var animation by remember { mutableStateOf(ElDialogAnimation.Fade) }
    val theme = LocalElTheme.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ElDialogAnimation.values().forEach { option ->
            ElButton(
                onClick = {
                    animation = option
                    visible = true
                },
                text = option.name,
                plain = true,
            )
        }
    }
    ElDialog(
        visible = visible,
        onClose = { visible = false },
        title = "${animation.name} Animation Dialog",
        widthPercent = 0.35f,
        animation = animation,
        footer = { DialogFooter(onCancel = { visible = false }, onConfirm = { visible = false }) },
    ) {
        BasicText("Current animation: ${animation.name}", style = theme.typography.base)
        Spacer(Modifier.height(8.dp))
        BasicText(
            "This dialog demonstrates the ${animation.name.lowercase()} animation effect.",
            style = theme.typography.small.copy(color = theme.colors.textSecondary),
        )
    }
}

@Composable
private fun BeforeCloseSample() {
    var visible by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }
    val theme = LocalElTheme.current
    ElButton(onClick = { visible = true }, text = "Open async-closing dialog", type = ElType.Primary)
    ElDialog(
        visible = visible,
        onClose = { visible = false },
        title = "Submit",
        widthPercent = 0.4f,
        beforeClose = { done ->
            closing = true
            delay(1200)
            closing = false
            done()
        },
        footer = {
            DialogFooter(
                onCancel = { visible = false },
                onConfirm = { visible = false },
                confirmLoading = closing,
            )
        },
    ) {
        BasicText(
            "Close this dialog with the close icon, the scrim or Escape: beforeClose runs an async task first, then lets the dialog close.",
            style = theme.typography.base,
        )
        if (closing) {
            Spacer(Modifier.height(8.dp))
            BasicText("Submitting…", style = theme.typography.small.copy(color = theme.colors.primary))
        }
    }
}

@Composable
private fun EventsSample() {
    var visible by remember { mutableStateOf(false) }
    val events = remember { mutableStateListOf<String>() }
    val log: (String) -> Unit = { event ->
        if (events.size >= 6) events.removeAt(events.lastIndex)
        events.add(0, event)
    }
    val theme = LocalElTheme.current
    ElButton(onClick = { visible = true }, text = "Open the event dialog", plain = true)
    DemoHint("Events: " + (if (events.isEmpty()) "none yet" else events.joinToString(" → ")))
    ElDialog(
        visible = visible,
        onClose = {
            log("close")
            visible = false
        },
        title = "Events",
        widthPercent = 0.4f,
        beforeClose = { done ->
            log("before-close")
            done()
        },
        onOpen = { log("open") },
        onOpened = { log("opened") },
        onClosed = { log("closed") },
        footer = { DialogFooter(onCancel = { visible = false }, onConfirm = { visible = false }) },
    ) {
        BasicText("It's an event dialog. Watch the log under the button.", style = theme.typography.base)
        Spacer(Modifier.height(8.dp))
        BasicText(
            "open → opened fire on the way in, close → closed on the way out, obtained through optional callbacks.",
            style = theme.typography.small.copy(color = theme.colors.textSecondary),
        )
    }
}
