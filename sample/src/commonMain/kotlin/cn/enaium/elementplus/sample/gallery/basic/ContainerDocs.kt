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
fun ContainerDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    DemoExample("Common layout", "Container places header, footer and content around a page.") {
        Box(Modifier.fillMaxWidth().height(220.dp)) {
            ElContainer(
                header = {
                    ElHeader(Modifier.padding(horizontal = 12.dp)) { BasicText("Header", style = theme.typography.small) }
                },
                footer = {
                    ElFooter(Modifier.padding(horizontal = 12.dp)) { BasicText("Footer", style = theme.typography.small) }
                },
            ) {
                ElMain {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BasicText("Main", style = theme.typography.base.copy(color = colors.textSecondary))
                    }
                }
            }
        }
    }
    DemoExample("Header, aside and main", "Use an aside when the page needs a sidebar.") {
        Box(Modifier.fillMaxWidth().height(220.dp)) {
            ElContainer(
                direction = ElContainerDirection.Horizontal,
                header = {
                    ElHeader(Modifier.padding(horizontal = 12.dp)) { BasicText("Header", style = theme.typography.small) }
                },
                aside = {
                    ElAside(width = 150.dp) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) { BasicText("Menu ${it + 1}", style = theme.typography.small) }
                        }
                    }
                },
                asideWidth = 150.dp,
            ) {
                ElMain {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BasicText("Main", style = theme.typography.base.copy(color = colors.textSecondary))
                    }
                }
            }
        }
    }
    DemoExample("Header, aside, main and footer", "All four container regions can be combined.") {
        Box(Modifier.fillMaxWidth().height(240.dp)) {
            ElContainer(
                direction = ElContainerDirection.Horizontal,
                header = { ElHeader(Modifier.padding(horizontal = 12.dp)) { BasicText("Header", style = theme.typography.small) } },
                footer = { ElFooter(Modifier.padding(horizontal = 12.dp)) { BasicText("Footer", style = theme.typography.small) } },
                aside = { ElAside(width = 140.dp) { BasicText("Aside", Modifier.padding(12.dp), style = theme.typography.small) } },
                asideWidth = 140.dp,
            ) {
                ElMain {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BasicText("Main", style = theme.typography.base.copy(color = colors.textSecondary))
                    }
                }
            }
        }
    }

    DemoExample("Header and main", "A header above the main content area.") {
        Box(Modifier.fillMaxWidth().height(190.dp)) {
            ElContainer(
                header = { ElHeader(Modifier.padding(horizontal = 12.dp)) { BasicText("Header", style = LocalElTheme.current.typography.small) } },
            ) {
                ElMain {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BasicText("Main content", style = LocalElTheme.current.typography.base.copy(color = LocalElTheme.current.colors.textSecondary))
                    }
                }
            }
        }
    }
    DemoExample("Aside and main", "An aside beside the main area.") {
        Box(Modifier.fillMaxWidth().height(190.dp)) {
            ElContainer(
                direction = ElContainerDirection.Horizontal,
                aside = { ElAside(width = 140.dp) { BasicText("Aside", Modifier.padding(12.dp), style = LocalElTheme.current.typography.small) } },
                asideWidth = 140.dp,
            ) {
                ElMain {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BasicText("Main content", style = LocalElTheme.current.typography.base.copy(color = LocalElTheme.current.colors.textSecondary))
                    }
                }
            }
        }
    }
}
