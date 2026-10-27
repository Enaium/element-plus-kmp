package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun MenuDocs() {
    var vertical by remember { mutableStateOf("2-1") }
    var horizontal by remember { mutableStateOf("home") }
    var collapsed by remember { mutableStateOf("1") }
    DemoExample("Vertical menu", "Vertical menus support nested submenus.") {
        ElMenu(activeIndex = vertical, onSelect = { vertical = it }, defaultOpeneds = setOf("2"), modifier = Modifier.height(280.dp)) {
            ElMenuItem("1", "Dashboard", icon = ElIcons.HomeFilled)
            ElSubMenu("2", "Components", icon = ElIcons.Grid) {
                ElMenuItem("2-1", "Buttons")
                ElMenuItem("2-2", "Forms")
            }
            ElMenuItem("3", "Settings", icon = ElIcons.Setting)
        }
    }
    DemoExample("Horizontal menu", "Use mode horizontal for a top navigation bar.") {
        ElMenu(activeIndex = horizontal, onSelect = { horizontal = it }, mode = ElMenuMode.Horizontal, modifier = Modifier.fillMaxWidth()) {
            ElMenuItem("home", "Home")
            ElMenuItem("docs", "Docs")
            ElSubMenu("more", "More") {
                ElMenuItem("changelog", "Changelog")
                ElMenuItem("faq", "FAQ")
            }
        }
    }
    DemoExample("Collapsed menu", "Collapsed menus show icons and open submenus in a popup.") {
        ElMenu(activeIndex = collapsed, onSelect = { collapsed = it }, collapsed = true, modifier = Modifier.height(220.dp)) {
            ElMenuItem("1", "Home", icon = ElIcons.HomeFilled)
            ElMenuItem("2", "Settings", icon = ElIcons.Setting)
            ElSubMenu("3", "More", icon = ElIcons.MoreFilled) {
                ElMenuItem("3-1", "Profile")
                ElMenuItem("3-2", "Logout")
            }
        }
    }
}
