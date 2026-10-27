package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.elDashedBorder
import cn.enaium.elementplus.internal.elDottedBorder
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElBorderStyle { Solid, Dashed, Dotted }

/**
 * The Border design page as a ready-made container: a surface with the
 * documented border, radius and stroke style.
 */
@Composable
fun ElBorder(
    modifier: Modifier = Modifier,
    color: Color = LocalElTheme.current.colors.border,
    width: Dp = 1.dp,
    radius: Dp = LocalElTheme.current.radius.base,
    borderStyle: ElBorderStyle = ElBorderStyle.Solid,
    dashed: Boolean = false,
    background: Color = LocalElTheme.current.colors.background,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .background(background, shape)
            .then(
                when {
                    dashed || borderStyle == ElBorderStyle.Dashed -> Modifier.elDashedBorder(color, radius, width)
                    borderStyle == ElBorderStyle.Dotted -> Modifier.elDottedBorder(color, radius, width)
                    else -> Modifier.border(width, color, shape)
                }
            ),
    ) {
        content()
    }
}

/** The Color design page as a compact swatch with label and hex value. */
@Composable
fun ElColor(
    color: Color,
    modifier: Modifier = Modifier,
    label: String? = null,
    size: Dp = 40.dp,
) {
    val colors = LocalElTheme.current.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier
                .size(size)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, colors.borderLighter, RoundedCornerShape(4.dp)),
        )
        if (label != null) {
            BasicText(
                text = label,
                style = LocalElTheme.current.typography.extraSmall.copy(color = colors.textSecondary),
            )
        }
    }
}

@Composable
fun ElColorRow(
    label: String,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(
            text = label,
            style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            colors.forEach { ElColor(color = it, size = 32.dp) }
        }
    }
}
