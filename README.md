# Element Plus KMP

![](https://img.cdn1.vip/i/6ae02a77c3c2e_1793075831.webp)

A Kotlin Multiplatform / Compose Multiplatform port of [Element Plus](https://element-plus.org). It follows the Element Plus visual language, component anatomy and theme tokens while exposing idiomatic, state-hoisted Compose APIs.

Supported targets: **Android, iOS (arm64 + simulator), Desktop/JVM, Kotlin/JS and Kotlin/Wasm**.

## Installation

```kotlin
implementation("cn.enaium.elementplus:element-plus-kmp:1.0.0")
```

The library is built on Compose Multiplatform Foundation/UI and does not require Material.

## Quick start

```kotlin
@Composable
fun App() {
    var dark by remember { mutableStateOf(false) }

    ElConfigProvider(
        theme = if (dark) ElTheme.Dark else ElTheme.Light,
        locale = ElLocale.English,
        size = ElSize.Default,
    ) {
        ElMessage.Host {
            ElNotification.Host {
                ElMessageBox.Host {
                    AppContent()
                }
            }
        }
    }
}
```

Mount the message, notification and message-box hosts once. After that they can be invoked imperatively from event handlers.

### Forms and controlled state

```kotlin
var name by remember { mutableStateOf("") }
var agree by remember { mutableStateOf(false) }

ElForm {
    ElFormItem(
        label = "Name",
        value = name,
        rules = listOf(ElFormRule(required = true, message = "Name is required")),
    ) {
        ElInput(value = name, onValueChange = { name = it }, status = validateStatus)
    }
    ElCheckbox(checked = agree, onCheckedChange = { agree = it }, label = "I agree")
    ElButton(onClick = { /* ... */ }, text = "Submit", type = ElType.Primary)
}
```

## Component coverage

| Category | Components |
| --- | --- |
| Basic | Button, Border, Color, Layout Container, Icon, Layout, Link, Text, Scrollbar, Space, Splitter, Typography |
| Config | Config Provider |
| Form | Autocomplete, Cascader, Checkbox, Color Picker Panel, Color Picker, Date Picker Panel, Date Picker, DateTime Picker, Form, Input, Input Number, Input Tag, Input OTP, Mention, Radio, Rate, Select, Virtualized Select, Slider, Switch, Time Picker, Time Select, Transfer, TreeSelect, Upload |
| Data | Avatar, Badge, Calendar, Card, Carousel, Collapse, Descriptions, Empty, Image, Infinite Scroll, Pagination, Progress, Result, Skeleton, Table, Virtualized Table, Tag, Timeline, Tour, Tree, Virtualized Tree, Statistic, Segmented |
| Navigation | Affix, Anchor, Backtop, Breadcrumb, Dropdown, Menu, Page Header, Steps, Tabs |
| Feedback | Alert, Dialog, Drawer, Loading, Message, Message Box, Notification, Popconfirm, Popover, Tooltip |
| Other | Divider, Watermark |

All components live in the flat package `cn.enaium.elementplus.components`.

## API conventions

Element Plus uses `v-model` and DOM slots. In Compose the same ideas map to explicit state:

| Element Plus | Element Plus KMP |
| --- | --- |
| `v-model="value"` | `value: T` + `onValueChange: (T) -> Unit` |
| slots | composable lambda parameters |
| CSS variables | `ElTheme` tokens |
| `size="large"` | `ElSize.Large` |
| `type="primary"` | `ElType.Primary` |
| locale keys | `ElLocale` |
| directives (`v-loading`, `v-infinite-scroll`) | state/overlay components (`ElLoading`, `ElInfiniteScroll`) |
| services (`ElMessage`, `ElNotification`, `ElMessageBox`) | service object + a `Host` wrapper |

Controlled components do not create hidden state:

- `ElInputNumber` is controlled by `Double?`.
- `ElSelect` is single-value generic; `ElSelectMultiple` handles lists.
- `ElDatePicker` uses `kotlinx.datetime.LocalDate`; `ElDateTimePicker` uses `LocalDateTime`.
- `ElTreeSelect` and `ElCascader` use path/key lists.

## Internationalization

All component copy is read from the locale installed by `ElConfigProvider`. The library bundles all 68 Element Plus locales with their official translations, covering date picker month/week names, pagination, transfer counts, upload copy, form validation messages, tour buttons and accessibility labels.

```kotlin
ElConfigProvider(locale = ElLocales.byName("fr-FR")) {
    App()
}

// Region tags fall back to the language: fr-FR -> fr, pt_BR -> pt-br.
// Unknown tags fall back to English.
```

`ElLocales.all` exposes every locale, `ElLocales.byName(...)` resolves BCP-47 tags, and `ElLocale.English` / `ElLocale.Chinese` are convenience aliases. RTL locales (`ar`, `ar-eg`, `ckb`, `fa`, `he`, `ug-cn`) also install `LayoutDirection.Rtl` through `ElConfigProvider`.

## Theming

Light colors, dark colors, the type palette and component sizes follow `element-plus/theme-chalk`:

- Primary `#409EFF`, success `#67C23A`, warning `#E6A23C`, danger/error `#F56C6C`, info `#909399`.
- Component heights: large 40dp, default 32dp, small 24dp.
- Text, border, fill, background, overlay and shadow/radius tokens are available through `LocalElTheme.current.colors` and `ElTheme`.

Dark mode is a first-class theme:

```kotlin
ElConfigProvider(theme = ElTheme.Dark) {
    AppContent()
}
```

## Platform integration

Some browser/DOM-specific features are intentionally injected:

- **Upload**: `ElUpload` renders the file list, progress, validation and actions; the host app supplies file picking and uploading through `onPickFiles` and `onUploadFile`.
- **Image**: `ElImage` and `ElImageViewer` accept `Painter`s, so any image loader can be plugged in.
- **Affix**: pass the same `ScrollState` used by the surrounding `verticalScroll`.
- **Infinite Scroll**: wraps a `LazyColumn`; the caller appends rows and toggles `loading` and `finished`.
- **Virtualized Tree/Table**: require a bounded height.
- **Anchor**: scroll-spy needs pixel `sectionOffsets`; without them it behaves as a controlled active-link list.
- **Tour**: target rectangles accept window `Rect`s, for example from `onGloballyPositioned { it.boundsInWindow() }`.
- **Message, Notification and Message Box**: mount the corresponding `Host` once.

## License and attribution

This library is MIT licensed. Element Plus is also MIT licensed. Icons are derived from `@element-plus/icons-vue`, and design tokens are derived from `element-plus/theme-chalk`. This is an independent Compose implementation of the Element Plus visual language.
