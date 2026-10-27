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
fun TransferDocs() {
    val data = cityOptions.map { ElTransferItem(it.value, it.label) }
    var value by remember { mutableStateOf(listOf("chengdu")) }
    var filterableValue by remember { mutableStateOf(emptyList<String>()) }
    DemoExample("Basic usage", "Move items between two lists.") {
        ElTransfer(
            data = data,
            value = value,
            onValueChange = { value = it },
            titles = listOf("Available", "Selected"),
            panelWidth = 210.dp,
            panelHeight = 240.dp,
        )
    }
    DemoExample("Filterable", "Filter each list before moving items.") {
        ElTransfer(
            data = data,
            value = filterableValue,
            onValueChange = { filterableValue = it },
            titles = listOf("Available", "Selected"),
            filterable = true,
            panelWidth = 210.dp,
            panelHeight = 240.dp,
        )
    }
    DemoExample("Custom titles", "Use custom titles and button texts.") {
        var custom by remember { mutableStateOf(listOf("beijing")) }
        ElTransfer(
            data = data,
            value = custom,
            onValueChange = { custom = it },
            titles = listOf("Cities", "Chosen"),
            filterable = true,
            buttonTexts = listOf("Add", "Remove"),
            panelWidth = 210.dp,
            panelHeight = 220.dp,
        )
    }

    DemoExample("Target order", "Choose where newly moved items are inserted.") {
        var targetValue by remember { mutableStateOf(listOf("chengdu")) }
        ElTransfer(
            data = data,
            value = targetValue,
            onValueChange = { targetValue = it },
            titles = listOf("Available", "Selected"),
            targetOrder = ElTransferTargetOrder.Push,
            panelWidth = 210.dp,
            panelHeight = 220.dp,
        )
    }
}
