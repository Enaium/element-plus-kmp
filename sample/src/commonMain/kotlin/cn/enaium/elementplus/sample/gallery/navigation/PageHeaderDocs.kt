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
fun PageHeaderDocs() {
    DemoExample("Basic usage", "A page header without a back action.") {
        ElPageHeader(title = "Page title", subtitle = "A short description")
    }
    DemoExample("With back", "Add a back action and extra content.") {
        ElPageHeader(
            title = "Detail",
            backText = "Back",
            subtitle = "Page description",
            onBack = {},
            extra = { ElButton(onClick = {}, text = "Action", size = ElSize.Small, type = ElType.Primary) },
        ) {
            BasicText("Page header content area.", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary))
        }
    }
    DemoExample("With breadcrumb", "A breadcrumb can be placed above the page header.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElBreadcrumb(
                items = listOf(
                    ElBreadcrumbItemData("Home", onClick = {}),
                    ElBreadcrumbItemData("Pages"),
                    ElBreadcrumbItemData("Detail"),
                ),
            )
            ElPageHeader(title = "Detail", backText = "Back", onBack = {}, subtitle = "Overview")
        }
    }

    DemoExample("No icon", "Hide the back action and icon for a plain title.") {
        ElPageHeader(title = "Page title", subtitle = "No back icon", onBack = null)
    }
    DemoExample("Custom icon", "Choose a different back icon.") {
        ElPageHeader(title = "Detail", backText = "Back", icon = ElIcons.DArrowLeft, onBack = {})
    }
    DemoExample("Complete example", "Combine breadcrumbs, actions and page content.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElBreadcrumb(
                items = listOf(
                    ElBreadcrumbItemData("Home", onClick = {}),
                    ElBreadcrumbItemData("Settings"),
                    ElBreadcrumbItemData("Profile"),
                ),
            )
            ElPageHeader(
                title = "Profile",
                subtitle = "Manage your account",
                backText = "Back",
                onBack = {},
                extra = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ElButton(onClick = {}, text = "Cancel", size = ElSize.Small)
                        ElButton(onClick = {}, text = "Save", size = ElSize.Small, type = ElType.Primary)
                    }
                },
            ) {
                BasicText("Main content of the page.", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary))
            }
        }
    }
}
