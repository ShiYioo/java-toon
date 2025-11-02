package org.shiyi.toon

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Toon 主 API 的集成测试
 */
class ToonTest {

    @Test
    fun `encode and decode should be reversible for simple objects`() {
        val original = mapOf(
            "name" to "Alice",
            "age" to 30,
            "active" to true
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        assertEquals("Alice", decoded["name"])
        assertEquals(30, (decoded["age"] as Number).toInt())
        assertEquals(true, decoded["active"])
    }

    @Test
    fun `encode and decode should be reversible for nested objects`() {
        val original = mapOf(
            "user" to mapOf(
                "name" to "Bob",
                "age" to 25
            ),
            "settings" to mapOf(
                "theme" to "dark"
            )
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        val user = decoded["user"] as Map<*, *>
        assertEquals("Bob", user["name"])
        assertEquals(25, (user["age"] as Number).toInt())

        val settings = decoded["settings"] as Map<*, *>
        assertEquals("dark", settings["theme"])
    }

    @Test
    fun `encode and decode should handle arrays`() {
        val original = mapOf(
            "numbers" to listOf(1, 2, 3, 4, 5),
            "names" to listOf("Alice", "Bob", "Charlie")
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        val numbers = decoded["numbers"] as List<*>
        assertEquals(5, numbers.size)
        assertEquals(1, (numbers[0] as Number).toInt())

        val names = decoded["names"] as List<*>
        assertEquals(3, names.size)
        assertEquals("Alice", names[0])
    }

    @Test
    fun `extension functions should work`() {
        val data = mapOf("key" to "value")

        val encoded = data.toToon()
        val decoded = encoded.fromToon() as Map<*, *>

        assertEquals("value", decoded["key"])
    }

    @Test
    fun `encode should support custom options`() {
        val data = mapOf(
            "parent" to mapOf(
                "child" to "value"
            )
        )

        val encoded = Toon.encode(data, EncodeOptions(indent = 4))

        assertTrue(encoded.contains("    child:"))
    }

    @Test
    fun `decode should support custom options`() {
        val toon = """
            parent:
                child: value
        """.trimIndent()

        val decoded = Toon.decode(toon, DecodeOptions(indent = 4)) as Map<*, *>
        val parent = decoded["parent"] as Map<*, *>

        assertEquals("value", parent["child"])
    }

    @Test
    fun `should handle complex nested structures`() {
        val original = mapOf(
            "company" to "ACME",
            "employees" to listOf(
                mapOf(
                    "name" to "Alice",
                    "age" to 30,
                    "department" to "Engineering"
                ),
                mapOf(
                    "name" to "Bob",
                    "age" to 25,
                    "department" to "Design"
                )
            ),
            "metadata" to mapOf(
                "version" to "1.0",
                "created" to "2025-10-31"
            )
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        assertEquals("ACME", decoded["company"])

        val employees = decoded["employees"] as List<*>
        assertEquals(2, employees.size)

        val emp1 = employees[0] as Map<*, *>
        assertEquals("Alice", emp1["name"])
        assertEquals(30, (emp1["age"] as Number).toInt())

        val metadata = decoded["metadata"] as Map<*, *>
        assertEquals("1.0", metadata["version"])
    }

    @Test
    fun `should handle special characters correctly`() {
        val original = mapOf(
            "message" to "Hello\nWorld",
            "quote" to "Say \"Hi\"",
            "path" to "C:\\Users\\test"
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        assertEquals("Hello\nWorld", decoded["message"])
        assertEquals("Say \"Hi\"", decoded["quote"])
        assertEquals("C:\\Users\\test", decoded["path"])
    }

    @Test
    fun `should handle empty structures`() {
        val original = mapOf(
            "emptyObject" to emptyMap<String, Any>(),
            "emptyArray" to emptyList<Any>()
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        val emptyObj = decoded["emptyObject"] as Map<*, *>
        assertEquals(0, emptyObj.size)

        val emptyArr = decoded["emptyArray"] as List<*>
        assertEquals(0, emptyArr.size)
    }

    @Test
    fun `should handle null values`() {
        val original = mapOf(
            "nullable" to null,
            "present" to "value"
        )

        val encoded = Toon.encode(original)
        val decoded = Toon.decode(encoded) as Map<*, *>

        assertNull(decoded["nullable"])
        assertEquals("value", decoded["present"])
    }

    @Test
    fun `should handle different delimiters`() {
        val original = mapOf(
            "items" to listOf("a", "b", "c")
        )

        val encoded = Toon.encode(original, EncodeOptions(delimiter = Delimiter.PIPE))
        assertTrue(encoded.contains("|"))

        val decoded = Toon.decode(encoded) as Map<*, *>
        val items = decoded["items"] as List<*>
        assertEquals(3, items.size)
    }
}

