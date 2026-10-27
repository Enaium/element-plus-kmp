@file:Suppress("DEPRECATION")

package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.LocalElFormSize
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

enum class ElFormLabelPosition { Left, Right, Top }
enum class ElFormRuleType { String, Number, Integer, Boolean, Array, Email, Url, Float, Object, Date, Enum, Method, Regexp, Hex }
enum class ElFormTrigger { Change, Blur }
enum class ElFormRequireAsteriskPosition { Left, Right }

/** A single validation rule, mirroring Element Plus' form rules. */
@Immutable
data class ElFormRule(
    val required: Boolean = false,
    val message: String? = null,
    val min: Double? = null,
    val max: Double? = null,
    val len: Int? = null,
    val pattern: Regex? = null,
    val type: ElFormRuleType? = null,
    val trigger: List<ElFormTrigger> = listOf(ElFormTrigger.Change, ElFormTrigger.Blur),
    val validator: ((value: Any?) -> String?)? = null,
    val whitespace: Boolean = false,
    /** Allowed values when [type] is [ElFormRuleType.Enum]. */
    val enum: List<Any?> = emptyList(),
    /** Asynchronous validator, awaited by the `validateSuspend` entry points. */
    val asyncValidator: (suspend (value: Any?) -> String?)? = null,
)

@Immutable
data class ElFormValidationResult(
    val valid: Boolean,
    val errors: Map<String, String>,
)

@Stable
class ElFormItemState internal constructor() {
    var error by mutableStateOf<String?>(null)
        internal set
    internal var validated by mutableStateOf(false)
    internal var rules: List<ElFormRule> = emptyList()
    internal var value: Any? = null
    internal var label: String? = null
    internal var displayLabel: String? = null
    internal var onReset: (() -> Unit)? = null
    internal var locale: ElLocale = ElLocale.English
    internal var validateOnRuleChange: Boolean = true
    internal var bringIntoView: (() -> Unit)? = null

    val validateStatus: ElInputStatus?
        get() = if (error != null) ElInputStatus.Error else null

    /**
     * Validates the rules matching [trigger]. Passing `null` validates every
     * rule (the behaviour of `ElFormState.validate`).
     */
    fun validate(trigger: ElFormTrigger? = null): Boolean {
        if (trigger == null) {
            error = validateElRules(value, rules, locale, displayLabel ?: label)
            validated = true
        } else {
            val applicable = rulesFor(trigger)
            if (applicable.isEmpty()) return error == null
            error = validateElRules(value, applicable, locale, displayLabel ?: label)
            validated = true
        }
        return error == null
    }

    /** Runs the matching rules and then awaits their [ElFormRule.asyncValidator]s. */
    suspend fun validateSuspend(trigger: ElFormTrigger? = null): Boolean {
        if (!validate(trigger)) return false
        rulesFor(trigger).forEach { rule ->
            rule.asyncValidator?.invoke(value)?.let { message ->
                error = message
                return false
            }
        }
        return true
    }

    fun clearValidate() {
        error = null
        validated = false
    }

    internal fun reset() {
        error = null
        validated = false
        onReset?.invoke()
    }

    private fun rulesFor(trigger: ElFormTrigger?): List<ElFormRule> =
        if (trigger == null) rules else rules.filter { it.trigger.contains(trigger) }
}

@Stable
class ElFormState {
    internal val items = mutableStateListOf<ElFormItemState>()
    internal var scrollToError: Boolean = false

    fun validate(): Boolean {
        var valid = true
        items.forEach { if (!it.validate()) valid = false }
        if (!valid && scrollToError) scrollToFirstError()
        return valid
    }

    /** Like [validate], but also awaits asynchronous validators. */
    suspend fun validateSuspend(): Boolean {
        var valid = true
        items.forEach { if (!it.validateSuspend()) valid = false }
        if (!valid && scrollToError) scrollToFirstError()
        return valid
    }

    fun validateField(vararg props: String): Boolean {
        var valid = true
        items.filter { it.label in props }.forEach { if (!it.validate()) valid = false }
        if (!valid && scrollToError) scrollToFirstError()
        return valid
    }

    /** Like [validateField], but also awaits asynchronous validators. */
    suspend fun validateFieldSuspend(vararg props: String): Boolean {
        var valid = true
        items.filter { it.label in props }.forEach { if (!it.validateSuspend()) valid = false }
        if (!valid && scrollToError) scrollToFirstError()
        return valid
    }

    fun clearValidate() {
        items.forEach { it.clearValidate() }
    }

    fun resetFields() {
        items.forEach { it.reset() }
    }

    fun validateResult(): ElFormValidationResult {
        validate()
        val errors = items.mapNotNull { item -> item.label?.let { it to item.error } }
            .filter { it.second != null }
            .associate { it.first to it.second!! }
        return ElFormValidationResult(errors.isEmpty(), errors)
    }

    val isValid: Boolean get() = items.all { it.error == null }

    /** Scrolls the first invalid item into view when [scrollToError] is enabled. */
    fun scrollToFirstError() {
        items.firstOrNull { it.error != null }?.bringIntoView?.invoke()
    }
}

@Composable
fun rememberElFormState(): ElFormState = remember { ElFormState() }

@Immutable
internal data class ElFormSharedConfig(
    val labelPosition: ElFormLabelPosition,
    val labelWidth: Dp?,
    val labelSuffix: String,
    val inline: Boolean,
    val disabled: Boolean,
    val size: ElSize?,
    val showMessage: Boolean,
    val inlineMessage: Boolean = false,
    val statusIcon: Boolean = false,
    val hideRequiredAsterisk: Boolean = false,
    val requireAsteriskPosition: ElFormRequireAsteriskPosition = ElFormRequireAsteriskPosition.Left,
    val validateOnRuleChange: Boolean = true,
)

internal val LocalElFormState = staticCompositionLocalOf<ElFormState?> { null }
internal val LocalElFormSharedConfig = staticCompositionLocalOf {
    ElFormSharedConfig(
        labelPosition = ElFormLabelPosition.Right,
        labelWidth = null,
        labelSuffix = "",
        inline = false,
        disabled = false,
        size = null,
        showMessage = true,
    )
}

/** `<el-form>` with a composable validation state. */
@Composable
fun ElForm(
    modifier: Modifier = Modifier,
    state: ElFormState = rememberElFormState(),
    labelPosition: ElFormLabelPosition = ElFormLabelPosition.Right,
    labelWidth: Dp? = null,
    labelSuffix: String = "",
    inline: Boolean = false,
    disabled: Boolean = false,
    size: ElSize? = null,
    showMessage: Boolean = true,
    onSubmit: (() -> Unit)? = null,
    inlineMessage: Boolean = false,
    statusIcon: Boolean = false,
    hideRequiredAsterisk: Boolean = false,
    requireAsteriskPosition: ElFormRequireAsteriskPosition = ElFormRequireAsteriskPosition.Left,
    validateOnRuleChange: Boolean = true,
    scrollToError: Boolean = false,
    content: @Composable () -> Unit,
) {
    SideEffect { state.scrollToError = scrollToError }
    CompositionLocalProvider(
        LocalElFormState provides state,
        LocalElFormSharedConfig provides ElFormSharedConfig(
            labelPosition = labelPosition,
            labelWidth = labelWidth,
            labelSuffix = labelSuffix,
            inline = inline,
            disabled = disabled,
            size = size,
            showMessage = showMessage,
            inlineMessage = inlineMessage,
            statusIcon = statusIcon,
            hideRequiredAsterisk = hideRequiredAsterisk,
            requireAsteriskPosition = requireAsteriskPosition,
            validateOnRuleChange = validateOnRuleChange,
        ),
        LocalElFormSize provides size,
    ) {
        if (inline) {
            Row(modifier, horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) { content() }
        } else {
            Column(modifier, verticalArrangement = Arrangement.spacedBy(18.dp)) { content() }
        }
    }
}

/** Receiver passed to [ElFormItem] content so fields can read validation state. */
@Stable
class ElFormItemScope internal constructor(
    private val itemState: ElFormItemState,
    val size: ElSize,
) {
    val error: String? get() = itemState.error
    val validateStatus: ElInputStatus? get() = itemState.validateStatus
    fun validate(): Boolean = itemState.validate()

    /** Validates only the rules bound to [trigger]. */
    fun validate(trigger: ElFormTrigger): Boolean = itemState.validate(trigger)

    /** Validates the `blur` triggered rules; call it from a field's focus loss. */
    fun blur() = itemState.validate(ElFormTrigger.Blur)

    suspend fun validateSuspend(): Boolean = itemState.validateSuspend()

    fun clearValidate() = itemState.clearValidate()
}

/**
 * `<el-form-item>`. The value is validated automatically when it changes after
 * the first interaction, and on [ElFormState.validate]. Rules whose
 * [ElFormRule.trigger] excludes [ElFormTrigger.Change] only run through
 * [ElFormItemScope.blur] or on demand.
 */
@Composable
fun ElFormItem(
    label: String? = null,
    value: Any? = null,
    modifier: Modifier = Modifier,
    prop: String? = null,
    rules: List<ElFormRule> = emptyList(),
    required: Boolean = false,
    error: String? = null,
    showMessage: Boolean? = null,
    inlineMessage: Boolean = false,
    labelWidth: Dp? = null,
    labelPosition: ElFormLabelPosition? = null,
    labelSuffix: String? = null,
    size: ElSize? = null,
    onReset: (() -> Unit)? = null,
    labelContent: (@Composable () -> Unit)? = null,
    errorContent: (@Composable (message: String) -> Unit)? = null,
    validateOnRuleChange: Boolean? = null,
    content: @Composable ElFormItemScope.() -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val formState = LocalElFormState.current
    val shared = LocalElFormSharedConfig.current
    val resolvedSize = resolveElSize(size ?: shared.size)
    val effectiveLabelPosition = labelPosition ?: shared.labelPosition
    val locale = LocalElConfig.current.locale
    val effectiveRules = remember(rules, required) {
        if (required && rules.none { it.required }) rules + ElFormRule(required = true) else rules
    }
    val itemState = remember { ElFormItemState() }
    itemState.rules = effectiveRules
    itemState.value = value
    itemState.label = prop ?: label
    itemState.displayLabel = label
    itemState.onReset = onReset
    itemState.locale = locale
    itemState.validateOnRuleChange = validateOnRuleChange ?: shared.validateOnRuleChange
    LaunchedEffect(error) {
        if (error != null) itemState.error = error else itemState.clearValidate()
    }
    val scope = remember(itemState) { ElFormItemScope(itemState, resolvedSize) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    itemState.bringIntoView = {
        coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
    }
    val itemModifier = modifier.bringIntoViewRequester(bringIntoViewRequester)

    DisposableEffect(formState, itemState) {
        formState?.items?.add(itemState)
        onDispose { formState?.items?.remove(itemState) }
    }
    // Validate changes after the initial composition, matching `validate-event`.
    var previous by remember { mutableStateOf(value) }
    var touched by remember { mutableStateOf(false) }
    LaunchedEffect(value) {
        if (touched && previous != value && effectiveRules.isNotEmpty()) {
            itemState.validate(ElFormTrigger.Change)
        }
        previous = value
    }
    // Mark as touched as soon as any validation rule exists and a field is shown.
    LaunchedEffect(Unit) { touched = true }
    var rulesInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(effectiveRules) {
        if (rulesInitialized && itemState.validateOnRuleChange && effectiveRules.isNotEmpty()) {
            itemState.validate()
        }
        rulesInitialized = true
    }

    val show = showMessage ?: shared.showMessage
    val showInlineMessage = inlineMessage || shared.inlineMessage
    val labelW = labelWidth ?: shared.labelWidth ?: 80.dp
    val requiredMark = effectiveRules.any { it.required } && !shared.hideRequiredAsterisk
    val labelBlock: @Composable () -> Unit = {
        if (labelContent != null) {
            labelContent()
        } else if (label != null) {
            Row(verticalAlignment = Alignment.Top) {
                if (requiredMark && shared.requireAsteriskPosition == ElFormRequireAsteriskPosition.Left) {
                    BasicText(
                        text = "*",
                        style = theme.typography.base.copy(color = colors.danger),
                    )
                }
                BasicText(
                    text = label + (labelSuffix ?: shared.labelSuffix),
                    style = theme.typography.base.copy(color = colors.textRegular),
                )
                if (requiredMark && shared.requireAsteriskPosition == ElFormRequireAsteriskPosition.Right) {
                    BasicText(
                        text = "*",
                        style = theme.typography.base.copy(color = colors.danger),
                    )
                }
            }
        }
    }
    val errorBlock: @Composable (Boolean) -> Unit = { withPadding ->
        if (show && itemState.error != null) {
            if (errorContent != null) {
                errorContent(itemState.error ?: "")
            } else {
                BasicText(
                    text = itemState.error ?: "",
                    modifier = if (withPadding) Modifier.padding(top = 4.dp) else Modifier,
                    style = theme.typography.extraSmall.copy(color = colors.danger, fontSize = 12.sp),
                )
            }
        }
    }
    val statusIconBlock: @Composable () -> Unit = {
        if (shared.statusIcon && itemState.validated && itemState.rules.isNotEmpty()) {
            val invalid = itemState.error != null
            ElIcon(
                if (invalid) ElIcons.CircleClose else ElIcons.CircleCheck,
                size = 14.dp,
                tint = if (invalid) colors.danger else colors.success,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
    val fieldContent: @Composable () -> Unit = {
        Column {
            if (showInlineMessage) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(Modifier.weight(1f, fill = false)) {
                        CompositionLocalProvider(LocalElFormSize provides resolvedSize) {
                            scope.content()
                        }
                    }
                    statusIconBlock()
                    if (show && itemState.error != null) {
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.padding(top = 6.dp)) { errorBlock(false) }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.Top) {
                    Box(Modifier.weight(1f)) {
                        CompositionLocalProvider(LocalElFormSize provides resolvedSize) {
                            scope.content()
                        }
                    }
                    statusIconBlock()
                }
                errorBlock(true)
            }
        }
    }

    when (effectiveLabelPosition) {
        ElFormLabelPosition.Top -> {
            Column(itemModifier.fillMaxWidth()) {
                if (label != null || labelContent != null) {
                    labelBlock()
                    Spacer(Modifier.height(6.dp))
                }
                fieldContent()
            }
        }
        ElFormLabelPosition.Left, ElFormLabelPosition.Right -> {
            if (shared.inline) {
                Row(itemModifier, verticalAlignment = Alignment.CenterVertically) {
                    if (label != null || labelContent != null) {
                        Box(
                            Modifier.width(labelW),
                            contentAlignment = if (effectiveLabelPosition == ElFormLabelPosition.Right) Alignment.CenterEnd else Alignment.CenterStart,
                        ) { labelBlock() }
                        Spacer(Modifier.width(12.dp))
                    }
                    fieldContent()
                }
            } else {
                Row(itemModifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    if (label != null || labelContent != null) {
                        Box(
                            Modifier.width(labelW).padding(top = 5.dp),
                            contentAlignment = if (effectiveLabelPosition == ElFormLabelPosition.Right) Alignment.TopEnd else Alignment.TopStart,
                        ) { labelBlock() }
                        Spacer(Modifier.width(12.dp))
                    }
                    Box(Modifier.weight(1f)) { fieldContent() }
                }
            }
        }
    }
}

/** Shared rule engine; also exposed for custom validation. */
fun validateElRules(
    value: Any?,
    rules: List<ElFormRule>,
    locale: ElLocale = ElLocale.English,
    label: String? = null,
): String? {
    rules.forEach { rule ->
        rule.validator?.invoke(value)?.let { return it }
        if (rule.required) {
            val empty = when (value) {
                null -> true
                is String -> if (rule.whitespace) value.isBlank() else value.isEmpty()
                is Collection<*> -> value.isEmpty()
                is Array<*> -> value.isEmpty()
                else -> false
            }
            if (empty) {
                return rule.message ?: if (label != null) {
                    locale.form.requiredWithLabel.replace("{label}", label)
                } else {
                    locale.form.required
                }
            }
        }
        when (rule.type) {
            ElFormRuleType.Email -> if (value is String && value.isNotEmpty() &&
                !Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$").matches(value)
            ) return rule.message ?: locale.form.email
            ElFormRuleType.Url -> if (value is String && value.isNotEmpty() &&
                !Regex("^https?://[\\w.-]+(\\.[\\w-]+)+(/\\S*)?$").matches(value)
            ) return rule.message ?: locale.form.url
            ElFormRuleType.Number -> if (value != null && value !is Number) return rule.message ?: locale.form.number
            ElFormRuleType.Integer -> when (value) {
                is Number -> if (value.toDouble() % 1.0 != 0.0) return rule.message ?: locale.form.integer
                is String -> if (value.isNotEmpty() && value.toLongOrNull() == null) return rule.message ?: locale.form.integer
                else -> Unit
            }
            ElFormRuleType.Float -> when (value) {
                is Number -> Unit
                is String -> if (value.isNotEmpty() && value.toDoubleOrNull() == null) return rule.message ?: locale.form.float
                else -> Unit
            }
            ElFormRuleType.Boolean -> when (value) {
                is Boolean -> Unit
                is String -> if (value.isNotEmpty() && value != "true" && value != "false") return rule.message ?: locale.form.boolean
                null -> Unit
                else -> return rule.message ?: locale.form.boolean
            }
            ElFormRuleType.Array -> if (value != null && value !is Collection<*> && value !is Array<*>) return rule.message ?: locale.form.array
            ElFormRuleType.Object -> if (value != null && (value is String || value is Number || value is Boolean ||
                value is Char || value is Collection<*> || value is Array<*>)
            ) return rule.message ?: locale.form.objectType
            ElFormRuleType.Date -> if (value != null && value !is kotlinx.datetime.LocalDate &&
                value !is kotlinx.datetime.LocalDateTime && value !is kotlinx.datetime.Instant &&
                !(value is String && runCatching { kotlinx.datetime.LocalDate.parse(value) }.isSuccess)
            ) return rule.message ?: locale.form.date
            ElFormRuleType.Enum -> if (rule.enum.isNotEmpty() && rule.enum.none { it == value || it?.toString() == value?.toString() }) {
                return rule.message ?: locale.form.enum
            }
            ElFormRuleType.Method -> if (value != null && value !is Function<*>) return rule.message ?: locale.form.method
            ElFormRuleType.Regexp -> if (value != null && value !is Regex) return rule.message ?: locale.form.regexp
            ElFormRuleType.Hex -> if (value is String && value.isNotEmpty() &&
                !Regex("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$").matches(value)
            ) return rule.message ?: locale.form.hex
            ElFormRuleType.String -> if (value != null && value !is String) return rule.message ?: locale.form.string
            null -> Unit
        }
        rule.pattern?.let { pattern ->
            if (value is String && value.isNotEmpty() && !pattern.matches(value)) {
                return rule.message ?: locale.form.pattern
            }
        }
        rule.len?.let { len ->
            val size = when (value) {
                is String -> value.length
                is Collection<*> -> value.size
                is Array<*> -> value.size
                else -> null
            }
            if (size != null && size != len) return rule.message ?: locale.form.length.replace("{len}", len.toString())
        }
        rule.min?.let { min ->
            val size = when (value) {
                is String -> value.length.toDouble()
                is Collection<*> -> value.size.toDouble()
                is Array<*> -> value.size.toDouble()
                is Number -> value.toDouble()
                else -> null
            }
            if (size != null && size < min) return rule.message ?: locale.form.min.replace("{min}", min.toString())
        }
        rule.max?.let { max ->
            val size = when (value) {
                is String -> value.length.toDouble()
                is Collection<*> -> value.size.toDouble()
                is Array<*> -> value.size.toDouble()
                is Number -> value.toDouble()
                else -> null
            }
            if (size != null && size > max) return rule.message ?: locale.form.max.replace("{max}", max.toString())
        }
    }
    return null
}
