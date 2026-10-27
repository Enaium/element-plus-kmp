package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAffix
import cn.enaium.elementplus.components.ElAffixPosition
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun AffixDocs() {
    DemoExample(
        "Basic usage",
        "The affixed content stays at the top offset while the rest of the container scrolls behind it.",
    ) {
        AffixSample(offsetTop = 60.dp)
    }
    DemoExample(
        "Offset top",
        "offsetTop keeps a distance between the container's top edge and the pinned content.",
    ) {
        AffixSample(offsetTop = 12.dp)
    }
    DemoExample(
        "Fixed bottom",
        "position = ElAffixPosition.Bottom pins the content to the bottom of the viewport; offsetBottom sets the gap.",
    ) {
        AffixBottomSample()
    }
    DemoExample(
        "Target container",
        "targetHeight is the Compose equivalent of the `target` prop: pinning stops at the bottom edge of the given container.",
    ) {
        AffixTargetSample()
    }
    DemoExample(
        "Change and scroll events",
        "onChange receives the affixed state (the `change` event), onScroll receives the container offset (the `scroll` event).",
    ) {
        AffixEventsSample()
    }
}

@Composable
private fun AffixSample(offsetTop: Dp = 0.dp) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scroll = rememberScrollState()
    Column(Modifier.fillMaxWidth().height(240.dp).verticalScroll(scroll)) {
        Spacer(Modifier.height(60.dp))
        ElAffix(scrollState = scroll, offsetTop = offsetTop) { affixed ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(if (affixed) colors.light(ElType.Primary, 9) else colors.fillLight)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    if (affixed) "Affixed to the top" else "Scroll down to pin this bar",
                    style = theme.typography.small.copy(
                        color = if (affixed) colors.primary else colors.textRegular,
                    ),
                )
                Spacer(Modifier.weight(1f))
                BasicText(
                    "Affix",
                    style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                )
            }
        }
        repeat(10) { index ->
            BasicText(
                text = "Scrollable content ${index + 1}",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                style = theme.typography.small,
            )
        }
    }
}

@Composable
private fun AffixBottomSample() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scroll = rememberScrollState()
    val height = 220.dp
    Column(Modifier.fillMaxWidth().height(height).verticalScroll(scroll)) {
        repeat(8) { index ->
            BasicText(
                text = "Bottom affix content ${index + 1}",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                style = theme.typography.small,
            )
        }
        Spacer(Modifier.height(20.dp))
        ElAffix(
            scrollState = scroll,
            position = ElAffixPosition.Bottom,
            offsetBottom = 20.dp,
            viewportHeight = height,
            modifier = Modifier.fillMaxWidth(),
        ) { affixed ->
            ElButton(
                onClick = {},
                text = if (affixed) "Affixed to bottom" else "Offset bottom 20",
                type = ElType.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun AffixTargetSample() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scroll = rememberScrollState()
    val targetHeight = 200.dp
    Column(Modifier.fillMaxWidth().height(280.dp).verticalScroll(scroll)) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(targetHeight)
                .background(colors.light(ElType.Primary, 9), RoundedCornerShape(4.dp)),
        ) {
            ElAffix(
                scrollState = scroll,
                offsetTop = 16.dp,
                targetHeight = targetHeight,
                modifier = Modifier.fillMaxWidth(),
            ) { affixed ->
                ElButton(
                    onClick = {},
                    text = if (affixed) "Pinned inside the target" else "Target ended",
                    type = ElType.Primary,
                )
            }
        }
        repeat(10) { index ->
            BasicText(
                text = "Content below the target ${index + 1}",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                style = theme.typography.small,
            )
        }
    }
}

@Composable
private fun AffixEventsSample() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scroll = rememberScrollState()
    var event by remember { mutableStateOf("Scroll the content to emit change/scroll events") }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.fillMaxWidth().height(200.dp).verticalScroll(scroll)) {
            Spacer(Modifier.height(40.dp))
            ElAffix(
                scrollState = scroll,
                offsetTop = 12.dp,
                onChange = { fixed -> event = "change: fixed=$fixed" },
                onScroll = { scrollTop, fixed -> event = "scroll: scrollTop=$scrollTop, fixed=$fixed" },
            ) { affixed ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (affixed) colors.light(ElType.Primary, 9) else colors.fillLight)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        if (affixed) "Pinned" else "Event demo",
                        style = theme.typography.small.copy(
                            color = if (affixed) colors.primary else colors.textRegular,
                        ),
                    )
                }
            }
            repeat(10) { index ->
                BasicText(
                    text = "Event content ${index + 1}",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    style = theme.typography.small,
                )
            }
        }
        BasicText(
            text = event,
            style = theme.typography.extraSmall.copy(color = colors.textSecondary),
        )
    }
}
