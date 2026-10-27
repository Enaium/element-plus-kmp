package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElStepsDirection { Horizontal, Vertical }
enum class ElStepStatus { Wait, Process, Finish, Error, Success }

/**
 * A single step.
 *
 * `iconContent`, `titleContent` and `descriptionContent` mirror the upstream
 * `icon`, `title` and `description` slots and take precedence over the
 * corresponding string/icon values.
 */
class ElStep(
    val title: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val status: ElStepStatus? = null,
    val iconContent: (@Composable () -> Unit)? = null,
    val titleContent: (@Composable () -> Unit)? = null,
    val descriptionContent: (@Composable () -> Unit)? = null,
)

/**
 * `<el-steps>` with horizontal/vertical layouts and simple mode.
 *
 * @param space fixed width of each step; when omitted steps share the available
 * width. Mirrors the upstream `space` prop (fixed spacing instead of
 * percentage, which has no direct Compose equivalent).
 * @param onChange invoked with the new active index whenever [active] changes.
 */
@Composable
fun ElSteps(
    steps: List<ElStep>,
    active: Int = 0,
    modifier: Modifier = Modifier,
    direction: ElStepsDirection = ElStepsDirection.Horizontal,
    simple: Boolean = false,
    alignCenter: Boolean = false,
    finishStatus: ElStepStatus = ElStepStatus.Finish,
    processStatus: ElStepStatus = ElStepStatus.Process,
    circleSize: Dp = 24.dp,
    space: Dp? = null,
    onChange: ((Int) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var lastActive by remember { mutableStateOf(active) }
    LaunchedEffect(active) {
        if (lastActive != active) {
            lastActive = active
            onChange?.invoke(active)
        }
    }
    if (simple) {
        val itemModifier = if (space != null) Modifier.width(space) else Modifier
        if (direction == ElStepsDirection.Vertical) {
            Column(modifier) {
                steps.forEachIndexed { index, step ->
                    if (index > 0) {
                        ElIcon(
                            ElIcons.ArrowDown,
                            size = 12.dp,
                            tint = colors.textPlaceholder,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                    SimpleStep(step, resolveStatus(step, index, active, finishStatus, processStatus), itemModifier)
                }
            }
        } else {
            Row(modifier, verticalAlignment = Alignment.CenterVertically) {
                steps.forEachIndexed { index, step ->
                    if (index > 0) {
                        ElIcon(
                            ElIcons.ArrowRight,
                            size = 12.dp,
                            tint = colors.textPlaceholder,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                    SimpleStep(step, resolveStatus(step, index, active, finishStatus, processStatus), itemModifier)
                }
            }
        }
        return
    }
    if (direction == ElStepsDirection.Horizontal) {
        Row(modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                val status = resolveStatus(step, index, active, finishStatus, processStatus)
                val stepModifier = if (space != null) Modifier.width(space) else Modifier.weight(1f)
                Column(
                    stepModifier,
                    horizontalAlignment = if (alignCenter) Alignment.CenterHorizontally else Alignment.Start,
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        StepCircle(index, status, step, circleSize)
                        if (index < steps.lastIndex) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .padding(horizontal = 8.dp)
                                    .background(if (index < active) colors.primary else colors.border),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    StepText(step, status, if (alignCenter) Alignment.CenterHorizontally else Alignment.Start)
                }
            }
        }
    } else {
        Column(modifier) {
            steps.forEachIndexed { index, step ->
                val status = resolveStatus(step, index, active, finishStatus, processStatus)
                Row(Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        StepCircle(index, status, step, circleSize)
                        if (index < steps.lastIndex) {
                            Box(
                                Modifier
                                    .width(2.dp)
                                    .height(46.dp)
                                    .background(if (index < active) colors.primary else colors.border),
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.padding(top = 2.dp, bottom = 18.dp)) {
                        StepText(step, status, Alignment.Start)
                    }
                }
            }
        }
    }
}

private fun resolveStatus(
    step: ElStep,
    index: Int,
    active: Int,
    finishStatus: ElStepStatus,
    processStatus: ElStepStatus,
): ElStepStatus = step.status ?: when {
    index < active -> finishStatus
    index == active -> processStatus
    else -> ElStepStatus.Wait
}

@Composable
private fun SimpleStep(step: ElStep, status: ElStepStatus, modifier: Modifier) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val color = when (status) {
        ElStepStatus.Process -> colors.primary
        ElStepStatus.Error -> colors.danger
        ElStepStatus.Finish, ElStepStatus.Success -> colors.textPrimary
        ElStepStatus.Wait -> colors.textSecondary
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        if (step.iconContent != null) {
            step.iconContent()
            Spacer(Modifier.width(4.dp))
        } else if (step.icon != null) {
            ElIcon(step.icon, size = 14.dp, tint = color)
            Spacer(Modifier.width(4.dp))
        }
        if (step.titleContent != null) {
            step.titleContent()
        } else {
            BasicText(
                text = step.title,
                style = theme.typography.base.copy(
                    color = color,
                    fontWeight = if (status == ElStepStatus.Process) FontWeight.Medium else FontWeight.Normal,
                ),
            )
        }
    }
}

@Composable
private fun StepCircle(index: Int, status: ElStepStatus, step: ElStep, circleSize: Dp) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val color = when (status) {
        ElStepStatus.Finish -> colors.primary
        ElStepStatus.Process -> colors.primary
        ElStepStatus.Error -> colors.danger
        ElStepStatus.Success -> colors.success
        ElStepStatus.Wait -> colors.textPlaceholder
    }
    val filled = status == ElStepStatus.Finish || status == ElStepStatus.Process || status == ElStepStatus.Success || status == ElStepStatus.Error
    Box(
        Modifier
            .size(circleSize)
            .background(if (filled) color else colors.background, CircleShape)
            .border(1.dp, color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        when {
            step.iconContent != null -> step.iconContent()
            step.icon != null -> ElIcon(step.icon, size = circleSize * 0.6f, tint = if (filled) colors.white else color)
            status == ElStepStatus.Finish || status == ElStepStatus.Success -> ElIcon(ElIcons.Check, size = circleSize * 0.55f, tint = colors.white)
            status == ElStepStatus.Error -> ElIcon(ElIcons.Close, size = circleSize * 0.5f, tint = colors.white)
            else -> BasicText(
                text = (index + 1).toString(),
                style = theme.typography.extraSmall.copy(color = if (filled) colors.white else color),
            )
        }
    }
}

@Composable
private fun StepText(step: ElStep, status: ElStepStatus, alignment: Alignment.Horizontal) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Column(horizontalAlignment = alignment) {
        if (step.titleContent != null) {
            step.titleContent()
        } else {
            BasicText(
                text = step.title,
                style = theme.typography.base.copy(
                    color = when (status) {
                        ElStepStatus.Wait -> colors.textSecondary
                        ElStepStatus.Error -> colors.danger
                        else -> colors.textPrimary
                    },
                    fontWeight = if (status == ElStepStatus.Process) FontWeight.Medium else FontWeight.Normal,
                ),
            )
        }
        if (step.descriptionContent != null || !step.description.isNullOrEmpty()) {
            if (step.descriptionContent != null) {
                Box(Modifier.padding(top = 4.dp)) { step.descriptionContent() }
            } else {
                BasicText(
                    text = step.description.orEmpty(),
                    modifier = Modifier.padding(top = 4.dp),
                    style = theme.typography.small.copy(color = colors.textSecondary),
                )
            }
        }
    }
}
