package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElNotificationPosition { TopRight, TopLeft, BottomRight, BottomLeft }
enum class ElNotificationType { Info, Success, Warning, Error }

data class ElNotificationItem(
    val id: Int,
    val title: String,
    val message: String? = null,
    val type: ElNotificationType = ElNotificationType.Info,
    val duration: Long = 4500,
    val position: ElNotificationPosition = ElNotificationPosition.TopRight,
    val showClose: Boolean = true,
    val offset: Dp? = null,
    val icon: ImageVector? = null,
    val messageContent: (@Composable () -> Unit)? = null,
    val progress: Boolean = false,
    val progressBrush: Brush? = null,
    val pauseOnHover: Boolean = true,
    val onClick: (() -> Unit)? = null,
)

@Stable
class ElNotificationService {
    internal val items = mutableStateListOf<ElNotificationItem>()
    private var nextId = 0
    fun notify(
        title: String,
        message: String? = null,
        type: ElNotificationType = ElNotificationType.Info,
        duration: Long = 4500,
        position: ElNotificationPosition = ElNotificationPosition.TopRight,
        showClose: Boolean = true,
        offset: Dp? = null,
        icon: ImageVector? = null,
        messageContent: (@Composable () -> Unit)? = null,
        progress: Boolean = false,
        progressBrush: Brush? = null,
        pauseOnHover: Boolean = true,
        onClick: (() -> Unit)? = null,
    ): Int {
        val id = nextId++
        items.add(
            ElNotificationItem(
                id, title, message, type, duration, position, showClose,
                offset, icon, messageContent, progress, progressBrush, pauseOnHover, onClick,
            )
        )
        return id
    }
    fun close(id: Int) { items.removeAll { it.id == id } }
    fun closeAll() { items.clear() }
}

@Composable
fun rememberElNotificationService(): ElNotificationService = remember { ElNotificationService() }

object ElNotification {
    val service = ElNotificationService()
    fun notify(
        title: String,
        message: String? = null,
        type: ElNotificationType = ElNotificationType.Info,
        duration: Long = 4500,
        position: ElNotificationPosition = ElNotificationPosition.TopRight,
        showClose: Boolean = true,
        offset: Dp? = null,
        icon: ImageVector? = null,
        messageContent: (@Composable () -> Unit)? = null,
        progress: Boolean = false,
        progressBrush: Brush? = null,
        pauseOnHover: Boolean = true,
        onClick: (() -> Unit)? = null,
    ): Int = service.notify(
        title, message, type, duration, position, showClose, offset, icon, messageContent,
        progress, progressBrush, pauseOnHover, onClick,
    )
    fun close(id: Int) = service.close(id)
    fun closeAll() = service.closeAll()

    @Composable
    fun Host(content: @Composable () -> Unit) = ElNotificationHost(service, content = content)
}

@Composable
fun ElNotificationHost(
    service: ElNotificationService,
    modifier: Modifier = Modifier,
    offset: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        content()
        ElNotificationPosition.entries.forEach { position ->
            val positionItems = service.items.filter { it.position == position }
            if (positionItems.isNotEmpty()) {
                val isTop = position == ElNotificationPosition.TopRight ||
                    position == ElNotificationPosition.TopLeft
                val itemOffset = positionItems.firstNotNullOfOrNull { it.offset } ?: offset
                Column(
                    Modifier
                        .align(position.toAlignment())
                        .padding(
                            start = offset,
                            end = offset,
                            top = if (isTop) itemOffset else 0.dp,
                            bottom = if (isTop) 0.dp else itemOffset,
                        ),
                    verticalArrangement = if (isTop) Arrangement.Top else Arrangement.Bottom,
                    horizontalAlignment = if (position == ElNotificationPosition.TopRight ||
                        position == ElNotificationPosition.BottomRight
                    ) Alignment.End else Alignment.Start,
                ) {
                    positionItems.forEach { item ->
                        NotificationItemView(item, onClose = { service.close(item.id) })
                    }
                }
            }
        }
    }
}

private fun ElNotificationPosition.toAlignment(): Alignment = when (this) {
    ElNotificationPosition.TopRight -> Alignment.TopEnd
    ElNotificationPosition.TopLeft -> Alignment.TopStart
    ElNotificationPosition.BottomRight -> Alignment.BottomEnd
    ElNotificationPosition.BottomLeft -> Alignment.BottomStart
}

@Composable
private fun NotificationItemView(item: ElNotificationItem, onClose: () -> Unit) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val type = when (item.type) {
        ElNotificationType.Info -> ElType.Info
        ElNotificationType.Success -> ElType.Success
        ElNotificationType.Warning -> ElType.Warning
        ElNotificationType.Error -> ElType.Danger
    }
    val icon = item.icon ?: when (item.type) {
        ElNotificationType.Info -> ElIcons.InfoFilled
        ElNotificationType.Success -> ElIcons.CircleCheckFilled
        ElNotificationType.Warning -> ElIcons.WarningFilled
        ElNotificationType.Error -> ElIcons.CircleCloseFilled
    }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val latestHovered = rememberUpdatedState(hovered)
    var progressFraction by remember(item.id) { mutableStateOf(1f) }
    LaunchedEffect(item.id, item.duration, item.pauseOnHover) {
        if (item.duration <= 0) return@LaunchedEffect
        val totalNanos = item.duration * 1_000_000L
        var elapsed = 0L
        var last = 0L
        while (elapsed < totalNanos) {
            withFrameNanos { now ->
                if (!(item.pauseOnHover && latestHovered.value)) {
                    if (last != 0L) elapsed += now - last
                }
                last = now
            }
            if (item.progress) {
                progressFraction = (1f - elapsed.toFloat() / totalNanos).coerceIn(0f, 1f)
            }
        }
        if (item.progress) progressFraction = 0f
        onClose()
    }
    val shape = RoundedCornerShape(4.dp)
    val clickableModifier = if (item.onClick != null) {
        Modifier.clickable(onClick = item.onClick)
    } else {
        Modifier
    }
    Column(
        Modifier
            .padding(vertical = 6.dp)
            .width(330.dp)
            .shadow(6.dp, shape)
            .background(colors.background, shape)
            .border(1.dp, colors.borderLighter, shape)
            .hoverable(interaction)
            .then(clickableModifier),
    ) {
        Row(Modifier.padding(14.dp)) {
            ElIcon(icon, size = 18.dp, tint = colors.colorOf(type))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                BasicText(
                    text = item.title,
                    style = theme.typography.base.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                )
                val content = item.messageContent
                if (content != null) {
                    Box(Modifier.padding(top = 4.dp)) { content() }
                } else if (item.message != null) {
                    BasicText(
                        text = item.message,
                        modifier = Modifier.padding(top = 4.dp),
                        style = theme.typography.small.copy(color = colors.textSecondary),
                    )
                }
            }
            if (item.showClose) {
                ElIcon(
                    ElIcons.Close,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = LocalElConfig.current.locale.close,
                    modifier = Modifier.clickable(onClick = onClose),
                )
            }
        }
        if (item.progress) {
            Box(
                Modifier
                    .fillMaxWidth(progressFraction)
                    .height(3.dp)
                    .background(
                        item.progressBrush ?: Brush.horizontalGradient(
                            listOf(colors.colorOf(type), colors.colorOf(type))
                        )
                    )
            )
        }
    }
}
