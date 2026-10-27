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
import cn.enaium.elementplus.components.ElCard
import cn.enaium.elementplus.components.ElCardShadow
import cn.enaium.elementplus.components.ElCheckbox
import cn.enaium.elementplus.components.ElDialog
import cn.enaium.elementplus.components.ElEmpty
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElLink
import cn.enaium.elementplus.components.ElLinkUnderline
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessagePlacement
import cn.enaium.elementplus.components.ElPagination
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.components.ElSelect
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.components.ElTable
import cn.enaium.elementplus.components.ElTableColumn
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.sample.gallery.DemoRowSpaced
import cn.enaium.elementplus.theme.ElButtonConfig
import cn.enaium.elementplus.theme.ElCardConfig
import cn.enaium.elementplus.theme.ElConfigProvider
import cn.enaium.elementplus.theme.ElDialogConfig
import cn.enaium.elementplus.theme.ElLinkConfig
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.theme.ElLocales
import cn.enaium.elementplus.theme.ElMessageConfig
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTableConfig
import cn.enaium.elementplus.theme.ElTheme
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

private data class ConfigProviderRow(val date: String, val name: String, val address: String)

@Composable
fun ConfigProviderDocs() {
    var input by remember { mutableStateOf("") }
    DemoExample(
        title = "Usage",
        description = "ElConfigProvider installs locale, default size, z-index and the per-component defaults for its subtree.",
    ) {
        val config = LocalElConfig.current
        DemoRowSpaced {
            ElText("Locale: ${config.locale.name}", size = ElSize.Small)
            ElText("Size: ${config.size?.name ?: "Default"}", size = ElSize.Small)
            ElText("Z-index: ${config.zIndex}", size = ElSize.Small)
            ElText("Namespace: ${config.namespace}", size = ElSize.Small)
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
    DemoExample(
        title = "Nested providers",
        description = "A nested provider inherits every setting it does not pass: the size of the outer provider survives, only the locale changes.",
    ) {
        ElConfigProvider(size = ElSize.Large) {
            val outer = LocalElConfig.current
            BasicText(
                "outer: size=${outer.size?.name}, locale=${outer.locale.name}",
                style = LocalElTheme.current.typography.small,
            )
            ElConfigProvider(locale = ElLocale.Chinese) {
                val inner = LocalElConfig.current
                BasicText(
                    "inner: size=${inner.size?.name}, locale=${inner.locale.name}",
                    style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.primary),
                )
                ElButton(onClick = {}, text = "继承大小")
            }
        }
    }
    DemoExample("Button config", "Global button defaults can be configured once and are inherited by every button.") {
        var autoInsertSpace by remember { mutableStateOf(true) }
        var plain by remember { mutableStateOf(true) }
        var round by remember { mutableStateOf(true) }
        var dashed by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf(false) }
        var type by remember { mutableStateOf("default") }
        DemoRow {
            ElCheckbox(checked = autoInsertSpace, onCheckedChange = { autoInsertSpace = it }, label = "autoInsertSpace")
            ElCheckbox(checked = plain, onCheckedChange = { plain = it }, label = "plain")
            ElCheckbox(checked = round, onCheckedChange = { round = it }, label = "round")
            ElCheckbox(checked = dashed, onCheckedChange = { dashed = it }, label = "dashed")
            ElCheckbox(checked = text, onCheckedChange = { text = it }, label = "text")
        }
        ElRadioGroup(
            options = listOf(
                ElRadioOption("default", "default"),
                ElRadioOption("primary", "primary"),
                ElRadioOption("success", "success"),
                ElRadioOption("warning", "warning"),
                ElRadioOption("danger", "danger"),
                ElRadioOption("info", "info"),
            ),
            selected = type,
            onSelectedChange = { type = it },
        )
        ElConfigProvider(
            button = ElButtonConfig(
                autoInsertSpace = autoInsertSpace,
                plain = plain,
                round = round,
                dashed = dashed,
                text = text,
                type = when (type) {
                    "primary" -> ElType.Primary
                    "success" -> ElType.Success
                    "warning" -> ElType.Warning
                    "danger" -> ElType.Danger
                    "info" -> ElType.Info
                    else -> null
                },
            ),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElButton(onClick = {}, text = "中文")
                ElButton(onClick = {}, text = "Normal text")
            }
        }
    }
    DemoExample("Card config", "The card shadow can be configured for a whole subtree.") {
        var shadow by remember { mutableStateOf(ElCardShadow.Always) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElRadioGroup(
                options = listOf(
                    ElRadioOption("always", "always"),
                    ElRadioOption("hover", "hover"),
                    ElRadioOption("never", "never"),
                ),
                selected = shadow.name.lowercase(),
                onSelectedChange = {
                    shadow = when (it) {
                        "hover" -> ElCardShadow.Hover
                        "never" -> ElCardShadow.Never
                        else -> ElCardShadow.Always
                    }
                },
            )
            ElConfigProvider(card = ElCardConfig(shadow = shadow)) {
                ElCard(header = { BasicText("Card name", style = LocalElTheme.current.typography.small) }) {
                    BasicText("Card desu!", style = LocalElTheme.current.typography.small)
                }
            }
        }
    }
    DemoExample("Dialog config", "Dialog defaults such as vertical centering are configured once for the subtree.") {
        var alignCenter by remember { mutableStateOf(true) }
        var visible by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSwitch(checked = alignCenter, onCheckedChange = { alignCenter = it }, activeText = "alignCenter")
            ElConfigProvider(dialog = ElDialogConfig(alignCenter = alignCenter)) {
                ElButton(onClick = { visible = true }, text = "Open Dialog", type = ElType.Primary)
                ElDialog(visible = visible, onClose = { visible = false }, title = "Dialog Title") {
                    BasicText("Dialog Content", style = LocalElTheme.current.typography.small)
                }
            }
        }
    }
    DemoExample("Link config", "Link type and underline default to the provider configuration.") {
        var type by remember { mutableStateOf("success") }
        var underline by remember { mutableStateOf("always") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoRow {
                ElSelect(
                    value = type,
                    onValueChange = { type = it ?: "success" },
                    options = listOf("primary", "success", "warning", "info", "danger", "default").map { ElSelectOption(it, it) },
                    modifier = Modifier.width(150.dp),
                )
                ElSelect(
                    value = underline,
                    onValueChange = { underline = it ?: "always" },
                    options = listOf("always", "hover", "never").map { ElSelectOption(it, it) },
                    modifier = Modifier.width(150.dp),
                )
            }
            ElConfigProvider(
                link = ElLinkConfig(
                    type = when (type) {
                        "primary" -> ElType.Primary
                        "success" -> ElType.Success
                        "warning" -> ElType.Warning
                        "info" -> ElType.Info
                        "danger" -> ElType.Danger
                        else -> ElType.Default
                    },
                    underline = when (underline) {
                        "always" -> ElLinkUnderline.Always
                        "never" -> ElLinkUnderline.Never
                        else -> ElLinkUnderline.Hover
                    },
                ),
            ) {
                ElLink(text = "Link desu!", onClick = {})
            }
        }
    }
    DemoExample("Message config", "Message defaults (max count, plain style and placement) come from the provider.") {
        val max = 3
        var plain by remember { mutableStateOf(true) }
        var placement by remember { mutableStateOf(ElMessagePlacement.Bottom) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoRow {
                DemoHint("max = $max")
                ElSwitch(checked = plain, onCheckedChange = { plain = it }, activeText = "plain")
                ElRadioGroup(
                    options = listOf(
                        ElRadioOption("top", "top"),
                        ElRadioOption("bottom", "bottom"),
                    ),
                    selected = if (placement == ElMessagePlacement.Bottom) "bottom" else "top",
                    onSelectedChange = {
                        placement = if (it == "bottom") ElMessagePlacement.Bottom else ElMessagePlacement.Top
                    },
                )
            }
            ElConfigProvider(message = ElMessageConfig(max = max, plain = plain, placement = placement)) {
                ElButton(onClick = { ElMessage.show("This is a message from the provider configuration.") }, text = "OPEN")
            }
        }
    }
    DemoExample(
        "Table config",
        "Table-level defaults such as showOverflowTooltip apply to every column that does not set the prop itself.",
    ) {
        var showOverflowTooltip by remember { mutableStateOf(true) }
        val rows = remember {
            listOf(
                ConfigProviderRow("2016-05-04", "Aleyna Kutzner", "Lohrbergstr. 86c, Süd Lilli, Saarland"),
                ConfigProviderRow("2016-05-03", "Helen Jacobi", "760 A Street, South Frankfield, Illinois"),
                ConfigProviderRow("2016-05-02", "Brandon Deckert", "Arnold-Ohletz-Str. 41a, Alt Malinascheid, Thüringen"),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElCheckbox(
                checked = showOverflowTooltip,
                onCheckedChange = { showOverflowTooltip = it },
                label = "showOverflowTooltip",
            )
            ElConfigProvider(table = ElTableConfig(showOverflowTooltip = showOverflowTooltip)) {
                ElTable(
                    data = rows,
                    columns = listOf(
                        ElTableColumn("Date", width = 120.dp, property = { it.date }),
                        ElTableColumn("Name", width = 140.dp, property = { it.name }),
                        ElTableColumn("Address (inherited)", width = 260.dp, property = { it.address }),
                    ),
                )
            }
        }
    }
    DemoExample(
        "Empty values",
        "emptyValues and valueOnClear define which values count as empty and what a cleared input becomes.",
    ) {
        var value by remember { mutableStateOf<String?>(null) }
        var cleared by remember { mutableStateOf("not cleared yet") }
        ElConfigProvider(emptyValues = listOf(null, ""), valueOnClear = null) {
            ElSelect(
                value = value,
                onValueChange = {
                    value = it
                    cleared = if (it == null || it.isEmpty()) "the empty value is: $it" else "value: $it"
                },
                options = listOf(ElSelectOption("", "All")) + (1..5).map { ElSelectOption("Option$it", "Option$it") },
                clearable = true,
                placeholder = "Select",
                modifier = Modifier.width(240.dp),
            )
        }
        DemoHint(cleared)
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
    DemoExample("Theme", "Theme tokens can be overridden for a subtree; a nested provider inherits the parent theme unless it overrides it.") {
        var dark by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSwitch(checked = dark, onCheckedChange = { dark = it }, activeText = "Dark", inactiveText = "Light", inlinePrompt = true)
            ElConfigProvider(theme = if (dark) ElTheme.Dark else ElTheme.Light) {
                ElConfigProvider(locale = ElLocale.English) {
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
}
