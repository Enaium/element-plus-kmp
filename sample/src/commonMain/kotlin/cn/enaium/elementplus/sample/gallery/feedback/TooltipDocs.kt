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
fun TooltipDocs() {
    DemoExample("Basic usage", "Tooltips appear on hover.") {
        ElTooltip(text = "This is a tooltip") {
            ElButton(onClick = {}, text = "Hover me")
        }
    }
    DemoExample("Placement", "Place the tooltip around the target.") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElTooltip(text = "Top tooltip", placement = cn.enaium.elementplus.internal.ElPlacement.Top) {
                ElButton(onClick = {}, text = "Top")
            }
            ElTooltip(text = "Right tooltip", placement = cn.enaium.elementplus.internal.ElPlacement.Right) {
                ElButton(onClick = {}, text = "Right")
            }
            ElTooltip(text = "Bottom tooltip", placement = cn.enaium.elementplus.internal.ElPlacement.Bottom) {
                ElButton(onClick = {}, text = "Bottom")
            }
        }
    }
    DemoExample("Light effect", "Use a light background instead of the default dark one.") {
        ElTooltip(text = "Light tooltip", effect = ElTooltipEffect.Light) {
            ElButton(onClick = {}, text = "Light effect", type = ElType.Primary, plain = true)
        }
    }
    DemoExample("Disabled", "A disabled tooltip never opens.") {
        ElTooltip(text = "You cannot see this", disabled = true) {
            ElButton(onClick = {}, text = "Disabled tooltip")
        }
    }

    DemoExample("Rich content", "Tooltips can contain arbitrary composables.") {
        ElTooltip(tooltip = {
            Column {
                BasicText("Rich tooltip", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.white))
                BasicText("With a second line", style = LocalElTheme.current.typography.extraSmall.copy(color = LocalElTheme.current.colors.white.copy(alpha = 0.7f)))
            }
        }) {
            ElButton(onClick = {}, text = "Hover for rich tooltip")
        }
    }

    DemoExample("Theme", "Use a light tooltip surface.") {
        ElTooltip(text = "Light tooltip", effect = ElTooltipEffect.Light) {
            ElButton(onClick = {}, text = "Light theme")
        }
    }

    DemoExample("Controlled", "Control tooltip visibility from application state.") {
        var visible by remember { mutableStateOf(false) }
        ElTooltip(
            text = "Controlled tooltip",
            visible = visible,
            onVisibleChange = { visible = it },
        ) {
            ElButton(onClick = { visible = !visible }, text = if (visible) "Hide tooltip" else "Show tooltip")
        }
    }
}
