package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElAlertEffect { Light, Dark }

/** `<el-alert>` with light/dark effects, descriptions and close actions. */
@Composable
fun ElAlert(
    title: String,
    modifier: Modifier = Modifier,
    type: ElType = ElType.Info,
    description: String? = null,
    effect: ElAlertEffect = ElAlertEffect.Light,
    closable: Boolean = true,
    showIcon: Boolean = true,
    center: Boolean = false,
    closeText: String? = null,
    onClose: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val typeColor = colors.colorOf(type)
    val icon = when (type) {
        ElType.Success -> ElIcons.CircleCheckFilled
        ElType.Warning -> ElIcons.WarningFilled
        ElType.Danger -> ElIcons.CircleCloseFilled
        ElType.Info -> ElIcons.InfoFilled
        else -> ElIcons.InfoFilled
    }
    val dark = effect == ElAlertEffect.Dark
    val bg = if (dark) typeColor else colors.light(type, 9)
    val fg = if (dark) colors.white else typeColor
    val textColor = if (dark) colors.white else colors.textRegular
    val shape = RoundedCornerShape(theme.radius.base)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, shape)
            .border(1.dp, if (dark) typeColor else colors.light(type, 8), shape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = if (description != null) Alignment.Top else Alignment.CenterVertically,
    ) {
        if (showIcon) {
            ElIcon(icon, size = if (description != null) 24.dp else 16.dp, tint = fg)
            Spacer(Modifier.width(8.dp))
        }
        Column(
            Modifier.weight(1f),
            horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start,
        ) {
            BasicText(
                text = title,
                style = theme.typography.base.copy(
                    color = if (dark) colors.white else typeColor,
                    fontWeight = if (description != null) FontWeight.Medium else FontWeight.Normal,
                ),
            )
            if (description != null) {
                BasicText(
                    text = description,
                    modifier = Modifier.padding(top = 4.dp),
                    style = theme.typography.small.copy(color = textColor.copy(alpha = 0.9f)),
                )
            }
        }
        if (closable) {
            if (closeText != null) {
                BasicText(
                    text = closeText,
                    modifier = Modifier.clickable { onClose?.invoke() }.padding(4.dp),
                    style = theme.typography.extraSmall.copy(color = fg),
                )
            } else {
                ElIcon(
                    ElIcons.Close,
                    size = 14.dp,
                    tint = fg,
                    contentDescription = locale.close,
                    modifier = Modifier.clickable { onClose?.invoke() }.padding(2.dp),
                )
            }
        }
    }
}
