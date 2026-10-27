package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElMessageBoxType { Info, Success, Warning, Error }
enum class ElMessageBoxKind { Alert, Confirm, Prompt }

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
    ): Int = add(
        ElMessageBoxItem(
            id = nextId++, kind = ElMessageBoxKind.Alert, title = title, message = message, type = type,
            confirmText = confirmText, cancelText = null, showClose = showClose,
            inputPlaceholder = null, initialInput = "",
            onConfirm = { onConfirm() }, onCancel = {}, onClose = onClose,
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
    ): Int = add(
        ElMessageBoxItem(
            id = nextId++, kind = ElMessageBoxKind.Confirm, title = title, message = message, type = type,
            confirmText = confirmText, cancelText = cancelText, showClose = showClose,
            inputPlaceholder = null, initialInput = "",
            onConfirm = { onConfirm() }, onCancel = onCancel, onClose = onClose,
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
    ): Int = add(
        ElMessageBoxItem(
            id = nextId++, kind = ElMessageBoxKind.Prompt, title = title, message = message, type = type,
            confirmText = confirmText, cancelText = cancelText, showClose = showClose,
            inputPlaceholder = inputPlaceholder, initialInput = initialInput,
            onConfirm = { onConfirm(it ?: "") }, onCancel = onCancel, onClose = onClose,
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
    ) = service.alert(message, title, type, confirmText, showClose, onConfirm, onClose)
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
    ) = service.confirm(message, title, type, confirmText, cancelText, showClose, onConfirm, onCancel, onClose)
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
    ) = service.prompt(message, title, type, confirmText, cancelText, inputPlaceholder, initialInput, showClose, onConfirm, onCancel, onClose)
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
                type = item.type,
                confirmText = item.confirmText,
                cancelText = item.cancelText,
                showClose = item.showClose,
                showCancel = item.kind != ElMessageBoxKind.Alert,
                input = item.kind == ElMessageBoxKind.Prompt,
                inputValue = input,
                onInputChange = { input = it },
                inputPlaceholder = item.inputPlaceholder,
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
) {
    if (!visible) return
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedTitle = title ?: locale.messageBox.title
    var localInput by remember { mutableStateOf(inputValue) }
    val currentInput = if (onInputChange != null) inputValue else localInput
    val (icon, color) = when (type) {
        ElMessageBoxType.Info -> ElIcons.InfoFilled to colors.info
        ElMessageBoxType.Success -> ElIcons.CircleCheckFilled to colors.success
        ElMessageBoxType.Warning -> ElIcons.WarningFilled to colors.warning
        ElMessageBoxType.Error -> ElIcons.CircleCloseFilled to colors.danger
    }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .width(420.dp)
                    .background(colors.background, RoundedCornerShape(4.dp))
                    .border(1.dp, colors.borderLighter, RoundedCornerShape(4.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { }
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElIcon(icon, size = 22.dp, tint = color)
                    Spacer(Modifier.width(10.dp))
                    BasicText(
                        text = resolvedTitle,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.medium.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                    )
                    if (showClose) {
                        ElIcon(
                            ElIcons.Close,
                            size = 16.dp,
                            tint = colors.textPlaceholder,
                            contentDescription = locale.messageBox.close,
                            modifier = Modifier.clickable(onClick = onClose),
                        )
                    }
                }
                if (message != null) {
                    BasicText(
                        text = message,
                        modifier = Modifier.padding(top = 12.dp),
                        style = theme.typography.base.copy(color = colors.textRegular),
                    )
                }
                if (input) {
                    Spacer(Modifier.height(10.dp))
                    ElInput(
                        value = currentInput,
                        onValueChange = {
                            if (onInputChange != null) onInputChange(it) else localInput = it
                        },
                        placeholder = inputPlaceholder,
                        size = ElSize.Default,
                    )
                }
                Row(
                    Modifier.fillMaxWidth().padding(top = 18.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (showCancel) {
                        ElButton(
                            onClick = { onCancel?.invoke() },
                            text = cancelText ?: locale.messageBox.cancel,
                            type = ElType.Default,
                            size = ElSize.Default,
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    ElButton(
                        onClick = { onConfirm?.invoke() },
                        text = confirmText ?: locale.messageBox.confirm,
                        type = ElType.Primary,
                        size = ElSize.Default,
                    )
                }
            }
        }
    }
}
