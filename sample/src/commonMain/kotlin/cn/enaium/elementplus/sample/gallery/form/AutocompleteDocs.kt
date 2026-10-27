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
fun AutocompleteDocs() {
    var basic by remember { mutableStateOf("") }
    var remote by remember { mutableStateOf("") }
    val suggestions = listOf("Kotlin", "Kotlin Multiplatform", "Compose Multiplatform", "Ktor", "Kotlinx Coroutines")
    DemoExample("Basic usage", "Type to fetch suggestions from a function.") {
        ElAutocomplete(
            value = basic,
            onValueChange = { basic = it },
            fetchSuggestions = { query -> suggestions.filter { it.contains(query, ignoreCase = true) } },
            placeholder = "Type a framework",
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Value: $basic")
    }
    DemoExample("Remote search", "The fetch function can represent an async remote query.") {
        ElAutocomplete(
            value = remote,
            onValueChange = { remote = it },
            fetchSuggestions = { query ->
                if (query.isBlank()) emptyList()
                else listOf("$query in Kotlin", "$query in Compose", "$query in Element Plus")
            },
            placeholder = "Search anything",
            debounce = 500,
            emptyText = "No matching data",
            modifier = Modifier.width(300.dp),
        )
    }
    DemoExample("Disabled", "Disable the autocomplete input.") {
        ElAutocomplete(
            value = "Kotlin",
            onValueChange = {},
            fetchSuggestions = { emptyList() },
            disabled = true,
            modifier = Modifier.width(300.dp),
        )
    }
}
