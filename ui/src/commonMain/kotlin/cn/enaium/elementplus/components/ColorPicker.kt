package cn.enaium.elementplus.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElColorFormat
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import cn.enaium.elementplus.theme.formatElColor
import cn.enaium.elementplus.theme.hsvToColor
import cn.enaium.elementplus.theme.parseElColor
import cn.enaium.elementplus.theme.rgbToHsv

/**
 * `<el-color-picker-panel>`: saturation/value area, hue and alpha sliders,
 * editable color value and predefined swatches.
 */
@Composable
fun ElColorPickerPanel(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    showAlpha: Boolean = true,
    format: ElColorFormat = ElColorFormat.Hex,
    predefine: List<Color> = emptyList(),
    disabled: Boolean = false,
    border: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val hsv = rgbToHsv(color)
    var hue by remember { mutableFloatStateOf(hsv.first) }
    LaunchedEffect(color) {
        val nextHue = attractiveHue(color)
        if (nextHue >= 0f) hue = nextHue
    }
    var valueText by remember { mutableStateOf(formatElColor(color, format, showAlpha)) }
    val latestColor by rememberUpdatedState(color)
    val latestHue by rememberUpdatedState(hue)
    val latestOnColorChange by rememberUpdatedState(onColorChange)
    var editing by remember { mutableStateOf(false) }
    LaunchedEffect(color) {
        if (!editing) valueText = formatElColor(color, format, showAlpha)
    }
    Column(
        modifier = modifier
            .then(
                if (border) {
                    Modifier
                        .border(1.dp, colors.border, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                } else {
                    Modifier
                },
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // HSV area
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.horizontalGradient(listOf(Color.White, hsvToColor(hue, 1f, 1f))))
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
                .then(if (disabled) Modifier else Modifier.pointerInput(disabled) {
                    detectTapGestures { offset ->
                        val s = (offset.x / size.width).coerceIn(0f, 1f)
                        val v = (1f - offset.y / size.height).coerceIn(0f, 1f)
                        latestOnColorChange(hsvToColor(latestHue, s, v, latestColor.alpha))
                    }
                })
                .then(if (disabled) Modifier else Modifier.pointerInput(disabled) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val s = (offset.x / size.width).coerceIn(0f, 1f)
                            val v = (1f - offset.y / size.height).coerceIn(0f, 1f)
                            latestOnColorChange(hsvToColor(latestHue, s, v, latestColor.alpha))
                        },
                    ) { change, _ ->
                        change.consume()
                        val s = (change.position.x / size.width).coerceIn(0f, 1f)
                        val v = (1f - change.position.y / size.height).coerceIn(0f, 1f)
                        latestOnColorChange(hsvToColor(latestHue, s, v, latestColor.alpha))
                    }
                }),
        ) {
            Box(
                Modifier
                    .offset(
                        x = maxWidth * hsv.second - 6.dp,
                        y = maxHeight * (1f - hsv.third) - 6.dp,
                    )
                    .size(12.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .background(Color.Transparent, CircleShape),
            )
        }
        // Hue slider
        GradientSlider(
            colorsList = listOf(
                Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red,
            ),
            fraction = hue / 360f,
            thumbColor = hsvToColor(hue, 1f, 1f),
            disabled = disabled,
            onFraction = { fraction ->
                hue = (fraction * 360f).coerceIn(0f, 360f)
                onColorChange(hsvToColor(hue, hsv.second, hsv.third, color.alpha))
            },
        )
        if (showAlpha) {
            BoxWithConstraints(Modifier.fillMaxWidth().height(12.dp)) {
                Box(Modifier.fillMaxWidth().height(12.dp).checkerboard())
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(color.copy(alpha = 0f), color.copy(alpha = 1f)),
                            ),
                        )
                        .then(if (disabled) Modifier else Modifier.pointerInput(disabled) {
                            detectTapGestures { offset ->
                                latestOnColorChange(latestColor.copy(alpha = (offset.x / size.width).coerceIn(0f, 1f)))
                            }
                        })
                        .then(if (disabled) Modifier else Modifier.pointerInput(disabled) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    latestOnColorChange(latestColor.copy(alpha = (offset.x / size.width).coerceIn(0f, 1f)))
                                },
                            ) { change, _ ->
                                change.consume()
                                latestOnColorChange(latestColor.copy(alpha = (change.position.x / size.width).coerceIn(0f, 1f)))
                            }
                        }),
                )
                Box(
                    Modifier
                        .offset(x = maxWidth * color.alpha - 6.dp)
                        .size(12.dp)
                        .background(color.copy(alpha = 1f), CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                )
            }
        }
        // Editable value + alpha preview
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(28.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .border(1.dp, colors.border, RoundedCornerShape(4.dp)),
            )
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = valueText,
                onValueChange = { next ->
                    editing = true
                    valueText = next
                    parseElColor(next)?.let { parsed ->
                        onColorChange(if (showAlpha) parsed.copy(alpha = color.alpha) else parsed.copy(alpha = 1f))
                    }
                },
                modifier = Modifier.weight(1f).onFocusChanged { if (!it.isFocused) editing = false },
                singleLine = true,
                enabled = !disabled,
                textStyle = theme.typography.small.copy(color = colors.textRegular),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { inner -> Box { inner() } },
            )
            if (showAlpha) {
                Spacer(Modifier.width(8.dp))
                BasicText(
                    text = "${(color.alpha * 100).toInt()}%",
                    style = theme.typography.small.copy(color = colors.textSecondary),
                )
            }
        }
        if (predefine.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                predefine.take(12).forEach { preset ->
                    Box(
                        Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(preset)
                            .border(1.dp, colors.borderLighter, RoundedCornerShape(3.dp))
                            .clickable(enabled = !disabled) { onColorChange(preset.copy(alpha = color.alpha)) },
                    )
                }
            }
        }
        footer?.invoke()
    }
}

@Composable
private fun GradientSlider(
    colorsList: List<Color>,
    fraction: Float,
    thumbColor: Color,
    disabled: Boolean,
    onFraction: (Float) -> Unit,
) {
    val latestOnFraction by rememberUpdatedState(onFraction)
    BoxWithConstraints(Modifier.fillMaxWidth().height(12.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.horizontalGradient(colorsList))
                .then(if (disabled) Modifier else Modifier.pointerInput(disabled) {
                    detectTapGestures { offset -> latestOnFraction((offset.x / size.width).coerceIn(0f, 1f)) }
                })
                .then(if (disabled) Modifier else Modifier.pointerInput(disabled) {
                    detectDragGestures(
                        onDragStart = { offset -> latestOnFraction((offset.x / size.width).coerceIn(0f, 1f)) },
                    ) { change, _ ->
                        change.consume()
                        latestOnFraction((change.position.x / size.width).coerceIn(0f, 1f))
                    }
                }),
        )
        Box(
            Modifier
                .offset(x = maxWidth * fraction - 6.dp)
                .size(12.dp)
                .background(thumbColor, CircleShape)
                .border(2.dp, Color.White, CircleShape),
        )
    }
}

private fun Modifier.checkerboard(): Modifier = this.drawBehind {
    val cell = 6.dp.toPx()
    var y = 0f
    var row = 0
    while (y < size.height) {
        var x = 0f
        var col = 0
        while (x < size.width) {
            drawRect(
                color = if ((row + col) % 2 == 0) Color(0xFFCCCCCC) else Color(0xFFFFFFFF),
                topLeft = Offset(x, y),
                size = Size(cell, cell),
            )
            x += cell
            col++
        }
        y += cell
        row++
    }
}

private fun attractiveHue(color: Color): Float {
    val hsv = rgbToHsv(color)
    return if (hsv.second < 0.01f) -1f else hsv.first
}

/**
 * `<el-color-picker>`: a color swatch field that opens [ElColorPickerPanel].
 */
@Composable
fun ElColorPicker(
    value: Color?,
    onValueChange: (Color?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    showAlpha: Boolean = true,
    format: ElColorFormat = ElColorFormat.Hex,
    predefine: List<Color> = emptyList(),
    onVisibleChange: ((Boolean) -> Unit)? = null,
    onChange: ((Color?) -> Unit)? = null,
    onActiveChange: ((Color) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    var expanded by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hoverState by interaction.collectIsHoveredAsState()
    val borderColor = when {
        disabled -> colors.disabledBorder
        expanded -> colors.primary
        hoverState -> colors.textDisabled
        else -> colors.border
    }
    // Value captured when the panel opens, so `onChange` only fires on commits.
    var valueAtOpen by remember { mutableStateOf(value) }
    fun commit() {
        if (valueAtOpen != value) {
            valueAtOpen = value
            onChange?.invoke(value)
        }
    }
    fun setExpanded(next: Boolean) {
        if (next) {
            valueAtOpen = value
        } else {
            commit()
        }
        expanded = next
        onVisibleChange?.invoke(next)
    }
    fun clear() {
        val hadValue = value != null
        valueAtOpen = null
        onValueChange(null)
        if (hadValue) onChange?.invoke(null)
        onClear?.invoke()
        expanded = false
        onVisibleChange?.invoke(false)
    }
    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(resolvedSize.height)
                .clip(shape)
                .background(if (disabled) colors.disabledBackground else colors.fillBlank, shape)
                .border(1.dp, borderColor, shape)
                .hoverable(interaction)
                .clickable(enabled = !disabled) { setExpanded(true) }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .background(value ?: Color.Transparent, RoundedCornerShape(3.dp))
                    .border(1.dp, colors.borderLighter, RoundedCornerShape(3.dp)),
            )
            Spacer(Modifier.width(8.dp))
            BasicText(
                text = value?.let { formatElColor(it, format, showAlpha) } ?: (placeholder ?: ""),
                modifier = Modifier.weight(1f),
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    color = if (value == null) colors.textPlaceholder else colors.textRegular,
                ),
            )
            if (clearable && value != null && hoverState && !disabled) {
                ElIcon(
                    ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = locale.colorPicker.clear,
                    modifier = Modifier.clickable { clear() },
                )
            }
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(modifier = Modifier.width(280.dp)) {
                Column(Modifier.padding(12.dp)) {
                    ElColorPickerPanel(
                        color = value ?: Color.White,
                        onColorChange = { next ->
                            onValueChange(next)
                            onActiveChange?.invoke(next)
                        },
                        showAlpha = showAlpha,
                        format = format,
                        predefine = predefine,
                        disabled = disabled,
                    )
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.End) {
                        BasicText(
                            text = locale.colorPicker.clear,
                            modifier = Modifier
                                .clickable { clear() }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                        Spacer(Modifier.width(8.dp))
                        BasicText(
                            text = locale.colorPicker.confirm,
                            modifier = Modifier
                                .clickable { setExpanded(false) }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                    }
                }
            }
        }
    }
}
