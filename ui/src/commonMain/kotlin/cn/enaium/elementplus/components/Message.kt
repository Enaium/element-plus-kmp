package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElMessageType { Info, Success, Warning, Error }

data class ElMessageItem(
    val id: Int,
    val message: String,
    val type: ElMessageType = ElMessageType.Info,
    val duration: Long = 3000,
    val showClose: Boolean = false,
    val center: Boolean = false,
)

/**
 * State holder for `<el-message>`. Render [ElMessageHost] once around the app
 * and call `show` imperatively from any event handler.
 */
@Stable
class ElMessageService {
    internal val items = mutableStateListOf<ElMessageItem>()
    private var nextId = 0

    fun show(
        message: String,
        type: ElMessageType = ElMessageType.Info,
        duration: Long = 3000,
        showClose: Boolean = false,
        center: Boolean = false,
    ): Int {
        val id = nextId++
        items.add(ElMessageItem(id, message, type, duration, showClose, center))
        return id
    }

    fun close(id: Int) {
        items.removeAll { it.id == id }
    }

    fun closeAll() {
        items.clear()
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
    ): Int = service.show(message, type, duration, showClose, center)
    fun close(id: Int) = service.close(id)
    fun closeAll() = service.closeAll()

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
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            service.items.forEach { item ->
                MessageItemView(item, onClose = { service.close(item.id) })
            }
        }
    }
}

@Composable
private fun MessageItemView(item: ElMessageItem, onClose: () -> Unit) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val (type, icon) = messageIcon(item.type)
    val color = colors.colorOf(type)
    LaunchedEffect(item.id, item.duration) {
        if (item.duration > 0) {
            delay(item.duration)
            onClose()
        }
    }
    Row(
        Modifier
            .widthIn(min = 200.dp, max = 480.dp)
            .padding(vertical = 5.dp)
            .background(colors.light(type, 9), RoundedCornerShape(4.dp))
            .border(1.dp, colors.light(type, 8), RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ElIcon(icon, size = 14.dp, tint = color)
        Spacer(Modifier.padding(start = 8.dp))
        BasicText(
            text = item.message,
            modifier = Modifier.weight(1f, fill = false),
            style = theme.typography.small.copy(
                color = if (color == colors.info) colors.textRegular else color,
                textAlign = if (item.center) androidx.compose.ui.text.style.TextAlign.Center
                else androidx.compose.ui.text.style.TextAlign.Start,
            ),
        )
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
}

internal fun messageIcon(type: ElMessageType): Pair<ElType, ImageVector> = when (type) {
    ElMessageType.Info -> ElType.Info to ElIcons.InfoFilled
    ElMessageType.Success -> ElType.Success to ElIcons.CircleCheckFilled
    ElMessageType.Warning -> ElType.Warning to ElIcons.WarningFilled
    ElMessageType.Error -> ElType.Danger to ElIcons.CircleCloseFilled
}
