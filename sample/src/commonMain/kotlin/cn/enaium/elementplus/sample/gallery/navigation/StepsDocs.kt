package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.*
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
    DemoExample("Basic usage", "Steps show progress through a process.") {
        ElSteps(steps = steps, active = 2)
    }
    DemoExample("With description", "Add a description to each step.") {
        ElSteps(steps = steps, active = 1)
    }
    DemoExample("Vertical", "Vertical steps are useful in narrow layouts.") {
        ElSteps(steps = steps, active = 2, direction = ElStepsDirection.Vertical, modifier = Modifier.width(300.dp))
    }
    DemoExample("Simple", "A compact simple mode.") {
        ElSteps(steps = steps, active = 2, simple = true)
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
    DemoExample("Status", "Override individual step statuses.") {
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
}
