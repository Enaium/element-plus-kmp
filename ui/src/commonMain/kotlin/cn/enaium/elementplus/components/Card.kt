package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.elSoftShadow
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElCardShadow { Always, Hover, Never }

/** `<el-card>` with header, footer and body slots. */
@Composable
fun ElCard(
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    shadow: ElCardShadow = ElCardShadow.Always,
    contentPadding: Dp = 20.dp,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier = modifier
            .hoverable(interaction)
            .then(
                when (shadow) {
                    ElCardShadow.Always -> Modifier.elSoftShadow(theme.radius.base)
                    ElCardShadow.Never -> Modifier
                    ElCardShadow.Hover -> if (hovered) Modifier.elSoftShadow(theme.radius.base) else Modifier
                }
            )
            .background(colors.background, shape)
            .border(1.dp, colors.borderLight, shape),
    ) {
        Column {
            if (header != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = contentPadding, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    header()
                }
            }
            Box(Modifier.fillMaxWidth().padding(contentPadding)) { content() }
            if (footer != null) {
                Box(Modifier.fillMaxWidth().padding(horizontal = contentPadding, vertical = 14.dp)) { footer() }
            }
        }
    }
}
