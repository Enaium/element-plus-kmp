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
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

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
    DemoExample("No title", "Hide the drawer header for content-only panels.") {
        DrawerSample(title = null)
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
private fun DrawerSample(direction: ElDrawerDirection = ElDrawerDirection.Right, withFooter: Boolean = false, title: String? = "Drawer title") {
    var visible by remember { mutableStateOf(false) }
    ElButton(onClick = { visible = true }, text = "Open drawer")
    ElDrawer(
        visible = visible,
        onClose = { visible = false },
        title = title,
        direction = direction,
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
