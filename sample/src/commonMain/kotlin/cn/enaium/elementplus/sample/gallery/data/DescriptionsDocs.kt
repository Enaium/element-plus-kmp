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
fun DescriptionsDocs() {
    val items = listOf(
        ElDescriptionItem("Name") { BasicText("Alice") },
        ElDescriptionItem("Age") { BasicText("28") },
        ElDescriptionItem("City") { BasicText("Chengdu") },
        ElDescriptionItem("Bio", span = 3) { BasicText("Kotlin and Compose developer.") },
    )
    DemoExample("Basic usage", "Descriptions display label/content pairs.") {
        ElDescriptions(title = "User profile", items = items)
    }
    DemoExample("With border", "Borders make the table-like structure clear.") {
        ElDescriptions(title = "User profile", column = 3, border = true, items = items)
    }
    DemoExample("Vertical list", "Place the label above the content.") {
        ElDescriptions(
            title = "Details",
            column = 2,
            direction = ElDescriptionsDirection.Vertical,
            border = true,
            items = listOf(
                ElDescriptionItem("Name") { BasicText("Alice") },
                ElDescriptionItem("Email") { BasicText("alice@example.com") },
                ElDescriptionItem("Address", span = 2) { BasicText("Chengdu, Sichuan, China") },
            ),
        )
    }
    DemoExample("Row span", "Items can span multiple columns.") {
        ElDescriptions(
            column = 3,
            border = true,
            items = listOf(
                ElDescriptionItem("Project", span = 2) { BasicText("Element Plus KMP") },
                ElDescriptionItem("Status") { ElTag("Active", type = ElType.Success) },
                ElDescriptionItem("Description", span = 3) {
                    BasicText("A Kotlin Multiplatform port of Element Plus components.")
                },
            ),
        )
    }

    DemoExample("Sizes", "Descriptions support large, default and small sizes.") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ElDescriptions(
                items = listOf(
                    ElDescriptionItem("Large", span = 3) { BasicText("Large size") },
                ),
                column = 3,
                size = ElSize.Large,
                border = true,
            )
            ElDescriptions(
                items = listOf(ElDescriptionItem("Small", span = 3) { BasicText("Small size") }),
                column = 3,
                size = ElSize.Small,
                border = true,
            )
        }
    }
    DemoExample("Customized style", "Customize labels, borders and extra content.") {
        ElDescriptions(
            items = listOf(
                ElDescriptionItem("Product", span = 2) { BasicText("Cloud database") },
                ElDescriptionItem("Billing") { BasicText("Prepaid") },
                ElDescriptionItem("Time") { BasicText("2026-10-26") },
                ElDescriptionItem("Amount") { BasicText("$120.00") },
            ),
            column = 3,
            border = true,
            title = "Order",
            extra = { ElTag("Paid", type = ElType.Success) },
            labelWidth = 100.dp,
        )
    }
}
