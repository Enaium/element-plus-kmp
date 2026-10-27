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
fun ResultDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Basic usage", "Primary, success, warning, error and info results.") {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ElResult(status = ElResultStatus.Primary, title = "Primary Tip", subTitle = "Please follow the instructions.")
            ElResult(status = ElResultStatus.Success, title = "Success", subTitle = "The operation completed successfully.")
            ElResult(status = ElResultStatus.Warning, title = "Warning", subTitle = "Please check the form fields.")
            ElResult(status = ElResultStatus.Info, title = "Info", subTitle = "This is an informational result.")
            ElResult(status = ElResultStatus.Error, title = "Error", subTitle = "Something went wrong.")
        }
    }
    DemoExample("Customized content", "Add actions below the result.") {
        ElResult(
            status = ElResultStatus.Success,
            title = "Payment complete",
            subTitle = "Your order has been submitted.",
            extra = {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElButton(onClick = {}, text = "View order", type = ElType.Primary)
                    ElButton(onClick = {}, text = "Back home")
                }
            },
        )
    }
    DemoExample("Custom icon, title and subtitle slots", "Replace the illustration and typography with composables.") {
        ElResult(
            icon = {
                ElImage(
                    painter = ColorPainter(colors.warning),
                    modifier = Modifier.size(96.dp),
                )
            },
            titleContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    BasicText("404", style = theme.typography.extraLarge)
                    ElTag("Error", type = ElType.Danger, size = ElSize.Small)
                }
            },
            subTitleContent = {
                BasicText(
                    "Sorry, request error",
                    style = theme.typography.base.copy(color = colors.textSecondary),
                )
            },
            extra = { ElButton(onClick = {}, text = "Back", type = ElType.Primary) },
        )
    }
}
