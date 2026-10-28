package cn.enaium.elementplus.sample.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElLink
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * One example block inside a component page. Mirrors the `:::demo` blocks in
 * the Element Plus documentation.
 */
@Composable
fun DemoExample(
    title: String,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val sourcePath = LocalSampleSourcePath.current
    val sourceCode = sourcePath?.let { SampleDemoSources.find(it, title) }
    var showSource by remember(title, sourcePath) { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(theme.colors.background, RoundedCornerShape(6.dp))
            .border(1.dp, theme.colors.borderLighter, RoundedCornerShape(6.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                BasicText(
                    text = title,
                    style = theme.typography.medium.copy(
                        color = theme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                if (description != null) {
                    BasicText(
                        text = description,
                        style = theme.typography.small.copy(color = theme.colors.textSecondary),
                    )
                }
            }
            if (sourceCode != null) {
                ElLink(
                    text = if (showSource) "Hide source" else "View source",
                    onClick = { showSource = !showSource },
                )
            }
        }
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
        if (showSource && sourceCode != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.colors.fillLight)
                    .border(1.dp, theme.colors.borderLight, RoundedCornerShape(4.dp))
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
                    .testTag("demo-source-code"),
            ) {
                SelectionContainer {
                    BasicText(
                        text = sourceCode,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = theme.colors.textRegular,
                        ),
                        softWrap = false,
                    )
                }
            }
        }
    }
}

@Composable
fun DemoRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
    androidx.compose.foundation.layout.FlowRow(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

@Composable
fun DemoColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) { content() }
}

@Composable
fun DemoHint(text: String) {
    val theme = LocalElTheme.current
    BasicText(text = text, style = theme.typography.extraSmall.copy(color = theme.colors.textSecondary))
}

@Composable
fun DemoSpacer() {
    androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
}

@Composable
fun DemoLabel(text: String) {
    val theme = LocalElTheme.current
    BasicText(
        text = text,
        style = theme.typography.extraSmall.copy(color = theme.colors.textSecondary, fontWeight = FontWeight.Medium),
    )
}

@Composable
fun DemoRowSpaced(content: @Composable () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}
