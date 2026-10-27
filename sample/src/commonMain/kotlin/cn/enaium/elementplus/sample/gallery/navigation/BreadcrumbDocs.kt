package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElBreadcrumb
import cn.enaium.elementplus.components.ElBreadcrumbItem
import cn.enaium.elementplus.components.ElBreadcrumbItemData
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun BreadcrumbDocs() {
    DemoExample(
        "Basic usage",
        "Items with an href are links (the `to` prop); the last item is the current page and is not clickable.",
    ) {
        var lastNavigation by remember { mutableStateOf("none") }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElBreadcrumb(
                items = listOf(
                    ElBreadcrumbItemData("homepage", href = "/"),
                    ElBreadcrumbItemData("promotion management", href = "/promotion"),
                    ElBreadcrumbItemData("promotion list"),
                    ElBreadcrumbItemData("promotion detail"),
                ),
                onNavigate = { href, _ -> lastNavigation = href },
            )
            BasicText(
                text = "onNavigate: $lastNavigation",
                style = LocalElTheme.current.typography.extraSmall.copy(
                    color = LocalElTheme.current.colors.textSecondary,
                ),
            )
        }
    }
    DemoExample("Icon separator", "separatorIcon replaces the text separator with an icon.") {
        ElBreadcrumb(
            items = listOf(
                ElBreadcrumbItemData("homepage", href = "/"),
                ElBreadcrumbItemData("promotion management"),
                ElBreadcrumbItemData("promotion list"),
                ElBreadcrumbItemData("promotion detail"),
            ),
            separatorIcon = ElIcons.ArrowRight,
        )
    }
    DemoExample("With icon", "Items can include a leading icon.") {
        ElBreadcrumb(
            items = listOf(
                ElBreadcrumbItemData("Home", icon = ElIcons.HomeFilled, href = "/"),
                ElBreadcrumbItemData("Settings", icon = ElIcons.Setting),
                ElBreadcrumbItemData("Profile"),
            ),
        )
    }
    DemoExample(
        "Link target",
        "target is passed through to onNavigate, so the caller decides how a link opens (e.g. a new window).",
    ) {
        var target by remember { mutableStateOf("none") }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElBreadcrumb(
                items = listOf(
                    ElBreadcrumbItemData("homepage", href = "/", target = "_blank"),
                    ElBreadcrumbItemData("promotion management", href = "/promotion"),
                    ElBreadcrumbItemData("promotion detail"),
                ),
                onNavigate = { href, resolvedTarget -> target = "$href (target=${resolvedTarget ?: "self"})" },
            )
            BasicText(
                text = "navigate to $target",
                style = LocalElTheme.current.typography.extraSmall.copy(
                    color = LocalElTheme.current.colors.textSecondary,
                ),
            )
        }
    }
    DemoExample(
        "Separator class",
        "separatorClass maps the CSS hook to a test tag on the separator; use separatorModifier to style it.",
    ) {
        val theme = LocalElTheme.current
        val colors = theme.colors
        ElBreadcrumb(
            items = listOf(
                ElBreadcrumbItemData("Home", href = "/"),
                ElBreadcrumbItemData("Components"),
                ElBreadcrumbItemData("Breadcrumb"),
            ),
            separator = ">",
            separatorClass = "el-breadcrumb__separator--bold",
            separatorModifier = Modifier
                .background(colors.light(ElType.Primary, 9), RoundedCornerShape(3.dp))
                .padding(horizontal = 2.dp),
        )
    }
    DemoExample(
        "Custom items",
        "The slot based API composes ElBreadcrumbItem so an item can render arbitrary content and be marked as the last one.",
    ) {
        val theme = LocalElTheme.current
        val colors = theme.colors
        var lastNavigation by remember { mutableStateOf("none") }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElBreadcrumb(onNavigate = { href, _ -> lastNavigation = href }) {
                ElBreadcrumbItem("Home", icon = ElIcons.HomeFilled, href = "/")
                ElBreadcrumbItem("Components", href = "/components")
                ElBreadcrumbItem(
                    label = "Custom item",
                    isLast = true,
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .background(colors.primary, CircleShape),
                            )
                            Box(Modifier.width(6.dp))
                            BasicText(
                                text = "Custom item",
                                style = theme.typography.small.copy(color = colors.primary),
                            )
                        }
                    },
                )
            }
            BasicText(
                text = "onNavigate: $lastNavigation",
                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
            )
        }
    }
}
