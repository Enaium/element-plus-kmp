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

    DemoExample(
        "Customizable items, footer slots and check counters",
        "Render each option with a custom slot, add footer content, and format the check counters.",
    ) {
        val options = remember {
            (1..15).map { ElTransferItem("$it", "Option $it", disabled = it % 4 == 0) }
        }
        var customValue by remember { mutableStateOf(listOf("1")) }
        ElTransfer(
            data = options,
            value = customValue,
            onValueChange = { customValue = it },
            titles = listOf("Source", "Target"),
            filterable = true,
            buttonTexts = listOf("To left", "To right"),
            leftDefaultChecked = listOf("2", "3"),
            rightDefaultChecked = listOf("1"),
            noCheckedFormat = "\${total}",
            hasCheckedFormat = "\${checked}/\${total}",
            itemContent = { item ->
                BasicText(
                    text = "${item.key} - ${item.label}",
                    style = LocalElTheme.current.typography.small.copy(
                        color = LocalElTheme.current.colors.textRegular,
                    ),
                )
            },
            leftFooter = {
                ElButton(onClick = {}, size = ElSize.Small, text = "Operation")
            },
            rightFooter = {
                ElButton(onClick = {}, size = ElSize.Small, text = "Operation")
            },
            panelWidth = 220.dp,
            panelHeight = 250.dp,
        )
    }

    DemoExample(
        "Empty content slots",
        "Replace the built-in empty text with a custom empty state per panel.",
    ) {
        val options = remember { (1..5).map { ElTransferItem("$it", "Option $it") } }
        var emptyValue by remember { mutableStateOf(emptyList<String>()) }
        ElTransfer(
            data = options,
            value = emptyValue,
            onValueChange = { emptyValue = it },
            leftEmptyContent = { ElEmpty(imageSize = 60.dp, description = "No data") },
            rightEmptyContent = { ElEmpty(imageSize = 60.dp, description = "No data") },
            panelWidth = 210.dp,
            panelHeight = 240.dp,
        )
    }

    DemoExample(
        "Prop alias (custom key and label fields)",
        "Map a raw model to transfer items with ElTransferProps instead of using ElTransferItem directly.",
    ) {
        var aliasValue by remember { mutableStateOf(emptyList<String>()) }
        ElTransfer(
            data = transferAliasOptions.toTransferItems(
                ElTransferProps(
                    key = { it.id },
                    label = { it.desc },
                    disabled = { it.locked },
                ),
            ),
            value = aliasValue,
            onValueChange = { aliasValue = it },
            titles = listOf("Available", "Selected"),
            panelWidth = 210.dp,
            panelHeight = 220.dp,
        )
    }

    DemoExample(
        "Virtual scroll",
        "Render a very large list with virtual scrolling and a fixed item size.",
    ) {
        val options = remember {
            (1..2000).map { ElTransferItem("$it", "Option $it", disabled = it % 4 == 0) }
        }
        var virtualValue by remember { mutableStateOf(emptyList<String>()) }
        ElTransfer(
            data = options,
            value = virtualValue,
            onValueChange = { virtualValue = it },
            filterable = true,
            virtualScroll = true,
            itemSize = 30.dp,
            panelWidth = 220.dp,
            panelHeight = 250.dp,
        )
    }

    DemoExample(
        "Change event and check change events",
        "Receive the moved keys with their direction and the checked keys of each panel.",
    ) {
        val options = remember { (1..8).map { ElTransferItem("$it", "Option $it") } }
        var eventValue by remember { mutableStateOf(emptyList<String>()) }
        var lastDirection by remember { mutableStateOf("-") }
        var lastMoved by remember { mutableStateOf("-") }
        var lastChecked by remember { mutableStateOf("-") }
        ElTransfer(
            data = options,
            value = eventValue,
            onValueChange = { eventValue = it },
            titles = listOf("Source", "Target"),
            panelWidth = 200.dp,
            panelHeight = 220.dp,
            onChange = { next, direction, movedKeys ->
                eventValue = next
                lastDirection = if (direction == ElTransferDirection.Right) "right" else "left"
                lastMoved = movedKeys.joinToString(", ")
            },
            onLeftCheckChange = { checked -> lastChecked = checked.joinToString(", ").ifEmpty { "-" } },
            onRightCheckChange = { checked -> lastChecked = checked.joinToString(", ").ifEmpty { "-" } },
        )
        DemoHint("Direction: $lastDirection · moved: $lastMoved · checked: $lastChecked")
    }

    DemoExample(
        "Custom filter method and clearQuery",
        "Use a custom filter predicate and reset both panel queries through ElTransferState.",
    ) {
        val state = remember { ElTransferState() }
        val options = remember { (1..8).map { ElTransferItem("$it", "Option $it") } }
        var queryValue by remember { mutableStateOf(listOf("2")) }
        ElTransfer(
            data = options,
            value = queryValue,
            onValueChange = { queryValue = it },
            titles = listOf("Source", "Target"),
            filterable = true,
            state = state,
            filterMethod = { query, item ->
                item.key.contains(query) || item.label.contains(query, ignoreCase = true)
            },
            panelWidth = 200.dp,
            panelHeight = 220.dp,
        )
        ElButton(onClick = { state.clearQuery() }, size = ElSize.Small, text = "Clear query")
    }
}

private data class TransferAliasOption(
    val id: String,
    val desc: String,
    val locked: Boolean = false,
)

private val transferAliasOptions = (1..8).map { index ->
    TransferAliasOption("city-$index", "City $index", locked = index % 4 == 0)
}
