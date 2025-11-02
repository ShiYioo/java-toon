package org.shiyi.toon.decode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.shiyi.toon.*

/**
 * Float 和 Double 类型的往返测试
 *
 * 关键点：
 * 1. Kotlin 区分 Int/Long/Float/Double，而 TOON（类似 JSON）不区分
 * 2. 编码时：5.0 (Double) -> "5" (整数值的浮点数会去掉小数点)
 * 3. 解码时："5" -> 5 (Int)，"5.5" -> 5.5 (Double)
 * 4. 因此整数值的 Float/Double 在往返后会变成 Int/Long
 */
class FloatDoubleRoundTripTest {

    data class DoubleData(val value: Double)
    data class FloatData(val value: Float)
    data class IntData(val value: Int)
    data class MixedNumberData(val intVal: Int, val doubleVal: Double, val floatVal: Float)

    // ========== 基本类型往返测试 ==========

    @Test
    fun `round-trip Int should preserve as Int`() {
        val original = mapOf("value" to 5)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("Int 往返: $original -> \"$encoded\" -> $decoded")
        assertEquals(5, decoded["value"])
        assertTrue(decoded["value"] is Int)
    }

    @Test
    fun `round-trip Double with decimal should preserve as Double`() {
        val original = mapOf("value" to 5.5)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("Double (有小数) 往返: $original -> \"$encoded\" -> $decoded")
        assertEquals(5.5, (decoded["value"] as Number).toDouble(), 0.001)
        assertTrue(decoded["value"] is Double)
    }

    @Test
    fun `round-trip Double without decimal becomes Int`() {
        // 这是预期行为：5.0 编码为 "5"，解码为 Int(5)
        val original = mapOf("value" to 5.0)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("Double (无小数) 往返: $original -> \"$encoded\" -> $decoded")
        assertEquals("value: 5", encoded)
        assertEquals(5, decoded["value"])
        assertTrue(decoded["value"] is Int, "整数值的 Double 解码后应该是 Int")
    }

    @Test
    fun `round-trip Float with decimal should preserve as Double`() {
        val original = mapOf("value" to 3.14f)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("Float (有小数) 往返: $original -> \"$encoded\" -> $decoded")
        assertEquals(3.14, (decoded["value"] as Number).toDouble(), 0.01)
        // 注意：Float 会被解码为 Double（因为 TOON 不区分）
        assertTrue(decoded["value"] is Double)
    }

    @Test
    fun `round-trip Float without decimal becomes Int`() {
        val original = mapOf("value" to 10.0f)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("Float (无小数) 往返: $original -> \"$encoded\" -> $decoded")
        assertEquals("value: 10", encoded)
        assertEquals(10, decoded["value"])
        assertTrue(decoded["value"] is Int)
    }

    // ========== 类型化解码测试（使用 Jackson）==========

    @Test
    fun `decodeAs should convert Int to Double field`() {
        // TOON: "value: 5"，解码为 DoubleData(value=5.0)
        val toon = "value: 5"
        val result = Toon.decodeAs<DoubleData>(toon)

        assertEquals(5.0, result.value, 0.001)
        println("Int -> Double 字段: \"$toon\" -> $result")
    }

    @Test
    fun `decodeAs should convert Int to Float field`() {
        // TOON: "value: 5"，解码为 FloatData(value=5.0f)
        val toon = "value: 5"
        val result = Toon.decodeAs<FloatData>(toon)

        assertEquals(5.0f, result.value, 0.001f)
        println("Int -> Float 字段: \"$toon\" -> $result")
    }

    @Test
    fun `decodeAs should convert Double to Int field`() {
        // TOON: "value: 5.0"（编码为 "5"），解码为 IntData(value=5)
        val original = IntData(5)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<IntData>(encoded)

        assertEquals(original, decoded)
        println("IntData 往返: $original -> \"$encoded\" -> $decoded")
    }

    @Test
    fun `decodeAs should handle mixed number types`() {
        val toon = """
            intVal: 10
            doubleVal: 3.14
            floatVal: 2.5
        """.trimIndent()

        val result = Toon.decodeAs<MixedNumberData>(toon)

        assertEquals(10, result.intVal)
        assertEquals(3.14, result.doubleVal, 0.001)
        assertEquals(2.5f, result.floatVal, 0.001f)
        println("混合数字类型: $result")
    }

    // ========== 数组中的 Float/Double 测试 ==========

    @Test
    fun `round-trip Double array with decimals`() {
        val original = mapOf("values" to listOf(1.1, 2.2, 3.3))
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>
        val values = decoded["values"] as List<*>

        println("Double 数组 (有小数): $original -> \"$encoded\" -> $decoded")
        assertEquals(3, values.size)
        assertEquals(1.1, (values[0] as Number).toDouble(), 0.001)
        assertEquals(2.2, (values[1] as Number).toDouble(), 0.001)
        assertEquals(3.3, (values[2] as Number).toDouble(), 0.001)
    }

    @Test
    fun `round-trip Double array without decimals becomes Int array`() {
        val original = mapOf("values" to listOf(1.0, 2.0, 3.0))
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>
        val values = decoded["values"] as List<*>

        println("Double 数组 (无小数): $original -> \"$encoded\" -> $decoded")
        assertEquals("values[3]: 1,2,3", encoded)
        assertEquals(listOf(1, 2, 3), values)
    }

    @Test
    fun `round-trip mixed Int and Double array`() {
        val original = mapOf("values" to listOf(1, 2.5, 3, 4.7))
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>
        val values = decoded["values"] as List<*>

        println("混合 Int/Double 数组: $original -> \"$encoded\" -> $decoded")
        assertEquals(4, values.size)
        assertEquals(1, values[0])
        assertEquals(2.5, (values[1] as Number).toDouble(), 0.001)
        assertEquals(3, values[2])
        assertEquals(4.7, (values[3] as Number).toDouble(), 0.001)
    }

    // ========== Data Class 往返测试 ==========

    data class Point(val x: Double, val y: Double)

    @Test
    fun `data class with Double fields should round-trip correctly`() {
        val original = Point(10.5, 20.3)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<Point>(encoded)

        println("Point (Double 字段): $original -> \"$encoded\" -> $decoded")
        assertEquals(10.5, decoded.x, 0.001)
        assertEquals(20.3, decoded.y, 0.001)
    }

    @Test
    fun `data class with integer Double values should round-trip`() {
        // 注意：x=5.0, y=10.0 会被编码为 "5" 和 "10"
        // 但解码时 Jackson 会自动转换为 Double
        val original = Point(5.0, 10.0)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<Point>(encoded)

        println("Point (整数 Double): $original -> \"$encoded\" -> $decoded")
        assertEquals("x: 5\ny: 10", encoded)
        assertEquals(5.0, decoded.x, 0.001)
        assertEquals(10.0, decoded.y, 0.001)
    }

    data class Temperature(val celsius: Float)

    @Test
    fun `data class with Float field should round-trip`() {
        val original = Temperature(36.5f)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<Temperature>(encoded)

        println("Temperature (Float 字段): $original -> \"$encoded\" -> $decoded")
        assertEquals(36.5f, decoded.celsius, 0.001f)
    }

    // ========== 精度测试 ==========

    @Test
    fun `should preserve high precision decimals`() {
        val original = mapOf("pi" to 3.141592653589793)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("高精度小数: $original -> \"$encoded\" -> $decoded")
        assertEquals(3.141592653589793, (decoded["pi"] as Number).toDouble(), 0.0000000001)
    }

    @Test
    fun `should handle very small numbers`() {
        val original = mapOf("tiny" to 1e-10)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("极小数字: $original -> \"$encoded\" -> $decoded")
        assertEquals(1e-10, (decoded["tiny"] as Number).toDouble(), 1e-15)
    }

    @Test
    fun `should handle very large numbers`() {
        val original = mapOf("huge" to 1e15)
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        println("极大数字: $original -> \"$encoded\" -> $decoded")
        assertEquals(1e15, (decoded["huge"] as Number).toDouble(), 1.0)
    }

    // ========== 特殊情况说明性测试 ==========

    @Test
    fun `understand type loss in round-trip`() {
        // 这个测试展示了类型丢失的情况
        val originalInt = mapOf("value" to 5)
        val originalDouble = mapOf("value" to 5.0)
        val originalFloat = mapOf("value" to 5.0f)

        val encodedInt = Toon.encode(originalInt)
        val encodedDouble = Toon.encode(originalDouble)
        val encodedFloat = Toon.encode(originalFloat)

        println("编码结果:")
        println("  Int(5) -> \"$encodedInt\"")
        println("  Double(5.0) -> \"$encodedDouble\"")
        println("  Float(5.0f) -> \"$encodedFloat\"")

        // 所有都编码为 "value: 5"
        assertEquals(encodedInt, encodedDouble)
        assertEquals(encodedInt, encodedFloat)

        val decoded = Toon.decode(encodedInt) as Map<*, *>
        println("解码结果: $decoded (类型: ${decoded["value"]?.javaClass?.simpleName})")

        // 解码后都是 Int(5)
        assertEquals(5, decoded["value"])
        assertTrue(decoded["value"] is Int)
    }
}

