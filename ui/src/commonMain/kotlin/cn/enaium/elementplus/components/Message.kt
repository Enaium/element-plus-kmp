package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.ElMessageConfig
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElMessageType { Info, Success, Warning, Error, Primary }

/** The six viewport positions supported by `<el-message>`'s `placement` prop. */
enum class ElMessagePlacement { Top, TopLeft, TopRight, Bottom, BottomLeft, BottomRight }

data class ElMessageItem(
    val id: Int,
    val message: String,
    val type: ElMessageType = ElMessageType.Info,
    val duration: Long = 3000,
    val showClose: Boolean = false,
    val center: Boolean = false,
    val placement: ElMessagePlacement = ElMessagePlacement.Top,
    val offset: Dp? = null,
    val plain: Boolean = false,
    val icon: ImageVector? = null,
    val grouping: Boolean = false,
    val repeatNum: Int = 1,
    val onClick: (() -> Unit)? = null,
    val messageContent: (@Composable () -> Unit)? = null,
)

/**
 * State holder for `<el-message>`. Render [ElMessageHost] once around the app
 * and call `show` imperatively from any event handler.
 */
@Stable
class ElMessageService {
    internal val items = mutableStateListOf<ElMessageItem>()
    private var nextId = 0
    internal var config: ElMessageConfig = ElMessageConfig()

    fun show(
        message: String,
        type: ElMessageType = ElMessageType.Info,
        duration: Long = 3000,
        showClose: Boolean = false,
        center: Boolean = false,
        placement: ElMessagePlacement = ElMessagePlacement.Top,
        offset: Dp? = null,
        plain: Boolean = false,
        icon: ImageVector? = null,
        grouping: Boolean = false,
        repeatNum: Int = 1,
        onClick: (() -> Unit)? = null,
        messageContent: (@Composable () -> Unit)? = null,
    ): Int {
        val effectivePlacement = config.placement ?: placement
        val effectivePlain = config.plain ?: plain
        val effectiveGrouping = config.grouping ?: grouping
        val max = config.max
        if (max != null && max > 0 && items.size >= max) {
            while (items.size >= max) items.removeAt(0)
        }
        if (effectiveGrouping) {
            val existing = items.firstOrNull { it.placement == effectivePlacement && it.message == message }
            if (existing != null) {
                val index = items.indexOf(existing)
                items[index] = existing.copy(
                    type = type,
                    repeatNum = existing.repeatNum + 1,
                    plain = effectivePlain,
                    icon = icon ?: existing.icon,
                    onClick = onClick ?: existing.onClick,
                )
                return existing.id
            }
        }
        val id = nextId++
        items.add(
            ElMessageItem(
                id = id,
                message = message,
                type = type,
                duration = duration,
                showClose = showClose,
                center = center,
                placement = effectivePlacement,
                offset = offset,
                plain = effectivePlain,
                icon = icon,
                grouping = effectiveGrouping,
                repeatNum = if (repeatNum < 1) 1 else repeatNum,
                onClick = onClick,
                messageContent = messageContent,
            )
        )
        return id
    }

    fun close(id: Int) {
        items.removeAll { it.id == id }
    }

    fun closeAll() {
        items.clear()
    }

    /** Close only the messages shown at [placement]. */
    fun closeAll(placement: ElMessagePlacement) {
        items.removeAll { it.placement == placement }
    }
}

@Composable
fun rememberElMessageService(): ElMessageService = remember { ElMessageService() }

/** Global convenience instance used by the `ElMessage.show { }` style API. */
object ElMessage {
    val service = ElMessageService()
    fun show(
        message: String,
        type: ElMessageType = ElMessageType.Info,
        duration: Long = 3000,
        showClose: Boolean = false,
        center: Boolean = false,
        placement: ElMessagePlacement = ElMessagePlacement.Top,
        offset: Dp? = null,
        plain: Boolean = false,
        icon: ImageVector? = null,
        grouping: Boolean = false,
        repeatNum: Int = 1,
        onClick: (() -> Unit)? = null,
        messageContent: (@Composable () -> Unit)? = null,
    ): Int = service.show(
        message, type, duration, showClose, center, placement, offset, plain, icon, grouping, repeatNum,
        onClick, messageContent,
    )
    fun close(id: Int) = service.close(id)
    fun closeAll() = service.closeAll()
    fun closeAll(placement: ElMessagePlacement) = service.closeAll(placement)

    @Composable
    fun Host(content: @Composable () -> Unit) = ElMessageHost(service, content = content)
}

@Composable
fun ElMessageHost(
    service: ElMessageService,
    modifier: Modifier = Modifier,
    top: Dp = 20.dp,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        content()
        ElMessagePlacement.entries.forEach { placement ->
            val positionItems = service.items.filter { it.placement == placement }
            if (positionItems.isNotEmpty()) {
                val offset = positionItems.firstNotNullOfOrNull { it.offset }
                val isTop = placement == ElMessagePlacement.Top ||
                    placement == ElMessagePlacement.TopLeft ||
                    placement == ElMessagePlacement.TopRight
                Column(
                    Modifier
                        .align(placement.toAlignment())
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = if (isTop) offset ?: top else 0.dp,
                            bottom = if (isTop) 0.dp else offset ?: 16.dp,
                        ),
                    horizontalAlignment = when (placement) {
                        ElMessagePlacement.TopLeft, ElMessagePlacement.BottomLeft -> Alignment.Start
                        ElMessagePlacement.TopRight, ElMessagePlacement.BottomRight -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    },
                    verticalArrangement = if (isTop) {
                        androidx.compose.foundation.layout.Arrangement.Top
                    } else {
                        androidx.compose.foundation.layout.Arrangement.Bottom
                    },
                ) {
                    positionItems.forEach { item ->
                        MessageItemView(item, onClose = { service.close(item.id) })
                    }
                }
            }
        }
    }
}

private fun ElMessagePlacement.toAlignment(): Alignment = when (this) {
    ElMessagePlacement.Top -> Alignment.TopCenter
    ElMessagePlacement.TopLeft -> Alignment.TopStart
    ElMessagePlacement.TopRight -> Alignment.TopEnd
    ElMessagePlacement.Bottom -> Alignment.BottomCenter
    ElMessagePlacement.BottomLeft -> Alignment.BottomStart
    ElMessagePlacement.BottomRight -> Alignment.BottomEnd
}

@Composable
private fun MessageItemView(item: ElMessageItem, onClose: () -> Unit) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val (type, defaultIcon) = messageIcon(item.type)
    val icon = item.icon ?: defaultIcon
    val color = colors.colorOf(type)
    LaunchedEffect(item.id, item.duration, item.repeatNum) {
        if (item.duration > 0) {
            delay(item.duration)
            onClose()
        }
    }
    val shape = RoundedCornerShape(4.dp)
    val clickableModifier = if (item.onClick != null) {
        Modifier.clickable(onClick = item.onClick)
    } else {
        Modifier
    }
    Box(Modifier.padding(vertical = 5.dp)) {
        Row(
            Modifier
                .widthIn(min = 200.dp, max = 480.dp)
                .then(clickableModifier)
                .then(
                    if (item.plain) {
                        Modifier
                            .shadow(6.dp, shape)
                            .background(colors.backgroundOverlay, shape)
                    } else {
                        Modifier
                            .background(colors.light(type, 9), shape)
                            .border(1.dp, colors.light(type, 8), shape)
                    }
                )
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ElIcon(icon, size = 14.dp, tint = color)
            Spacer(Modifier.padding(start = 8.dp))
            val body = item.messageContent
            if (body != null) {
                Box(Modifier.weight(1f, fill = false)) { body() }
            } else {
                BasicText(
                    text = item.message,
                    modifier = Modifier.weight(1f, fill = false),
                    style = theme.typography.small.copy(
                        color = if (color == colors.info) colors.textRegular else color,
                        textAlign = if (item.center) androidx.compose.ui.text.style.TextAlign.Center
                        else androidx.compose.ui.text.style.TextAlign.Start,
                    ),
                )
            }
            if (item.showClose) {
                Spacer(Modifier.padding(start = 8.dp))
                ElIcon(
                    ElIcons.Close,
                    size = 12.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = LocalElConfig.current.locale.close,
                    modifier = Modifier.clickable(onClick = onClose),
                )
            }
        }
        if (item.repeatNum > 1) {
            val badgeColor = colors.colorOf(item.type.toBadgeType())
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 1.dp)
                    .background(badgeColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp),
            ) {
                BasicText(
                    text = if (item.repeatNum > 99) "99+" else item.repeatNum.toString(),
                    style = theme.typography.extraSmall.copy(color = colors.white),
                )
            }
        }
    }
}

private fun ElMessageType.toBadgeType(): ElType = when (this) {
    ElMessageType.Info -> ElType.Info
    ElMessageType.Success -> ElType.Success
    ElMessageType.Warning -> ElType.Warning
    ElMessageType.Error -> ElType.Danger
    ElMessageType.Primary -> ElType.Primary
}

internal fun messageIcon(type: ElMessageType): Pair<ElType, ImageVector> = when (type) {
    ElMessageType.Info -> ElType.Info to ElIcons.InfoFilled
    ElMessageType.Success -> ElType.Success to ElIcons.CircleCheckFilled
    ElMessageType.Warning -> ElType.Warning to ElIcons.WarningFilled
    ElMessageType.Error -> ElType.Danger to ElIcons.CircleCloseFilled
    ElMessageType.Primary -> ElType.Primary to ElIcons.InfoFilled
}
