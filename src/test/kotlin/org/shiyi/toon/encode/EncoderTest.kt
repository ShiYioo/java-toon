package org.shiyi.toon.encode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.shiyi.toon.*

/**
 * 编码器的单元测试
 */
class EncoderTest {

    @Test
    fun `encode should handle primitives`() {
        assertEquals("null", encode(null, EncodeOptions()))
        assertEquals("true", encode(true, EncodeOptions()))
        assertEquals("false", encode(false, EncodeOptions()))
        assertEquals("42", encode(42, EncodeOptions()))
        assertEquals("3.14", encode(3.14, EncodeOptions()))
        assertEquals("hello", encode("hello", EncodeOptions()))
    }

    @Test
    fun `encode should quote strings with special characters`() {
        // 包含冒号的字符串需要引号
        val result = encode("has:colon", EncodeOptions())
        assertTrue(result.contains("\""))

        // 包含逗号的字符串需要引号
        val result2 = encode("a,b", EncodeOptions())
        assertTrue(result2.contains("\""))

        // 前后有空格需要引号（但中间空格不需要）
        val result3 = encode(" padded ", EncodeOptions())
        assertTrue(result3.contains("\""))
    }

    @Test
    fun `encode should not quote strings with internal spaces`() {
        // 中间的空格不需要引号（与 TypeScript 版本一致）
        val result = encode("hello world", EncodeOptions())
        assertEquals("hello world", result)

        // Unicode 和 emoji 带空格也不需要引号
        val result2 = encode("hello 👋 world", EncodeOptions())
        assertEquals("hello 👋 world", result2)
    }

    @Test
    fun `encode should handle simple objects`() {
        val data = mapOf(
            "name" to "Alice",
            "age" to 30
        )

        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("name:"))
        assertTrue(result.contains("age:"))
        assertTrue(result.contains("Alice"))
        assertTrue(result.contains("30"))
    }

    @Test
    fun `encode should handle nested objects`() {
        val data = mapOf(
            "user" to mapOf(
                "name" to "Bob",
                "age" to 25
            )
        )

        val result = encode(data, EncodeOptions(indent = 2))

        assertTrue(result.contains("user:"))
        assertTrue(result.contains("  name:"))
        assertTrue(result.contains("  age:"))
    }

    @Test
    fun `encode should handle arrays of primitives`() {
        val data = mapOf(
            "numbers" to listOf(1, 2, 3, 4, 5)
        )

        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("numbers[5]:"))
        assertTrue(result.contains("1, 2, 3, 4, 5") || result.contains("1,2,3,4,5"))
    }

    @Test
    fun `encode should handle empty arrays`() {
        val data = mapOf("empty" to emptyList<Any>())
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("empty[0]:"))
    }

    @Test
    fun `encode should handle arrays with custom delimiter`() {
        val data = mapOf(
            "items" to listOf("a", "b", "c")
        )

        val result = encode(data, EncodeOptions(delimiter = Delimiter.PIPE))

        assertTrue(result.contains("items[3|]:") || result.contains("items[3]:"))
        assertTrue(result.contains("|"))
    }

    @Test
    fun `encode should use tabular format for similar objects`() {
        // 相同字段的对象使用表格格式
        val data = mapOf(
            "items" to listOf(
                mapOf("sku" to "A1", "qty" to 2, "price" to 9.99),
                mapOf("sku" to "B2", "qty" to 1, "price" to 14.5)
            )
        )

        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("items[2]{sku,qty,price}:"))
        assertTrue(result.contains("A1,2,9.99"))
        assertTrue(result.contains("B2,1,14.5"))
    }

    @Test
    fun `encode should use list format for objects with different fields`() {
        // 不同字段的对象使用列表格式
        val data = mapOf(
            "items" to listOf(
                mapOf("id" to 1, "name" to "First"),
                mapOf("id" to 2, "name" to "Second", "extra" to true)
            )
        )

        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("items[2]:"))
        assertTrue(result.contains("- id:"))
        assertTrue(result.contains("name:"))
    }

    @Test
    fun `encode should use list format for objects with nested values`() {
        // 包含嵌套值的对象使用列表格式
        val data = mapOf(
            "items" to listOf(
                mapOf("id" to 1, "nested" to mapOf("x" to 1))
            )
        )

        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("items[1]:"))
        assertTrue(result.contains("- id:"))
        assertTrue(result.contains("nested:"))
    }


    @Test
    fun `encode should respect indent option`() {
        val data = mapOf(
            "parent" to mapOf(
                "child" to "value"
            )
        )

        val result = encode(data, EncodeOptions(indent = 4))

        assertTrue(result.contains("    child:"))
    }

    @Test
    fun `encode should handle empty objects`() {
        val data = mapOf("empty" to emptyMap<String, Any>())
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("empty:"))
    }

    @Test
    fun `encodePrimitive should handle all types`() {
        assertEquals("null", encodePrimitive(null))
        assertEquals("true", encodePrimitive(true))
        assertEquals("false", encodePrimitive(false))
        assertEquals("123", encodePrimitive(123))
        assertEquals("3.14", encodePrimitive(3.14))
        assertEquals("hello", encodePrimitive("hello"))
    }

    @Test
    fun `encodeKey should quote special keys`() {
        assertEquals("normal", encodeKey("normal"))
        assertEquals("\"true\"", encodeKey("true"))
        assertEquals("\"123\"", encodeKey("123"))
        assertEquals("\"has:colon\"", encodeKey("has:colon"))
    }

    @Test
    fun `formatHeader should format correctly`() {
        val header1 = formatHeader(5, "items", null, Delimiter.COMMA, false)
        assertEquals("items[5]:", header1)

        val header2 = formatHeader(3, null, listOf("a", "b"), Delimiter.PIPE, true)
        assertTrue(header2.contains("[#3|]"))
        assertTrue(header2.contains("{a|b}"))
    }
}

