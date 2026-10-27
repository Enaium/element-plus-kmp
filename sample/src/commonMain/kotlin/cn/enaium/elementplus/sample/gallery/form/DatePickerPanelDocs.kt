package cn.enaium.elementplus.sample.gallery.form

import cn.enaium.elementplus.components.ElBorder
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
fun DatePickerPanelDocs() {
    var month by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 1)) }
    var year by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 1, 1)) }
    DemoExample("Basic usage", "A calendar panel for selecting a date.") {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePickerPanel(value = date, onValueChange = { date = it })
    }
    DemoExample("Month and year types", "Select a month or a year.") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElDatePickerPanel(value = month, onValueChange = { month = it }, type = ElDatePickerType.Month)
            ElDatePickerPanel(value = year, onValueChange = { year = it }, type = ElDatePickerType.Year)
        }
    }
    DemoExample("Disabled dates", "Weekends cannot be selected.") {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePickerPanel(
            value = date,
            onValueChange = { date = it },
            disabledDate = { it.dayOfWeek.isoDayNumber >= 6 },
        )
    }

    DemoExample("Border", "Wrap the panel in a bordered container.") {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElBorder {
            ElDatePickerPanel(value = date, onValueChange = { date = it }, modifier = Modifier.padding(8.dp))
        }
    }
}
