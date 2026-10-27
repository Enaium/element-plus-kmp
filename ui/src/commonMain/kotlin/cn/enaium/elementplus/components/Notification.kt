package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

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
    ): Int {
        val id = nextId++
        items.add(ElNotificationItem(id, title, message, type, duration, position, showClose))
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
    ): Int = service.notify(title, message, type, duration, position, showClose)
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
                Column(
                    Modifier
                        .align(position.toAlignment())
                        .padding(offset),
                    verticalArrangement = if (position.name.startsWith("Top")) Arrangement.Top else Arrangement.Bottom,
                    horizontalAlignment = if (position.name.endsWith("Right")) Alignment.End else Alignment.Start,
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
    val icon = when (item.type) {
        ElNotificationType.Info -> ElIcons.InfoFilled
        ElNotificationType.Success -> ElIcons.CircleCheckFilled
        ElNotificationType.Warning -> ElIcons.WarningFilled
        ElNotificationType.Error -> ElIcons.CircleCloseFilled
    }
    LaunchedEffect(item.id, item.duration) {
        if (item.duration > 0) {
            delay(item.duration)
            onClose()
        }
    }
    Row(
        Modifier
            .padding(vertical = 6.dp)
            .width(330.dp)
            .shadow(6.dp, RoundedCornerShape(4.dp))
            .background(colors.background, RoundedCornerShape(4.dp))
            .border(1.dp, colors.borderLighter, RoundedCornerShape(4.dp))
            .padding(14.dp),
    ) {
        ElIcon(icon, size = 18.dp, tint = colors.colorOf(type))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            BasicText(
                text = item.title,
                style = theme.typography.base.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
            )
            if (item.message != null) {
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
}
