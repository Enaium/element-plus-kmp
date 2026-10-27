package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/** `<el-page-header>` with back action, title, content and extra slots. */
@Composable
fun ElPageHeader(
    title: String,
    modifier: Modifier = Modifier,
    backText: String? = null,
    subtitle: String? = null,
    icon: ImageVector = ElIcons.ArrowLeft,
    onBack: (() -> Unit)? = null,
    background: androidx.compose.ui.graphics.Color? = null,
    extra: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedBackText = backText ?: locale.pageHeader.title
    Column(
        modifier
            .fillMaxWidth()
            .background(background ?: colors.background),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                Row(
                    Modifier
                        .clickable(onClick = onBack)
                        .padding(end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElIcon(icon, size = 16.dp, tint = colors.primary)
                    Spacer(Modifier.width(6.dp))
                    BasicText(
                        text = resolvedBackText,
                        style = theme.typography.base.copy(color = colors.primary, fontWeight = FontWeight.Medium),
                    )
                }
                BasicText(
                    text = title,
                    style = theme.typography.medium.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                )
                if (subtitle != null) {
                    Spacer(Modifier.width(8.dp))
                    BasicText(
                        text = subtitle,
                        style = theme.typography.small.copy(color = colors.textPlaceholder),
                    )
                }
            } else {
                BasicText(
                    text = title,
                    style = theme.typography.medium.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                )
            }
            Spacer(Modifier.weight(1f))
            extra?.invoke()
        }
        content?.let {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) { it() }
        }
        BoxDivider(colors.borderLighter)
    }
}

@Composable
private fun BoxDivider(color: androidx.compose.ui.graphics.Color) {
    androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth().height(1.dp).background(color))
}
