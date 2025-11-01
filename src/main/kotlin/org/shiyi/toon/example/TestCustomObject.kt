package org.shiyi.toon.example

import org.shiyi.toon.Toon

/**
 * 测试自定义对象编码
 */
public fun main() {
    println("=== 测试自定义对象编码 ===\n")

    // 测试 1: data class 数组 - 应该使用表格格式
    println("测试 1: data class 数组（应该使用表格格式）")
    data class User(val name: String, val age: Int, val email: String?)

    val users = listOf(
        User("Ada", 32, "ada@example.com"),
        User("Bob", 28, "bob@example.com")
    )

    val result1 = Toon.encode(mapOf("users" to users))
    println(result1)
    println("\n期望输出:")
    println("users[2]{age,email,name}:")
    println("  32,ada@example.com,Ada")
    println("  28,bob@example.com,Bob")
    println("\n" + "=".repeat(50) + "\n")

    // 测试 2: Product 数组
    println("测试 2: Product 数组")
    data class Product(val id: Int, val name: String, val price: Double)

    val products = listOf(
        Product(1, "Laptop", 999.99),
        Product(2, "Mouse", 29.99)
    )

    val result2 = Toon.encode(mapOf("products" to products))
    println(result2)
    println("\n期望输出:")
    println("products[2]{id,name,price}:")
    println("  1,Laptop,999.99")
    println("  2,Mouse,29.99")
    println("\n" + "=".repeat(50) + "\n")

    // 测试 3: 嵌套结构中的 data class 数组
    println("测试 3: 嵌套结构中的 data class 数组")

    val complexData = mapOf(
        "items" to listOf(
            mapOf(
                "users" to users,
                "status" to "active"
            )
        )
    )

    val result3 = Toon.encode(complexData)
    println(result3)
    println("\n期望输出:")
    println("items[1]:")
    println("  - users[2]{age,email,name}:")
    println("      32,ada@example.com,Ada")
    println("      28,bob@example.com,Bob")
    println("    status: active")
    println()
}

