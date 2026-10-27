package cn.enaium.elementplus.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElUploadStatus { Ready, Uploading, Success, Fail }
enum class ElUploadListType { Text, Picture, PictureCard }

data class ElUploadFile(
    val uid: String,
    val name: String,
    val size: Long? = null,
    val status: ElUploadStatus = ElUploadStatus.Ready,
    val percentage: Double = 0.0,
    val url: String? = null,
    val thumbnail: Painter? = null,
    val error: String? = null,
)

/**
 * Imperative handle for the `<el-upload>` instance methods: `submit`,
 * `abort`, `clearFiles`, `handleStart` and `handleRemove`.
 */
class ElUploadController {
    internal var submitAction: (() -> Unit)? = null
    internal var abortAction: ((String?) -> Unit)? = null
    internal var clearAction: (() -> Unit)? = null
    internal var startAction: ((ElUploadFile) -> Unit)? = null
    internal var removeAction: ((ElUploadFile) -> Unit)? = null
    internal var pickAction: (() -> Unit)? = null

    /** Requests the injected file picker, honouring `disabled` and `limit`. */
    fun pick() {
        pickAction?.invoke()
    }

    /** Submits every file that is still in [ElUploadStatus.Ready]. */
    fun submit() {
        submitAction?.invoke()
    }

    /** Marks the uploading file ([uid]) - or all uploading files - as failed. */
    fun abort(uid: String? = null) {
        abortAction?.invoke(uid)
    }

    /** Removes every file from the list without firing `before-remove`. */
    fun clearFiles() {
        clearAction?.invoke()
    }

    /** Adds [file], applying `before-upload`, `limit` and `auto-upload`. */
    fun handleStart(file: ElUploadFile) {
        startAction?.invoke(file)
    }

    /** Removes [file], applying `before-remove`. */
    fun handleRemove(file: ElUploadFile) {
        removeAction?.invoke(file)
    }
}

/**
 * `<el-upload>` UI and state contract. Platform file picking and networking are
 * deliberately injected through [onPickFiles] and [onUploadFile] so the
 * component stays multiplatform; the caller appends new [ElUploadFile]s via
 * [onFilesChange].
 *
 * Parity additions: [directory] picks, [autoUpload], the `before-upload`
 * ([beforeUpload]) and `before-remove` ([beforeRemove]) gates, the
 * `on-change`/`on-remove`/`on-success`/`on-error`/`on-progress` lifecycle
 * callbacks, the `trigger`/`tip`/`file` slots and [ElUploadController] methods.
 * HTTP options (`action`, `method`, `headers`, `data`, `name`,
 * `with-credentials`, `crossorigin`, `http-request`) stay delegated to the
 * `onUploadFile` transport.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ElUpload(
    files: List<ElUploadFile>,
    onFilesChange: (List<ElUploadFile>) -> Unit,
    onPickFiles: () -> Unit,
    modifier: Modifier = Modifier,
    onUploadFile: ((ElUploadFile) -> Unit)? = null,
    onPreview: ((ElUploadFile) -> Unit)? = null,
    accept: String? = null,
    multiple: Boolean = false,
    limit: Int? = null,
    drag: Boolean = false,
    listType: ElUploadListType = ElUploadListType.Text,
    disabled: Boolean = false,
    tip: String? = null,
    buttonText: String? = null,
    showFileList: Boolean = true,
    onExceed: (() -> Unit)? = null,
    directory: Boolean = false,
    autoUpload: Boolean = true,
    beforeUpload: ((ElUploadFile) -> Boolean)? = null,
    beforeRemove: ((ElUploadFile) -> Boolean)? = null,
    onRemove: ((ElUploadFile, List<ElUploadFile>) -> Unit)? = null,
    onChange: ((ElUploadFile, List<ElUploadFile>) -> Unit)? = null,
    onSuccess: ((ElUploadFile) -> Unit)? = null,
    onError: ((ElUploadFile) -> Unit)? = null,
    onProgress: ((ElUploadFile, Double) -> Unit)? = null,
    trigger: (@Composable () -> Unit)? = null,
    tipContent: (@Composable () -> Unit)? = null,
    fileContent: (@Composable (ElUploadFile) -> Unit)? = null,
    controller: ElUploadController? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedButtonText = buttonText ?: locale.upload.selectFile.ifBlank { locale.upload.upload }
    val resolvedDropText = locale.upload.dropFileHere.ifBlank { locale.upload.upload }
    val limitReached = limit != null && files.size >= limit
    var internalFiles by remember { mutableStateOf(files) }
    LaunchedEffect(files) {
        if (files != internalFiles) internalFiles = files
    }

    fun requestPick() {
        if (disabled) Unit
        else if (limitReached) onExceed?.invoke()
        else onPickFiles()
    }

    fun commitFiles(next: List<ElUploadFile>) {
        internalFiles = next
        onFilesChange(next)
    }

    fun handleStart(file: ElUploadFile) {
        if (limit != null && internalFiles.size >= limit) {
            onExceed?.invoke()
            return
        }
        if (beforeUpload?.invoke(file) == false) return
        commitFiles(internalFiles + file)
        if (autoUpload) onUploadFile?.invoke(file)
    }

    fun handleRemove(file: ElUploadFile) {
        if (beforeRemove?.invoke(file) == false) return
        commitFiles(internalFiles - file)
    }

    fun submitAll() {
        internalFiles.filter { it.status == ElUploadStatus.Ready }.forEach { onUploadFile?.invoke(it) }
    }

    fun abortUpload(uid: String?) {
        val targets = internalFiles.filter {
            it.status == ElUploadStatus.Uploading && (uid == null || it.uid == uid)
        }
        if (targets.isEmpty()) return
        commitFiles(
            internalFiles.map { file ->
                if (targets.any { it.uid == file.uid }) file.copy(status = ElUploadStatus.Fail, error = "aborted")
                else file
            },
        )
    }

    SideEffect {
        controller?.apply {
            submitAction = { submitAll() }
            abortAction = { uid -> abortUpload(uid) }
            clearAction = { commitFiles(emptyList()) }
            startAction = { file -> handleStart(file) }
            removeAction = { file -> handleRemove(file) }
            pickAction = { requestPick() }
        }
    }

    // Lifecycle callbacks derived from the controlled file-list transitions.
    var previousFiles by remember { mutableStateOf(files) }
    LaunchedEffect(files) {
        val previousUids = previousFiles.map { it.uid }.toSet()
        val currentUids = files.map { it.uid }.toSet()
        files.filter { it.uid !in previousUids }.forEach { onChange?.invoke(it, files) }
        previousFiles.filter { it.uid !in currentUids }.forEach { onRemove?.invoke(it, files) }
        files.forEach { current ->
            val previous = previousFiles.firstOrNull { it.uid == current.uid } ?: return@forEach
            if (current.status == ElUploadStatus.Uploading && current.percentage != previous.percentage) {
                onProgress?.invoke(current, current.percentage)
            }
            if (current.status != previous.status) {
                when (current.status) {
                    ElUploadStatus.Success -> onSuccess?.invoke(current)
                    ElUploadStatus.Fail -> onError?.invoke(current)
                    else -> Unit
                }
            }
        }
        previousFiles = files
    }

    val pickContent: @Composable () -> Unit = {
        if (trigger != null) {
            Box(Modifier.clickable(enabled = !disabled) { requestPick() }) { trigger() }
        } else {
            ElButton(
                onClick = { requestPick() },
                type = ElType.Primary,
                disabled = disabled || limitReached,
                text = resolvedButtonText,
                icon = if (directory) ElIcons.Folder else ElIcons.Upload,
            )
        }
    }
    val cardMode = listType == ElUploadListType.PictureCard && showFileList
    val appendTile: @Composable () -> Unit = {
        Box(
            Modifier
                .width(100.dp)
                .height(120.dp)
                .dashedBorder(colors.border, theme.radius.base)
                .background(colors.fillLighter, RoundedCornerShape(theme.radius.base))
                .clip(RoundedCornerShape(theme.radius.base))
                .clickable(enabled = !disabled) { requestPick() },
            contentAlignment = Alignment.Center,
        ) {
            if (trigger != null) {
                trigger()
            } else {
                ElIcon(ElIcons.Plus, size = 24.dp, tint = colors.textPlaceholder)
            }
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (drag) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .dashedBorder(colors.border, theme.radius.base)
                    .background(colors.fillLighter)
                    .clip(RoundedCornerShape(theme.radius.base))
                    .clickable(enabled = !disabled && !limitReached, onClick = { requestPick() }),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ElIcon(if (directory) ElIcons.Folder else ElIcons.Upload, size = 42.dp, tint = colors.textPlaceholder)
                Spacer(Modifier.height(8.dp))
                if (trigger != null) {
                    trigger()
                } else {
                    BasicText(
                        text = if (limitReached) "${files.size}/$limit" else (tip ?: resolvedDropText),
                        style = theme.typography.base.copy(color = colors.textRegular),
                    )
                }
                if (accept != null) {
                    Spacer(Modifier.height(4.dp))
                    BasicText(text = accept, style = theme.typography.extraSmall.copy(color = colors.textSecondary))
                }
            }
            if (tipContent != null) {
                tipContent()
            }
        } else if (!cardMode) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                pickContent()
                if (tip != null) {
                    Spacer(Modifier.width(8.dp))
                    BasicText(text = tip, style = theme.typography.extraSmall.copy(color = colors.textSecondary))
                }
            }
            if (tipContent != null) tipContent()
        }
        if (cardMode) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                files.forEach { file ->
                    if (fileContent != null) {
                        fileContent(file)
                    } else {
                        UploadPictureCard(
                            file = file,
                            onRemove = { handleRemove(file) },
                            onPreview = { onPreview?.invoke(file) },
                        )
                    }
                }
                if (!limitReached) appendTile()
            }
            if (tipContent != null) {
                tipContent()
            } else if (tip != null) {
                BasicText(text = tip, style = theme.typography.extraSmall.copy(color = colors.textSecondary))
            }
        } else if (showFileList && files.isNotEmpty()) {
            when (listType) {
                ElUploadListType.Text -> Column(Modifier.fillMaxWidth()) {
                    files.forEach { file ->
                        if (fileContent != null) {
                            fileContent(file)
                        } else {
                            UploadTextRow(
                                file = file,
                                onRemove = { handleRemove(file) },
                                onPreview = { onPreview?.invoke(file) },
                                onUpload = { onUploadFile?.invoke(file) },
                            )
                        }
                    }
                }
                ElUploadListType.Picture -> Column(Modifier.fillMaxWidth()) {
                    files.forEach { file ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            UploadThumbnail(file, 40.dp)
                            Spacer(Modifier.width(8.dp))
                            if (fileContent != null) {
                                Box(Modifier.weight(1f)) { fileContent(file) }
                            } else {
                                UploadTextRow(
                                    file = file,
                                    modifier = Modifier.weight(1f),
                                    onRemove = { handleRemove(file) },
                                    onPreview = { onPreview?.invoke(file) },
                                    onUpload = { onUploadFile?.invoke(file) },
                                )
                            }
                        }
                    }
                }
                ElUploadListType.PictureCard -> Unit
            }
        }
    }
}

@Composable
private fun UploadTextRow(
    file: ElUploadFile,
    onRemove: () -> Unit,
    onPreview: () -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Row(modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        ElIcon(ElIcons.Document, size = 16.dp, tint = colors.textPlaceholder)
        Spacer(Modifier.width(6.dp))
        BasicText(
            text = file.name,
            modifier = Modifier.weight(1f).clickable {
                if (file.status == ElUploadStatus.Ready) onUpload() else onPreview()
            },
            style = theme.typography.small.copy(
                color = when (file.status) {
                    ElUploadStatus.Fail -> colors.danger
                    ElUploadStatus.Success -> colors.success
                    else -> colors.textRegular
                },
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (file.status) {
            ElUploadStatus.Uploading -> {
                Box(Modifier.width(80.dp).height(4.dp).background(colors.fill)) {
                    Box(
                        Modifier
                            .fillMaxWidth((file.percentage / 100.0).toFloat().coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(colors.primary),
                    )
                }
                Spacer(Modifier.width(6.dp))
                BasicText(
                    text = "${file.percentage.toInt()}%",
                    style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                )
            }
            ElUploadStatus.Success -> ElIcon(ElIcons.CircleCheckFilled, size = 14.dp, tint = colors.success)
            ElUploadStatus.Fail -> ElIcon(ElIcons.CircleCloseFilled, size = 14.dp, tint = colors.danger)
            ElUploadStatus.Ready -> Unit
        }
        Spacer(Modifier.width(8.dp))
        ElIcon(
            ElIcons.Close,
            size = 14.dp,
            tint = colors.textPlaceholder,
            contentDescription = LocalElConfig.current.locale.upload.delete,
            modifier = Modifier.clickable(onClick = onRemove),
        )
    }
}

@Composable
private fun UploadThumbnail(file: ElUploadFile, size: Dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(LocalElTheme.current.colors.fillLight),
        contentAlignment = Alignment.Center,
    ) {
        if (file.thumbnail != null) {
            Image(file.thumbnail, contentDescription = file.name, modifier = Modifier.size(size), contentScale = ContentScale.Crop)
        } else {
            ElIcon(ElIcons.Picture, size = 20.dp, tint = LocalElTheme.current.colors.textPlaceholder)
        }
    }
}

@Composable
private fun UploadPictureCard(
    file: ElUploadFile,
    onRemove: () -> Unit,
    onPreview: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Column(
        Modifier
            .width(100.dp)
            .height(120.dp)
            .border(1.dp, colors.borderLight, RoundedCornerShape(4.dp))
            .background(colors.fillLighter, RoundedCornerShape(4.dp)),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(86.dp)
                .clickable(onClick = onPreview),
            contentAlignment = Alignment.Center,
        ) {
            UploadThumbnail(file, 60.dp)
        }
        Row(
            Modifier.fillMaxWidth().height(30.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = file.name,
                modifier = Modifier.weight(1f),
                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            ElIcon(
                ElIcons.Close,
                size = 12.dp,
                tint = colors.textPlaceholder,
                contentDescription = LocalElConfig.current.locale.upload.delete,
                modifier = Modifier.clickable(onClick = onRemove),
            )
        }
    }
}

private fun Modifier.dashedBorder(color: Color, radius: Dp): Modifier =
    this.drawBehind {
        val stroke = 1.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(size.width - stroke, size.height - stroke),
            cornerRadius = CornerRadius(radius.toPx()),
            style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))),
        )
    }
