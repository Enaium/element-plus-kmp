package cn.enaium.elementplus.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

class ElBreadcrumbItemData(
    val label: String,
    val onClick: (() -> Unit)? = null,
    val icon: ImageVector? = null,
    val disabled: Boolean = false,
)

/** `<el-breadcrumb>` with text or icon separators. */
@Composable
fun ElBreadcrumb(
    items: List<ElBreadcrumbItemData>,
    modifier: Modifier = Modifier,
    separator: String = "/",
    separatorIcon: ImageVector? = null,
    separatorClass: String? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                if (separatorIcon != null) {
                    ElIcon(
                        separatorIcon,
                        size = 12.dp,
                        tint = colors.textPlaceholder,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                } else {
                    BasicText(
                        text = separator,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = theme.typography.small.copy(color = colors.textPlaceholder),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .then(if (item.onClick != null && !item.disabled) Modifier.clickable(onClick = item.onClick) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (item.icon != null) {
                    ElIcon(item.icon, size = 14.dp, tint = if (index == items.lastIndex) colors.textPrimary else colors.textRegular)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 4.dp))
                }
                BasicText(
                    text = item.label,
                    style = theme.typography.small.copy(
                        color = when {
                            item.disabled -> colors.textDisabled
                            index == items.lastIndex -> colors.textPrimary
                            else -> colors.textRegular
                        },
                        fontWeight = if (index == items.lastIndex) FontWeight.Medium else FontWeight.Normal,
                    ),
                )
            }
        }
    }
}

/** A single breadcrumb item for custom slot based layouts. */
@Composable
fun ElBreadcrumbItem(
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
) {
    val item = ElBreadcrumbItemData(label, onClick, icon)
    ElBreadcrumb(listOf(item), modifier)
}
