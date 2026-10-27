package cn.enaium.elementplus.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Element Plus semantic color types. */
enum class ElType { Default, Primary, Success, Warning, Info, Danger }

/** The three component sizes supported by Element Plus. */
enum class ElSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val fontSize: androidx.compose.ui.unit.TextUnit,
) {
    Large(40.dp, 20.dp, 13.dp, 14.sp),
    Default(32.dp, 16.dp, 9.dp, 14.sp),
    Small(24.dp, 12.dp, 6.dp, 12.sp),
}

/** Text alignment used by table cells and several textual components. */
enum class ElTextAlign { Start, Center, End }

/**
 * Element Plus design tokens. The light palette is the exact palette shipped by
 * `element-plus/theme-chalk/src/common/var.scss`; the dark palette mirrors the
 * values of `theme-chalk/src/dark/var.scss`.
 */
@Immutable
data class ElColors(
    val white: Color,
    val black: Color,
    val primary: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val error: Color,
    val info: Color,
    val textPrimary: Color,
    val textRegular: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,
    val border: Color,
    val borderLight: Color,
    val borderLighter: Color,
    val borderExtraLight: Color,
    val borderDark: Color,
    val borderDarker: Color,
    val fill: Color,
    val fillLight: Color,
    val fillLighter: Color,
    val fillExtraLight: Color,
    val fillDark: Color,
    val fillDarker: Color,
    val fillBlank: Color,
    val background: Color,
    val backgroundPage: Color,
    val backgroundOverlay: Color,
    val overlay: Color,
    val mask: Color,
    val isDark: Boolean = false,
) {
    fun colorOf(type: ElType): Color = when (type) {
        ElType.Default -> textRegular
        ElType.Primary -> primary
        ElType.Success -> success
        ElType.Warning -> warning
        ElType.Info -> info
        ElType.Danger -> danger
    }

    /** Equivalent of `--el-color-{type}-light-{level}` (level 1..9). */
    fun light(type: ElType, level: Int): Color {
        require(level in 1..9) { "light level must be in 1..9" }
        val base = colorOf(type)
        val mix = if (isDark) background else white
        return lerp(base, mix, level / 10f)
    }

    /** Equivalent of `--el-color-{type}-dark-2`. */
    fun dark(type: ElType): Color {
        val base = colorOf(type)
        return if (isDark) lerp(base, white, 0.2f) else lerp(base, black, 0.2f)
    }

    val disabledBackground: Color get() = fillLight
    val disabledText: Color get() = textPlaceholder
    val disabledBorder: Color get() = borderLight

    companion object {
        val Light = ElColors(
            white = Color(0xFFFFFFFF),
            black = Color(0xFF000000),
            primary = Color(0xFF409EFF),
            success = Color(0xFF67C23A),
            warning = Color(0xFFE6A23C),
            danger = Color(0xFFF56C6C),
            error = Color(0xFFF56C6C),
            info = Color(0xFF909399),
            textPrimary = Color(0xFF303133),
            textRegular = Color(0xFF606266),
            textSecondary = Color(0xFF909399),
            textPlaceholder = Color(0xFFA8ABB2),
            textDisabled = Color(0xFFC0C4CC),
            border = Color(0xFFDCDFE6),
            borderLight = Color(0xFFE4E7ED),
            borderLighter = Color(0xFFEBEEF5),
            borderExtraLight = Color(0xFFF2F6FC),
            borderDark = Color(0xFFD4D7DE),
            borderDarker = Color(0xFFCDD0D6),
            fill = Color(0xFFF0F2F5),
            fillLight = Color(0xFFF5F7FA),
            fillLighter = Color(0xFFFAFAFA),
            fillExtraLight = Color(0xFFFAFCFF),
            fillDark = Color(0xFFEBEDF0),
            fillDarker = Color(0xFFE6E8EB),
            fillBlank = Color(0xFFFFFFFF),
            background = Color(0xFFFFFFFF),
            backgroundPage = Color(0xFFF2F3F5),
            backgroundOverlay = Color(0xFFFFFFFF),
            overlay = Color(0xCC000000),
            mask = Color(0xE6FFFFFF),
            isDark = false,
        )

        val Dark = ElColors(
            white = Color(0xFFFFFFFF),
            black = Color(0xFF000000),
            primary = Color(0xFF409EFF),
            success = Color(0xFF67C23A),
            warning = Color(0xFFE6A23C),
            danger = Color(0xFFF56C6C),
            error = Color(0xFFF56C6C),
            info = Color(0xFF909399),
            textPrimary = Color(0xFFE5EAF3),
            textRegular = Color(0xFFCFD3DC),
            textSecondary = Color(0xFFA3A6AD),
            textPlaceholder = Color(0xFF8D9095),
            textDisabled = Color(0xFF6C6E72),
            border = Color(0xFF4C4D4F),
            borderLight = Color(0xFF414243),
            borderLighter = Color(0xFF363637),
            borderExtraLight = Color(0xFF2B2B2C),
            borderDark = Color(0xFF58585B),
            borderDarker = Color(0xFF636466),
            fill = Color(0xFF303030),
            fillLight = Color(0xFF262727),
            fillLighter = Color(0xFF1D1D1D),
            fillExtraLight = Color(0xFF191919),
            fillDark = Color(0xFF39393A),
            fillDarker = Color(0xFF424243),
            fillBlank = Color(0xFF141414),
            background = Color(0xFF141414),
            backgroundPage = Color(0xFF0A0A0A),
            backgroundOverlay = Color(0xFF1D1E1F),
            overlay = Color(0xCC000000),
            mask = Color(0xE6141414),
            isDark = true,
        )
    }
}

@Immutable
data class ElTypography(
    val fontFamily: FontFamily = FontFamily.SansSerif,
    val extraLarge: TextStyle = baseStyle(fontFamily, 20.sp),
    val large: TextStyle = baseStyle(fontFamily, 18.sp),
    val medium: TextStyle = baseStyle(fontFamily, 16.sp),
    val base: TextStyle = baseStyle(fontFamily, 14.sp),
    val small: TextStyle = baseStyle(fontFamily, 13.sp),
    val extraSmall: TextStyle = baseStyle(fontFamily, 12.sp),
) {
    companion object {
        internal fun baseStyle(family: FontFamily, size: androidx.compose.ui.unit.TextUnit) = TextStyle(
            fontFamily = family,
            fontSize = size,
            lineHeight = size * 1.5f,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Immutable
data class ElRadius(
    val base: Dp = 4.dp,
    val small: Dp = 2.dp,
    val round: Dp = 20.dp,
)

@Immutable
data class ElTheme(
    val colors: ElColors = ElColors.Light,
    val typography: ElTypography = ElTypography(),
    val radius: ElRadius = ElRadius(),
) {
    val isDark: Boolean get() = colors.isDark

    companion object {
        val Light = ElTheme()
        val Dark = ElTheme(colors = ElColors.Dark)
    }
}

val LocalElTheme = staticCompositionLocalOf { ElTheme.Light }

/** Current inheritance color for icons and inline content. */
val LocalElContentColor = staticCompositionLocalOf { Color.Unspecified }
