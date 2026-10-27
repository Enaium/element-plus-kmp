package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElBacktop
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun BacktopDocs() {
    DemoExample(
        "Basic usage",
        "Scroll down inside the box: the button appears after the visibility height and returns the container to the top.",
    ) {
        BacktopSample()
    }
    DemoExample(
        "Custom content",
        "The content slot replaces the default caret icon; the slot fills the circular button.",
    ) {
        BacktopSample(custom = true)
    }
    DemoExample(
        "Position offsets",
        "right and bottom inset the button from the placement corner (the Element Plus right/bottom props).",
    ) {
        BacktopSample(right = 100.dp, bottom = 60.dp)
    }
    DemoExample(
        "Target list",
        "Pass a LazyListState instead of a ScrollState to scroll a lazy list back to its first item.",
    ) {
        BacktopListSample()
    }
    DemoExample(
        "Click event",
        "onClick is the `click` event and fires before the container scrolls.",
    ) {
        BacktopClickSample()
    }
}

@Composable
private fun BacktopSample(
    custom: Boolean = false,
    right: Dp = 20.dp,
    bottom: Dp = 20.dp,
) {
    val theme = LocalElTheme.current
    val scroll = rememberScrollState()
    Box(Modifier.fillMaxWidth().height(220.dp)) {
        Column(Modifier.fillMaxWidth().height(220.dp).verticalScroll(scroll)) {
            repeat(12) { index ->
                BasicText(
                    text = "Backtop content ${index + 1}",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    style = theme.typography.small,
                )
            }
        }
        ElBacktop(
            modifier = Modifier.align(Alignment.BottomEnd),
            scrollState = scroll,
            visibilityHeight = 80.dp,
            right = right,
            bottom = bottom,
            content = if (custom) {
                {
                    BasicText(
                        text = "UP",
                        style = theme.typography.extraSmall.copy(color = theme.colors.primary),
                    )
                }
            } else {
                null
            },
        )
    }
}

@Composable
private fun BacktopListSample() {
    val theme = LocalElTheme.current
    val listState = rememberLazyListState()
    Box(Modifier.fillMaxWidth().height(220.dp)) {
        LazyColumn(Modifier.fillMaxWidth().height(220.dp), state = listState) {
            items(30) { index ->
                BasicText(
                    text = "List item ${index + 1}",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    style = theme.typography.small,
                )
            }
        }
        ElBacktop(
            modifier = Modifier.align(Alignment.BottomEnd),
            listState = listState,
            visibilityHeight = 100.dp,
            right = 16.dp,
            bottom = 16.dp,
        )
    }
}

@Composable
private fun BacktopClickSample() {
    val theme = LocalElTheme.current
    val scroll = rememberScrollState()
    var clicks by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.fillMaxWidth().height(180.dp)) {
            Column(Modifier.fillMaxWidth().height(180.dp).verticalScroll(scroll)) {
                repeat(10) { index ->
                    BasicText(
                        text = "Click demo content ${index + 1}",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        style = theme.typography.small,
                    )
                }
            }
            ElBacktop(
                modifier = Modifier.align(Alignment.BottomEnd),
                scrollState = scroll,
                visibilityHeight = 60.dp,
                right = 16.dp,
                bottom = 16.dp,
                onClick = { clicks++ },
            )
        }
        BasicText(
            text = "onClick fired $clicks time(s)",
            style = theme.typography.extraSmall.copy(color = theme.colors.textSecondary),
        )
    }
}
