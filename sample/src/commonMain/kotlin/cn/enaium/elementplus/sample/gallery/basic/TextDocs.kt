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
fun TextDocs() {
    val theme = LocalElTheme.current
    DemoExample("Basic text", "Text supports semantic types and sizes.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DemoRow {
                ElText("Default")
                ElText("Primary", type = ElType.Primary)
                ElText("Success", type = ElType.Success)
                ElText("Warning", type = ElType.Warning)
                ElText("Danger", type = ElType.Danger)
                ElText("Info", type = ElType.Info)
            }
            DemoRow {
                ElText("Large", size = ElSize.Large)
                ElText("Default")
                ElText("Small", size = ElSize.Small)
            }
        }
    }
    DemoExample("Decorations", "Bold, italic, underline, strikethrough and mark.") {
        DemoRow {
            ElText("Bold", bold = true)
            ElText("Italic", italic = true)
            ElText("Underline", underline = true)
            ElText("Strikethrough", strikethrough = true)
            ElText("Mark", mark = true)
            ElText("Disabled", disabled = true)
        }
    }
    DemoExample("Truncated", "Long text can be truncated with an ellipsis.") {
        ElText(
            text = "This is a very long text that demonstrates the truncated property of ElText. It will be cut off with an ellipsis at the end of the available width.",
            truncated = true,
        )
        Spacer(Modifier.height(8.dp))
        ElText(
            text = "Line clamp can show multiple lines. ".repeat(6),
            lineClamp = 2,
            truncated = true,
        )
    }

    DemoExample("Mixed content", "The default slot mixes text with icons and other components.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElText { color ->
                ElIcon(ElIcons.ElementPlus, size = 16.dp, tint = color)
                Spacer(Modifier.width(4.dp))
                BasicText("Element Plus", style = LocalElTheme.current.typography.base.copy(color = color))
            }
            DemoRow {
                ElText("Rate")
                ElText("★★★★★", type = ElType.Warning, modifier = Modifier.padding(start = 6.dp))
            }
            ElText { color ->
                BasicText(
                    "This is text mixed icon ",
                    style = LocalElTheme.current.typography.base.copy(color = color),
                )
                ElIcon(ElIcons.Bell, size = 16.dp, tint = color)
                BasicText(
                    " and component ",
                    style = LocalElTheme.current.typography.base.copy(color = color),
                )
                ElButton(onClick = {}, text = "Button", size = ElSize.Small)
            }
        }
    }
    DemoExample("Tag", "The tag prop applies the presentation of the matching HTML element.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElText("span")
            ElText("This is a paragraph.", tag = ElTextTag.Paragraph)
            ElText("Bold", tag = ElTextTag.Bold)
            ElText("Italic", tag = ElTextTag.Italic)
            ElText("Inserted", tag = ElTextTag.Insert)
            ElText("Deleted", tag = ElTextTag.Delete)
            ElText("Marked", tag = ElTextTag.Mark)
            DemoRow {
                ElText("This is")
                ElText("subscript", tag = ElTextTag.Sub, size = ElSize.Small)
            }
            DemoRow {
                ElText("This is")
                ElText("superscript", tag = ElTextTag.Sup, size = ElSize.Small)
            }
        }
    }
    DemoExample("Line clamp", "maxLines clamps the text without enabling truncation.") {
        ElText(
            "Line clamp works on its own. ".repeat(8),
            maxLines = 2,
            modifier = Modifier.width(260.dp),
        )
    }

    DemoExample("Sizes", "Use the small, default and large text sizes.") {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ElText("Large text", size = ElSize.Large)
            ElText("Default text")
            ElText("Small text", size = ElSize.Small)
        }
    }

    DemoExample("Ellipsis", "Truncate long text to one or more lines.") {
        ElText(
            "This is a long piece of text that should be truncated with an ellipsis when it exceeds the available width.",
            truncated = true,
            lineClamp = 1,
            modifier = Modifier.width(260.dp),
        )
    }
    DemoExample("Override", "Override the text color and decoration.") {
        ElText("Custom colored text", color = LocalElTheme.current.colors.primary, bold = true)
        ElText("Underlined text", underline = true)
        ElText("Marked text", mark = true)
    }
}
