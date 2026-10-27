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
fun SpaceDocs() {
    val colors = LocalElTheme.current.colors
    val theme = LocalElTheme.current
    @Composable
    fun Item(text: String) {
        Box(
            Modifier.background(colors.light(ElType.Primary, 8), RoundedCornerShape(4.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
        ) { BasicText(text, style = theme.typography.small) }
    }
    DemoExample("Basic spacing", "Space provides a consistent gap between elements.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSpace(size = ElSpaceSize.Small) { repeat(3) { Item("Item ${it + 1}") } }
            ElSpace(size = ElSpaceSize.Default) { repeat(3) { Item("Item ${it + 1}") } }
            ElSpace(size = ElSpaceSize.Large) { repeat(3) { Item("Item ${it + 1}") } }
        }
    }
    DemoExample("Vertical layout", "Set vertical to arrange items in a column.") {
        ElSpace(vertical = true) {
            repeat(3) { Item("Vertical ${it + 1}") }
        }
    }
    DemoExample("Auto wrapping", "Wrap items onto new lines when space is limited.") {
        ElSpace(wrap = true) {
            repeat(10) { Item("Wrapped item ${it + 1}") }
        }
    }
    DemoExample("Fill", "Fill makes the space occupy its parent width.") {
        ElSpace(fill = true, size = ElSpaceSize.Large) {
            Item("Start")
            Item("Middle")
            Item("End")
        }
    }

    DemoExample("Alignment", "Align horizontal spaces at the start, center or end.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSpace(fill = true, horizontalAlignment = Alignment.Start) { repeat(3) { Item("Start") } }
            ElSpace(fill = true, horizontalAlignment = Alignment.CenterHorizontally) { repeat(3) { Item("Center") } }
            ElSpace(fill = true, horizontalAlignment = Alignment.End) { repeat(3) { Item("End") } }
        }
    }

    DemoExample("Customized size", "Override the spacing with an arbitrary size.") {
        ElSpace(spacing = 24.dp) { repeat(3) { Item("Custom") } }
    }

    DemoExample("Spacer", "Use a fixed-size spacer between arbitrary items.") {
        ElSpace {
            Item("Left")
            ElSpaceItem(Modifier.width(40.dp)) {}
            Item("Right")
        }
    }
}
