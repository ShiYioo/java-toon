package org.shiyi.toon.decode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.shiyi.toon.*

/**
 * 解码器的单元测试
 */
class DecoderTest {

    @Test
    fun `decode should handle primitives`() {
        assertEquals(null, decode("null", DecodeOptions()))
        assertEquals(true, decode("true", DecodeOptions()))
        assertEquals(false, decode("false", DecodeOptions()))

        val num = decode("42", DecodeOptions())
        assertTrue(num is Number)

        assertEquals("hello", decode("hello", DecodeOptions()))
    }

    @Test
    fun `decode should handle simple objects`() {
        val toon = """
            name: Alice
            age: 30
        """.trimIndent()

        val result = decode(toon, DecodeOptions()) as Map<*, *>

        assertEquals("Alice", result["name"])
        assertEquals(30, (result["age"] as Number).toInt())
    }

    @Test
    fun `decode should handle nested objects`() {
        val toon = """
            user:
              name: Bob
              age: 25
        """.trimIndent()

        val result = decode(toon, DecodeOptions()) as Map<*, *>
        val user = result["user"] as Map<*, *>

        assertEquals("Bob", user["name"])
        assertEquals(25, (user["age"] as Number).toInt())
    }

    @Test
    fun `decode should handle quoted strings`() {
        val toon = """name: "Hello World""""
        val result = decode(toon, DecodeOptions()) as Map<*, *>

        assertEquals("Hello World", result["name"])
    }

    @Test
    fun `decode should handle escaped characters`() {
        val toon = """message: "Line1\nLine2""""
        val result = decode(toon, DecodeOptions()) as Map<*, *>

        assertEquals("Line1\nLine2", result["message"])
    }

    @Test
    fun `decode should handle inline arrays`() {
        val toon = """numbers[5]: 1, 2, 3, 4, 5"""
        val result = decode(toon, DecodeOptions()) as Map<*, *>
        val numbers = result["numbers"] as List<*>

        assertEquals(5, numbers.size)
        assertEquals(1, (numbers[0] as Number).toInt())
        assertEquals(5, (numbers[4] as Number).toInt())
    }

    @Test
    fun `decode should handle list arrays`() {
        val toon = """
            items[3]:
              - apple
              - banana
              - cherry
        """.trimIndent()

        val result = decode(toon, DecodeOptions()) as Map<*, *>
        val items = result["items"] as List<*>

        assertEquals(3, items.size)
        assertEquals("apple", items[0])
        assertEquals("banana", items[1])
        assertEquals("cherry", items[2])
    }

    @Test
    fun `decode should handle empty arrays`() {
        val toon = """empty[0]:"""
        val result = decode(toon, DecodeOptions()) as Map<*, *>
        val empty = result["empty"] as List<*>

        assertEquals(0, empty.size)
    }

    @Test
    fun `decode should handle tabular arrays`() {
        val toon = """
            users[2]{name,age}:
              Alice, 30
              Bob, 25
        """.trimIndent()

        val result = decode(toon, DecodeOptions()) as Map<*, *>
        val users = result["users"] as List<*>

        assertEquals(2, users.size)

        val user1 = users[0] as Map<*, *>
        assertEquals("Alice", user1["name"])
        assertEquals(30, (user1["age"] as Number).toInt())

        val user2 = users[1] as Map<*, *>
        assertEquals("Bob", user2["name"])
        assertEquals(25, (user2["age"] as Number).toInt())
    }

    @Test
    fun `decode should throw on empty input`() {
        assertThrows<IllegalArgumentException> {
            decode("", DecodeOptions())
        }

        assertThrows<IllegalArgumentException> {
            decode("   ", DecodeOptions())
        }
    }

    @Test
    fun `decode should enforce strict mode`() {
        val toon = """
            items[3]:
              - one
              - two
        """.trimIndent()

        // 严格模式下应该失败（声明3个但只有2个）
        assertThrows<IllegalArgumentException> {
            decode(toon, DecodeOptions(strict = true))
        }

        // 宽松模式下应该成功
        val result = decode(toon, DecodeOptions(strict = false)) as Map<*, *>
        val items = result["items"] as List<*>
        assertEquals(2, items.size)
    }

    @Test
    fun `decode should handle different indentation`() {
        val toon = """
            parent:
                child: value
        """.trimIndent()

        val result = decode(toon, DecodeOptions(indent = 4)) as Map<*, *>
        val parent = result["parent"] as Map<*, *>

        assertEquals("value", parent["child"])
    }

    @Test
    fun `decode should reject invalid indentation in strict mode`() {
        val toon = """
            parent:
               child: value
        """.trimIndent()

        // 3个空格不是2的倍数
        assertThrows<IllegalArgumentException> {
            decode(toon, DecodeOptions(indent = 2, strict = true))
        }
    }

    @Test
    fun `parsePrimitiveToken should parse correctly`() {
        assertEquals(null, parsePrimitiveToken("null"))
        assertEquals(true, parsePrimitiveToken("true"))
        assertEquals(false, parsePrimitiveToken("false"))

        val num = parsePrimitiveToken("42")
        assertTrue(num is Number)

        assertEquals("hello", parsePrimitiveToken("hello"))
        assertEquals("Hello World", parsePrimitiveToken("\"Hello World\""))
    }
}

