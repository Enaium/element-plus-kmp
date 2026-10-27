package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

/**
 * `<el-autocomplete>`: input with asynchronously/lazily fetched suggestions.
 * [fetchSuggestions] is invoked in a debounced coroutine, so it can be an
 * expensive pure function or read from a cache.
 */
@Composable
fun ElAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    fetchSuggestions: (String) -> List<String>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    debounce: Long = 300,
    triggerOnFocus: Boolean = true,
    onSelect: ((String) -> Unit)? = null,
    emptyText: String? = null,
) {
    val theme = LocalElTheme.current
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val effectivePlaceholder = placeholder ?: locale.select.placeholder
    val effectiveEmptyText = emptyText ?: locale.select.noMatch
    var focused by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    var loading by remember { mutableStateOf(false) }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val providerHover by interaction.collectIsHoveredAsState()

    LaunchedEffect(value, focused) {
        if (!focused) {
            visible = false
            return@LaunchedEffect
        }
        if (!triggerOnFocus && value.isBlank()) {
            visible = false
            return@LaunchedEffect
        }
        loading = true
        delay(debounce)
        suggestions = fetchSuggestions(value)
        loading = false
        visible = true
    }

    Box(modifier) {
        ElInput(
            value = value,
            onValueChange = {
                onValueChange(it)
                visible = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } },
            placeholder = effectivePlaceholder,
            size = resolvedSize,
            disabled = disabled,
            clearable = clearable,
            prefix = { ElIcon(ElIcons.Search, size = 14.dp) },
            onFocusChanged = { focused = it },
        )
        ElPopper(
            visible = visible && (suggestions.isNotEmpty() || effectiveEmptyText.isNotEmpty()),
            onDismissRequest = { visible = false },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
        ) {
            ElPopperSurface(modifier = Modifier.widthIn(min = fieldWidth).width(fieldWidth.coerceAtLeast(200.dp))) {
                Column(Modifier.heightIn(max = 274.dp)) {
                    if (suggestions.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                            BasicText(
                                text = effectiveEmptyText,
                                style = theme.typography.small.copy(color = theme.colors.textSecondary),
                            )
                        }
                    }
                    suggestions.forEach { item ->
                        val itemInteraction = remember(item) { MutableInteractionSource() }
                        val hovered by itemInteraction.collectIsHoveredAsState()
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(if (hovered) theme.colors.fillLight else theme.colors.backgroundOverlay)
                                .clickable {
                                    onValueChange(item)
                                    onSelect?.invoke(item)
                                    visible = false
                                }
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                        ) {
                            BasicText(
                                text = item,
                                style = theme.typography.base.copy(
                                    color = if (hovered) theme.colors.primary else theme.colors.textRegular,
                                    fontSize = resolvedSize.fontSize,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
