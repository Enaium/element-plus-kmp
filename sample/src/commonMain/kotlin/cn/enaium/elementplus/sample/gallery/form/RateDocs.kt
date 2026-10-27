package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElRate
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun RateDocs() {
    var half by remember { mutableStateOf(3.5) }
    var clearable by remember { mutableStateOf(2.0) }
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Click a star to set the rating.") {
        var basic by remember { mutableStateOf(3.0) }
        ElRate(value = basic, onValueChange = { basic = it })
        DemoHint("Rating: $basic")
    }
    DemoExample(
        "Color thresholds",
        "Colors are picked by low-threshold and high-threshold instead of a raw score ratio.",
    ) {
        var value by remember { mutableStateOf(3.0) }
        ElRate(
            value = value,
            onValueChange = { value = it },
            colors = listOf(colors.info, colors.warning, colors.danger),
            lowThreshold = 2,
            highThreshold = 4,
        )
        DemoHint("Score: $value")
    }
    DemoExample("Allow half", "Half stars can be selected.") {
        ElRate(value = half, onValueChange = { half = it }, allowHalf = true)
    }
    DemoExample("Clearable", "Click the selected star again to clear the rating.") {
        ElRate(value = clearable, onValueChange = { clearable = it }, clearable = true)
    }
    DemoExample("Texts and colors", "Provide labels for each score level.") {
        var basic by remember { mutableStateOf(3.0) }
        ElRate(
            value = basic,
            onValueChange = { basic = it },
            showText = true,
            texts = listOf("Terrible", "Poor", "Average", "Good", "Excellent"),
            colors = listOf(colors.info, colors.warning, colors.danger),
        )
    }
    DemoExample("Show score", "Display the numeric score instead of a label.") {
        var basic by remember { mutableStateOf(3.0) }
        ElRate(value = basic, onValueChange = { basic = it }, showScore = true)
    }
    DemoExample(
        "More icons",
        "Custom per-level active icons, a custom void icon and threshold colors.",
    ) {
        var value by remember { mutableStateOf(3.0) }
        ElRate(
            value = value,
            onValueChange = { value = it },
            icons = listOf(ElIcons.StarFilled, ElIcons.ChatDotRound, ElIcons.Trophy),
            voidIcon = ElIcons.Star,
            colors = listOf(colors.primary, colors.success, colors.warning),
        )
        DemoHint("Score: $value")
    }
    DemoExample(
        "Readonly score with template",
        "A disabled rate shows the score through score-template and a custom text color.",
    ) {
        ElRate(
            value = 3.7,
            onValueChange = {},
            disabled = true,
            showScore = true,
            textColor = colors.warning,
            scoreTemplate = "{value} points",
        )
    }
    DemoExample("Sizes and disabled", "Rates are available in three sizes.") {
        var large by remember { mutableStateOf(3.0) }
        var small by remember { mutableStateOf(3.0) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElRate(value = large, onValueChange = { large = it }, size = ElSize.Large)
            ElRate(value = small, onValueChange = { small = it }, size = ElSize.Small)
            ElRate(value = 4.0, onValueChange = {}, disabled = true)
        }
    }
    DemoExample(
        "Void colors",
        "void-color and disabled-void-color style the unselected icons.",
    ) {
        var value by remember { mutableStateOf(2.0) }
        ElRate(
            value = value,
            onValueChange = { value = it },
            voidColor = colors.borderDark,
        )
        ElRate(
            value = 3.0,
            onValueChange = {},
            disabled = true,
            voidColor = colors.borderDark,
            disabledVoidColor = colors.primary,
            disabledVoidIcon = ElIcons.StarFilled,
        )
    }
    DemoExample("Readonly", "A readonly rate displays a value without allowing changes.") {
        ElRate(value = 3.5, onValueChange = {}, readOnly = true)
    }
    DemoExample("Sizes", "Rates scale with the size token.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElRate(value = 3.0, onValueChange = {}, size = ElSize.Large)
            ElRate(value = 3.0, onValueChange = {}, size = ElSize.Default)
            ElRate(value = 3.0, onValueChange = {}, size = ElSize.Small)
        }
    }
}
