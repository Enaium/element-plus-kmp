package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-loading>` overlay. Pass [content] to mask a specific area, or omit it for
 * a full-screen loading surface.
 *
 * @param spinner replaces the built-in spinner icon (`element-loading-spinner`).
 * @param textContent renders rich loading text instead of the [text] string.
 * @param lock consumes pointer input while loading so the masked content cannot
 * be scrolled or clicked (`v-loading.lock`).
 * @param overlayModifier extra styling for the mask itself (`custom-class`).
 * @param beforeClose awaited before the mask is hidden when [visible] turns
 * false. Element Plus uses this service hook for async teardown; a controlled
 * parent owns visibility, so the hook can delay but not veto the close.
 * @param onClosed fires once the mask is hidden (`closed` in the service API).
 */
@Composable
fun ElLoading(
    visible: Boolean,
    modifier: Modifier = Modifier,
    text: String? = null,
    background: Color = Color.Transparent,
    fullscreen: Boolean = false,
    spinnerSize: Dp = 30.dp,
    spinner: (@Composable () -> Unit)? = null,
    textContent: (@Composable () -> Unit)? = null,
    lock: Boolean = false,
    overlayModifier: Modifier = Modifier,
    spinnerModifier: Modifier = Modifier,
    beforeClose: (suspend () -> Unit)? = null,
    onClosed: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val currentOnClosed by rememberUpdatedState(onClosed)
    var shown by remember { mutableStateOf(visible) }
    LaunchedEffect(visible) {
        if (visible) {
            shown = true
        } else if (shown) {
            beforeClose?.invoke()
            shown = false
            currentOnClosed?.invoke()
        }
    }
    val overlay: @Composable () -> Unit = {
        if (shown) {
            Box(
                Modifier
                    .then(if (content != null) Modifier.fillMaxSize() else if (fullscreen) Modifier.fillMaxSize() else Modifier)
                    .then(overlayModifier)
                    .background(background)
                    .then(if (lock) Modifier.pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                event.changes.forEach { it.consume() }
                            }
                        }
                    } else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (spinner != null) {
                        spinner()
                    } else {
                        ElIcon(
                            ElIcons.Loading,
                            size = spinnerSize,
                            tint = colors.primary,
                            spin = true,
                            modifier = spinnerModifier,
                        )
                    }
                    if (textContent != null) {
                        Spacer(Modifier.height(8.dp))
                        textContent()
                    } else if (text != null) {
                        Spacer(Modifier.height(8.dp))
                        BasicText(
                            text = text,
                            modifier = Modifier
                                .background(colors.background.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                    }
                }
            }
        }
    }
    if (content != null) {
        Box(modifier) {
            content()
            overlay()
        }
    } else {
        Box(modifier.fillMaxSize()) { overlay() }
    }
}
