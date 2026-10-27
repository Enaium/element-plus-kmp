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
fun ColorDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Semantic colors", "Colors convey meaning and are shared between components.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElColorRow("Primary / Success / Warning / Danger / Info", listOf(colors.primary, colors.success, colors.warning, colors.danger, colors.info))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ElColor(colors.primary, label = "primary")
                ElColor(colors.success, label = "success")
                ElColor(colors.warning, label = "warning")
                ElColor(colors.danger, label = "danger")
                ElColor(colors.info, label = "info")
            }
        }
    }
    DemoExample("Light variants", "Each semantic color has nine light mixes for backgrounds and borders.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElColorRow("Primary", (1..9).map { colors.light(ElType.Primary, it) })
            ElColorRow("Success", (1..9).map { colors.light(ElType.Success, it) })
            ElColorRow("Warning", (1..9).map { colors.light(ElType.Warning, it) })
            ElColorRow("Danger", (1..9).map { colors.light(ElType.Danger, it) })
        }
    }
    DemoExample("Neutral colors", "Text, border and fill tokens used by the components.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElColorRow("Text", listOf(colors.textPrimary, colors.textRegular, colors.textSecondary, colors.textPlaceholder, colors.textDisabled))
            ElColorRow("Border", listOf(colors.border, colors.borderLight, colors.borderLighter, colors.borderExtraLight))
            ElColorRow("Fill", listOf(colors.fill, colors.fillLight, colors.fillLighter, colors.fillExtraLight))
        }
    }

    DemoExample("Main and secondary colors", "Primary and its dark/light variants.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElColorRow("Primary shades", listOf(colors.light(ElType.Primary, 3), colors.primary, colors.dark(ElType.Primary)))
            ElColorRow("Success shades", listOf(colors.light(ElType.Success, 3), colors.success, colors.dark(ElType.Success)))
        }
    }

    DemoExample(
        "Colors in components",
        "The semantic types are shared by buttons, tags and alerts, so the same type reads the same everywhere.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(ElType.Primary, ElType.Success, ElType.Warning, ElType.Danger, ElType.Info).forEach { type ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElButton(onClick = {}, text = type.name, type = type)
                    ElTag(text = type.name, type = type)
                    ElAlert(
                        title = "${type.name} alert",
                        description = "The ${type.name.lowercase()} palette drives the whole component.",
                        type = type,
                        closable = false,
                        modifier = Modifier.width(280.dp),
                    )
                }
            }
        }
    }
}
