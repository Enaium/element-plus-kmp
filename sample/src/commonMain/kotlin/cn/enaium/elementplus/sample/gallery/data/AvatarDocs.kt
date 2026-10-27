package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun AvatarDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Basic usage", "Use an icon, initials or an image as the avatar.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ElAvatar(icon = ElIcons.User)
            ElAvatar(text = "EP", background = colors.light(ElType.Primary, 8), color = colors.primary)
            ElAvatar(painter = ColorPainter(colors.primary), contentDescription = "Avatar")
        }
    }
    DemoExample("Shapes and sizes", "Avatars can be circular or square.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ElAvatar(text = "SM", size = 28.dp)
            ElAvatar(text = "MD", size = 40.dp)
            ElAvatar(text = "LG", size = 56.dp)
            ElAvatar(text = "SQ", size = 40.dp, shape = ElAvatarShape.Square, background = colors.light(ElType.Success, 8))
        }
    }
    DemoExample("Avatar group", "Group avatars and collapse the overflow.") {
        ElAvatarGroup(count = 5, max = 3) { index ->
            ElAvatar(
                text = "U${index + 1}",
                background = colors.light(listOf(ElType.Primary, ElType.Success, ElType.Warning)[index % 3], 9),
                color = colors.colorOf(listOf(ElType.Primary, ElType.Success, ElType.Warning)[index % 3]),
            )
        }
    }
    DemoExample("Fallback", "Use the content slot for a completely custom fallback.") {
        ElAvatar(background = colors.fillLight) {
            BasicText("JD", style = LocalElTheme.current.typography.small.copy(color = colors.textSecondary))
        }
    }

    DemoExample("Fit", "Control how an image avatar fills its container.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ElAvatar(
                painter = ColorPainter(LocalElTheme.current.colors.primary),
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop,
            )
            ElAvatar(
                painter = ColorPainter(LocalElTheme.current.colors.success),
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }

    DemoExample("Types", "Avatars can render an icon, text or image.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ElAvatar(icon = ElIcons.User)
            ElAvatar(text = "K")
            ElAvatar(painter = ColorPainter(LocalElTheme.current.colors.primary))
        }
    }
}
