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
fun LayoutDocs() {
    val colors = LocalElTheme.current.colors
    val blockColor = colors.light(ElType.Primary, 8)
    @Composable
    fun Block(text: String, modifier: Modifier = Modifier) {
        Box(
            modifier.height(36.dp).background(blockColor, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) { BasicText(text, style = LocalElTheme.current.typography.small.copy(color = colors.primary)) }
    }
    DemoExample("Basic layout", "Rows use a 24-column system.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElRow(gutter = 8.dp) {
                ElRowCol(span = 24) { Block("span=24", Modifier.fillMaxWidth()) }
            }
            ElRow(gutter = 8.dp) {
                ElRowCol(span = 12) { Block("span=12", Modifier.fillMaxWidth()) }
                ElRowCol(span = 12) { Block("span=12", Modifier.fillMaxWidth()) }
            }
            ElRow(gutter = 8.dp) {
                repeat(4) { ElRowCol(span = 6) { Block("span=6", Modifier.fillMaxWidth()) } }
            }
        }
    }
    DemoExample("Column offset", "Use offset to leave empty columns before a span.") {
        ElRow(gutter = 8.dp) {
            ElRowCol(span = 6, offset = 6) { Block("offset=6 span=6", Modifier.fillMaxWidth()) }
            ElRowCol(span = 6) { Block("span=6", Modifier.fillMaxWidth()) }
        }
    }
    DemoExample("Gutter and alignment", "Gutter controls spacing; justify controls the row alignment.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElRow(gutter = 16.dp) {
                ElRowCol(span = 6) { Block("gutter=16", Modifier.fillMaxWidth()) }
                ElRowCol(span = 6) { Block("gutter=16", Modifier.fillMaxWidth()) }
                ElRowCol(span = 6) { Block("gutter=16", Modifier.fillMaxWidth()) }
            }
            ElRow(gutter = 8.dp, justify = ElJustify.Center) {
                ElRowCol(span = 4) { Block("centered", Modifier.fillMaxWidth()) }
                ElRowCol(span = 4) { Block("centered", Modifier.fillMaxWidth()) }
            }
        }
    }
}
