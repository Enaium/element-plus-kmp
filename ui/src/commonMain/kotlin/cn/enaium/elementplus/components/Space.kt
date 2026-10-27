package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ElSpaceSize(val value: Dp) {
    Small(8.dp),
    Default(12.dp),
    Large(16.dp),
}

/**
 * `<el-space>`: consistent gaps between inline items. [wrap] is honored for
 * horizontal spaces by using [FlowRow].
 */
@Composable
fun ElSpace(
    modifier: Modifier = Modifier,
    size: ElSpaceSize = ElSpaceSize.Small,
    spacing: Dp? = null,
    vertical: Boolean = false,
    wrap: Boolean = false,
    fill: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit,
) {
    val gap = spacing ?: size.value
    if (vertical) {
        Column(
            modifier = modifier.then(if (fill) Modifier.fillMaxWidth() else Modifier),
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = horizontalAlignment,
        ) {
            content()
        }
    } else if (wrap) {
        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
        androidx.compose.foundation.layout.FlowRow(
            modifier = modifier.then(if (fill) Modifier.fillMaxWidth() else Modifier),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            content()
        }
    } else {
        Row(
            modifier = modifier.then(if (fill) Modifier.fillMaxWidth() else Modifier),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = verticalAlignment,
        ) {
            content()
        }
    }
}

@Composable
fun ElSpaceItem(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(modifier) { content() }
}
