package cn.enaium.elementplus.sample.gallery.data

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.ElTour
import cn.enaium.elementplus.components.ElTourGap
import cn.enaium.elementplus.components.ElTourMask
import cn.enaium.elementplus.components.ElTourPlacement
import cn.enaium.elementplus.components.ElTourStep
import cn.enaium.elementplus.components.ElTourTargetRegistry
import cn.enaium.elementplus.components.ElTourType
import cn.enaium.elementplus.components.LocalElTourTargets
import cn.enaium.elementplus.components.elTourTarget
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun TourDocs() {
    DemoExample("Basic usage", "Guide users through a sequence of steps.") {
        TourSample()
    }
    DemoExample("Custom content", "Steps can contain arbitrary composables.") {
        TourSample(withCustomContent = true)
    }
    DemoExample("Without mask", "Disable the dark mask to keep the page visible.") {
        TourSample(showMask = false)
    }
    DemoExample("Placement", "Attach the card to the top, right or bottom of a target element.") {
        TourPlacementSample()
    }
    DemoExample("Target elements", "Point steps at different elements through elTourTarget keys.") {
        TourTargetSample()
    }
    DemoExample("Custom indicator", "Replace the progress text with the indicators slot.") {
        TourIndicatorSample()
    }
    DemoExample("Custom mask", "Recolor the mask and tune the gap around the target.") {
        TourMaskSample()
    }
    DemoExample("Primary type", "Paint the card with the primary color through type.") {
        TourPrimarySample()
    }
    DemoExample("Non-modal", "Render the tour inside the page so its mask never blocks interaction.") {
        TourNonModalSample()
    }
    DemoExample("No arrow or close button", "Hide the pointing arrow and the close icon per tour or per step.") {
        var visible by remember { mutableStateOf(false) }
        var current by remember { mutableIntStateOf(0) }
        ElButton(onClick = { current = 0; visible = true }, text = "Begin tour", type = ElType.Primary)
        ElTour(
            visible = visible,
            steps = listOf(
                ElTourStep("No arrow", "This card has no pointing arrow.", showArrow = false),
                ElTourStep("No close", "This step cannot be dismissed from the card.", showClose = false),
            ),
            current = current,
            onCurrentChange = { current = it },
            onClose = { visible = false },
            showArrow = false,
            showClose = false,
        )
    }
}

@Composable
private fun TourSample(withCustomContent: Boolean = false, showMask: Boolean = true) {
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(0) }
    val steps = remember(withCustomContent) {
        listOf(
            ElTourStep(
                title = "Welcome",
                description = "This is the first step of the guide.",
                content = if (withCustomContent) {
                    { BasicText("Custom content can be placed inside a tour step.", style = LocalElTheme.current.typography.small) }
                } else null,
            ),
            ElTourStep("Components", "Use the sidebar to browse component examples."),
            ElTourStep("Finish", "You have reached the end of the tour."),
        )
    }
    ElButton(onClick = { current = 0; visible = true }, text = "Start tour", type = ElType.Primary)
    ElTour(
        visible = visible,
        steps = steps,
        current = current,
        onCurrentChange = { current = it },
        onClose = { visible = false },
        showMask = showMask,
        onFinish = { ElMessage.show("Tour finished", ElMessageType.Success) },
    )
}

@Composable
private fun TourPlacementSample() {
    val registry = remember { ElTourTargetRegistry() }
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(0) }
    val steps = remember {
        listOf(
            ElTourStep("Center", "No target keeps the card in the middle of the screen."),
            ElTourStep(
                title = "Right",
                description = "Attached to the right of the target.",
                targetKey = "placement-target",
                placement = ElTourPlacement.Right,
            ),
            ElTourStep(
                title = "Top",
                description = "Attached above the target.",
                targetKey = "placement-target",
                placement = ElTourPlacement.Top,
            ),
        )
    }
    CompositionLocalProvider(LocalElTourTargets provides registry) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ElButton(
                    onClick = { current = 0; visible = true },
                    text = "Begin tour",
                    type = ElType.Primary,
                    modifier = Modifier.elTourTarget("placement-target"),
                )
                BasicText(
                    text = "The target button anchors every placement.",
                    style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
                )
            }
            ElTour(
                visible = visible,
                steps = steps,
                current = current,
                onCurrentChange = { current = it },
                onClose = { visible = false },
            )
        }
    }
}

@Composable
private fun TourTargetSample() {
    val registry = remember { ElTourTargetRegistry() }
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(0) }
    val steps = remember {
        listOf(
            ElTourStep(
                title = "Upload File",
                description = "Put your files here.",
                targetKey = "tour-upload",
                placement = ElTourPlacement.Bottom,
            ),
            ElTourStep(
                title = "Save",
                description = "Save your changes.",
                targetKey = "tour-save",
                placement = ElTourPlacement.BottomStart,
            ),
            ElTourStep(
                title = "Other actions",
                description = "Click to see other options.",
                targetKey = "tour-more",
                placement = ElTourPlacement.BottomEnd,
            ),
        )
    }
    CompositionLocalProvider(LocalElTourTargets provides registry) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ElButton(onClick = {}, text = "Upload", modifier = Modifier.elTourTarget("tour-upload"))
                ElButton(onClick = {}, text = "Save", type = ElType.Primary, modifier = Modifier.elTourTarget("tour-save"))
                ElButton(onClick = {}, text = "More", modifier = Modifier.elTourTarget("tour-more"))
            }
            ElButton(onClick = { current = 0; visible = true }, text = "Begin tour", type = ElType.Primary, size = ElSize.Small)
            ElTour(
                visible = visible,
                steps = steps,
                current = current,
                onCurrentChange = { current = it },
                onClose = { visible = false },
            )
        }
    }
}

@Composable
private fun TourIndicatorSample() {
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(0) }
    ElButton(onClick = { current = 0; visible = true }, text = "Begin tour", type = ElType.Primary)
    ElTour(
        visible = visible,
        steps = listOf(
            ElTourStep("Upload File", "Put your files here."),
            ElTourStep("Save", "Save your changes."),
            ElTourStep("Other actions", "Click to see other options."),
        ),
        current = current,
        onCurrentChange = { current = it },
        onClose = { visible = false },
        indicators = { index, total ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(total) { dot ->
                    Box(
                        Modifier
                            .width(if (dot == index) 16.dp else 6.dp)
                            .height(6.dp)
                            .background(
                                if (dot == index) LocalElTheme.current.colors.primary else LocalElTheme.current.colors.border,
                                RoundedCornerShape(3.dp),
                            ),
                    )
                }
                BasicText(
                    text = "${index + 1} / $total",
                    modifier = Modifier.padding(start = 4.dp),
                    style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
                )
            }
        },
    )
}

@Composable
private fun TourMaskSample() {
    val registry = remember { ElTourTargetRegistry() }
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(0) }
    CompositionLocalProvider(LocalElTourTargets provides registry) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElButton(
                onClick = { current = 0; visible = true },
                text = "Begin tour",
                type = ElType.Primary,
                modifier = Modifier.elTourTarget("tour-mask-target"),
            )
            ElTour(
                visible = visible,
                steps = listOf(
                    ElTourStep(
                        title = "Custom mask",
                        description = "The spotlight uses a primary tint and a wider gap.",
                        targetKey = "tour-mask-target",
                        placement = ElTourPlacement.Bottom,
                    ),
                    ElTourStep(
                        title = "Per-step mask",
                        description = "This step disables the mask entirely.",
                        mask = false,
                    ),
                ),
                current = current,
                onCurrentChange = { current = it },
                onClose = { visible = false },
                mask = ElTourMask(color = LocalElTheme.current.colors.primary.copy(alpha = 0.45f)),
                gap = ElTourGap(offset = 10.dp, radius = 12.dp, padding = 10.dp),
            )
        }
    }
}

@Composable
private fun TourPrimarySample() {
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(0) }
    ElButton(onClick = { current = 0; visible = true }, text = "Begin tour", type = ElType.Primary)
    ElTour(
        visible = visible,
        steps = listOf(
            ElTourStep("Primary card", "The card uses the primary background.", type = ElTourType.Primary),
            ElTourStep("Second step", "Text and buttons adapt to the primary background.", type = ElTourType.Primary),
        ),
        current = current,
        onCurrentChange = { current = it },
        onClose = { visible = false },
        type = ElTourType.Primary,
    )
}

@Composable
private fun TourNonModalSample() {
    val registry = remember { ElTourTargetRegistry() }
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(0) }
    var clicks by remember { mutableIntStateOf(0) }
    val steps = remember {
        listOf(
            ElTourStep(
                title = "Non-modal tour",
                description = "The page underneath stays interactive; only the card captures input.",
                targetKey = "tour-non-modal-target",
                placement = ElTourPlacement.Bottom,
            ),
        )
    }
    CompositionLocalProvider(LocalElTourTargets provides registry) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(LocalElTheme.current.colors.fillLighter, RoundedCornerShape(6.dp)),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BasicText(
                    text = "Page content stays clickable under the non-modal mask.",
                    style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ElButton(
                        onClick = { clicks++ },
                        text = "Clickable ($clicks)",
                        modifier = Modifier.elTourTarget("tour-non-modal-target"),
                    )
                    ElButton(
                        onClick = { current = 0; visible = true },
                        text = "Begin tour",
                        type = ElType.Primary,
                    )
                }
                DemoHint("Clicks: $clicks")
            }
            ElTour(
                visible = visible,
                steps = steps,
                current = current,
                onCurrentChange = { current = it },
                onClose = { visible = false },
                modal = false,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}
