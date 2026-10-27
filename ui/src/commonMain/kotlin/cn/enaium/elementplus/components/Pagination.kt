package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.ceil

/**
 * `<el-pagination>` with pager, prev/next, total, sizes and jumper parts.
 */
@Composable
fun ElPagination(
    total: Int,
    currentPage: Int,
    onCurrentPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pageSize: Int = 10,
    onPageSizeChange: ((Int) -> Unit)? = null,
    pagerCount: Int = 7,
    background: Boolean = false,
    layout: String = "prev, pager, next, jumper, ->, total",
    pageSizes: List<Int> = listOf(10, 20, 30, 40, 50, 100),
    small: Boolean = false,
    disabled: Boolean = false,
    hideOnSinglePage: Boolean = false,
    pageCount: Int? = null,
    prevText: String? = null,
    nextText: String? = null,
    prevIcon: ImageVector = ElIcons.ArrowLeft,
    nextIcon: ImageVector = ElIcons.ArrowRight,
    slot: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedPageCount = (pageCount ?: ceil(total.toDouble() / pageSize.coerceAtLeast(1)).toInt()).coerceAtLeast(1)
    if (hideOnSinglePage && resolvedPageCount <= 1) return
    val current = currentPage.coerceIn(1, resolvedPageCount)
    val parts = layout.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    val itemSize = if (small) 24.dp else 32.dp
    val shape = RoundedCornerShape(theme.radius.base)
    fun goTo(page: Int) {
        if (disabled) return
        val next = page.coerceIn(1, resolvedPageCount)
        if (next != current) onCurrentPageChange(next)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (small) 4.dp else 8.dp),
    ) {
        parts.forEach { part ->
            when (part) {
                "->" -> Spacer(Modifier.weight(1f))
                "slot" -> slot?.invoke()
                "total" -> BasicText(
                    text = locale.pagination.total.replace("{total}", total.toString()),
                    style = theme.typography.small.copy(color = colors.textRegular),
                )
                "sizes" -> if (onPageSizeChange != null) {
                    ElSelect(
                        value = pageSize,
                        onValueChange = { next ->
                            next?.let { onPageSizeChange.invoke(it) }
                            onCurrentPageChange(1)
                        },
                        options = pageSizes.map { ElSelectOption(it, "$it${locale.pagination.pagesize}") },
                        modifier = Modifier.width(if (small) 90.dp else 110.dp),
                        size = if (small) cn.enaium.elementplus.theme.ElSize.Small else cn.enaium.elementplus.theme.ElSize.Default,
                        clearable = false,
                        filterable = false,
                    )
                }
                "prev" -> PagerNavButton(
                    icon = prevIcon,
                    text = prevText,
                    contentDescription = prevText ?: locale.pagination.prev,
                    enabled = !disabled && current > 1,
                    background = background,
                    size = itemSize,
                    onClick = { goTo(current - 1) },
                )
                "pager" -> PagerNumbers(
                    pageCount = resolvedPageCount,
                    current = current,
                    pagerCount = pagerCount,
                    disabled = disabled,
                    background = background,
                    size = itemSize,
                    onSelect = ::goTo,
                )
                "next" -> PagerNavButton(
                    icon = nextIcon,
                    text = nextText,
                    contentDescription = nextText ?: locale.pagination.next,
                    enabled = !disabled && current < resolvedPageCount,
                    background = background,
                    size = itemSize,
                    onClick = { goTo(current + 1) },
                )
                "jumper" -> Jumper(
                    small = small,
                    disabled = disabled,
                    current = current,
                    onGo = ::goTo,
                )
            }
        }
    }
}

@Composable
private fun PagerNavButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    background: Boolean,
    size: Dp,
    onClick: () -> Unit,
    text: String? = null,
) {
    val colors = LocalElTheme.current.colors
    Box(
        Modifier
            .height(size)
            .then(if (text != null) Modifier.widthIn(min = size) else Modifier.width(size))
            .clip(RoundedCornerShape(4.dp))
            .then(if (background) Modifier.background(colors.fillLight) else Modifier)
            .then(if (!background) Modifier.border(1.dp, colors.borderLight, RoundedCornerShape(4.dp)) else Modifier)
            .clickable(enabled = enabled, onClick = onClick)
            .then(if (text != null) Modifier.padding(horizontal = 8.dp) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (text != null) {
            BasicText(
                text = text,
                style = LocalElTheme.current.typography.small.copy(
                    color = if (enabled) colors.textRegular else colors.textDisabled,
                ),
                maxLines = 1,
            )
        } else {
            ElIcon(
                icon,
                size = 12.dp,
                tint = if (enabled) colors.textRegular else colors.textDisabled,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
private fun PagerNumbers(
    pageCount: Int,
    current: Int,
    pagerCount: Int,
    disabled: Boolean,
    background: Boolean,
    size: Dp,
    onSelect: (Int) -> Unit,
) {
    val colors = LocalElTheme.current.colors
    val theme = LocalElTheme.current
    val count = pagerCount.coerceAtLeast(5).let { if (it % 2 == 0) it - 1 else it }
    val pages = remember(pageCount, current, count) {
        buildList<Int?> {
            if (pageCount <= count) {
                for (page in 1..pageCount) add(page)
            } else if (current <= count - 2) {
                for (page in 1..(count - 2)) add(page)
                add(null)
                add(pageCount)
            } else if (current >= pageCount - count + 3) {
                add(1)
                add(null)
                for (page in (pageCount - count + 3)..pageCount) add(page)
            } else {
                add(1)
                add(null)
                for (page in (current - 1)..(current + 1)) add(page)
                add(null)
                add(pageCount)
            }
        }
    }
    pages.forEach { page ->
        if (page == null) {
            BasicText(
                text = "···",
                modifier = Modifier.width(size),
                style = theme.typography.small.copy(color = colors.textSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
            )
        } else {
            val selected = page == current
            Box(
                Modifier
                    .height(size)
                    .width(size)
                    .clip(RoundedCornerShape(4.dp))
                    .then(
                        if (selected) Modifier.background(colors.primary)
                        else if (background) Modifier.background(colors.fillLight)
                        else Modifier.border(1.dp, colors.borderLight, RoundedCornerShape(4.dp))
                    )
                    .clickable(enabled = !disabled) { onSelect(page) },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = page.toString(),
                    style = theme.typography.small.copy(
                        color = when {
                            disabled -> colors.textDisabled
                            selected -> colors.white
                            else -> colors.textRegular
                        },
                        fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Medium else androidx.compose.ui.text.font.FontWeight.Normal,
                    ),
                )
            }
        }
    }
}

@Composable
private fun Jumper(
    small: Boolean,
    disabled: Boolean,
    current: Int,
    onGo: (Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var text by remember(current) { mutableStateOf(current.toString()) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        BasicText(text = locale.pagination.goto, style = theme.typography.small.copy(color = colors.textRegular))
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .width(if (small) 36.dp else 44.dp)
                .height(if (small) 24.dp else 32.dp)
                .border(1.dp, colors.border, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it.filter { c -> c.isDigit() }.take(5) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !disabled,
                textStyle = theme.typography.small.copy(
                    color = if (disabled) colors.textDisabled else colors.textRegular,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                ),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    text.toIntOrNull()?.let(onGo)
                }),
            )
        }
        Spacer(Modifier.width(4.dp))
        BasicText(text = locale.pagination.pageClassifier, style = theme.typography.small.copy(color = colors.textRegular))
    }
}
