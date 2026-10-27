package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun StepsDocs() {
    val steps = listOf(
        ElStep("Design", "Choose the structure"),
        ElStep("Develop", "Implement the components"),
        ElStep("Review", "Verify the result"),
        ElStep("Release", "Ship it"),
    )
    DemoExample("Basic usage", "Steps show progress through a process; the button advances it.") {
        var active by remember { mutableStateOf(0) }
        Column {
            ElSteps(
                steps = listOf(ElStep("Step 1"), ElStep("Step 2"), ElStep("Step 3")),
                active = active,
                finishStatus = ElStepStatus.Success,
            )
            Spacer(Modifier.height(12.dp))
            ElButton(
                onClick = { active = if (active >= 2) 0 else active + 1 },
                text = "Next step",
                size = ElSize.Small,
            )
        }
    }
    DemoExample("With description", "Add a description to each step.") {
        ElSteps(steps = steps, active = 1)
    }
    DemoExample("Vertical", "Vertical steps are useful in narrow layouts.") {
        ElSteps(steps = steps, active = 2, direction = ElStepsDirection.Vertical, modifier = Modifier.width(300.dp))
    }
    DemoExample("Simple", "A compact simple mode with icons.") {
        ElSteps(
            steps = listOf(
                ElStep("Step 1", icon = ElIcons.Edit),
                ElStep("Step 2", icon = ElIcons.Upload),
                ElStep("Step 3", icon = ElIcons.Picture),
            ),
            active = 1,
            simple = true,
            space = 160.dp,
        )
    }
    DemoExample("Vertical simple", "Simple mode follows the vertical direction as well.") {
        ElSteps(
            steps = listOf(ElStep("Step 1"), ElStep("Step 2"), ElStep("Step 3")),
            active = 1,
            simple = true,
            direction = ElStepsDirection.Vertical,
        )
    }
    DemoExample("With icon", "Replace step numbers with icons.") {
        ElSteps(
            steps = listOf(
                ElStep("Design", icon = ElIcons.Edit),
                ElStep("Develop", icon = ElIcons.Setting),
                ElStep("Review", icon = ElIcons.View),
            ),
            active = 1,
        )
    }
    DemoExample("Status", "Override individual step statuses and the finish status.") {
        ElSteps(
            steps = listOf(
                ElStep("Done", status = ElStepStatus.Success),
                ElStep("Error", status = ElStepStatus.Error),
                ElStep("Current", status = ElStepStatus.Process),
                ElStep("Waiting", status = ElStepStatus.Wait),
            ),
            active = 2,
            finishStatus = ElStepStatus.Success,
        )
    }
    DemoExample("Center", "Center the step content horizontally.") {
        ElSteps(
            steps = listOf(ElStep("Step 1", "Description 1"), ElStep("Step 2", "Description 2"), ElStep("Step 3", "Description 3")),
            active = 1,
            alignCenter = true,
        )
    }
    DemoExample("Space", "Set a fixed width for every step with the space parameter.") {
        ElSteps(
            steps = listOf(
                ElStep("Done"),
                ElStep("Processing"),
                ElStep("Step 3"),
            ),
            active = 1,
            finishStatus = ElStepStatus.Success,
            space = 200.dp,
        )
    }
    DemoExample("Custom slots", "Replace the icon, title and description with custom content.") {
        ElSteps(
            steps = listOf(
                ElStep(
                    title = "Design",
                    description = "Choose the structure",
                    iconContent = {
                        ElIcon(ElIcons.Edit, size = 12.dp, tint = LocalElTheme.current.colors.white)
                    },
                ),
                ElStep(
                    title = "Develop",
                    titleContent = {
                        BasicText(
                            "Develop (custom)",
                            style = LocalElTheme.current.typography.base.copy(color = LocalElTheme.current.colors.primary),
                        )
                    },
                    descriptionContent = {
                        BasicText(
                            "Custom description content",
                            style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
                        )
                    },
                ),
                ElStep("Review", description = "Verify the result"),
            ),
            active = 1,
        )
    }
    DemoExample("Change event", "The change callback reports each active step change.") {
        var active by remember { mutableStateOf(1) }
        var message by remember { mutableStateOf("Active step: 2") }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSteps(
                steps = listOf(ElStep("Step 1"), ElStep("Step 2"), ElStep("Step 3")),
                active = active,
                onChange = { message = "Active step: ${it + 1}" },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ElButton(onClick = { if (active > 0) active -= 1 }, text = "Previous", size = ElSize.Small)
                ElButton(onClick = { if (active < 2) active += 1 }, text = "Next", size = ElSize.Small, type = ElType.Primary)
                BasicText(
                    message,
                    style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
                )
            }
        }
    }
}
