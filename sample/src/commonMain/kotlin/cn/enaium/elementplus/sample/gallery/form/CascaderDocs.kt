package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElCascader
import cn.enaium.elementplus.components.ElCascaderLazyLoad
import cn.enaium.elementplus.components.ElCascaderMultiple
import cn.enaium.elementplus.components.ElCascaderOption
import cn.enaium.elementplus.components.ElCascaderPanel
import cn.enaium.elementplus.components.ElCascaderProps
import cn.enaium.elementplus.components.ElCascaderShowCheckedStrategy
import cn.enaium.elementplus.components.ElCheckbox
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoLabel
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

@Composable
fun CascaderDocs() {
    val options = listOf(
        ElCascaderOption(
            "sichuan", "Sichuan", listOf(
                ElCascaderOption("chengdu", "Chengdu"),
                ElCascaderOption("mianyang", "Mianyang"),
            ),
        ),
        ElCascaderOption(
            "guangdong", "Guangdong", listOf(
                ElCascaderOption("guangzhou", "Guangzhou"),
                ElCascaderOption("shenzhen", "Shenzhen"),
                ElCascaderOption("zhuhai", "Zhuhai (disabled)", disabled = true),
            ),
        ),
    )
    var basic by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
    var filterable by remember { mutableStateOf(emptyList<String>()) }
    DemoExample("Basic usage", "Select a path through cascading columns.") {
        ElCascader(
            value = basic,
            onValueChange = { basic = it },
            options = options,
            placeholder = "Select a city",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Path: ${basic.joinToString(" / ")}")
    }
    DemoExample("Filterable", "Search among all leaf labels.") {
        ElCascader(
            value = filterable,
            onValueChange = { filterable = it },
            options = options,
            filterable = true,
            placeholder = "Type to filter",
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Disabled", "Disable selection while keeping the current path visible.") {
        ElCascader(
            value = listOf("guangdong", "shenzhen"),
            onValueChange = {},
            options = options,
            disabled = true,
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Clearable", "Clear the current path.") {
        var clearable by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
        ElCascader(
            value = clearable,
            onValueChange = { clearable = it },
            options = options,
            placeholder = "Clearable cascader",
            clearable = true,
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample(
        "Custom clear icon",
        "Replace the default clear icon with the clear-icon parameter.",
    ) {
        var value by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
        ElCascader(
            value = value,
            onValueChange = { value = it },
            options = options,
            clearable = true,
            clearIcon = ElIcons.CloseBold,
            placeholder = "Hover to clear",
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Display only the last level", "Show only the selected leaf in the field.") {
        var leaf by remember { mutableStateOf(listOf("guangdong", "shenzhen")) }
        ElCascader(
            value = leaf,
            onValueChange = { leaf = it },
            options = options,
            showAllLevels = false,
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Disabled option", "Disabled options cannot be selected.") {
        var disabled by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
        ElCascader(
            value = disabled,
            onValueChange = { disabled = it },
            options = options,
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Zhuhai is disabled in the province list.")
    }

    DemoExample(
        "Multiple selection",
        "Multiple mode renders a checkbox column and selected paths as tags. Collapse the tags or cap the visible tag count.",
    ) {
        var all by remember {
            mutableStateOf(
                listOf(
                    listOf("asia", "china", "beijing"),
                    listOf("asia", "japan", "tokyo"),
                ),
            )
        }
        var collapsed by remember { mutableStateOf(all) }
        var capped by remember { mutableStateOf(all) }
        DemoLabel("Display all tags (default)")
        ElCascaderMultiple(
            values = all,
            onValuesChange = { all = it },
            options = regionOptions,
            placeholder = "Select cities",
            modifier = Modifier.width(420.dp),
        )
        DemoLabel("Collapse tags with tooltip")
        ElCascaderMultiple(
            values = collapsed,
            onValuesChange = { collapsed = it },
            options = regionOptions,
            placeholder = "Select cities",
            collapseTags = true,
            collapseTagsTooltip = true,
            modifier = Modifier.width(420.dp),
        )
        DemoLabel("Collapse tags with max-collapse-tags = 2")
        ElCascaderMultiple(
            values = capped,
            onValuesChange = { capped = it },
            options = regionOptions,
            placeholder = "Select cities",
            collapseTags = true,
            collapseTagsTooltip = true,
            maxCollapseTags = 2,
            modifier = Modifier.width(420.dp),
        )
        DemoHint("Selected paths: ${all.size}")
    }

    DemoExample(
        "Any level selection",
        "checkStrictly lets a single-select cascader stop at any level instead of only leaves.",
    ) {
        var single by remember { mutableStateOf(emptyList<String>()) }
        ElCascader(
            value = single,
            onValueChange = { single = it },
            options = guideOptions,
            props = ElCascaderProps(checkStrictly = true),
            placeholder = "Select any level",
            clearable = true,
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Path: ${single.joinToString(" / ")}")
    }

    DemoExample(
        "Check strictly",
        "With checkStrictly in multiple mode, checking a parent no longer checks its children; every checkbox is independent.",
    ) {
        var multi by remember { mutableStateOf(emptyList<List<String>>()) }
        ElCascaderMultiple(
            values = multi,
            onValuesChange = { multi = it },
            options = guideOptions,
            props = ElCascaderProps(checkStrictly = true),
            placeholder = "Check any nodes",
            clearable = true,
            modifier = Modifier.width(420.dp),
        )
        DemoHint("Checked paths: ${multi.size}")
    }

    DemoExample(
        "Dynamic loading",
        "lazy + lazyLoad fetch children when a node is expanded. Set leaf = true on the returned nodes to stop loading.",
    ) {
        var lazyValue by remember { mutableStateOf(emptyList<String>()) }
        val lazyLoad: ElCascaderLazyLoad = { path, level ->
            delay(500)
            val parent = path.lastOrNull() ?: "root"
            List(level + 2) { index ->
                ElCascaderOption(
                    value = "$parent-$index",
                    label = "Option ${level + 1}-${index + 1}",
                    leaf = level >= 2,
                )
            }
        }
        ElCascader(
            value = lazyValue,
            onValueChange = { lazyValue = it },
            options = emptyList(),
            props = ElCascaderProps(lazy = true),
            lazyLoad = lazyLoad,
            placeholder = "Expand to load options",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Path: ${lazyValue.joinToString(" / ")}")
    }

    DemoExample(
        "Custom option content",
        "The option content slot replaces the default label in every column.",
    ) {
        var value by remember { mutableStateOf(emptyList<String>()) }
        ElCascader(
            value = value,
            onValueChange = { value = it },
            options = guideOptions,
            optionContent = { node, _ ->
                val theme = LocalElTheme.current
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = node.label,
                        style = theme.typography.base.copy(color = theme.colors.textRegular),
                    )
                    if (node.children.isNotEmpty()) {
                        BasicText(
                            text = " (${node.children.size})",
                            style = theme.typography.small.copy(color = theme.colors.textSecondary),
                        )
                    }
                }
            },
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Path: ${value.joinToString(" / ")}")
    }

    DemoExample(
        "Custom suggestion item",
        "suggestionContent customizes the rows shown while searching.",
    ) {
        var value by remember { mutableStateOf(emptyList<String>()) }
        ElCascader(
            value = value,
            onValueChange = { value = it },
            options = guideOptions,
            filterable = true,
            placeholder = "Try searching: Guide",
            suggestionContent = { _, path ->
                val theme = LocalElTheme.current
                BasicText(
                    text = "🔍 ${path.joinToString(" > ") { it.label }}",
                    style = theme.typography.base.copy(color = theme.colors.primary),
                )
            },
            modifier = Modifier.width(320.dp),
        )
    }

    DemoExample(
        "Cascader panel",
        "ElCascaderPanel renders the columns standalone, without the trigger and popup.",
    ) {
        var value by remember { mutableStateOf(emptyList<String>()) }
        ElCascaderPanel(
            value = value,
            onValueChange = { value = it },
            options = guideOptions,
            modifier = Modifier.width(520.dp),
        )
        DemoHint("Path: ${value.joinToString(" / ")}")
    }

    DemoExample(
        "Custom tag",
        "tagContent replaces the default tag. The first example colors tags and shows the leaf, the second shows only the top level.",
    ) {
        var colored by remember { mutableStateOf(emptyList<List<String>>()) }
        var topLevel by remember { mutableStateOf(emptyList<List<String>>()) }
        DemoLabel("Colored tags")
        ElCascaderMultiple(
            values = colored,
            onValuesChange = { colored = it },
            options = regionOptions,
            clearable = true,
            tagContent = { tag ->
                ElTag(
                    text = tag.pathLabels.lastOrNull() ?: tag.text,
                    color = if (tag.path.size % 2 == 0) Color(0xFFFFDE0A) else null,
                    size = ElSize.Small,
                    closable = true,
                    onClose = tag.onRemove,
                )
            },
            modifier = Modifier.width(420.dp),
        )
        DemoLabel("Display top-level tags only")
        ElCascaderMultiple(
            values = topLevel,
            onValuesChange = { topLevel = it },
            options = regionOptions,
            clearable = true,
            tagContent = { tag ->
                ElTag(
                    text = tag.pathLabels.firstOrNull() ?: tag.text,
                    type = cn.enaium.elementplus.theme.ElType.Primary,
                    size = ElSize.Small,
                    closable = true,
                    onClose = tag.onRemove,
                )
            },
            modifier = Modifier.width(420.dp),
        )
    }

    DemoExample(
        "Show checked strategy",
        "In multiple mode, showCheckedStrategy = Parent collapses fully selected parents into a single tag.",
    ) {
        val guideLeaves = leafPathsOf(listOf(guideOptions.first()))
        var child by remember { mutableStateOf(guideLeaves) }
        var parent by remember { mutableStateOf(guideLeaves) }
        DemoLabel("Strategy: child (default)")
        ElCascaderMultiple(
            values = child,
            onValuesChange = { child = it },
            options = guideOptions,
            showCheckedStrategy = ElCascaderShowCheckedStrategy.Child,
            clearable = true,
            modifier = Modifier.width(420.dp),
        )
        DemoLabel("Strategy: parent")
        ElCascaderMultiple(
            values = parent,
            onValuesChange = { parent = it },
            options = guideOptions,
            showCheckedStrategy = ElCascaderShowCheckedStrategy.Parent,
            clearable = true,
            modifier = Modifier.width(420.dp),
        )
        DemoHint("Child tags: ${child.size} - Parent tags: ${parent.size}")
    }

    DemoExample(
        "Click to check node",
        "checkOnClickNode toggles a node when its row is clicked instead of only its prefix.",
    ) {
        var single by remember { mutableStateOf(emptyList<String>()) }
        var multi by remember { mutableStateOf(emptyList<List<String>>()) }
        DemoLabel("checkStrictly single mode")
        ElCascader(
            value = single,
            onValueChange = { single = it },
            options = guideOptions,
            props = ElCascaderProps(checkStrictly = true, checkOnClickNode = true),
            clearable = true,
            modifier = Modifier.width(320.dp),
        )
        DemoLabel("Multiple mode")
        ElCascaderMultiple(
            values = multi,
            onValuesChange = { multi = it },
            options = guideOptions,
            props = ElCascaderProps(checkOnClickNode = true),
            clearable = true,
            modifier = Modifier.width(420.dp),
        )
    }

    DemoExample(
        "Custom header and footer",
        "The header hosts an all checkbox, the footer a clear action.",
    ) {
        var values by remember { mutableStateOf(emptyList<List<String>>()) }
        val allPaths = remember(guideOptions) { leafPathsOf(guideOptions) }
        val allChecked = allPaths.isNotEmpty() && values.toSet() == allPaths.toSet()
        ElCascaderMultiple(
            values = values,
            onValuesChange = { values = it },
            options = guideOptions,
            clearable = true,
            header = {
                ElCheckbox(
                    checked = allChecked,
                    onCheckedChange = { checked ->
                        values = if (checked) allPaths else emptyList()
                    },
                    label = "All",
                )
            },
            footer = {
                ElButton(
                    text = "Clear",
                    link = true,
                    size = ElSize.Small,
                    onClick = { values = emptyList() },
                )
            },
            modifier = Modifier.width(420.dp),
        )
        DemoHint("Selected: ${values.size} / ${allPaths.size}")
    }

    DemoExample(
        "Fit suggestion width",
        "By default the suggestion panel grows with its content. fitInputWidth matches the input, suggestionWidth fixes a pixel width.",
    ) {
        var value by remember { mutableStateOf(emptyList<String>()) }
        DemoLabel("Default expanding panel")
        ElCascader(
            value = value,
            onValueChange = { value = it },
            options = guideOptions,
            filterable = true,
            placeholder = "Default expanding panel",
            modifier = Modifier.width(320.dp),
        )
        DemoLabel("Fit input width")
        ElCascader(
            value = value,
            onValueChange = { value = it },
            options = guideOptions,
            filterable = true,
            fitInputWidth = true,
            placeholder = "Fit input width",
            modifier = Modifier.width(320.dp),
        )
        DemoLabel("Fixed width (420dp)")
        ElCascader(
            value = value,
            onValueChange = { value = it },
            options = guideOptions,
            filterable = true,
            suggestionWidth = 420.dp,
            suggestionContent = { option, _ ->
                val theme = LocalElTheme.current
                BasicText(
                    text = "${option.label} (custom content that is very long)",
                    style = theme.typography.base.copy(color = theme.colors.textRegular),
                )
            },
            placeholder = "Fixed width",
            modifier = Modifier.width(320.dp),
        )
    }
}

private fun leafPathsOf(
    nodes: List<ElCascaderOption>,
    parent: List<String> = emptyList(),
): List<List<String>> = nodes.flatMap { node ->
    val path = parent + node.value
    if (node.children.isEmpty()) listOf(path) else leafPathsOf(node.children, path)
}

private val regionOptions = listOf(
    ElCascaderOption(
        "asia", "Asia", listOf(
            ElCascaderOption(
                "china", "China", listOf(
                    ElCascaderOption("beijing", "Beijing"),
                    ElCascaderOption("shanghai", "Shanghai"),
                    ElCascaderOption("hangzhou", "Hangzhou"),
                ),
            ),
            ElCascaderOption(
                "japan", "Japan", listOf(
                    ElCascaderOption("tokyo", "Tokyo"),
                    ElCascaderOption("osaka", "Osaka"),
                    ElCascaderOption("kyoto", "Kyoto"),
                ),
            ),
            ElCascaderOption(
                "korea", "Korea", listOf(
                    ElCascaderOption("seoul", "Seoul"),
                    ElCascaderOption("busan", "Busan"),
                ),
            ),
        ),
    ),
    ElCascaderOption(
        "europe", "Europe", listOf(
            ElCascaderOption(
                "france", "France", listOf(
                    ElCascaderOption("paris", "Paris"),
                    ElCascaderOption("lyon", "Lyon"),
                ),
            ),
            ElCascaderOption(
                "uk", "UK", listOf(
                    ElCascaderOption("london", "London"),
                    ElCascaderOption("manchester", "Manchester"),
                ),
            ),
        ),
    ),
    ElCascaderOption(
        "north-america", "North America", listOf(
            ElCascaderOption(
                "us", "US", listOf(
                    ElCascaderOption("new-york", "New York"),
                    ElCascaderOption("los-angeles", "Los Angeles"),
                ),
            ),
            ElCascaderOption(
                "canada", "Canada", listOf(
                    ElCascaderOption("toronto", "Toronto"),
                    ElCascaderOption("montreal", "Montreal"),
                ),
            ),
        ),
    ),
)

private val guideOptions = listOf(
    ElCascaderOption(
        "guide", "Guide", listOf(
            ElCascaderOption(
                "disciplines", "Disciplines", listOf(
                    ElCascaderOption("consistency", "Consistency"),
                    ElCascaderOption("feedback", "Feedback"),
                    ElCascaderOption("efficiency", "Efficiency"),
                    ElCascaderOption("controllability", "Controllability"),
                ),
            ),
            ElCascaderOption(
                "navigation", "Navigation", listOf(
                    ElCascaderOption("side-nav", "Side Navigation"),
                    ElCascaderOption("top-nav", "Top Navigation"),
                ),
            ),
        ),
    ),
    ElCascaderOption(
        "component", "Component", listOf(
            ElCascaderOption(
                "basic", "Basic", listOf(
                    ElCascaderOption("layout", "Layout"),
                    ElCascaderOption("color", "Color"),
                    ElCascaderOption("typography", "Typography"),
                    ElCascaderOption("icon", "Icon"),
                    ElCascaderOption("button", "Button"),
                ),
            ),
            ElCascaderOption(
                "form", "Form", listOf(
                    ElCascaderOption("radio", "Radio"),
                    ElCascaderOption("checkbox", "Checkbox"),
                    ElCascaderOption("input", "Input"),
                    ElCascaderOption("select", "Select"),
                    ElCascaderOption("cascader", "Cascader"),
                ),
            ),
            ElCascaderOption(
                "data", "Data", listOf(
                    ElCascaderOption("table", "Table"),
                    ElCascaderOption("tag", "Tag"),
                    ElCascaderOption("tree", "Tree"),
                ),
            ),
        ),
    ),
    ElCascaderOption(
        "resource", "Resource", listOf(
            ElCascaderOption("axure", "Axure Components"),
            ElCascaderOption("sketch", "Sketch Templates"),
            ElCascaderOption("docs", "Design Documentation"),
        ),
    ),
)
