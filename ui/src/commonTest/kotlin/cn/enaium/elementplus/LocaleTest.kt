package cn.enaium.elementplus

import cn.enaium.elementplus.components.ElFormRule
import cn.enaium.elementplus.components.validateElRules
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.theme.ElLocales
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocaleTest {

    @Test
    fun allElementPlusLocalesAreAvailable() {
        assertEquals(68, ElLocales.all.size)
        assertEquals(68, ElLocales.all.map { it.name }.distinct().size)
        assertTrue(ElLocales.all.all { it.name.isNotBlank() })
    }

    @Test
    fun localeLookupFallsBackFromRegionToLanguage() {
        assertEquals("fr", ElLocales.byName("fr-FR").name)
        assertEquals("zh-cn", ElLocales.byName("zh").name)
        assertEquals("pt-br", ElLocales.byName("pt_BR").name)
        assertEquals("en", ElLocales.byName("unknown").name)
    }

    @Test
    fun rtlLocalesExposeDirectionMetadata() {
        assertTrue(ElLocales.ar.isRtl)
        assertTrue(ElLocales.fa.isRtl)
        assertTrue(ElLocales.he.isRtl)
        assertFalse(ElLocales.fr.isRtl)
        assertFalse(ElLocales.zhCn.isRtl)
    }

    @Test
    fun chineseStringsCoverComponentChrome() {
        val locale = ElLocales.zhCn
        assertEquals("暂无数据", locale.emptyText)
        assertEquals("共 {total} 条", locale.pagination.total)
        assertEquals("选择日期", locale.datePicker.selectDate)
        assertEquals("请选择", locale.select.placeholder)
        assertEquals("输入的数据不合法!", locale.messageBox.error)
        assertEquals("此字段为必填项", locale.form.required)
        assertEquals("加载中...", locale.infiniteScroll.loading)
    }

    @Test
    fun localeDataFallsBackToEnglishForExtensionKeys() {
        assertEquals(ElLocale.English.upload.selectFile, ElLocales.fr.upload.selectFile)
        assertEquals(ElLocale.English.form.email, ElLocales.de.form.email)
        assertEquals(ElLocale.English.search, ElLocales.ja.search)
    }

    @Test
    fun chineseValidationMessagesUseLocaleAndLabel() {
        val required = listOf(ElFormRule(required = true))
        assertEquals("请输入名称", validateElRules("", required, ElLocales.zhCn, "名称"))
        assertEquals("请输入有效的邮箱地址", validateElRules(
            "not-an-email",
            listOf(ElFormRule(type = cn.enaium.elementplus.components.ElFormRuleType.Email)),
            ElLocales.zhCn,
            "Email",
        ))
        assertEquals("This field is required", validateElRules("", required, ElLocale.English))
    }

    @Test
    fun everyLocaleHasNonEmptyPrimarySections() {
        ElLocales.all.forEach { locale ->
            assertNotNull(locale.pagination)
            assertTrue(locale.datePicker.months.size == 12)
            assertTrue(locale.datePicker.weekdays.size == 7)
            assertTrue(locale.select.placeholder.isNotBlank())
            assertTrue(locale.table.emptyText.isNotBlank())
        }
    }
}
