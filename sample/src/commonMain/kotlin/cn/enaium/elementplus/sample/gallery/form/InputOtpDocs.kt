package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

@Composable
fun InputOtpDocs() {
    var masked by remember { mutableStateOf("1234") }
    var separated by remember { mutableStateOf("") }
    DemoExample("Basic usage", "Enter a fixed-length one-time code.") {
        var basic by remember { mutableStateOf("") }
        ElInputOtp(value = basic, onValueChange = { basic = it }, length = 6)
        DemoHint("Value: $basic")
    }
    DemoExample("Length and mask", "Change the length or mask the entered characters.") {
        var plain by remember { mutableStateOf("1234") }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElInputOtp(value = plain, onValueChange = { plain = it }, length = 6)
            ElInputOtp(value = masked, onValueChange = { masked = it }, length = 4, mask = true)
        }
    }
    DemoExample("Separator", "Add a separator between groups of fields.") {
        ElInputOtp(
            value = separated,
            onValueChange = { separated = it },
            length = 6,
            separator = { BasicText("-", style = LocalElTheme.current.typography.medium) },
        )
    }
    DemoExample("Types", "Outlined, filled and underlined input styles.") {
        var outlined by remember { mutableStateOf("") }
        var filled by remember { mutableStateOf("") }
        var underlined by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElInputOtp(value = outlined, onValueChange = { outlined = it }, length = 6, type = ElInputOtpType.Outlined)
            ElInputOtp(value = filled, onValueChange = { filled = it }, length = 6, type = ElInputOtpType.Filled)
            ElInputOtp(value = underlined, onValueChange = { underlined = it }, length = 6, type = ElInputOtpType.Underlined)
        }
    }
    DemoExample("Sizes and disabled", "OTP inputs support sizes and disabled state.") {
        var large by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElInputOtp(value = large, onValueChange = { large = it }, length = 6, size = ElSize.Large)
            ElInputOtp(value = "123456", onValueChange = {}, length = 6, disabled = true)
        }
    }

    DemoExample("Validator", "Provide a custom character validator.") {
        var value by remember { mutableStateOf("") }
        ElInputOtp(
            value = value,
            onValueChange = { value = it },
            length = 4,
            validator = { char, index -> char.all { it.isDigit() } && (index != 0 || char != "0") },
            onFinish = { ElMessage.show("Code: $it", ElMessageType.Success) },
        )
    }

    DemoExample("Custom length", "Use a different number of characters.") {
        var value by remember { mutableStateOf("") }
        ElInputOtp(value = value, onValueChange = { value = it }, length = 4)
    }
    DemoExample("Sizes and readonly", "OTP inputs follow the size scale and support readonly mode.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            var large by remember { mutableStateOf("1234") }
            ElInputOtp(value = large, onValueChange = { large = it }, length = 4, size = ElSize.Large)
            var readOnly by remember { mutableStateOf("5678") }
            ElInputOtp(value = readOnly, onValueChange = {}, length = 4, readonly = true)
        }
    }
}
