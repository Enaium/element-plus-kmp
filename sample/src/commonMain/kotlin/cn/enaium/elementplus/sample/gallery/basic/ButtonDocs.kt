package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElCard
import cn.enaium.elementplus.components.ElCardShadow
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ButtonDocs() {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var verticalGroup by remember { mutableStateOf(false) }
    DemoExample(
        title = "Basic usage",
        description = "Use type, plain, round, dashed and circle to define Button's style.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DemoRow {
                ElButton(onClick = {}, text = "Default")
                ElButton(onClick = {}, text = "Primary", type = ElType.Primary)
                ElButton(onClick = {}, text = "Success", type = ElType.Success)
                ElButton(onClick = {}, text = "Info", type = ElType.Info)
                ElButton(onClick = {}, text = "Warning", type = ElType.Warning)
                ElButton(onClick = {}, text = "Danger", type = ElType.Danger)
            }
            DemoRow {
                ElButton(onClick = {}, text = "Plain")
                ElButton(onClick = {}, text = "Primary", type = ElType.Primary, plain = true)
                ElButton(onClick = {}, text = "Success", type = ElType.Success, plain = true)
                ElButton(onClick = {}, text = "Info", type = ElType.Info, plain = true)
                ElButton(onClick = {}, text = "Warning", type = ElType.Warning, plain = true)
                ElButton(onClick = {}, text = "Danger", type = ElType.Danger, plain = true)
            }
            DemoRow {
                ElButton(onClick = {}, text = "Round")
                ElButton(onClick = {}, text = "Primary", type = ElType.Primary, round = true)
                ElButton(onClick = {}, text = "Success", type = ElType.Success, round = true)
                ElButton(onClick = {}, text = "Info", type = ElType.Info, round = true)
                ElButton(onClick = {}, text = "Warning", type = ElType.Warning, round = true)
                ElButton(onClick = {}, text = "Danger", type = ElType.Danger, round = true)
            }
            DemoRow {
                ElButton(onClick = {}, text = "Dashed")
                ElButton(onClick = {}, text = "Primary", type = ElType.Primary, dashed = true)
                ElButton(onClick = {}, text = "Success", type = ElType.Success, dashed = true)
                ElButton(onClick = {}, text = "Info", type = ElType.Info, dashed = true)
                ElButton(onClick = {}, text = "Warning", type = ElType.Warning, dashed = true)
                ElButton(onClick = {}, text = "Danger", type = ElType.Danger, dashed = true)
            }
            DemoRow {
                ElButton(onClick = {}, icon = ElIcons.Search, circle = true)
                ElButton(onClick = {}, icon = ElIcons.Edit, type = ElType.Primary, circle = true)
                ElButton(onClick = {}, icon = ElIcons.Check, type = ElType.Success, circle = true)
                ElButton(onClick = {}, icon = ElIcons.Message, type = ElType.Info, circle = true)
                ElButton(onClick = {}, icon = ElIcons.Star, type = ElType.Warning, circle = true)
                ElButton(onClick = {}, icon = ElIcons.Delete, type = ElType.Danger, circle = true)
            }
        }
    }
    DemoExample("Sizes", "Use size to change the button size.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DemoRow {
                ElButton(onClick = {}, text = "Large", size = ElSize.Large)
                ElButton(onClick = {}, text = "Default")
                ElButton(onClick = {}, text = "Small", size = ElSize.Small)
            }
            DemoRow {
                ElButton(onClick = {}, text = "Search", icon = ElIcons.Search, size = ElSize.Large)
                ElButton(onClick = {}, text = "Search", icon = ElIcons.Search)
                ElButton(onClick = {}, text = "Search", icon = ElIcons.Search, size = ElSize.Small)
            }
        }
    }
    DemoExample("Disabled", "The disabled attribute determines if the button is disabled.") {
        DemoRow {
            ElButton(onClick = {}, text = "Default", disabled = true)
            ElButton(onClick = {}, text = "Primary", type = ElType.Primary, disabled = true)
            ElButton(onClick = {}, text = "Success", type = ElType.Success, disabled = true)
            ElButton(onClick = {}, text = "Info", type = ElType.Info, disabled = true)
            ElButton(onClick = {}, text = "Warning", type = ElType.Warning, disabled = true)
            ElButton(onClick = {}, text = "Danger", type = ElType.Danger, disabled = true)
            ElButton(onClick = {}, text = "Plain", type = ElType.Primary, plain = true, disabled = true)
        }
    }
    DemoExample("Link Button", "Use link to render a button as a link.") {
        DemoRow {
            ElButton(onClick = {}, text = "plain", link = true)
            ElButton(onClick = {}, text = "primary", link = true, type = ElType.Primary)
            ElButton(onClick = {}, text = "success", link = true, type = ElType.Success)
            ElButton(onClick = {}, text = "info", link = true, type = ElType.Info)
            ElButton(onClick = {}, text = "warning", link = true, type = ElType.Warning)
            ElButton(onClick = {}, text = "danger", link = true, type = ElType.Danger)
        }
        DemoRow {
            ElButton(onClick = {}, text = "plain", link = true, disabled = true)
            ElButton(onClick = {}, text = "primary", link = true, type = ElType.Primary, disabled = true)
            ElButton(onClick = {}, text = "success", link = true, type = ElType.Success, disabled = true)
            ElButton(onClick = {}, text = "danger", link = true, type = ElType.Danger, disabled = true)
        }
    }
    DemoExample("Text Button", "Text buttons have no border and background.") {
        DemoRow {
            ElButton(onClick = {}, text = "Text", textButton = true, type = ElType.Primary)
            ElButton(onClick = {}, text = "Text", textButton = true, type = ElType.Success)
            ElButton(onClick = {}, text = "Text", textButton = true, type = ElType.Warning)
            ElButton(onClick = {}, text = "Text", textButton = true, type = ElType.Danger)
            ElButton(onClick = {}, text = "Disabled", textButton = true, disabled = true)
        }
    }
    DemoExample("Icon Button", "Use the icon attribute to add meaning to a button.") {
        DemoRow {
            ElButton(onClick = {}, text = "Search", icon = ElIcons.Search)
            ElButton(onClick = {}, text = "Upload", icon = ElIcons.Upload, type = ElType.Primary)
            ElButton(onClick = {}, text = "Delete", icon = ElIcons.Delete, type = ElType.Danger, plain = true)
            ElButton(onClick = {}, icon = ElIcons.MoreFilled, circle = true, plain = true)
        }
    }
    DemoExample("Icon on the right", "Use the content slot to place an icon after the label.") {
        DemoRow {
            ElButton(
                onClick = {},
                content = { color ->
                    BasicText("Next Page", style = LocalElTheme.current.typography.base.copy(color = color))
                    Spacer(Modifier.width(6.dp))
                    ElIcon(ElIcons.ArrowRight, size = 14.dp, tint = color)
                },
            )
            ElButton(
                onClick = {},
                type = ElType.Primary,
                content = { color ->
                    BasicText("Continue", style = LocalElTheme.current.typography.base.copy(color = color))
                    Spacer(Modifier.width(6.dp))
                    ElIcon(ElIcons.ArrowRight, size = 14.dp, tint = color)
                },
            )
        }
    }
    DemoExample("Button Group", "Group similar operations; the direction toggles horizontal and vertical grouping.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoRow {
                ElButtonGroup {
                    ElButtonGroupItem(first = true, last = false) {
                        ElButton(onClick = {}, text = "Previous Page", icon = ElIcons.ArrowLeft, type = ElType.Primary)
                    }
                    ElButtonGroupItem(first = false, last = true) {
                        ElButton(onClick = {}, text = "Next Page", icon = ElIcons.ArrowRight, type = ElType.Primary)
                    }
                }
            }
            DemoRow {
                ElButton(
                    onClick = { verticalGroup = false },
                    text = "Horizontal",
                    size = ElSize.Small,
                    type = if (verticalGroup) ElType.Default else ElType.Primary,
                )
                ElButton(
                    onClick = { verticalGroup = true },
                    text = "Vertical",
                    size = ElSize.Small,
                    type = if (verticalGroup) ElType.Primary else ElType.Default,
                )
            }
            ElButtonGroup(vertical = verticalGroup, type = ElType.Primary) {
                ElButton(onClick = {}, icon = ElIcons.House)
                ElButton(onClick = {}, icon = ElIcons.Operation)
                ElButton(onClick = {}, icon = ElIcons.Notification)
            }
            DemoRow {
                ElButtonGroup(size = ElSize.Small, type = ElType.Success) {
                    ElButton(onClick = {}, text = "Inherited size")
                    ElButton(onClick = {}, text = "Inherited type")
                }
            }
        }
    }
    DemoExample("Loading Button", "Set loading to true; loadingIcon and the loading slot replace the spinner.") {
        DemoRow {
            ElButton(onClick = {}, text = "Loading", loading = true, type = ElType.Primary)
            ElButton(
                onClick = {},
                text = "Custom icon",
                loading = true,
                loadingIcon = ElIcons.Refresh,
                type = ElType.Success,
            )
            ElButton(
                onClick = {},
                text = "Custom slot",
                loading = true,
                type = ElType.Warning,
                loadingContent = { color ->
                    ElIcon(ElIcons.Refresh, size = 14.dp, tint = color, spin = true)
                },
            )
            ElButton(
                onClick = {
                    loading = true
                    scope.launch {
                        delay(1200)
                        loading = false
                    }
                },
                text = if (loading) "Loading" else "Click to load",
                loading = loading,
                type = ElType.Primary,
            )
        }
    }
    DemoExample("Custom Color", "The color prop sets the base color and derives the hover and active shades.") {
        val custom = Color(0xFF626AEF)
        val isDark = LocalElTheme.current.colors.isDark
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DemoRow {
                ElButton(onClick = {}, text = "Default", color = custom, dark = isDark)
                ElButton(onClick = {}, text = "Plain", color = custom, dark = isDark, plain = true)
                ElButton(onClick = {}, text = "Link", color = custom, dark = isDark, link = true)
                ElButton(onClick = {}, text = "Text", color = custom, dark = isDark, textButton = true)
                ElButton(onClick = {}, text = "Text BG", color = custom, dark = isDark, textButton = true, bg = true)
            }
            DemoRow {
                ElButton(onClick = {}, text = "Disabled", color = custom, dark = isDark, disabled = true)
                ElButton(onClick = {}, text = "Disabled Plain", color = custom, dark = isDark, disabled = true, plain = true)
                ElButton(onClick = {}, text = "Disabled Link", color = custom, dark = isDark, disabled = true, link = true)
                ElButton(onClick = {}, text = "Disabled Text", color = custom, dark = isDark, disabled = true, textButton = true)
            }
        }
    }
    DemoExample("Custom Tag", "The tag prop selects the semantic role; link buttons open href with the platform URI handler.") {
        DemoRow {
            ElButton(onClick = {}, text = "div", tag = ElButtonTag.Div)
            ElButton(
                onClick = {},
                text = "a",
                tag = ElButtonTag.Link,
                type = ElType.Primary,
                href = "https://element-plus.org",
            )
        }
    }
}
