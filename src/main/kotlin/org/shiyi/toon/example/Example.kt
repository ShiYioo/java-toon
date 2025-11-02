package org.shiyi.toon.example

import org.shiyi.toon.Toon
import org.shiyi.toon.fromToon
import org.shiyi.toon.toToon
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

/**
 * 将解码结果格式化为更易读的 JSON 格式
 */
private fun formatDecodeResult(value: Any?, indent: Int = 0): String {
    val indentStr = "  ".repeat(indent)
    return when (value) {
        null -> "null"
        is String -> "\"$value\""
        is Boolean, is Number -> value.toString()
        is Map<*, *> -> {
            if (value.isEmpty()) return "{}"
            val entries = value.entries.joinToString(",\n") { (k, v) ->
                "$indentStr  \"$k\": ${formatDecodeResult(v, indent + 1)}"
            }
            "{\n$entries\n$indentStr}"
        }
        is List<*> -> {
            if (value.isEmpty()) return "[]"
            val items = value.joinToString(",\n") { item ->
                "$indentStr  ${formatDecodeResult(item, indent + 1)}"
            }
            "[\n$items\n$indentStr]"
        }
        else -> value.toString()
    }
}

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

    println("\n========== 复杂 TOON 解码示例 ==========\n")

    // 示例 21: 解码嵌套对象
    println("示例 21: 解码嵌套对象")
    val nestedToonInput = """
        a:
          b:
            c: deep
    """.trimIndent()
    val nestedDecoded = Toon.decode(nestedToonInput)
    println("输入:\n$nestedToonInput")
    println("解码结果:\n${formatDecodeResult(nestedDecoded)}")
    println()

    // 示例 22: 解码原始类型数组
    println("示例 22: 解码原始类型数组")
    val primitiveArrayToonInput = """
        tags[3]: reading,gaming,coding
        nums[3]: 1,2,3
        data[4]: x,y,true,10
    """.trimIndent()
    val primitiveArrayDecoded = Toon.decode(primitiveArrayToonInput)
    println("输入:\n$primitiveArrayToonInput")
    println("解码结果:\n${formatDecodeResult(primitiveArrayDecoded)}")
    println()

    // 示例 23: 解码表格格式的对象数组
    println("示例 23: 解码表格格式的对象数组")
    val tabularToonInput = """
        items[2]{sku,qty,price}:
          A1,2,9.99
          B2,1,14.5
    """.trimIndent()
    val tabularDecoded = Toon.decode(tabularToonInput)
    println("输入:\n$tabularToonInput")
    println("解码结果:\n${formatDecodeResult(tabularDecoded)}")
    println()

    // 示例 24: 解码带引号的字符串数组
    println("示例 24: 解码带引号的字符串数组")
    val quotedArrayToonInput = """
        items[3]: a,"b,c","d:e"
        values[4]: x,"true","42","-3.14"
    """.trimIndent()
    val quotedArrayDecoded = Toon.decode(quotedArrayToonInput)
    println("输入:\n$quotedArrayToonInput")
    println("解码结果:\n${formatDecodeResult(quotedArrayDecoded)}")
    println()

    // 示例 25: 解码列表格式的对象数组
    println("示例 25: 解码列表格式的对象数组")
    val listItemsToonInput = """
        items[2]:
          - id: 1
            name: First
          - id: 2
            name: Second
            extra: true
    """.trimIndent()
    val listItemsDecoded = Toon.decode(listItemsToonInput)
    println("输入:\n$listItemsToonInput")
    println("解码结果:\n${formatDecodeResult(listItemsDecoded)}")
    println()

    // 示例 26: 解码带有嵌套值的列表项
    println("示例 26: 解码带有嵌套值的列表项")
    val nestedListToonInput = """
        items[1]:
          - id: 1
            nested:
              x: 1
              y: 2
    """.trimIndent()
    val nestedListDecoded = Toon.decode(nestedListToonInput)
    println("输入:\n$nestedListToonInput")
    println("解码结果:\n${formatDecodeResult(nestedListDecoded)}")
    println()

    // 示例 27: 解码嵌套的表格数组
    println("示例 27: 解码嵌套的表格数组")
    val nestedTabularToonInput = """
        items[1]:
          - users[2]{id,name}:
            1,Ada
            2,Bob
            status: active
    """.trimIndent()
    val nestedTabularDecoded = Toon.decode(nestedTabularToonInput)
    println("输入:\n$nestedTabularToonInput")
    println("解码结果:\n${formatDecodeResult(nestedTabularDecoded)}")
    println()

    // 示例 28: 解码混合数组（原始类型、对象和字符串）
    println("示例 28: 解码混合数组")
    val mixedListToonInput = """
        items[3]:
          - 1
          - a: 1
          - text
    """.trimIndent()
    val mixedListDecoded = Toon.decode(mixedListToonInput)
    println("输入:\n$mixedListToonInput")
    println("解码结果:\n${formatDecodeResult(mixedListDecoded)}")
    println()

    // 示例 29: 解码复杂混合结构
    println("示例 29: 解码复杂混合结构")
    val complexMixedToonInput = """
        user:
          id: 123
          name: Ada
          tags[2]: reading,gaming
          active: true
          prefs[0]:
    """.trimIndent()
    val complexMixedDecoded = Toon.decode(complexMixedToonInput)
    println("输入:\n$complexMixedToonInput")
    println("解码结果:\n${formatDecodeResult(complexMixedDecoded)}")
    println()

    // 示例 30: 解码嵌套原始类型数组
    println("示例 30: 解码嵌套原始类型数组")
    val nestedPrimitivesToonInput = """
        matrix[2]:
          - [3]: 1,2,3
          - [3]: 4,5,6
    """.trimIndent()
    val nestedPrimitivesDecoded = Toon.decode(nestedPrimitivesToonInput)
    println("输入:\n$nestedPrimitivesToonInput")
    println("解码结果:\n${formatDecodeResult(nestedPrimitivesDecoded)}")
    println()

    // 示例 31: 解码空数组和空对象
    println("示例 31: 解码空数组和空对象")
    val emptyStructuresToonInput = """
        items[0]:
        config:
    """.trimIndent()
    val emptyStructuresDecoded = Toon.decode(emptyStructuresToonInput)
    println("输入:\n$emptyStructuresToonInput")
    println("解码结果:\n${formatDecodeResult(emptyStructuresDecoded)}")
    println()

    // 示例 32: 解码包含 null 和布尔值的表格
    println("示例 32: 解码包含 null 和布尔值的表格")
    val nullBoolTableToonInput = """
        items[3]{id,value,active}:
          1,null,true
          2,"test",false
          3,null,null
    """.trimIndent()
    val nullBoolTableDecoded = Toon.decode(nullBoolTableToonInput)
    println("输入:\n$nullBoolTableToonInput")
    println("解码结果:\n${formatDecodeResult(nullBoolTableDecoded)}")
    println()

    // 示例 33: 解码 Unicode 和 Emoji
    println("示例 33: 解码 Unicode 和 Emoji")
    val unicodeToonInput = """
        name: 你好世界
        emoji: 🚀
        greeting: hello 👋 world
        café: café
    """.trimIndent()
    val unicodeDecoded = Toon.decode(unicodeToonInput)
    println("输入:\n$unicodeToonInput")
    println("解码结果:\n${formatDecodeResult(unicodeDecoded)}")
    println()

    // 示例 34: 解码转义字符
    println("示例 34: 解码转义字符")
    val escapedToonInput = """
        path: "C:\\Users\\path"
        multiline: "line1\nline2"
        quoted: "say \"hello\""
    """.trimIndent()
    val escapedDecoded = Toon.decode(escapedToonInput)
    println("输入:\n$escapedToonInput")
    println("解码结果:\n${formatDecodeResult(escapedDecoded)}")
    println()

    // 示例 35: 编码后再解码（往返测试）
    println("示例 35: 编码后再解码（往返测试）")
    val originalData = mapOf(
        "users" to listOf(
            mapOf("id" to 1, "name" to "Alice", "active" to true),
            mapOf("id" to 2, "name" to "Bob", "active" to false)
        ),
        "config" to mapOf(
            "theme" to "dark",
            "fontSize" to 14
        ),
        "tags" to listOf("kotlin", "toon", "serialization")
    )
    val encodedToon = Toon.encode(originalData)
    println("原始数据:\n${formatDecodeResult(originalData)}")
    println("\n编码为 TOON:\n$encodedToon")
    val roundTripDecoded = Toon.decode(encodedToon)
    println("\n解码回数据:\n${formatDecodeResult(roundTripDecoded)}")
    println("\n往返匹配: ${originalData == roundTripDecoded}")
    println()

    // 示例 36: 解码实际业务场景（用户配置）
    println("示例 36: 解码实际业务场景（用户配置）")
    val userProfileToonInput = """
        profile:
          userId: u123
          username: alice_dev
          email: alice@example.com
          preferences:
            language: zh-CN
            theme: dark
            notifications: true
          roles[3]: admin,developer,reviewer
          metadata:
            lastLogin: 2024-11-01T10:30:00Z
            loginCount: 42
    """.trimIndent()
    val userProfileDecoded = Toon.decode(userProfileToonInput)
    println("输入:\n$userProfileToonInput")
    println("解码结果:\n${formatDecodeResult(userProfileDecoded)}")
    println()

    // 示例 37: 解码实际业务场景（订单数据）
    println("示例 37: 解码实际业务场景（订单数据）")
    val orderDataToonInput = """
        order:
          orderId: ORD-2024-001
          customer:
            name: John Doe
            email: john@example.com
          items[2]{productId,name,quantity,price}:
            P001,Laptop,1,999.99
            P002,Mouse,2,29.99
          shipping:
            address: 123 Main St
            city: Beijing
            country: China
          total: 1059.97
          status: pending
    """.trimIndent()
    val orderDataDecoded = Toon.decode(orderDataToonInput)
    println("输入:\n$orderDataToonInput")
    println("解码结果:\n${formatDecodeResult(orderDataDecoded)}")
    println()

    println("=== 所有示例运行完成 ===")
}

