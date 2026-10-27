package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun LayoutDocs() {
    val colors = LocalElTheme.current.colors
    val blockColor = colors.light(ElType.Primary, 8)
    val lightBlockColor = colors.light(ElType.Primary, 9)

    @Composable
    fun Block(
        text: String,
        modifier: Modifier = Modifier,
        height: Dp = 36.dp,
        color: Color? = null,
    ) {
        Box(
            modifier.height(height).background(color ?: blockColor, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) { BasicText(text, style = LocalElTheme.current.typography.small.copy(color = colors.primary)) }
    }

    DemoExample("Basic layout", "Rows use a 24-column system.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElRow(gutter = 8.dp) {
                ElRowCol { Block("span=24 (default)", Modifier.fillMaxWidth()) }
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
    DemoExample("Alignment", "justify positions the columns on the row and align their vertical placement.") {
        val alignments = listOf(
            "start" to ElJustify.Start,
            "center" to ElJustify.Center,
            "end" to ElJustify.End,
            "space-between" to ElJustify.SpaceBetween,
            "space-around" to ElJustify.SpaceAround,
            "space-evenly" to ElJustify.SpaceEvenly,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            alignments.forEach { (label, justify) ->
                ElRow(gutter = 8.dp, justify = justify, modifier = Modifier.height(36.dp)) {
                    ElRowCol(span = 6) { Block(label, Modifier.fillMaxWidth()) }
                    ElRowCol(span = 6) { Block(label, Modifier.fillMaxWidth(), color = lightBlockColor) }
                    ElRowCol(span = 6) { Block(label, Modifier.fillMaxWidth()) }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("top" to ElAlign.Top, "center" to ElAlign.Center, "bottom" to ElAlign.Bottom).forEach { (label, align) ->
                    ElRow(
                        gutter = 8.dp,
                        align = align,
                        modifier = Modifier.weight(1f).height(80.dp),
                    ) {
                        ElRowCol(span = 8) { Block(label, Modifier.fillMaxWidth(), height = 24.dp) }
                        ElRowCol(span = 8) {
                            Block("height 40", Modifier.fillMaxWidth(), height = 40.dp, color = lightBlockColor)
                        }
                        ElRowCol(span = 8) { Block("height 56", Modifier.fillMaxWidth(), height = 56.dp) }
                    }
                }
            }
        }
    }
    DemoExample("Responsive layout", "xs/sm/md/lg/xl override the span from their breakpoint upwards.") {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val width = maxWidth
            val breakpoint = when {
                width == Dp.Infinity -> "unbounded"
                width >= ElBreakpoint.Xl.minWidth -> "xl (>=1920px)"
                width >= ElBreakpoint.Lg.minWidth -> "lg (>=1200px)"
                width >= ElBreakpoint.Md.minWidth -> "md (>=992px)"
                width >= ElBreakpoint.Sm.minWidth -> "sm (>=768px)"
                else -> "xs (<768px)"
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoHint("Current breakpoint: $breakpoint")
                ElRow(gutter = 10.dp) {
                    ElRowCol(
                        span = 24,
                        xs = ElColSize(span = 8),
                        sm = ElColSize(span = 6),
                        md = ElColSize(span = 4),
                        lg = ElColSize(span = 3),
                        xl = ElColSize(span = 1),
                    ) { Block("xs=8 sm=6 md=4 lg=3 xl=1", Modifier.fillMaxWidth()) }
                    ElRowCol(
                        span = 24,
                        xs = ElColSize(span = 4),
                        sm = ElColSize(span = 6),
                        md = ElColSize(span = 8),
                        lg = ElColSize(span = 9),
                        xl = ElColSize(span = 11),
                    ) { Block("xs=4 sm=6 md=8 lg=9 xl=11", Modifier.fillMaxWidth(), color = lightBlockColor) }
                    ElRowCol(
                        span = 24,
                        xs = ElColSize(span = 4),
                        sm = ElColSize(span = 6),
                        md = ElColSize(span = 8),
                        lg = ElColSize(span = 9),
                        xl = ElColSize(span = 11),
                    ) { Block("xs=4 sm=6 md=8 lg=9 xl=11", Modifier.fillMaxWidth()) }
                    ElRowCol(
                        span = 24,
                        xs = ElColSize(span = 8),
                        sm = ElColSize(span = 6),
                        md = ElColSize(span = 4),
                        lg = ElColSize(span = 3),
                        xl = ElColSize(span = 1),
                    ) { Block("xs=8 sm=6 md=4 lg=3 xl=1", Modifier.fillMaxWidth(), color = lightBlockColor) }
                }
            }
        }
    }
    DemoExample("Hybrid layout", "Combine spans on a single row for asymmetric layouts.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElRow(gutter = 8.dp) {
                ElRowCol(span = 16) { Block("span=16", Modifier.fillMaxWidth()) }
                ElRowCol(span = 8) { Block("span=8", Modifier.fillMaxWidth(), color = lightBlockColor) }
            }
            ElRow(gutter = 8.dp) {
                ElRowCol(span = 8) { Block("span=8", Modifier.fillMaxWidth()) }
                ElRowCol(span = 8) { Block("span=8", Modifier.fillMaxWidth(), color = lightBlockColor) }
                ElRowCol(span = 4) { Block("4", Modifier.fillMaxWidth()) }
                ElRowCol(span = 4) { Block("4", Modifier.fillMaxWidth(), color = lightBlockColor) }
            }
            ElRow(gutter = 8.dp) {
                ElRowCol(span = 4) { Block("4", Modifier.fillMaxWidth()) }
                ElRowCol(span = 16) { Block("span=16", Modifier.fillMaxWidth(), color = lightBlockColor) }
                ElRowCol(span = 4) { Block("4", Modifier.fillMaxWidth()) }
            }
        }
    }
    DemoExample("Push and pull", "push shifts a column right and pull shifts it left without reordering.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElRow(gutter = 8.dp, modifier = Modifier.height(36.dp)) {
                ElRowCol(span = 18, push = 6) { Block("span=18 push=6", Modifier.fillMaxWidth()) }
                ElRowCol(span = 6, pull = 18) {
                    Block("span=6 pull=18", Modifier.fillMaxWidth(), color = lightBlockColor)
                }
            }
            ElRow(gutter = 8.dp, modifier = Modifier.height(36.dp)) {
                ElRowCol(span = 6, push = 18) { Block("span=6 push=18", Modifier.fillMaxWidth()) }
                ElRowCol(span = 12) { Block("span=12", Modifier.fillMaxWidth(), color = lightBlockColor) }
            }
        }
    }
}
