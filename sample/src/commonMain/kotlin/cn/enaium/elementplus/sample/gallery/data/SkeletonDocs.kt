package cn.enaium.elementplus.sample.gallery.data

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
fun SkeletonDocs() {
    var loading by remember { mutableStateOf(true) }
    DemoExample("Basic usage", "Use skeleton placeholders while content is loading.") {
        ElSkeleton(loading = true, rows = 3)
    }
    DemoExample("Avatar and title", "Configure the default skeleton structure.") {
        ElSkeleton(loading = true, avatar = true, title = true, rows = 2)
    }
    DemoExample("Loading state", "Toggle between the skeleton and real content.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSwitch(checked = loading, onCheckedChange = { loading = it }, activeText = "Loading", inactiveText = "Content", inlinePrompt = true)
            ElSkeleton(loading = loading, avatar = true, rows = 3) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicText("Loaded content", style = LocalElTheme.current.typography.large.copy(fontWeight = FontWeight.Medium))
                    BasicText(
                        "The skeleton is replaced by this content when loading is false.",
                        style = LocalElTheme.current.typography.base.copy(color = LocalElTheme.current.colors.textSecondary),
                    )
                }
            }
        }
    }
    DemoExample("Customized template", "Compose your own skeleton with ElSkeletonItem.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSkeletonItem(ElSkeletonVariant.Circle, Modifier.size(48.dp))
            ElSkeletonItem(ElSkeletonVariant.H1)
            ElSkeletonItem(ElSkeletonVariant.Text)
            ElSkeletonItem(ElSkeletonVariant.Text)
            ElSkeletonItem(ElSkeletonVariant.Image, Modifier.height(100.dp))
        }
    }

    DemoExample("Animation and rows", "Disable animation or change the number of rows.") {
        ElSkeleton(loading = true, animated = false, rows = 5)
    }

    DemoExample("Configurable rows", "Control the number of skeleton rows.") {
        ElSkeleton(loading = true, rows = 5)
    }
    DemoExample("No animation", "Disable the shimmer animation.") {
        ElSkeleton(loading = true, animated = false, rows = 3)
    }

    DemoExample("Rendering a list of data", "Show skeleton rows while a list loads.") {
        var loading by remember { mutableStateOf(true) }
        ElButton(onClick = { loading = !loading }, text = if (loading) "Show data" else "Show skeleton")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { index ->
                ElSkeleton(loading = loading, rows = 2, avatar = true) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        ElAvatar(text = "U${index + 1}")
                        Column {
                            BasicText("User ${index + 1}", style = LocalElTheme.current.typography.medium)
                            BasicText("Loaded row ${index + 1}", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary))
                        }
                    }
                }
            }
        }
    }

    DemoExample("Avoiding rendering bouncing", "A 500ms leading throttle delays the skeleton so quick loads never flash it.") {
        var loading by remember { mutableStateOf(false) }
        DemoRow {
            BasicText("Switch Loading", style = LocalElTheme.current.typography.small)
            ElSwitch(checked = loading, onCheckedChange = { loading = it })
        }
        ElSkeleton(
            loading = loading,
            animated = true,
            rows = 2,
            modifier = Modifier.width(260.dp),
            throttle = ElSkeletonThrottle(leading = 500),
        ) {
            SkeletonLoadedCard()
        }
    }

    DemoExample("Initial rendering loading", "Combine a leading throttle with initVal so the first frame already shows the skeleton.") {
        var loading by remember { mutableStateOf(true) }
        DemoRow {
            BasicText("Switch Loading", style = LocalElTheme.current.typography.small)
            ElSwitch(checked = loading, onCheckedChange = { loading = it })
        }
        ElSkeleton(
            loading = loading,
            animated = true,
            rows = 2,
            modifier = Modifier.width(260.dp),
            throttle = ElSkeletonThrottle(leading = 500, initVal = true),
        ) {
            SkeletonLoadedCard()
        }
    }

    DemoExample("Leading and trailing without bouncing", "Delay both directions so rapidly toggled states do not flicker.") {
        var loading by remember { mutableStateOf(false) }
        DemoRow {
            BasicText("Switch Loading", style = LocalElTheme.current.typography.small)
            ElSwitch(checked = loading, onCheckedChange = { loading = it })
        }
        ElSkeleton(
            loading = loading,
            animated = true,
            rows = 2,
            modifier = Modifier.width(260.dp),
            throttle = ElSkeletonThrottle(leading = 500, trailing = 500, initVal = true),
        ) {
            SkeletonLoadedCard()
        }
    }

    DemoExample("Customized template slot", "Use the template slot to compose a fully custom skeleton.") {
        ElSkeleton(
            loading = true,
            animated = true,
            modifier = Modifier.width(240.dp),
            template = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElSkeletonItem(ElSkeletonVariant.Image, Modifier.fillMaxWidth().height(160.dp))
                    ElSkeletonItem(ElSkeletonVariant.P, Modifier.fillMaxWidth(0.5f))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ElSkeletonItem(ElSkeletonVariant.Text, Modifier.weight(1f))
                        ElSkeletonItem(ElSkeletonVariant.Text, Modifier.weight(0.3f))
                    }
                }
            },
        )
    }

    DemoExample("Count", "Repeat the placeholder block a number of times.") {
        ElSkeleton(loading = true, count = 3, rows = 2, avatar = true)
    }
}

@Composable
private fun SkeletonLoadedCard() {
    val theme = LocalElTheme.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(theme.colors.background, RoundedCornerShape(6.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        BasicText("Delicious hamburger", style = theme.typography.medium)
        BasicText(
            "The real content is rendered once the throttled loading state clears.",
            style = theme.typography.small.copy(color = theme.colors.textSecondary),
        )
    }
}
