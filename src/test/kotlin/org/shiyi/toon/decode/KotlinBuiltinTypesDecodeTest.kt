package org.shiyi.toon.decode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.shiyi.toon.*

/**
 * Kotlin 内置类型的解码测试
 *
 * 测试解码后的类型转换，确保可以正确还原为 Kotlin 类型
 */
class KotlinBuiltinTypesDecodeTest {

    // ========== 数组解码测试 ==========

    @Test
    fun `decode should handle array of primitives as List`() {
        val toon = "numbers[5]: 1,2,3,4,5"
        val result = Toon.decode(toon) as Map<*, *>
        val numbers = result["numbers"] as List<*>

        assertEquals(5, numbers.size)
        assertEquals(1, (numbers[0] as Number).toInt())
        assertEquals(5, (numbers[4] as Number).toInt())
    }

    @Test
    fun `decode should handle boolean array`() {
        val toon = "flags[3]: true,false,true"
        val result = Toon.decode(toon) as Map<*, *>
        val flags = result["flags"] as List<*>

        assertEquals(3, flags.size)
        assertEquals(true, flags[0])
        assertEquals(false, flags[1])
        assertEquals(true, flags[2])
    }

    @Test
    fun `decode should handle string array`() {
        val toon = "tags[3]: kotlin,java,scala"
        val result = Toon.decode(toon) as Map<*, *>
        val tags = result["tags"] as List<*>

        assertEquals(3, tags.size)
        assertEquals("kotlin", tags[0])
        assertEquals("java", tags[1])
        assertEquals("scala", tags[2])
    }

    @Test
    fun `decode should handle mixed type array`() {
        val toon = "data[4]: hello,42,true,3.14"
        val result = Toon.decode(toon) as Map<*, *>
        val data = result["data"] as List<*>

        assertEquals(4, data.size)
        assertEquals("hello", data[0])
        assertEquals(42, (data[1] as Number).toInt())
        assertEquals(true, data[2])
        assertEquals(3.14, (data[3] as Number).toDouble(), 0.001)
    }

    // ========== Pair 和 Triple 解码测试 ==========

    @Test
    fun `decode two-element array can be converted to Pair`() {
        val toon = "coordinate[2]: 10,20"
        val result = Toon.decode(toon) as Map<*, *>
        val coordinate = result["coordinate"] as List<*>

        assertEquals(2, coordinate.size)
        // 可以手动转换为 Pair
        val pair = Pair(
            (coordinate[0] as Number).toInt(),
            (coordinate[1] as Number).toInt()
        )
        assertEquals(Pair(10, 20), pair)
    }

    @Test
    fun `decode three-element array can be converted to Triple`() {
        val toon = "rgb[3]: 255,128,64"
        val result = Toon.decode(toon) as Map<*, *>
        val rgb = result["rgb"] as List<*>

        assertEquals(3, rgb.size)
        // 可以手动转换为 Triple
        val triple = Triple(
            (rgb[0] as Number).toInt(),
            (rgb[1] as Number).toInt(),
            (rgb[2] as Number).toInt()
        )
        assertEquals(Triple(255, 128, 64), triple)
    }

    // ========== 类型化解码测试 ==========

    data class Point(val x: Int, val y: Int)

    @Test
    fun `decodeAs should convert to data class`() {
        val toon = """
            x: 10
            y: 20
        """.trimIndent()

        val point = Toon.decodeAs<Point>(toon)

        assertEquals(10, point.x)
        assertEquals(20, point.y)
    }

    data class ColorRGB(val r: Int, val g: Int, val b: Int)

    @Test
    fun `decodeAs should convert to data class with multiple fields`() {
        val toon = """
            r: 255
            g: 128
            b: 64
        """.trimIndent()

        val color = Toon.decodeAs<ColorRGB>(toon)

        assertEquals(255, color.r)
        assertEquals(128, color.g)
        assertEquals(64, color.b)
    }

    enum class Status {
        ACTIVE, INACTIVE, PENDING
    }

    data class Task(val name: String, val status: Status)

    @Test
    fun `decodeAs should handle enum fields`() {
        val toon = """
            name: Task1
            status: ACTIVE
        """.trimIndent()

        val task = Toon.decodeAs<Task>(toon)

        assertEquals("Task1", task.name)
        assertEquals(Status.ACTIVE, task.status)
    }

    data class Container(val items: List<String>)

    @Test
    fun `decodeAs should handle list fields`() {
        val toon = """
            items[3]: a,b,c
        """.trimIndent()

        val container = Toon.decodeAs<Container>(toon)

        assertEquals(3, container.items.size)
        assertEquals(listOf("a", "b", "c"), container.items)
    }

    // ========== 特殊值解码测试 ==========

    @Test
    fun `decode should handle null values`() {
        val toon = "value: null"
        val result = Toon.decode(toon) as Map<*, *>

        assertNull(result["value"])
    }

    @Test
    fun `decode should handle quoted null as string`() {
        val toon = """value: "null""""
        val result = Toon.decode(toon) as Map<*, *>

        assertEquals("null", result["value"])
    }

    @Test
    fun `decode should handle quoted numbers as strings`() {
        val toon = """
            num1: "42"
            num2: "3.14"
            num3: "-7"
        """.trimIndent()
        val result = Toon.decode(toon) as Map<*, *>

        assertEquals("42", result["num1"])
        assertEquals("3.14", result["num2"])
        assertEquals("-7", result["num3"])
    }

    @Test
    fun `decode should handle quoted booleans as strings`() {
        val toon = """
            bool1: "true"
            bool2: "false"
        """.trimIndent()
        val result = Toon.decode(toon) as Map<*, *>

        assertEquals("true", result["bool1"])
        assertEquals("false", result["bool2"])
    }

    // ========== Unicode 和特殊字符测试 ==========

    @Test
    fun `decode should handle Unicode strings`() {
        val toon = """
            name: 你好世界
            emoji: 🚀
            greeting: hello 👋 world
        """.trimIndent()
        val result = Toon.decode(toon) as Map<*, *>

        assertEquals("你好世界", result["name"])
        assertEquals("🚀", result["emoji"])
        assertEquals("hello 👋 world", result["greeting"])
    }

    @Test
    fun `decode should handle escaped characters`() {
        val toon = """
            path: "C:\\Users\\path"
            multiline: "line1\nline2"
            quoted: "say \"hello\""
        """.trimIndent()
        val result = Toon.decode(toon) as Map<*, *>

        assertEquals("C:\\Users\\path", result["path"])
        assertEquals("line1\nline2", result["multiline"])
        assertEquals("say \"hello\"", result["quoted"])
    }

    // ========== 空集合测试 ==========

    @Test
    fun `decode should handle empty array`() {
        val toon = "items[0]:"
        val result = Toon.decode(toon) as Map<*, *>
        val items = result["items"] as List<*>

        assertTrue(items.isEmpty())
    }

    @Test
    fun `decode should handle empty object`() {
        val toon = "config:"
        val result = Toon.decode(toon) as Map<*, *>
        val config = result["config"] as Map<*, *>

        assertTrue(config.isEmpty())
    }

    // ========== 往返测试（Round-trip） ==========

    @Test
    fun `round-trip should preserve primitive array`() {
        val original = mapOf("numbers" to listOf(1, 2, 3, 4, 5))
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        // 整数列表应该保持不变
        assertEquals(original["numbers"], decoded["numbers"])

        val numbers = decoded["numbers"] as List<*>
        numbers.forEach {
            assertTrue(it is Int, "元素应该是 Int 类型")
        }
    }

    @Test
    fun `round-trip should preserve string array`() {
        val original = mapOf("tags" to listOf("kotlin", "java", "scala"))
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        assertEquals(original["tags"], decoded["tags"])
    }

    @Test
    fun `round-trip should preserve nested structure`() {
        val original = mapOf(
            "user" to mapOf(
                "name" to "Alice",
                "age" to 30,
                "tags" to listOf("kotlin", "developer")
            )
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `round-trip should preserve boolean values`() {
        val original = mapOf(
            "active" to true,
            "verified" to false,
            "flags" to listOf(true, false, true)
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `round-trip should preserve null values`() {
        val original = mapOf(
            "value" to null,
            "items" to listOf("a", null, "b")
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded)

        assertEquals(original, decoded)
    }

    // ========== Data Class 包含 Float/Double 字段的往返测试 ==========

    data class PointDouble(val x: Double, val y: Double)

    @Test
    fun `round-trip data class with Double fields should preserve values`() {
        val original = PointDouble(10.5, 20.3)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<PointDouble>(encoded)

        assertEquals(10.5, decoded.x, 0.001)
        assertEquals(20.3, decoded.y, 0.001)
        println("PointDouble 往返: $original -> \"$encoded\" -> $decoded")
    }

    @Test
    fun `round-trip data class with integer-valued Double fields`() {
        // Double 字段值为整数（如 5.0, 10.0）
        val original = PointDouble(5.0, 10.0)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<PointDouble>(encoded)

        // 编码时 5.0 -> "5"，但 decodeAs 会自动转换回 Double
        assertEquals("x: 5\ny: 10", encoded)
        assertEquals(5.0, decoded.x, 0.001)
        assertEquals(10.0, decoded.y, 0.001)
        println("PointDouble (整数值) 往返: $original -> \"$encoded\" -> $decoded")
    }

    data class PointFloat(val x: Float, val y: Float)

    @Test
    fun `round-trip data class with Float fields should preserve values`() {
        val original = PointFloat(3.14f, 2.71f)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<PointFloat>(encoded)

        assertEquals(3.14f, decoded.x, 0.01f)
        assertEquals(2.71f, decoded.y, 0.01f)
        println("PointFloat 往返: $original -> \"$encoded\" -> $decoded")
    }

    @Test
    fun `round-trip data class with integer-valued Float fields`() {
        val original = PointFloat(5.0f, 10.0f)
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<PointFloat>(encoded)

        assertEquals("x: 5\ny: 10", encoded)
        assertEquals(5.0f, decoded.x, 0.001f)
        assertEquals(10.0f, decoded.y, 0.001f)
        println("PointFloat (整数值) 往返: $original -> \"$encoded\" -> $decoded")
    }

    data class MixedNumbers(
        val intValue: Int,
        val longValue: Long,
        val floatValue: Float,
        val doubleValue: Double
    )

    @Test
    fun `round-trip data class with mixed number types`() {
        val original = MixedNumbers(
            intValue = 42,
            longValue = 999999999999L,
            floatValue = 3.14f,
            doubleValue = 2.718281828
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<MixedNumbers>(encoded)

        assertEquals(original.intValue, decoded.intValue)
        assertEquals(original.longValue, decoded.longValue)
        assertEquals(original.floatValue, decoded.floatValue, 0.001f)
        assertEquals(original.doubleValue, decoded.doubleValue, 0.000000001)
        println("MixedNumbers 往返: $original -> \"$encoded\" -> $decoded")
    }

    data class Product(
        val id: Int,
        val name: String,
        val price: Double,
        val weight: Float
    )

    @Test
    fun `round-trip data class with Double and Float fields`() {
        val original = Product(
            id = 1,
            name = "Laptop",
            price = 999.99,
            weight = 2.5f
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<Product>(encoded)

        assertEquals(original.id, decoded.id)
        assertEquals(original.name, decoded.name)
        assertEquals(original.price, decoded.price, 0.001)
        assertEquals(original.weight, decoded.weight, 0.001f)
        println("Product 往返: $original\n编码:\n$encoded\n解码: $decoded")
    }

    data class Coordinates(val points: List<PointDouble>)

    @Test
    fun `round-trip data class with list of objects containing Double fields`() {
        val original = Coordinates(
            points = listOf(
                PointDouble(0.0, 0.0),
                PointDouble(10.5, 20.3),
                PointDouble(100.0, 200.0)
            )
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<Coordinates>(encoded)

        assertEquals(3, decoded.points.size)
        assertEquals(0.0, decoded.points[0].x, 0.001)
        assertEquals(0.0, decoded.points[0].y, 0.001)
        assertEquals(10.5, decoded.points[1].x, 0.001)
        assertEquals(20.3, decoded.points[1].y, 0.001)
        assertEquals(100.0, decoded.points[2].x, 0.001)
        assertEquals(200.0, decoded.points[2].y, 0.001)
        println("Coordinates 往返:\n编码:\n$encoded\n解码: $decoded")
    }

    data class Stats(
        val mean: Double,
        val median: Double,
        val stdDev: Double
    )

    @Test
    fun `round-trip data class with high precision Double values`() {
        val original = Stats(
            mean = 3.141592653589793,
            median = 2.718281828459045,
            stdDev = 1.414213562373095
        )
        val encoded = Toon.encode(original)
        val decoded = Toon.decodeAs<Stats>(encoded)

        assertEquals(original.mean, decoded.mean, 0.0000000001)
        assertEquals(original.median, decoded.median, 0.0000000001)
        assertEquals(original.stdDev, decoded.stdDev, 0.0000000001)
        println("Stats (高精度) 往返: $original -> $decoded")
    }

    data class Temperature(val celsius: Float, val fahrenheit: Float)

    @Test
    fun `round-trip data class array with Float fields`() {
        val original = mapOf(
            "temperatures" to listOf(
                Temperature(0.0f, 32.0f),
                Temperature(100.0f, 212.0f),
                Temperature(36.5f, 97.7f)
            )
        )
        val encoded = Toon.encode(original)

        // 解码为 Map 然后手动转换（因为泛型擦除）
        val decoded = Toon.decode(encoded) as Map<*, *>
        val temps = decoded["temperatures"] as List<*>

        assertEquals(3, temps.size)
        println("Temperature 数组往返:\n编码:\n$encoded")

        // 使用类型化解码
        data class TemperatureList(val temperatures: List<Temperature>)
        val typedDecoded = Toon.decodeAs<TemperatureList>(encoded)

        assertEquals(3, typedDecoded.temperatures.size)
        assertEquals(0.0f, typedDecoded.temperatures[0].celsius, 0.001f)
        assertEquals(32.0f, typedDecoded.temperatures[0].fahrenheit, 0.001f)
        assertEquals(36.5f, typedDecoded.temperatures[2].celsius, 0.001f)
        assertEquals(97.7f, typedDecoded.temperatures[2].fahrenheit, 0.001f)
    }
}

