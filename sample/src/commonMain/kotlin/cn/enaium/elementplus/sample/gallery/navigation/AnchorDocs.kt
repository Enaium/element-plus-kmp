package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAffix
import cn.enaium.elementplus.components.ElAnchor
import cn.enaium.elementplus.components.ElAnchorDirection
import cn.enaium.elementplus.components.ElAnchorLink
import cn.enaium.elementplus.components.ElAnchorState
import cn.enaium.elementplus.components.ElAnchorType
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.rememberElAnchorState
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

private val anchorDocsLinks = listOf(
    ElAnchorLink("basic", "Basic Usage"),
    ElAnchorLink("horizontal", "Horizontal Mode"),
    ElAnchorLink("container", "Scroll Container"),
    ElAnchorLink(
        "api",
        "Anchor API",
        listOf(
            ElAnchorLink("attributes", "Anchor Attributes"),
            ElAnchorLink("events", "Anchor Events"),
        ),
    ),
)

@Composable
fun AnchorDocs() {
    DemoExample("Basic usage", "Nested anchor links highlight the active section; clicking selects it.") {
        var active by remember { mutableStateOf("basic") }
        ElAnchor(
            links = anchorDocsLinks,
            activeHref = active,
            onActiveChange = { active = it },
        )
    }
    DemoExample(
        "Change event",
        "onActiveChange is the `change` event and hides the manual active handling: the anchor reports the selected href.",
    ) {
        var active by remember { mutableStateOf("basic") }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            ElAnchor(
                links = anchorDocsLinks,
                activeHref = active,
                onActiveChange = { active = it },
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BasicText(
                    "Change emitted for: $active",
                    style = LocalElTheme.current.typography.small,
                )
                BasicText(
                    "Click a link to emit another change.",
                    style = LocalElTheme.current.typography.extraSmall.copy(
                        color = LocalElTheme.current.colors.textSecondary,
                    ),
                )
            }
        }
    }
    DemoExample(
        "Horizontal mode",
        "direction = ElAnchorDirection.Horizontal lays the links out inline; nested links are hidden in this mode.",
    ) {
        var active by remember { mutableStateOf("basic") }
        ElAnchor(
            links = anchorDocsLinks.take(3),
            activeHref = active,
            onActiveChange = { active = it },
            direction = ElAnchorDirection.Horizontal,
        )
    }
    DemoExample(
        "Underline type",
        "type = ElAnchorType.Underline replaces the moving marker bar with a marker on a full-height guideline.",
    ) {
        var active by remember { mutableStateOf("basic") }
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.Top) {
            ElAnchor(
                links = anchorDocsLinks,
                activeHref = active,
                onActiveChange = { active = it },
                type = ElAnchorType.Underline,
                modifier = Modifier.width(200.dp),
            )
            Column(Modifier.width(260.dp)) {
                ElAnchor(
                    links = anchorDocsLinks.take(3),
                    activeHref = active,
                    onActiveChange = { active = it },
                    type = ElAnchorType.Underline,
                    direction = ElAnchorDirection.Horizontal,
                )
            }
        }
    }
    DemoExample(
        "Scroll container",
        "Pass the container's ScrollState plus the section offsets and the anchor spies on that container and scrolls it.",
    ) {
        AnchorScrollSample()
    }
    DemoExample(
        "Select first link at scroll top",
        "selectScrollTop highlights the first link when the container is back at the very top.",
    ) {
        AnchorScrollSample(initialActive = "", selectScrollTop = true)
    }
    DemoExample(
        "Affix anchor",
        "An anchor pinned with ElAffix (offset 60 → 20 here) while the page content scrolls underneath.",
    ) {
        AnchorAffixSample()
    }
    DemoExample(
        "Programmatic scrollTo and custom link content",
        "The anchor state exposes scrollTo(href) and linkContent replaces the default link label (the anchor-link default slot).",
    ) {
        val state = rememberElAnchorState()
        AnchorScrollSample(state = state, customLinkContent = true)
    }
}

@Composable
private fun AnchorScrollSample(
    state: ElAnchorState? = null,
    customLinkContent: Boolean = false,
    initialActive: String = "part1",
    selectScrollTop: Boolean = false,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val density = LocalDensity.current
    val scroll = rememberScrollState()
    var active by remember(initialActive) { mutableStateOf(initialActive) }
    val sectionHeight = 150.dp
    val gap = 20.dp
    val offsets = remember(density) {
        val section = with(density) { sectionHeight.roundToPx() }
        val space = with(density) { gap.roundToPx() }
        mapOf(
            "part1" to space,
            "part2" to section + 2 * space,
            "part3" to 2 * section + 3 * space,
        )
    }
    val anchorState = state
    val links = listOf(
        ElAnchorLink("part1", "part1"),
        ElAnchorLink("part2", "part2"),
        ElAnchorLink("part3", "part3"),
    )
    val customContent: (@Composable (ElAnchorLink, Boolean) -> Unit)? = if (customLinkContent) {
        { link, isActive ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(if (isActive) colors.primary else colors.borderLight, CircleShape),
                )
                Spacer(Modifier.width(6.dp))
                BasicText(
                    text = link.title.uppercase(),
                    style = theme.typography.extraSmall.copy(
                        color = if (isActive) colors.primary else colors.textSecondary,
                    ),
                )
            }
        }
    } else {
        null
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f).height(200.dp).verticalScroll(scroll)) {
                repeat(3) { index ->
                    Spacer(Modifier.height(gap))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(sectionHeight)
                            .background(colors.light(ElType.Primary, 9), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(
                            text = "part${index + 1}",
                            style = theme.typography.small.copy(color = colors.textRegular),
                        )
                    }
                }
            }
            ElAnchor(
                links = links,
                activeHref = active,
                onActiveChange = { active = it },
                scrollState = scroll,
                sectionOffsets = offsets,
                offsetTop = gap,
                width = 120.dp,
                state = anchorState,
                selectScrollTop = selectScrollTop,
                linkContent = customContent,
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                "Active: $active",
                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
            )
            if (anchorState != null) {
                ElButton(
                    onClick = { anchorState.scrollTo("part3") },
                    text = "scrollTo(\"part3\")",
                    size = ElSize.Small,
                )
            }
        }
    }
}

@Composable
private fun AnchorAffixSample() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scroll = rememberScrollState()
    var active by remember { mutableStateOf("basic") }
    Box(Modifier.fillMaxWidth().height(240.dp)) {
        Column(Modifier.fillMaxWidth().height(240.dp).verticalScroll(scroll)) {
            Spacer(Modifier.height(40.dp))
            ElAffix(
                scrollState = scroll,
                offsetTop = 20.dp,
                modifier = Modifier.fillMaxWidth(),
            ) { affixed ->
                ElAnchor(
                    links = anchorDocsLinks,
                    activeHref = active,
                    onActiveChange = { active = it },
                    offsetTop = 70.dp,
                    modifier = Modifier
                        .background(if (affixed) colors.light(ElType.Primary, 9) else colors.background)
                        .padding(vertical = 4.dp),
                )
            }
            repeat(10) { index ->
                BasicText(
                    text = "Scrollable content ${index + 1}",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    style = theme.typography.small,
                )
            }
        }
    }
}
