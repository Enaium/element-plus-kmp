package cn.enaium.elementplus.sample.gallery.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElParagraph
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTitle
import cn.enaium.elementplus.components.ElTypography
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

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
    DemoExample(
        "Font size convention",
        "The six type tokens of the design system: extra small, small, base, medium, large and extra large.",
    ) {
        val rows = listOf(
            "Supplementary text" to theme.typography.extraSmall,
            "Body (small)" to theme.typography.small,
            "Body" to theme.typography.base,
            "Small Title" to theme.typography.medium,
            "Title" to theme.typography.large,
            "Main Title" to theme.typography.extraLarge,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DemoHint("Level")
                DemoHint("Font size")
                DemoHint("Demo")
            }
            rows.forEach { (level, style) ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    BasicText(level, Modifier.width(150.dp), style = theme.typography.extraSmall.copy(color = colors.textSecondary))
                    BasicText(
                        "${style.fontSize.value.toInt()} sp",
                        Modifier.width(90.dp),
                        style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                    )
                    BasicText("Build with Element", style = style.copy(color = colors.textPrimary))
                }
            }
        }
    }
    DemoExample(
        "Font stacks",
        "Element Plus renders with the platform UI font and falls back through the CJK and Latin stacks of the system.",
    ) {
        val stacks = listOf(
            "PingFang SC · Hiragino Sans GB · Microsoft YaHei" to FontFamily.SansSerif,
            "Helvetica Neue · Helvetica · Arial" to FontFamily.SansSerif,
            "Georgia · Times New Roman" to FontFamily.Serif,
            "Menlo · Consolas · Courier New" to FontFamily.Monospace,
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            stacks.forEach { (label, family) ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    BasicText(
                        label,
                        style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                    )
                    BasicText(
                        "Build with Element 构建更美好的产品",
                        style = theme.typography.base.copy(color = colors.textPrimary, fontFamily = family),
                    )
                }
            }
        }
    }
    DemoExample(
        "Line height",
        "The theme uses 1.5 as the regular line height; compact text uses 1.3 and loose text 1.7, no line height is 1.",
    ) {
        val sample =
            "Element Plus typography follows a consistent rhythm so that dense paragraphs stay readable " +
                "and long form content keeps a comfortable measure across the product."
        val ratios = listOf(
            "line-height: 1 · No line height" to 1.0f,
            "line-height: 1.3 · Compact" to 1.3f,
            "line-height: 1.5 · Regular" to 1.5f,
            "line-height: 1.7 · Loose" to 1.7f,
        )
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ratios.forEach { (label, ratio) ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BasicText(label, style = theme.typography.extraSmall.copy(color = colors.textSecondary))
                    BasicText(
                        sample,
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(colors.fillLighter, RoundedCornerShape(4.dp)),
                        style = withLineHeight(theme.typography.base, ratio).copy(color = colors.textRegular),
                    )
                }
            }
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

private fun withLineHeight(style: TextStyle, ratio: Float): TextStyle =
    style.copy(lineHeight = style.fontSize * ratio)
