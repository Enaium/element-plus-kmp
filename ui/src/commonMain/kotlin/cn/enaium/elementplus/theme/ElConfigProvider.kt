package cn.enaium.elementplus.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Immutable
data class ElButtonConfig(
    val autoInsertSpace: Boolean = false,
    val plain: Boolean = false,
    val round: Boolean = false,
    val text: Boolean = false,
    val dashed: Boolean = false,
)

@Immutable
data class ElConfig(
    val locale: ElLocale = ElLocale.English,
    val size: ElSize? = null,
    val zIndex: Int = 2000,
    val button: ElButtonConfig = ElButtonConfig(),
)

val LocalElConfig = staticCompositionLocalOf { ElConfig() }

/**
 * Equivalent of `<el-config-provider>`: installs the theme, locale, default
 * component size and global button defaults for all descendants.
 */
@Composable
fun ElConfigProvider(
    theme: ElTheme = ElTheme.Light,
    locale: ElLocale = ElLocale.English,
    size: ElSize? = null,
    zIndex: Int = 2000,
    button: ElButtonConfig = ElButtonConfig(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalElTheme provides theme,
        LocalElConfig provides ElConfig(locale = locale, size = size, zIndex = zIndex, button = button),
        LocalLayoutDirection provides if (locale.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
        content()
    }
}

@Composable
fun elLocale(): ElLocale = LocalElConfig.current.locale

@Composable
fun elTheme(): ElTheme = LocalElTheme.current
