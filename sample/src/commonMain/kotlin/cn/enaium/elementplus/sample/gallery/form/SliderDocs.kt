package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElSlider
import cn.enaium.elementplus.components.ElSliderMark
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize

@Composable
fun SliderDocs() {
    DemoExample("Basic usage", "Drag the handle to choose a value; tooltips can be hidden or formatted.") {
        var basic by remember { mutableStateOf(40.0) }
        var hidden by remember { mutableStateOf(40.0) }
        var formatted by remember { mutableStateOf(40.0) }
        ElSlider(value = basic, onValueChange = { basic = it })
        DemoHint("Value: ${basic.toInt()}")
        ElSlider(value = hidden, onValueChange = { hidden = it }, showTooltip = false)
        ElSlider(
            value = formatted,
            onValueChange = { formatted = it },
            formatTooltip = { (it / 100).toString() },
        )
        DemoHint("Formatted tooltip: ${formatted / 100}")
    }
    DemoExample("Range selection", "Use a range for a start and an end value.") {
        var value by remember { mutableStateOf(4.0..8.0) }
        ElSlider(value = value, onValueChange = { value = it }, max = 10.0, showStops = true)
        DemoHint("Range: ${value.start.toInt()} - ${value.endInclusive.toInt()}")
    }
    DemoExample("Discrete values", "Set step and show stops to snap to values.") {
        var value by remember { mutableStateOf(30.0) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSlider(value = value, onValueChange = { value = it }, step = 10.0)
            ElSlider(value = value, onValueChange = { value = it }, step = 10.0, showStops = true)
        }
    }
    DemoExample("Disabled", "A disabled slider cannot be moved.") {
        ElSlider(value = 50.0, onValueChange = {}, disabled = true)
    }
    DemoExample("Show marks", "Draw labelled marks along the track, including a custom mark color.") {
        var value by remember { mutableStateOf(30.0..60.0) }
        ElSlider(
            value = value,
            onValueChange = { value = it },
            marks = mapOf(
                0.0 to ElSliderMark("0°C"),
                8.0 to ElSliderMark("8°C"),
                37.0 to ElSliderMark("37°C"),
                50.0 to ElSliderMark("50%", color = cn.enaium.elementplus.theme.LocalElTheme.current.colors.primary),
            ),
            modifier = Modifier.width(360.dp),
        )
        DemoHint("Range: ${value.start.toInt()} - ${value.endInclusive.toInt()}")
    }
    DemoExample(
        "Vertical mode",
        "Vertical sliders take their length from height and their tooltip side from placement.",
    ) {
        var value by remember { mutableStateOf(37.0) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            ElSlider(
                value = value,
                onValueChange = { value = it },
                vertical = true,
                height = 200.dp,
                placement = ElPlacement.Right,
                formatTooltip = { "${it.toInt()}°C" },
                marks = mapOf(
                    0.0 to ElSliderMark("0°C"),
                    37.0 to ElSliderMark("37°C"),
                    100.0 to ElSliderMark("100°C"),
                ),
            )
            DemoHint("Value: ${value.toInt()}°C")
        }
    }
    DemoExample(
        "Tooltip placement",
        "Control which side of the thumb the value tooltip is drawn on.",
    ) {
        var top by remember { mutableStateOf(40.0) }
        var bottom by remember { mutableStateOf(40.0) }
        var left by remember { mutableStateOf(40.0) }
        var right by remember { mutableStateOf(40.0) }
        Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ElSlider(value = top, onValueChange = { top = it }, placement = ElPlacement.Top)
            ElSlider(value = bottom, onValueChange = { bottom = it }, placement = ElPlacement.Bottom)
            ElSlider(value = left, onValueChange = { left = it }, placement = ElPlacement.Left)
            ElSlider(value = right, onValueChange = { right = it }, placement = ElPlacement.Right)
        }
    }
    DemoExample("Component height", "Change the vertical space reserved for the slider.") {
        var large by remember { mutableStateOf(40.0) }
        var small by remember { mutableStateOf(40.0) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSlider(value = large, onValueChange = { large = it }, height = 40.dp)
            ElSlider(value = small, onValueChange = { small = it }, height = 24.dp)
        }
    }
    DemoExample("Slider with input box", "Render a built-in input box beside the slider.") {
        var value by remember { mutableStateOf(40.0) }
        ElSlider(
            value = value,
            onValueChange = { value = it },
            showInput = true,
            modifier = Modifier.width(420.dp),
        )
        var rangeValue by remember { mutableStateOf(20.0..70.0) }
        ElSlider(
            value = rangeValue,
            onValueChange = { rangeValue = it },
            showInput = true,
            modifier = Modifier.width(520.dp),
        )
    }
    DemoExample(
        "Sizes with input box",
        "Sliders scale their track, thumb and built-in input with the size token.",
    ) {
        Column(Modifier.width(460.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            var large by remember { mutableStateOf(40.0) }
            var normal by remember { mutableStateOf(40.0) }
            var small by remember { mutableStateOf(40.0) }
            ElSlider(value = large, onValueChange = { large = it }, showInput = true, size = ElSize.Large)
            ElSlider(value = normal, onValueChange = { normal = it }, showInput = true)
            ElSlider(value = small, onValueChange = { small = it }, showInput = true, size = ElSize.Small)
        }
    }
    DemoExample("Restrict value", "Restrict the slider to a custom range.") {
        var value by remember { mutableStateOf(30.0) }
        ElSlider(value = value, onValueChange = { value = it }, min = 10.0, max = 50.0, step = 5.0, modifier = Modifier.width(360.dp))
        DemoHint("Value: $value")
    }
}
