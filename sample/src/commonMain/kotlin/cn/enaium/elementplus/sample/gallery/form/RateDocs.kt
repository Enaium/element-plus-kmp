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
fun RateDocs() {
    var half by remember { mutableStateOf(3.5) }
    var clearable by remember { mutableStateOf(2.0) }
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Click a star to set the rating.") {
        var basic by remember { mutableStateOf(3.0) }
        ElRate(value = basic, onValueChange = { basic = it })
        DemoHint("Rating: $basic")
    }
    DemoExample("Allow half", "Half stars can be selected.") {
        ElRate(value = half, onValueChange = { half = it }, allowHalf = true)
    }
    DemoExample("Clearable", "Click the selected star again to clear the rating.") {
        ElRate(value = clearable, onValueChange = { clearable = it }, clearable = true)
    }
    DemoExample("Texts and colors", "Provide labels and threshold colors.") {
        var basic by remember { mutableStateOf(3.0) }
        ElRate(
            value = basic,
            onValueChange = { basic = it },
            showText = true,
            texts = listOf("Terrible", "Poor", "Average", "Good", "Excellent"),
            colors = listOf(colors.info, colors.warning, colors.danger),
        )
    }
    DemoExample("Show score", "Display the numeric score instead of a label.") {
        var basic by remember { mutableStateOf(3.0) }
        ElRate(value = basic, onValueChange = { basic = it }, showScore = true)
    }
    DemoExample("Sizes and disabled", "Rates are available in three sizes.") {
        var large by remember { mutableStateOf(3.0) }
        var small by remember { mutableStateOf(3.0) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElRate(value = large, onValueChange = { large = it }, size = ElSize.Large)
            ElRate(value = small, onValueChange = { small = it }, size = ElSize.Small)
            ElRate(value = 4.0, onValueChange = {}, disabled = true)
        }
    }

    DemoExample("Readonly", "A readonly rate displays a value without allowing changes.") {
        ElRate(value = 3.5, onValueChange = {}, readOnly = true)
    }

    DemoExample("Sizes", "Rates scale with the size token.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElRate(value = 3.0, onValueChange = {}, size = ElSize.Large)
            ElRate(value = 3.0, onValueChange = {}, size = ElSize.Default)
            ElRate(value = 3.0, onValueChange = {}, size = ElSize.Small)
        }
    }
}
