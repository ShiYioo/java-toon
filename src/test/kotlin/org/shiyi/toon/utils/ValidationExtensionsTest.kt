package org.shiyi.toon.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * 验证扩展函数的单元测试
 */
class ValidationExtensionsTest {

    @Test
    fun `isBooleanOrNullLiteral should identify literals`() {
        assertTrue("true".isBooleanOrNullLiteral())
        assertTrue("false".isBooleanOrNullLiteral())
        assertTrue("null".isBooleanOrNullLiteral())

        assertFalse("True".isBooleanOrNullLiteral())
        assertFalse("FALSE".isBooleanOrNullLiteral())
        assertFalse("NULL".isBooleanOrNullLiteral())
        assertFalse("yes".isBooleanOrNullLiteral())
        assertFalse("no".isBooleanOrNullLiteral())
        assertFalse("".isBooleanOrNullLiteral())
    }

    @Test
    fun `isNumericLiteral should identify numbers`() {
        assertTrue("123".isNumericLiteral())
        assertTrue("0".isNumericLiteral())
        assertTrue("-42".isNumericLiteral())
        assertTrue("3.14".isNumericLiteral())
        assertTrue("-3.14".isNumericLiteral())
        assertTrue("1e10".isNumericLiteral())
        assertTrue("1.5e-3".isNumericLiteral())

        assertFalse("".isNumericLiteral())
        assertFalse("abc".isNumericLiteral())
        assertFalse("12abc".isNumericLiteral())
        assertFalse("1.2.3".isNumericLiteral())
    }

    @Test
    fun `isSafeUnquoted should check for safe strings`() {
        // 安全字符串
        assertTrue("hello".isSafeUnquoted(','))
        assertTrue("simple-value".isSafeUnquoted(','))
        assertTrue("value_123".isSafeUnquoted(','))

        // 需要引号的情况
        assertFalse("".isSafeUnquoted(','))
        assertFalse("true".isSafeUnquoted(','))
        assertFalse("false".isSafeUnquoted(','))
        assertFalse("null".isSafeUnquoted(','))
        assertFalse("123".isSafeUnquoted(','))
        assertFalse("has:colon".isSafeUnquoted(','))
        assertFalse("has,comma".isSafeUnquoted(','))
        assertFalse("has\"quote".isSafeUnquoted(','))
        assertFalse("has\nnewline".isSafeUnquoted(','))
        assertFalse("has\ttab".isSafeUnquoted(','))
        assertFalse("has\\backslash".isSafeUnquoted(','))
        assertFalse("- starts with dash".isSafeUnquoted(','))
        assertFalse("#comment".isSafeUnquoted(','))
        assertFalse("[array]".isSafeUnquoted(','))
        assertFalse(" leading space".isSafeUnquoted(','))
        assertFalse("trailing space ".isSafeUnquoted(','))
    }

    @Test
    fun `isSafeUnquoted should respect delimiter`() {
        assertTrue("has,comma".isSafeUnquoted('|'))
        assertFalse("has,comma".isSafeUnquoted(','))

        assertTrue("has|pipe".isSafeUnquoted(','))
        assertFalse("has|pipe".isSafeUnquoted('|'))
    }

    @Test
    fun `isValidUnquotedKey should check for valid keys`() {
        // 有效的键
        assertTrue("name".isValidUnquotedKey())
        assertTrue("user_name".isValidUnquotedKey())
        assertTrue("key123".isValidUnquotedKey())

        // 无效的键
        assertFalse("".isValidUnquotedKey())
        assertFalse("true".isValidUnquotedKey())
        assertFalse("123".isValidUnquotedKey())
        assertFalse("has:colon".isValidUnquotedKey())
        assertFalse("has\"quote".isValidUnquotedKey())
        assertFalse("has\nnewline".isValidUnquotedKey())
        assertFalse("- list".isValidUnquotedKey())
        assertFalse("[array]".isValidUnquotedKey())
        assertFalse(" space".isValidUnquotedKey())
    }

    @Test
    fun `isValidUnquotedKey should not check delimiter`() {
        // 键不需要检查分隔符
        assertTrue("has,comma".isValidUnquotedKey())
        assertTrue("has|pipe".isValidUnquotedKey())
    }
}

