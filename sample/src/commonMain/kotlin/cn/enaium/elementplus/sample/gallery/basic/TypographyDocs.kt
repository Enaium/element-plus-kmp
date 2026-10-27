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
fun TypographyDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Headings", "Use a consistent heading scale.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElTypography("Heading 1", level = 1)
            ElTypography("Heading 2", level = 2)
            ElTypography("Heading 3", level = 3)
            ElTypography("Heading 4", level = 4)
            ElTypography("Heading 5", level = 5)
            ElTypography("Heading 6", level = 6)
        }
    }
    DemoExample("Text and paragraphs", "Body copy uses the base text style.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElText("This is a paragraph of text using the default text component.", type = ElType.Default)
            ElParagraph(
                "Element Plus typography provides a consistent scale and supports semantic colors, " +
                    "truncation and inline decorations.",
            )
            ElTitle("A title component", level = 4)
        }
    }
    DemoExample("Convention", "Primary text, regular text, secondary text and disabled text.") {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BasicText("Primary text", style = theme.typography.large.copy(color = colors.textPrimary))
            BasicText("Regular text", style = theme.typography.base.copy(color = colors.textRegular))
            BasicText("Secondary text", style = theme.typography.small.copy(color = colors.textSecondary))
            BasicText("Disabled text", style = theme.typography.small.copy(color = colors.textDisabled))
        }
    }
}
