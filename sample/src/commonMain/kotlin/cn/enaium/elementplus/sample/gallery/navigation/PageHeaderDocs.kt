package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun PageHeaderDocs() {
    DemoExample("Basic usage", "A page header with a back action and title.") {
        ElPageHeader(title = "Page title", subtitle = "A short description", onBack = {})
    }
    DemoExample("Breadcrumb slot", "Place a breadcrumb inside the header with the breadcrumb slot.") {
        ElPageHeader(
            title = "Detail",
            backText = "Back",
            onBack = {},
            breadcrumb = {
                ElBreadcrumb(
                    items = listOf(
                        ElBreadcrumbItemData("Home", onClick = {}),
                        ElBreadcrumbItemData("Pages", onClick = {}),
                        ElBreadcrumbItemData("Detail"),
                    ),
                )
            },
        )
    }
    DemoExample("No icon", "Hide the back icon while keeping the back action.") {
        ElPageHeader(title = "Page title", icon = null, onBack = {})
    }
    DemoExample("Custom icon", "Choose a different back icon.") {
        ElPageHeader(title = "Detail", backText = "Back", icon = ElIcons.DArrowLeft, onBack = {})
    }
    DemoExample("Icon slot", "Replace the back icon entirely with the icon slot.") {
        ElPageHeader(
            title = "Detail",
            backText = "Back",
            onBack = {},
            iconContent = {
                ElIcon(
                    ElIcons.Calendar,
                    size = 16.dp,
                    tint = LocalElTheme.current.colors.primary,
                )
            },
        )
    }
    DemoExample("Title slot", "Customize the title row with the title slot.") {
        ElPageHeader(
            title = "Profile",
            onBack = {},
            titleContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        "Profile",
                        style = LocalElTheme.current.typography.medium.copy(color = LocalElTheme.current.colors.textPrimary),
                    )
                    Spacer(Modifier.width(8.dp))
                    ElTag("Default", size = ElSize.Small)
                }
            },
        )
    }
    DemoExample("Additional sections", "Use the header content slot for an avatar, title, subtitle and tag.") {
        ElPageHeader(
            title = "Title",
            icon = null,
            headerContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElAvatar(text = "EP", size = 32.dp)
                    Spacer(Modifier.width(12.dp))
                    BasicText(
                        "Title",
                        style = LocalElTheme.current.typography.large.copy(color = LocalElTheme.current.colors.textPrimary),
                    )
                    Spacer(Modifier.width(12.dp))
                    BasicText(
                        "Sub title",
                        style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textRegular),
                    )
                    Spacer(Modifier.width(8.dp))
                    ElTag("Default")
                }
            },
            extra = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ElButton(onClick = {}, text = "Print", size = ElSize.Small)
                    ElButton(onClick = {}, text = "Edit", size = ElSize.Small, type = ElType.Primary)
                }
            },
        )
    }
    DemoExample("Custom back content", "Replace the whole back button with the back slot.") {
        ElPageHeader(
            title = "Detail",
            onBack = {},
            backContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElIcon(ElIcons.ArrowLeft, size = 16.dp, tint = LocalElTheme.current.colors.primary)
                    Spacer(Modifier.width(6.dp))
                    BasicText(
                        "Return",
                        style = LocalElTheme.current.typography.base.copy(color = LocalElTheme.current.colors.primary),
                    )
                }
            },
        )
    }
    DemoExample("Main content", "Add extra content below the header with the default slot.") {
        ElPageHeader(title = "Page title", subtitle = "Main content slot", onBack = {}) {
            BasicText(
                "Your additional content can be added with the default slot.",
                style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
            )
        }
    }
    DemoExample("Complete example", "Combine breadcrumb, header content, actions and page content.") {
        ElPageHeader(
            title = "Profile",
            backText = "Back",
            onBack = {},
            breadcrumb = {
                ElBreadcrumb(
                    items = listOf(
                        ElBreadcrumbItemData("Home", onClick = {}),
                        ElBreadcrumbItemData("Settings", onClick = {}),
                        ElBreadcrumbItemData("Profile"),
                    ),
                )
            },
            headerContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElAvatar(text = "EP", size = 32.dp)
                    Spacer(Modifier.width(12.dp))
                    BasicText(
                        "Profile",
                        style = LocalElTheme.current.typography.large.copy(color = LocalElTheme.current.colors.textPrimary),
                    )
                    Spacer(Modifier.width(12.dp))
                    BasicText(
                        "Manage your account",
                        style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textRegular),
                    )
                }
            },
            extra = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ElButton(onClick = {}, text = "Cancel", size = ElSize.Small)
                    ElButton(onClick = {}, text = "Save", size = ElSize.Small, type = ElType.Primary)
                }
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ElDescriptions(
                    items = listOf(
                        ElDescriptionItem("Username") { BasicText("kooriookami") },
                        ElDescriptionItem("Telephone") { BasicText("18100000000") },
                        ElDescriptionItem("Place") { BasicText("Suzhou") },
                        ElDescriptionItem("Address", span = 3) {
                            BasicText("No.1188, Wuzhong Avenue, Wuzhong District, Suzhou, Jiangsu Province")
                        },
                    ),
                    column = 3,
                    size = ElSize.Small,
                )
                BasicText(
                    "Element Plus team uses weekly release strategy under normal circumstance.",
                    style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
                )
            }
        }
    }
}
