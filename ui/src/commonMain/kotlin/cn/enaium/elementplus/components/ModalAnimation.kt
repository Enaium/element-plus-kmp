package cn.enaium.elementplus.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay

internal const val ElModalAnimationMillis = 300

internal data class ModalAnimationState(
    val mounted: Boolean,
    val contentVisible: Boolean,
)

/**
 * Keeps a modal composed while its panel exit animation plays. The scrim is
 * intentionally not animated; only the panel uses enter/exit transitions.
 */
@Composable
internal fun rememberModalAnimationState(
    visible: Boolean,
    durationMillis: Int = ElModalAnimationMillis,
): ModalAnimationState {
    var mounted by remember { mutableStateOf(visible) }
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            mounted = true
            withFrameNanos { }
            contentVisible = true
        } else if (mounted) {
            contentVisible = false
            delay(durationMillis.toLong() + 24)
            mounted = false
        }
    }
    return ModalAnimationState(mounted = mounted, contentVisible = contentVisible)
}
