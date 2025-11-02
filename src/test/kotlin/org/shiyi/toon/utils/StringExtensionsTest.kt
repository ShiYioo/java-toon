package org.shiyi.toon.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

/**
 * 字符串扩展函数的单元测试
 */
class StringExtensionsTest {

    @Test
    fun `escapeForToon should escape special characters`() {
        assertEquals("\\\\", "\\".escapeForToon())
        assertEquals("\\\"", "\"".escapeForToon())
        assertEquals("\\n", "\n".escapeForToon())
        assertEquals("\\r", "\r".escapeForToon())
        assertEquals("\\t", "\t".escapeForToon())

        assertEquals("Hello\\nWorld", "Hello\nWorld".escapeForToon())
        assertEquals("Say \\\"Hi\\\"", "Say \"Hi\"".escapeForToon())
    }

    @Test
    fun `unescapeFromToon should unescape sequences`() {
        assertEquals("\\", "\\\\".unescapeFromToon())
        assertEquals("\"", "\\\"".unescapeFromToon())
        assertEquals("\n", "\\n".unescapeFromToon())
        assertEquals("\r", "\\r".unescapeFromToon())
        assertEquals("\t", "\\t".unescapeFromToon())

        assertEquals("Hello\nWorld", "Hello\\nWorld".unescapeFromToon())
        assertEquals("Say \"Hi\"", "Say \\\"Hi\\\"".unescapeFromToon())
    }

    @Test
    fun `escape and unescape should be reversible`() {
        val original = "Hello\n\"World\"\t\r\\"
        val escaped = original.escapeForToon()
        val unescaped = escaped.unescapeFromToon()

        assertEquals(original, unescaped)
    }

    @Test
    fun `unescapeFromToon should throw on invalid escape sequence`() {
        assertThrows<IllegalArgumentException> {
            "\\x".unescapeFromToon()
        }
    }

    @Test
    fun `unescapeFromToon should throw on trailing backslash`() {
        assertThrows<IllegalArgumentException> {
            "test\\".unescapeFromToon()
        }
    }

    @Test
    fun `findClosingQuote should find correct position`() {
        val text = "\"Hello World\""
        assertEquals(12, text.findClosingQuote(0))

        val textWithEscape = "\"Hello \\\"World\\\"\""
        assertEquals(16, textWithEscape.findClosingQuote(0))
    }

    @Test
    fun `findClosingQuote should return -1 when not found`() {
        assertEquals(-1, "\"no closing".findClosingQuote(0))
    }

    @Test
    fun `findClosingQuote should skip escaped quotes`() {
        val text = "\"a\\\"b\\\"c\""
        assertEquals(8, text.findClosingQuote(0))
    }

    @Test
    fun `findUnquotedChar should find character outside quotes`() {
        val text = "key: \"value with : inside\""
        assertEquals(3, text.findUnquotedChar(':', 0))

        val text2 = "\"quoted:text\":value"
        assertEquals(13, text2.findUnquotedChar(':', 0))
    }

    @Test
    fun `findUnquotedChar should return -1 when not found`() {
        assertEquals(-1, "no colon here".findUnquotedChar(':', 0))
        assertEquals(-1, "\"all:in:quotes\"".findUnquotedChar(':', 0))
    }

    @Test
    fun `findUnquotedChar should work with start offset`() {
        val text = "key1:value1,key2:value2"
        assertEquals(4, text.findUnquotedChar(':', 0))
        assertEquals(16, text.findUnquotedChar(':', 5))
    }
}

