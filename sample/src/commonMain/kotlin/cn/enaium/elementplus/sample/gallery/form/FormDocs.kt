package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

private val activityZones = listOf(
    ElSelectOption("shanghai", "Zone one"),
    ElSelectOption("beijing", "Zone two"),
)

private val activityTypes = listOf(
    ElCheckboxOption("Online activities", "Online activities"),
    ElCheckboxOption("Promotion activities", "Promotion activities"),
    ElCheckboxOption("Offline activities", "Offline activities"),
    ElCheckboxOption("Simple brand exposure", "Simple brand exposure"),
)

private val resourceOptions = listOf(
    ElRadioOption("Sponsor", "Sponsor"),
    ElRadioOption("Venue", "Venue"),
)

@Composable
fun FormDocs() {
    DemoExample("Basic form", "A complete form with inputs, selects, pickers, switches, checkboxes and radio groups.") {
        var name by remember { mutableStateOf("") }
        var zone by remember { mutableStateOf<String?>(null) }
        var date by remember { mutableStateOf<LocalDate?>(null) }
        var time by remember { mutableStateOf<LocalTime?>(null) }
        var delivery by remember { mutableStateOf(false) }
        var types by remember { mutableStateOf(emptyList<String>()) }
        var resource by remember { mutableStateOf<String?>(null) }
        var description by remember { mutableStateOf("") }
        ElForm(labelWidth = 150.dp) {
            ElFormItem(label = "Activity name") {
                ElInput(value = name, onValueChange = { name = it }, modifier = Modifier.width(420.dp))
            }
            ElFormItem(label = "Activity zone") {
                ElSelect(
                    value = zone,
                    onValueChange = { zone = it },
                    options = activityZones,
                    placeholder = "please select your zone",
                    modifier = Modifier.width(420.dp),
                )
            }
            ElFormItem(label = "Activity time") {
                Row(
                    Modifier.width(420.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElDatePicker(
                        value = date,
                        onValueChange = { date = it },
                        placeholder = "Pick a date",
                        modifier = Modifier.weight(1f),
                    )
                    BasicText("-", style = cn.enaium.elementplus.theme.LocalElTheme.current.typography.base)
                    ElTimePicker(
                        value = time,
                        onValueChange = { time = it },
                        placeholder = "Pick a time",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            ElFormItem(label = "Instant delivery") {
                ElSwitch(checked = delivery, onCheckedChange = { delivery = it })
            }
            ElFormItem(label = "Activity type") {
                ElCheckboxGroup(options = activityTypes, selected = types, onSelectedChange = { types = it })
            }
            ElFormItem(label = "Resources") {
                ElRadioGroup(options = resourceOptions, selected = resource, onSelectedChange = { resource = it })
            }
            ElFormItem(label = "Activity form") {
                ElInput(
                    value = description,
                    onValueChange = { description = it },
                    type = ElInputType.Textarea,
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.width(420.dp),
                )
            }
            ElFormItem {
                Row(Modifier.padding(start = 162.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElButton(
                        onClick = { ElMessage.show("Activity created", ElMessageType.Success) },
                        text = "Create",
                        type = ElType.Primary,
                    )
                    ElButton(onClick = {}, content = { _ -> BasicText("Cancel") })
                }
            }
        }
    }
    DemoExample("Inline form", "Inline forms place fields and actions on one row.") {
        var name by remember { mutableStateOf("") }
        var zone by remember { mutableStateOf<String?>(null) }
        ElForm(inline = true) {
            ElFormItem(label = "Name") {
                ElInput(value = name, onValueChange = { name = it }, modifier = Modifier.width(150.dp))
            }
            ElFormItem(label = "Zone") {
                ElSelect(
                    value = zone,
                    onValueChange = { zone = it },
                    options = activityZones,
                    placeholder = "Zone",
                    modifier = Modifier.width(150.dp),
                )
            }
            ElButton(onClick = {}, text = "Query", type = ElType.Primary, size = ElSize.Small)
        }
    }
    DemoExample("Alignment", "Switch the form label position and override alignment per form item.") {
        var position by remember { mutableStateOf(ElFormLabelPosition.Right) }
        var itemPosition by remember { mutableStateOf<ElFormLabelPosition?>(null) }
        var name by remember { mutableStateOf("") }
        var zone by remember { mutableStateOf("") }
        var form by remember { mutableStateOf("") }
        ElForm(labelPosition = position, labelWidth = 120.dp) {
            ElFormItem(label = "Form Align", labelPosition = ElFormLabelPosition.Right) {
                ElRadioGroup(
                    options = listOf(
                        ElRadioOption("left", "Left"),
                        ElRadioOption("right", "Right"),
                        ElRadioOption("top", "Top"),
                    ),
                    selected = when (position) {
                        ElFormLabelPosition.Left -> "left"
                        ElFormLabelPosition.Right -> "right"
                        ElFormLabelPosition.Top -> "top"
                    },
                    onSelectedChange = {
                        position = when (it) {
                            "left" -> ElFormLabelPosition.Left
                            "top" -> ElFormLabelPosition.Top
                            else -> ElFormLabelPosition.Right
                        }
                    },
                    button = true,
                )
            }
            ElFormItem(label = "Form Item Align", labelPosition = ElFormLabelPosition.Right) {
                ElRadioGroup(
                    options = listOf(
                        ElRadioOption("", "Empty"),
                        ElRadioOption("left", "Left"),
                        ElRadioOption("right", "Right"),
                        ElRadioOption("top", "Top"),
                    ),
                    selected = when (itemPosition) {
                        null -> ""
                        ElFormLabelPosition.Left -> "left"
                        ElFormLabelPosition.Right -> "right"
                        ElFormLabelPosition.Top -> "top"
                    },
                    onSelectedChange = {
                        itemPosition = when (it) {
                            "left" -> ElFormLabelPosition.Left
                            "right" -> ElFormLabelPosition.Right
                            "top" -> ElFormLabelPosition.Top
                            else -> null
                        }
                    },
                    button = true,
                )
            }
            ElFormItem(label = "Name", labelPosition = itemPosition) {
                ElInput(value = name, onValueChange = { name = it })
            }
            ElFormItem(label = "Activity zone", labelPosition = itemPosition) {
                ElInput(value = zone, onValueChange = { zone = it })
            }
            ElFormItem(label = "Activity form", labelPosition = itemPosition) {
                ElInput(value = form, onValueChange = { form = it })
            }
        }
    }
    DemoExample("Validation", "Validate the complete activity form on submit.") {
        val state = rememberElFormState()
        var name by remember { mutableStateOf("Hello") }
        var zone by remember { mutableStateOf<String?>(null) }
        var delivery by remember { mutableStateOf(false) }
        var types by remember { mutableStateOf(emptyList<String>()) }
        var resource by remember { mutableStateOf<String?>(null) }
        var description by remember { mutableStateOf("") }
        ElForm(state = state, labelWidth = 150.dp) {
            ElFormItem(
                label = "Activity name",
                value = name,
                prop = "name",
                rules = listOf(
                    ElFormRule(required = true, message = "Please input Activity name"),
                    ElFormRule(min = 3.0, max = 5.0, message = "Length should be 3 to 5"),
                ),
            ) {
                ElInput(value = name, onValueChange = { name = it }, status = validateStatus)
            }
            ElFormItem(
                label = "Activity zone",
                value = zone,
                prop = "zone",
                rules = listOf(ElFormRule(required = true, message = "Please select Activity zone")),
            ) {
                ElSelect(
                    value = zone,
                    onValueChange = { zone = it },
                    options = activityZones,
                    placeholder = "Activity zone",
                    modifier = Modifier.width(420.dp),
                )
            }
            ElFormItem(label = "Activity time", required = true) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    var date by remember { mutableStateOf<LocalDate?>(null) }
                    var time by remember { mutableStateOf<LocalTime?>(null) }
                    ElDatePicker(value = date, onValueChange = { date = it }, placeholder = "Pick a date", modifier = Modifier.width(180.dp))
                    BasicText("-")
                    ElTimePicker(value = time, onValueChange = { time = it }, placeholder = "Pick a time", modifier = Modifier.width(160.dp))
                }
            }
            ElFormItem(
                label = "Activity location",
                value = "",
                prop = "location",
            ) {
                var location by remember { mutableStateOf("") }
                ElSegmented(
                    options = listOf(
                        ElSegmentedOption("Home", "Home"),
                        ElSegmentedOption("Company", "Company"),
                        ElSegmentedOption("School", "School"),
                    ),
                    value = location,
                    onValueChange = { location = it },
                )
            }
            ElFormItem(
                label = "Instant delivery",
                value = delivery,
                prop = "delivery",
            ) {
                ElSwitch(checked = delivery, onCheckedChange = { delivery = it })
            }
            ElFormItem(
                label = "Activity type",
                value = types,
                prop = "types",
                rules = listOf(ElFormRule(required = true, message = "Please select at least one activity type")),
            ) {
                ElCheckboxGroup(options = activityTypes, selected = types, onSelectedChange = { types = it })
            }
            ElFormItem(
                label = "Resources",
                value = resource,
                prop = "resource",
                rules = listOf(ElFormRule(required = true, message = "Please select activity resource")),
            ) {
                ElRadioGroup(options = resourceOptions, selected = resource, onSelectedChange = { resource = it })
            }
            ElFormItem(
                label = "Activity form",
                value = description,
                prop = "description",
                rules = listOf(ElFormRule(required = true, message = "Please input activity form")),
            ) {
                ElInput(
                    value = description,
                    onValueChange = { description = it },
                    type = ElInputType.Textarea,
                    minLines = 2,
                    maxLines = 4,
                    status = validateStatus,
                )
            }
            ElFormItem {
                Row(Modifier.padding(start = 162.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElButton(
                        onClick = {
                            if (state.validate()) ElMessage.show("Valid", ElMessageType.Success)
                            else ElMessage.show("Please fix the errors", ElMessageType.Error)
                        },
                        text = "Create",
                        type = ElType.Primary,
                    )
                    ElButton(onClick = { state.clearValidate() }, text = "Reset")
                }
            }
        }
    }
    DemoExample("Number validate", "Use a custom validator to validate a numeric field.") {
        val state = rememberElFormState()
        var number by remember { mutableStateOf("") }
        ElForm(state = state) {
            ElFormItem(
                label = "Number",
                value = number,
                rules = listOf(
                    ElFormRule(
                        validator = { value ->
                            val text = value as? String ?: ""
                            when {
                                text.isBlank() -> "Please input a number"
                                text.toIntOrNull() == null -> "Please input a number"
                                else -> null
                            }
                        },
                    ),
                ),
            ) {
                ElInput(value = number, onValueChange = { number = it }, placeholder = "Type a number", status = validateStatus)
            }
            ElButton(onClick = { state.validate() }, text = "Validate", type = ElType.Primary)
        }
    }
    DemoExample("Custom validation", "Use a validator for business-specific constraints.") {
        val state = rememberElFormState()
        var bio by remember { mutableStateOf("") }
        var count by remember { mutableStateOf("") }
        ElForm(state = state) {
            ElFormItem(
                label = "Bio",
                value = bio,
                rules = listOf(ElFormRule(min = 10.0, message = "At least 10 characters")),
            ) {
                ElInput(
                    value = bio,
                    onValueChange = { bio = it },
                    type = ElInputType.Textarea,
                    minLines = 2,
                    maxLines = 4,
                    status = validateStatus,
                )
            }
            ElFormItem(
                label = "Even number",
                value = count,
                rules = listOf(
                    ElFormRule(
                        validator = { value ->
                            if ((value as? String)?.toIntOrNull()?.rem(2) == 1) "Must be even" else null
                        },
                    ),
                ),
            ) {
                ElInput(value = count, onValueChange = { count = it }, placeholder = "Type a number", status = validateStatus)
            }
            ElButton(onClick = { state.validate() }, text = "Validate", type = ElType.Primary)
        }
    }
    DemoExample("Size control", "Form items inherit the form size.") {
        var value by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElForm(size = ElSize.Large) { ElFormItem(label = "Large") { ElInput(value = value, onValueChange = { value = it }) } }
            ElForm(size = ElSize.Default) { ElFormItem(label = "Default") { ElInput(value = value, onValueChange = { value = it }) } }
            ElForm(size = ElSize.Small) { ElFormItem(label = "Small") { ElInput(value = value, onValueChange = { value = it }) } }
        }
    }

    DemoExample("Add/Delete form item", "Add and remove form rows dynamically.") {
        var items by remember { mutableStateOf(listOf("")) }
        ElForm(labelWidth = 100.dp) {
            items.forEachIndexed { index, value ->
                ElFormItem(label = "Item ${index + 1}") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        ElInput(
                            value = value,
                            onValueChange = { next -> items = items.toMutableList().also { it[index] = next } },
                            placeholder = "Enter a value",
                            modifier = Modifier.width(320.dp),
                        )
                        ElButton(
                            onClick = { items = items.filterIndexed { i, _ -> i != index } },
                            text = "Delete",
                            type = ElType.Danger,
                            disabled = items.size == 1,
                        )
                    }
                }
            }
            ElFormItem {
                ElButton(onClick = { items = items + "" }, text = "+ Add item", type = ElType.Primary)
                BasicText("Current rows: ${items.size}", style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary))
            }
        }
    }

    DemoExample("Accessibility", "Labels describe each field; grouped fields need an aria-label per input.") {
        var fullName by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        ElForm(labelPosition = ElFormLabelPosition.Left, labelWidth = 120.dp) {
            ElFormItem(label = "Full Name") {
                ElInput(
                    value = fullName,
                    onValueChange = { fullName = it },
                    ariaLabel = "Full Name",
                    modifier = Modifier.width(400.dp),
                )
            }
            ElFormItem(label = "Your Information") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicText(
                        "\"Your Information\" labels the group; every input still declares its own aria-label.",
                        style = LocalElTheme.current.typography.extraSmall.copy(color = LocalElTheme.current.colors.textSecondary),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ElInput(
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = "First Name",
                            ariaLabel = "First Name",
                            modifier = Modifier.weight(1f),
                        )
                        ElInput(
                            value = lastName,
                            onValueChange = { lastName = it },
                            placeholder = "Last Name",
                            ariaLabel = "Last Name",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }

    DemoExample("Validation triggers", "Blur-triggered rules, inline messages and a status icon.") {
        val state = rememberElFormState()
        var name by remember { mutableStateOf("") }
        var checks by remember { mutableStateOf(0) }
        ElForm(state = state, labelWidth = 120.dp, inlineMessage = true, statusIcon = true) {
            ElFormItem(
                label = "Activity name",
                value = name,
                prop = "name",
                rules = listOf(
                    ElFormRule(
                        required = true,
                        message = "Please input Activity name",
                        trigger = listOf(ElFormTrigger.Blur),
                    ),
                    ElFormRule(
                        min = 3.0,
                        message = "Length should be at least 3",
                        trigger = listOf(ElFormTrigger.Blur),
                    ),
                ),
            ) {
                ElInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Triggered on blur",
                    onFocusChanged = { focused -> if (!focused) blur() },
                    modifier = Modifier.width(320.dp),
                )
            }
            ElFormItem {
                ElButton(
                    onClick = { checks += 1; state.validate() },
                    text = "Validate ($checks)",
                    type = ElType.Primary,
                )
            }
        }
    }

    DemoExample("Required asterisk", "Hide the asterisk or place it after the label.") {
        var hide by remember { mutableStateOf(false) }
        var position by remember { mutableStateOf(ElFormRequireAsteriskPosition.Left) }
        var name by remember { mutableStateOf("") }
        ElForm(
            labelWidth = 120.dp,
            hideRequiredAsterisk = hide,
            requireAsteriskPosition = position,
        ) {
            ElFormItem(label = "Name", required = true) {
                ElInput(value = name, onValueChange = { name = it }, modifier = Modifier.width(320.dp))
            }
        }
        DemoRowSpaced {
            ElCheckbox(checked = hide, onCheckedChange = { hide = it }, label = "Hide asterisk")
            ElSegmented(
                options = listOf(
                    ElSegmentedOption(ElFormRequireAsteriskPosition.Left, "Left"),
                    ElSegmentedOption(ElFormRequireAsteriskPosition.Right, "Right"),
                ),
                value = position,
                onValueChange = { position = it },
            )
        }
    }
}
