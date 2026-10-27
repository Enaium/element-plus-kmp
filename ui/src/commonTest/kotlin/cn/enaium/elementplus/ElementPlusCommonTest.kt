package cn.enaium.elementplus

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.computeCascadeCheck
import cn.enaium.elementplus.components.flattenTree
import cn.enaium.elementplus.components.formatStatistic
import cn.enaium.elementplus.components.ElFormRule
import cn.enaium.elementplus.components.ElFormRuleType
import cn.enaium.elementplus.components.formatLocalDate
import cn.enaium.elementplus.components.formatLocalDateTime
import cn.enaium.elementplus.components.formatLocalTime
import cn.enaium.elementplus.components.validateElRules
import cn.enaium.elementplus.theme.formatElColor
import cn.enaium.elementplus.theme.parseElColor
import cn.enaium.elementplus.theme.hslToColor
import cn.enaium.elementplus.theme.rgbToHsl
import cn.enaium.elementplus.theme.rgbToHsv
import cn.enaium.elementplus.theme.hsvToColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ElementPlusCommonTest {

    @Test
    fun colorParsingAndConversion() {
        assertEquals(0xFF409EFF.toInt(), parseElColor("#409EFF")?.toArgb())
        assertEquals(0xFF409EFF.toInt(), parseElColor("rgb(64, 158, 255)")?.toArgb())
        assertEquals(0x80F56C6C.toInt(), parseElColor("#F56C6C80")?.toArgb())
        assertNull(parseElColor("not-a-color"))
        assertEquals("#409EFF", formatElColor(Color(0xFF409EFF), cn.enaium.elementplus.theme.ElColorFormat.Hex))
        val hsv = rgbToHsv(Color(0xFF409EFF))
        val back = hsvToColor(hsv.first, hsv.second, hsv.third)
        assertEquals(Color(0xFF409EFF).red, back.red, 0.01f)
        assertEquals(Color(0xFF409EFF).green, back.green, 0.01f)
        assertEquals(Color(0xFF409EFF).blue, back.blue, 0.01f)
    }

    @Test
    fun requiredAndTypedRules() {
        assertEquals("Required", validateElRules("", listOf(ElFormRule(required = true, message = "Required"))))
        assertNull(validateElRules("value", listOf(ElFormRule(required = true))))
        assertNotNull(validateElRules("bad@", listOf(ElFormRule(type = ElFormRuleType.Email, message = "Email"))))
        assertNull(validateElRules("a@b.co", listOf(ElFormRule(type = ElFormRuleType.Email))))
        assertNotNull(validateElRules("ab", listOf(ElFormRule(min = 3.0, message = "Too short"))))
        assertNull(validateElRules("abc", listOf(ElFormRule(min = 3.0, max = 4.0))))
        assertNull(validateElRules("abc123", listOf(ElFormRule(pattern = Regex("^[a-z0-9]+$")))))
        assertNotNull(validateElRules("abc!", listOf(ElFormRule(pattern = Regex("^[a-z0-9]+$"), message = "Pattern"))))
        assertNotNull(validateElRules(listOf("a"), listOf(ElFormRule(max = 0.0, message = "Max"))))
        assertEquals("Custom", validateElRules("x", listOf(ElFormRule(validator = { "Custom" }))))
    }

    @Test
    fun treeFlattenAndCascadeChecks() {
        val tree = listOf(
            ElTreeNode("root", "Root", listOf(
                ElTreeNode("a", "A"),
                ElTreeNode("b", "B", listOf(ElTreeNode("b1", "B1"))),
            )),
        )
        assertTrue(flattenTree(tree, setOf("root")).any { it.node.key == "b" })
        assertFalse(flattenTree(tree, emptySet()).any { it.node.key == "a" })
        val partial = computeCascadeCheck(tree, emptySet(), tree[0].children[0])
        assertTrue("a" in partial)
        assertFalse("root" in partial)
        val all = computeCascadeCheck(tree, setOf("b1"), tree[0])
        assertTrue("root" in all)
        assertTrue("a" in all)
        assertTrue("b" in all)
        assertTrue("b1" in all)
        val cleared = computeCascadeCheck(tree, all, tree[0])
        assertTrue(cleared.isEmpty())
    }

    @Test
    fun dateAndTimeFormatting() {
        val date = LocalDate(2026, 1, 2)
        assertEquals("2026-01-02", formatLocalDate(date, "YYYY-MM-DD"))
        assertEquals("2026/01/02", formatLocalDate(date, "YYYY/MM/DD"))
        assertEquals("09:05:07", formatLocalTime(LocalTime(9, 5, 7)))
        assertEquals(
            "2026-10-26 13:04:09",
            formatLocalDateTime(LocalDateTime(2026, 10, 26, 13, 4, 9)),
        )
    }

    @Test
    fun statisticFormatting() {
        assertEquals("1,234,568", formatStatistic(1234567.891, 0))
        assertEquals("1,234,567.89", formatStatistic(1234567.891, 2))
        assertEquals("98.6", formatStatistic(98.64, 1))
        assertEquals("-1,000", formatStatistic(-999.6, 0))
        assertEquals("1000", formatStatistic(1000.0, 0, ""))
    }
}

class ElementPlusStateTest {

    @Test
    fun formStateCollectsAndClearsErrors() {
        val form = cn.enaium.elementplus.components.ElFormState()
        val item = cn.enaium.elementplus.components.ElFormItemState()
        item.label = "name"
        item.value = ""
        item.rules = listOf(
            cn.enaium.elementplus.components.ElFormRule(required = true, message = "Name required"),
        )
        form.items.add(item)
        assertFalse(form.validate())
        val result = form.validateResult()
        assertFalse(result.valid)
        assertEquals("Name required", result.errors["name"])
        item.value = "Alice"
        assertTrue(form.validate())
        form.clearValidate()
        assertNull(item.error)
    }

    @Test
    fun messageAndNotificationServices() {
        val messages = cn.enaium.elementplus.components.ElMessageService()
        val first = messages.show("one")
        messages.show("two", type = cn.enaium.elementplus.components.ElMessageType.Success)
        assertEquals(2, messages.items.size)
        messages.close(first)
        assertEquals(1, messages.items.size)
        messages.closeAll()
        assertTrue(messages.items.isEmpty())

        val notifications = cn.enaium.elementplus.components.ElNotificationService()
        val notification = notifications.notify("Saved", "All changes stored")
        assertEquals(1, notifications.items.size)
        notifications.close(notification)
        assertTrue(notifications.items.isEmpty())
    }

    @Test
    fun hslColorParsing() {
        val color = parseElColor("hsl(210, 100%, 63%)")
        assertNotNull(color)
        val hsl = rgbToHsl(Color(0xFF409EFF))
        val regenerated = hslToColor(hsl.first, hsl.second, hsl.third)
        assertEquals(Color(0xFF409EFF).red, regenerated.red, 0.01f)
        assertEquals(Color(0xFF409EFF).green, regenerated.green, 0.01f)
        assertEquals(Color(0xFF409EFF).blue, regenerated.blue, 0.01f)
        val alpha = parseElColor("hsla(210, 100%, 63%, 0.5)")
        assertNotNull(alpha)
        assertEquals(0.5f, alpha.alpha, 0.01f)
    }
}
