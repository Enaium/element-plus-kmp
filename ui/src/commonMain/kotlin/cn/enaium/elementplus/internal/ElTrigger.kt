package cn.enaium.elementplus.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Observes a primary press before the trigger content receives it. A normal
 * [Modifier.clickable] on the wrapper does not fire when the content is itself
 * clickable (for example an `ElButton`), because the child consumes the event;
 * inspecting the initial pass keeps the popover/confirm trigger working for all
 * kinds of trigger content without preventing the child's own click handler.
 */
internal fun Modifier.elTriggerPress(
    enabled: Boolean,
    onPress: () -> Unit,
): Modifier = if (!enabled) {
    this
} else {
    pointerInput(enabled, onPress) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (
                    event.type == PointerEventType.Press &&
                    event.changes.any { it.pressed && !it.previousPressed } &&
                    !event.buttons.isSecondaryPressed
                ) {
                    onPress()
                }
            }
        }
    }
}
