package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

@Immutable
private data class ElCollapseContext(
    val activeNames: List<String>,
    val toggle: (String, Boolean) -> Unit,
    val accordion: Boolean,
    val expandIconPosition: ElCollapseIconPosition?,
)

private val LocalElCollapse = staticCompositionLocalOf<ElCollapseContext?> { null }

/** `<el-collapse>`. Children are [ElCollapseItem]s and share the active list. */
@Composable
fun ElCollapse(
    activeNames: List<String>,
    onActiveChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    accordion: Boolean = false,
    expandIconPosition: ElCollapseIconPosition? = null,
    beforeCollapse: (suspend (name: String, expanded: Boolean) -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scope = rememberCoroutineScope()
    val latestActive by rememberUpdatedState(activeNames)
    val latestOnChange by rememberUpdatedState(onActiveChange)
    val context = remember(activeNames, accordion, onActiveChange, expandIconPosition, beforeCollapse, scope) {
        ElCollapseContext(
            activeNames = activeNames,
            accordion = accordion,
            expandIconPosition = expandIconPosition,
            toggle = { name, expanded ->
                val apply: () -> Unit = {
                    if (accordion) {
                        latestOnChange(if (expanded) listOf(name) else emptyList())
                    } else {
                        latestOnChange(if (expanded) latestActive + name else latestActive - name)
                    }
                }
                val guard = beforeCollapse
                if (guard == null) {
                    apply()
                } else {
                    scope.launch {
                        if (guard(name, expanded)) apply()
                    }
                }
            },
        )
    }
    Column(
        modifier
            .fillMaxWidth()
            .border(1.dp, colors.borderLight, RoundedCornerShape(theme.radius.base))
            .background(colors.background, RoundedCornerShape(theme.radius.base)),
    ) {
        CompositionLocalProvider(LocalElCollapse provides context) { content() }
    }
}

/** `<el-collapse-item>`; clickable header with animated content. */
enum class ElCollapseIconPosition { Start, End }

@Composable
fun ElCollapseItem(
    name: String,
    title: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    icon: ImageVector = ElIcons.ArrowRight,
    iconPosition: ElCollapseIconPosition = ElCollapseIconPosition.End,
    titleContent: (@Composable () -> Unit)? = null,
    iconContent: (@Composable (isActive: Boolean) -> Unit)? = null,
    titleSlot: (@Composable (isActive: Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val context = LocalElCollapse.current ?: return
    val expanded = name in context.activeNames
    val resolvedIconPosition = context.expandIconPosition ?: iconPosition
    val expandIcon: @Composable () -> Unit = {
        if (iconContent != null) {
            iconContent(expanded)
        } else {
            ElIcon(
                icon,
                size = 12.dp,
                tint = if (disabled) colors.textDisabled else colors.textPlaceholder,
                modifier = Modifier.rotate(if (expanded) 90f else 0f),
            )
        }
    }
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .then(if (disabled) Modifier else Modifier.clickable { context.toggle(name, !expanded) })
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (resolvedIconPosition == ElCollapseIconPosition.Start) {
                expandIcon()
                Spacer(Modifier.width(8.dp))
            }
            when {
                titleSlot != null -> Box(Modifier.weight(1f)) { titleSlot(expanded) }
                titleContent != null -> Box(Modifier.weight(1f)) { titleContent() }
                else -> BasicText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = theme.typography.base.copy(
                        color = if (disabled) colors.textDisabled else colors.textPrimary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    ),
                )
            }
            if (resolvedIconPosition == ElCollapseIconPosition.End) {
                expandIcon()
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                content()
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.borderLighter))
    }
}
