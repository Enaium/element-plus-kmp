package cn.enaium.elementplus.theme

import androidx.compose.runtime.Immutable

@Immutable
data class ElBreadcrumbLocale(
    val label: String = "Breadcrumb",
)

@Immutable
data class ElPaginationLocale(
    val prev: String = "Go to previous page",
    val next: String = "Go to next page",
    val goto: String = "Go to",
    val page: String = "Page",
    val pagesize: String = "/page",
    val total: String = "Total {total}",
    val jump: String = "Go to",
    val pageClassifier: String = "page",
    val currentPage: String = "page {pager}",
    val prevPages: String = "Previous {pager} pages",
    val nextPages: String = "Next {pager} pages",
    val deprecationWarning: String = "Deprecated usages detected, please refer to the el-pagination documentation for more details",
)

@Immutable
data class ElDatePickerLocale(
    val now: String = "Now",
    val today: String = "Today",
    val cancel: String = "Cancel",
    val confirm: String = "OK",
    val clear: String = "Clear",
    val week: String = "Week",
    val month: String = "Month",
    val year: String = "Year",
    val dateTablePrompt: String = "Use the arrow keys and enter to select the day of the month",
    val monthTablePrompt: String = "Use the arrow keys and enter to select the month",
    val quarterTablePrompt: String = "Use the arrow keys and enter to select the quarter",
    val yearTablePrompt: String = "Use the arrow keys and enter to select the year",
    val selectedDate: String = "Selected date",
    val selectDate: String = "Select date",
    val selectTime: String = "Select time",
    val startDate: String = "Start Date",
    val startTime: String = "Start Time",
    val endDate: String = "End Date",
    val endTime: String = "End Time",
    val prevMonth: String = "Previous Month",
    val nextMonth: String = "Next Month",
    val prevYear: String = "Previous Year",
    val nextYear: String = "Next Year",
    val months: List<String> = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    ),
    val monthsShort: List<String> = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    ),
    val weekdays: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    val weekdaysShort: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    val weekdaysFull: List<String> = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
)

@Immutable
data class ElTimePickerLocale(
    val am: String = "AM",
    val pm: String = "PM",
    val selectTime: String = "Select time",
    val startTime: String = "Start Time",
    val endTime: String = "End Time",
    val selectDate: String = "Select date",
    val now: String = "Now",
    val confirm: String = "OK",
    val cancel: String = "Cancel",
)

@Immutable
data class ElInputLocale(
    val characters: String = "{count} / {max} characters",
)

@Immutable
data class ElInputNumberLocale(
    val decrease: String = "decrease number",
    val increase: String = "increase number",
)

@Immutable
data class ElTableLocale(
    val emptyText: String = "No Data",
    val confirmFilter: String = "Confirm",
    val resetFilter: String = "Reset",
    val clearFilter: String = "All",
    val sumText: String = "Sum",
    val selectAll: String = "Select all",
    val selectAllLabel: String = "Select all rows",
    val selectRowLabel: String = "Select this row",
    val expandRowLabel: String = "Expand this row",
    val collapseRowLabel: String = "Collapse this row",
    val sortLabel: String = "Sort by {column}",
    val filterLabel: String = "Filter by {column}",
)

@Immutable
data class ElSelectLocale(
    val loading: String = "Loading",
    val noData: String = "No data",
    val noMatch: String = "No matching data",
    val placeholder: String = "Select",
    val filterPlaceholder: String = "",
)

@Immutable
data class ElMentionLocale(
    val loading: String = "Loading",
)

@Immutable
data class ElDropdownLocale(
    val toggleDropdown: String = "Toggle Dropdown",
)

@Immutable
data class ElCascaderLocale(
    val noMatch: String = "No matching data",
    val loading: String = "Loading",
    val placeholder: String = "Select",
    val noData: String = "No data",
    val filterPlaceholder: String = "",
)

@Immutable
data class ElDialogLocale(
    val close: String = "Close this dialog",
)

@Immutable
data class ElDrawerLocale(
    val close: String = "Close this dialog",
)

@Immutable
data class ElMessageBoxLocale(
    val title: String = "Message",
    val confirm: String = "OK",
    val cancel: String = "Cancel",
    val error: String = "Illegal input",
    val close: String = "Close this dialog",
)

@Immutable
data class ElUploadLocale(
    val deleteTip: String = "press delete to remove",
    val delete: String = "Delete",
    val preview: String = "Preview",
    val continueText: String = "Continue",
    val cancel: String = "Cancel",
    val file: String = "File",
    val upload: String = "Upload",
    val uploading: String = "Uploading",
    val success: String = "Success",
    val fail: String = "Failed",
    val selectFile: String = "",
    val dropFileHere: String = "",
)

@Immutable
data class ElColorPickerLocale(
    val clear: String = "Clear",
    val confirm: String = "OK",
    val cancel: String = "Cancel",
    val defaultLabel: String = "color picker",
    val description: String = "current color is {color}. press enter to select a new color.",
    val alphaLabel: String = "pick alpha value",
    val alphaDescription: String = "alpha {alpha}, current color is {color}",
    val hueLabel: String = "pick hue value",
    val hueDescription: String = "hue {hue}, current color is {color}",
    val svLabel: String = "pick saturation and brightness value",
    val svDescription: String = "saturation {saturation}, brightness {brightness}, current color is {color}",
    val predefineDescription: String = "select {value} as the color",
)

@Immutable
data class ElSliderLocale(
    val defaultLabel: String = "slider between {min} and {max}",
    val defaultRangeStartLabel: String = "pick start value",
    val defaultRangeEndLabel: String = "pick end value",
)

@Immutable
data class ElTagLocale(
    val close: String = "Close this tag",
)

@Immutable
data class ElTourLocale(
    val next: String = "Next",
    val previous: String = "Previous",
    val finish: String = "Finish",
    val close: String = "Close this dialog",
)

@Immutable
data class ElTreeLocale(
    val emptyText: String = "No Data",
)

@Immutable
data class ElTransferLocale(
    val noMatch: String = "No matching data",
    val noData: String = "No data",
    val titles: List<String> = listOf("List 1", "List 2"),
    val filterPlaceholder: String = "Enter keyword",
    val noCheckedFormat: String = "{total} items",
    val hasCheckedFormat: String = "{checked}/{total} checked",
    val leftDefaultChecked: String = "Select all",
    val rightDefaultChecked: String = "Select all",
)

@Immutable
data class ElImageLocale(
    val error: String = "FAILED",
)

@Immutable
data class ElPageHeaderLocale(
    val title: String = "Back",
)

@Immutable
data class ElPopconfirmLocale(
    val title: String = "Are you sure?",
    val confirmButtonText: String = "Yes",
    val cancelButtonText: String = "No",
)

@Immutable
data class ElCarouselLocale(
    val leftArrow: String = "Carousel arrow left",
    val rightArrow: String = "Carousel arrow right",
    val indicator: String = "Carousel switch to index {index}",
)

@Immutable
data class ElInputOtpLocale(
    val groupLabel: String = "OTP Input",
    val defaultLabel: String = "Please enter OTP character {index}",
)

@Immutable
data class ElFormLocale(
    val required: String = "This field is required",
    val requiredWithLabel: String = "Please enter {label}",
    val email: String = "Please enter a valid email",
    val url: String = "Please enter a valid URL",
    val number: String = "Please enter a number",
    val integer: String = "Please enter an integer",
    val boolean: String = "Please enter a boolean",
    val array: String = "Please enter an array",
    val string: String = "Please enter a string",
    val pattern: String = "Value does not match the requested pattern",
    val length: String = "Length should be {len}",
    val min: String = "Value must be at least {min}",
    val max: String = "Value must be at most {max}",
)

@Immutable
data class ElInfiniteScrollLocale(
    val loading: String = "",
    val finished: String = "",
)

/**
 * UI strings used across the library. The shape mirrors the `el` object shipped
 * by Element Plus; nested sections fall back to the English defaults so adding
 * a new component string does not require touching every language override.
 */
@Immutable
data class ElLocale(
    val name: String = "en",
    val isRtl: Boolean = false,
    val emptyText: String = "No Data",
    val noMatchText: String = "No matching data",
    val loading: String = "Loading",
    val confirm: String = "OK",
    val cancel: String = "Cancel",
    val clear: String = "Clear",
    val close: String = "Close",
    val back: String = "Back",
    val save: String = "Save",
    val search: String = "Search",
    val breadcrumb: ElBreadcrumbLocale = ElBreadcrumbLocale(),
    val pagination: ElPaginationLocale = ElPaginationLocale(),
    val datePicker: ElDatePickerLocale = ElDatePickerLocale(),
    val timePicker: ElTimePickerLocale = ElTimePickerLocale(),
    val input: ElInputLocale = ElInputLocale(),
    val inputNumber: ElInputNumberLocale = ElInputNumberLocale(),
    val table: ElTableLocale = ElTableLocale(),
    val select: ElSelectLocale = ElSelectLocale(),
    val mention: ElMentionLocale = ElMentionLocale(),
    val dropdown: ElDropdownLocale = ElDropdownLocale(),
    val cascader: ElCascaderLocale = ElCascaderLocale(),
    val dialog: ElDialogLocale = ElDialogLocale(),
    val drawer: ElDrawerLocale = ElDrawerLocale(),
    val messageBox: ElMessageBoxLocale = ElMessageBoxLocale(),
    val upload: ElUploadLocale = ElUploadLocale(),
    val colorPicker: ElColorPickerLocale = ElColorPickerLocale(),
    val slider: ElSliderLocale = ElSliderLocale(),
    val tag: ElTagLocale = ElTagLocale(),
    val tour: ElTourLocale = ElTourLocale(),
    val tree: ElTreeLocale = ElTreeLocale(),
    val transfer: ElTransferLocale = ElTransferLocale(),
    val image: ElImageLocale = ElImageLocale(),
    val pageHeader: ElPageHeaderLocale = ElPageHeaderLocale(),
    val popconfirm: ElPopconfirmLocale = ElPopconfirmLocale(),
    val carousel: ElCarouselLocale = ElCarouselLocale(),
    val inputOtp: ElInputOtpLocale = ElInputOtpLocale(),
    val form: ElFormLocale = ElFormLocale(),
    val infiniteScroll: ElInfiniteScrollLocale = ElInfiniteScrollLocale(),
) {
    companion object {
        val English: ElLocale get() = ElLocales.en
        val Chinese: ElLocale get() = ElLocales.zhCn
    }
}
