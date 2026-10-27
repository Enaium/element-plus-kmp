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
fun TourDocs() {
    DemoExample("Basic usage", "Guide users through a sequence of steps.") {
        TourSample()
    }
    DemoExample("Custom content", "Steps can contain arbitrary composables.") {
        TourSample(withCustomContent = true)
    }
    DemoExample("Without mask", "Disable the dark mask to keep the page visible.") {
        TourSample(showMask = false)
    }
}

@Composable
private fun TourSample(withCustomContent: Boolean = false, showMask: Boolean = true) {
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(0) }
    val steps = remember(withCustomContent) {
        listOf(
            ElTourStep(
                title = "Welcome",
                description = "This is the first step of the guide.",
                content = if (withCustomContent) {
                    { BasicText("Custom content can be placed inside a tour step.", style = LocalElTheme.current.typography.small) }
                } else null,
            ),
            ElTourStep("Components", "Use the sidebar to browse component examples."),
            ElTourStep("Finish", "You have reached the end of the tour."),
        )
    }
    ElButton(onClick = { current = 0; visible = true }, text = "Start tour", type = ElType.Primary)
    ElTour(
        visible = visible,
        steps = steps,
        current = current,
        onCurrentChange = { current = it },
        onClose = { visible = false },
        showMask = showMask,
        onFinish = { ElMessage.show("Tour finished", ElMessageType.Success) },
    )
}
