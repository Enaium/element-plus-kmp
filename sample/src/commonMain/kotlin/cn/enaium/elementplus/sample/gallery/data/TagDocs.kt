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
fun TagDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Tags support semantic types.") {
        DemoRow {
            ElTag("Primary")
            ElTag("Success", type = ElType.Success)
            ElTag("Info", type = ElType.Info)
            ElTag("Warning", type = ElType.Warning)
            ElTag("Danger", type = ElType.Danger)
        }
    }
    DemoExample("Effects", "Choose light, dark or plain effects.") {
        DemoRow {
            ElTag("Light", effect = ElTagEffect.Light)
            ElTag("Dark", effect = ElTagEffect.Dark)
            ElTag("Plain", type = ElType.Success, effect = ElTagEffect.Plain)
        }
    }
    DemoExample("Rounded and sizes", "Use a pill shape or a smaller size.") {
        DemoRow {
            ElTag("Round", round = true)
            ElTag("Large", size = ElSize.Large)
            ElTag("Small", size = ElSize.Small)
        }
    }
    DemoExample("Removable", "Add a close icon and handle removal.") {
        var tags by remember { mutableStateOf(listOf("Kotlin", "Compose", "Multiplatform")) }
        DemoRow {
            tags.forEach { tag ->
                ElTag(
                    text = tag,
                    closable = true,
                    onClose = { tags = tags - tag },
                )
            }
        }
    }
    DemoExample("Custom color", "Set a custom tag color.") {
        DemoRow {
            ElTag("Custom", color = colors.warning)
            ElTag("Green", color = colors.success)
            ElTag("Purple", color = Color(0xFF8E44AD))
        }
    }

    DemoExample("Checkable tag", "Toggle a tag selection with state.") {
        var checked by remember { mutableStateOf(true) }
        ElTag(
            text = if (checked) "Checked" else "Unchecked",
            type = if (checked) ElType.Primary else ElType.Info,
            effect = if (checked) ElTagEffect.Dark else ElTagEffect.Light,
            onClick = { checked = !checked },
        )
    }
}
