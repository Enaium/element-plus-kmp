package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * `<el-auto-resizer>`: reports the available size to a child, typically a
 * [ElVirtualizedTable], which requires a bounded height.
 */
@Composable
fun ElAutoResizer(
    modifier: Modifier = Modifier,
    content: @Composable (width: Dp, height: Dp) -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        content(maxWidth, maxHeight)
    }
}
