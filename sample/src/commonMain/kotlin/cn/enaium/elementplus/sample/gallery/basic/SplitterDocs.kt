package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElSplitter
import cn.enaium.elementplus.components.ElSplitterDirection
import cn.enaium.elementplus.components.ElSplitterPanel
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun SplitterDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors

    @Composable
    fun PanelBox(label: String, background: Color) {
        Box(Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
            BasicText(label, style = theme.typography.small)
        }
    }

    DemoExample(
        "Basic splitter",
        "Drag the dividers to resize the panes. The first panel has a fixed initial size, the second one a minimum size.",
    ) {
        ElSplitter(
            panels = listOf(
                ElSplitterPanel(size = 120.dp),
                ElSplitterPanel(min = 80.dp),
            ),
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) { index ->
            PanelBox(
                "Panel ${index + 1}",
                if (index == 0) colors.light(ElType.Primary, 8) else colors.light(ElType.Success, 8),
            )
        }
    }

    DemoExample("Vertical layout", "Set the direction to vertical to stack the panes.") {
        ElSplitter(
            panels = listOf(ElSplitterPanel(), ElSplitterPanel()),
            direction = ElSplitterDirection.Vertical,
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) { index ->
            PanelBox("Row ${index + 1}", colors.light(ElType.Warning, 8))
        }
    }

    DemoExample(
        "Panel size",
        "A panel can start at a fixed size and be constrained between min and max. The resize events report the bar index and the panel sizes in pixels.",
    ) {
        var size by remember { mutableStateOf(100f) }
        var lastEvent by remember { mutableStateOf("drag a bar") }
        ElSplitter(
            panels = listOf(
                ElSplitterPanel(),
                ElSplitterPanel(size = size.dp, min = 50.dp, max = 200.dp),
                ElSplitterPanel(),
            ),
            modifier = Modifier.fillMaxWidth().height(220.dp),
            onResizeStart = { index, sizes ->
                size = sizes.getOrElse(1) { size }
                lastEvent = "resizeStart($index)"
            },
            onResize = { index, sizes ->
                size = sizes.getOrElse(1) { size }
                lastEvent = "resize($index, ${sizes.map { it.toInt() }})"
            },
            onResizeEnd = { index, sizes ->
                size = sizes.getOrElse(1) { size }
                lastEvent = "resizeEnd($index)"
            },
        ) { index ->
            PanelBox(
                if (index == 1) "${size.toInt()}px" else "Panel ${index + 1}",
                colors.light(ElType.Primary, 7 + index),
            )
        }
        DemoHint(lastEvent)
    }

    var collapsible by remember { mutableStateOf(true) }
    DemoExample(
        "Collapsible panels",
        "Panels marked collapsible can be collapsed by double-tapping the adjacent bar; the collapse event reports the bar index and side.",
    ) {
        var collapseInfo by remember { mutableStateOf("double-tap a bar to collapse a panel") }
        DemoRow {
            ElSwitch(
                checked = collapsible,
                onCheckedChange = { collapsible = it },
                activeText = "enable",
                inactiveText = "disable",
                inlinePrompt = true,
            )
            DemoHint(collapseInfo)
        }
        ElSplitter(
            panels = listOf(
                ElSplitterPanel(collapsible = collapsible, min = 50.dp),
                ElSplitterPanel(collapsible = collapsible),
                ElSplitterPanel(),
                ElSplitterPanel(collapsible = collapsible),
            ),
            modifier = Modifier.fillMaxWidth().height(240.dp),
            onCollapse = { index, type, sizes ->
                collapseInfo = "collapse($index, $type, ${sizes.map { it.toInt() }})"
            },
        ) { index ->
            if (index == 3) {
                ElSplitter(
                    panels = listOf(
                        ElSplitterPanel(collapsible = collapsible),
                        ElSplitterPanel(collapsible = collapsible),
                    ),
                    direction = ElSplitterDirection.Vertical,
                ) { nested ->
                    PanelBox("Panel ${nested + 4}", colors.light(ElType.Info, 8))
                }
            } else {
                PanelBox("Panel ${index + 1}", colors.light(ElType.Primary, 7 + index))
            }
        }
    }

    var lazySplit by remember { mutableStateOf(false) }
    DemoExample(
        "Lazy panel sizes",
        "With lazy the pixel sizes are rescaled when the splitter is resized; without it the panes keep their pixel sizes.",
    ) {
        DemoRow {
            ElSwitch(
                checked = lazySplit,
                onCheckedChange = { lazySplit = it },
                activeText = "enable",
                inactiveText = "disable",
                inlinePrompt = true,
            )
            DemoHint("Resize the window to see the difference")
        }
        ElSplitter(
            panels = listOf(
                ElSplitterPanel(collapsible = true, min = 50.dp),
                ElSplitterPanel(collapsible = true),
                ElSplitterPanel(collapsible = true),
            ),
            lazy = lazySplit,
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) { index ->
            PanelBox("Panel ${index + 1}", colors.light(ElType.Success, 7 + index))
        }
    }

    var resizable by remember { mutableStateOf(false) }
    DemoExample(
        "Disable drag",
        "A panel that is not resizable disables the bars next to it, while the rest of the layout stays interactive.",
    ) {
        DemoRow {
            ElSwitch(
                checked = resizable,
                onCheckedChange = { resizable = it },
                activeText = "enable",
                inactiveText = "disable",
                inlinePrompt = true,
            )
        }
        ElSplitter(
            panels = listOf(
                ElSplitterPanel(),
                ElSplitterPanel(resizable = resizable),
                ElSplitterPanel(),
            ),
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) { index ->
            PanelBox(
                if (index == 1) "drag ${if (resizable) "enable" else "disable"}" else "Panel ${index + 1}",
                colors.light(ElType.Warning, 7 + index),
            )
        }
    }

    DemoExample(
        "Custom split bar",
        "splitBarContent renders custom content inside every drag bar.",
    ) {
        ElSplitter(
            panels = listOf(ElSplitterPanel(), ElSplitterPanel(), ElSplitterPanel()),
            modifier = Modifier.fillMaxWidth().height(200.dp),
            splitBarContent = { index ->
                Box(
                    Modifier.fillMaxSize().padding(vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        "${index + 1}",
                        style = theme.typography.extraSmall.copy(color = colors.primary),
                    )
                }
            },
        ) { index ->
            PanelBox("Panel ${index + 1}", colors.light(ElType.Primary, 7 + index))
        }
    }
}
