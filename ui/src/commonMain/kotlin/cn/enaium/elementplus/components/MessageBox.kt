package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class ElMessageBoxType { Info, Success, Warning, Error }
enum class ElMessageBoxKind { Alert, Confirm, Prompt }

/** The action that triggered a message box dismissal, mirroring Element Plus' `Action` type. */
enum class ElMessageBoxAction { Confirm, Cancel, Close }

data class ElMessageBoxItem(
    val id: Int,
    val kind: ElMessageBoxKind,
    val title: String?,
    val message: String?,
    val type: ElMessageBoxType,
    val confirmText: String?,
    val cancelText: String?,
    val showClose: Boolean,
    val inputPlaceholder: String?,
    val initialInput: String,
    val onConfirm: (String?) -> Unit,
    val onCancel: () -> Unit,
    val onClose: () -> Unit,
    val showConfirm: Boolean = true,
    val showCancel: Boolean = true,
    val messageContent: (@Composable () -> Unit)? = null,
    val inputType: ElInputType = ElInputType.Text,
    val inputPattern: Regex? = null,
    val inputValidator: ((String) -> Boolean)? = null,
    val inputErrorMessage: String? = null,
    val icon: ImageVector? = null,
    val closeIcon: ImageVector? = null,
    val center: Boolean = false,
    val roundButton: Boolean = false,
    val closeOnClickModal: Boolean = true,
    val draggable: Boolean = false,
    val beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
)

@Stable
class ElMessageBoxService {
    internal val items = mutableStateListOf<ElMessageBoxItem>()
    private var nextId = 0

    fun alert(
        message: String,
        title: String? = null,
        type: ElMessageBoxType = ElMessageBoxType.Warning,
        confirmText: String? = null,
        showClose: Boolean = true,
        onConfirm: () -> Unit = {},
        onClose: () -> Unit = {},
        messageContent: (@Composable () -> Unit)? = null,
        icon: ImageVector? = null,
        closeIcon: ImageVector? = null,
        center: Boolean = false,
        roundButton: Boolean = false,
        closeOnClickModal: Boolean = true,
        draggable: Boolean = false,
        beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
    ): Int = add(
        ElMessageBoxItem(
            id = nextId++, kind = ElMessageBoxKind.Alert, title = title, message = message, type = type,
            confirmText = confirmText, cancelText = null, showClose = showClose,
            inputPlaceholder = null, initialInput = "",
            onConfirm = { onConfirm() }, onCancel = {}, onClose = onClose,
            messageContent = messageContent,
            icon = icon, closeIcon = closeIcon, center = center, roundButton = roundButton,
            closeOnClickModal = closeOnClickModal, draggable = draggable, beforeClose = beforeClose,
        )
    )

    fun confirm(
        message: String,
        title: String? = null,
        type: ElMessageBoxType = ElMessageBoxType.Warning,
        confirmText: String? = null,
        cancelText: String? = null,
        showClose: Boolean = true,
        onConfirm: () -> Unit = {},
        onCancel: () -> Unit = {},
        onClose: () -> Unit = {},
        messageContent: (@Composable () -> Unit)? = null,
        showConfirm: Boolean = true,
        icon: ImageVector? = null,
        closeIcon: ImageVector? = null,
        center: Boolean = false,
        roundButton: Boolean = false,
        closeOnClickModal: Boolean = true,
        draggable: Boolean = false,
        beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
        showCancel: Boolean = true,
    ): Int = add(
        ElMessageBoxItem(
            id = nextId++, kind = ElMessageBoxKind.Confirm, title = title, message = message, type = type,
            confirmText = confirmText, cancelText = cancelText, showClose = showClose,
            inputPlaceholder = null, initialInput = "",
            onConfirm = { onConfirm() }, onCancel = onCancel, onClose = onClose,
            showConfirm = showConfirm,
            showCancel = showCancel,
            messageContent = messageContent,
            icon = icon, closeIcon = closeIcon, center = center, roundButton = roundButton,
            closeOnClickModal = closeOnClickModal, draggable = draggable, beforeClose = beforeClose,
        )
    )

    fun prompt(
        message: String,
        title: String? = null,
        type: ElMessageBoxType = ElMessageBoxType.Info,
        confirmText: String? = null,
        cancelText: String? = null,
        inputPlaceholder: String? = null,
        initialInput: String = "",
        showClose: Boolean = true,
        onConfirm: (String) -> Unit = {},
        onCancel: () -> Unit = {},
        onClose: () -> Unit = {},
        messageContent: (@Composable () -> Unit)? = null,
        inputType: ElInputType = ElInputType.Text,
        inputPattern: Regex? = null,
        inputValidator: ((String) -> Boolean)? = null,
        inputErrorMessage: String? = null,
        icon: ImageVector? = null,
        closeIcon: ImageVector? = null,
        center: Boolean = false,
        roundButton: Boolean = false,
        closeOnClickModal: Boolean = true,
        draggable: Boolean = false,
        beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
    ): Int = add(
        ElMessageBoxItem(
            id = nextId++, kind = ElMessageBoxKind.Prompt, title = title, message = message, type = type,
            confirmText = confirmText, cancelText = cancelText, showClose = showClose,
            inputPlaceholder = inputPlaceholder, initialInput = initialInput,
            onConfirm = { onConfirm(it ?: "") }, onCancel = onCancel, onClose = onClose,
            messageContent = messageContent,
            inputType = inputType,
            inputPattern = inputPattern,
            inputValidator = inputValidator,
            inputErrorMessage = inputErrorMessage,
            icon = icon, closeIcon = closeIcon, center = center, roundButton = roundButton,
            closeOnClickModal = closeOnClickModal, draggable = draggable, beforeClose = beforeClose,
        )
    )

    private fun add(item: ElMessageBoxItem): Int {
        items.add(item)
        return item.id
    }

    fun close(id: Int) {
        items.removeAll { it.id == id }
    }

    fun closeAll() = items.clear()
}

@Composable
fun rememberElMessageBoxService(): ElMessageBoxService = remember { ElMessageBoxService() }

object ElMessageBox {
    val service = ElMessageBoxService()
    fun alert(
        message: String,
        title: String? = null,
        type: ElMessageBoxType = ElMessageBoxType.Warning,
        confirmText: String? = null,
        showClose: Boolean = true,
        onConfirm: () -> Unit = {},
        onClose: () -> Unit = {},
        messageContent: (@Composable () -> Unit)? = null,
        icon: ImageVector? = null,
        closeIcon: ImageVector? = null,
        center: Boolean = false,
        roundButton: Boolean = false,
        closeOnClickModal: Boolean = true,
        draggable: Boolean = false,
        beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
    ) = service.alert(
        message, title, type, confirmText, showClose, onConfirm, onClose,
        messageContent, icon, closeIcon, center, roundButton, closeOnClickModal, draggable, beforeClose,
    )

    fun confirm(
        message: String,
        title: String? = null,
        type: ElMessageBoxType = ElMessageBoxType.Warning,
        confirmText: String? = null,
        cancelText: String? = null,
        showClose: Boolean = true,
        onConfirm: () -> Unit = {},
        onCancel: () -> Unit = {},
        onClose: () -> Unit = {},
        messageContent: (@Composable () -> Unit)? = null,
        showConfirm: Boolean = true,
        icon: ImageVector? = null,
        closeIcon: ImageVector? = null,
        center: Boolean = false,
        roundButton: Boolean = false,
        closeOnClickModal: Boolean = true,
        draggable: Boolean = false,
        beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
        showCancel: Boolean = true,
    ) = service.confirm(
        message, title, type, confirmText, cancelText, showClose, onConfirm, onCancel, onClose,
        messageContent, showConfirm, icon, closeIcon, center, roundButton, closeOnClickModal, draggable,
        beforeClose, showCancel,
    )

    fun prompt(
        message: String,
        title: String? = null,
        type: ElMessageBoxType = ElMessageBoxType.Info,
        confirmText: String? = null,
        cancelText: String? = null,
        inputPlaceholder: String? = null,
        initialInput: String = "",
        showClose: Boolean = true,
        onConfirm: (String) -> Unit = {},
        onCancel: () -> Unit = {},
        onClose: () -> Unit = {},
        messageContent: (@Composable () -> Unit)? = null,
        inputType: ElInputType = ElInputType.Text,
        inputPattern: Regex? = null,
        inputValidator: ((String) -> Boolean)? = null,
        inputErrorMessage: String? = null,
        icon: ImageVector? = null,
        closeIcon: ImageVector? = null,
        center: Boolean = false,
        roundButton: Boolean = false,
        closeOnClickModal: Boolean = true,
        draggable: Boolean = false,
        beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
    ) = service.prompt(
        message, title, type, confirmText, cancelText, inputPlaceholder, initialInput, showClose,
        onConfirm, onCancel, onClose, messageContent, inputType, inputPattern, inputValidator,
        inputErrorMessage, icon, closeIcon, center, roundButton, closeOnClickModal, draggable,
        beforeClose,
    )
    fun close(id: Int) = service.close(id)

    @Composable
    fun Host(content: @Composable () -> Unit) = ElMessageBoxHost(service, content = content)
}

@Composable
fun ElMessageBoxHost(
    service: ElMessageBoxService,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        content()
        service.items.forEach { item ->
            var input by remember(item.id) { mutableStateOf(item.initialInput) }
            ElMessageBox(
                visible = true,
                onClose = {
                    service.close(item.id)
                    item.onClose()
                },
                title = item.title,
                message = item.message,
                messageContent = item.messageContent,
                type = item.type,
                confirmText = item.confirmText,
                cancelText = item.cancelText,
                showClose = item.showClose,
                showCancel = item.kind != ElMessageBoxKind.Alert && item.showCancel,
                showConfirm = item.showConfirm,
                input = item.kind == ElMessageBoxKind.Prompt,
                inputValue = input,
                onInputChange = { input = it },
                inputPlaceholder = item.inputPlaceholder,
                inputType = item.inputType,
                inputPattern = item.inputPattern,
                inputValidator = item.inputValidator,
                inputErrorMessage = item.inputErrorMessage,
                icon = item.icon,
                closeIcon = item.closeIcon,
                center = item.center,
                roundButton = item.roundButton,
                closeOnClickModal = item.closeOnClickModal,
                draggable = item.draggable,
                beforeClose = item.beforeClose,
                onConfirm = {
                    service.close(item.id)
                    item.onConfirm(if (item.kind == ElMessageBoxKind.Prompt) input else null)
                },
                onCancel = {
                    service.close(item.id)
                    item.onCancel()
                },
            )
        }
    }
}

/**
 * `<el-message-box>` as a standalone controlled component.
 *
 * [beforeClose] mirrors Element Plus' `beforeClose` hook: returning `false`
 * keeps the box open, and while it is suspended the corresponding action
 * button shows a loading state. [messageContent] is the Compose equivalent of
 * Element Plus' VNode/HTML message.
 */
@Composable
fun ElMessageBox(
    visible: Boolean,
    onClose: () -> Unit,
    title: String? = null,
    message: String? = null,
    type: ElMessageBoxType = ElMessageBoxType.Warning,
    confirmText: String? = null,
    cancelText: String? = null,
    showClose: Boolean = true,
    showCancel: Boolean = true,
    input: Boolean = false,
    inputValue: String = "",
    onInputChange: ((String) -> Unit)? = null,
    inputPlaceholder: String? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    showConfirm: Boolean = true,
    messageContent: (@Composable () -> Unit)? = null,
    inputType: ElInputType = ElInputType.Text,
    inputPattern: Regex? = null,
    inputValidator: ((String) -> Boolean)? = null,
    inputErrorMessage: String? = null,
    icon: ImageVector? = null,
    closeIcon: ImageVector? = null,
    center: Boolean = false,
    roundButton: Boolean = false,
    closeOnClickModal: Boolean = true,
    draggable: Boolean = false,
    beforeClose: (suspend (ElMessageBoxAction) -> Boolean)? = null,
    confirmButtonLoading: Boolean = false,
    cancelButtonLoading: Boolean = false,
) {
    if (!visible) return
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedTitle = title ?: locale.messageBox.title
    var localInput by remember { mutableStateOf(inputValue) }
    val currentInput = if (onInputChange != null) inputValue else localInput
    val scope = rememberCoroutineScope()
    var busyAction by remember { mutableStateOf<ElMessageBoxAction?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val (defaultIcon, color) = when (type) {
        ElMessageBoxType.Info -> ElIcons.InfoFilled to colors.info
        ElMessageBoxType.Success -> ElIcons.CircleCheckFilled to colors.success
        ElMessageBoxType.Warning -> ElIcons.WarningFilled to colors.warning
        ElMessageBoxType.Error -> ElIcons.CircleCloseFilled to colors.danger
    }
    val resolvedIcon = icon ?: defaultIcon
    val titleAlignment = if (center) TextAlign.Center else TextAlign.Start
    val beforeCloseGate = beforeClose

    fun finish(action: ElMessageBoxAction) {
        when (action) {
            ElMessageBoxAction.Confirm -> onConfirm?.invoke()
            ElMessageBoxAction.Cancel -> onCancel?.invoke()
            ElMessageBoxAction.Close -> onClose()
        }
    }

    fun runAction(action: ElMessageBoxAction) {
        if (busyAction != null) return
        val gate = beforeCloseGate
        if (gate == null) {
            finish(action)
            return
        }
        busyAction = action
        scope.launch {
            val proceed = gate(action)
            busyAction = null
            if (proceed) finish(action)
        }
    }

    fun requestClose() = runAction(ElMessageBoxAction.Close)

    fun trigger(action: ElMessageBoxAction) {
        if (action == ElMessageBoxAction.Confirm && input) {
            val value = currentInput
            val matchesPattern = inputPattern == null || inputPattern.matches(value)
            val passesValidator = inputValidator == null || inputValidator(value)
            if (!matchesPattern || !passesValidator) {
                validationError = inputErrorMessage ?: locale.messageBox.error
                return
            }
        }
        validationError = null
        runAction(action)
    }

    Dialog(
        onDismissRequest = { requestClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { if (closeOnClickModal) requestClose() },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                    .width(420.dp)
                    .background(colors.background, RoundedCornerShape(4.dp))
                    .border(1.dp, colors.borderLighter, RoundedCornerShape(4.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { }
                    .padding(20.dp),
                horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start,
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .then(
                            if (draggable) {
                                Modifier.pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount
                                    }
                                }
                            } else {
                                Modifier
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElIcon(resolvedIcon, size = 22.dp, tint = color)
                    Spacer(Modifier.width(10.dp))
                    BasicText(
                        text = resolvedTitle,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.medium.copy(
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            textAlign = titleAlignment,
                        ),
                    )
                    if (showClose) {
                        ElIcon(
                            closeIcon ?: ElIcons.Close,
                            size = 16.dp,
                            tint = colors.textPlaceholder,
                            contentDescription = locale.messageBox.close,
                            modifier = Modifier.clickable(onClick = { requestClose() }),
                        )
                    }
                }
                val contentBody = messageContent
                if (contentBody != null) {
                    Box(Modifier.padding(top = 12.dp)) { contentBody() }
                } else if (message != null) {
                    BasicText(
                        text = message,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        style = theme.typography.base.copy(
                            color = colors.textRegular,
                            textAlign = if (center) TextAlign.Center else TextAlign.Start,
                        ),
                    )
                }
                if (input) {
                    Spacer(Modifier.height(10.dp))
                    ElInput(
                        value = currentInput,
                        onValueChange = {
                            validationError = null
                            if (onInputChange != null) onInputChange(it) else localInput = it
                        },
                        placeholder = inputPlaceholder,
                        size = ElSize.Default,
                        type = inputType,
                        status = if (validationError != null) ElInputStatus.Error else null,
                    )
                    val error = validationError
                    if (error != null) {
                        BasicText(
                            text = error,
                            modifier = Modifier.padding(top = 4.dp),
                            style = theme.typography.extraSmall.copy(color = colors.danger),
                        )
                    }
                }
                Row(
                    Modifier.fillMaxWidth().padding(top = 18.dp),
                    horizontalArrangement = if (center) Arrangement.Center else Arrangement.End,
                ) {
                    if (showCancel) {
                        ElButton(
                            onClick = { trigger(ElMessageBoxAction.Cancel) },
                            text = cancelText ?: locale.messageBox.cancel,
                            type = ElType.Default,
                            size = ElSize.Default,
                            round = roundButton,
                            loading = cancelButtonLoading || busyAction == ElMessageBoxAction.Cancel,
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    if (showConfirm) {
                        ElButton(
                            onClick = { trigger(ElMessageBoxAction.Confirm) },
                            text = confirmText ?: locale.messageBox.confirm,
                            type = ElType.Primary,
                            size = ElSize.Default,
                            round = roundButton,
                            loading = confirmButtonLoading || busyAction == ElMessageBoxAction.Confirm,
                        )
                    }
                }
            }
        }
    }
}
