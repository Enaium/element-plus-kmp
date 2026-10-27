package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun BreadcrumbDocs() {
    DemoExample("Basic usage", "Breadcrumbs show the path to the current page.") {
        ElBreadcrumb(
            items = listOf(
                ElBreadcrumbItemData("Home", onClick = {}),
                ElBreadcrumbItemData("Components", onClick = {}),
                ElBreadcrumbItemData("Breadcrumb"),
            ),
        )
    }
    DemoExample("Icon separator", "Use an icon as the separator.") {
        ElBreadcrumb(
            items = listOf(
                ElBreadcrumbItemData("Home", icon = ElIcons.HomeFilled, onClick = {}),
                ElBreadcrumbItemData("Components", onClick = {}),
                ElBreadcrumbItemData("Breadcrumb"),
            ),
            separatorIcon = ElIcons.ArrowRight,
        )
    }
    DemoExample("With icon", "Items can include leading icons.") {
        ElBreadcrumb(
            items = listOf(
                ElBreadcrumbItemData("Home", icon = ElIcons.HomeFilled),
                ElBreadcrumbItemData("Settings", icon = ElIcons.Setting),
                ElBreadcrumbItemData("Profile"),
            ),
        )
    }
}
