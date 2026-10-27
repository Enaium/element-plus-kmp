package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.components.ElScrollbar
import cn.enaium.elementplus.components.ElScrollbarDirection
import cn.enaium.elementplus.components.ElScrollbarMask
import cn.enaium.elementplus.components.ElSlider
import cn.enaium.elementplus.components.rememberElScrollbarState
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun ScrollbarDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors

    @Composable
    fun ScrollItem(label: String, horizontal: Boolean) {
        Box(
            Modifier
                .then(if (horizontal) Modifier.width(100.dp) else Modifier.fillMaxWidth())
                .height(50.dp)
                .padding(4.dp)
                .background(colors.light(ElType.Primary, 9), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(label, style = theme.typography.small.copy(color = colors.primary))
        }
    }

    DemoExample("Basic usage", "A custom scrollbar is displayed while scrolling.") {
        ElScrollbar(height = 200.dp) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(14) { index -> ScrollItem("Scrollable content ${index + 1}", false) }
            }
        }
    }
    DemoExample(
        "Scroll on both axes",
        "bothAxis scrolls a grid in both directions; two thumbs track the offsets.",
    ) {
        ElScrollbar(bothAxis = true, height = 180.dp, width = 320.dp) {
            Column(Modifier.padding(8.dp)) {
                repeat(12) { row ->
                    Row(
                        Modifier.padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        repeat(12) { column ->
                            Box(
                                Modifier
                                    .width(90.dp)
                                    .height(36.dp)
                                    .background(colors.light(ElType.Primary, 8), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center,
                            ) { BasicText("$row-$column", style = theme.typography.extraSmall) }
                        }
                    }
                }
            }
        }
    }
    DemoExample("Always visible", "Set always to keep the scrollbar visible.") {
        ElScrollbar(height = 140.dp, always = true) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(10) { index -> ScrollItem("Row ${index + 1}", false) }
            }
        }
    }
    DemoExample(
        "Horizontal scrollbar",
        "Horizontal spaces scroll sideways; the content keeps its intrinsic width.",
    ) {
        ElScrollbar(horizontal = true, height = 80.dp, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(20) { index -> ScrollItem("Item ${index + 1}", horizontal = true) }
            }
        }
    }

    var count by remember { mutableStateOf(3) }
    DemoExample(
        "Max height",
        "maxHeight grows with the content and stops at the limit; adding or deleting items is reflected immediately.",
    ) {
        DemoRow {
            ElButton(onClick = { count++ }, text = "Add Item")
            ElButton(onClick = { if (count > 0) count-- }, text = "Delete Item")
        }
        ElScrollbar(maxHeight = 200.dp, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(count) { index -> ScrollItem("Item ${index + 1}", false) }
            }
        }
    }

    val manualState = rememberElScrollbarState()
    var manualValue by remember { mutableStateOf(0f) }
    DemoExample(
        "Manual scroll",
        "The scrollbar state exposes setScrollTop and reports scroll offsets, so a slider can drive the scroll position.",
    ) {
        ElScrollbar(
            height = 200.dp,
            always = true,
            state = manualState,
            onScroll = { scrollTop, _ -> manualValue = scrollTop },
        ) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(20) { index -> ScrollItem("Item ${index + 1}", false) }
            }
        }
        ElSlider(
            value = manualValue.toDouble(),
            onValueChange = { manualState.setScrollTop(it.toFloat()) },
            min = 0.0,
            max = manualState.maxScrollTop.toDouble().coerceAtLeast(1.0),
            step = 1.0,
            formatTooltip = { "${it.toInt()} px" },
        )
    }

    var endlessCount by remember { mutableStateOf(30) }
    DemoExample(
        "Infinite scroll",
        "The endReached event reports bottom and top edge hits, which is how more content is appended.",
    ) {
        ElScrollbar(
            height = 240.dp,
            distance = 20.dp,
            onEndReached = { direction ->
                if (direction == ElScrollbarDirection.Bottom) endlessCount += 5
            },
        ) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(endlessCount) { index -> ScrollItem("Item ${index + 1}", false) }
            }
        }
        DemoHint("$endlessCount items loaded")
    }

    var mask by remember { mutableStateOf("all") }
    val maskSet: Set<ElScrollbarMask> = when (mask) {
        "all" -> setOf(
            ElScrollbarMask.Top,
            ElScrollbarMask.Bottom,
            ElScrollbarMask.Left,
            ElScrollbarMask.Right,
        )
        "top" -> setOf(ElScrollbarMask.Top)
        "bottom" -> setOf(ElScrollbarMask.Bottom)
        "left" -> setOf(ElScrollbarMask.Left)
        "right" -> setOf(ElScrollbarMask.Right)
        else -> emptySet()
    }
    DemoExample(
        "Scroll mask",
        "The mask fades the edges that still have hidden content, per axis and per edge.",
    ) {
        ElRadioGroup(
            options = listOf(
                ElRadioOption("off", "Off"),
                ElRadioOption("all", "All"),
                ElRadioOption("top", "Top"),
                ElRadioOption("bottom", "Bottom"),
                ElRadioOption("left", "Left"),
                ElRadioOption("right", "Right"),
            ),
            selected = mask,
            onSelectedChange = { mask = it },
        )
        DemoHint("Vertical scrolling")
        ElScrollbar(height = 180.dp, mask = maskSet, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(end = 12.dp)) {
                repeat(20) { index -> ScrollItem("Item ${index + 1}", false) }
            }
        }
        DemoHint("Horizontal scrolling")
        ElScrollbar(horizontal = true, height = 80.dp, mask = maskSet, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(20) { index -> ScrollItem("Item ${index + 1}", horizontal = true) }
            }
        }
    }
}
