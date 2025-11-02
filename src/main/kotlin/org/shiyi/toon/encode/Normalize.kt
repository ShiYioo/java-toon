package org.shiyi.toon.encode

import org.shiyi.toon.ToonMapper
import java.math.BigDecimal
import java.math.BigInteger
import java.time.temporal.Temporal
import java.util.Date

/**
 * 值规范化器 - 将任意 Kotlin/Java 对象转换为可序列化的基本类型
 *
 * 使用 Jackson 处理复杂对象转换，提供更可靠和高效的实现。
 *
 * @author ShiYi
 */
internal object ValueNormalizer {

    /**
     * 规范化任意值为 TOON 可序列化类型
     *
     * 支持的类型转换：
     * - 基本类型：null, Boolean, Number, String
     * - 日期时间：Date, Temporal -> ISO 8601 字符串
     * - 大数值：BigInteger, BigDecimal -> Number 或 String
     * - 集合类型：Array, List, Set, Collection -> List
     * - 映射类型：Map -> Map<String, Any?>
     * - 元组类型：Pair, Triple -> List
     * - 枚举类型：Enum -> String (name)
     * - 自定义对象：通过 Jackson 转换为 Map<String, Any?>
     *
     * @param value 要规范化的值
     * @return 规范化后的值（null、Boolean、Number、String、List 或 Map）
     */
    fun normalize(value: Any?): Any? = when (value) {
        null -> null

        // 基本类型
        is Boolean -> value
        is String -> value
        is Number -> normalizeNumber(value)

        // 字符相关
        is CharSequence -> value.toString()
        is Char -> value.toString()

        // 日期时间
        is Date -> value.toInstant().toString()
        is Temporal -> value.toString()

        // 大数值
        is BigInteger -> normalizeBigInteger(value)
        is BigDecimal -> normalizeBigDecimal(value)

        // Map（优先处理，因为某些类可能实现了 Map）
        is Map<*, *> -> normalizeMap(value)

        // 集合类型
        is Set<*> -> normalizeSet(value)
        is Collection<*> -> normalizeCollection(value)

        // 数组类型
        is Array<*> -> normalizeArray(value)
        is BooleanArray -> value.toList()
        is ByteArray -> value.toList()
        is ShortArray -> value.toList()
        is IntArray -> value.toList()
        is LongArray -> value.toList()
        is FloatArray -> value.map { normalizeNumber(it) }
        is DoubleArray -> value.map { normalizeNumber(it) }
        is CharArray -> value.map { it.toString() }

        // 枚举
        is Enum<*> -> value.name

        // 元组
        is Pair<*, *> -> listOf(normalize(value.first), normalize(value.second))
        is Triple<*, *, *> -> listOf(
            normalize(value.first),
            normalize(value.second),
            normalize(value.third)
        )

        // 自定义对象（使用 Jackson 转换）
        else -> normalizeObject(value)
    }

    /**
     * 规范化数字类型
     *
     * 处理特殊情况：
     * - NaN、Infinity -> null
     * - -0.0 -> 0.0
     */
    private fun normalizeNumber(value: Number): Any? = when (value) {
        is Float -> when {
            value.isNaN() || value.isInfinite() -> null
            value == -0.0f -> 0.0f
            else -> value
        }
        is Double -> when {
            value.isNaN() || value.isInfinite() -> null
            value == -0.0 -> 0.0
            else -> value
        }
        else -> value
    }

    /**
     * 规范化 BigInteger
     *
     * 策略：在 Long 范围内转换为 Long，否则转换为字符串
     */
    private fun normalizeBigInteger(value: BigInteger): Any =
        if (value >= BigInteger.valueOf(Long.MIN_VALUE) &&
            value <= BigInteger.valueOf(Long.MAX_VALUE)) {
            value.toLong()
        } else {
            value.toString()
        }

    /**
     * 规范化 BigDecimal
     *
     * 策略：尝试无损转换为 Double，否则转换为字符串
     */
    private fun normalizeBigDecimal(value: BigDecimal): Any {
        val doubleValue = value.toDouble()
        return if (doubleValue.isFinite() &&
                   BigDecimal.valueOf(doubleValue).compareTo(value) == 0) {
            doubleValue
        } else {
            value.toPlainString()
        }
    }

    /**
     * 规范化集合
     */
    private fun normalizeCollection(collection: Collection<*>): List<Any?> =
        collection.map { normalize(it) }

    /**
     * 规范化 Set
     */
    private fun normalizeSet(set: Set<*>): List<Any?> =
        set.map { normalize(it) }

    /**
     * 规范化数组
     */
    private fun normalizeArray(array: Array<*>): List<Any?> =
        array.map { normalize(it) }

    /**
     * 规范化 Map
     *
     * 将所有键转换为字符串，值递归规范化
     */
    private fun normalizeMap(map: Map<*, *>): Map<String, Any?> =
        map.entries.associate { (key, value) ->
            key.toString() to normalize(value)
        }

    /**
     * 规范化普通对象
     *
     * 使用 Jackson 将对象转换为 Map，然后递归规范化值。
     *
     * 优点：
     * - 支持所有 Jackson 注解 (@JsonProperty, @JsonIgnore 等)
     * - 处理复杂类型（日期、枚举、嵌套对象等）
     * - 性能更好，代码更简洁
     * - 更可靠，避免反射相关的问题
     *
     * @param obj 要规范化的对象
     * @return 规范化后的 Map
     */
    private fun normalizeObject(obj: Any): Any {
        return try {
            // 使用 Jackson 将对象转换为 Map
            val jsonValue = ToonMapper.toJsonValue(obj)

            // 递归规范化 Map 中的值
            when (jsonValue) {
                is Map<*, *> -> normalizeMap(jsonValue)
                is List<*> -> normalizeCollection(jsonValue)
                else -> jsonValue ?: obj.toString()
            }
        } catch (e: Exception) {
            // Jackson 转换失败，返回字符串表示
            obj.toString()
        }
    }
}

/**
 * 扩展函数：规范化任意值
 *
 * 提供更符合 Kotlin 习惯的 API
 *
 * 示例：
 * ```kotlin
 * data class User(val name: String, val age: Int)
 * val user = User("Alice", 30)
 * val normalized = user.normalize()  // Map("name" to "Alice", "age" to 30)
 * ```
 */
internal fun Any?.normalize(): Any? = ValueNormalizer.normalize(this)

