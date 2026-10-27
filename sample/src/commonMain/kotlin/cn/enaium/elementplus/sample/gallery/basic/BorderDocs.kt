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
import androidx.compose.foundation.layout.size
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
fun BorderDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Border", "Borders use the border-color tokens from the theme.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElBorder { BasicText("Default", Modifier.padding(16.dp), style = theme.typography.base) }
            ElBorder(color = colors.primary, radius = 4.dp) {
                BasicText("Primary", Modifier.padding(16.dp), style = theme.typography.base)
            }
            ElBorder(color = colors.success, dashed = true) {
                BasicText("Dashed", Modifier.padding(16.dp), style = theme.typography.base)
            }
            ElBorder(color = colors.danger, radius = 12.dp) {
                BasicText("Radius 12", Modifier.padding(16.dp), style = theme.typography.base)
            }
        }
    }
    DemoExample("Radius", "Use the radius tokens or a custom radius.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf(0.dp, 2.dp, 4.dp, 12.dp, 20.dp).forEach { radius ->
                ElBorder(radius = radius, modifier = Modifier.padding(4.dp)) {
                    BasicText("${radius.value.toInt()}dp", Modifier.padding(horizontal = 20.dp, vertical = 14.dp), style = theme.typography.small)
                }
            }
        }
    }
    DemoExample("Shadow", "Cards support always, hover and never shadow modes.") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElCard(shadow = ElCardShadow.Always, modifier = Modifier.width(160.dp)) {
                BasicText("Always", style = theme.typography.base)
            }
            ElCard(shadow = ElCardShadow.Hover, modifier = Modifier.width(160.dp)) {
                BasicText("Hover", style = theme.typography.base)
            }
            ElCard(shadow = ElCardShadow.Never, modifier = Modifier.width(160.dp)) {
                BasicText("Never", style = theme.typography.base)
            }
        }
    }

    DemoExample("Border style", "Choose a solid, dashed or dotted stroke.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElBorder(Modifier.size(150.dp, 56.dp), borderStyle = ElBorderStyle.Solid) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { BasicText("Solid") }
            }
            ElBorder(
                Modifier.size(150.dp, 56.dp),
                borderStyle = ElBorderStyle.Dashed,
                color = LocalElTheme.current.colors.success,
                width = 2.dp,
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { BasicText("Dashed") }
            }
            ElBorder(
                Modifier.size(150.dp, 56.dp),
                borderStyle = ElBorderStyle.Dotted,
                color = LocalElTheme.current.colors.primary,
                width = 2.dp,
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { BasicText("Dotted") }
            }
        }
    }
}
