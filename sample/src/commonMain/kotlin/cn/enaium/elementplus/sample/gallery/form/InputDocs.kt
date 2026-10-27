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
fun InputDocs() {
    var password by remember { mutableStateOf("secret") }
    var textarea by remember { mutableStateOf("Element Plus for Compose") }
    var iconValue by remember { mutableStateOf("") }
    var grouped by remember { mutableStateOf("") }
    DemoExample("Basic usage", "The input supports placeholder, clearable and text values.") {
        var basic by remember { mutableStateOf("") }
        Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(value = basic, onValueChange = { basic = it }, placeholder = "Please input", clearable = true)
            ElInput(value = basic, onValueChange = { basic = it }, placeholder = "With max length", maxLength = 20, showWordLimit = true)
        }
    }
    DemoExample("Password", "Use the password type to mask input and optionally reveal it.") {
        ElInput(
            value = password,
            onValueChange = { password = it },
            type = ElInputType.Password,
            showPassword = true,
            placeholder = "Password",
            modifier = Modifier.width(360.dp),
        )
    }
    DemoExample("Textarea", "Use the textarea type for multi-line text.") {
        ElInput(
            value = textarea,
            onValueChange = { textarea = it },
            type = ElInputType.Textarea,
            minLines = 3,
            maxLines = 5,
            maxLength = 120,
            showWordLimit = true,
            modifier = Modifier.width(360.dp),
        )
    }
    DemoExample("Sizes", "Inputs follow the component size scale.") {
        var basic by remember { mutableStateOf("") }
        Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(value = basic, onValueChange = { basic = it }, size = ElSize.Large, placeholder = "Large")
            ElInput(value = basic, onValueChange = { basic = it }, size = ElSize.Default, placeholder = "Default")
            ElInput(value = basic, onValueChange = { basic = it }, size = ElSize.Small, placeholder = "Small")
        }
    }
    DemoExample("With icon", "Prefix and suffix slots can hold icons or text.") {
        Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(
                value = iconValue,
                onValueChange = { iconValue = it },
                placeholder = "Search",
                prefix = { cn.enaium.elementplus.icons.ElIcon(cn.enaium.elementplus.icons.ElIcons.Search, size = 14.dp) },
                clearable = true,
            )
            ElInput(
                value = iconValue,
                onValueChange = { iconValue = it },
                placeholder = "With suffix",
                suffix = { ElText("km", size = ElSize.Small) },
            )
        }
    }
    DemoExample("Prepend and append", "Combine the field with fixed text blocks.") {
        ElInput(
            value = grouped,
            onValueChange = { grouped = it },
            placeholder = "Hostname",
            prepend = { ElText("https://", size = ElSize.Small) },
            append = { ElText(".com", size = ElSize.Small) },
            modifier = Modifier.width(420.dp),
        )
    }
    DemoExample("Disabled", "A disabled input cannot be edited.") {
        ElInput(value = "Disabled", onValueChange = {}, disabled = true, modifier = Modifier.width(360.dp))
    }

    DemoExample("Auto-sizing textarea", "The textarea grows between min and max lines.") {
        var value by remember { mutableStateOf("A textarea that grows with its content.") }
        ElInput(
            value = value,
            onValueChange = { value = it },
            type = ElInputType.Textarea,
            minLines = 2,
            maxLines = 6,
            modifier = Modifier.width(360.dp),
        )
    }

    DemoExample("Clearable", "Show a clear button while the input has a value.") {
        var value by remember { mutableStateOf("Clear me") }
        ElInput(value = value, onValueChange = { value = it }, clearable = true, modifier = Modifier.width(300.dp))
    }
    DemoExample("Limit length", "Limit the number of characters and show a word count.") {
        var value by remember { mutableStateOf("") }
        ElInput(
            value = value,
            onValueChange = { value = it },
            maxLength = 10,
            showWordLimit = true,
            placeholder = "Up to 10 characters",
            modifier = Modifier.width(300.dp),
        )
    }
}
