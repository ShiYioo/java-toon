package org.shiyi.toon

/**
 * 简单的使用示例
 */
public fun main() {
    println("=== TOON Kotlin 实现示例 ===\n")

    // 示例 1: 编码简单对象
    println("示例 1: 编码简单对象")
    val simpleData = mapOf(
        "name" to "Alice",
        "age" to 30,
        "active" to true
    )
    val simpleToon = Toon.encode(simpleData)
    println(simpleToon)
    println()

    // 示例 2: 解码 TOON 字符串
    println("示例 2: 解码 TOON 字符串")
    val toonString = """
        name: Bob
        age: 25
        city: Beijing
    """.trimIndent()
    val decoded = Toon.decode(toonString)
    println("解码结果: $decoded")
    println()

    // 示例 3: 嵌套对象
    println("示例 3: 编码嵌套对象")
    val nestedData = mapOf(
        "user" to mapOf(
            "name" to "Charlie",
            "age" to 35
        ),
        "settings" to mapOf(
            "theme" to "dark",
            "notifications" to true
        )
    )
    val nestedToon = Toon.encode(nestedData)
    println(nestedToon)
    println()

    // 示例 4: 数组
    println("示例 4: 编码数组")
    val arrayData = mapOf(
        "numbers" to listOf(1, 2, 3, 4, 5),
        "names" to listOf("Alice", "Bob", "Charlie")
    )
    val arrayToon = Toon.encode(arrayData)
    println(arrayToon)
    println()

    // 示例 5: 使用扩展函数
    println("示例 5: 使用扩展函数")
    val data = mapOf("message" to "Hello TOON!")
    val toon = data.toToon()
    println(toon)

    val result = toon.fromToon()
    println("解码: $result")
    println()

    println("=== 所有示例运行完成 ===")
}

