package cn.enaium.elementplus.sample.gallery

import androidx.compose.runtime.Composable
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.sample.gallery.basic.*
import cn.enaium.elementplus.sample.gallery.config.*
import cn.enaium.elementplus.sample.gallery.data.*
import cn.enaium.elementplus.sample.gallery.feedback.*
import cn.enaium.elementplus.sample.gallery.form.*
import cn.enaium.elementplus.sample.gallery.navigation.*
import cn.enaium.elementplus.sample.gallery.other.*

enum class SampleSection(val id: String, val title: String) {
    Basic("basic", "Basic 基础组件"),
    Config("config", "Config 配置"),
    Form("form", "Form 表单组件"),
    Data("data", "Data 数据展示"),
    Navigation("navigation", "Navigation 导航"),
    Feedback("feedback", "Feedback 反馈组件"),
    Other("other", "Other 其他"),
}

data class SampleComponent(
    val id: String,
    val title: String,
    val section: SampleSection,
    val description: String,
)

private val hanCharacters = Regex("[\\u4e00-\\u9fff]+")
private val repeatedSpaces = Regex("\\s+")

/** Chinese is shown only in Chinese locales; other locales use the English title. */
fun String.sampleTitle(locale: ElLocale): String =
    if (locale.name.startsWith("zh")) this
    else replace(hanCharacters, " ").replace(repeatedSpaces, " ").trim()

fun SampleSection.localizedTitle(locale: ElLocale): String = title.sampleTitle(locale)

fun SampleComponent.localizedTitle(locale: ElLocale): String = title.sampleTitle(locale)


private val localeDisplayNames = mapOf(
    "af" to "Afrikaans",
    "ar-eg" to "Arabic (Egypt)",
    "ar" to "Arabic",
    "az" to "Azerbaijani",
    "bg" to "Bulgarian",
    "bn" to "Bengali",
    "ca" to "Catalan",
    "ckb" to "Kurdish (Sorani)",
    "cs" to "Czech",
    "da" to "Danish",
    "de" to "German",
    "el" to "Greek",
    "en" to "English",
    "eo" to "Esperanto",
    "es" to "Spanish",
    "et" to "Estonian",
    "eu" to "Basque",
    "fa" to "Persian",
    "fi" to "Finnish",
    "fr" to "French",
    "gl" to "Galician",
    "he" to "Hebrew",
    "hi" to "Hindi",
    "hr" to "Croatian",
    "hu" to "Hungarian",
    "hy-am" to "Armenian",
    "id" to "Indonesian",
    "it" to "Italian",
    "ja" to "Japanese",
    "kk" to "Kazakh",
    "km" to "Khmer",
    "ko" to "Korean",
    "ku" to "Kurdish",
    "ky" to "Kyrgyz",
    "lo" to "Lao",
    "lt" to "Lithuanian",
    "lv" to "Latvian",
    "mg" to "Malagasy",
    "mn" to "Mongolian",
    "ms" to "Malay",
    "my" to "Burmese",
    "nb-no" to "Norwegian (Bokmål)",
    "nl" to "Dutch",
    "no" to "Norwegian",
    "pa" to "Punjabi",
    "pl" to "Polish",
    "pt-br" to "Portuguese (Brazil)",
    "pt" to "Portuguese",
    "ro" to "Romanian",
    "ru" to "Russian",
    "sk" to "Slovak",
    "sl" to "Slovenian",
    "sr" to "Serbian",
    "sv" to "Swedish",
    "sw" to "Swahili",
    "ta" to "Tamil",
    "te" to "Telugu",
    "th" to "Thai",
    "tk" to "Turkmen",
    "tr" to "Turkish",
    "ug-cn" to "Uyghur",
    "uk" to "Ukrainian",
    "uz-uz" to "Uzbek",
    "vi" to "Vietnamese",
    "zh-cn" to "Chinese (Simplified)",
    "zh-hk" to "Chinese (Hong Kong)",
    "zh-mo" to "Chinese (Macao)",
    "zh-tw" to "Chinese (Traditional)",
)

fun localeDisplayName(locale: ElLocale): String = localeDisplayNames[locale.name] ?: locale.name

fun sampleComponents(): List<SampleComponent> = listOf(
    // Basic
    SampleComponent("button", "Button 按钮", SampleSection.Basic, "Commonly used button."),
    SampleComponent("border", "Border 边框", SampleSection.Basic, "Border, radius and shadow tokens for surfaces."),
    SampleComponent("color", "Color 色彩", SampleSection.Basic, "The Element Plus semantic and neutral color palette."),
    SampleComponent("container", "Container 布局容器", SampleSection.Basic, "Header, aside, main and footer page containers."),
    SampleComponent("icon", "Icon 图标", SampleSection.Basic, "Icons generated from @element-plus/icons-vue."),
    SampleComponent("layout", "Layout 布局", SampleSection.Basic, "24-column responsive layout helpers."),
    SampleComponent("link", "Link 链接", SampleSection.Basic, "Text links with type, underline and disabled states."),
    SampleComponent("text", "Text 文本", SampleSection.Basic, "Semantic text, decoration, sizing and truncation."),
    SampleComponent("scrollbar", "Scrollbar 滚动条", SampleSection.Basic, "Custom vertical and horizontal scrollbars."),
    SampleComponent("space", "Space 间距", SampleSection.Basic, "Consistent spacing between inline elements."),
    SampleComponent("splitter", "Splitter 分割面板", SampleSection.Basic, "Resizable split panes."),
    SampleComponent("typography", "Typography 排版", SampleSection.Basic, "Headings, paragraphs and the typographic scale."),
    // Config
    SampleComponent("config-provider", "Config Provider 全局配置", SampleSection.Config, "Provide theme, locale, default size and button defaults to a subtree."),
    // Form
    SampleComponent("autocomplete", "Autocomplete 自动补全", SampleSection.Form, "Input with suggestions fetched from a caller-provided function."),
    SampleComponent("cascader", "Cascader 级联选择", SampleSection.Form, "Select a value from a hierarchy of options."),
    SampleComponent("checkbox", "Checkbox 多选框", SampleSection.Form, "Checkbox groups, button styles and indeterminate state."),
    SampleComponent("color-picker-panel", "Color Picker Panel 颜色选择面板", SampleSection.Form, "Saturation, hue and alpha controls in a standalone panel."),
    SampleComponent("color-picker", "Color Picker 颜色选择", SampleSection.Form, "A color field with an attached panel."),
    SampleComponent("date-picker-panel", "Date Picker Panel 日期选择面板", SampleSection.Form, "A calendar panel for date, month or year selection."),
    SampleComponent("date-picker", "Date Picker 日期选择", SampleSection.Form, "Date, month and year selection in a popup."),
    SampleComponent("datetime-picker", "DateTime Picker 日期时间选择", SampleSection.Form, "Date and time selection."),
    SampleComponent("form", "Form 表单", SampleSection.Form, "Form layout, labels and validation."),
    SampleComponent("input", "Input 输入框", SampleSection.Form, "Text, password and textarea input."),
    SampleComponent("input-number", "Input Number 数字输入框", SampleSection.Form, "Numeric input with controls, bounds and precision."),
    SampleComponent("input-tag", "Input Tag 标签输入", SampleSection.Form, "Turn text input into closable tags."),
    SampleComponent("input-otp", "Input OTP 一次性密码", SampleSection.Form, "Fixed-length one-time password input."),
    SampleComponent("mention", "Mention 提及", SampleSection.Form, "At-mention suggestions in a textarea."),
    SampleComponent("radio", "Radio 单选框", SampleSection.Form, "Radio groups and button variants."),
    SampleComponent("rate", "Rate 评分", SampleSection.Form, "Star rating with half stars and texts."),
    SampleComponent("select", "Select 选择器", SampleSection.Form, "Single/multiple, remote, create and custom-slot selection."),
    SampleComponent("select-v2", "Virtualized Select 虚拟化选择器", SampleSection.Form, "Large lazy option lists, groups, remote and custom slots."),
    SampleComponent("slider", "Slider 滑块", SampleSection.Form, "Single and range sliders."),
    SampleComponent("switch", "Switch 开关", SampleSection.Form, "Toggle between two states."),
    SampleComponent("time-picker", "Time Picker 时间选择", SampleSection.Form, "Time selection with a spinner panel."),
    SampleComponent("time-select", "Time Select 时间选择", SampleSection.Form, "Time selection from a generated list."),
    SampleComponent("transfer", "Transfer 穿梭框", SampleSection.Form, "Move items between two lists."),
    SampleComponent("tree-select", "TreeSelect 树选择", SampleSection.Form, "Select a value from a tree."),
    SampleComponent("upload", "Upload 上传", SampleSection.Form, "Headless upload UI with file list and status."),
    // Data
    SampleComponent("avatar", "Avatar 头像", SampleSection.Data, "Images, icons, initials and avatar groups."),
    SampleComponent("badge", "Badge 徽章", SampleSection.Data, "Counts and dot indicators on content."),
    SampleComponent("calendar", "Calendar 日历", SampleSection.Data, "A month calendar for display and date selection."),
    SampleComponent("card", "Card 卡片", SampleSection.Data, "Content container with header and footer."),
    SampleComponent("carousel", "Carousel 走马灯", SampleSection.Data, "Looping content slider with indicators."),
    SampleComponent("collapse", "Collapse 折叠面板", SampleSection.Data, "Expandable content panels."),
    SampleComponent("descriptions", "Descriptions 描述列表", SampleSection.Data, "Label/content lists with spans and borders."),
    SampleComponent("empty", "Empty 空状态", SampleSection.Data, "Empty state with an illustration."),
    SampleComponent("image", "Image 图片", SampleSection.Data, "Image display with preview and placeholders."),
    SampleComponent("infinite-scroll", "Infinite Scroll 无限滚动", SampleSection.Data, "Load more content when reaching the end."),
    SampleComponent("pagination", "Pagination 分页", SampleSection.Data, "Page navigation with sizes and jumper."),
    SampleComponent("progress", "Progress 进度条", SampleSection.Data, "Linear, circular and dashboard progress."),
    SampleComponent("result", "Result 结果", SampleSection.Data, "Status result pages with actions."),
    SampleComponent("skeleton", "Skeleton 骨架屏", SampleSection.Data, "Loading placeholders for content."),
    SampleComponent("table", "Table 表格", SampleSection.Data, "Data table with sorting and selection."),
    SampleComponent("table-v2", "Virtualized Table 虚拟化表格", SampleSection.Data, "Virtualized rows with fixed columns, tree, spans, sorting, filtering and slots."),
    SampleComponent("tag", "Tag 标签", SampleSection.Data, "Compact labels with types and effects."),
    SampleComponent("timeline", "Timeline 时间线", SampleSection.Data, "Activity timeline with timestamps."),
    SampleComponent("tour", "Tour 引导", SampleSection.Data, "Step-by-step guided tour."),
    SampleComponent("tree", "Tree 树形控件", SampleSection.Data, "Selectable, lazy, draggable and checkable tree."),
    SampleComponent("tree-v2", "Virtualized Tree 虚拟化树", SampleSection.Data, "Large trees with lazy row rendering and custom nodes."),
    SampleComponent("statistic", "Statistic 统计数值", SampleSection.Data, "Formatted numeric statistics."),
    SampleComponent("segmented", "Segmented 分段控制器", SampleSection.Data, "Segmented option selection."),
    // Navigation
    SampleComponent("affix", "Affix 固钉", SampleSection.Navigation, "Pin an element while its container scrolls."),
    SampleComponent("anchor", "Anchor 锚点", SampleSection.Navigation, "Anchor links with active state."),
    SampleComponent("backtop", "Backtop 回到顶部", SampleSection.Navigation, "A floating scroll-to-top button."),
    SampleComponent("breadcrumb", "Breadcrumb 面包屑", SampleSection.Navigation, "Show the current page's hierarchy."),
    SampleComponent("dropdown", "Dropdown 下拉菜单", SampleSection.Navigation, "A menu attached to a trigger."),
    SampleComponent("menu", "Menu 菜单", SampleSection.Navigation, "Vertical and horizontal navigation menus."),
    SampleComponent("page-header", "Page Header 页头", SampleSection.Navigation, "Page header with back, content and extras."),
    SampleComponent("steps", "Steps 步骤条", SampleSection.Navigation, "Guide users through a process."),
    SampleComponent("tabs", "Tabs 标签页", SampleSection.Navigation, "Switch between views with tabs."),
    // Feedback
    SampleComponent("alert", "Alert 警告提示", SampleSection.Feedback, "Inline feedback messages."),
    SampleComponent("dialog", "Dialog 对话框", SampleSection.Feedback, "Modal content and actions."),
    SampleComponent("drawer", "Drawer 抽屉", SampleSection.Feedback, "A panel that slides in from an edge."),
    SampleComponent("loading", "Loading 加载", SampleSection.Feedback, "Loading overlay for content or the page."),
    SampleComponent("message", "Message 消息提示", SampleSection.Feedback, "Transient messages at the top of the screen."),
    SampleComponent("message-box", "Message Box 消息框", SampleSection.Feedback, "Alert, confirm and prompt dialogs."),
    SampleComponent("notification", "Notification 通知", SampleSection.Feedback, "Rich notifications at a screen corner."),
    SampleComponent("popconfirm", "Popconfirm 气泡确认", SampleSection.Feedback, "Inline confirmation attached to a trigger."),
    SampleComponent("popover", "Popover 弹出框", SampleSection.Feedback, "Rich content attached to a trigger."),
    SampleComponent("tooltip", "Tooltip 文字提示", SampleSection.Feedback, "Short hints on hover."),
    // Other
    SampleComponent("divider", "Divider 分割线", SampleSection.Other, "Horizontal and vertical content separators."),
    SampleComponent("watermark", "Watermark 水印", SampleSection.Other, "Tiled text watermark over content."),
)

@Composable
fun SampleComponentPage(component: SampleComponent) {
    when (component.id) {
        "button" -> ButtonDocs()
        "border" -> BorderDocs()
        "color" -> ColorDocs()
        "container" -> ContainerDocs()
        "icon" -> IconDocs()
        "layout" -> LayoutDocs()
        "link" -> LinkDocs()
        "text" -> TextDocs()
        "scrollbar" -> ScrollbarDocs()
        "space" -> SpaceDocs()
        "splitter" -> SplitterDocs()
        "typography" -> TypographyDocs()
        "config-provider" -> ConfigProviderDocs()
        "autocomplete" -> AutocompleteDocs()
        "cascader" -> CascaderDocs()
        "checkbox" -> CheckboxDocs()
        "color-picker-panel" -> ColorPickerPanelDocs()
        "color-picker" -> ColorPickerDocs()
        "date-picker-panel" -> DatePickerPanelDocs()
        "date-picker" -> DatePickerDocs()
        "datetime-picker" -> DateTimePickerDocs()
        "form" -> FormDocs()
        "input" -> InputDocs()
        "input-number" -> InputNumberDocs()
        "input-tag" -> InputTagDocs()
        "input-otp" -> InputOtpDocs()
        "mention" -> MentionDocs()
        "radio" -> RadioDocs()
        "rate" -> RateDocs()
        "select" -> SelectDocs()
        "select-v2" -> VirtualizedSelectDocs()
        "slider" -> SliderDocs()
        "switch" -> SwitchDocs()
        "time-picker" -> TimePickerDocs()
        "time-select" -> TimeSelectDocs()
        "transfer" -> TransferDocs()
        "tree-select" -> TreeSelectDocs()
        "upload" -> UploadDocs()
        "avatar" -> AvatarDocs()
        "badge" -> BadgeDocs()
        "calendar" -> CalendarDocs()
        "card" -> CardDocs()
        "carousel" -> CarouselDocs()
        "collapse" -> CollapseDocs()
        "descriptions" -> DescriptionsDocs()
        "empty" -> EmptyDocs()
        "image" -> ImageDocs()
        "infinite-scroll" -> InfiniteScrollDocs()
        "pagination" -> PaginationDocs()
        "progress" -> ProgressDocs()
        "result" -> ResultDocs()
        "skeleton" -> SkeletonDocs()
        "table" -> TableDocs()
        "table-v2" -> VirtualizedTableDocs()
        "tag" -> TagDocs()
        "timeline" -> TimelineDocs()
        "tour" -> TourDocs()
        "tree" -> TreeDocs()
        "tree-v2" -> VirtualizedTreeDocs()
        "statistic" -> StatisticDocs()
        "segmented" -> SegmentedDocs()
        "affix" -> AffixDocs()
        "anchor" -> AnchorDocs()
        "backtop" -> BacktopDocs()
        "breadcrumb" -> BreadcrumbDocs()
        "dropdown" -> DropdownDocs()
        "menu" -> MenuDocs()
        "page-header" -> PageHeaderDocs()
        "steps" -> StepsDocs()
        "tabs" -> TabsDocs()
        "alert" -> AlertDocs()
        "dialog" -> DialogDocs()
        "drawer" -> DrawerDocs()
        "loading" -> LoadingDocs()
        "message" -> MessageDocs()
        "message-box" -> MessageBoxDocs()
        "notification" -> NotificationDocs()
        "popconfirm" -> PopconfirmDocs()
        "popover" -> PopoverDocs()
        "tooltip" -> TooltipDocs()
        "divider" -> DividerDocs()
        "watermark" -> WatermarkDocs()
    }
}
