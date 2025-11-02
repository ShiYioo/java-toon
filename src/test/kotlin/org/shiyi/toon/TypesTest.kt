package org.shiyi.toon

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

/**
 * 类型和配置选项的单元测试
 */
class TypesTest {

    @Test
    fun `Depth value class should work correctly`() {
        val depth0 = Depth.ZERO
        val depth1 = Depth(1)
        val depth2 = Depth(2)

        assertEquals(0, depth0.value)
        assertEquals(1, depth1.value)

        // 测试加法
        val depth3 = depth1 + 2
        assertEquals(3, depth3.value)

        // 测试比较
        assertTrue(depth1 < depth2)
        assertTrue(depth2 > depth1)
        assertEquals(0, depth1.compareTo(Depth(1)))
    }

    @Test
    fun `EncodeOptions should validate indent`() {
        // 正常情况
        val options = EncodeOptions(indent = 4)
        assertEquals(4, options.indent)

        // 无效缩进应该抛出异常
        assertThrows<IllegalArgumentException> {
            EncodeOptions(indent = 0)
        }

        assertThrows<IllegalArgumentException> {
            EncodeOptions(indent = -1)
        }
    }

    @Test
    fun `EncodeOptions should have correct defaults`() {
        val options = EncodeOptions()
        assertEquals(2, options.indent)
        assertEquals(Delimiter.DEFAULT, options.delimiter)
        assertEquals(false, options.lengthMarker)
    }

    @Test
    fun `DecodeOptions should validate indent`() {
        // 正常情况
        val options = DecodeOptions(indent = 4)
        assertEquals(4, options.indent)

        // 无效缩进应该抛出异常
        assertThrows<IllegalArgumentException> {
            DecodeOptions(indent = 0)
        }
    }

    @Test
    fun `DecodeOptions should have correct defaults`() {
        val options = DecodeOptions()
        assertEquals(2, options.indent)
        assertEquals(true, options.strict)
    }

    @Test
    fun `Delimiter enum should work correctly`() {
        assertEquals(',', Delimiter.COMMA.char)
        assertEquals('\t', Delimiter.TAB.char)
        assertEquals('|', Delimiter.PIPE.char)

        assertEquals(Delimiter.COMMA, Delimiter.DEFAULT)

        // 测试 fromChar
        assertEquals(Delimiter.COMMA, Delimiter.fromChar(','))
        assertEquals(Delimiter.TAB, Delimiter.fromChar('\t'))
        assertEquals(Delimiter.PIPE, Delimiter.fromChar('|'))
        assertNull(Delimiter.fromChar('x'))
    }

    @Test
    fun `Data classes should support copy`() {
        val options1 = EncodeOptions(indent = 4, delimiter = Delimiter.PIPE)
        val options2 = options1.copy(indent = 2)

        assertEquals(2, options2.indent)
        assertEquals(Delimiter.PIPE, options2.delimiter)
    }

    @Test
    fun `ArrayHeaderInfo should be created correctly`() {
        val header = ArrayHeaderInfo(
            key = "items",
            length = 5,
            delimiter = Delimiter.COMMA,
            fields = listOf("name", "age"),
            hasLengthMarker = true
        )

        assertEquals("items", header.key)
        assertEquals(5, header.length)
        assertEquals(Delimiter.COMMA, header.delimiter)
        assertEquals(listOf("name", "age"), header.fields)
        assertTrue(header.hasLengthMarker)
    }
}

