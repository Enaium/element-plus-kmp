package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

/**
 * `<el-backtop>` floating scroll-to-top button. Attach it to a [ScrollState] or
 * a [LazyListState]; visibility is derived from the current offset.
 */
@Composable
fun ElBacktop(
    modifier: Modifier = Modifier,
    visibilityHeight: Dp = 200.dp,
    scrollState: ScrollState? = null,
    listState: LazyListState? = null,
    size: Dp = 40.dp,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scope = rememberCoroutineScope()
    val offset = when {
        listState != null -> listState.firstVisibleItemIndex * 1000 + listState.firstVisibleItemScrollOffset
        scrollState != null -> scrollState.value
        else -> 0
    }
    val visible = offset > visibilityHeight.value
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier
                .size(size)
                .shadow(4.dp, CircleShape)
                .background(colors.background, CircleShape)
                .border(1.dp, colors.borderLight, CircleShape)
                .clickable {
                    scope.launch {
                        if (listState != null) listState.animateScrollToItem(0)
                        else scrollState?.animateScrollTo(0)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            if (content != null) content()
            else ElIcon(ElIcons.CaretTop, size = 18.dp, tint = colors.primary)
        }
    }
}
