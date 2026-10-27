package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun CarouselDocs() {
    val colors = LocalElTheme.current.colors
    @Composable
    fun Slide(index: Int) {
        Box(
            Modifier.fillMaxSize().background(
                listOf(colors.primary, colors.success, colors.warning)[index % 3],
                RoundedCornerShape(6.dp),
            ),
            contentAlignment = Alignment.Center,
        ) {
            BasicText("Slide ${index + 1}", style = LocalElTheme.current.typography.extraLarge.copy(color = Color.White))
        }
    }
    DemoExample("Basic usage", "Autoplay a set of slides with indicators.") {
        ElCarousel(itemCount = 3, modifier = Modifier.fillMaxWidth().height(200.dp)) { Slide(it) }
    }
    DemoExample("Arrows", "Show arrows all the time.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            arrow = ElCarouselArrow.Always,
            indicatorPosition = ElCarouselIndicatorPosition.Outside,
        ) { Slide(it) }
    }
    DemoExample("Card mode", "Scale non-active slides for a card effect.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            type = ElCarouselType.Card,
            loop = false,
        ) { Slide(it) }
    }
    DemoExample("Custom interval", "Control autoplay speed and disable looping.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            autoplay = true,
            interval = 1500,
            loop = false,
            arrow = ElCarouselArrow.Hover,
        ) { Slide(it) }
    }

    DemoExample("No indicators or arrows", "Disable indicators and arrows for a clean slide surface.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            arrow = ElCarouselArrow.Never,
            indicatorPosition = ElCarouselIndicatorPosition.None,
        ) { Slide(it) }
    }

    DemoExample("Indicators", "Place indicators inside or outside the carousel.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElCarousel(itemCount = 3, modifier = Modifier.fillMaxWidth().height(180.dp), indicatorPosition = ElCarouselIndicatorPosition.Outside) { Slide(it) }
            ElCarousel(itemCount = 3, modifier = Modifier.fillMaxWidth().height(180.dp), indicatorPosition = ElCarouselIndicatorPosition.Inside) { Slide(it) }
        }
    }

    DemoExample("Vertical", "Run the carousel vertically.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.width(360.dp).height(300.dp),
            vertical = true,
            autoplay = false,
        ) { Slide(it) }
    }
}
