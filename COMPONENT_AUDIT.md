# Component completeness audit

Last updated: 2026-10-27

This document records the parity status of the Kotlin Multiplatform port against
`element-plus` (`packages/components`, `docs/en-US/component`, `docs/examples`)
and the deliberate platform adaptations. It replaces the earlier gap report,
whose findings were addressed by the follow-up parity pass.

## Coverage

| Category | Count | Components |
| --- | ---: | --- |
| Basic | 12 | Button, Border, Color, Container, Icon, Layout, Link, Text, Scrollbar, Space, Splitter, Typography |
| Config | 1 | Config Provider |
| Form | 25 | Autocomplete, Cascader, Checkbox, Color Picker Panel, Color Picker, Date Picker Panel, Date Picker, DateTime Picker, Form, Input, Input Number, Input Tag, Input OTP, Mention, Radio, Rate, Select, Virtualized Select, Slider, Switch, Time Picker, Time Select, Transfer, TreeSelect, Upload |
| Data | 23 | Avatar, Badge, Calendar, Card, Carousel, Collapse, Descriptions, Empty, Image, Infinite Scroll, Pagination, Progress, Result, Skeleton, Table, Virtualized Table, Tag, Timeline, Tour, Tree, Virtualized Tree, Statistic, Segmented |
| Navigation | 9 | Affix, Anchor, Backtop, Breadcrumb, Dropdown, Menu, Page Header, Steps, Tabs |
| Feedback | 10 | Alert, Dialog, Drawer, Loading, Message, Message Box, Notification, Popconfirm, Popover, Tooltip |
| Other | 2 | Divider, Watermark |

All 82 documented pages exist in the sample gallery (`SampleCatalog`), each with
runnable demos. `ElIcons` contains all 293 icons exported by
`@element-plus/icons-vue` on the original 1024-unit viewport.

Cross-cutting parity includes:

- **Theming**: light/dark palettes, semantic types, light/dark shades, fill,
  border, text, overlay/mask, radius and typography tokens.
- **Configuration**: nested `ElConfigProvider` inherits unspecified values.
  Locale, size, button/card/dialog/link/message/table defaults, global empty
  values, value-on-clear and RTL direction are consumed by the components.
- **Localization**: all 68 Element Plus locales, including date/time names,
  pagination, form validation, upload, transfer, tour and accessibility copy.
- **Overlays**: placement/offset/arrow/delay/effect/controlled visibility,
  context-menu triggers, dismissal and keyboard handling.
- **Tables**: column-driven layout, fixed/grouped headers and columns, selection,
  sorting, filtering, tree rows, lazy rows, spans, summaries, expandable rows,
  virtualized rows and the `ElAutoResizer` contract.
- **Trees/selects**: cascade checkboxes, lazy loading, filtering, drag-and-drop,
  custom node content, multiple select, remote/create options and virtualized
  dropdowns.
- **Date/time**: date, week, month, year, datetime and range pickers; typed
  editing; disabled dates/times; time constraints; shortcuts; panels; value
  formatting and localized labels.
- **Uploads**: injected picker/transport, directory picking, drag-and-drop,
  file status/progress, validation hooks, lifecycle callbacks and an imperative
  controller.

## Deliberate platform adaptations

These are API-shape changes required by Compose Multiplatform, not missing
behavior:

| Element Plus | Element Plus KMP |
| --- | --- |
| `v-model` | `value` + `onValueChange` |
| slots | composable lambda parameters |
| CSS variables and classes | `ElTheme`/`ElColors` and `Modifier` parameters |
| CSS lengths (`"50%"`, `"800"`) | `Dp` / percentage parameters where meaningful |
| DOM `tag`, `native-type`, `href`, `target` | typed semantics/handlers plus platform URI handling |
| `teleported` / `append-to` | Compose `Popup`/`Dialog` windows |
| CSS transitions | Compose animation parameters |
| directives (`v-loading`, infinite scroll) | `ElLoading` overlay and `ElInfiniteScroll` wrapper |
| services (`ElMessage`, `ElNotification`, `ElMessageBox`) | service object plus a mounted `Host` |

Web-only integration points are intentionally injected so the library stays
multiplatform:

- **Upload** delegates file picking and HTTP transport to `onPickFiles` and
  `onUploadFile` (including action/headers/data through the caller's transport).
- **Image** accepts `Painter`s, keeping image loading independent of a networking
  library.
- **Anchor** accepts pixel section offsets for scroll-spy.
- **Tour** accepts target bounds or a target registry keyed by application IDs.
- **Virtualized Table/Tree** require a bounded height, like the original
  `el-table-v2`/`el-tree-v2`.

`namespace`, `a11y`, `keyboardNavigation` and `zIndex` are retained on
`ElConfigProvider` for source parity. They do not map to CSS class names or
DOM accessibility attributes; Compose semantics and platform overlay ordering
apply instead. `zIndex` is therefore metadata rather than system-window order.

## Verification

Run from the repository root:

```bash
./gradlew :ui:jvmTest :sample:jvmTest --offline
./gradlew :ui:compileKotlinJs :ui:compileKotlinWasmJs \
  :ui:compileKotlinIosSimulatorArm64 :ui:compileAndroidMain --offline
./gradlew :sample:compileKotlinJs :sample:compileKotlinWasmJs \
  :sample:compileKotlinIosSimulatorArm64 :sample:compileAndroidMain --offline
```

The JVM tests cover interactions (button/input/select/tree/table/dialog/menu/
tooltip/message/config defaults) and render every gallery page to
`sample/build/screenshots/components`. The sample also renders light and dark
app shells. All target compilations above run in CI/local verification.
