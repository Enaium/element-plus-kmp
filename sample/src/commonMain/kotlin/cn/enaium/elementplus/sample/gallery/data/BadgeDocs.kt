package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import cn.enaium.elementplus.icons.ElIcon
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
fun BadgeDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Attach a badge to content.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(value = "12") { ElButton(onClick = {}, text = "Messages") }
            ElBadge(value = "3") { ElButton(onClick = {}, text = "Notifications") }
        }
    }
    DemoExample("Max", "Large values collapse to max+.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(value = "200", max = 99) { ElButton(onClick = {}, text = "Inbox") }
            ElBadge(value = "999", max = 999) { ElButton(onClick = {}, text = "Archive") }
        }
    }
    DemoExample("Dot", "A dot badge shows that something changed.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(isDot = true) { ElButton(onClick = {}, text = "Updates") }
            ElBadge(value = "new", isDot = false) { ElButton(onClick = {}, text = "News") }
        }
    }
    DemoExample("Custom color", "Override the badge color.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(value = "9", color = colors.success) { ElButton(onClick = {}, text = "Success") }
            ElBadge(value = "!", color = colors.warning) { ElButton(onClick = {}, text = "Warning") }
        }
    }
    DemoExample("Custom content", "Replace the badge text with custom markup such as an icon and value.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(value = "new") { ElButton(onClick = {}, text = "Comments") }
            ElBadge(
                value = "99",
                badgeContent = { value ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ElIcon(ElIcons.Message, size = 12.dp, tint = colors.white)
                        BasicText(
                            value,
                            style = LocalElTheme.current.typography.extraSmall.copy(color = colors.white),
                        )
                    }
                },
            ) { ElButton(onClick = {}, text = "Messages") }
        }
    }
    DemoExample("Custom badge style", "Restyle the badge surface with badgeModifier.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(
                value = "5",
                badgeModifier = Modifier.border(2.dp, colors.white, RoundedCornerShape(50)),
            ) { ElButton(onClick = {}, text = "Bordered") }
            ElBadge(
                value = "hot",
                color = colors.warning,
                badgeModifier = Modifier.border(1.dp, colors.danger, RoundedCornerShape(50)),
            ) { ElButton(onClick = {}, text = "Outlined") }
        }
    }
    DemoExample("Standalone", "Badges can be used without attached content.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ElBadge(value = "1")
            ElBadge(value = "25", type = ElType.Success)
            ElBadge(isDot = true)
        }
    }

    DemoExample("Offset", "Offset the badge from the corner.") {
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
            ElBadge(value = "3", offsetX = 12.dp, offsetY = (-2).dp) {
                ElButton(onClick = {}, text = "Offset X")
            }
            ElBadge(value = "3", offsetX = 0.dp, offsetY = 10.dp) {
                ElButton(onClick = {}, text = "Offset Y")
            }
        }
    }
}
