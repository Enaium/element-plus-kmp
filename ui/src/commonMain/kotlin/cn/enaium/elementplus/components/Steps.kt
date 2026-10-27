package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElStepsDirection { Horizontal, Vertical }
enum class ElStepStatus { Wait, Process, Finish, Error, Success }

class ElStep(
    val title: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val status: ElStepStatus? = null,
)

/** `<el-steps>` with horizontal/vertical layouts and simple mode. */
@Composable
fun ElSteps(
    steps: List<ElStep>,
    active: Int,
    modifier: Modifier = Modifier,
    direction: ElStepsDirection = ElStepsDirection.Horizontal,
    simple: Boolean = false,
    alignCenter: Boolean = false,
    finishStatus: ElStepStatus = ElStepStatus.Finish,
    processStatus: ElStepStatus = ElStepStatus.Process,
    circleSize: Dp = 24.dp,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    if (simple) {
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
                val status = step.status ?: when {
                    index < active -> finishStatus
                    index == active -> processStatus
                    else -> ElStepStatus.Wait
                }
                BasicText(
                    text = step.title,
                    style = theme.typography.base.copy(
                        color = if (status == ElStepStatus.Process) colors.primary else if (status == ElStepStatus.Finish) colors.textPrimary else colors.textSecondary,
                        fontWeight = if (status == ElStepStatus.Process) FontWeight.Medium else FontWeight.Normal,
                    ),
                )
            }
        }
        return
    }
    if (direction == ElStepsDirection.Horizontal) {
        Row(modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                val status = step.status ?: when {
                    index < active -> finishStatus
                    index == active -> processStatus
                    else -> ElStepStatus.Wait
                }
                Column(Modifier.weight(1f), horizontalAlignment = if (alignCenter) Alignment.CenterHorizontally else Alignment.Start) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        StepCircle(index, status, step.icon, circleSize)
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
                val status = step.status ?: when {
                    index < active -> finishStatus
                    index == active -> processStatus
                    else -> ElStepStatus.Wait
                }
                Row(Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        StepCircle(index, status, step.icon, circleSize)
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

@Composable
private fun StepCircle(index: Int, status: ElStepStatus, icon: ImageVector?, circleSize: Dp) {
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
            icon != null -> ElIcon(icon, size = circleSize * 0.6f, tint = if (filled) colors.white else color)
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
        if (step.description != null) {
            BasicText(
                text = step.description,
                modifier = Modifier.padding(top = 4.dp),
                style = theme.typography.small.copy(color = colors.textSecondary),
            )
        }
    }
}
