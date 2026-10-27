package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

    DemoExample("Photo wall", "Render picture cards with thumbnails.") {
        var files by remember {
            mutableStateOf(
                listOf(
                    ElUploadFile("1", "photo-1.jpg", thumbnail = ColorPainter(Color(0xFF409EFF)), status = ElUploadStatus.Success),
                    ElUploadFile("2", "photo-2.jpg", thumbnail = ColorPainter(Color(0xFF67C23A)), status = ElUploadStatus.Success),
                )
            )
        }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            listType = ElUploadListType.PictureCard,
            tip = "Click to add more photos",
        )
    }
    DemoExample("User avatar", "Limit the picker to a single avatar image.") {
        var files by remember {
            mutableStateOf(listOf(ElUploadFile("1", "avatar.png", thumbnail = ColorPainter(Color(0xFFE6A23C)), status = ElUploadStatus.Success)))
        }
        ElUpload(
            files = files,
            onFilesChange = { files = it },
            onPickFiles = {},
            listType = ElUploadListType.PictureCard,
            limit = 1,
            tip = "Only one avatar can be uploaded",
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
