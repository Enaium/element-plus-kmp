package cn.enaium.elementplus.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Element Plus icon set, generated from `@element-plus/icons-vue` (MIT).
 * Every icon is drawn on the original 1024x1024 viewport and tinted by `ElIcon`.
 */
object ElIcons {
    val ArrowDown: ImageVector by lazy { buildArrowDown() }
    val ArrowUp: ImageVector by lazy { buildArrowUp() }
    val ArrowLeft: ImageVector by lazy { buildArrowLeft() }
    val ArrowRight: ImageVector by lazy { buildArrowRight() }
    val ArrowDownBold: ImageVector by lazy { buildArrowDownBold() }
    val ArrowUpBold: ImageVector by lazy { buildArrowUpBold() }
    val ArrowLeftBold: ImageVector by lazy { buildArrowLeftBold() }
    val ArrowRightBold: ImageVector by lazy { buildArrowRightBold() }
    val CaretTop: ImageVector by lazy { buildCaretTop() }
    val CaretBottom: ImageVector by lazy { buildCaretBottom() }
    val CaretLeft: ImageVector by lazy { buildCaretLeft() }
    val CaretRight: ImageVector by lazy { buildCaretRight() }
    val DArrowLeft: ImageVector by lazy { buildDArrowLeft() }
    val DArrowRight: ImageVector by lazy { buildDArrowRight() }
    val Check: ImageVector by lazy { buildCheck() }
    val Close: ImageVector by lazy { buildClose() }
    val CloseBold: ImageVector by lazy { buildCloseBold() }
    val CircleCheck: ImageVector by lazy { buildCircleCheck() }
    val CircleCheckFilled: ImageVector by lazy { buildCircleCheckFilled() }
    val CircleClose: ImageVector by lazy { buildCircleClose() }
    val CircleCloseFilled: ImageVector by lazy { buildCircleCloseFilled() }
    val Warning: ImageVector by lazy { buildWarning() }
    val WarningFilled: ImageVector by lazy { buildWarningFilled() }
    val InfoFilled: ImageVector by lazy { buildInfoFilled() }
    val SuccessFilled: ImageVector by lazy { buildSuccessFilled() }
    val Loading: ImageVector by lazy { buildLoading() }
    val Search: ImageVector by lazy { buildSearch() }
    val Plus: ImageVector by lazy { buildPlus() }
    val Minus: ImageVector by lazy { buildMinus() }
    val Remove: ImageVector by lazy { buildRemove() }
    val RemoveFilled: ImageVector by lazy { buildRemoveFilled() }
    val Delete: ImageVector by lazy { buildDelete() }
    val Edit: ImageVector by lazy { buildEdit() }
    val More: ImageVector by lazy { buildMore() }
    val MoreFilled: ImageVector by lazy { buildMoreFilled() }
    val Calendar: ImageVector by lazy { buildCalendar() }
    val Clock: ImageVector by lazy { buildClock() }
    val Timer: ImageVector by lazy { buildTimer() }
    val Star: ImageVector by lazy { buildStar() }
    val StarFilled: ImageVector by lazy { buildStarFilled() }
    val Upload: ImageVector by lazy { buildUpload() }
    val Download: ImageVector by lazy { buildDownload() }
    val Document: ImageVector by lazy { buildDocument() }
    val Folder: ImageVector by lazy { buildFolder() }
    val Picture: ImageVector by lazy { buildPicture() }
    val View: ImageVector by lazy { buildView() }
    val Hide: ImageVector by lazy { buildHide() }
    val Refresh: ImageVector by lazy { buildRefresh() }
    val Setting: ImageVector by lazy { buildSetting() }
    val User: ImageVector by lazy { buildUser() }
    val Lock: ImageVector by lazy { buildLock() }
    val Message: ImageVector by lazy { buildMessage() }
    val Bell: ImageVector by lazy { buildBell() }
    val HomeFilled: ImageVector by lazy { buildHomeFilled() }
    val Menu: ImageVector by lazy { buildMenu() }
    val Rank: ImageVector by lazy { buildRank() }
    val Sort: ImageVector by lazy { buildSort() }
    val SortUp: ImageVector by lazy { buildSortUp() }
    val SortDown: ImageVector by lazy { buildSortDown() }
    val CopyDocument: ImageVector by lazy { buildCopyDocument() }
    val ZoomIn: ImageVector by lazy { buildZoomIn() }
    val ZoomOut: ImageVector by lazy { buildZoomOut() }
    val FullScreen: ImageVector by lazy { buildFullScreen() }
    val VideoPlay: ImageVector by lazy { buildVideoPlay() }
    val VideoPause: ImageVector by lazy { buildVideoPause() }
    val Back: ImageVector by lazy { buildBack() }
    val Right: ImageVector by lazy { buildRight() }
    val Bottom: ImageVector by lazy { buildBottom() }
    val Top: ImageVector by lazy { buildTop() }
    val Select: ImageVector by lazy { buildSelect() }
    val SemiSelect: ImageVector by lazy { buildSemiSelect() }
    val SwitchButton: ImageVector by lazy { buildSwitchButton() }
    val Location: ImageVector by lazy { buildLocation() }
    val Link: ImageVector by lazy { buildLink() }
    val CirclePlus: ImageVector by lazy { buildCirclePlus() }
    val Filter: ImageVector by lazy { buildFilter() }
    val Operation: ImageVector by lazy { buildOperation() }
    val Aim: ImageVector by lazy { buildAim() }
    val MagicStick: ImageVector by lazy { buildMagicStick() }
    val Brush: ImageVector by lazy { buildBrush() }
    val Scissor: ImageVector by lazy { buildScissor() }
    val Crop: ImageVector by lazy { buildCrop() }
    val Film: ImageVector by lazy { buildFilm() }
    val Headset: ImageVector by lazy { buildHeadset() }
    val Mic: ImageVector by lazy { buildMic() }
    val Mute: ImageVector by lazy { buildMute() }
    val Sunny: ImageVector by lazy { buildSunny() }
    val Moon: ImageVector by lazy { buildMoon() }
    val Cloudy: ImageVector by lazy { buildCloudy() }
    val Lightning: ImageVector by lazy { buildLightning() }
    val Trophy: ImageVector by lazy { buildTrophy() }
    val Medal: ImageVector by lazy { buildMedal() }
    val Present: ImageVector by lazy { buildPresent() }
    val ShoppingCart: ImageVector by lazy { buildShoppingCart() }
    val Goods: ImageVector by lazy { buildGoods() }
    val Money: ImageVector by lazy { buildMoney() }
    val Wallet: ImageVector by lazy { buildWallet() }
    val Ticket: ImageVector by lazy { buildTicket() }
    val Discount: ImageVector by lazy { buildDiscount() }
    val Ship: ImageVector by lazy { buildShip() }
    val Van: ImageVector by lazy { buildVan() }
    val Bicycle: ImageVector by lazy { buildBicycle() }
    val Checked: ImageVector by lazy { buildChecked() }
    val Finished: ImageVector by lazy { buildFinished() }
    val Flag: ImageVector by lazy { buildFlag() }
    val List: ImageVector by lazy { buildList() }
    val Grid: ImageVector by lazy { buildGrid() }
    val ChatDotRound: ImageVector by lazy { buildChatDotRound() }
    val Phone: ImageVector by lazy { buildPhone() }
    val Promotion: ImageVector by lazy { buildPromotion() }
    val Compass: ImageVector by lazy { buildCompass() }
    val TrendCharts: ImageVector by lazy { buildTrendCharts() }
    val Histogram: ImageVector by lazy { buildHistogram() }
    val PieChart: ImageVector by lazy { buildPieChart() }
    val DataLine: ImageVector by lazy { buildDataLine() }
    val DataAnalysis: ImageVector by lazy { buildDataAnalysis() }
    val Monitor: ImageVector by lazy { buildMonitor() }
    val Iphone: ImageVector by lazy { buildIphone() }
    val Cellphone: ImageVector by lazy { buildCellphone() }
}

private fun buildArrowDown(): ImageVector = ImageVector.Builder(
    name = "ArrowDown", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M831.872 340.864 512 652.672 192.128 340.864a30.59 30.59 0 0 0-42.752 0 29.12 29.12 0 0 0 0 41.6L489.664 714.24a32 32 0 0 0 44.672 0l340.288-331.712a29.12 29.12 0 0 0 0-41.728 30.59 30.59 0 0 0-42.752 0z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowUp(): ImageVector = ImageVector.Builder(
    name = "ArrowUp", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m488.832 344.32-339.84 356.672a32 32 0 0 0 0 44.16l.384.384a29.44 29.44 0 0 0 42.688 0l320-335.872 319.872 335.872a29.44 29.44 0 0 0 42.688 0l.384-.384a32 32 0 0 0 0-44.16L535.168 344.32a32 32 0 0 0-46.336 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowLeft(): ImageVector = ImageVector.Builder(
    name = "ArrowLeft", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M609.408 149.376 277.76 489.6a32 32 0 0 0 0 44.672l331.648 340.352a29.12 29.12 0 0 0 41.728 0 30.59 30.59 0 0 0 0-42.752L339.264 511.936l311.872-319.872a30.59 30.59 0 0 0 0-42.688 29.12 29.12 0 0 0-41.728 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowRight(): ImageVector = ImageVector.Builder(
    name = "ArrowRight", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M340.864 149.312a30.59 30.59 0 0 0 0 42.752L652.736 512 340.864 831.872a30.59 30.59 0 0 0 0 42.752 29.12 29.12 0 0 0 41.728 0L714.24 534.336a32 32 0 0 0 0-44.672L382.592 149.376a29.12 29.12 0 0 0-41.728 0z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowDownBold(): ImageVector = ImageVector.Builder(
    name = "ArrowDownBold", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M104.704 338.752a64 64 0 0 1 90.496 0l316.8 316.8 316.8-316.8a64 64 0 0 1 90.496 90.496L557.248 791.296a64 64 0 0 1-90.496 0L104.704 429.248a64 64 0 0 1 0-90.496").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowUpBold(): ImageVector = ImageVector.Builder(
    name = "ArrowUpBold", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M104.704 685.248a64 64 0 0 0 90.496 0l316.8-316.8 316.8 316.8a64 64 0 0 0 90.496-90.496L557.248 232.704a64 64 0 0 0-90.496 0L104.704 594.752a64 64 0 0 0 0 90.496").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowLeftBold(): ImageVector = ImageVector.Builder(
    name = "ArrowLeftBold", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M685.248 104.704a64 64 0 0 1 0 90.496L368.448 512l316.8 316.8a64 64 0 0 1-90.496 90.496L232.704 557.248a64 64 0 0 1 0-90.496l362.048-362.048a64 64 0 0 1 90.496 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildArrowRightBold(): ImageVector = ImageVector.Builder(
    name = "ArrowRightBold", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M338.752 104.704a64 64 0 0 0 0 90.496l316.8 316.8-316.8 316.8a64 64 0 0 0 90.496 90.496l362.048-362.048a64 64 0 0 0 0-90.496L429.248 104.704a64 64 0 0 0-90.496 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCaretTop(): ImageVector = ImageVector.Builder(
    name = "CaretTop", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 320 192 704h639.936z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCaretBottom(): ImageVector = ImageVector.Builder(
    name = "CaretBottom", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m192 384 320 384 320-384z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCaretLeft(): ImageVector = ImageVector.Builder(
    name = "CaretLeft", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M672 192 288 511.936 672 832z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCaretRight(): ImageVector = ImageVector.Builder(
    name = "CaretRight", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M384 192v640l384-320.064z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDArrowLeft(): ImageVector = ImageVector.Builder(
    name = "DArrowLeft", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M529.408 149.376a29.12 29.12 0 0 1 41.728 0 30.59 30.59 0 0 1 0 42.688L259.264 511.936l311.872 319.936a30.59 30.59 0 0 1-.512 43.264 29.12 29.12 0 0 1-41.216-.512L197.76 534.272a32 32 0 0 1 0-44.672zm256 0a29.12 29.12 0 0 1 41.728 0 30.59 30.59 0 0 1 0 42.688L515.264 511.936l311.872 319.936a30.59 30.59 0 0 1-.512 43.264 29.12 29.12 0 0 1-41.216-.512L453.76 534.272a32 32 0 0 1 0-44.672z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDArrowRight(): ImageVector = ImageVector.Builder(
    name = "DArrowRight", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M452.864 149.312a29.12 29.12 0 0 1 41.728.064L826.24 489.664a32 32 0 0 1 0 44.672L494.592 874.624a29.12 29.12 0 0 1-41.728 0 30.59 30.59 0 0 1 0-42.752L764.736 512 452.864 192a30.59 30.59 0 0 1 0-42.688m-256 0a29.12 29.12 0 0 1 41.728.064L570.24 489.664a32 32 0 0 1 0 44.672L238.592 874.624a29.12 29.12 0 0 1-41.728 0 30.59 30.59 0 0 1 0-42.752L508.736 512 196.864 192a30.59 30.59 0 0 1 0-42.688").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCheck(): ImageVector = ImageVector.Builder(
    name = "Check", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M406.656 706.944 195.84 496.256a32 32 0 1 0-45.248 45.248l256 256 512-512a32 32 0 0 0-45.248-45.248L406.592 706.944z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildClose(): ImageVector = ImageVector.Builder(
    name = "Close", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCloseBold(): ImageVector = ImageVector.Builder(
    name = "CloseBold", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M195.2 195.2a64 64 0 0 1 90.496 0L512 421.504 738.304 195.2a64 64 0 0 1 90.496 90.496L602.496 512 828.8 738.304a64 64 0 0 1-90.496 90.496L512 602.496 285.696 828.8a64 64 0 0 1-90.496-90.496L421.504 512 195.2 285.696a64 64 0 0 1 0-90.496").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCircleCheck(): ImageVector = ImageVector.Builder(
    name = "CircleCheck", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M745.344 361.344a32 32 0 0 1 45.312 45.312l-288 288a32 32 0 0 1-45.312 0l-160-160a32 32 0 1 1 45.312-45.312L480 626.752z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCircleCheckFilled(): ImageVector = ImageVector.Builder(
    name = "CircleCheckFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m-55.808 536.384-99.52-99.584a38.4 38.4 0 1 0-54.336 54.336l126.72 126.72a38.27 38.27 0 0 0 54.336 0l262.4-262.464a38.4 38.4 0 1 0-54.272-54.336z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCircleClose(): ImageVector = ImageVector.Builder(
    name = "CircleClose", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m466.752 512-90.496-90.496a32 32 0 0 1 45.248-45.248L512 466.752l90.496-90.496a32 32 0 1 1 45.248 45.248L557.248 512l90.496 90.496a32 32 0 1 1-45.248 45.248L512 557.248l-90.496 90.496a32 32 0 0 1-45.248-45.248z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCircleCloseFilled(): ImageVector = ImageVector.Builder(
    name = "CircleCloseFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m0 393.664L407.936 353.6a38.4 38.4 0 1 0-54.336 54.336L457.664 512 353.6 616.064a38.4 38.4 0 1 0 54.336 54.336L512 566.336 616.064 670.4a38.4 38.4 0 1 0 54.336-54.336L566.336 512 670.4 407.936a38.4 38.4 0 1 0-54.336-54.336z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildWarning(): ImageVector = ImageVector.Builder(
    name = "Warning", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m0 832a384 384 0 0 0 0-768 384 384 0 0 0 0 768m48-176a48 48 0 1 1-96 0 48 48 0 0 1 96 0m-48-464a32 32 0 0 1 32 32v288a32 32 0 0 1-64 0V288a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildWarningFilled(): ImageVector = ImageVector.Builder(
    name = "WarningFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m0 192a58.43 58.43 0 0 0-58.24 63.744l23.36 256.384a35.072 35.072 0 0 0 69.76 0l23.296-256.384A58.43 58.43 0 0 0 512 256m0 512a51.2 51.2 0 1 0 0-102.4 51.2 51.2 0 0 0 0 102.4").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildInfoFilled(): ImageVector = ImageVector.Builder(
    name = "InfoFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896.064A448 448 0 0 1 512 64m67.2 275.072c33.28 0 60.288-23.104 60.288-57.344s-27.072-57.344-60.288-57.344c-33.28 0-60.16 23.104-60.16 57.344s26.88 57.344 60.16 57.344M590.912 699.2c0-6.848 2.368-24.64 1.024-34.752l-52.608 60.544c-10.88 11.456-24.512 19.392-30.912 17.28a12.99 12.99 0 0 1-8.256-14.72l87.68-276.992c7.168-35.136-12.544-67.2-54.336-71.296-44.096 0-108.992 44.736-148.48 101.504 0 6.784-1.28 23.68.064 33.792l52.544-60.608c10.88-11.328 23.552-19.328 29.952-17.152a12.8 12.8 0 0 1 7.808 16.128L388.48 728.576c-10.048 32.256 8.96 63.872 55.04 71.04 67.84 0 107.904-43.648 147.456-100.416z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSuccessFilled(): ImageVector = ImageVector.Builder(
    name = "SuccessFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m-55.808 536.384-99.52-99.584a38.4 38.4 0 1 0-54.336 54.336l126.72 126.72a38.27 38.27 0 0 0 54.336 0l262.4-262.464a38.4 38.4 0 1 0-54.272-54.336z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildLoading(): ImageVector = ImageVector.Builder(
    name = "Loading", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32m0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32m448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32m-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32M195.2 195.2a32 32 0 0 1 45.248 0L376.32 331.008a32 32 0 0 1-45.248 45.248L195.2 240.448a32 32 0 0 1 0-45.248m452.544 452.544a32 32 0 0 1 45.248 0L828.8 783.552a32 32 0 0 1-45.248 45.248L647.744 692.992a32 32 0 0 1 0-45.248M828.8 195.264a32 32 0 0 1 0 45.184L692.992 376.32a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0m-452.544 452.48a32 32 0 0 1 0 45.248L240.448 828.8a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSearch(): ImageVector = ImageVector.Builder(
    name = "Search", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m795.904 750.72 124.992 124.928a32 32 0 0 1-45.248 45.248L750.656 795.904a416 416 0 1 1 45.248-45.248zM480 832a352 352 0 1 0 0-704 352 352 0 0 0 0 704").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildPlus(): ImageVector = ImageVector.Builder(
    name = "Plus", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M480 480V128a32 32 0 0 1 64 0v352h352a32 32 0 1 1 0 64H544v352a32 32 0 1 1-64 0V544H128a32 32 0 0 1 0-64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMinus(): ImageVector = ImageVector.Builder(
    name = "Minus", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128 544h768a32 32 0 1 0 0-64H128a32 32 0 0 0 0 64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildRemove(): ImageVector = ImageVector.Builder(
    name = "Remove", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M352 480h320a32 32 0 1 1 0 64H352a32 32 0 0 1 0-64").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildRemoveFilled(): ImageVector = ImageVector.Builder(
    name = "RemoveFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896M288 512a38.4 38.4 0 0 0 38.4 38.4h371.2a38.4 38.4 0 0 0 0-76.8H326.4A38.4 38.4 0 0 0 288 512").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDelete(): ImageVector = ImageVector.Builder(
    name = "Delete", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M160 256H96a32 32 0 0 1 0-64h256V95.936a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32V192h256a32 32 0 1 1 0 64h-64v672a32 32 0 0 1-32 32H192a32 32 0 0 1-32-32zm448-64v-64H416v64zM224 896h576V256H224zm192-128a32 32 0 0 1-32-32V416a32 32 0 0 1 64 0v320a32 32 0 0 1-32 32m192 0a32 32 0 0 1-32-32V416a32 32 0 0 1 64 0v320a32 32 0 0 1-32 32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildEdit(): ImageVector = ImageVector.Builder(
    name = "Edit", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M832 512a32 32 0 1 1 64 0v352a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V160a32 32 0 0 1 32-32h352a32 32 0 0 1 0 64H192v640h640z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("m469.952 554.24 52.8-7.552L847.104 222.4a32 32 0 1 0-45.248-45.248L477.44 501.44l-7.552 52.8zm422.4-422.4a96 96 0 0 1 0 135.808l-331.84 331.84a32 32 0 0 1-18.112 9.088L436.8 623.68a32 32 0 0 1-36.224-36.224l15.104-105.6a32 32 0 0 1 9.024-18.112l331.904-331.84a96 96 0 0 1 135.744 0z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMore(): ImageVector = ImageVector.Builder(
    name = "More", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M176 416a112 112 0 1 0 0 224 112 112 0 0 0 0-224m0 64a48 48 0 1 1 0 96 48 48 0 0 1 0-96m336-64a112 112 0 1 1 0 224 112 112 0 0 1 0-224m0 64a48 48 0 1 0 0 96 48 48 0 0 0 0-96m336-64a112 112 0 1 1 0 224 112 112 0 0 1 0-224m0 64a48 48 0 1 0 0 96 48 48 0 0 0 0-96").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMoreFilled(): ImageVector = ImageVector.Builder(
    name = "MoreFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M176 416a112 112 0 1 1 0 224 112 112 0 0 1 0-224m336 0a112 112 0 1 1 0 224 112 112 0 0 1 0-224m336 0a112 112 0 1 1 0 224 112 112 0 0 1 0-224").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCalendar(): ImageVector = ImageVector.Builder(
    name = "Calendar", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128 384v512h768V192H768v32a32 32 0 1 1-64 0v-32H320v32a32 32 0 0 1-64 0v-32H128v128h768v64zm192-256h384V96a32 32 0 1 1 64 0v32h160a32 32 0 0 1 32 32v768a32 32 0 0 1-32 32H96a32 32 0 0 1-32-32V160a32 32 0 0 1 32-32h160V96a32 32 0 0 1 64 0zm-32 384h64a32 32 0 0 1 0 64h-64a32 32 0 0 1 0-64m0 192h64a32 32 0 1 1 0 64h-64a32 32 0 1 1 0-64m192-192h64a32 32 0 0 1 0 64h-64a32 32 0 0 1 0-64m0 192h64a32 32 0 1 1 0 64h-64a32 32 0 1 1 0-64m192-192h64a32 32 0 1 1 0 64h-64a32 32 0 1 1 0-64m0 192h64a32 32 0 1 1 0 64h-64a32 32 0 1 1 0-64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildClock(): ImageVector = ImageVector.Builder(
    name = "Clock", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M480 256a32 32 0 0 1 32 32v256a32 32 0 0 1-64 0V288a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M480 512h256q32 0 32 32t-32 32H480q-32 0-32-32t32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildTimer(): ImageVector = ImageVector.Builder(
    name = "Timer", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 896a320 320 0 1 0 0-640 320 320 0 0 0 0 640m0 64a384 384 0 1 1 0-768 384 384 0 0 1 0 768").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 320a32 32 0 0 1 32 32l-.512 224a32 32 0 1 1-64 0L480 352a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M448 576a64 64 0 1 0 128 0 64 64 0 1 0-128 0m96-448v128h-64V128h-96a32 32 0 0 1 0-64h256a32 32 0 1 1 0 64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildStar(): ImageVector = ImageVector.Builder(
    name = "Star", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m512 747.84 228.16 119.936a6.4 6.4 0 0 0 9.28-6.72l-43.52-254.08 184.512-179.904a6.4 6.4 0 0 0-3.52-10.88l-255.104-37.12L517.76 147.904a6.4 6.4 0 0 0-11.52 0L392.192 379.072l-255.104 37.12a6.4 6.4 0 0 0-3.52 10.88L318.08 606.976l-43.584 254.08a6.4 6.4 0 0 0 9.28 6.72zM313.6 924.48a70.4 70.4 0 0 1-102.144-74.24l37.888-220.928L88.96 472.96A70.4 70.4 0 0 1 128 352.896l221.76-32.256 99.2-200.96a70.4 70.4 0 0 1 126.208 0l99.2 200.96 221.824 32.256a70.4 70.4 0 0 1 39.04 120.064L774.72 629.376l37.888 220.928a70.4 70.4 0 0 1-102.144 74.24L512 820.096l-198.4 104.32z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildStarFilled(): ImageVector = ImageVector.Builder(
    name = "StarFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M313.6 924.48a70.4 70.4 0 0 1-74.152-5.365 70.4 70.4 0 0 1-27.992-68.875l37.888-220.928L88.96 472.96a70.4 70.4 0 0 1 3.788-104.225A70.4 70.4 0 0 1 128 352.896l221.76-32.256 99.2-200.96a70.4 70.4 0 0 1 100.246-28.595 70.4 70.4 0 0 1 25.962 28.595l99.2 200.96 221.824 32.256a70.4 70.4 0 0 1 39.04 120.064L774.72 629.376l37.888 220.928a70.4 70.4 0 0 1-102.144 74.24L512 820.096l-198.4 104.32z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildUpload(): ImageVector = ImageVector.Builder(
    name = "Upload", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M160 832h704a32 32 0 1 1 0 64H160a32 32 0 1 1 0-64m384-578.304V704h-64V247.296L237.248 490.048 192 444.8 508.8 128l316.8 316.8-45.312 45.248z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDownload(): ImageVector = ImageVector.Builder(
    name = "Download", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M160 832h704a32 32 0 1 1 0 64H160a32 32 0 1 1 0-64m384-253.696 236.288-236.352 45.248 45.248L508.8 704 192 387.2l45.248-45.248L480 584.704V128h64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDocument(): ImageVector = ImageVector.Builder(
    name = "Document", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M832 384H576V128H192v768h640zm-26.496-64L640 154.496V320zM160 64h480l256 256v608a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32m160 448h384v64H320zm0-192h160v64H320zm0 384h384v64H320z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildFolder(): ImageVector = ImageVector.Builder(
    name = "Folder", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128 192v640h768V320H485.76L357.504 192zm-32-64h287.872l128.384 128H928a32 32 0 0 1 32 32v576a32 32 0 0 1-32 32H96a32 32 0 0 1-32-32V160a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildPicture(): ImageVector = ImageVector.Builder(
    name = "Picture", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M160 160v704h704V160zm-32-64h768a32 32 0 0 1 32 32v768a32 32 0 0 1-32 32H128a32 32 0 0 1-32-32V128a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M384 288q64 0 64 64t-64 64-64-64 64-64M185.408 876.992l-50.816-38.912L350.72 556.032a96 96 0 0 1 134.592-17.856l1.856 1.472 122.88 99.136a32 32 0 0 0 44.992-4.864l216-269.888 49.92 39.936-215.808 269.824-.256.32a96 96 0 0 1-135.04 14.464l-122.88-99.072-.64-.512a32 32 0 0 0-44.8 5.952z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildView(): ImageVector = ImageVector.Builder(
    name = "View", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 160c320 0 512 352 512 352S832 864 512 864 0 512 0 512s192-352 512-352m0 64c-225.28 0-384.128 208.064-436.8 288 52.608 79.872 211.456 288 436.8 288 225.28 0 384.128-208.064 436.8-288-52.608-79.872-211.456-288-436.8-288m0 64a224 224 0 1 1 0 448 224 224 0 0 1 0-448m0 64a160.19 160.19 0 0 0-160 160c0 88.192 71.744 160 160 160s160-71.808 160-160-71.744-160-160-160").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildHide(): ImageVector = ImageVector.Builder(
    name = "Hide", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M876.8 156.8c0-9.6-3.2-16-9.6-22.4s-12.8-9.6-22.4-9.6-16 3.2-22.4 9.6L736 220.8c-64-32-137.6-51.2-224-60.8-160 16-288 73.6-377.6 176S0 496 0 512s48 73.6 134.4 176c22.4 25.6 44.8 48 73.6 67.2l-86.4 89.6c-6.4 6.4-9.6 12.8-9.6 22.4s3.2 16 9.6 22.4 12.8 9.6 22.4 9.6 16-3.2 22.4-9.6l704-710.4c3.2-6.4 6.4-12.8 6.4-22.4m-646.4 528Q115.2 579.2 76.8 512q43.2-72 153.6-172.8C304 272 400 230.4 512 224c64 3.2 124.8 19.2 176 44.8l-54.4 54.4C598.4 300.8 560 288 512 288c-64 0-115.2 22.4-160 64s-64 96-64 160c0 48 12.8 89.6 35.2 124.8L256 707.2c-9.6-6.4-19.2-16-25.6-22.4m140.8-96Q352 555.2 352 512c0-44.8 16-83.2 48-112s67.2-48 112-48c28.8 0 54.4 6.4 73.6 19.2zM889.599 336c-12.8-16-28.8-28.8-41.6-41.6l-48 48c73.6 67.2 124.8 124.8 150.4 169.6q-43.2 72-153.6 172.8c-73.6 67.2-172.8 108.8-284.8 115.2-51.2-3.2-99.2-12.8-140.8-28.8l-48 48c57.6 22.4 118.4 38.4 188.8 44.8 160-16 288-73.6 377.6-176S1024 528 1024 512s-48.001-73.6-134.401-176").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M511.998 672c-12.8 0-25.6-3.2-38.4-6.4l-51.2 51.2c28.8 12.8 57.6 19.2 89.6 19.2 64 0 115.2-22.4 160-64 41.6-41.6 64-96 64-160 0-32-6.4-64-19.2-89.6l-51.2 51.2c3.2 12.8 6.4 25.6 6.4 38.4 0 44.8-16 83.2-48 112s-67.2 48-112 48").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildRefresh(): ImageVector = ImageVector.Builder(
    name = "Refresh", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M771.776 794.88A384 384 0 0 1 128 512h64a320 320 0 0 0 555.712 216.448H654.72a32 32 0 1 1 0-64h149.056a32 32 0 0 1 32 32v148.928a32 32 0 1 1-64 0v-50.56zM276.288 295.616h92.992a32 32 0 0 1 0 64H220.16a32 32 0 0 1-32-32V178.56a32 32 0 0 1 64 0v50.56A384 384 0 0 1 896.128 512h-64a320 320 0 0 0-555.776-216.384z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSetting(): ImageVector = ImageVector.Builder(
    name = "Setting", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M600.704 64a32 32 0 0 1 30.464 22.208l35.2 109.376c14.784 7.232 28.928 15.36 42.432 24.512l112.384-24.192a32 32 0 0 1 34.432 15.36L944.32 364.8a32 32 0 0 1-4.032 37.504l-77.12 85.12a357 357 0 0 1 0 49.024l77.12 85.248a32 32 0 0 1 4.032 37.504l-88.704 153.6a32 32 0 0 1-34.432 15.296L708.8 803.904c-13.44 9.088-27.648 17.28-42.368 24.512l-35.264 109.376A32 32 0 0 1 600.704 960H423.296a32 32 0 0 1-30.464-22.208L357.696 828.48a352 352 0 0 1-42.56-24.64l-112.32 24.256a32 32 0 0 1-34.432-15.36L79.68 659.2a32 32 0 0 1 4.032-37.504l77.12-85.248a357 357 0 0 1 0-48.896l-77.12-85.248A32 32 0 0 1 79.68 364.8l88.704-153.6a32 32 0 0 1 34.432-15.296l112.32 24.256c13.568-9.152 27.776-17.408 42.56-24.64l35.2-109.312A32 32 0 0 1 423.232 64H600.64zm-23.424 64H446.72l-36.352 113.088-24.512 11.968a294 294 0 0 0-34.816 20.096l-22.656 15.36-116.224-25.088-65.28 113.152 79.68 88.192-1.92 27.136a293 293 0 0 0 0 40.192l1.92 27.136-79.808 88.192 65.344 113.152 116.224-25.024 22.656 15.296a294 294 0 0 0 34.816 20.096l24.512 11.968L446.72 896h130.688l36.48-113.152 24.448-11.904a288 288 0 0 0 34.752-20.096l22.592-15.296 116.288 25.024 65.28-113.152-79.744-88.192 1.92-27.136a293 293 0 0 0 0-40.256l-1.92-27.136 79.808-88.128-65.344-113.152-116.288 24.96-22.592-15.232a288 288 0 0 0-34.752-20.096l-24.448-11.904L577.344 128zM512 320a192 192 0 1 1 0 384 192 192 0 0 1 0-384m0 64a128 128 0 1 0 0 256 128 128 0 0 0 0-256").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildUser(): ImageVector = ImageVector.Builder(
    name = "User", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 512a192 192 0 1 0 0-384 192 192 0 0 0 0 384m0 64a256 256 0 1 1 0-512 256 256 0 0 1 0 512m320 320v-96a96 96 0 0 0-96-96H288a96 96 0 0 0-96 96v96a32 32 0 1 1-64 0v-96a160 160 0 0 1 160-160h448a160 160 0 0 1 160 160v96a32 32 0 1 1-64 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildLock(): ImageVector = ImageVector.Builder(
    name = "Lock", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M224 448a32 32 0 0 0-32 32v384a32 32 0 0 0 32 32h576a32 32 0 0 0 32-32V480a32 32 0 0 0-32-32zm0-64h576a96 96 0 0 1 96 96v384a96 96 0 0 1-96 96H224a96 96 0 0 1-96-96V480a96 96 0 0 1 96-96").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 544a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V576a32 32 0 0 1 32-32m192-160v-64a192 192 0 1 0-384 0v64zM512 64a256 256 0 0 1 256 256v128H256V320A256 256 0 0 1 512 64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMessage(): ImageVector = ImageVector.Builder(
    name = "Message", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128 224v512a64 64 0 0 0 64 64h640a64 64 0 0 0 64-64V224zm0-64h768a64 64 0 0 1 64 64v512a128 128 0 0 1-128 128H192A128 128 0 0 1 64 736V224a64 64 0 0 1 64-64").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M904 224 656.512 506.88a192 192 0 0 1-289.024 0L120 224zm-698.944 0 210.56 240.704a128 128 0 0 0 192.704 0L818.944 224z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildBell(): ImageVector = ImageVector.Builder(
    name = "Bell", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a64 64 0 0 1 64 64v64H448v-64a64 64 0 0 1 64-64").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M256 768h512V448a256 256 0 1 0-512 0zm256-640a320 320 0 0 1 320 320v384H192V448a320 320 0 0 1 320-320").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M96 768h832q32 0 32 32t-32 32H96q-32 0-32-32t32-32m352 128h128a64 64 0 0 1-128 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildHomeFilled(): ImageVector = ImageVector.Builder(
    name = "HomeFilled", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 128 128 447.936V896h255.936V640H640v256h255.936V447.936z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMenu(): ImageVector = ImageVector.Builder(
    name = "Menu", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M160 448a32 32 0 0 1-32-32V160.064a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32V416a32 32 0 0 1-32 32zm448 0a32 32 0 0 1-32-32V160.064a32 32 0 0 1 32-32h255.936a32 32 0 0 1 32 32V416a32 32 0 0 1-32 32zM160 896a32 32 0 0 1-32-32V608a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32v256a32 32 0 0 1-32 32zm448 0a32 32 0 0 1-32-32V608a32 32 0 0 1 32-32h255.936a32 32 0 0 1 32 32v256a32 32 0 0 1-32 32z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildRank(): ImageVector = ImageVector.Builder(
    name = "Rank", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m186.496 544 41.408 41.344a32 32 0 1 1-45.248 45.312l-96-96a32 32 0 0 1 0-45.312l96-96a32 32 0 1 1 45.248 45.312L186.496 480h290.816V186.432l-41.472 41.472a32 32 0 1 1-45.248-45.184l96-96.128a32 32 0 0 1 45.312 0l96 96.064a32 32 0 0 1-45.248 45.184l-41.344-41.28V480H832l-41.344-41.344a32 32 0 0 1 45.248-45.312l96 96a32 32 0 0 1 0 45.312l-96 96a32 32 0 0 1-45.248-45.312L832 544H541.312v293.44l41.344-41.28a32 32 0 1 1 45.248 45.248l-96 96a32 32 0 0 1-45.312 0l-96-96a32 32 0 1 1 45.312-45.248l41.408 41.408V544z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSort(): ImageVector = ImageVector.Builder(
    name = "Sort", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M384 96a32 32 0 0 1 64 0v786.752a32 32 0 0 1-54.592 22.656L95.936 608a32 32 0 0 1 0-45.312h.128a32 32 0 0 1 45.184 0L384 805.632zm192 45.248a32 32 0 0 1 54.592-22.592L928.064 416a32 32 0 0 1 0 45.312h-.128a32 32 0 0 1-45.184 0L640 218.496V928a32 32 0 1 1-64 0z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSortUp(): ImageVector = ImageVector.Builder(
    name = "SortUp", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M384 141.248V928a32 32 0 1 0 64 0V218.56l242.688 242.688A32 32 0 1 0 736 416L438.592 118.656A32 32 0 0 0 384 141.248").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSortDown(): ImageVector = ImageVector.Builder(
    name = "SortDown", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M576 96v709.568L333.312 562.816A32 32 0 1 0 288 608l297.408 297.344A32 32 0 0 0 640 882.688V96a32 32 0 0 0-64 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCopyDocument(): ImageVector = ImageVector.Builder(
    name = "CopyDocument", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M768 832a128 128 0 0 1-128 128H192A128 128 0 0 1 64 832V384a128 128 0 0 1 128-128v64a64 64 0 0 0-64 64v448a64 64 0 0 0 64 64h448a64 64 0 0 0 64-64z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M384 128a64 64 0 0 0-64 64v448a64 64 0 0 0 64 64h448a64 64 0 0 0 64-64V192a64 64 0 0 0-64-64zm0-64h448a128 128 0 0 1 128 128v448a128 128 0 0 1-128 128H384a128 128 0 0 1-128-128V192A128 128 0 0 1 384 64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildZoomIn(): ImageVector = ImageVector.Builder(
    name = "ZoomIn", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m795.904 750.72 124.992 124.928a32 32 0 0 1-45.248 45.248L750.656 795.904a416 416 0 1 1 45.248-45.248zM480 832a352 352 0 1 0 0-704 352 352 0 0 0 0 704m-32-384v-96a32 32 0 0 1 64 0v96h96a32 32 0 0 1 0 64h-96v96a32 32 0 0 1-64 0v-96h-96a32 32 0 0 1 0-64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildZoomOut(): ImageVector = ImageVector.Builder(
    name = "ZoomOut", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m795.904 750.72 124.992 124.928a32 32 0 0 1-45.248 45.248L750.656 795.904a416 416 0 1 1 45.248-45.248zM480 832a352 352 0 1 0 0-704 352 352 0 0 0 0 704M352 448h256a32 32 0 0 1 0 64H352a32 32 0 0 1 0-64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildFullScreen(): ImageVector = ImageVector.Builder(
    name = "FullScreen", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m160 96.064 192 .192a32 32 0 0 1 0 64l-192-.192V352a32 32 0 0 1-64 0V96h64zm0 831.872V928H96V672a32 32 0 1 1 64 0v191.936l192-.192a32 32 0 1 1 0 64zM864 96.064V96h64v256a32 32 0 1 1-64 0V160.064l-192 .192a32 32 0 1 1 0-64zm0 831.872-192-.192a32 32 0 0 1 0-64l192 .192V672a32 32 0 1 1 64 0v256h-64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildVideoPlay(): ImageVector = ImageVector.Builder(
    name = "VideoPlay", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m0 832a384 384 0 0 0 0-768 384 384 0 0 0 0 768m-48-247.616L668.608 512 464 375.616zm10.624-342.656 249.472 166.336a48 48 0 0 1 0 79.872L474.624 718.272A48 48 0 0 1 400 678.336V345.6a48 48 0 0 1 74.624-39.936z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildVideoPause(): ImageVector = ImageVector.Builder(
    name = "VideoPause", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m0 832a384 384 0 0 0 0-768 384 384 0 0 0 0 768m-96-544q32 0 32 32v256q0 32-32 32t-32-32V384q0-32 32-32m192 0q32 0 32 32v256q0 32-32 32t-32-32V384q0-32 32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildBack(): ImageVector = ImageVector.Builder(
    name = "Back", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M224 480h640a32 32 0 1 1 0 64H224a32 32 0 0 1 0-64").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("m237.248 512 265.408 265.344a32 32 0 0 1-45.312 45.312l-288-288a32 32 0 0 1 0-45.312l288-288a32 32 0 1 1 45.312 45.312z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildRight(): ImageVector = ImageVector.Builder(
    name = "Right", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M754.752 480H160a32 32 0 1 0 0 64h594.752L521.344 777.344a32 32 0 0 0 45.312 45.312l288-288a32 32 0 0 0 0-45.312l-288-288a32 32 0 1 0-45.312 45.312z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildBottom(): ImageVector = ImageVector.Builder(
    name = "Bottom", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M544 805.888V168a32 32 0 1 0-64 0v637.888L246.656 557.952a30.72 30.72 0 0 0-45.312 0 35.52 35.52 0 0 0 0 48.064l288 306.048a30.72 30.72 0 0 0 45.312 0l288-306.048a35.52 35.52 0 0 0 0-48 30.72 30.72 0 0 0-45.312 0L544 805.824z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildTop(): ImageVector = ImageVector.Builder(
    name = "Top", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M572.235 205.282v600.365a30.118 30.118 0 1 1-60.235 0V205.282L292.382 438.633a28.913 28.913 0 0 1-42.646 0 33.43 33.43 0 0 1 0-45.236l271.058-288.045a28.913 28.913 0 0 1 42.647 0L834.5 393.397a33.43 33.43 0 0 1 0 45.176 28.913 28.913 0 0 1-42.647 0l-219.618-233.23z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSelect(): ImageVector = ImageVector.Builder(
    name = "Select", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M77.248 415.04a64 64 0 0 1 90.496 0l226.304 226.304L846.528 188.8a64 64 0 1 1 90.56 90.496l-543.04 543.04-316.8-316.8a64 64 0 0 1 0-90.496").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSemiSelect(): ImageVector = ImageVector.Builder(
    name = "SemiSelect", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128 448h768q64 0 64 64t-64 64H128q-64 0-64-64t64-64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSwitchButton(): ImageVector = ImageVector.Builder(
    name = "SwitchButton", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M352 159.872V230.4a352 352 0 1 0 320 0v-70.528A416.128 416.128 0 0 1 512 960a416 416 0 0 1-160-800.128").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 64q32 0 32 32v320q0 32-32 32t-32-32V96q0-32 32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildLocation(): ImageVector = ImageVector.Builder(
    name = "Location", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M800 416a288 288 0 1 0-576 0c0 118.144 94.528 272.128 288 456.576C705.472 688.128 800 534.144 800 416M512 960C277.312 746.688 160 565.312 160 416a352 352 0 0 1 704 0c0 149.312-117.312 330.688-352 544").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 512a96 96 0 1 0 0-192 96 96 0 0 0 0 192m0 64a160 160 0 1 1 0-320 160 160 0 0 1 0 320").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildLink(): ImageVector = ImageVector.Builder(
    name = "Link", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M715.648 625.152 670.4 579.904l90.496-90.56c75.008-74.944 85.12-186.368 22.656-248.896-62.528-62.464-173.952-52.352-248.96 22.656L444.16 353.6l-45.248-45.248 90.496-90.496c100.032-99.968 251.968-110.08 339.456-22.656 87.488 87.488 77.312 239.424-22.656 339.456l-90.496 90.496zm-90.496 90.496-90.496 90.496C434.624 906.112 282.688 916.224 195.2 828.8c-87.488-87.488-77.312-239.424 22.656-339.456l90.496-90.496 45.248 45.248-90.496 90.56c-75.008 74.944-85.12 186.368-22.656 248.896 62.528 62.464 173.952 52.352 248.96-22.656l90.496-90.496zm0-362.048 45.248 45.248L398.848 670.4 353.6 625.152z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCirclePlus(): ImageVector = ImageVector.Builder(
    name = "CirclePlus", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M352 480h320a32 32 0 1 1 0 64H352a32 32 0 0 1 0-64").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M480 672V352a32 32 0 1 1 64 0v320a32 32 0 0 1-64 0").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildFilter(): ImageVector = ImageVector.Builder(
    name = "Filter", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M384 523.392V928a32 32 0 0 0 46.336 28.608l192-96A32 32 0 0 0 640 832V523.392l280.768-343.104a32 32 0 1 0-49.536-40.576l-288 352A32 32 0 0 0 576 512v300.224l-128 64V512a32 32 0 0 0-7.232-20.288L195.52 192H704a32 32 0 1 0 0-64H128a32 32 0 0 0-24.768 52.288z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildOperation(): ImageVector = ImageVector.Builder(
    name = "Operation", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M389.44 768a96.064 96.064 0 0 1 181.12 0H896v64H570.56a96.064 96.064 0 0 1-181.12 0H128v-64zm192-288a96.064 96.064 0 0 1 181.12 0H896v64H762.56a96.064 96.064 0 0 1-181.12 0H128v-64zm-320-288a96.064 96.064 0 0 1 181.12 0H896v64H442.56a96.064 96.064 0 0 1-181.12 0H128v-64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildAim(): ImageVector = ImageVector.Builder(
    name = "Aim", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 96a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V128a32 32 0 0 1 32-32m0 576a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V704a32 32 0 0 1 32-32M96 512a32 32 0 0 1 32-32h192a32 32 0 0 1 0 64H128a32 32 0 0 1-32-32m576 0a32 32 0 0 1 32-32h192a32 32 0 1 1 0 64H704a32 32 0 0 1-32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMagicStick(): ImageVector = ImageVector.Builder(
    name = "MagicStick", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 64h64v192h-64zm0 576h64v192h-64zM160 480v-64h192v64zm576 0v-64h192v64zM249.856 199.04l45.248-45.184L430.848 289.6 385.6 334.848 249.856 199.104zM657.152 606.4l45.248-45.248 135.744 135.744-45.248 45.248zM114.048 923.2 68.8 877.952l316.8-316.8 45.248 45.248zM702.4 334.848 657.152 289.6l135.744-135.744 45.248 45.248z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildBrush(): ImageVector = ImageVector.Builder(
    name = "Brush", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M896 448H128v192a64 64 0 0 0 64 64h192v192h256V704h192a64 64 0 0 0 64-64zm-770.752-64c0-47.552 5.248-90.24 15.552-128 14.72-54.016 42.496-107.392 83.2-160h417.28l-15.36 70.336L736 96h211.2c-24.832 42.88-41.92 96.256-51.2 160a664 664 0 0 0-6.144 128H960v256a128 128 0 0 1-128 128H704v160a32 32 0 0 1-32 32H352a32 32 0 0 1-32-32V768H192A128 128 0 0 1 64 640V384zm64 0h636.544c-2.048-45.824.256-91.584 6.848-137.216 4.48-30.848 10.688-59.776 18.688-86.784h-96.64l-221.12 141.248L561.92 160H256.512c-25.856 37.888-43.776 75.456-53.952 112.832-8.768 32.064-13.248 69.12-13.312 111.168").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildScissor(): ImageVector = ImageVector.Builder(
    name = "Scissor", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m512.064 578.368-106.88 152.768a160 160 0 1 1-23.36-78.208L472.96 522.56 196.864 128.256a32 32 0 1 1 52.48-36.736l393.024 561.344a160 160 0 1 1-23.36 78.208l-106.88-152.704zm54.4-189.248 208.384-297.6a32 32 0 0 1 52.48 36.736l-221.76 316.672-39.04-55.808zm-376.32 425.856a96 96 0 1 0 110.144-157.248 96 96 0 0 0-110.08 157.248zm643.84 0a96 96 0 1 0-110.08-157.248 96 96 0 0 0 110.08 157.248").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCrop(): ImageVector = ImageVector.Builder(
    name = "Crop", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M256 768h672a32 32 0 1 1 0 64H224a32 32 0 0 1-32-32V96a32 32 0 0 1 64 0z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M832 224v704a32 32 0 1 1-64 0V256H96a32 32 0 0 1 0-64h704a32 32 0 0 1 32 32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildFilm(): ImageVector = ImageVector.Builder(
    name = "Film", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M160 160v704h704V160zm-32-64h768a32 32 0 0 1 32 32v768a32 32 0 0 1-32 32H128a32 32 0 0 1-32-32V128a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M320 288V128h64v352h256V128h64v160h160v64H704v128h160v64H704v128h160v64H704v160h-64V544H384v352h-64V736H128v-64h192V544H128v-64h192V352H128v-64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMic(): ImageVector = ImageVector.Builder(
    name = "Mic", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M480 704h160a64 64 0 0 0 64-64v-32h-96a32 32 0 0 1 0-64h96v-96h-96a32 32 0 0 1 0-64h96v-96h-96a32 32 0 0 1 0-64h96v-32a64 64 0 0 0-64-64H384a64 64 0 0 0-64 64v32h96a32 32 0 0 1 0 64h-96v96h96a32 32 0 0 1 0 64h-96v96h96a32 32 0 0 1 0 64h-96v32a64 64 0 0 0 64 64zm64 64v128h192a32 32 0 1 1 0 64H288a32 32 0 1 1 0-64h192V768h-96a128 128 0 0 1-128-128V192A128 128 0 0 1 384 64h256a128 128 0 0 1 128 128v448a128 128 0 0 1-128 128z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMute(): ImageVector = ImageVector.Builder(
    name = "Mute", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m412.16 592.128-45.44 45.44A191.23 191.23 0 0 1 320 512V256a192 192 0 1 1 384 0v44.352l-64 64V256a128 128 0 1 0-256 0v256c0 30.336 10.56 58.24 28.16 80.128m51.968 38.592A128 128 0 0 0 640 512v-57.152l64-64V512a192 192 0 0 1-287.68 166.528zM314.88 779.968l46.144-46.08A223 223 0 0 0 480 768h64a224 224 0 0 0 224-224v-32a32 32 0 1 1 64 0v32a288 288 0 0 1-288 288v64h64a32 32 0 1 1 0 64H416a32 32 0 1 1 0-64h64v-64c-61.44 0-118.4-19.2-165.12-52.032M266.752 737.6A286.98 286.98 0 0 1 192 544v-32a32 32 0 0 1 64 0v32c0 56.832 21.184 108.8 56.064 148.288z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M150.72 859.072a32 32 0 0 1-45.44-45.056l704-708.544a32 32 0 0 1 45.44 45.056z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildSunny(): ImageVector = ImageVector.Builder(
    name = "Sunny", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 704a192 192 0 1 0 0-384 192 192 0 0 0 0 384m0 64a256 256 0 1 1 0-512 256 256 0 0 1 0 512m0-704a32 32 0 0 1 32 32v64a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32m0 768a32 32 0 0 1 32 32v64a32 32 0 1 1-64 0v-64a32 32 0 0 1 32-32M195.2 195.2a32 32 0 0 1 45.248 0l45.248 45.248a32 32 0 1 1-45.248 45.248L195.2 240.448a32 32 0 0 1 0-45.248m543.104 543.104a32 32 0 0 1 45.248 0l45.248 45.248a32 32 0 0 1-45.248 45.248l-45.248-45.248a32 32 0 0 1 0-45.248M64 512a32 32 0 0 1 32-32h64a32 32 0 0 1 0 64H96a32 32 0 0 1-32-32m768 0a32 32 0 0 1 32-32h64a32 32 0 1 1 0 64h-64a32 32 0 0 1-32-32M195.2 828.8a32 32 0 0 1 0-45.248l45.248-45.248a32 32 0 0 1 45.248 45.248L240.448 828.8a32 32 0 0 1-45.248 0m543.104-543.104a32 32 0 0 1 0-45.248l45.248-45.248a32 32 0 0 1 45.248 45.248l-45.248 45.248a32 32 0 0 1-45.248 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMoon(): ImageVector = ImageVector.Builder(
    name = "Moon", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M240.448 240.448a384 384 0 1 0 559.424 525.696 448 448 0 0 1-542.016-542.08 391 391 0 0 0-17.408 16.384m181.056 362.048a384 384 0 0 0 525.632 16.384A448 448 0 1 1 405.056 76.8a384 384 0 0 0 16.448 525.696").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCloudy(): ImageVector = ImageVector.Builder(
    name = "Cloudy", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M598.4 831.872H328.192a256 256 0 0 1-34.496-510.528A352 352 0 1 1 598.4 831.872m-271.36-64h272.256a288 288 0 1 0-248.512-417.664L335.04 381.44l-34.816 3.584a192 192 0 0 0 26.88 382.848z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildLightning(): ImageVector = ImageVector.Builder(
    name = "Lightning", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M288 671.36v64.128A239.81 239.81 0 0 1 63.744 496.192a240.32 240.32 0 0 1 199.488-236.8 256.128 256.128 0 0 1 487.872-30.976A256.064 256.064 0 0 1 736 734.016v-64.768a192 192 0 0 0 3.328-377.92l-35.2-6.592-12.8-33.408a192.064 192.064 0 0 0-365.952 23.232l-9.92 40.896-41.472 7.04a176.32 176.32 0 0 0-146.24 173.568c0 91.968 70.464 167.36 160.256 175.232z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M416 736a32 32 0 0 1-27.776-47.872l128-224a32 32 0 1 1 55.552 31.744L471.168 672H608a32 32 0 0 1 27.776 47.872l-128 224a32 32 0 1 1-55.68-31.744L552.96 736z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildTrophy(): ImageVector = ImageVector.Builder(
    name = "Trophy", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M480 896V702.08A256.26 256.26 0 0 1 264.064 512h-32.64a96 96 0 0 1-91.968-68.416L93.632 290.88a76.8 76.8 0 0 1 73.6-98.88H256V96a32 32 0 0 1 32-32h448a32 32 0 0 1 32 32v96h88.768a76.8 76.8 0 0 1 73.6 98.88L884.48 443.52A96 96 0 0 1 792.576 512h-32.64A256.26 256.26 0 0 1 544 702.08V896h128a32 32 0 1 1 0 64H352a32 32 0 1 1 0-64zm224-448V128H320v320a192 192 0 1 0 384 0m64 0h24.576a32 32 0 0 0 30.656-22.784l45.824-152.768A12.8 12.8 0 0 0 856.768 256H768zm-512 0V256h-88.768a12.8 12.8 0 0 0-12.288 16.448l45.824 152.768A32 32 0 0 0 231.424 448z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMedal(): ImageVector = ImageVector.Builder(
    name = "Medal", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 896a256 256 0 1 0 0-512 256 256 0 0 0 0 512m0 64a320 320 0 1 1 0-640 320 320 0 0 1 0 640").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M576 128H448v200a286.7 286.7 0 0 1 64-8c19.52 0 40.832 2.688 64 8zm64 0v219.648c24.448 9.088 50.56 20.416 78.4 33.92L757.44 128zm-256 0H266.624l39.04 253.568c27.84-13.504 53.888-24.832 78.336-33.92zM229.312 64h565.376a32 32 0 0 1 31.616 36.864L768 480c-113.792-64-199.104-96-256-96s-142.208 32-256 96l-58.304-379.136A32 32 0 0 1 229.312 64").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildPresent(): ImageVector = ImageVector.Builder(
    name = "Present", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M480 896V640H192v-64h288V320H192v576zm64 0h288V320H544v256h288v64H544zM128 256h768v672a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M96 256h832q32 0 32 32t-32 32H96q-32 0-32-32t32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M416 256a64 64 0 1 0 0-128 64 64 0 0 0 0 128m0 64a128 128 0 1 1 0-256 128 128 0 0 1 0 256").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M608 256a64 64 0 1 0 0-128 64 64 0 0 0 0 128m0 64a128 128 0 1 1 0-256 128 128 0 0 1 0 256").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildShoppingCart(): ImageVector = ImageVector.Builder(
    name = "ShoppingCart", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M432 928a48 48 0 1 1 0-96 48 48 0 0 1 0 96m320 0a48 48 0 1 1 0-96 48 48 0 0 1 0 96M96 128a32 32 0 0 1 0-64h160a32 32 0 0 1 31.36 25.728L320.64 256H928a32 32 0 0 1 31.296 38.72l-96 448A32 32 0 0 1 832 768H384a32 32 0 0 1-31.36-25.728L229.76 128zm314.24 576h395.904l82.304-384H333.44z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildGoods(): ImageVector = ImageVector.Builder(
    name = "Goods", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M320 288v-22.336C320 154.688 405.504 64 512 64s192 90.688 192 201.664v22.4h131.072a32 32 0 0 1 31.808 28.8l57.6 576a32 32 0 0 1-31.808 35.2H131.328a32 32 0 0 1-31.808-35.2l57.6-576a32 32 0 0 1 31.808-28.8H320zm64 0h256v-22.336C640 189.248 582.272 128 512 128s-128 61.248-128 137.664v22.4zm-64 64H217.92l-51.2 512h690.56l-51.264-512H704v96a32 32 0 1 1-64 0v-96H384v96a32 32 0 0 1-64 0z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMoney(): ImageVector = ImageVector.Builder(
    name = "Money", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M256 640v192h640V384H768v-64h150.976c14.272 0 19.456 1.472 24.64 4.288a29.06 29.06 0 0 1 12.16 12.096c2.752 5.184 4.224 10.368 4.224 24.64v493.952c0 14.272-1.472 19.456-4.288 24.64a29.06 29.06 0 0 1-12.096 12.16c-5.184 2.752-10.368 4.224-24.64 4.224H233.024c-14.272 0-19.456-1.472-24.64-4.288a29.06 29.06 0 0 1-12.16-12.096c-2.688-5.184-4.224-10.368-4.224-24.576V640z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M768 192H128v448h640zm64-22.976v493.952c0 14.272-1.472 19.456-4.288 24.64a29.06 29.06 0 0 1-12.096 12.16c-5.184 2.752-10.368 4.224-24.64 4.224H105.024c-14.272 0-19.456-1.472-24.64-4.288a29.06 29.06 0 0 1-12.16-12.096C65.536 682.432 64 677.248 64 663.04V169.024c0-14.272 1.472-19.456 4.288-24.64a29.06 29.06 0 0 1 12.096-12.16C85.568 129.536 90.752 128 104.96 128h685.952c14.272 0 19.456 1.472 24.64 4.288a29.06 29.06 0 0 1 12.16 12.096c2.752 5.184 4.224 10.368 4.224 24.64z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M448 576a160 160 0 1 1 0-320 160 160 0 0 1 0 320m0-64a96 96 0 1 0 0-192 96 96 0 0 0 0 192").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildWallet(): ImageVector = ImageVector.Builder(
    name = "Wallet", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M640 288h-64V128H128v704h384v32a32 32 0 0 0 32 32H96a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32h512a32 32 0 0 1 32 32z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M128 320v512h768V320zm-32-64h832a32 32 0 0 1 32 32v576a32 32 0 0 1-32 32H96a32 32 0 0 1-32-32V288a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M704 640a64 64 0 1 1 0-128 64 64 0 0 1 0 128").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildTicket(): ImageVector = ImageVector.Builder(
    name = "Ticket", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M640 832H64V640a128 128 0 1 0 0-256V192h576v160h64V192h256v192a128 128 0 1 0 0 256v192H704V672h-64zm0-416v192h64V416z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDiscount(): ImageVector = ImageVector.Builder(
    name = "Discount", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M224 704h576V318.336L552.512 115.84a64 64 0 0 0-81.024 0L224 318.336zm0 64v128h576V768zM593.024 66.304l259.2 212.096A32 32 0 0 1 864 303.168V928a32 32 0 0 1-32 32H192a32 32 0 0 1-32-32V303.168a32 32 0 0 1 11.712-24.768l259.2-212.096a128 128 0 0 1 162.112 0").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 448a64 64 0 1 0 0-128 64 64 0 0 0 0 128m0 64a128 128 0 1 1 0-256 128 128 0 0 1 0 256").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildShip(): ImageVector = ImageVector.Builder(
    name = "Ship", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 386.88V448h405.568a32 32 0 0 1 30.72 40.768l-76.48 267.968A192 192 0 0 1 687.168 896H336.832a192 192 0 0 1-184.64-139.264L75.648 488.768A32 32 0 0 1 106.368 448H448V117.888a32 32 0 0 1 47.36-28.096l13.888 7.616L512 96v2.88l231.68 126.4a32 32 0 0 1-2.048 57.216zm0-70.272 144.768-65.792L512 171.84zM512 512H148.864l18.24 64H856.96l18.24-64zM185.408 640l28.352 99.2A128 128 0 0 0 336.832 832h350.336a128 128 0 0 0 123.072-92.8l28.352-99.2z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildVan(): ImageVector = ImageVector.Builder(
    name = "Van", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128.896 736H96a32 32 0 0 1-32-32V224a32 32 0 0 1 32-32h576a32 32 0 0 1 32 32v96h164.544a32 32 0 0 1 31.616 27.136l54.144 352A32 32 0 0 1 922.688 736h-91.52a144 144 0 1 1-286.272 0H415.104a144 144 0 1 1-286.272 0zm23.36-64a143.872 143.872 0 0 1 239.488 0H568.32c17.088-25.6 42.24-45.376 71.744-55.808V256H128v416zm655.488 0h77.632l-19.648-128H704v64.896A144 144 0 0 1 807.744 672m48.128-192-14.72-96H704v96zM688 832a80 80 0 1 0 0-160 80 80 0 0 0 0 160m-416 0a80 80 0 1 0 0-160 80 80 0 0 0 0 160").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildBicycle(): ImageVector = ImageVector.Builder(
    name = "Bicycle", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M256 832a128 128 0 1 0 0-256 128 128 0 0 0 0 256m0 64a192 192 0 1 1 0-384 192 192 0 0 1 0 384").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M288 672h320q32 0 32 32t-32 32H288q-32 0-32-32t32-32").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M768 832a128 128 0 1 0 0-256 128 128 0 0 0 0 256m0 64a192 192 0 1 1 0-384 192 192 0 0 1 0 384").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M480 192a32 32 0 0 1 0-64h160a32 32 0 0 1 31.04 24.256l96 384a32 32 0 0 1-62.08 15.488L615.04 192zM96 384a32 32 0 0 1 0-64h128a32 32 0 0 1 30.336 21.888l64 192a32 32 0 1 1-60.672 20.224L200.96 384z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("m373.376 599.808-42.752-47.616 320-288 42.752 47.616z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildChecked(): ImageVector = ImageVector.Builder(
    name = "Checked", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M704 192h160v736H160V192h160.064v64H704zM311.616 537.28l-45.312 45.248L447.36 763.52l316.8-316.8-45.312-45.184L447.36 673.024zM384 192V96h256v96z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildFinished(): ImageVector = ImageVector.Builder(
    name = "Finished", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M280.768 753.728 691.456 167.04a32 32 0 1 1 52.416 36.672L314.24 817.472a32 32 0 0 1-45.44 7.296l-230.4-172.8a32 32 0 0 1 38.4-51.2zM736 448a32 32 0 1 1 0-64h192a32 32 0 1 1 0 64zM608 640a32 32 0 0 1 0-64h319.936a32 32 0 1 1 0 64zM480 832a32 32 0 1 1 0-64h447.936a32 32 0 1 1 0 64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildFlag(): ImageVector = ImageVector.Builder(
    name = "Flag", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M288 128h608L736 384l160 256H288v320h-96V64h96z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildList(): ImageVector = ImageVector.Builder(
    name = "List", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M704 192h160v736H160V192h160v64h384zM288 512h448v-64H288zm0 256h448v-64H288zm96-576V96h256v96z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildGrid(): ImageVector = ImageVector.Builder(
    name = "Grid", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M640 384v256H384V384zm64 0h192v256H704zm-64 512H384V704h256zm64 0V704h192v192zm-64-768v192H384V128zm64 0h192v192H704zM320 384v256H128V384zm0 512H128V704h192zm0-768v192H128V128z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildChatDotRound(): ImageVector = ImageVector.Builder(
    name = "ChatDotRound", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m174.72 855.68 135.296-45.12 23.68 11.84C388.096 849.536 448.576 864 512 864c211.84 0 384-166.784 384-352S723.84 160 512 160 128 326.784 128 512c0 69.12 24.96 139.264 70.848 199.232l22.08 28.8-46.272 115.584zm-45.248 82.56A32 32 0 0 1 89.6 896l58.368-145.92C94.72 680.32 64 596.864 64 512 64 299.904 256 96 512 96s448 203.904 448 416-192 416-448 416a461.06 461.06 0 0 1-206.912-48.384l-175.616 58.56z").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M512 563.2a51.2 51.2 0 1 1 0-102.4 51.2 51.2 0 0 1 0 102.4m192 0a51.2 51.2 0 1 1 0-102.4 51.2 51.2 0 0 1 0 102.4m-384 0a51.2 51.2 0 1 1 0-102.4 51.2 51.2 0 0 1 0 102.4").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildPhone(): ImageVector = ImageVector.Builder(
    name = "Phone", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M79.36 432.256 591.744 944.64a32 32 0 0 0 35.2 6.784l253.44-108.544a32 32 0 0 0 9.984-52.032l-153.856-153.92a32 32 0 0 0-36.928-6.016l-69.888 34.944L358.08 394.24l35.008-69.888a32 32 0 0 0-5.952-36.928L233.152 133.568a32 32 0 0 0-52.032 10.048L72.512 397.056a32 32 0 0 0 6.784 35.2zm60.48-29.952 81.536-190.08L325.568 316.48l-24.64 49.216-20.608 41.216 32.576 32.64 271.552 271.552 32.64 32.64 41.216-20.672 49.28-24.576 104.192 104.128-190.08 81.472zM512 320v-64a256 256 0 0 1 256 256h-64a192 192 0 0 0-192-192m0-192V64a448 448 0 0 1 448 448h-64a384 384 0 0 0-384-384").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildPromotion(): ImageVector = ImageVector.Builder(
    name = "Promotion", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m64 448 832-320-128 704-446.08-243.328L832 192 242.816 545.472zm256 512V657.024L512 768z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCompass(): ImageVector = ImageVector.Builder(
    name = "Compass", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768m0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M725.888 315.008C676.48 428.672 624 513.28 568.576 568.64c-55.424 55.424-139.968 107.904-253.568 157.312a12.8 12.8 0 0 1-16.896-16.832c49.536-113.728 102.016-198.272 157.312-253.632 55.36-55.296 139.904-107.776 253.632-157.312a12.8 12.8 0 0 1 16.832 16.832").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildTrendCharts(): ImageVector = ImageVector.Builder(
    name = "TrendCharts", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M128 896V128h768v768zm291.712-327.296 128 102.4 180.16-201.792-47.744-42.624-139.84 156.608-128-102.4-180.16 201.792 47.744 42.624zM816 352a48 48 0 1 0-96 0 48 48 0 0 0 96 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildHistogram(): ImageVector = ImageVector.Builder(
    name = "Histogram", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M416 896V128h192v768zm-288 0V448h192v448zm576 0V320h192v576z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildPieChart(): ImageVector = ImageVector.Builder(
    name = "PieChart", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M448 68.48v64.832A384.128 384.128 0 0 0 512 896a384.13 384.13 0 0 0 378.688-320h64.768A448.128 448.128 0 0 1 64 512 448.13 448.13 0 0 1 448 68.48").toNodes(), fill = SolidColor(Color.Black))
    addPath(pathData = PathParser().parsePathString("M576 97.28V448h350.72A384.064 384.064 0 0 0 576 97.28M512 64V33.152A448 448 0 0 1 990.848 512H512z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDataLine(): ImageVector = ImageVector.Builder(
    name = "DataLine", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M359.168 768H160a32 32 0 0 1-32-32V192H64a32 32 0 0 1 0-64h896a32 32 0 1 1 0 64h-64v544a32 32 0 0 1-32 32H665.216l110.848 192h-73.856L591.36 768H433.024L322.176 960H248.32zM832 192H192v512h640zM342.656 534.656a32 32 0 1 1-45.312-45.312L444.992 341.76l125.44 94.08L679.04 300.032a32 32 0 1 1 49.92 39.936L581.632 524.224 451.008 426.24 342.656 534.592z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildDataAnalysis(): ImageVector = ImageVector.Builder(
    name = "DataAnalysis", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("m665.216 768 110.848 192h-73.856L591.36 768H433.024L322.176 960H248.32l110.848-192H160a32 32 0 0 1-32-32V192H64a32 32 0 0 1 0-64h896a32 32 0 1 1 0 64h-64v544a32 32 0 0 1-32 32zM832 192H192v512h640zM352 448a32 32 0 0 1 32 32v64a32 32 0 0 1-64 0v-64a32 32 0 0 1 32-32m160-64a32 32 0 0 1 32 32v128a32 32 0 0 1-64 0V416a32 32 0 0 1 32-32m160-64a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V352a32 32 0 0 1 32-32").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildMonitor(): ImageVector = ImageVector.Builder(
    name = "Monitor", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M544 768v128h192a32 32 0 1 1 0 64H288a32 32 0 1 1 0-64h192V768H192A128 128 0 0 1 64 640V256a128 128 0 0 1 128-128h640a128 128 0 0 1 128 128v384a128 128 0 0 1-128 128zM192 192a64 64 0 0 0-64 64v384a64 64 0 0 0 64 64h640a64 64 0 0 0 64-64V256a64 64 0 0 0-64-64z").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildIphone(): ImageVector = ImageVector.Builder(
    name = "Iphone", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M224 768v96.064a64 64 0 0 0 64 64h448a64 64 0 0 0 64-64V768zm0-64h576V160a64 64 0 0 0-64-64H288a64 64 0 0 0-64 64zm32 288a96 96 0 0 1-96-96V128a96 96 0 0 1 96-96h512a96 96 0 0 1 96 96v768a96 96 0 0 1-96 96zm304-144a48 48 0 1 1-96 0 48 48 0 0 1 96 0").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildCellphone(): ImageVector = ImageVector.Builder(
    name = "Cellphone", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M256 128a64 64 0 0 0-64 64v640a64 64 0 0 0 64 64h512a64 64 0 0 0 64-64V192a64 64 0 0 0-64-64zm0-64h512a128 128 0 0 1 128 128v640a128 128 0 0 1-128 128H256a128 128 0 0 1-128-128V192A128 128 0 0 1 256 64m128 128h256a32 32 0 1 1 0 64H384a32 32 0 0 1 0-64m128 640a64 64 0 1 1 0-128 64 64 0 0 1 0 128").toNodes(), fill = SolidColor(Color.Black))
}.build()

private fun buildHeadset(): ImageVector = ImageVector.Builder(
    name = "Headset", defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 1024f, viewportHeight = 1024f,
).apply {
    addPath(pathData = PathParser().parsePathString("M896 529.152V512a384 384 0 1 0-768 0v17.152A128 128 0 0 1 320 640v128a128 128 0 1 1-256 0V512a448 448 0 1 1 896 0v256a128 128 0 1 1-256 0V640a128 128 0 0 1 192-110.848M896 640a64 64 0 0 0-128 0v128a64 64 0 0 0 128 0zm-768 0v128a64 64 0 0 0 128 0V640a64 64 0 1 0-128 0").toNodes(), fill = SolidColor(Color.Black))
}.build()
