package cn.enaium.elementplus.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.sample.gallery.SampleApp
import cn.enaium.elementplus.sample.gallery.SampleComponentPage
import cn.enaium.elementplus.sample.gallery.sampleComponents
import cn.enaium.elementplus.theme.ElConfigProvider
import cn.enaium.elementplus.theme.ElTheme
import cn.enaium.elementplus.theme.LocalElTheme
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import org.jetbrains.skia.EncodedImageFormat

@OptIn(ExperimentalComposeUiApi::class)
class SampleRenderTest {

    private fun saveScene(
        file: File,
        width: Int,
        height: Int,
        density: Float,
        content: @Composable () -> Unit,
    ) {
        val scene = ImageComposeScene(
            width = width,
            height = height,
            density = Density(density),
            coroutineContext = Dispatchers.Unconfined,
            content = content,
        )
        try {
            val image = scene.render()
            val data = image.encodeToData(EncodedImageFormat.PNG) ?: error("PNG encoding failed")
            file.parentFile?.mkdirs()
            file.writeBytes(data.bytes)
            assertTrue(file.length() > 1000, "${file.name} screenshot is empty")
            image.close()
        } finally {
            scene.close()
        }
    }

    @Test
    fun renderEveryComponentDocsPage() {
        val out = File("build/screenshots/components")
        sampleComponents().forEach { component ->
            saveScene(
                file = File(out, "${component.id}.png"),
                width = 1200,
                height = 1500,
                density = 1.0f,
            ) {
                ElConfigProvider {
                    Box(Modifier.fillMaxSize().background(LocalElTheme.current.colors.backgroundPage)) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            BasicText(component.title, style = LocalElTheme.current.typography.extraLarge)
                            BasicText(component.description, style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary))
                            SampleComponentPage(component)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun renderApplicationsShell() {
        saveScene(
            file = File("build/screenshots/app-shell.png"),
            width = 1440,
            height = 900,
            density = 1.0f,
        ) { SampleApp() }
    }

    @Test
    fun renderDarkApplicationShell() {
        saveScene(
            file = File("build/screenshots/app-shell-dark.png"),
            width = 1440,
            height = 900,
            density = 1.0f,
        ) { SampleApp(initialDark = true) }
    }

    @Test
    fun renderDarkTheme() {
        val component = sampleComponents().first { it.id == "button" }
        saveScene(
            file = File("build/screenshots/dark-theme.png"),
            width = 1000,
            height = 1100,
            density = 1.0f,
        ) {
            ElConfigProvider(theme = ElTheme.Dark) {
                Box(Modifier.fillMaxSize().background(LocalElTheme.current.colors.backgroundPage)) {
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    ) {
                        BasicText(component.title, style = LocalElTheme.current.typography.extraLarge)
                        SampleComponentPage(component)
                    }
                }
            }
        }
    }
}
