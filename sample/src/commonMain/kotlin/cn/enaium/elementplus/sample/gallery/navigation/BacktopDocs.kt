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
fun BacktopDocs() {
    DemoExample("Basic usage", "The button appears after the content is scrolled.") {
        BacktopSample()
    }
    DemoExample("Custom content", "Replace the default icon with custom content.") {
        BacktopSample(custom = true)
    }
}

@Composable
private fun BacktopSample(custom: Boolean = false) {
    val theme = LocalElTheme.current
    val scroll = rememberScrollState()
    Box(Modifier.fillMaxWidth().height(200.dp)) {
        Column(Modifier.fillMaxWidth().height(200.dp).verticalScroll(scroll)) {
            repeat(12) { index ->
                BasicText("Backtop content ${index + 1}", Modifier.fillMaxWidth().padding(12.dp), style = theme.typography.small)
            }
        }
        ElBacktop(
            modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp),
            scrollState = scroll,
            visibilityHeight = 80.dp,
            content = if (custom) {
                { BasicText("TOP", style = theme.typography.extraSmall.copy(color = theme.colors.primary)) }
            } else null,
        )
    }
}
