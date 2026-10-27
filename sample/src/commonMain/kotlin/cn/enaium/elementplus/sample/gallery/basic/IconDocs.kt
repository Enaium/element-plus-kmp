package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElCard
import cn.enaium.elementplus.components.ElCardShadow
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IconDocs() {
    val colors = LocalElTheme.current.colors
    val icons = listOf(
        "Check" to ElIcons.Check, "Close" to ElIcons.Close, "Search" to ElIcons.Search,
        "Calendar" to ElIcons.Calendar, "Clock" to ElIcons.Clock, "Star" to ElIcons.StarFilled,
        "Setting" to ElIcons.Setting, "User" to ElIcons.User, "Bell" to ElIcons.Bell,
        "Upload" to ElIcons.Upload, "Download" to ElIcons.Download, "Picture" to ElIcons.Picture,
    )
    DemoExample("Basic icons", "Icons are available through the ElIcons object.") {
        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            icons.forEach { (name, icon) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ElIcon(icon, size = 24.dp, tint = colors.textRegular)
                    BasicText(name, style = LocalElTheme.current.typography.extraSmall.copy(color = colors.textSecondary))
                }
            }
        }
    }
    DemoExample("Size and color", "Icons inherit a tint and can be any size.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElIcon(ElIcons.StarFilled, size = 16.dp, tint = colors.primary)
            ElIcon(ElIcons.StarFilled, size = 24.dp, tint = colors.warning)
            ElIcon(ElIcons.StarFilled, size = 32.dp, tint = colors.success)
            ElIcon(ElIcons.StarFilled, size = 48.dp, tint = colors.danger)
        }
    }
    DemoExample("Loading", "Use spin to keep an icon rotating.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElIcon(ElIcons.Loading, size = 20.dp, tint = colors.primary, spin = true)
            ElIcon(ElIcons.Loading, size = 30.dp, tint = colors.success, spin = true)
            BasicText("Loading...", style = LocalElTheme.current.typography.small.copy(color = colors.textSecondary))
        }
    }
}
