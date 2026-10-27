package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElCarousel
import cn.enaium.elementplus.components.ElCarouselArrow
import cn.enaium.elementplus.components.ElCarouselIndicatorPosition
import cn.enaium.elementplus.components.ElCarouselTrigger
import cn.enaium.elementplus.components.ElCarouselType
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

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
    DemoExample("Card scale", "Control how small the inactive slides become in card mode.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            type = ElCarouselType.Card,
            cardScale = 0.6f,
            loop = false,
        ) { Slide(it) }
    }
    DemoExample("Pause on hover", "Hovering the carousel pauses autoplay by default.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            autoplay = true,
            interval = 1500,
            pauseOnHover = true,
        ) { Slide(it) }
        DemoHint("Autoplay stops while the pointer is over the carousel and resumes when it leaves.")
    }
    DemoExample("Indicator trigger", "Switch slides on indicator hover or only on click.") {
        var trigger by remember { mutableStateOf(ElCarouselTrigger.Hover.name) }
        ElRadioGroup(
            options = listOf(
                ElRadioOption(ElCarouselTrigger.Hover.name, "Hover"),
                ElRadioOption(ElCarouselTrigger.Click.name, "Click"),
            ),
            selected = trigger,
            onSelectedChange = { trigger = it },
            button = true,
            size = ElSize.Small,
        )
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(180.dp),
            autoplay = false,
            trigger = if (trigger == ElCarouselTrigger.Click.name) ElCarouselTrigger.Click else ElCarouselTrigger.Hover,
            indicatorPosition = ElCarouselIndicatorPosition.Outside,
        ) { Slide(it) }
    }
    DemoExample("Auto height", "The container follows the tallest slide instead of a fixed height.") {
        val heights = listOf(120.dp, 190.dp, 90.dp)
        ElCarousel(itemCount = 3, autoplay = false, autoHeight = true) { index ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(heights[index])
                    .background(listOf(colors.primary, colors.success, colors.warning)[index % 3], RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = "Slide ${index + 1} · ${heights[index].value.toInt()}dp",
                    style = LocalElTheme.current.typography.medium.copy(color = Color.White),
                )
            }
        }
        DemoHint("Slides should use natural heights; fillMaxSize content cannot be measured automatically.")
    }
    DemoExample("Motion blur", "Blur slides while they are moving for a sense of speed.") {
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            interval = 1200,
            motionBlur = true,
        ) { Slide(it) }
    }
    DemoExample("Change event", "Track the current and previous slide through onChange.") {
        var current by remember { mutableIntStateOf(0) }
        var previous by remember { mutableIntStateOf(0) }
        ElCarousel(
            itemCount = 3,
            modifier = Modifier.fillMaxWidth().height(180.dp),
            onChange = { now, before ->
                current = now
                previous = before
            },
        ) { Slide(it) }
        DemoHint("Current: ${current + 1} · Previous: ${previous + 1}")
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
