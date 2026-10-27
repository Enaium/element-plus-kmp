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
    val theme = LocalElTheme.current
    DemoExample("Basic link", "Links support the semantic types.") {
        DemoRow {
            ElLink("Default", onClick = {})
            ElLink("Primary", onClick = {}, type = ElType.Primary)
            ElLink("Success", onClick = {}, type = ElType.Success)
            ElLink("Warning", onClick = {}, type = ElType.Warning)
            ElLink("Danger", onClick = {}, type = ElType.Danger)
            ElLink("Info", onClick = {}, type = ElType.Info)
        }
    }
    DemoExample("Underline", "Control when the underline appears.") {
        DemoRow {
            ElLink("Always", onClick = {}, underline = ElLinkUnderline.Always)
            ElLink("Hover", onClick = {}, underline = ElLinkUnderline.Hover)
            ElLink("Never", onClick = {}, underline = ElLinkUnderline.Never)
        }
    }
    DemoExample("Disabled", "Disable a link to prevent interaction.") {
        DemoRow {
            ElLink("Disabled", onClick = {}, disabled = true)
            ElLink("Disabled primary", onClick = {}, type = ElType.Primary, disabled = true)
        }
    }
    DemoExample("With icon", "A link can include a leading icon.") {
        DemoRow {
            ElLink("Edit", onClick = {}, icon = ElIcons.Edit)
            ElLink("Download", onClick = {}, type = ElType.Success, icon = ElIcons.Download)
            ElLink("Link", onClick = {}, type = ElType.Primary, icon = ElIcons.Link)
        }
    }
}
