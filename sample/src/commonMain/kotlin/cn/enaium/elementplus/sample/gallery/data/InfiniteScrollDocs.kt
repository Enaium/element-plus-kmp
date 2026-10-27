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
fun InfiniteScrollDocs() {
    DemoExample("Basic usage", "More rows load when the end is reached.") {
        InfiniteScrollSample()
    }
    DemoExample("Finished", "Show a finished message and stop loading.") {
        InfiniteScrollSample(finished = true)
    }
    DemoExample("Disabled", "Disable automatic loading.") {
        InfiniteScrollSample(disabled = true)
    }
}

@Composable
private fun InfiniteScrollSample(finished: Boolean = false, disabled: Boolean = false) {
    val items = remember { mutableStateListOf(*(1..12).map { "Row $it" }.toTypedArray()) }
    var loading by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(finished) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    ElInfiniteScroll(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        state = listState,
        loading = loading,
        finished = isFinished,
        disabled = disabled,
        onLoadMore = {
            if (!isFinished) {
                loading = true
                scope.launch {
                    delay(400)
                    val next = items.size
                    items.addAll((next + 1..next + 8).map { "Row $it" })
                    loading = false
                    if (items.size >= 36) isFinished = true
                }
            }
        },
    ) {
        items(items.size) { index ->
            BasicText(
                items[index],
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                style = LocalElTheme.current.typography.small,
            )
        }
    }
}
