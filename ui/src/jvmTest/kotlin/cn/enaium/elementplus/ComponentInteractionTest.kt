package cn.enaium.elementplus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.width
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.rightClick
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElCheckbox
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElInputNumberControlsPosition
import cn.enaium.elementplus.components.ElInputNumber
import cn.enaium.elementplus.components.ElMention
import cn.enaium.elementplus.components.ElMentionOption
import cn.enaium.elementplus.components.ElSelect
import cn.enaium.elementplus.components.ElTree
import cn.enaium.elementplus.components.ElPagination
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElTooltip
import cn.enaium.elementplus.components.ElTooltipTrigger
import cn.enaium.elementplus.components.ElDatePickerPanel
import cn.enaium.elementplus.components.ElDatePickerType
import cn.enaium.elementplus.components.ElSelectMultiple
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElSlider
import cn.enaium.elementplus.components.ElDialog
import cn.enaium.elementplus.components.ElDropdown
import cn.enaium.elementplus.components.ElMenu
import cn.enaium.elementplus.components.ElMenuItem
import cn.enaium.elementplus.components.ElMenuMode
import cn.enaium.elementplus.components.ElSubMenu
import cn.enaium.elementplus.components.ElDropdownItem
import cn.enaium.elementplus.components.ElDropdownTrigger
import cn.enaium.elementplus.components.ElTabData
import cn.enaium.elementplus.components.ElTabs
import cn.enaium.elementplus.components.ElMessageBoxHost
import cn.enaium.elementplus.components.rememberElMessageBoxService
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.theme.ElConfigProvider
import cn.enaium.elementplus.theme.ElMessageConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalTestApi::class)
class ComponentInteractionTest {

    @Test
    fun buttonAndCheckboxRespondToClicks() = runComposeUiTest {
        var clicks = 0
        var checked = false
        setContent {
            ElConfigProvider {
                Column {
                    ElButton(onClick = { clicks++ }, text = "Increment")
                    ElCheckbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        label = "Agree",
                    )
                }
            }
        }
        onNodeWithText("Increment").performClick()
        assertEquals(1, clicks)
        onNodeWithText("Agree").performClick()
        assertTrue(checked)
    }

    @Test
    fun inputAcceptsTextAndButtonsDisable() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                Column {
                    ElInput(value = value, onValueChange = { value = it }, placeholder = "Name")
                    ElButton(onClick = {}, text = "Disabled", disabled = true)
                }
            }
        }
        onNode(hasSetTextAction()).performTextInput("Element")
        assertEquals("Element", value)
        onNodeWithText("Disabled").assertIsNotEnabled()
    }

    @Test
    fun selectChoosesAnOption() = runComposeUiTest {
        var value by mutableStateOf<String?>(null)
        val options = listOf(
            ElSelectOption("beijing", "Beijing"),
            ElSelectOption("chengdu", "Chengdu"),
        )
        setContent {
            ElConfigProvider {
                ElSelect(
                    value = value,
                    onValueChange = { value = it },
                    options = options,
                    placeholder = "Pick a city",
                    modifier = Modifier.width(200.dp),
                )
            }
        }
        onNodeWithText("Pick a city").performClick()
        onNodeWithText("Chengdu").performClick()
        assertEquals("chengdu", value)
    }

    @Test
    fun dropdownRunsCommandThroughTriggerOverlay() = runComposeUiTest {
        var command = ""
        setContent {
            ElConfigProvider {
                ElDropdown(
                    items = listOf(ElDropdownItem("Edit", command = "edit")),
                    onCommand = { command = it },
                    trigger = ElDropdownTrigger.Click,
                ) {
                    ElButton(onClick = {}, text = "Actions")
                }
            }
        }
        onNodeWithText("Actions").performClick()
        onNodeWithText("Edit").performClick()
        assertEquals("edit", command)
    }

    @Test
    fun dialogOpensAndCloses() = runComposeUiTest {
        var visible by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                ElButton(onClick = { visible = true }, text = "Open")
                ElDialog(
                    visible = visible,
                    onClose = { visible = false },
                    title = "Dialog",
                    footer = { ElButton(onClick = { visible = false }, text = "OK") },
                ) {
                    BasicText("Dialog body")
                }
            }
        }
        onNodeWithText("Open").performClick()
        onNodeWithText("Dialog body").assertExists()
        onNodeWithText("OK").performClick()
        assertFalse(visible)
    }

    @Test
    fun datePanelSelectsADay() = runComposeUiTest {
        var date by mutableStateOf<LocalDate?>(null)
        setContent {
            ElConfigProvider {
                ElDatePickerPanel(value = date, onValueChange = { date = it })
            }
        }
        onNodeWithText("15").performClick()
        assertNotNull(date)
        assertEquals(15, date!!.day)
    }

    @Test
    fun tabsSwitchContent() = runComposeUiTest {
        var active by mutableStateOf("one")
        val tabs = listOf(
            ElTabData("one", "One") { BasicText("First content") },
            ElTabData("two", "Two") { BasicText("Second content") },
        )
        setContent {
            ElConfigProvider {
                ElTabs(tabs = tabs, activeName = active, onTabChange = { active = it })
            }
        }
        onNodeWithText("First content").assertExists()
        onNodeWithText("Two").performClick()
        onNodeWithText("Second content").assertExists()
    }

    @Test
    fun sliderDragUpdatesContinuously() = runComposeUiTest {
        var value by mutableStateOf(0.0)
        setContent {
            ElConfigProvider {
                ElSlider(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.width(200.dp).testTag("slider"),
                )
            }
        }
        onNodeWithTag("slider").performTouchInput {
            down(Offset(centerX, centerY))
            moveTo(Offset(width * 0.5f, centerY))
            moveTo(Offset(width * 0.9f, centerY))
            up()
        }
        assertTrue(value > 70.0, "Slider value after drag was $value")
    }

    @Test
    fun submenuPopupFitsItsWidestItem() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElMenu(activeIndex = "", onSelect = {}, mode = ElMenuMode.Horizontal) {
                    ElSubMenu("more", "More") {
                        ElMenuItem("a", "A")
                        ElMenuItem("b", "A much longer menu item")
                    }
                }
            }
        }
        onNodeWithText("More").performClick()
        val bounds = onNodeWithText("A much longer menu item").getUnclippedBoundsInRoot()
        val width = bounds.right.value - bounds.left.value
        assertTrue(width < 260f, "Submenu popup width was $width dp")
    }

    @Test
    fun yearPanelShowsTenYearWindow() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElDatePickerPanel(
                    value = LocalDate(2026, 1, 1),
                    onValueChange = {},
                    type = ElDatePickerType.Year,
                )
            }
        }
        onNodeWithText("2029").assertExists()
        onNodeWithText("2030").assertDoesNotExist()
        onNodeWithText("2031").assertDoesNotExist()
    }

    @Test
    fun collapsedSelectTagsShowRemainingCount() = runComposeUiTest {
        val options = (1..3).map { ElSelectOption(it.toString(), "Option $it") }
        setContent {
            ElConfigProvider {
                ElSelectMultiple(
                    values = listOf("1", "2", "3"),
                    onValueChange = {},
                    options = options,
                    collapseTags = true,
                    maxCollapseTags = 1,
                )
            }
        }
        onNodeWithText("+2").assertExists()
    }

    @Test
    fun inputReportsFocusChanges() = runComposeUiTest {
        var focused = false
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                ElInput(value = value, onValueChange = { value = it }, onFocusChanged = { focused = it })
            }
        }
        onNode(hasSetTextAction()).performClick()
        waitForIdle()
        assertTrue(focused, "Input did not report focus")
    }

    @Test
    fun mentionShowsSuggestionsAfterTypingTrigger() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                ElMention(
                    value = value,
                    onValueChange = { value = it },
                    options = listOf(ElMentionOption("alice", "Alice"), ElMentionOption("bob", "Bob")),
                )
            }
        }
        onNode(hasSetTextAction()).performClick()
        onNode(hasSetTextAction()).performTextInput("@")
        onNode(hasSetTextAction()).assertIsFocused()
        assertEquals("@", value)
        waitForIdle()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Alice").performClick()
        assertEquals("@alice ", value)
    }

    @Test
    fun submenuPopupCentersItemLabels() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElMenu(
                    activeIndex = "",
                    onSelect = {},
                    mode = ElMenuMode.Horizontal,
                    defaultOpeneds = setOf("more"),
                ) {
                    ElMenuItem("home", "Home")
                    ElSubMenu("more", "More") {
                        ElMenuItem("a", "A")
                        ElMenuItem("b", "A much longer menu item")
                    }
                }
            }
        }
        val short = onNodeWithText("A").getUnclippedBoundsInRoot()
        val long = onNodeWithText("A much longer menu item").getUnclippedBoundsInRoot()
        val shortCenter = (short.left.value + short.right.value) / 2f
        val longCenter = (long.left.value + long.right.value) / 2f
        assertEquals(longCenter, shortCenter, 1f)
    }

    @Test
    fun dropdownPopupWidthFitsContent() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElDropdown(
                    items = listOf(
                        ElDropdownItem("Edit"),
                        ElDropdownItem("A much longer menu item"),
                    ),
                    onCommand = {},
                    trigger = ElDropdownTrigger.Click,
                ) {
                    ElButton(onClick = {}, text = "Actions")
                }
            }
        }
        onNodeWithText("Actions").performClick()
        val bounds = onNodeWithText("A much longer menu item").getUnclippedBoundsInRoot()
        val width = bounds.right.value - bounds.left.value
        assertTrue(width < 300f, "Dropdown item width was $width dp")
    }

    @Test
    fun splitInputNumberControlsAdjustValue() = runComposeUiTest {
        var value by mutableStateOf<Double?>(5.0)
        setContent {
            ElConfigProvider {
                ElInputNumber(
                    value = value,
                    onValueChange = { value = it },
                    controlsPosition = ElInputNumberControlsPosition.Split,
                )
            }
        }
        onNodeWithContentDescription("increase number").performClick()
        assertEquals(6.0, value)
        onNodeWithContentDescription("decrease number").performClick()
        assertEquals(5.0, value)
    }

    @Test
    fun controlledTooltipShowsWithoutHover() = runComposeUiTest {
        var visible by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                ElTooltip(
                    text = "Controlled tooltip",
                    visible = visible,
                    onVisibleChange = { visible = it },
                ) {
                    ElButton(onClick = { visible = true }, text = "Show")
                }
            }
        }
        onNodeWithText("Controlled tooltip").assertDoesNotExist()
        onNodeWithText("Show").performClick()
        onNodeWithText("Controlled tooltip").assertExists()
    }

    @Test
    fun treeAccordionKeepsOneBranchExpanded() = runComposeUiTest {
        val data = listOf(
            ElTreeNode("p1", "Parent 1", listOf(ElTreeNode("a", "Child A"))),
            ElTreeNode("p2", "Parent 2", listOf(ElTreeNode("b", "Child B"))),
        )
        setContent {
            ElConfigProvider {
                ElTree(
                    data = data,
                    defaultExpandedKeys = setOf("p1"),
                    accordion = true,
                )
            }
        }
        onNodeWithText("Child A").assertExists()
        onNodeWithText("Parent 2").performClick()
        onNodeWithText("Child B").assertExists()
        onNodeWithText("Parent 1").performClick()
        onNodeWithText("Child A").assertExists()
        onNodeWithText("Child B").assertDoesNotExist()
    }

    @Test
    fun messageConfigAppliesLimitFromProvider() = runComposeUiTest {
        setContent {
            ElConfigProvider(message = ElMessageConfig(max = 2)) {
                ElMessage.Host {
                    ElButton(
                        onClick = {
                            ElMessage.show("Message one")
                            ElMessage.show("Message two")
                            ElMessage.show("Message three")
                        },
                        text = "Show",
                    )
                }
            }
        }
        onNodeWithText("Show").performClick()
        onNodeWithText("Message one").assertDoesNotExist()
        onNodeWithText("Message two").assertExists()
        onNodeWithText("Message three").assertExists()
        ElMessage.closeAll()
    }

    @Test
    fun tooltipContextMenuTriggerOpensOnSecondaryClick() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElTooltip(text = "Context menu tooltip", trigger = ElTooltipTrigger.ContextMenu) {
                    BasicText("Right click target")
                }
            }
        }
        onNodeWithText("Right click target").performMouseInput { rightClick() }
        onNodeWithText("Context menu tooltip").assertExists()
    }

    @Test
    fun messageServiceHostRendersMessages() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElMessage.Host {
                    ElButton(onClick = { ElMessage.show("Hello from message") }, text = "Show")
                }
            }
        }
        onNodeWithText("Show").performClick()
        onNodeWithText("Hello from message").assertExists()
        ElMessage.closeAll()
    }
}

@OptIn(ExperimentalTestApi::class)
class LocalizedRenderingTest {

    @Test
    fun configProviderAppliesChineseComponentStrings() = runComposeUiTest {
        setContent {
            ElConfigProvider(locale = cn.enaium.elementplus.theme.ElLocales.zhCn) {
                Column {
                    ElPagination(total = 100, currentPage = 1, onCurrentPageChange = {})
                    ElInput(value = "a", onValueChange = {}, maxLength = 10, showWordLimit = true)
                    cn.enaium.elementplus.components.ElEmpty()
                }
            }
        }
        onNodeWithText("共 100 条").assertExists()
        onNodeWithText("1 / 10 个字符").assertExists()
        onNodeWithText("暂无数据").assertExists()
    }

    @Test
    fun messageBoxUsesLocalizedDefaults() = runComposeUiTest {
        setContent {
            ElConfigProvider(locale = cn.enaium.elementplus.theme.ElLocales.zhCn) {
                val service = rememberElMessageBoxService()
                ElMessageBoxHost(service) {
                    ElButton(onClick = { service.confirm("保存修改？") }, text = "Open message box")
                }
            }
        }
        onNodeWithText("Open message box").performClick()
        onNodeWithText("提示").assertExists()
        onNodeWithText("保存修改？").assertExists()
        onNodeWithText("确定").assertExists()
        onNodeWithText("取消").assertExists()
        onNodeWithText("取消").performClick()
    }

    @Test
    fun chineseDatePickerUsesLocalizedMonthsAndToday() = runComposeUiTest {
        setContent {
            ElConfigProvider(locale = cn.enaium.elementplus.theme.ElLocales.zhCn) {
                ElDatePickerPanel(
                    value = LocalDate(2026, 1, 15),
                    onValueChange = {},
                )
            }
        }
        onNodeWithText("一月 2026").assertExists()
        onNodeWithText("今天").assertExists()
    }

    @Test
    fun configProviderAppliesRtlLayoutDirection() = runComposeUiTest {
        setContent {
            ElConfigProvider(locale = cn.enaium.elementplus.theme.ElLocales.ar) {
                androidx.compose.foundation.text.BasicText(
                    androidx.compose.ui.platform.LocalLayoutDirection.current.name,
                )
            }
        }
        onNodeWithText("Rtl").assertExists()
    }
}
