package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

@Composable
fun TreeSelectDocs() {
    val treeData = listOf(
        ElTreeNode("1", "Guidelines", listOf(ElTreeNode("1-1", "Principles"), ElTreeNode("1-2", "Colors"))),
        ElTreeNode("2", "Components", listOf(ElTreeNode("2-1", "Button"), ElTreeNode("2-2", "Input"))),
        ElTreeNode("3", "Resources", listOf(ElTreeNode("3-1", "Icons"), ElTreeNode("3-2", "Fonts"))),
    )
    var selected by remember { mutableStateOf<String?>("1-1") }
    var filtered by remember { mutableStateOf<String?>(null) }
    DemoExample("Basic usage", "Select a node from a tree dropdown.") {
        ElTreeSelect(
            selectedKey = selected,
            onSelectedChange = { selected = it },
            data = treeData,
            placeholder = "Select a node",
            defaultExpandAll = true,
            modifier = Modifier.width(260.dp),
        )
        DemoHint("Selected: ${selected ?: "-"}")
    }
    DemoExample("Filterable", "Search the tree by label.") {
        ElTreeSelect(
            selectedKey = filtered,
            onSelectedChange = { filtered = it },
            data = treeData,
            placeholder = "Type to filter",
            filterable = true,
            modifier = Modifier.width(260.dp),
        )
    }
    DemoExample("Disabled", "Disable the tree select.") {
        ElTreeSelect(
            selectedKey = "2-2",
            onSelectedChange = {},
            data = treeData,
            disabled = true,
            modifier = Modifier.width(260.dp),
        )
    }
}
