package cn.enaium.elementplus.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * One `<el-breadcrumb-item>`.
 *
 * @param href link destination, the Compose equivalent of the `to` prop.
 * @param target link target (e.g. `_blank`); passed to
 * [ElBreadcrumbContext.onNavigate] and otherwise kept for web parity.
 * @param content replaces the label (the item's `default` slot), e.g. for an
 * anchor element or a custom row.
 */
class ElBreadcrumbItemData(
    val label: String,
    val onClick: (() -> Unit)? = null,
    val icon: ImageVector? = null,
    val disabled: Boolean = false,
    val href: String? = null,
    val target: String? = null,
    val content: (@Composable () -> Unit)? = null,
)

/**
 * Context shared by the breadcrumb and its items: separator configuration and
 * the navigation handler used for [ElBreadcrumbItemData.href].
 */
class ElBreadcrumbContext(
    val separator: String = "/",
    val separatorIcon: ImageVector? = null,
    val separatorClass: String? = null,
    val separatorModifier: Modifier = Modifier,
    val onNavigate: ((href: String, target: String?) -> Unit)? = null,
)

private val LocalElBreadcrumb = staticCompositionLocalOf<ElBreadcrumbContext?> { null }

/**
 * `<el-breadcrumb>` with text or icon separators.
 *
 * `separatorClass` maps the CSS hook to a test tag on the separator (the closest
 * Compose equivalent of a class selector); use [separatorModifier] to style the
 * separator. Links render from [ElBreadcrumbItemData.href] and are opened with
 * [LocalUriHandler] unless [onNavigate] handles the navigation.
 */
@Composable
fun ElBreadcrumb(
    items: List<ElBreadcrumbItemData>,
    modifier: Modifier = Modifier,
    separator: String = "/",
    separatorIcon: ImageVector? = null,
    separatorClass: String? = null,
    separatorModifier: Modifier = Modifier,
    onNavigate: ((href: String, target: String?) -> Unit)? = null,
) {
    val context = ElBreadcrumbContext(separator, separatorIcon, separatorClass, separatorModifier, onNavigate)
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        items.forEachIndexed { index, item ->
            BreadcrumbItemContent(
                item = item,
                isLast = index == items.lastIndex,
                showSeparator = index != items.lastIndex,
                context = context,
            )
        }
    }
}

/**
 * Slot based `<el-breadcrumb>`: compose [ElBreadcrumbItem]s yourself to build
 * custom item layouts. Each item renders the separator of its parent unless it
 * is marked as the last one.
 */
@Composable
fun ElBreadcrumb(
    modifier: Modifier = Modifier,
    separator: String = "/",
    separatorIcon: ImageVector? = null,
    separatorClass: String? = null,
    separatorModifier: Modifier = Modifier,
    onNavigate: ((href: String, target: String?) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val context = ElBreadcrumbContext(separator, separatorIcon, separatorClass, separatorModifier, onNavigate)
    CompositionLocalProvider(LocalElBreadcrumb provides context) {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) { content() }
    }
}

/**
 * A single `<el-breadcrumb-item>` for custom slot based layouts. Inside a slot
 * based [ElBreadcrumb] it renders the shared separator; standalone it renders
 * just the item content. Mark the last item with `isLast = true` so it does not
 * get a trailing separator.
 */
@Composable
fun ElBreadcrumbItem(
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    href: String? = null,
    target: String? = null,
    isLast: Boolean = false,
    content: (@Composable () -> Unit)? = null,
) {
    val context = LocalElBreadcrumb.current
    BreadcrumbItemContent(
        item = ElBreadcrumbItemData(
            label = label,
            onClick = onClick,
            icon = icon,
            disabled = disabled,
            href = href,
            target = target,
            content = content,
        ),
        isLast = isLast,
        showSeparator = context != null && !isLast,
        context = context ?: ElBreadcrumbContext(),
        modifier = modifier,
    )
}

@Composable
private fun BreadcrumbItemContent(
    item: ElBreadcrumbItemData,
    isLast: Boolean,
    showSeparator: Boolean,
    context: ElBreadcrumbContext,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val uriHandler = LocalUriHandler.current
    val href = item.href
    val onClick = item.onClick
    val navigate: (() -> Unit)? = when {
        item.disabled -> null
        onClick != null -> onClick
        href != null -> breadcrumbNavigate(href, item.target, context.onNavigate, uriHandler)
        else -> null
    }
    val contentColor = when {
        item.disabled -> colors.textDisabled
        isLast -> colors.textRegular
        navigate != null -> colors.textPrimary
        else -> colors.textRegular
    }
    val icon = item.icon
    val content = item.content
    Row(
        modifier = modifier.then(
            if (navigate != null) Modifier.clickable(onClick = navigate) else Modifier,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            ElIcon(icon, size = 14.dp, tint = contentColor)
            Spacer(Modifier.width(4.dp))
        }
        if (content != null) {
            content()
        } else {
            BasicText(
                text = item.label,
                style = theme.typography.small.copy(
                    color = contentColor,
                    fontWeight = if (navigate != null && !isLast) FontWeight.SemiBold else FontWeight.Normal,
                ),
            )
        }
    }
    if (showSeparator) {
        BreadcrumbSeparator(context)
    }
}

private fun breadcrumbNavigate(
    href: String,
    target: String?,
    onNavigate: ((href: String, target: String?) -> Unit)?,
    uriHandler: UriHandler,
): () -> Unit {
    if (onNavigate != null) {
        return { onNavigate(href, target) }
    }
    return { uriHandler.openUri(href) }
}

@Composable
private fun BreadcrumbSeparator(context: ElBreadcrumbContext) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val separatorClass = context.separatorClass
    val base = context.separatorModifier.then(
        if (separatorClass != null) Modifier.testTag(separatorClass) else Modifier,
    )
    val separatorIcon = context.separatorIcon
    if (separatorIcon != null) {
        ElIcon(
            separatorIcon,
            size = 12.dp,
            tint = colors.textPlaceholder,
            modifier = base.padding(horizontal = 6.dp),
        )
    } else {
        BasicText(
            text = context.separator,
            modifier = base.padding(horizontal = 9.dp),
            style = theme.typography.small.copy(
                color = colors.textPlaceholder,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
