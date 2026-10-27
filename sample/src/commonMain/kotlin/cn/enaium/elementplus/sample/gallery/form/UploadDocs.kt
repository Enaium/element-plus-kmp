package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

@Composable
fun UploadDocs() {
    DemoExample("Basic usage", "The sample app simulates file picking and upload progress.") {
        UploadSample()
    }
    DemoExample("Drag and drop", "Use a large drop area for drag-style uploads.") {
        UploadSample(drag = true, limit = 5)
    }
    DemoExample("Picture card", "Render uploaded files as cards with thumbnails.") {
        UploadSample(listType = ElUploadListType.PictureCard)
    }
    DemoExample("Limit", "Prevent adding more files after the limit is reached.") {
        UploadSample(limit = 2)
    }

    DemoExample("File list with thumbnails", "Render existing files and their upload status.") {
        var files by remember {
            mutableStateOf(
                listOf(
                    ElUploadFile("1", "element-plus-logo.svg", thumbnail = ColorPainter(Color(0xFF409EFF)), status = ElUploadStatus.Success, percentage = 100.0),
                    ElUploadFile("2", "guide.pdf", thumbnail = ColorPainter(Color(0xFF67C23A)), status = ElUploadStatus.Uploading, percentage = 42.0),
                    ElUploadFile("3", "broken.docx", thumbnail = ColorPainter(Color(0xFFF56C6C)), status = ElUploadStatus.Fail, error = "Network error"),
                )
            )
        }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            listType = ElUploadListType.Picture,
            tip = "Existing files with status and thumbnails",
        )
    }
    DemoExample("Plain text file list with on-change", "Keep only the last three files using the on-change callback.") {
        var files by remember {
            mutableStateOf(
                listOf(
                    ElUploadFile("1", "food.jpeg", status = ElUploadStatus.Success),
                    ElUploadFile("2", "food2.jpeg", status = ElUploadStatus.Success),
                )
            )
        }
        var picked by remember { mutableStateOf(0) }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {
                picked += 1
                files = files + ElUploadFile("plain-$picked", "food-extra-$picked.jpeg", status = ElUploadStatus.Ready)
            },
            onChange = { _, list ->
                if (list.size > 3) files = list.takeLast(3)
            },
            tip = "Only the last three files are kept",
        )
    }
    DemoExample("Manual upload", "File picking is delegated; upload only when the user asks.") {
        var files by remember { mutableStateOf(listOf(ElUploadFile("1", "manual.pdf", status = ElUploadStatus.Ready))) }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            onUploadFile = { file ->
                files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Uploading, percentage = 50.0) else it }
            },
            tip = "Click a pending file to upload",
        )
    }

    DemoExample(
        "Manual submit with trigger and tip slots",
        "Use the trigger and tip slots and submit the pending files from a controller.",
    ) {
        var files by remember { mutableStateOf(emptyList<ElUploadFile>()) }
        var picked by remember { mutableStateOf(0) }
        val controller = remember { ElUploadController() }
        val scope = rememberCoroutineScope()
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {
                picked += 1
                files = files + ElUploadFile("manual-$picked", "manual-$picked.jpg", status = ElUploadStatus.Ready)
            },
            onUploadFile = { file ->
                files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Uploading, percentage = 20.0) else it }
                scope.launch {
                    delay(500)
                    files = files.map {
                        if (it.uid == file.uid) it.copy(status = ElUploadStatus.Success, percentage = 100.0) else it
                    }
                }
            },
            autoUpload = false,
            controller = controller,
            trigger = {
                ElButton(
                    onClick = { controller.pick() },
                    type = ElType.Primary,
                    text = "Select file",
                )
            },
            tipContent = { DemoHint("jpg/png files with a size less than 500kb") },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElButton(onClick = { controller.submit() }, type = ElType.Success, text = "Upload to server")
            ElButton(onClick = { controller.clearFiles() }, text = "Clear files")
        }
    }

    DemoExample(
        "Upload lifecycle hooks",
        "before-upload gates new files and on-success/on-error/on-progress report state transitions.",
    ) {
        var files by remember { mutableStateOf(emptyList<ElUploadFile>()) }
        var picked by remember { mutableStateOf(0) }
        var status by remember { mutableStateOf("idle") }
        val controller = remember { ElUploadController() }
        val scope = rememberCoroutineScope()
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {
                picked += 1
                val size = if (picked % 3 == 0) 3L * 1024 * 1024 else 400L * 1024
                controller.handleStart(
                    ElUploadFile("hook-$picked", "hook-$picked.png", size = size, status = ElUploadStatus.Ready),
                )
            },
            onUploadFile = { file ->
                files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Uploading, percentage = 0.0) else it }
                scope.launch {
                    files = files.map { if (it.uid == file.uid) it.copy(percentage = 45.0) else it }
                    delay(400)
                    files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Success, percentage = 100.0) else it }
                }
            },
            beforeUpload = { file ->
                if ((file.size ?: 0L) > 2L * 1024 * 1024) {
                    ElMessage.show("File size can not exceed 2MB!", ElMessageType.Error)
                    status = "rejected by before-upload"
                    false
                } else {
                    true
                }
            },
            onProgress = { file, percentage -> status = "${file.name}: ${percentage.toInt()}%" },
            onSuccess = { file -> status = "${file.name}: success" },
            onError = { file -> status = "${file.name}: error" },
            controller = controller,
            tip = "Files larger than 2MB are rejected before uploading",
        )
        DemoHint("Lifecycle: $status")
    }

    DemoExample(
        "Before remove validation",
        "Reject the removal of protected files with the before-remove hook.",
    ) {
        var files by remember {
            mutableStateOf(
                listOf(
                    ElUploadFile("1", "report.pdf", status = ElUploadStatus.Success, thumbnail = ColorPainter(Color(0xFF409EFF))),
                    ElUploadFile("2", "archive.locked", status = ElUploadStatus.Success, thumbnail = ColorPainter(Color(0xFFF56C6C))),
                )
            )
        }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            listType = ElUploadListType.Picture,
            beforeRemove = { file ->
                if (file.name.endsWith(".locked")) {
                    ElMessage.show("${file.name} can not be removed", ElMessageType.Warning)
                    false
                } else {
                    true
                }
            },
            tip = "archive.locked can not be removed",
        )
    }

    DemoExample(
        "Directory upload",
        "Pick or drop a whole directory; the injected picker returns the nested paths.",
    ) {
        var files by remember { mutableStateOf(emptyList<ElUploadFile>()) }
        var picked by remember { mutableStateOf(0) }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {
                picked += 1
                files = files + listOf(
                    ElUploadFile("dir-$picked-a", "assets/image-$picked.png", status = ElUploadStatus.Success, thumbnail = ColorPainter(Color(0xFF409EFF))),
                    ElUploadFile("dir-$picked-b", "assets/readme-$picked.md", status = ElUploadStatus.Success),
                )
            },
            drag = true,
            directory = true,
            multiple = true,
            tip = "Drop directory here or click to upload",
            tipContent = { DemoHint("Nested paths are preserved for directory uploads.") },
        )
    }

    DemoExample("Photo wall", "Render a picture wall with an append tile for adding photos.") {
        var files by remember {
            mutableStateOf(
                listOf(
                    ElUploadFile("1", "photo-1.jpg", thumbnail = ColorPainter(Color(0xFF409EFF)), status = ElUploadStatus.Success),
                    ElUploadFile("2", "photo-2.jpg", thumbnail = ColorPainter(Color(0xFF67C23A)), status = ElUploadStatus.Success),
                )
            )
        }
        var picked by remember { mutableStateOf(0) }
        val scope = rememberCoroutineScope()
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {
                picked += 1
                files = files + ElUploadFile(
                    "photo-$picked",
                    "photo-new-$picked.jpg",
                    status = ElUploadStatus.Uploading,
                    percentage = 60.0,
                    thumbnail = ColorPainter(Color(0xFFE6A23C)),
                )
                scope.launch {
                    delay(600)
                    files = files.map {
                        if (it.status == ElUploadStatus.Uploading) it.copy(status = ElUploadStatus.Success, percentage = 100.0) else it
                    }
                }
            },
            listType = ElUploadListType.PictureCard,
            tip = "Click the plus tile to add photos",
        )
    }

    DemoExample(
        "User avatar",
        "Use a trigger slot as the avatar and validate the picked image before uploading.",
    ) {
        var avatar by remember { mutableStateOf<ElUploadFile?>(null) }
        var picked by remember { mutableStateOf(0) }
        val controller = remember { ElUploadController() }
        val colors = LocalElTheme.current.colors
        ElUpload(
            files = listOfNotNull(avatar),
            onFilesChange = { list -> avatar = list.firstOrNull() },
            onPickFiles = {
                picked += 1
                controller.handleStart(
                    ElUploadFile(
                        uid = "avatar-$picked",
                        name = "avatar-$picked.jpg",
                        size = 600L * 1024,
                        status = ElUploadStatus.Success,
                        thumbnail = ColorPainter(colors.light(ElType.Primary, 7)),
                    ),
                )
            },
            showFileList = false,
            listType = ElUploadListType.PictureCard,
            controller = controller,
            beforeUpload = { file ->
                if ((file.size ?: 0L) > 2L * 1024 * 1024) {
                    ElMessage.show("Avatar picture size can not exceed 2MB!", ElMessageType.Error)
                    false
                } else {
                    true
                }
            },
            trigger = {
                Box(
                    Modifier
                        .size(120.dp)
                        .border(1.dp, colors.border, RoundedCornerShape(6.dp))
                        .background(colors.fillLighter, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    val thumbnail = avatar?.thumbnail
                    if (thumbnail != null) {
                        Image(
                            thumbnail,
                            contentDescription = avatar?.name,
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        DemoHint("+")
                    }
                }
            },
            tip = "Avatar must be a JPG under 2MB",
        )
    }

    DemoExample(
        "Custom file slot",
        "Replace the picture-card item content with a custom preview and remove action.",
    ) {
        var files by remember {
            mutableStateOf(
                listOf(
                    ElUploadFile("1", "custom.png", thumbnail = ColorPainter(Color(0xFFF56C6C)), status = ElUploadStatus.Success),
                )
            )
        }
        val controller = remember { ElUploadController() }
        val colors = LocalElTheme.current.colors
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            listType = ElUploadListType.PictureCard,
            controller = controller,
            fileContent = { file ->
                Column(
                    Modifier
                        .width(100.dp)
                        .height(120.dp)
                        .border(1.dp, colors.borderLight, RoundedCornerShape(4.dp))
                        .background(colors.fillLighter, RoundedCornerShape(4.dp)),
                ) {
                    Box(Modifier.fillMaxWidth().height(70.dp), contentAlignment = Alignment.Center) {
                        val thumbnail = file.thumbnail
                        if (thumbnail != null) {
                            Image(thumbnail, contentDescription = file.name, modifier = Modifier.size(50.dp))
                        } else {
                            DemoHint("file")
                        }
                    }
                    BasicText(
                        text = file.name,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        style = LocalElTheme.current.typography.extraSmall.copy(color = colors.textSecondary),
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        ElButton(onClick = {}, size = ElSize.Small, text = "Preview")
                        ElButton(onClick = { controller.handleRemove(file) }, size = ElSize.Small, text = "Delete")
                    }
                }
            },
            tip = "Custom file slot with a Delete action",
        )
    }

    DemoExample(
        "Controller methods",
        "submit, abort and clearFiles are available through ElUploadController.",
    ) {
        var files by remember { mutableStateOf(emptyList<ElUploadFile>()) }
        var picked by remember { mutableStateOf(0) }
        val controller = remember { ElUploadController() }
        val scope = rememberCoroutineScope()
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {
                picked += 1
                files = files + ElUploadFile("method-$picked", "method-$picked.pdf", status = ElUploadStatus.Ready)
            },
            onUploadFile = { file ->
                files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Uploading, percentage = 30.0) else it }
                scope.launch {
                    delay(1200)
                    files = files.map {
                        if (it.uid == file.uid && it.status == ElUploadStatus.Uploading) {
                            it.copy(status = ElUploadStatus.Success, percentage = 100.0)
                        } else {
                            it
                        }
                    }
                }
            },
            autoUpload = false,
            controller = controller,
            tip = "Select files, then submit, abort or clear them",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElButton(onClick = { controller.submit() }, type = ElType.Primary, text = "Submit")
            ElButton(onClick = { controller.abort() }, type = ElType.Warning, text = "Abort")
            ElButton(onClick = { controller.clearFiles() }, text = "Clear files")
        }
    }

    DemoExample(
        "Limit cover",
        "Exceeding the limit clears the old file and starts the new one with handleStart.",
    ) {
        var files by remember {
            mutableStateOf(listOf(ElUploadFile("cover-0", "cover-0.pdf", status = ElUploadStatus.Ready)))
        }
        var coverIndex by remember { mutableStateOf(0) }
        val controller = remember { ElUploadController() }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            limit = 1,
            autoUpload = false,
            controller = controller,
            onExceed = {
                controller.clearFiles()
                coverIndex += 1
                controller.handleStart(ElUploadFile("cover-$coverIndex", "cover-$coverIndex.pdf", status = ElUploadStatus.Ready))
            },
            trigger = { BasicText("Select file") },
            tipContent = { DemoHint("Limit 1 file, a new file covers the old one.") },
        )
    }

    DemoExample("Custom thumbnail", "Provide a thumbnail painter per file.") {
        var files by remember {
            mutableStateOf(listOf(ElUploadFile("1", "custom.png", thumbnail = ColorPainter(Color(0xFFF56C6C)), status = ElUploadStatus.Success)))
        }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            listType = ElUploadListType.Picture,
        )
    }
}

@Composable
private fun UploadSample(
    drag: Boolean = false,
    listType: ElUploadListType = ElUploadListType.Text,
    limit: Int? = null,
) {
    var files by remember { mutableStateOf(listOf<ElUploadFile>()) }
    val scope = rememberCoroutineScope()
    val colors = LocalElTheme.current.colors
    ElUpload(
        files = files,
        onFilesChange = { files = it },
        onPickFiles = {
            val index = files.size + 1
            files = files + ElUploadFile(
                uid = "file-$index",
                name = "file-$index.pdf",
                size = 1024L * index,
                status = ElUploadStatus.Ready,
                thumbnail = if (listType == ElUploadListType.PictureCard) ColorPainter(colors.light(ElType.Primary, 7)) else null,
            )
        },
        onUploadFile = { file ->
            files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Uploading) else it }
            scope.launch {
                delay(350)
                files = files.map { if (it.uid == file.uid) it.copy(status = ElUploadStatus.Success, percentage = 100.0) else it }
            }
        },
        drag = drag,
        listType = listType,
        limit = limit,
        tip = if (drag) "Drop files here or click to select" else "PDF or DOCX, up to 5 files",
        accept = ".pdf,.docx",
        multiple = limit == null || limit > 1,
    )
}
