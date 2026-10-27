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
            ElLink("Primary", onClick = {}, icon = ElIcons.Edit)
            ElLink("Success", onClick = {}, type = ElType.Success)
            ElLink("Warning", onClick = {}, type = ElType.Warning)
            ElLink("Danger", onClick = {}, type = ElType.Danger)
            ElLink("Disabled", onClick = {}, disabled = true)
            ElLink("Underline", onClick = {}, underline = ElLinkUnderline.Always)
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
    DemoExample("Button Group", "Group a series of similar operations together.") {
        ElButtonGroup {
            ElButton(onClick = {}, text = "Previous", plain = true)
            ElButton(onClick = {}, text = "Middle", plain = true)
            ElButton(onClick = {}, text = "Next", plain = true)
        }
    }
    DemoExample("Loading Button", "Set loading to true to display the loading state.") {
        DemoRow {
            ElButton(onClick = {}, text = "Loading", loading = true, type = ElType.Primary)
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
}
