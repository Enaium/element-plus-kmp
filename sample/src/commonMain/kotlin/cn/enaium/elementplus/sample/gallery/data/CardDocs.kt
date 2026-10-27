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
fun CardDocs() {
    val theme = LocalElTheme.current
    DemoExample("Basic usage", "Cards provide header, body and footer slots.") {
        ElCard(
            modifier = Modifier.width(360.dp),
            header = { BasicText("Card header", style = theme.typography.medium) },
            footer = { ElButton(onClick = {}, text = "Action", size = ElSize.Small, type = ElType.Primary) },
        ) {
            BasicText("A card body with a header and a footer.", style = theme.typography.base)
        }
    }
    DemoExample("Simple card", "Omit the header and footer for a simple surface.") {
        ElCard(modifier = Modifier.width(280.dp)) {
            BasicText("Simple card content", style = theme.typography.base)
        }
    }
    DemoExample("Shadow", "Control the card shadow behavior.") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElCard(shadow = ElCardShadow.Always, modifier = Modifier.width(160.dp)) { BasicText("Always") }
            ElCard(shadow = ElCardShadow.Hover, modifier = Modifier.width(160.dp)) { BasicText("Hover") }
            ElCard(shadow = ElCardShadow.Never, modifier = Modifier.width(160.dp)) { BasicText("Never") }
        }
    }
    DemoExample("With images", "Cards can contain images and rich content.") {
        ElCard(modifier = Modifier.width(300.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ElImage(
                    painter = ColorPainter(theme.colors.light(ElType.Primary, 7)),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                )
                BasicText("Image card", style = theme.typography.medium)
                BasicText("A short description under the image.", style = theme.typography.small.copy(color = theme.colors.textSecondary))
            }
        }
    }
}
