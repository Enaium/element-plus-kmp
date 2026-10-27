package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-loading>` overlay. Pass [content] to mask a specific area, or omit it for
 * a full-screen loading surface.
 */
@Composable
fun ElLoading(
    visible: Boolean,
    modifier: Modifier = Modifier,
    text: String? = null,
    background: Color = Color.Transparent,
    fullscreen: Boolean = false,
    spinnerSize: Dp = 30.dp,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val overlay: @Composable () -> Unit = {
        if (visible) {
            Box(
                Modifier
                    .then(if (content != null) Modifier.fillMaxSize() else if (fullscreen) Modifier.fillMaxSize() else Modifier)
                    .background(background),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ElIcon(ElIcons.Loading, size = spinnerSize, tint = colors.primary, spin = true)
                    if (text != null) {
                        Spacer(Modifier.height(8.dp))
                        BasicText(
                            text = text,
                            modifier = Modifier
                                .background(colors.background.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                    }
                }
            }
        }
    }
    if (content != null) {
        Box(modifier) {
            content()
            overlay()
        }
    } else {
        Box(modifier.fillMaxSize()) { overlay() }
    }
}
