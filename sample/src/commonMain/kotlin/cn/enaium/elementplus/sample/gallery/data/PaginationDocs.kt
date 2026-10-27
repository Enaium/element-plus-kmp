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
fun PaginationDocs() {
    DemoExample("Basic usage", "Navigate pages with the pager and previous/next buttons.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(total = 200, currentPage = page, onCurrentPageChange = { page = it })
    }
    DemoExample("Background", "Use filled backgrounds for page buttons.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(total = 200, currentPage = page, onCurrentPageChange = { page = it }, background = true)
    }
    DemoExample("Small", "A compact pagination is available.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(total = 100, currentPage = page, onCurrentPageChange = { page = it }, small = true)
    }
    DemoExample("More elements", "Add sizes, total and a page jumper.") {
        var page by remember { mutableStateOf(1) }
        var pageSize by remember { mutableStateOf(20) }
        ElPagination(
            total = 500,
            currentPage = page,
            onCurrentPageChange = { page = it },
            pageSize = pageSize,
            onPageSizeChange = { pageSize = it },
            layout = "total, sizes, prev, pager, next, jumper",
        )
    }
    DemoExample("Auto hide pagination", "Toggle hide-on-single-page so the pager disappears when only one page remains.") {
        var hide by remember { mutableStateOf(true) }
        DemoRow {
            BasicText("Hide when single page", style = LocalElTheme.current.typography.small)
            ElSwitch(checked = hide, onCheckedChange = { hide = it })
        }
        ElPagination(
            total = 5,
            currentPage = 1,
            onCurrentPageChange = {},
            pageSize = 5,
            layout = "prev, pager, next",
            hideOnSinglePage = hide,
        )
        DemoHint(if (hide) "No pagination is rendered because there is only one page." else "The pager is shown even though there is only one page.")
    }

    DemoExample("Number of pagers", "Control how many page buttons are shown.") {
        var smallPage by remember { mutableStateOf(1) }
        ElPagination(total = 100, currentPage = smallPage, onCurrentPageChange = { smallPage = it }, pagerCount = 5)
    }

    DemoExample("Page count", "Set the number of pages directly instead of deriving it from total.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(total = 0, currentPage = page, onCurrentPageChange = { page = it }, pageCount = 24)
        DemoHint("Current page: $page of 24")
    }

    DemoExample("Previous and next text", "Replace the arrow icons with text labels.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(
            total = 200,
            currentPage = page,
            onCurrentPageChange = { page = it },
            layout = "prev, pager, next",
            prevText = "Previous",
            nextText = "Next",
        )
    }

    DemoExample("Custom prev and next icons", "Provide your own icons for the navigation buttons.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(
            total = 200,
            currentPage = page,
            onCurrentPageChange = { page = it },
            layout = "prev, pager, next",
            prevIcon = ElIcons.ArrowLeftBold,
            nextIcon = ElIcons.ArrowRightBold,
        )
    }

    DemoExample("Layout spacer and custom slot", "Use `->` to push later parts to the end and `slot` to inject custom content.") {
        var page by remember { mutableStateOf(1) }
        ElPagination(
            total = 200,
            currentPage = page,
            onCurrentPageChange = { page = it },
            layout = "total, ->, prev, pager, next, slot",
            slot = {
                DemoHint("Page $page")
            },
        )
    }

    DemoExample("Disabled", "Disable every pagination control.") {
        ElPagination(
            total = 100,
            currentPage = 2,
            onCurrentPageChange = {},
            background = true,
            disabled = true,
        )
    }
}
