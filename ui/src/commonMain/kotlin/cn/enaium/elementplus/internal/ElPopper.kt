package cn.enaium.elementplus.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.roundToInt

enum class ElPlacement {
    Top, TopStart, TopEnd,
    Bottom, BottomStart, BottomEnd,
    Left, LeftStart, LeftEnd,
    Right, RightStart, RightEnd,
    ;

    val isTop: Boolean get() = this == Top || this == TopStart || this == TopEnd
    val isBottom: Boolean get() = this == Bottom || this == BottomStart || this == BottomEnd
    val isLeft: Boolean get() = this == Left || this == LeftStart || this == LeftEnd
    val isRight: Boolean get() = this == Right || this == RightStart || this == RightEnd

    fun flipVertical(): ElPlacement = when (this) {
        Top -> Bottom; TopStart -> BottomStart; TopEnd -> BottomEnd
        Bottom -> Top; BottomStart -> TopStart; BottomEnd -> TopEnd
        else -> this
    }

    fun flipHorizontal(): ElPlacement = when (this) {
        Left -> Right; LeftStart -> RightStart; LeftEnd -> RightEnd
        Right -> Left; RightStart -> LeftStart; RightEnd -> LeftEnd
        else -> this
    }
}

internal class ElPopupPositionProvider(
    private val placement: ElPlacement,
    private val offset: DpOffset,
    private val density: androidx.compose.ui.unit.Density,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val gapX = with(density) { offset.x.roundToPx() }
        val gapY = with(density) { offset.y.roundToPx() }
        var place = placement
        val fits = when {
            place.isBottom -> anchorBounds.bottom + gapY + popupContentSize.height <= windowSize.height
            place.isTop -> anchorBounds.top - gapY - popupContentSize.height >= 0
            place.isRight -> anchorBounds.right + gapX + popupContentSize.width <= windowSize.width
            else -> anchorBounds.left - gapX - popupContentSize.width >= 0
        }
        if (!fits) {
            place = if (place.isTop || place.isBottom) place.flipVertical() else place.flipHorizontal()
        }

        val sideStart = when (place) {
            ElPlacement.LeftStart, ElPlacement.RightStart -> anchorBounds.top
            ElPlacement.Left, ElPlacement.Right -> anchorBounds.top + (anchorBounds.height - popupContentSize.height) / 2
            else -> anchorBounds.top
        }
        val sideEnd = anchorBounds.bottom - popupContentSize.height

        val x = when {
            place.isLeft -> if (place == ElPlacement.LeftEnd) anchorBounds.left - gapX - popupContentSize.width
                else anchorBounds.left - gapX - popupContentSize.width
            place.isRight -> anchorBounds.right + gapX
            place == ElPlacement.BottomEnd || place == ElPlacement.TopEnd -> anchorBounds.right - popupContentSize.width
            place == ElPlacement.Bottom || place == ElPlacement.Top -> anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
            else -> anchorBounds.left
        }
        val y = when {
            place.isBottom -> anchorBounds.bottom + gapY
            place.isTop -> anchorBounds.top - gapY - popupContentSize.height
            place == ElPlacement.LeftEnd || place == ElPlacement.RightEnd -> sideEnd
            else -> sideStart
        }
        return IntOffset(x, y)
    }
}

/**
 * Low-level anchored overlay. Place it in the same layout as the anchor; the
 * popup is positioned against that layout's bounds and flips when it would
 * overflow the window.
 */
@Composable
fun ElPopper(
    visible: Boolean,
    onDismissRequest: () -> Unit = {},
    placement: ElPlacement = ElPlacement.BottomStart,
    offset: DpOffset = DpOffset(0.dp, 6.dp),
    focusable: Boolean = false,
    dismissOnClickOutside: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (!visible) return
    val density = LocalDensity.current
    val provider = remember(placement, offset, density) { ElPopupPositionProvider(placement, offset, density) }
    Popup(
        popupPositionProvider = provider,
        onDismissRequest = { if (dismissOnClickOutside) onDismissRequest() },
        properties = PopupProperties(
            focusable = focusable,
            dismissOnBackPress = true,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = false,
        ),
    ) {
        content()
    }
}

@Composable
fun ElPopperSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    background: Color = LocalElTheme.current.colors.backgroundOverlay,
    borderColor: Color = LocalElTheme.current.colors.borderLight,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(8.dp, shape, clip = false)
            .background(background, shape)
            .border(1.dp, borderColor, shape)
            .padding(contentPadding),
    ) {
        content()
    }
}
