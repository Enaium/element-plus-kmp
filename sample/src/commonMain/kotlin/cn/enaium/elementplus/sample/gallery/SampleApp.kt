package cn.enaium.elementplus.sample.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElDivider
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElLink
import cn.enaium.elementplus.components.ElScrollbar
import cn.enaium.elementplus.components.ElDrawer
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageBox
import cn.enaium.elementplus.components.ElNotification
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.components.ElBreadcrumb
import cn.enaium.elementplus.components.ElBreadcrumbItemData
import cn.enaium.elementplus.components.ElMenu
import cn.enaium.elementplus.components.ElSelect
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElMenuItem
import cn.enaium.elementplus.components.ElSubMenu
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElConfigProvider
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.theme.ElLocales
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.ElTheme
import cn.enaium.elementplus.theme.LocalElTheme
import cn.enaium.elementplus.components.ElDrawerDirection

@Composable
fun SampleApp(
    initialDark: Boolean = false,
    initialDrawerOpen: Boolean = false,
) {
    var dark by remember(initialDark) { mutableStateOf(initialDark) }
    var locale by remember { mutableStateOf(ElLocales.en) }
    var selectedId by remember { mutableStateOf("button") }
    var drawerOpen by remember(initialDrawerOpen) { mutableStateOf(initialDrawerOpen) }
    val components = remember { sampleComponents() }
    ElConfigProvider(
        theme = if (dark) ElTheme.Dark else ElTheme.Light,
        locale = locale,
    ) {
        ElMessage.Host {
            ElNotification.Host {
                ElMessageBox.Host {
                    SampleShell(
                        components = components,
                        selectedId = selectedId,
                        onSelect = {
                            selectedId = it
                            drawerOpen = false
                        },
                        drawerOpen = drawerOpen,
                        onDrawerClose = { drawerOpen = false },
                        onOpenDrawer = { drawerOpen = true },
                        dark = dark,
                        onDarkChange = { dark = it },
                        locale = locale,
                        onLocaleChange = { locale = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun SampleShell(
    components: List<SampleComponent>,
    selectedId: String,
    onSelect: (String) -> Unit,
    drawerOpen: Boolean,
    onDrawerClose: () -> Unit,
    onOpenDrawer: () -> Unit,
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    locale: ElLocale,
    onLocaleChange: (ElLocale) -> Unit,
) {
    val colors = LocalElTheme.current.colors
    BoxWithConstraints(Modifier.fillMaxSize().background(colors.backgroundPage)) {
        val wide = maxWidth >= 900.dp
        Column(Modifier.fillMaxSize()) {
            SampleHeader(
                showMenu = !wide,
                onOpenMenu = onOpenDrawer,
                dark = dark,
                onDarkChange = onDarkChange,
                locale = locale,
                onLocaleChange = onLocaleChange,
            )
            Row(Modifier.fillMaxSize()) {
                if (wide) {
                    SampleSidebar(
                        components = components,
                        selectedId = selectedId,
                        onSelect = onSelect,
                        locale = locale,
                        modifier = Modifier.width(280.dp).fillMaxHeight(),
                    )
                }
                SampleContent(
                    component = components.firstOrNull { it.id == selectedId } ?: components.first(),
                    locale = locale,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
        if (!wide) {
            ElDrawer(
                visible = drawerOpen,
                onClose = onDrawerClose,
                title = if (locale.name.startsWith("zh")) "组件" else "Components",
                direction = ElDrawerDirection.Left,
                size = 300.dp,
                scrollable = false,
            ) {
                SampleSidebar(
                    components = components,
                    selectedId = selectedId,
                    onSelect = onSelect,
                    locale = locale,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun SampleHeader(
    showMenu: Boolean,
    onOpenMenu: () -> Unit,
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    locale: ElLocale,
    onLocaleChange: (ElLocale) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val uriHandler = LocalUriHandler.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(colors.background)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showMenu) {
            ElIcon(
                ElIcons.Menu,
                size = 20.dp,
                tint = colors.textRegular,
                modifier = Modifier.clickable(onClick = onOpenMenu).padding(end = 14.dp),
            )
        }
        BasicText(
            text = "Element Plus",
            style = theme.typography.large.copy(color = colors.primary, fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.width(8.dp))
        BasicText(
            text = "Kotlin Multiplatform",
            style = theme.typography.small.copy(color = colors.textSecondary),
        )
        Spacer(Modifier.weight(1f))
        val localeOptions = remember {
            ElLocales.all.map { ElSelectOption(it.name, localeDisplayName(it)) }
        }
        ElSelect(
            value = locale.name,
            onValueChange = { onLocaleChange(ElLocales.byName(it ?: "en")) },
            options = localeOptions,
            filterable = true,
            size = ElSize.Small,
            modifier = Modifier.width(160.dp),
        )
        Spacer(Modifier.width(10.dp))
        ElSwitch(
            checked = dark,
            onCheckedChange = onDarkChange,
            activeText = "Dark",
            inactiveText = "Light",
            inlinePrompt = true,
        )
        Spacer(Modifier.width(14.dp))
        ElLink("GitHub", onClick = { uriHandler.openUri("https://github.com/Enaium/element-plus-kmp") })
    }
}

@Composable
private fun SampleSidebar(
    components: List<SampleComponent>,
    selectedId: String,
    onSelect: (String) -> Unit,
    locale: ElLocale,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, components, locale) {
        if (query.isBlank()) components
        else components.filter {
            it.localizedTitle(locale).contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
        }
    }
    Column(
        modifier
            .background(colors.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        ElInput(
            value = query,
            onValueChange = { query = it },
            placeholder = if (locale.name.startsWith("zh")) "搜索组件" else "Search components",
            clearable = true,
            prefix = { ElIcon(ElIcons.Search, size = 14.dp, tint = colors.textPlaceholder) },
        )
        Spacer(Modifier.height(10.dp))
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
            val availableHeight = maxHeight
            ElScrollbar(height = availableHeight) {
                key(query) {
                    ElMenu(
                        activeIndex = selectedId,
                        onSelect = onSelect,
                        defaultOpeneds = SampleSection.entries.map { "section-${it.id}" }.toSet(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SampleSection.entries.forEach { section ->
                            val sectionItems = filtered.filter { it.section == section }
                            if (sectionItems.isNotEmpty()) {
                                ElSubMenu(index = "section-${section.id}", title = section.localizedTitle(locale)) {
                                    sectionItems.forEach { component ->
                                        ElMenuItem(index = component.id, title = component.localizedTitle(locale))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleContent(
    component: SampleComponent,
    locale: ElLocale,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val uriHandler = LocalUriHandler.current
    val sourcePath = remember(component.id) { component.sourcePath() }
    val sourceUrl = remember(sourcePath) { component.sourceUrl() }
    val scroll = rememberScrollState()
    LaunchedEffect(component.id) { scroll.scrollTo(0) }
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .fillMaxHeight()
                .widthIn(max = 1040.dp)
                .fillMaxWidth()
                .verticalScroll(scroll)
                .padding(horizontal = 32.dp, vertical = 24.dp),
        ) {
            ElBreadcrumb(
                items = listOf(
                    ElBreadcrumbItemData(if (locale.name.startsWith("zh")) "组件" else "Components"),
                    ElBreadcrumbItemData(component.section.localizedTitle(locale)),
                    ElBreadcrumbItemData(component.localizedTitle(locale)),
                ),
            )
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    BasicText(
                        text = component.localizedTitle(locale),
                        style = theme.typography.extraLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                        ),
                    )
                    Spacer(Modifier.height(8.dp))
                    BasicText(
                        text = component.description,
                        style = theme.typography.base.copy(color = colors.textSecondary),
                    )
                }
                Spacer(Modifier.width(16.dp))
                ElButton(
                    onClick = { uriHandler.openUri(sourceUrl) },
                    text = "View sample source",
                    type = ElType.Primary,
                    plain = true,
                    size = ElSize.Small,
                )
            }
            Spacer(Modifier.height(20.dp))
            ElDivider()
            Spacer(Modifier.height(20.dp))
            CompositionLocalProvider(LocalSampleSourcePath provides sourcePath) {
                key(component.id) { SampleComponentPage(component) }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
