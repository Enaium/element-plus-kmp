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
fun AffixDocs() {
    DemoExample("Basic usage", "The affix content stays at the top of its scroll container.") {
        AffixSample()
    }
    DemoExample("Offset top", "Add an offset to leave space above the affixed bar.") {
        AffixSample(offsetTop = 12.dp)
    }
}

@Composable
private fun AffixSample(offsetTop: Dp = 0.dp) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scroll = rememberScrollState()
    Column(Modifier.fillMaxWidth().height(200.dp).verticalScroll(scroll)) {
        ElAffix(scrollState = scroll, offsetTop = offsetTop) { affixed ->
            Row(
                Modifier.fillMaxWidth().background(if (affixed) colors.light(ElType.Primary, 9) else colors.fillLight).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    if (affixed) "Affixed to the top" else "Scroll down to pin this bar",
                    style = theme.typography.small.copy(color = if (affixed) colors.primary else colors.textRegular),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                BasicText("Affix", style = theme.typography.extraSmall.copy(color = colors.textSecondary))
            }
        }
        repeat(10) { index ->
            BasicText("Scrollable content ${index + 1}", Modifier.fillMaxWidth().padding(12.dp), style = theme.typography.small)
        }
    }
}
