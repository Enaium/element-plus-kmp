# Element Plus KMP

![](https://img.cdn1.vip/i/6ae02a77c3c2e_1793075831.webp)

A Kotlin Multiplatform / Compose Multiplatform port of [Element Plus](https://element-plus.org). It follows the Element Plus visual language, component anatomy and theme tokens while exposing idiomatic, state-hoisted Compose APIs.

Supported targets: **Android, iOS (arm64 + simulator), Desktop/JVM, Kotlin/JS and Kotlin/Wasm**.

**Live component gallery:** [https://enaium.github.io/element-plus-kmp/](https://enaium.github.io/element-plus-kmp/)

## Installation

```kotlin
implementation("cn.enaium.elementplus:element-plus-kmp:1.1.3")
```

The library is built on Compose Multiplatform Foundation/UI and does not require Material.

## Getting started

Wrap the application in `ElConfigProvider` to install the theme, locale, size and component defaults. Mount `ElMessage.Host`, `ElNotification.Host` and `ElMessageBox.Host` once; after that, their services can be invoked imperatively from event handlers.

Every component has a runnable, state-hoisted example under `sample`. The coverage table links directly to the corresponding sample source file.

## Component coverage

Every component name links to its sample source file.

| Category | Components |
| --- | --- |
| Basic | [Button](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/ButtonDocs.kt) · [Border](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/BorderDocs.kt) · [Color](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/ColorDocs.kt) · [Container](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/ContainerDocs.kt) · [Icon](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/IconDocs.kt) · [Layout](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/LayoutDocs.kt) · [Link](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/LinkDocs.kt) · [Text](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/TextDocs.kt) · [Scrollbar](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/ScrollbarDocs.kt) · [Space](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/SpaceDocs.kt) · [Splitter](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/SplitterDocs.kt) · [Typography](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/basic/TypographyDocs.kt) |
| Config | [Config Provider](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/config/ConfigProviderDocs.kt) |
| Form | [Autocomplete](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/AutocompleteDocs.kt) · [Cascader](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/CascaderDocs.kt) · [Checkbox](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/CheckboxDocs.kt) · [Color Picker Panel](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/ColorPickerPanelDocs.kt) · [Color Picker](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/ColorPickerDocs.kt) · [Date Picker Panel](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/DatePickerPanelDocs.kt) · [Date Picker](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/DatePickerDocs.kt) · [DateTime Picker](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/DateTimePickerDocs.kt) · [Form](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/FormDocs.kt) · [Input](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/InputDocs.kt) · [Input Number](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/InputNumberDocs.kt) · [Input Tag](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/InputTagDocs.kt) · [Input OTP](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/InputOtpDocs.kt) · [Mention](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/MentionDocs.kt) · [Radio](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/RadioDocs.kt) · [Rate](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/RateDocs.kt) · [Select](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/SelectDocs.kt) · [Virtualized Select](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/VirtualizedSelectDocs.kt) · [Slider](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/SliderDocs.kt) · [Switch](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/SwitchDocs.kt) · [Time Picker](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/TimePickerDocs.kt) · [Time Select](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/TimeSelectDocs.kt) · [Transfer](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/TransferDocs.kt) · [TreeSelect](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/TreeSelectDocs.kt) · [Upload](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/form/UploadDocs.kt) |
| Data | [Avatar](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/AvatarDocs.kt) · [Badge](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/BadgeDocs.kt) · [Calendar](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/CalendarDocs.kt) · [Card](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/CardDocs.kt) · [Carousel](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/CarouselDocs.kt) · [Collapse](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/CollapseDocs.kt) · [Descriptions](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/DescriptionsDocs.kt) · [Empty](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/EmptyDocs.kt) · [Image](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/ImageDocs.kt) · [Infinite Scroll](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/InfiniteScrollDocs.kt) · [Pagination](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/PaginationDocs.kt) · [Progress](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/ProgressDocs.kt) · [Result](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/ResultDocs.kt) · [Skeleton](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/SkeletonDocs.kt) · [Table](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/TableDocs.kt) · [Virtualized Table](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/VirtualizedTableDocs.kt) · [Tag](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/TagDocs.kt) · [Timeline](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/TimelineDocs.kt) · [Tour](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/TourDocs.kt) · [Tree](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/TreeDocs.kt) · [Virtualized Tree](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/VirtualizedTreeDocs.kt) · [Statistic](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/StatisticDocs.kt) · [Segmented](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/data/SegmentedDocs.kt) |
| Navigation | [Affix](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/AffixDocs.kt) · [Anchor](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/AnchorDocs.kt) · [Backtop](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/BacktopDocs.kt) · [Breadcrumb](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/BreadcrumbDocs.kt) · [Dropdown](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/DropdownDocs.kt) · [Menu](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/MenuDocs.kt) · [Page Header](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/PageHeaderDocs.kt) · [Steps](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/StepsDocs.kt) · [Tabs](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/navigation/TabsDocs.kt) |
| Feedback | [Alert](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/AlertDocs.kt) · [Dialog](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/DialogDocs.kt) · [Drawer](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/DrawerDocs.kt) · [Loading](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/LoadingDocs.kt) · [Message](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/MessageDocs.kt) · [Message Box](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/MessageBoxDocs.kt) · [Notification](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/NotificationDocs.kt) · [Popconfirm](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/PopconfirmDocs.kt) · [Popover](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/PopoverDocs.kt) · [Tooltip](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/feedback/TooltipDocs.kt) |
| Other | [Divider](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/other/DividerDocs.kt) · [Watermark](sample/src/commonMain/kotlin/cn/enaium/elementplus/sample/gallery/other/WatermarkDocs.kt) |

All components live in the flat package `cn.enaium.elementplus.components`.

## API conventions

Element Plus uses `v-model` and DOM slots. In Compose, the same ideas map to explicit state:

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

`ElSelect` and `ElSelectMultiple` support local/remote filtering, grouping, create-item, value-key comparisons, clear values, header/footer slots and custom option/tag/label/loading renderers. `ElVirtualizedSelect` and `ElVirtualizedSelectMultiple` expose the same behavior with a lazy dropdown. `elSelectOptions(items, value, label)` maps domain objects to the options attribute.

## Internationalization

All component copy is read from the locale installed by `ElConfigProvider`. The library bundles all 68 Element Plus locales with their official translations, covering date picker month/week names, pagination, transfer counts, upload copy, form validation messages, tour buttons and accessibility labels.

`ElLocales.all` exposes every locale, `ElLocales.byName(...)` resolves BCP-47 tags, and `ElLocale.English` / `ElLocale.Chinese` are convenience aliases. Region tags fall back to the language (`fr-FR` → `fr`, `pt_BR` → `pt-br`); unknown tags fall back to English. RTL locales (`ar`, `ar-eg`, `ckb`, `fa`, `he`, `ug-cn`) install `LayoutDirection.Rtl` through `ElConfigProvider`.

## Theming

Light colors, dark colors, the type palette and component sizes follow `element-plus/theme-chalk`:

- Primary `#409EFF`, success `#67C23A`, warning `#E6A23C`, danger/error `#F56C6C`, info `#909399`.
- Component heights: large 40dp, default 32dp, small 24dp.
- Text, border, fill, background, overlay and shadow/radius tokens are available through `LocalElTheme.current.colors` and `ElTheme`.

Dark mode is a first-class theme via `ElTheme.Dark`. Theme tokens can be overridden for a subtree with a nested `ElConfigProvider`.

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
