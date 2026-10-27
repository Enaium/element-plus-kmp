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
fun ScrollbarDocs() {
    val theme = LocalElTheme.current
    DemoExample("Vertical scrollbar", "A custom scrollbar is displayed while scrolling.") {
        ElScrollbar(height = 140.dp) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(14) { index ->
                    BasicText(
                        "Scrollable content ${index + 1}",
                        Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        style = theme.typography.small,
                    )
                }
            }
        }
    }
    DemoExample("Always visible", "Set always to keep the scrollbar visible.") {
        ElScrollbar(height = 120.dp, always = true) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(10) { BasicText("Row ${it + 1}", Modifier.padding(vertical = 6.dp), style = theme.typography.small) }
            }
        }
    }
    DemoExample("Horizontal scrollbar", "The same component scrolls horizontally.") {
        ElScrollbar(horizontal = true, height = 80.dp, width = 420.dp) {
            Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(10) { index ->
                    Box(
                        Modifier.width(120.dp).height(48.dp).background(
                            LocalElTheme.current.colors.light(ElType.Primary, 8),
                            RoundedCornerShape(4.dp),
                        ),
                        contentAlignment = Alignment.Center,
                    ) { BasicText("Item ${index + 1}", style = theme.typography.small) }
                }
            }
        }
    }

    DemoExample("Max height", "Constrain the viewport height and scroll the overflowing content.") {
        ElScrollbar(height = 140.dp, modifier = Modifier.width(300.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(12) { BasicText("Scrollable row ${it + 1}", style = LocalElTheme.current.typography.base) }
            }
        }
    }
}
