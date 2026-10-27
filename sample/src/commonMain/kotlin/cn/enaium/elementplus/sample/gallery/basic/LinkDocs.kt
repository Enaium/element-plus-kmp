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
fun LinkDocs() {
    DemoExample("Basic link", "Links support href, target and the semantic types.") {
        DemoRow {
            ElLink("default", href = "https://element-plus.org", target = "_blank")
            ElLink("primary", onClick = {}, type = ElType.Primary)
            ElLink("success", onClick = {}, type = ElType.Success)
            ElLink("warning", onClick = {}, type = ElType.Warning)
            ElLink("danger", onClick = {}, type = ElType.Danger)
            ElLink("info", onClick = {}, type = ElType.Info)
        }
    }
    DemoExample("Href and target", "href opens through the platform URI handler; _self keeps navigation in the click handler.") {
        DemoRow {
            ElLink("External link", href = "https://element-plus.org", target = "_blank")
            ElLink("In-app link", href = "/components/link", target = "_self", onClick = {})
            ElLink("No handler or href", underline = ElLinkUnderline.Never)
        }
    }
    DemoExample("Underline", "Control when the underline appears, with the enum or the boolean shorthand.") {
        DemoRow {
            ElLink("default", onClick = {})
            ElLink("always", underline = ElLinkUnderline.Always, onClick = {})
            ElLink("hover", underline = ElLinkUnderline.Hover, onClick = {})
            ElLink("never", underline = ElLinkUnderline.Never, onClick = {})
            ElLink("boolean true", underline = true, onClick = {})
            ElLink("boolean false", underline = false, onClick = {})
        }
    }
    DemoExample("Disabled", "Disable a link to prevent interaction.") {
        DemoRow {
            ElLink("Disabled", onClick = {}, disabled = true)
            ElLink("Disabled primary", onClick = {}, type = ElType.Primary, disabled = true)
            ElLink("Disabled href", href = "https://element-plus.org", disabled = true)
        }
    }
    DemoExample("With icon", "A link can include an icon prop or custom icon content.") {
        DemoRow {
            ElLink("Edit", onClick = {}, icon = ElIcons.Edit)
            ElLink("Download", onClick = {}, type = ElType.Success, icon = ElIcons.Download)
            ElLink("Custom icon", onClick = {}, iconContent = { color ->
                ElIcon(ElIcons.Star, size = 14.dp, tint = color)
            })
            ElLink(onClick = {}) { color, decoration ->
                BasicText(
                    "Check",
                    style = LocalElTheme.current.typography.base.copy(color = color, textDecoration = decoration),
                )
                Spacer(Modifier.width(4.dp))
                ElIcon(ElIcons.View, size = 14.dp, tint = color)
            }
        }
    }
}
