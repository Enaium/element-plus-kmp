package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElCard
import cn.enaium.elementplus.components.ElCardShadow
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplitterDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Basic splitter", "Drag the dividers to resize the panes.") {
        ElSplitter(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            panelCount = 2,
            minSize = 80.dp,
        ) { index ->
            Box(
                Modifier.fillMaxSize().background(
                    if (index == 0) colors.light(ElType.Primary, 8) else colors.light(ElType.Success, 8),
                ),
                contentAlignment = Alignment.Center,
            ) { BasicText("Panel ${index + 1}", style = theme.typography.small) }
        }
    }
    DemoExample("Three panes", "Any number of panes can be created.") {
        ElSplitter(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            panelCount = 3,
            minSize = 60.dp,
            initialSizes = listOf(0.25f, 0.5f, 0.25f),
        ) { index ->
            Box(
                Modifier.fillMaxSize().background(colors.light(ElType.Primary, 7 + index),),
                contentAlignment = Alignment.Center,
            ) { BasicText("Pane ${index + 1}", style = theme.typography.small) }
        }
    }
    DemoExample("Vertical splitter", "Change the direction to stack panes.") {
        ElSplitter(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            direction = ElSplitterDirection.Vertical,
            panelCount = 2,
            minSize = 60.dp,
        ) { index ->
            Box(
                Modifier.fillMaxSize().background(colors.light(ElType.Warning, 8)),
                contentAlignment = Alignment.Center,
            ) { BasicText("Row ${index + 1}", style = theme.typography.small) }
        }
    }

    DemoExample("Panel size", "Set initial panel proportions.") {
        ElSplitter(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            initialSizes = listOf(0.25f, 0.75f),
        ) { index ->
            Box(Modifier.fillMaxSize().background(LocalElTheme.current.colors.fillLight), contentAlignment = Alignment.Center) {
                BasicText("Panel ${index + 1}")
            }
        }
    }

    DemoExample("Disable drag", "Keep the layout but disable divider dragging.") {
        ElSplitter(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            disabled = true,
        ) { index ->
            Box(Modifier.fillMaxSize().background(LocalElTheme.current.colors.fillLight), contentAlignment = Alignment.Center) {
                BasicText("Panel ${index + 1}")
            }
        }
    }
}
