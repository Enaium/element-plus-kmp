package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingDocs() {
    var loading by remember { mutableStateOf(false) }
    var fullscreen by remember { mutableStateOf(false) }
    var closedCount by remember { mutableStateOf(0) }
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Overlay a loading indicator on a content area.") {
        ElSwitch(checked = loading, onCheckedChange = { loading = it }, activeText = "Loading", inactiveText = "Idle")
        ElLoading(
            visible = loading,
            text = "Loading...",
            background = colors.background.copy(alpha = 0.75f),
            onClosed = { closedCount++ },
        ) {
            Box(
                Modifier.fillMaxWidth().height(140.dp).background(colors.fillLight, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) { BasicText("Content area", style = LocalElTheme.current.typography.base.copy(color = colors.textSecondary)) }
        }
        DemoHint("closed fired $closedCount time(s).")
    }
    DemoExample("Custom spinner and background", "Replace the built-in spinner with a custom composable and darken the mask.") {
        var show by remember { mutableStateOf(true) }
        ElSwitch(checked = show, onCheckedChange = { show = it })
        ElLoading(
            visible = show,
            spinner = { CustomArcSpinner(Color.White) },
            textContent = {
                BasicText(
                    "Loading...",
                    style = LocalElTheme.current.typography.small.copy(color = Color.White),
                )
            },
            background = Color.Transparent,
            // custom-class equivalent: an inset, rounded mask.
            overlayModifier = Modifier.padding(6.dp).background(Color(0xCC7A7A7A), RoundedCornerShape(6.dp)),
        ) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(colors.fillLighter, RoundedCornerShape(6.dp)))
        }
    }
    DemoExample("Custom spinner size", "Scale the default spinner without replacing it.") {
        var show by remember { mutableStateOf(true) }
        ElSwitch(checked = show, onCheckedChange = { show = it })
        ElLoading(
            visible = show,
            text = "Please wait",
            spinnerSize = 42.dp,
        ) {
            Box(Modifier.fillMaxWidth().height(100.dp).background(colors.fillLighter, RoundedCornerShape(6.dp)))
        }
    }
    DemoExample("Scroll lock", "lock intercepts pointer input so the masked content cannot be clicked.") {
        var locked by remember { mutableStateOf(true) }
        var clicks by remember { mutableStateOf(0) }
        ElSwitch(checked = locked, onCheckedChange = { locked = it }, activeText = "Locked", inactiveText = "Click-through")
        ElLoading(
            visible = true,
            lock = locked,
            text = if (locked) "Locked" else "Click-through",
            background = colors.background.copy(alpha = 0.7f),
        ) {
            Box(
                Modifier.fillMaxWidth().height(120.dp).background(colors.fillLight, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                ElButton(onClick = { clicks++ }, text = "Clicked $clicks times", type = ElType.Primary)
            }
        }
        DemoHint("Turn the lock off and click the button under the mask to count a click.")
    }
    DemoExample("Target overlay", "Mask a single target area without wrapping its content.") {
        var targetLoading by remember { mutableStateOf(true) }
        ElSwitch(checked = targetLoading, onCheckedChange = { targetLoading = it }, activeText = "Loading", inactiveText = "Idle")
        Box(
            Modifier.fillMaxWidth().height(150.dp).background(colors.fillLight, RoundedCornerShape(6.dp)),
        ) {
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BasicText("Target area", style = LocalElTheme.current.typography.base)
                ElButton(onClick = { }, text = "Action in target")
            }
            ElLoading(
                visible = targetLoading,
                modifier = Modifier.matchParentSize(),
                text = "Loading",
                background = colors.background.copy(alpha = 0.8f),
            )
        }
    }
    DemoExample("Async close with beforeClose", "beforeClose keeps the mask visible while async teardown runs.") {
        var loading by remember { mutableStateOf(true) }
        var closing by remember { mutableStateOf(false) }
        var closed by remember { mutableStateOf(0) }
        ElSwitch(checked = loading, onCheckedChange = { loading = it }, activeText = "Loading", inactiveText = "Idle")
        ElLoading(
            visible = loading,
            text = if (closing) "Closing..." else "Loading...",
            beforeClose = {
                closing = true
                try {
                    delay(900)
                } finally {
                    closing = false
                }
            },
            onClosed = { closed++ },
            background = colors.background.copy(alpha = 0.7f),
        ) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(colors.fillLighter, RoundedCornerShape(6.dp)))
        }
        DemoHint("The mask stays up for the async hook, then hides; closed fired $closed time(s).")
    }
    DemoExample("Fullscreen", "A fullscreen overlay covers the whole sample area and locks interaction.") {
        ElButton(onClick = { fullscreen = true }, text = "Show fullscreen loading")
        if (fullscreen) {
            Box(Modifier.fillMaxWidth().height(200.dp)) {
                ElLoading(
                    visible = fullscreen,
                    text = "Loading page...",
                    fullscreen = true,
                    lock = true,
                    background = colors.background.copy(alpha = 0.9f),
                )
                ElButton(
                    onClick = { fullscreen = false },
                    text = "Stop loading",
                    modifier = Modifier.align(Alignment.BottomCenter).padding(10.dp),
                )
            }
        }
    }
}

@Composable
private fun CustomArcSpinner(color: Color) {
    val transition = rememberInfiniteTransition(label = "custom-loading-spinner")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "custom-loading-angle",
    )
    Canvas(Modifier.size(36.dp).rotate(angle)) {
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}
