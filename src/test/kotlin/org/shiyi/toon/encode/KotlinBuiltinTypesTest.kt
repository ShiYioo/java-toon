package org.shiyi.toon.encode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.shiyi.toon.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.Date

/**
 * Kotlin 内置类型的编码测试
 *
 * 测试以下类型：
 * - Pair
 * - Triple
 * - 各种数组类型（IntArray, DoubleArray 等）
 * - BigInteger, BigDecimal
 * - 日期时间类型（Date, Instant, LocalDate 等）
 * - Enum
 * - Set
 */
class KotlinBuiltinTypesTest {

    // ========== Pair 测试 ==========

    @Test
    fun `encode should handle Pair as array`() {
        val data = mapOf("coordinate" to Pair(10, 20))
        val result = encode(data, EncodeOptions())

        // Pair 应该被编码为数组
        assertTrue(result.contains("coordinate[2]:"))
        assertTrue(result.contains("10,20"))
    }

    @Test
    fun `encode should handle Pair with mixed types`() {
        val data = mapOf("pair" to Pair("key", 42))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("pair[2]:"))
        assertTrue(result.contains("key,42"))
    }

    @Test
    fun `encode should handle Pair with null values`() {
        val data = mapOf("pair" to Pair(null, "value"))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("pair[2]:"))
        assertTrue(result.contains("null,value"))
    }

    @Test
    fun `encode should handle nested Pair`() {
        val data = mapOf("nested" to Pair(Pair(1, 2), Pair(3, 4)))
        val result = encode(data, EncodeOptions())

        // 嵌套的 Pair 应该使用列表格式
        assertTrue(result.contains("nested[2]:"))
    }

    // ========== Triple 测试 ==========

    @Test
    fun `encode should handle Triple as array`() {
        val data = mapOf("rgb" to Triple(255, 128, 64))
        val result = encode(data, EncodeOptions())

        // Triple 应该被编码为数组
        assertTrue(result.contains("rgb[3]:"))
        assertTrue(result.contains("255,128,64"))
    }

    @Test
    fun `encode should handle Triple with mixed types`() {
        val data = mapOf("triple" to Triple("name", 30, true))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("triple[3]:"))
        assertTrue(result.contains("name,30,true"))
    }

    @Test
    fun `encode should handle Triple with null values`() {
        val data = mapOf("triple" to Triple(1, null, 3))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("triple[3]:"))
        assertTrue(result.contains("1,null,3"))
    }

    // ========== 原始类型数组测试 ==========

    @Test
    fun `encode should handle IntArray`() {
        val data = mapOf("numbers" to intArrayOf(1, 2, 3, 4, 5))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("numbers[5]:"))
        assertTrue(result.contains("1,2,3,4,5"))
    }

    @Test
    fun `encode should handle DoubleArray`() {
        val data = mapOf("values" to doubleArrayOf(1.1, 2.2, 3.3))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("values[3]:"))
        assertTrue(result.contains("1.1,2.2,3.3"))
    }

    @Test
    fun `encode should handle BooleanArray`() {
        val data = mapOf("flags" to booleanArrayOf(true, false, true))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("flags[3]:"))
        assertTrue(result.contains("true,false,true"))
    }

    @Test
    fun `encode should handle LongArray`() {
        val data = mapOf("ids" to longArrayOf(1L, 2L, 3L))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("ids[3]:"))
        assertTrue(result.contains("1,2,3"))
    }

    @Test
    fun `encode should handle FloatArray`() {
        val data = mapOf("coords" to floatArrayOf(1.5f, 2.5f, 3.5f))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("coords[3]:"))
        assertTrue(result.contains("1.5,2.5,3.5"))
    }

    @Test
    fun `encode should handle CharArray`() {
        val data = mapOf("chars" to charArrayOf('a', 'b', 'c'))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("chars[3]:"))
        assertTrue(result.contains("a,b,c"))
    }

    @Test
    fun `encode should handle ByteArray`() {
        val data = mapOf("bytes" to byteArrayOf(1, 2, 3))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("bytes[3]:"))
        assertTrue(result.contains("1,2,3"))
    }

    @Test
    fun `encode should handle ShortArray`() {
        val data = mapOf("shorts" to shortArrayOf(1, 2, 3))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("shorts[3]:"))
        assertTrue(result.contains("1,2,3"))
    }

    // ========== Set 测试 ==========

    @Test
    fun `encode should handle Set as array`() {
        val data = mapOf("uniqueIds" to setOf(1, 2, 3, 2, 1)) // 重复的会被去重
        val result = encode(data, EncodeOptions())

        // Set 应该被编码为数组
        assertTrue(result.contains("uniqueIds[3]:"))
        // Set 不保证顺序，但包含所有不重复的元素
        assertTrue(result.contains("1"))
        assertTrue(result.contains("2"))
        assertTrue(result.contains("3"))
    }

    @Test
    fun `encode should handle LinkedHashSet preserving order`() {
        val data = mapOf("tags" to linkedSetOf("kotlin", "java", "scala"))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("tags[3]:"))
        assertTrue(result.contains("kotlin,java,scala"))
    }

    @Test
    fun `encode should handle empty Set`() {
        val data = mapOf("empty" to emptySet<String>())
        val result = encode(data, EncodeOptions())

        assertEquals("empty[0]:", result)
    }

    // ========== BigInteger 和 BigDecimal 测试 ==========

    @Test
    fun `encode should handle BigInteger within Long range`() {
        val data = mapOf("bigInt" to BigInteger("12345"))
        val result = encode(data, EncodeOptions())

        // 在 Long 范围内的 BigInteger 应该转换为数字
        assertEquals("bigInt: 12345", result)
    }

    @Test
    fun `encode should handle BigInteger outside Long range`() {
        val data = mapOf("hugeBigInt" to BigInteger("999999999999999999999999999"))
        val result = encode(data, EncodeOptions())

        // 超出 Long 范围的 BigInteger 应该转换为字符串（需要引号）
        assertTrue(result.contains("hugeBigInt:"))
        assertTrue(result.contains("999999999999999999999999999"))
    }

    @Test
    fun `encode should handle BigDecimal`() {
        val data = mapOf("precise" to BigDecimal("123.456789"))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("precise:"))
        assertTrue(result.contains("123.456789"))
    }

    @Test
    fun `encode should handle BigDecimal with high precision`() {
        val data = mapOf("veryPrecise" to BigDecimal("0.123456789012345678901234567890"))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("veryPrecise:"))
    }

    // ========== 日期时间类型测试 ==========

    @Test
    fun `encode should handle Date as ISO 8601 string`() {
        @Suppress("DEPRECATION")
        val date = Date(124, 10, 1, 10, 0) // 2024-11-01 10:00
        val data = mapOf("createdAt" to date)
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("createdAt:"))
        // Date 应该转换为 ISO 8601 字符串
        assertTrue(result.contains("2024-11-01") || result.contains("T"))
    }

    @Test
    fun `encode should handle Instant`() {
        val instant = Instant.parse("2024-11-01T10:30:00Z")
        val data = mapOf("timestamp" to instant)
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("timestamp:"))
        assertTrue(result.contains("2024-11-01T10:30:00Z"))
    }

    @Test
    fun `encode should handle LocalDate`() {
        val localDate = LocalDate.of(2024, 11, 1)
        val data = mapOf("date" to localDate)
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("date:"))
        assertTrue(result.contains("2024-11-01"))
    }

    @Test
    fun `encode should handle LocalDateTime`() {
        val localDateTime = LocalDateTime.of(2024, 11, 1, 10, 30)
        val data = mapOf("datetime" to localDateTime)
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("datetime:"))
        assertTrue(result.contains("2024-11-01T10:30"))
    }

    @Test
    fun `encode should handle LocalTime`() {
        val localTime = LocalTime.of(10, 30, 45)
        val data = mapOf("time" to localTime)
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("time:"))
        assertTrue(result.contains("10:30:45"))
    }

    @Test
    fun `encode should handle ZonedDateTime`() {
        val zonedDateTime = ZonedDateTime.of(2024, 11, 1, 10, 30, 0, 0, ZoneId.of("UTC"))
        val data = mapOf("zonedTime" to zonedDateTime)
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("zonedTime:"))
        assertTrue(result.contains("2024-11-01"))
    }

    // ========== Enum 测试 ==========

    enum class Status {
        ACTIVE, INACTIVE, PENDING
    }

    @Test
    fun `encode should handle Enum as string`() {
        val data = mapOf("status" to Status.ACTIVE)
        val result = encode(data, EncodeOptions())

        assertEquals("status: ACTIVE", result)
    }

    @Test
    fun `encode should handle Enum in array`() {
        val data = mapOf("statuses" to listOf(Status.ACTIVE, Status.PENDING))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("statuses[2]:"))
        assertTrue(result.contains("ACTIVE,PENDING"))
    }

    // ========== 复杂组合测试 ==========

    @Test
    fun `encode should handle complex structure with Kotlin types`() {
        val data = mapOf(
            "coordinate" to Pair(10.5, 20.3),
            "rgb" to Triple(255, 128, 64),
            "uniqueIds" to setOf(1, 2, 3),
            "matrix" to intArrayOf(1, 2, 3, 4),
            "status" to Status.ACTIVE
        )
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("coordinate[2]:"))
        assertTrue(result.contains("rgb[3]:"))
        assertTrue(result.contains("uniqueIds[3]:"))
        assertTrue(result.contains("matrix[4]:"))
        assertTrue(result.contains("status: ACTIVE"))
    }

    @Test
    fun `encode should handle nested Kotlin collections`() {
        val data = mapOf(
            "pairs" to listOf(
                Pair(1, 2),
                Pair(3, 4)
            )
        )
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("pairs[2]:"))
    }

    @Test
    fun `encode should handle Map with Pair values`() {
        val data = mapOf(
            "coords" to mapOf(
                "point1" to Pair(0, 0),
                "point2" to Pair(10, 10)
            )
        )
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("coords:"))
        assertTrue(result.contains("point1[2]:"))
        assertTrue(result.contains("point2[2]:"))
    }

    // ========== 特殊值测试 ==========

    @Test
    fun `encode should handle NaN as null`() {
        val data = mapOf("value" to Double.NaN)
        val result = encode(data, EncodeOptions())

        assertEquals("value: null", result)
    }

    @Test
    fun `encode should handle Infinity as null`() {
        val data = mapOf("value" to Double.POSITIVE_INFINITY)
        val result = encode(data, EncodeOptions())

        assertEquals("value: null", result)
    }

    @Test
    fun `encode should handle negative zero as zero`() {
        // -0.0 会被规范化为 0.0，然后因为是整数值的浮点数，被编码为 "0"
        val data = mapOf("value" to -0.0)
        val result = encode(data, EncodeOptions())

        assertEquals("value: 0", result)
    }

    @Test
    fun `encode should format integer-valued floats without decimal point`() {
        // 整数值的 Double/Float 应该编码为整数形式（与 JavaScript 一致）
        assertEquals("value: 5", encode(mapOf("value" to 5.0), EncodeOptions()))
        assertEquals("value: 10", encode(mapOf("value" to 10.0f), EncodeOptions()))
        assertEquals("value: 0", encode(mapOf("value" to 0.0), EncodeOptions()))

        // 但带小数的应该保留小数点
        assertEquals("value: 5.5", encode(mapOf("value" to 5.5), EncodeOptions()))
        assertEquals("value: 3.14", encode(mapOf("value" to 3.14f), EncodeOptions()))
    }

    // ========== CharSequence 和 Char 测试 ==========

    @Test
    fun `encode should handle StringBuilder`() {
        val data = mapOf("text" to StringBuilder("hello"))
        val result = encode(data, EncodeOptions())

        assertEquals("text: hello", result)
    }

    @Test
    fun `encode should handle StringBuffer`() {
        val data = mapOf("text" to StringBuffer("world"))
        val result = encode(data, EncodeOptions())

        assertEquals("text: world", result)
    }

    @Test
    fun `encode should handle Char as string`() {
        val data = mapOf("letter" to 'A')
        val result = encode(data, EncodeOptions())

        assertEquals("letter: A", result)
    }

    @Test
    fun `encode should handle Char array as string array`() {
        val data = mapOf("letters" to listOf('A', 'B', 'C'))
        val result = encode(data, EncodeOptions())

        assertTrue(result.contains("letters[3]:"))
        assertTrue(result.contains("A,B,C"))
    }
}

