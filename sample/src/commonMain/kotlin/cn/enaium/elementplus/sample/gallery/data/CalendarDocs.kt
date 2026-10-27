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
fun CalendarDocs() {
    DemoExample("Basic usage", "A month calendar with previous, next and today actions.") {
        var basic by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(value = basic, onValueChange = { basic = it })
        DemoHint("Selected: $basic")
    }
    DemoExample("Custom header", "Provide a custom header to control navigation.") {
        var headerDate by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = headerDate,
            onValueChange = { headerDate = it },
            header = { month, previous, next, today ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText("${month.year} / ${month.month.number}", style = LocalElTheme.current.typography.base)
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    ElButton(onClick = previous, text = "Prev", size = ElSize.Small)
                    androidx.compose.foundation.layout.Spacer(Modifier.width(6.dp))
                    ElButton(onClick = today, text = "Today", size = ElSize.Small)
                    androidx.compose.foundation.layout.Spacer(Modifier.width(6.dp))
                    ElButton(onClick = next, text = "Next", size = ElSize.Small)
                }
            },
        )
    }
    DemoExample("Custom date cell", "Render custom content inside each date cell.") {
        var custom by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = custom,
            onValueChange = { custom = it },
            dateCell = { date, selected, inMonth ->
                val theme = LocalElTheme.current
                Column(
                    Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    BasicText(
                        date.day.toString(),
                        style = theme.typography.small.copy(
                            color = when {
                                !inMonth -> theme.colors.textDisabled
                                selected -> theme.colors.primary
                                else -> theme.colors.textRegular
                            },
                        ),
                    )
                    if (date.day % 5 == 0) {
                        BasicText("Event", style = theme.typography.extraSmall.copy(color = theme.colors.success))
                    }
                }
            },
        )
    }
}
