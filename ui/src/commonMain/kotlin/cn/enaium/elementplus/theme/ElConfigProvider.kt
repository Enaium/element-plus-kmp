package cn.enaium.elementplus.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import cn.enaium.elementplus.components.ElCardShadow
import cn.enaium.elementplus.components.ElLinkUnderline
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessagePlacement

/**
 * Defaults applied to every `<el-button>` that does not set the matching
 * parameter itself. An explicitly passed parameter always wins.
 */
@Immutable
data class ElButtonConfig(
    val autoInsertSpace: Boolean = false,
    val plain: Boolean = false,
    val round: Boolean = false,
    val text: Boolean = false,
    val dashed: Boolean = false,
    val type: ElType? = null,
)

/** Card defaults installed by [ElConfigProvider]. */
@Immutable
data class ElCardConfig(
    val shadow: ElCardShadow? = null,
)

/**
 * Dialog defaults installed by [ElConfigProvider]. Element Plus also exposes
 * `draggable`, `overflow` and `transition`; Compose dialogs are not draggable,
 * so only the alignment is configurable here.
 */
@Immutable
data class ElDialogConfig(
    val alignCenter: Boolean? = null,
)

/** Link defaults installed by [ElConfigProvider]. */
@Immutable
data class ElLinkConfig(
    val type: ElType? = null,
    val underline: ElLinkUnderline? = null,
)

/** Message defaults installed by [ElConfigProvider]. */
@Immutable
data class ElMessageConfig(
    val max: Int? = null,
    val grouping: Boolean? = null,
    val plain: Boolean? = null,
    val placement: ElMessagePlacement? = null,
)

/** Table defaults installed by [ElConfigProvider]. */
@Immutable
data class ElTableConfig(
    val showOverflowTooltip: Boolean? = null,
)

/**
 * The resolved global configuration of a subtree. Values come from the nearest
 * [ElConfigProvider], which merges unspecified entries from its parent
 * provider, mirroring the original `mergeConfig` behaviour.
 */
@Immutable
data class ElConfig(
    val locale: ElLocale = ElLocale.English,
    val size: ElSize? = null,
    val zIndex: Int = 2000,
    val button: ElButtonConfig = ElButtonConfig(),
    val card: ElCardConfig = ElCardConfig(),
    val dialog: ElDialogConfig = ElDialogConfig(),
    val link: ElLinkConfig = ElLinkConfig(),
    val message: ElMessageConfig = ElMessageConfig(),
    val table: ElTableConfig = ElTableConfig(),
    val emptyValues: List<Any?> = emptyList(),
    val valueOnClear: Any? = null,
    val a11y: Boolean = true,
    val keyboardNavigation: Boolean = true,
    val namespace: String = "el",
)

val LocalElConfig = staticCompositionLocalOf { ElConfig() }

/**
 * Equivalent of `<el-config-provider>`: installs the theme, locale, default
 * component size and the per-component defaults for all descendants.
 *
 * Every parameter is optional and inherits the value of the enclosing
 * provider, so a nested provider only overrides the pieces it passes. This
 * matches the original `mergeConfig` semantics instead of resetting the
 * surrounding configuration.
 *
 * [emptyValues], [valueOnClear], [a11y], [keyboardNavigation] and [namespace]
 * are global defaults consumed by the components that support them; omitting
 * one inherits the parent value, like the other parameters.
 */
@Composable
fun ElConfigProvider(
    theme: ElTheme? = null,
    locale: ElLocale? = null,
    size: ElSize? = null,
    zIndex: Int? = null,
    button: ElButtonConfig? = null,
    card: ElCardConfig? = null,
    dialog: ElDialogConfig? = null,
    link: ElLinkConfig? = null,
    message: ElMessageConfig? = null,
    table: ElTableConfig? = null,
    emptyValues: List<Any?>? = null,
    valueOnClear: Any? = null,
    a11y: Boolean? = null,
    keyboardNavigation: Boolean? = null,
    namespace: String? = null,
    content: @Composable () -> Unit,
) {
    val parentConfig = LocalElConfig.current
    val parentTheme = LocalElTheme.current
    val resolvedTheme = theme ?: parentTheme
    val resolvedLocale = locale ?: parentConfig.locale
    val resolvedConfig = ElConfig(
        locale = resolvedLocale,
        size = size ?: parentConfig.size,
        zIndex = zIndex ?: parentConfig.zIndex,
        button = button?.mergeInto(parentConfig.button) ?: parentConfig.button,
        card = card?.mergeInto(parentConfig.card) ?: parentConfig.card,
        dialog = dialog?.mergeInto(parentConfig.dialog) ?: parentConfig.dialog,
        link = link?.mergeInto(parentConfig.link) ?: parentConfig.link,
        message = message?.mergeInto(parentConfig.message) ?: parentConfig.message,
        table = table?.mergeInto(parentConfig.table) ?: parentConfig.table,
        emptyValues = emptyValues ?: parentConfig.emptyValues,
        valueOnClear = valueOnClear ?: parentConfig.valueOnClear,
        a11y = a11y ?: parentConfig.a11y,
        keyboardNavigation = keyboardNavigation ?: parentConfig.keyboardNavigation,
        namespace = namespace ?: parentConfig.namespace,
    )
    DisposableEffect(resolvedConfig.message) {
        val previous = ElMessage.service.config
        ElMessage.service.config = resolvedConfig.message
        onDispose { ElMessage.service.config = previous }
    }
    CompositionLocalProvider(
        LocalElTheme provides resolvedTheme,
        LocalElConfig provides resolvedConfig,
        LocalLayoutDirection provides if (resolvedLocale.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
        content()
    }
}

@Composable
fun elLocale(): ElLocale = LocalElConfig.current.locale

@Composable
fun elTheme(): ElTheme = LocalElTheme.current

private fun ElButtonConfig.mergeInto(parent: ElButtonConfig): ElButtonConfig = ElButtonConfig(
    autoInsertSpace = autoInsertSpace || parent.autoInsertSpace,
    plain = plain || parent.plain,
    round = round || parent.round,
    text = text || parent.text,
    dashed = dashed || parent.dashed,
    type = type ?: parent.type,
)

private fun ElCardConfig.mergeInto(parent: ElCardConfig): ElCardConfig = ElCardConfig(
    shadow = shadow ?: parent.shadow,
)

private fun ElDialogConfig.mergeInto(parent: ElDialogConfig): ElDialogConfig = ElDialogConfig(
    alignCenter = alignCenter ?: parent.alignCenter,
)

private fun ElLinkConfig.mergeInto(parent: ElLinkConfig): ElLinkConfig = ElLinkConfig(
    type = type ?: parent.type,
    underline = underline ?: parent.underline,
)

private fun ElMessageConfig.mergeInto(parent: ElMessageConfig): ElMessageConfig = ElMessageConfig(
    max = max ?: parent.max,
    grouping = grouping ?: parent.grouping,
    plain = plain ?: parent.plain,
    placement = placement ?: parent.placement,
)

private fun ElTableConfig.mergeInto(parent: ElTableConfig): ElTableConfig = ElTableConfig(
    showOverflowTooltip = showOverflowTooltip ?: parent.showOverflowTooltip,
)
