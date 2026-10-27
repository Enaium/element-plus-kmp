package cn.enaium.elementplus.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.max

/**
 * `<el-affix>` for a Compose scroll container. Pass the same [scrollState] used
 * by the enclosing `verticalScroll`; the content is translated to stay at
 * [offsetTop] and reports whether it is currently pinned.
 */
@Composable
fun ElAffix(
    modifier: Modifier = Modifier,
    offsetTop: Dp = 0.dp,
    scrollState: ScrollState = rememberScrollState(),
    zIndex: Float = 2f,
    content: @Composable (affixed: Boolean) -> Unit,
) {
    val density = LocalDensity.current
    val offsetTopPx = with(density) { offsetTop.toPx() }
    var baseY by remember { mutableFloatStateOf(-1f) }
    val translation = if (baseY < 0f) 0f else max(0f, scrollState.value - (baseY - offsetTopPx))
    val affixed = translation > 0f
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .zIndex(zIndex)
            .onGloballyPositioned { coordinates ->
                if (baseY < 0f) baseY = coordinates.positionInParent().y
            }
            .graphicsLayer { translationY = translation },
    ) {
        content(affixed)
    }
}
