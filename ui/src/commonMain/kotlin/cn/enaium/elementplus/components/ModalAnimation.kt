package cn.enaium.elementplus.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay

internal const val ElModalAnimationMillis = 300

/** Lifecycle phase of a modal panel, mirroring Element Plus' open/opened/close/closed events. */
enum class ElModalPhase { Closed, Opening, Open, Closing }

internal data class ModalAnimationState(
    val mounted: Boolean,
    val contentVisible: Boolean,
    val phase: ElModalPhase,
)

/**
 * Keeps a modal composed while its panel exit animation plays. The scrim is
 * intentionally not animated; only the panel uses enter/exit transitions.
 *
 * [openDelayMillis] / [closeDelayMillis] mirror Element Plus'
 * `open-delay` / `close-delay`; [onOpened] and [onClosed] fire after the
 * enter and exit transitions settle.
 */
@Composable
internal fun rememberModalAnimationState(
    visible: Boolean,
    durationMillis: Int = ElModalAnimationMillis,
    openDelayMillis: Int = 0,
    closeDelayMillis: Int = 0,
    onOpened: (() -> Unit)? = null,
    onClosed: (() -> Unit)? = null,
): ModalAnimationState {
    var mounted by remember { mutableStateOf(visible && openDelayMillis <= 0) }
    var contentVisible by remember { mutableStateOf(false) }
    var phase by remember {
        mutableStateOf(if (visible) ElModalPhase.Opening else ElModalPhase.Closed)
    }
    val currentOnOpened by rememberUpdatedState(onOpened)
    val currentOnClosed by rememberUpdatedState(onClosed)

    LaunchedEffect(visible) {
        if (visible) {
            if (openDelayMillis > 0) {
                phase = ElModalPhase.Opening
                delay(openDelayMillis.toLong())
            }
            mounted = true
            phase = ElModalPhase.Opening
            withFrameNanos { }
            contentVisible = true
            delay(durationMillis.toLong())
            phase = ElModalPhase.Open
            currentOnOpened?.invoke()
        } else if (mounted) {
            if (closeDelayMillis > 0) delay(closeDelayMillis.toLong())
            contentVisible = false
            phase = ElModalPhase.Closing
            delay(durationMillis.toLong() + 24)
            mounted = false
            phase = ElModalPhase.Closed
            currentOnClosed?.invoke()
        }
    }
    return ModalAnimationState(mounted = mounted, contentVisible = contentVisible, phase = phase)
}
