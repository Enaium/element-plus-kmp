package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * `<el-infinite-scroll>` as a LazyColumn wrapper. [onLoadMore] is invoked once
 * when the last item becomes visible, until [loading] flips or more rows are
 * appended.
 */
@Composable
fun ElInfiniteScroll(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    distance: Int = 50,
    disabled: Boolean = false,
    loading: Boolean = false,
    finished: Boolean = false,
    loadingText: String? = null,
    finishedText: String? = null,
    onLoadMore: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    val theme = LocalElTheme.current
    val locale = LocalElConfig.current.locale
    val resolvedLoadingText = loadingText ?: locale.infiniteScroll.loading.ifBlank { locale.loading }
    val resolvedFinishedText = finishedText ?: locale.infiniteScroll.finished.ifBlank { locale.emptyText }
    var requestedTotal by remember { mutableStateOf(-1) }
    val density = LocalDensity.current
    val distancePx = with(density) { distance.dp.toPx() }
    LaunchedEffect(state, loading, finished, disabled, distancePx) {
        snapshotFlow {
            val info = state.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()
            val nearEnd = last != null && last.index >= info.totalItemsCount - 2 &&
                last.offset + last.size + distancePx >= info.viewportEndOffset
            nearEnd to info.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (nearEnd, total) ->
                if (!disabled && !loading && !finished && total > 0 && nearEnd && requestedTotal != total) {
                    requestedTotal = total
                    onLoadMore()
                }
            }
    }
    LazyColumn(modifier, state = state) {
        content()
        item(key = "__el_infinite_footer__") {
            Box(
                Modifier.fillMaxWidth().height(48.dp).padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    loading -> androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ElIcon(ElIcons.Loading, size = 16.dp, tint = theme.colors.primary, spin = true)
                        androidx.compose.foundation.layout.Spacer(androidx.compose.ui.Modifier.width(6.dp))
                        androidx.compose.foundation.text.BasicText(
                            text = resolvedLoadingText,
                            style = theme.typography.small.copy(color = theme.colors.textSecondary),
                        )
                    }
                    finished -> androidx.compose.foundation.text.BasicText(
                        text = resolvedFinishedText,
                        style = theme.typography.small.copy(color = theme.colors.textSecondary),
                    )
                }
            }
        }
    }
}
