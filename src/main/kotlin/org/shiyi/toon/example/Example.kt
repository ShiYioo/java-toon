package org.shiyi.toon.example

import org.shiyi.toon.Toon
import org.shiyi.toon.fromToon
import org.shiyi.toon.toToon
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

/**
 * 示例 data class：用户信息
 */
public data class User(
    val name: String,
    val age: Int,
    val email: String? = null
)

/**
 * 示例 data class：配置信息
 */
public data class Config(
    val theme: String,
    val notifications: Boolean,
    val fontSize: Int
)

/**
 * 示例 data class：产品信息
 */
public data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val tags: List<String>
)

/**
 * 示例 enum：状态
 */
public enum class Status {
    ACTIVE, INACTIVE, PENDING
}

/**
 * 示例复杂 data class：订单信息
 */
public data class Order(
    val orderId: String,
    val user: User,
    val products: List<Product>,
    val status: Status,
    val createdAt: Date
)

/**
 * TOON Kotlin 实现示例
 *
 * 展示如何使用 TOON 编码和解码各种数据类型，包括：
 * - 基本类型和集合
 * - 自定义 data class
 * - 日期、枚举、大数值
 * - 复杂嵌套结构
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

    // 示例 5: 数字对数组
    println("示例 5: 编码数字对数组")
    val pairsData = mapOf(
        "pairs" to listOf(
            listOf(1, 2),
            listOf(3, 4)
        )
    )
    val pairsToon = Toon.encode(pairsData)
    println(pairsToon)
    println()

    // 示例 6: 复杂嵌套结构（对象数组）
    println("示例 6: 编码复杂嵌套结构（对象数组）")
    val complexData = mapOf(
        "items" to listOf(
            mapOf(
                "users" to listOf(
                    mapOf("id" to 1, "name" to "Ada"),
                    mapOf("id" to 2, "name" to "Bob")
                ),
                "status" to "active"
            )
        )
    )
    try {
        val complexToon = Toon.encode(complexData)
        println(complexToon)
        println()
    }catch (e:Exception){
        println("编码失败: ${e.message}")
        println()
    }

    // 示例 7: 使用扩展函数
    println("示例 7: 使用扩展函数")
    val data = mapOf("message" to "Hello TOON!")
    val toon = data.toToon()
    println(toon)

    val result = toon.fromToon()
    println("解码: $result")
    println()

    // 示例 8: 边界情况（空数组、空对象等）
    println("示例 8: 边界情况")

    // 空数组
    println("空数组:")
    val emptyArrayData = mapOf("items" to emptyList<Any>())
    val emptyArrayToon = Toon.encode(emptyArrayData)
    println(emptyArrayToon)
    println()

    // 根级空数组
    println("根级空数组:")
    val rootEmptyArray = emptyList<Any>()
    val rootEmptyArrayToon = Toon.encode(rootEmptyArray)
    println(rootEmptyArrayToon)
    println()

    // 空对象
    println("空对象:")
    val emptyObjectData = mapOf("config" to emptyMap<String, Any>())
    val emptyObjectToon = Toon.encode(emptyObjectData)
    println(emptyObjectToon)
    println()

    // 根级空对象
    println("根级空对象:")
    val rootEmptyObject = emptyMap<String, Any>()
    val rootEmptyObjectToon = Toon.encode(rootEmptyObject)
    println(rootEmptyObjectToon)
    println()

    // ========== 自定义对象编码示例 ==========
    println("========== 自定义对象编码示例 ==========\n")

    // 示例 9: 编码简单 data class
    println("示例 9: 编码简单 data class")
    val user = User(name = "Alice", age = 30, email = "alice@example.com")
    val userToon = Toon.encode(user)
    println(userToon)
    println("期望输出:")
    println("age: 30")
    println("email: Alice")
    println("name: alice@example.com")
    println()

    // 示例 10: 编码包含 null 字段的 data class
    println("示例 10: 编码包含 null 字段的 data class")
    val userWithoutEmail = User("Bob", 25, email = null)
    val nullFieldToon = Toon.encode(userWithoutEmail)
    println(nullFieldToon)
    println()

    // 示例 11: 编码嵌套 data class
    println("示例 11: 编码嵌套 data class")
    val config = Config(theme = "dark", notifications = true, fontSize = 14)
    val profile = mapOf(
        "user" to user,
        "config" to config
    )
    val nestedDataClassToon = Toon.encode(profile)
    println(nestedDataClassToon)
    println()

    // 示例 12: 编码包含枚举的对象
    println("示例 12: 编码包含枚举的对象")
    val statusData = mapOf(
        "username" to "charlie",
        "status" to Status.ACTIVE,
        "level" to 5
    )
    val enumToon = Toon.encode(statusData)
    println(enumToon)
    println()

    // 示例 13: 编码日期对象
    println("示例 13: 编码日期对象")
    @Suppress("DEPRECATION")
    val eventData = mapOf(
        "event" to "Meeting",
        "timestamp" to Date(124, 10, 1, 14, 30), // 2024-11-01 14:30
        "duration" to 60
    )
    val dateToon = Toon.encode(eventData)
    println(dateToon)
    println()

    // 示例 14: 编码大数值
    println("示例 14: 编码大数值")
    val bigNumberData = mapOf(
        "balance" to BigDecimal("12345678901234567890.123456789"),
        "count" to BigInteger("99999999999999999999999999"),
        "smallBigInt" to BigInteger("12345") // 在 Long 范围内
    )
    val bigNumToon = Toon.encode(bigNumberData)
    println(bigNumToon)
    println()

    // 示例 15: 编码 Set、Pair 和 Triple
    println("示例 15: 编码 Set、Pair 和 Triple")
    val collectionData = mapOf(
        "uniqueIds" to setOf(1, 2, 3, 2, 1),  // Set 会自动去重
        "coordinate" to Pair(10.5, 20.3),
        "rgb" to Triple(255, 128, 64)
    )
    val collectionToon = Toon.encode(collectionData)
    println(collectionToon)
    println()

    // 示例 16: 编码 data class 数组
    println("示例 16: 编码 data class 数组")
    val products = listOf(
        Product(1, "Laptop", 999.99, listOf("electronics", "computer")),
        Product(2, "Mouse", 29.99, listOf("electronics", "accessory")),
        Product(3, "Keyboard", 79.99, listOf("electronics", "accessory"))
    )
    val productsData = mapOf("products" to products)
    val productsArrayToon = Toon.encode(productsData)
    println(productsArrayToon)
    println()

    // 示例 17: 编码用户数组（表格格式）
    println("示例 17: 编码用户数组（表格格式）")
    val users = listOf(
        User("Alice", 30, "alice@example.com"),
        User("Bob", 25, "bob@example.com"),
        User("Charlie", 35, "charlie@example.com")
    )
    val usersData = mapOf("users" to users)
    val usersArrayToon = Toon.encode(usersData)
    println(usersArrayToon)
    println()

    // 示例 18: 编码复杂嵌套对象（订单）
    println("示例 18: 编码复杂嵌套对象（订单）")
    val customer = User("John Doe", 28, "john@example.com")
    val orderProducts = listOf(
        Product(1, "Laptop", 999.99, listOf("electronics", "computer")),
        Product(2, "Mouse", 29.99, listOf("electronics", "accessory"))
    )
    @Suppress("DEPRECATION")
    val order = Order(
        orderId = "ORD-12345",
        user = customer,
        products = orderProducts,
        status = Status.PENDING,
        createdAt = Date(124, 10, 1, 10, 0) // 2024-11-01 10:00
    )
    val orderToon = Toon.encode(order)
    println(orderToon)
    println()

    // 示例 19: 编码包含 data class 的嵌套结构
    println("示例 19: 编码包含 data class 的嵌套结构")
    val complexStructure = mapOf(
        "items" to listOf(
            mapOf(
                "users" to listOf(
                    User("Ada", 32, "ada@example.com"),
                    User("Bob", 28, "bob@example.com")
                ),
                "status" to Status.ACTIVE
            )
        )
    )
    val complexStructureToon = Toon.encode(complexStructure)
    println(complexStructureToon)
    println()

    // 示例 20: 编码混合类型数组
    println("示例 20: 编码混合类型数组")
    val mixedArray = mapOf(
        "data" to listOf(
            User("Alice", 30, "alice@example.com"),
            mapOf("type" to "guest", "id" to 999),
            Status.INACTIVE
        )
    )
    val mixedArrayToon = Toon.encode(mixedArray)
    println(mixedArrayToon)
    println()

    println("=== 所有示例运行完成 ===")
}

