package cn.enaium.elementplus.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

data class ElAnchorLink(
    val href: String,
    val title: String,
    val children: List<ElAnchorLink> = emptyList(),
)

/**
 * `<el-anchor>` with nested links and optional scroll-spy / scroll-to-section
 * behavior. [sectionOffsets] are pixel offsets inside [scrollState].
 */
@Composable
fun ElAnchor(
    links: List<ElAnchorLink>,
    activeHref: String,
    onActiveChange: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    scrollState: ScrollState? = null,
    sectionOffsets: Map<String, Int> = emptyMap(),
    offsetTop: Dp = 0.dp,
    onLinkClick: ((String) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val offsetTopPx = with(density) { offsetTop.roundToPx() }
    LaunchedEffect(scrollState?.value, sectionOffsets, offsetTopPx) {
        if (scrollState == null || sectionOffsets.isEmpty() || onActiveChange == null) return@LaunchedEffect
        val current = scrollState.value + offsetTopPx
        val active = sectionOffsets.entries.filter { it.value <= current }.maxByOrNull { it.value }
        if (active != null && active.key != activeHref) onActiveChange(active.key)
    }
    Column(
        modifier
            .width(200.dp)
            .background(colors.background)
            .padding(vertical = 8.dp),
    ) {
        links.forEach { link ->
            AnchorLinkRow(
                link = link,
                depth = 0,
                activeHref = activeHref,
                onActivate = { href ->
                    onActiveChange?.invoke(href)
                    onLinkClick?.invoke(href)
                    if (onLinkClick == null && scrollState != null) {
                        sectionOffsets[href]?.let { offset ->
                            scope.launch { scrollState.animateScrollTo((offset - offsetTopPx).coerceAtLeast(0)) }
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun AnchorLinkRow(
    link: ElAnchorLink,
    depth: Int,
    activeHref: String,
    onActivate: (String) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val exactActive = link.href == activeHref
    val active = exactActive || link.children.any { it.href == activeHref || it.children.any { child -> child.href == activeHref } }
    Column {
        Row(
            Modifier
                .fillMaxHeight()
                .padding(start = 14.dp * depth)
                .clickable { onActivate(link.href) }
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (exactActive) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(colors.primary, RoundedCornerShape(1.dp)),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.width(6.dp))
            }
            BasicText(
                text = link.title,
                style = theme.typography.small.copy(
                    color = if (exactActive) colors.primary else if (active) colors.textPrimary else colors.textSecondary,
                    fontWeight = if (active) androidx.compose.ui.text.font.FontWeight.Medium else androidx.compose.ui.text.font.FontWeight.Normal,
                ),
            )
        }
        link.children.forEach { child ->
            AnchorLinkRow(
                link = child,
                depth = depth + 1,
                activeHref = activeHref,
                onActivate = onActivate,
            )
        }
    }
}
