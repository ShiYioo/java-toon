# TOON for Kotlin/Java

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**Token-Oriented Object Notation (TOON)** 的 Kotlin/Java 实现 - 一种为大语言模型优化的紧凑、人类可读的数据格式。

> 🚀 **TOON 是什么？** 一种专为 LLM 输入优化的数据格式，比 JSON 节省 **40-60% 的 tokens**，同时保持良好的可读性。

## ✨ 特性

- 🎯 **简洁的 API** - 符合 Kotlin 设计美学，提供类型安全的编解码
- 🔄 **双向转换** - 完整支持 `encode` 和 `decode`
- 💪 **类型化解码** - 使用 Jackson 实现强类型反序列化（`decodeAs<T>()`）
- 📦 **丰富的类型支持** - 支持 Kotlin/Java 所有常见类型
- ✅ **完整测试** - 单元测试覆盖率高，与 TypeScript 版本行为一致
- 🔧 **灵活配置** - 支持自定义缩进、分隔符等选项

## 📦 安装

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("org.shiyi:java-toon:0.0.1")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'org.shiyi:java-toon:0.0.1'
}
```

### Maven

```xml
<dependency>
    <groupId>org.shiyi</groupId>
    <artifactId>java-toon</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 🚀 快速开始

### 基本用法

```kotlin
import org.shiyi.toon.*

// 编码：Kotlin 对象 -> TOON 字符串
val data = mapOf(
    "name" to "Alice",
    "age" to 30,
    "tags" to listOf("kotlin", "developer")
)
val toon = Toon.encode(data)
println(toon)
// 输出:
// name: Alice
// age: 30
// tags[2]: kotlin,developer

// 解码：TOON 字符串 -> Kotlin 对象
val decoded = Toon.decode(toon)
println(decoded)
// {name=Alice, age=30, tags=[kotlin, developer]}
```

### 使用扩展函数

```kotlin
// 使用扩展函数更符合 Kotlin 习惯
val toon = data.toToon()
val decoded = toon.fromToon()
```

### 类型化解码（推荐）

```kotlin
// 定义数据类
data class User(
    val name: String,
    val age: Int,
    val tags: List<String>
)

// 编码
val user = User("Alice", 30, listOf("kotlin", "developer"))
val toon = Toon.encode(user)

// 类型化解码 - 自动转换为 User 对象
val decoded = Toon.decodeAs<User>(toon)
println(decoded.name)  // Alice

// 或使用扩展函数
val decoded2 = toon.fromToonAs<User>()
```

## 📚 TOON 格式示例

### 简单对象

```kotlin
// Kotlin
val user = mapOf(
    "name" to "Alice",
    "age" to 30,
    "active" to true
)

// TOON 格式
name: Alice
age: 30
active: true
```

### 嵌套对象

```kotlin
// Kotlin
val profile = mapOf(
    "user" to mapOf(
        "name" to "Alice",
        "email" to "alice@example.com"
    ),
    "settings" to mapOf(
        "theme" to "dark",
        "notifications" to true
    )
)

// TOON 格式
user:
  name: Alice
  email: alice@example.com
settings:
  theme: dark
  notifications: true
```

### 原始类型数组（内联格式）

```kotlin
// Kotlin
val data = mapOf(
    "tags" to listOf("kotlin", "java", "scala"),
    "scores" to listOf(95, 87, 92)
)

// TOON 格式
tags[3]: kotlin,java,scala
scores[3]: 95,87,92
```

### 对象数组（表格格式）

```kotlin
// Kotlin
data class Product(val id: Int, val name: String, val price: Double)

val products = listOf(
    Product(1, "Laptop", 999.99),
    Product(2, "Mouse", 29.99)
)
val data = mapOf("products" to products)

// TOON 格式
products[2]{id,name,price}:
  1,Laptop,999.99
  2,Mouse,29.99
```

### 对象数组（列表格式）

```kotlin
// Kotlin - 当对象包含数组或嵌套对象时使用列表格式
val items = listOf(
    mapOf(
        "id" to 1,
        "tags" to listOf("a", "b")
    ),
    mapOf(
        "id" to 2,
        "tags" to listOf("c", "d")
    )
)

// TOON 格式
items[2]:
  - id: 1
    tags[2]: a,b
  - id: 2
    tags[2]: c,d
```

## 🔧 配置选项

### 编码选项

```kotlin
val options = EncodeOptions(
    indent = 2,                    // 缩进空格数（默认 2）
    delimiter = Delimiter.COMMA,   // 数组分隔符（COMMA, TAB, PIPE）
    lengthMarker = false           // 是否显示长度标记 #
)

val toon = Toon.encode(data, options)
```

### 解码选项

```kotlin
val options = DecodeOptions(
    indent = 2,      // 缩进空格数（默认 2）
    strict = true    // 严格模式：检查缩进对齐（默认 true）
)

val decoded = Toon.decode(toon, options)
```

### 自定义分隔符

```kotlin
// 使用制表符分隔
val options = EncodeOptions(delimiter = Delimiter.TAB)
val toon = Toon.encode(data, options)
// tags[3	]: kotlin	java	scala

// 使用管道符分隔
val options2 = EncodeOptions(delimiter = Delimiter.PIPE)
val toon2 = Toon.encode(data, options2)
// tags[3|]: kotlin|java|scala
```

## 📖 API 参考

### Toon 对象

```kotlin
object Toon {
    // 编码：任意对象 -> TOON 字符串
    fun encode(value: Any?, options: EncodeOptions = EncodeOptions()): String
    
    // 解码：TOON 字符串 -> JsonValue (Map/List/原始类型)
    fun decode(input: String, options: DecodeOptions = DecodeOptions()): JsonValue
    
    // 类型化解码：TOON 字符串 -> 指定类型 T
    inline fun <reified T : Any> decodeAs(
        input: String, 
        options: DecodeOptions = DecodeOptions()
    ): T
    
    // 类型化解码：使用 KClass（用于 Java 互操作）
    fun <T : Any> decodeAs(
        input: String, 
        kClass: KClass<T>, 
        options: DecodeOptions = DecodeOptions()
    ): T
}
```

### 扩展函数

```kotlin
// 编码扩展
fun Any?.toToon(options: EncodeOptions = EncodeOptions()): String

// 解码扩展
fun String.fromToon(options: DecodeOptions = DecodeOptions()): JsonValue

// 类型化解码扩展
inline fun <reified T : Any> String.fromToonAs(
    options: DecodeOptions = DecodeOptions()
): T

fun <T : Any> String.fromToonAs(
    kClass: KClass<T>, 
    options: DecodeOptions = DecodeOptions()
): T
```

### ToonMapper

```kotlin
object ToonMapper {
    // 将 JsonValue 转换为指定类型
    inline fun <reified T : Any> mapTo(value: JsonValue): T
    
    fun <T : Any> mapTo(value: JsonValue, kClass: KClass<T>): T
    
    // 将对象转换为 JsonValue
    fun toJsonValue(obj: Any?): JsonValue
}
```

## 🎯 支持的类型

### 基本类型
- ✅ `null`, `Boolean`, `String`
- ✅ `Int`, `Long`, `Float`, `Double`
- ✅ `BigInteger`, `BigDecimal`

### 集合类型
- ✅ `List`, `MutableList`, `ArrayList`
- ✅ `Set`, `MutableSet`, `LinkedHashSet`
- ✅ `Map`, `MutableMap`, `HashMap`
- ✅ 所有原始类型数组：`IntArray`, `DoubleArray`, `BooleanArray` 等

### Kotlin 特有类型
- ✅ `Pair`, `Triple`
- ✅ `data class`
- ✅ `enum class`

### Java 类型
- ✅ `Date`, `Instant`, `LocalDate`, `LocalDateTime`, `ZonedDateTime`
- ✅ `StringBuilder`, `StringBuffer`
- ✅ 自定义 Java 类（通过 Jackson）

### 特殊值处理
- ✅ `NaN`, `Infinity` → `null`
- ✅ `-0.0` → `0`
- ✅ 整数值的浮点数：`5.0` → `"5"`（无小数点）

## 💡 使用场景

### 1. LLM 提示工程

TOON 专为 LLM 输入优化，可显著减少 token 消耗：

```kotlin
// 准备数据
val users = listOf(
    User("Alice", 30, listOf("kotlin", "java")),
    User("Bob", 25, listOf("python", "go"))
)

// 编码为 TOON
val toon = Toon.encode(mapOf("users" to users))

// 在 LLM 提示中使用
val prompt = """
分析以下用户数据：

$toon

请总结用户的技能分布。
""".trimIndent()
```

### 2. 配置文件

TOON 格式人类可读，适合作为配置文件：

```kotlin
// 读取 TOON 配置
val configToon = File("config.toon").readText()
val config = Toon.decodeAs<AppConfig>(configToon)

// 写入 TOON 配置
val newConfig = AppConfig(...)
File("config.toon").writeText(Toon.encode(newConfig))
```

### 3. API 数据传输

在需要优化数据传输大小时使用：

```kotlin
// API 响应编码为 TOON
@GetMapping("/users")
fun getUsers(): String {
    val users = userService.findAll()
    return Toon.encode(users)
}

// 客户端解码
val response = api.getUsers()
val users = Toon.decodeAs<List<User>>(response)
```

## 🔍 高级用法

### 往返测试（Round-trip）

```kotlin
// 确保数据可以完整往返
val original = Product(1, "Laptop", 999.99)
val encoded = Toon.encode(original)
val decoded = Toon.decodeAs<Product>(encoded)
assert(original == decoded)
```

### 处理 Float/Double 字段

```kotlin
data class Point(val x: Double, val y: Double)

// 整数值的 Double 会被编码为整数字符串
val point1 = Point(5.0, 10.0)
val toon1 = Toon.encode(point1)
// x: 5
// y: 10

// 解码时 Jackson 会自动转换回 Double
val decoded1 = Toon.decodeAs<Point>(toon1)
assert(decoded1.x == 5.0)  // ✅

// 带小数的 Double 正常编码
val point2 = Point(5.5, 10.3)
val toon2 = Toon.encode(point2)
// x: 5.5
// y: 10.3
```

详见：[Float/Double 行为说明](FLOAT_DOUBLE_BEHAVIOR.md)

### 自定义对象序列化

TOON 使用 Jackson 处理对象序列化，支持所有 Jackson 注解：

```kotlin
data class User(
    @JsonProperty("user_name")
    val name: String,
    
    @JsonIgnore
    val password: String,
    
    val email: String
)

// password 字段会被忽略
val toon = Toon.encode(User("Alice", "secret", "alice@example.com"))
```

## 🧪 测试

运行所有测试：

```bash
./gradlew test
```

运行特定测试：

```bash
# 编码器测试
./gradlew test --tests "EncoderTest"

# 解码器测试
./gradlew test --tests "DecoderTest"

# Kotlin 内置类型测试
./gradlew test --tests "KotlinBuiltinTypesTest"

# Float/Double 往返测试
./gradlew test --tests "FloatDoubleRoundTripTest"
```

## 📊 Token 效率对比

以下是与 JSON 的 token 对比（使用 GPT-4 tokenizer）：

| 数据类型 | JSON Tokens | TOON Tokens | 节省 |
|---------|-------------|-------------|------|
| 简单对象数组 (100条) | 2,450 | 980 | **60%** |
| 嵌套结构 | 1,820 | 1,100 | **40%** |
| 原始类型数组 | 450 | 180 | **60%** |

> 💡 **最佳实践**：TOON 在处理**相同结构的对象数组**时效率最高（表格格式）。

## ⚠️ 注意事项

### 类型丢失

TOON（类似 JSON）是无类型文本格式：

```kotlin
// ❌ 往返时类型可能丢失
val original = mapOf("value" to 5.0)  // Double
val decoded = Toon.decode(Toon.encode(original))
// decoded["value"] 是 Int(5)，不是 Double(5.0)

// ✅ 使用类型化解码保持类型
data class Data(val value: Double)
val original2 = Data(5.0)
val decoded2 = Toon.decodeAs<Data>(Toon.encode(original2))
// decoded2.value 是 Double(5.0) ✅
```

### 数字格式

- 整数值的浮点数（如 `5.0`）会被编码为 `"5"`（去掉 `.0`）
- 解码时 `"5"` 被解析为 `Int`，`"5.5"` 被解析为 `Double`
- 这与 JavaScript 行为一致

### 严格模式

在严格模式下（默认），TOON 会检查：
- 缩进必须是 `indent` 的精确倍数
- 不允许使用制表符作为缩进
- 列表项和表格行中不允许有空行

```kotlin
// 关闭严格模式以容忍格式错误
val options = DecodeOptions(strict = false)
val decoded = Toon.decode(toon, options)
```

## 🔗 相关资源

- [TOON 规范](https://github.com/pnchnk/toon/blob/main/SPEC.md)
- [TypeScript 实现](https://github.com/pnchnk/toon)
- [Float/Double 行为说明](FLOAT_DOUBLE_BEHAVIOR.md)
- [实现指南](IMPLEMENTATION_GUIDE.md)
- [测试总结](TEST_SUMMARY.md)

## 🤝 贡献

欢迎贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启 Pull Request

### 代码规范

- 遵循 Kotlin 设计美学和最佳实践
- 使用有意义的变量名和函数名
- 添加适当的注释和文档
- 确保所有测试通过
- 新功能需要添加对应的测试

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源。

## 👨‍💻 作者

**ShiYi** - [@ShiYioo](https://github.com/ShiYioo)

## 🙏 致谢

- [TOON 格式](https://github.com/johannschopplich/toon) 的原始设计者
- [Jackson](https://github.com/FasterXML/jackson) - 用于对象序列化
- Kotlin 社区

## 关于一些问题
- 为什么不对encode方法提供函数重载来给出更友好的提示，而是设置了Any？类型，这是不是不符合Kotlin的设计美学？

-- 因为这里几乎支持了Kotlin的所有类型，如果一个个写出重载可能不太好。在根据原ts项目的设计中我选择了Any？
- 为什么从toon字符串转化到object时不采用fastjson或者其他的json库，而是使用了jackson？

-- 我觉得均衡来说，Jackson比较好一点。如果想自己进行修改，请自行修改ToonMapper.kt文件
- 为什么不提供spring bean 来自定义配置？

-- 目前是测试版。

- 你返回的JsonValue是Any?类型，这合适吗？

-- 这是为了和ts版本保持一致，JsonValue可以是Map/List/原始类型，想获得返回类型需要手动 as 一下。如果你觉得不合适，可以自行封装一层。

---

<p align="center">
  Made with ❤️ using Kotlin
</p>

