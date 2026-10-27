package cn.enaium.elementplus.sample.gallery.config

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElEmpty
import cn.enaium.elementplus.components.ElPagination
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSelect
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElButtonConfig
import cn.enaium.elementplus.theme.ElConfigProvider
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.theme.ElLocales
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTheme
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun ConfigProviderDocs() {
    var input by remember { mutableStateOf("") }
    DemoExample(
        title = "Usage",
        description = "ElConfigProvider installs locale, default size and theme for its subtree.",
    ) {
        val config = LocalElConfig.current
        DemoRowSpaced {
            ElText("Locale: ${config.locale.name}", size = ElSize.Small)
            ElText("Size: ${config.size?.name ?: "Default"}", size = ElSize.Small)
            ElText("Z-index: ${config.zIndex}", size = ElSize.Small)
        }
        ElConfigProvider(size = ElSize.Large, locale = ElLocale.Chinese) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BasicText("Large + zh-cn subtree", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.primary))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElButton(onClick = {}, text = "确定", type = ElType.Primary)
                    ElInput(value = input, onValueChange = { input = it }, placeholder = "请输入内容")
                }
            }
        }
    }
    DemoExample("Button config", "Global button defaults can be configured once.") {
        ElConfigProvider(
            button = ElButtonConfig(
                autoInsertSpace = true,
                plain = true,
                round = true,
            ),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElButton(onClick = {}, text = "按钮")
                ElButton(onClick = {}, text = "Normal text")
                ElButton(onClick = {}, text = "Primary", type = ElType.Primary)
            }
        }
    }
    DemoExample("Locale", "Locale controls pagination, date picker and empty-state copy.") {
        var page by remember { mutableStateOf(1) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BasicText("English", style = LocalElTheme.current.typography.small)
            ElPagination(total = 100, currentPage = page, onCurrentPageChange = { page = it })
            ElConfigProvider(locale = ElLocale.Chinese) {
                BasicText("中文", style = LocalElTheme.current.typography.small)
                ElPagination(total = 100, currentPage = page, onCurrentPageChange = { page = it })
                ElEmpty()
            }
        }
    }
    DemoExample("All locales", "All 68 Element Plus locales are bundled; switching is synchronous and includes RTL direction metadata.") {
        var locale by remember { mutableStateOf(ElLocales.en) }
        var page by remember { mutableStateOf(1) }
        val options = remember { ElLocales.all.map { ElSelectOption(it.name, it.name) } }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSelect(
                value = locale.name,
                onValueChange = { locale = ElLocales.byName(it ?: "en") },
                options = options,
                filterable = true,
                modifier = Modifier.width(260.dp),
            )
            ElConfigProvider(locale = locale) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BasicText(
                        "${locale.name} · ${locale.select.placeholder} · ${locale.datePicker.selectDate}",
                        style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.primary),
                    )
                    ElPagination(total = 100, currentPage = page, onCurrentPageChange = { page = it })
                    ElEmpty()
                }
            }
        }
    }
    DemoExample("Theme", "Theme tokens can be overridden for a subtree.") {
        var dark by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSwitch(checked = dark, onCheckedChange = { dark = it }, activeText = "Dark", inactiveText = "Light", inlinePrompt = true)
            ElConfigProvider(theme = if (dark) ElTheme.Dark else ElTheme.Light) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(LocalElTheme.current.colors.backgroundPage, RoundedCornerShape(6.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ElButton(onClick = {}, text = "Primary", type = ElType.Primary)
                    ElButton(onClick = {}, text = "Default")
                    ElButton(onClick = {}, text = "Danger", type = ElType.Danger)
                }
            }
        }
    }
}
