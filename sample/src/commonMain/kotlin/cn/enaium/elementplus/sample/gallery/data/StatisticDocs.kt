package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun StatisticDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Statistic formats a numeric value for display.") {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            ElStatistic(value = 128945.0, title = "Revenue", prefix = "$")
            ElStatistic(value = 1024.0, title = "Downloads")
        }
    }
    DemoExample("Precision and grouping", "Control group separators and decimals.") {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            ElStatistic(value = 1234567.89, title = "Amount", precision = 2)
            ElStatistic(value = 987654.0, title = "No grouping", groupSeparator = "")
        }
    }
    DemoExample("Prefix and suffix", "Add units before or after the value.") {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            ElStatistic(value = 98.6, title = "Satisfaction", precision = 1, suffix = "%")
            ElStatistic(value = 24.0, title = "Duration", suffix = "h")
        }
    }
    DemoExample("Custom formatter and color", "Format values yourself and override the color.") {
        ElStatistic(
            value = 0.725,
            title = "Conversion",
            formatter = { "${(it * 100).toInt()}%" },
            color = colors.success,
        )
    }

    DemoExample("Card usage", "Place statistics inside a card.") {
        ElCard(header = { BasicText("Weekly sales", style = LocalElTheme.current.typography.medium) }) {
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                ElStatistic(value = 1280.0, title = "Orders")
                ElStatistic(value = 86400.0, title = "Revenue", prefix = "$", precision = 2)
            }
        }
    }
}
