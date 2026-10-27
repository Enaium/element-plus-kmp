package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawerDocs() {
    DemoExample("Basic usage", "A drawer slides in from the right.") {
        DrawerSample()
    }
    DemoExample("Directions", "Drawers can open from any edge.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DrawerSample(ElDrawerDirection.Left)
            DrawerSample(ElDrawerDirection.Top)
            DrawerSample(ElDrawerDirection.Bottom)
        }
    }
    DemoExample("With footer", "Add actions at the bottom of the drawer.") {
        DrawerSample(withFooter = true)
    }
    DemoExample("No title", "Hide the whole header with withHeader = false while a title is still declared.") {
        DrawerSample(headerless = true)
    }

    DemoExample("Customized header", "Provide a custom header slot.") {
        var visible by remember { mutableStateOf(false) }
        ElButton(onClick = { visible = true }, text = "Open custom drawer")
        ElDrawer(
            visible = visible,
            onClose = { visible = false },
            header = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText("Custom drawer header", style = LocalElTheme.current.typography.medium, modifier = Modifier.weight(1f))
                    ElTag("New", type = ElType.Success, size = ElSize.Small)
                }
            },
        ) {
            BasicText("Drawer content with a custom header.", style = LocalElTheme.current.typography.base)
        }
    }

    DemoExample("Customized content", "Nest a table or a form in the drawer body, with an async beforeClose gate.") {
        CustomContentDrawerSample()
    }

    DemoExample("Resizable", "Drag the inner edge to resize the drawer within min and max bounds.") {
        ResizableDrawerSample()
    }

    DemoExample("Nested drawer", "Open a second drawer from inside the first.") {
        var outer by remember { mutableStateOf(false) }
        var inner by remember { mutableStateOf(false) }
        ElButton(onClick = { outer = true }, text = "Open outer drawer")
        ElDrawer(visible = outer, onClose = { outer = false }, title = "Outer drawer", size = 360.dp) {
            BasicText("Click to open a nested drawer.", style = LocalElTheme.current.typography.base)
            Spacer(Modifier.height(10.dp))
            ElButton(onClick = { inner = true }, text = "Open nested drawer", type = ElType.Primary)
        }
        ElDrawer(visible = inner, onClose = { inner = false }, title = "Nested drawer", size = 280.dp) {
            BasicText("Nested drawer content", style = LocalElTheme.current.typography.base)
        }
    }
    DemoExample("Modal", "Disable the scrim for a non-modal drawer.") {
        var visible by remember { mutableStateOf(false) }
        ElButton(onClick = { visible = true }, text = "Open non-modal drawer")
        ElDrawer(visible = visible, onClose = { visible = false }, title = "Non-modal drawer", modal = false) {
            BasicText("The page behind is not dimmed.", style = LocalElTheme.current.typography.base)
        }
    }
}

@Composable
private fun DrawerSample(
    direction: ElDrawerDirection = ElDrawerDirection.Right,
    withFooter: Boolean = false,
    headerless: Boolean = false,
) {
    var visible by remember { mutableStateOf(false) }
    ElButton(onClick = { visible = true }, text = "Open drawer")
    ElDrawer(
        visible = visible,
        onClose = { visible = false },
        title = "Drawer title",
        direction = direction,
        withHeader = !headerless,
        size = if (direction == ElDrawerDirection.Left || direction == ElDrawerDirection.Right) 300.dp else 220.dp,
        footer = if (withFooter) {
            { ElButton(onClick = { visible = false }, text = "Done", type = ElType.Primary, size = ElSize.Small) }
        } else null,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BasicText("Drawer content", style = LocalElTheme.current.typography.base)
            BasicText("Use drawers for forms or secondary navigation.", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary))
        }
    }
}

private data class DrawerActivity(val date: String, val name: String, val address: String)

@Composable
private fun CustomContentDrawerSample() {
    var tableVisible by remember { mutableStateOf(false) }
    var formVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var formName by remember { mutableStateOf("") }
    var formRegion by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val theme = LocalElTheme.current
    val rows = remember {
        listOf(
            DrawerActivity("2016-05-02", "Peter Parker", "Queens, New York City"),
            DrawerActivity("2016-05-04", "Peter Parker", "Queens, New York City"),
            DrawerActivity("2016-05-01", "Peter Parker", "Queens, New York City"),
            DrawerActivity("2016-05-03", "Peter Parker", "Queens, New York City"),
        )
    }
    val columns = remember {
        listOf(
            ElTableColumn<DrawerActivity>("Date", width = 130.dp, property = { it.date }),
            ElTableColumn<DrawerActivity>("Name", width = 150.dp, property = { it.name }),
            ElTableColumn<DrawerActivity>("Address", property = { it.address }),
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ElButton(onClick = { tableVisible = true }, text = "Drawer with nested table", textButton = true)
        ElButton(onClick = { formVisible = true }, text = "Drawer with nested form", textButton = true)
    }
    ElDrawer(
        visible = tableVisible,
        onClose = { tableVisible = false },
        title = "I have a nested table inside!",
        direction = ElDrawerDirection.Right,
        sizePercent = 0.5f,
        scrollable = false,
    ) {
        ElTable(data = rows, columns = columns, height = 320.dp)
    }
    ElDrawer(
        visible = formVisible,
        onClose = { formVisible = false },
        title = "I have a nested form inside!",
        direction = ElDrawerDirection.Left,
        size = 400.dp,
        beforeClose = { done ->
            if (!loading) {
                loading = true
                delay(900)
                loading = false
                done()
            }
        },
        footer = {
            ElButton(onClick = { formVisible = false }, text = "Cancel")
            Spacer(Modifier.width(8.dp))
            ElButton(
                onClick = {
                    scope.launch {
                        loading = true
                        delay(1200)
                        loading = false
                        formVisible = false
                    }
                },
                text = if (loading) "Submitting ..." else "Submit",
                type = ElType.Primary,
                loading = loading,
            )
        },
    ) {
        ElForm(labelWidth = 80.dp) {
            ElFormItem(label = "Name") {
                ElInput(value = formName, onValueChange = { formName = it }, placeholder = "Please input")
            }
            ElFormItem(label = "Area") {
                ElSelect(
                    value = formRegion,
                    onValueChange = { formRegion = it },
                    options = listOf(
                        ElSelectOption("shanghai", "Area1"),
                        ElSelectOption("beijing", "Area2"),
                    ),
                    placeholder = "Please select activity area",
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        BasicText(
            "Clicking the close icon submits asynchronously through beforeClose before the drawer closes.",
            style = theme.typography.small.copy(color = theme.colors.textSecondary),
        )
    }
}

@Composable
private fun ResizableDrawerSample() {
    var direction by remember { mutableStateOf(ElDrawerDirection.Right) }
    var visible by remember { mutableStateOf(false) }
    var currentSize by remember { mutableStateOf(300.dp) }
    val theme = LocalElTheme.current
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ElDrawerDirection.values().forEach { option ->
            ElButton(
                onClick = {
                    direction = option
                    currentSize = 300.dp
                    visible = true
                },
                text = option.name.lowercase(),
                plain = true,
            )
        }
    }
    ElDrawer(
        visible = visible,
        onClose = { visible = false },
        title = "Resizable drawer",
        direction = direction,
        size = 300.dp,
        resizable = ElDrawerResizable(minSize = 200.dp, maxSize = 520.dp),
        onResize = { currentSize = it },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText("Drag the inner edge to resize this drawer.", style = theme.typography.base)
            DemoHint("Current size: ${currentSize.value.toInt()} dp (min 200dp, max 520dp)")
        }
    }
}
