package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElResultStatus { Primary, Success, Warning, Info, Error }

/** `<el-result>` with a status illustration, title, subtitle and extra actions. */
@Composable
fun ElResult(
    modifier: Modifier = Modifier,
    title: String? = null,
    subTitle: String? = null,
    status: ElResultStatus = ElResultStatus.Info,
    iconSize: Dp = 64.dp,
    icon: (@Composable () -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    subTitleContent: (@Composable () -> Unit)? = null,
    extra: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val (statusIcon, color) = when (status) {
        ElResultStatus.Primary -> ElIcons.InfoFilled to colors.primary
        ElResultStatus.Success -> ElIcons.CircleCheckFilled to colors.success
        ElResultStatus.Warning -> ElIcons.WarningFilled to colors.warning
        ElResultStatus.Info -> ElIcons.InfoFilled to colors.info
        ElResultStatus.Error -> ElIcons.CircleCloseFilled to colors.danger
    }
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) {
            icon()
        } else {
            Box(
                Modifier
                    .size(iconSize)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                ElIcon(statusIcon, size = iconSize * 0.56f, tint = color)
            }
        }
        Spacer(Modifier.height(16.dp))
        if (titleContent != null) {
            titleContent()
            Spacer(Modifier.height(8.dp))
        } else if (title != null) {
            BasicText(
                text = title,
                style = theme.typography.extraLarge.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
            )
            Spacer(Modifier.height(8.dp))
        }
        if (subTitleContent != null) {
            subTitleContent()
        } else if (subTitle != null) {
            BasicText(
                text = subTitle,
                style = theme.typography.base.copy(color = colors.textSecondary),
            )
        }
        if (extra != null) {
            Spacer(Modifier.height(20.dp))
            extra()
        }
    }
}
