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
fun LoadingDocs() {
    var loading by remember { mutableStateOf(false) }
    var fullscreen by remember { mutableStateOf(false) }
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Overlay a loading indicator on a content area.") {
        ElSwitch(checked = loading, onCheckedChange = { loading = it }, activeText = "Loading", inactiveText = "Idle")
        ElLoading(
            visible = loading,
            text = "Loading...",
            background = colors.background.copy(alpha = 0.75f),
        ) {
            Box(
                Modifier.fillMaxWidth().height(140.dp).background(colors.fillLight, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) { BasicText("Content area", style = LocalElTheme.current.typography.base.copy(color = colors.textSecondary)) }
        }
    }
    DemoExample("Fullscreen", "A fullscreen overlay covers the whole sample area.") {
        ElButton(onClick = { fullscreen = true }, text = "Show fullscreen loading")
        if (fullscreen) {
            Box(Modifier.fillMaxWidth().height(200.dp)) {
                ElLoading(
                    visible = fullscreen,
                    text = "Loading page...",
                    fullscreen = true,
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
    DemoExample("Customization", "Change the spinner size.") {
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
}
