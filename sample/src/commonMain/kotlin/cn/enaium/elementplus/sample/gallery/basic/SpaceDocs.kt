package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import cn.enaium.elementplus.components.ElCard
import cn.enaium.elementplus.components.ElDivider
import cn.enaium.elementplus.components.ElDividerDirection
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.components.ElSlider
import cn.enaium.elementplus.components.ElSpace
import cn.enaium.elementplus.components.ElSpaceSize
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun SpaceDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors

    @Composable
    fun Item(text: String) {
        Box(
            Modifier
                .background(colors.light(ElType.Primary, 8), RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) { BasicText(text, style = theme.typography.small) }
    }

    @Composable
    fun CardItem(title: String) {
        ElCard(
            header = { BasicText(title, style = theme.typography.small) },
            contentPadding = 12.dp,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { BasicText("List item ${it + 1}", style = theme.typography.extraSmall) }
            }
        }
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

    var fill by remember { mutableStateOf(true) }
    DemoExample("Fill", "Fill makes the items share the available width; switch it off to see the natural sizes.") {
        DemoRow {
            ElSwitch(checked = fill, onCheckedChange = { fill = it })
            DemoHint("fill = $fill")
        }
        ElSpace(fill = fill, wrap = true) {
            repeat(3) { CardItem("Card name") }
        }
    }

    var fillDirection by remember { mutableStateOf("horizontal") }
    var fillRatio by remember { mutableStateOf(30.0) }
    DemoExample(
        "Fill ratio",
        "fillRatio is the minimum width of every item in percent of the container. With 30 the items line up three per row; with 100 each item takes a whole line.",
    ) {
        DemoRow {
            ElRadioGroup(
                options = listOf(ElRadioOption("horizontal", "horizontal"), ElRadioOption("vertical", "vertical")),
                selected = fillDirection,
                onSelectedChange = { fillDirection = it },
            )
            DemoHint("fillRatio = ${fillRatio.toInt()}")
        }
        ElSlider(value = fillRatio, onValueChange = { fillRatio = it }, min = 10.0, max = 100.0, step = 5.0)
        ElSpace(
            fill = true,
            wrap = true,
            fillRatio = fillRatio.toFloat(),
            vertical = fillDirection == "vertical",
            modifier = Modifier.fillMaxWidth(),
        ) {
            repeat(5) { CardItem("Card name") }
        }
    }

    var controlSize by remember { mutableStateOf(ElSpaceSize.Default) }
    DemoExample(
        "Control size",
        "Change the gap at runtime; the values match the small, default and large spacing tokens.",
    ) {
        ElSpace(vertical = true, horizontalAlignment = Alignment.Start, spacing = 30.dp) {
            ElRadioGroup(
                options = listOf(
                    ElRadioOption("large", "Large"),
                    ElRadioOption("default", "Default"),
                    ElRadioOption("small", "Small"),
                ),
                selected = when (controlSize) {
                    ElSpaceSize.Large -> "large"
                    ElSpaceSize.Default -> "default"
                    ElSpaceSize.Small -> "small"
                },
                onSelectedChange = {
                    controlSize = when (it) {
                        "large" -> ElSpaceSize.Large
                        "default" -> ElSpaceSize.Default
                        else -> ElSpaceSize.Small
                    }
                },
            )
            ElSpace(wrap = true, size = controlSize) {
                repeat(3) { index ->
                    Box(Modifier.width(160.dp)) { CardItem("Card ${index + 1}") }
                }
            }
        }
    }

    DemoExample(
        "Literal spacer",
        "A string spacer is rendered between every two items, like spacer=\"|\" in the original component.",
    ) {
        ElSpace(
            spacing = 10.dp,
            spacer = { BasicText("|", style = theme.typography.base.copy(color = colors.textPlaceholder)) },
        ) {
            ElButton(onClick = {}, text = "Button 1")
            ElButton(onClick = {}, text = "Button 2")
        }
    }
    DemoExample(
        "Component spacer",
        "Any composable can be the spacer; a vertical divider reproduces the vnode spacer example.",
    ) {
        ElSpace(
            spacing = 10.dp,
            spacer = { ElDivider(direction = ElDividerDirection.Vertical, modifier = Modifier.height(18.dp)) },
        ) {
            ElButton(onClick = {}, text = "Button 1")
            ElButton(onClick = {}, text = "Button 2")
        }
    }

    DemoExample(
        "Alignment",
        "The cross-axis alignment of the items: flex-start, center and flex-end, matching the alignment prop.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                Alignment.Top to "flex-start",
                Alignment.CenterVertically to "center",
                Alignment.Bottom to "flex-end",
            ).forEach { (alignment, label) ->
                DemoHint(label)
                ElSpace(verticalAlignment = alignment, spacing = 8.dp) {
                    ElText("string", size = ElSize.Small)
                    ElButton(onClick = {}, text = "button", size = ElSize.Small)
                    ElCard(contentPadding = 6.dp) { BasicText("body", style = theme.typography.extraSmall) }
                }
            }
        }
    }

    DemoExample("Customized size", "Override the spacing with an arbitrary size.") {
        ElSpace(spacing = 24.dp) { repeat(3) { Item("Custom") } }
    }
    DemoExample(
        "Horizontal and vertical size",
        "Passing both gaps is the equivalent of size=\"[horizontal, vertical]\"; the cross-axis gap only applies to wrapped lines.",
    ) {
        ElSpace(spacing = 20.dp, verticalSpacing = 4.dp, wrap = true, modifier = Modifier.fillMaxWidth()) {
            repeat(12) { Item("Item ${it + 1}") }
        }
    }
    DemoExample(
        "Spacer between items",
        "A spacer can also be an empty fixed-size box, the closest Compose equivalent of a spacer element.",
    ) {
        ElSpace {
            Item("Left")
            Box(Modifier.width(40.dp).height(1.dp).background(colors.border))
            Item("Right")
        }
    }
}
