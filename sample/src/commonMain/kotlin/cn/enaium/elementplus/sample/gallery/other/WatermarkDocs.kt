package cn.enaium.elementplus.sample.gallery.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElDivider
import cn.enaium.elementplus.components.ElDividerBorderStyle
import cn.enaium.elementplus.components.ElDividerContentPosition
import cn.enaium.elementplus.components.ElDividerDirection
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElWatermark
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun WatermarkDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Basic usage", "A tiled watermark is drawn over the content.") {
        ElWatermark(
            text = "Element Plus KMP",
            modifier = Modifier.fillMaxWidth().height(220.dp).background(colors.background, RoundedCornerShape(6.dp)),
            gapX = 80.dp,
            gapY = 60.dp,
            rotate = -18f,
            color = colors.primary.copy(alpha = 0.18f),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BasicText("Watermarked content", style = theme.typography.medium.copy(color = colors.textPrimary))
                    BasicText("The watermark tiles over this card.", style = theme.typography.small.copy(color = colors.textSecondary))
                }
            }
        }
    }
    DemoExample("Custom color and spacing", "Adjust color, gap and rotation for different content.") {
        ElWatermark(
            text = "Confidential",
            modifier = Modifier.fillMaxWidth().height(180.dp).background(colors.fillLight, RoundedCornerShape(6.dp)),
            gapX = 120.dp,
            gapY = 50.dp,
            rotate = -30f,
            color = colors.danger.copy(alpha = 0.2f),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BasicText("Confidential document", style = theme.typography.base.copy(color = colors.textPrimary))
            }
        }
    }

    DemoExample("Custom configuration", "Adjust color, rotation and gap.") {
        ElWatermark(
            text = "Element Plus",
            color = LocalElTheme.current.colors.primary.copy(alpha = 0.18f),
            rotate = -15f,
            gapX = 70.dp,
            gapY = 70.dp,
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BasicText("Custom watermark", style = LocalElTheme.current.typography.medium)
            }
        }
    }

    DemoExample("Multi-line watermark", "Watermark text can span multiple lines.") {
        ElWatermark(
            text = "Element Plus\nKMP",
            color = LocalElTheme.current.colors.primary.copy(alpha = 0.18f),
            rotate = -20f,
            gapX = 80.dp,
            gapY = 70.dp,
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BasicText("Multi-line watermark", style = LocalElTheme.current.typography.medium)
            }
        }
    }
}
