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
fun ProgressDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Linear progress bar", "Progress can be shown with or without text.") {
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElProgress(percentage = 50.0)
            ElProgress(percentage = 80.0, textInside = true)
        }
    }
    DemoExample("Status", "Use a status to color the progress bar.") {
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElProgress(percentage = 100.0, status = ElProgressStatus.Success)
            ElProgress(percentage = 60.0, status = ElProgressStatus.Warning)
            ElProgress(percentage = 30.0, status = ElProgressStatus.Exception)
        }
    }
    DemoExample("Custom color", "Override the progress color.") {
        ElProgress(percentage = 65.0, color = colors.success)
    }
    DemoExample("Striped", "Striped progress makes activity more visible.") {
        ElProgress(percentage = 70.0, striped = true, status = ElProgressStatus.Warning)
    }
    DemoExample("Circular progress bar", "Circle progress supports a text label in the center.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ElProgress(percentage = 75.0, type = ElProgressType.Circle, width = 110.dp)
            ElProgress(percentage = 42.0, type = ElProgressType.Circle, width = 110.dp, color = colors.warning)
        }
    }
    DemoExample("Dashboard progress bar", "A dashboard progress uses a partial circle.") {
        ElProgress(percentage = 55.0, type = ElProgressType.Dashboard, width = 120.dp)
    }

    DemoExample("Internal percentage", "Place the percentage inside the progress bar.") {
        ElProgress(percentage = 70.0, textInside = true, width = 300.dp)
    }
    DemoExample("Customized content", "Format the progress text with a custom formatter.") {
        ElProgress(percentage = 70.0, format = { "Done ${it.toInt()} / 100" }, width = 300.dp)
    }
}
