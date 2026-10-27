package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun AlertDocs() {
    DemoExample("Basic usage", "Alerts provide inline contextual feedback.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElAlert("Success alert", type = ElType.Success)
            ElAlert("Warning alert", type = ElType.Warning)
            ElAlert("Info alert", type = ElType.Info)
            ElAlert("Error alert", type = ElType.Danger)
        }
    }
    DemoExample("With description", "Add a longer description below the title.") {
        ElAlert(
            title = "With description",
            description = "A detailed message that explains the alert and what the user should do next.",
            type = ElType.Primary,
        )
    }
    DemoExample("Closable", "Provide a close action.") {
        var visible by remember { mutableStateOf(true) }
        if (visible) {
            ElAlert("This alert can be closed", type = ElType.Info, onClose = { visible = false })
        } else {
            ElButton(onClick = { visible = true }, text = "Show alert again", size = ElSize.Small)
        }
    }
    DemoExample("Dark effect", "Use a dark background for stronger emphasis.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElAlert("Dark success", type = ElType.Success, effect = ElAlertEffect.Dark)
            ElAlert("Dark warning", type = ElType.Warning, effect = ElAlertEffect.Dark)
            ElAlert("Dark error", type = ElType.Danger, effect = ElAlertEffect.Dark)
        }
    }
    DemoExample("Center and icon", "Center the content or hide the icon.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElAlert("Centered alert", type = ElType.Success, center = true)
            ElAlert("No icon", type = ElType.Info, showIcon = false)
        }
    }

    DemoExample("Theme", "Use light or dark alert surfaces.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElAlert("Light success", type = ElType.Success, effect = ElAlertEffect.Light, closable = false)
            ElAlert("Dark success", type = ElType.Success, effect = ElAlertEffect.Dark, closable = false)
        }
    }
    DemoExample("Customizable close button", "Replace the close icon with text.") {
        ElAlert(
            title = "Custom close text",
            type = ElType.Warning,
            closeText = "Got it",
            onClose = { ElMessage.show("Alert closed") },
        )
    }
    DemoExample("With icon", "Icons communicate the alert type.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElAlert("Success", type = ElType.Success, showIcon = true, closable = false)
            ElAlert("Info", type = ElType.Info, showIcon = true, closable = false)
            ElAlert("Warning", type = ElType.Warning, showIcon = true, closable = false)
            ElAlert("Error", type = ElType.Danger, showIcon = true, closable = false)
        }
    }
    DemoExample("Centered text", "Center the title and description.") {
        ElAlert("Centered alert", type = ElType.Info, center = true, closable = false)
    }
    DemoExample("With icon and description", "Combine an icon with a longer description.") {
        ElAlert(
            title = "Success",
            description = "This alert combines an icon with a description.",
            type = ElType.Success,
            showIcon = true,
            center = false,
            closable = false,
        )
    }

    DemoExample("Unclosable alert", "Hide the close action so the alert stays visible.") {
        ElAlert(
            title = "Unclosable alert",
            type = ElType.Success,
            closable = false,
        )
    }

    DemoExample("Custom close icon", "Replace the default close icon.") {
        ElAlert(
            title = "Custom close icon",
            type = ElType.Info,
            closeIcon = ElIcons.CircleClose,
            onClose = { ElMessage.show("Alert closed") },
        )
    }
}
