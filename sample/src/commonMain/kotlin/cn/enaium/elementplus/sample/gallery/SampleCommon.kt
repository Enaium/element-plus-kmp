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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(theme.colors.background, RoundedCornerShape(6.dp))
            .border(1.dp, theme.colors.borderLighter, RoundedCornerShape(6.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
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
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
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
