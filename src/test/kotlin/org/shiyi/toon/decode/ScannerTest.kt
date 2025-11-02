package org.shiyi.toon.decode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.shiyi.toon.*

/**
 * Scanner 的单元测试
 */
class ScannerTest {

    @Test
    fun `scanLines should handle empty input`() {
        val result = scanLines("", 2, false)
        assertEquals(0, result.lines.size)
        assertEquals(0, result.blankLines.size)
    }

    @Test
    fun `scanLines should parse simple lines`() {
        val input = """
            line1
            line2
        """.trimIndent()

        val result = scanLines(input, 2, false)

        assertEquals(2, result.lines.size)
        assertEquals("line1", result.lines[0].content)
        assertEquals("line2", result.lines[1].content)
    }

    @Test
    fun `scanLines should calculate depth correctly`() {
        val input = """
            level0
              level1
                level2
        """.trimIndent()

        val result = scanLines(input, 2, false)

        assertEquals(Depth(0), result.lines[0].depth)
        assertEquals(Depth(1), result.lines[1].depth)
        assertEquals(Depth(2), result.lines[2].depth)
    }

    @Test
    fun `scanLines should track blank lines`() {
        val input = """
            line1
            
            line2
        """.trimIndent()

        val result = scanLines(input, 2, false)

        assertEquals(2, result.lines.size)
        assertEquals(1, result.blankLines.size)
        assertEquals(2, result.blankLines[0].lineNumber)
    }

    @Test
    fun `scanLines should enforce strict indentation`() {
        val input = """
            parent:
               child: value
        """.trimIndent()

        // 3个空格不是2的倍数
        assertThrows<IllegalArgumentException> {
            scanLines(input, 2, true)
        }
    }

    @Test
    fun `scanLines should reject tabs in strict mode`() {
        val input = "level0\n\tlevel1"

        assertThrows<IllegalArgumentException> {
            scanLines(input, 2, true)
        }
    }

    @Test
    fun `scanLines should allow tabs in lenient mode`() {
        val input = "level0\n\tlevel1"

        val result = scanLines(input, 2, false)
        assertEquals(2, result.lines.size)
    }

    @Test
    fun `LineCursor should navigate correctly`() {
        val lines = listOf(
            ParsedLine("line1", Depth(0), 0, "line1", 1),
            ParsedLine("line2", Depth(0), 0, "line2", 2),
            ParsedLine("line3", Depth(0), 0, "line3", 3)
        )

        val cursor = LineCursor(lines)

        assertEquals("line1", cursor.peek()?.content)
        assertEquals("line1", cursor.next()?.content)
        assertEquals("line1", cursor.current()?.content)
        assertEquals("line2", cursor.peek()?.content)

        cursor.advance()
        assertEquals("line3", cursor.peek()?.content)

        assertFalse(cursor.atEnd())
        cursor.advance()
        assertTrue(cursor.atEnd())
    }

    @Test
    fun `LineCursor should check depth correctly`() {
        val lines = listOf(
            ParsedLine("level0", Depth(0), 0, "level0", 1),
            ParsedLine("  level1", Depth(1), 2, "level1", 2),
            ParsedLine("    level2", Depth(2), 4, "level2", 3)
        )

        val cursor = LineCursor(lines)

        assertNotNull(cursor.peekAtDepth(Depth(0)))
        assertTrue(cursor.hasMoreAtDepth(Depth(0)))

        cursor.advance()
        assertNotNull(cursor.peekAtDepth(Depth(1)))
        assertFalse(cursor.hasMoreAtDepth(Depth(0)))
    }

    @Test
    fun `LineCursor length should return total lines`() {
        val lines = listOf(
            ParsedLine("line1", Depth(0), 0, "line1", 1),
            ParsedLine("line2", Depth(0), 0, "line2", 2)
        )

        val cursor = LineCursor(lines)
        assertEquals(2, cursor.length)
    }

    @Test
    fun `ParsedLine should contain all information`() {
        val line = ParsedLine(
            raw = "  content",
            depth = Depth(1),
            indent = 2,
            content = "content",
            lineNumber = 5
        )

        assertEquals("  content", line.raw)
        assertEquals(Depth(1), line.depth)
        assertEquals(2, line.indent)
        assertEquals("content", line.content)
        assertEquals(5, line.lineNumber)
    }
}

