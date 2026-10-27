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
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun DividerDocs() {
    val theme = LocalElTheme.current
    DemoExample("Basic usage", "A horizontal divider separates sections of content.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText("Content above", style = theme.typography.base)
            ElDivider()
            BasicText("Content below", style = theme.typography.base)
        }
    }
    DemoExample("Dashed and dotted", "Change the divider line style.") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElDivider(borderStyle = ElDividerBorderStyle.Dashed)
            ElDivider(borderStyle = ElDividerBorderStyle.Dotted)
        }
    }
    DemoExample("With content", "Place text or icons in the divider.") {
        ElDivider(
            content = { ElText("Center", size = ElSize.Small) },
            contentPosition = ElDividerContentPosition.Center,
        )
        ElDivider(
            content = { ElText("Left", size = ElSize.Small, type = ElType.Primary) },
            contentPosition = ElDividerContentPosition.Left,
        )
        ElDivider(
            content = { ElText("Right", size = ElSize.Small) },
            contentPosition = ElDividerContentPosition.Right,
        )
        ElDivider(
            content = {
                ElIcon(
                    ElIcons.StarFilled,
                    size = 16.dp,
                    tint = LocalElTheme.current.colors.warning,
                )
            },
        )
    }
    DemoExample("Vertical divider", "Separate inline items with a vertical line.") {
        Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
            ElText("Item")
            ElDivider(direction = ElDividerDirection.Vertical, modifier = Modifier.padding(horizontal = 12.dp))
            ElText("Item")
            ElDivider(direction = ElDividerDirection.Vertical, modifier = Modifier.padding(horizontal = 12.dp))
            ElText("Item")
        }
    }
}
