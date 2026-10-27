package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-page-header>` with back action, title, content and extra slots.
 *
 * The named `breadcrumb`, `titleContent`, `iconContent`, `headerContent` and
 * `backContent` parameters mirror the upstream `breadcrumb`, `title`, `icon`,
 * `content` and back-button slots. `content` is the default/main slot.
 */
@Composable
fun ElPageHeader(
    title: String,
    modifier: Modifier = Modifier,
    backText: String? = null,
    subtitle: String? = null,
    icon: ImageVector? = ElIcons.ArrowLeft,
    onBack: (() -> Unit)? = null,
    background: Color? = null,
    extra: (@Composable () -> Unit)? = null,
    breadcrumb: (@Composable () -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    iconContent: (@Composable () -> Unit)? = null,
    headerContent: (@Composable () -> Unit)? = null,
    backContent: (@Composable () -> Unit)? = null,
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
        if (breadcrumb != null) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            ) {
                breadcrumb()
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    Row(
                        Modifier
                            .clickable(onClick = onBack)
                            .padding(end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (backContent != null) {
                            backContent()
                        } else {
                            if (iconContent != null) {
                                iconContent()
                            } else if (icon != null) {
                                ElIcon(icon, size = 16.dp, tint = colors.primary)
                            }
                            if (iconContent != null || icon != null) {
                                Spacer(Modifier.width(6.dp))
                            }
                            BasicText(
                                text = resolvedBackText,
                                style = theme.typography.base.copy(
                                    color = colors.primary,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                    }
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(16.dp)
                            .background(colors.borderLight),
                    )
                    Spacer(Modifier.width(12.dp))
                }
                if (headerContent != null) {
                    headerContent()
                } else {
                    if (titleContent != null) {
                        titleContent()
                    } else {
                        BasicText(
                            text = title,
                            style = theme.typography.medium.copy(
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                    if (subtitle != null) {
                        Spacer(Modifier.width(8.dp))
                        BasicText(
                            text = subtitle,
                            style = theme.typography.small.copy(color = colors.textPlaceholder),
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            extra?.invoke()
        }
        content?.let {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) { it() }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.borderLighter))
    }
}
