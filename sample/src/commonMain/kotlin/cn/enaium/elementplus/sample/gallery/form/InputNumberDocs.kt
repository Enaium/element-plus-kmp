package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

@Composable
fun InputNumberDocs() {
    var steps by remember { mutableStateOf<Double?>(0.0) }
    var precision by remember { mutableStateOf<Double?>(3.14) }
    DemoExample("Basic usage", "Use controls to increase or decrease the value.") {
        var basic by remember { mutableStateOf<Double?>(1.0) }
        ElInputNumber(value = basic, onValueChange = { basic = it }, min = 0.0, max = 10.0)
        DemoHint("Value: $basic")
    }
    DemoExample("Steps", "Set step to change the increment size.") {
        ElInputNumber(value = steps, onValueChange = { steps = it }, step = 2.0, min = 0.0, max = 20.0)
    }
    DemoExample("Precision", "Round the value to a fixed number of decimals.") {
        ElInputNumber(value = precision, onValueChange = { precision = it }, step = 0.1, precision = 2, min = 0.0, max = 10.0)
    }
    DemoExample("Sizes and disabled", "Input numbers support sizes and disabled state.") {
        var large by remember { mutableStateOf<Double?>(1.0) }
        var small by remember { mutableStateOf<Double?>(1.0) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInputNumber(value = large, onValueChange = { large = it }, size = ElSize.Large, width = 180.dp)
            ElInputNumber(value = small, onValueChange = { small = it }, size = ElSize.Small, width = 120.dp)
            ElInputNumber(value = 1.0, onValueChange = {}, disabled = true)
        }
    }

    DemoExample("Controls position", "Place the step controls on the left or right.") {
        var left by remember { mutableStateOf<Double?>(3.0) }
        var right by remember { mutableStateOf<Double?>(3.0) }
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DemoHint("Left")
                ElInputNumber(
                    value = left,
                    onValueChange = { left = it },
                    controlsPosition = ElInputNumberControlsPosition.Left,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DemoHint("Right")
                ElInputNumber(
                    value = right,
                    onValueChange = { right = it },
                    controlsPosition = ElInputNumberControlsPosition.Right,
                )
            }
        }
    }

    DemoExample("Left decrease, right increase", "Split the step controls to both sides of the field.") {
        var value by remember { mutableStateOf<Double?>(5.0) }
        ElInputNumber(
            value = value,
            onValueChange = { value = it },
            controlsPosition = ElInputNumberControlsPosition.Split,
            width = 200.dp,
        )
    }

    DemoExample("Decimal step and precision", "Use a decimal step and precision to format values.") {
        var value by remember { mutableStateOf<Double?>(1.0) }
        ElInputNumber(
            value = value,
            onValueChange = { value = it },
            step = 0.5,
            precision = 1,
            min = 0.0,
            max = 5.0,
        )
    }

    DemoExample("With prefix and suffix", "Add a prefix or suffix inside the field.") {
        var amount by remember { mutableStateOf<Double?>(100.0) }
        ElInputNumber(
            value = amount,
            onValueChange = { amount = it },
            prefix = { BasicText("$", style = LocalElTheme.current.typography.base.copy(color = LocalElTheme.current.colors.textSecondary)) },
            suffix = { BasicText("USD", style = LocalElTheme.current.typography.extraSmall.copy(color = LocalElTheme.current.colors.textSecondary)) },
            width = 220.dp,
        )
    }

    DemoExample("Controlled", "Share one value across sizes and observe committed changes with onChange.") {
        var num by remember { mutableStateOf<Double?>(1.0) }
        var lastChange by remember { mutableStateOf("-") }
        DemoRowSpaced {
            ElInputNumber(
                value = num,
                onValueChange = { num = it },
                min = 1.0,
                max = 10.0,
                controlsPosition = ElInputNumberControlsPosition.Right,
                size = ElSize.Large,
                onChange = { lastChange = it?.toString() ?: "(empty)" },
            )
            ElInputNumber(
                value = num,
                onValueChange = { num = it },
                min = 1.0,
                max = 10.0,
                controlsPosition = ElInputNumberControlsPosition.Right,
                onChange = { lastChange = it?.toString() ?: "(empty)" },
            )
            ElInputNumber(
                value = num,
                onValueChange = { num = it },
                min = 1.0,
                max = 10.0,
                size = ElSize.Small,
                controlsPosition = ElInputNumberControlsPosition.Right,
                onChange = { lastChange = it?.toString() ?: "(empty)" },
            )
        }
        DemoHint("Value: $num · Last change: $lastChange")
    }

    DemoExample("Custom icons", "Override the increase and decrease icons.") {
        var num by remember { mutableStateOf<Double?>(1.0) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoRowSpaced {
                ElInputNumber(value = num, onValueChange = { num = it })
                ElInputNumber(
                    value = num,
                    onValueChange = { num = it },
                    increaseIcon = { ElIcon(ElIcons.ArrowUp, size = 10.dp) },
                    decreaseIcon = { ElIcon(ElIcons.ArrowDown, size = 10.dp) },
                )
            }
            DemoRowSpaced {
                ElInputNumber(
                    value = num,
                    onValueChange = { num = it },
                    controlsPosition = ElInputNumberControlsPosition.Right,
                )
                ElInputNumber(
                    value = num,
                    onValueChange = { num = it },
                    controlsPosition = ElInputNumberControlsPosition.Right,
                    increaseIcon = { ElIcon(ElIcons.Plus, size = 10.dp) },
                    decreaseIcon = { ElIcon(ElIcons.Minus, size = 10.dp) },
                )
            }
        }
    }

    DemoExample("Formatter and parser", "Render a currency formatted value and parse typed text back.") {
        var input by remember { mutableStateOf<Double?>(10000.0) }
        ElInputNumber(
            value = input,
            onValueChange = { input = it },
            formatter = { value ->
                value?.let { "$ " + it.toLong().toString().reversed().chunked(3).joinToString(",").reversed() } ?: ""
            },
            parser = { text -> text.replace("$", "").replace(",", "").replace(" ", "").toDoubleOrNull() },
            width = 220.dp,
        )
        DemoHint("Value: $input")
    }

    DemoExample("Step strictly", "Only multiples of the step are accepted.") {
        var num by remember { mutableStateOf<Double?>(2.0) }
        ElInputNumber(
            value = num,
            onValueChange = { num = it },
            step = 2.0,
            stepStrictly = true,
        )
        DemoHint("Value: $num")
    }
}
