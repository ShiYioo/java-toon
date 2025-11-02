package org.shiyi.toon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import kotlin.reflect.KClass

/**
 * TOON Mapper - 将 JsonValue 映射到指定的 Kotlin 类型
 *
 * 使用 Jackson 实现类型安全的反序列化，支持：
 * - Data classes
 * - 嵌套对象
 * - 集合类型
 * - 枚举
 * - 日期时间
 * - 所有 Jackson 支持的类型
 *
 * 遵循 Kotlin 设计美学，提供简洁的 API 和强大的类型推断。
 */
public object ToonMapper {

    /**
     * Jackson ObjectMapper 实例
     * 配置了 Kotlin 模块和 Java 8 时间模块
     */
    public val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    /**
     * 将 JsonValue 转换为指定类型 T
     *
     * 使用 reified 泛型提供类型安全的转换。
     *
     * @param T 目标类型
     * @param value 要转换的 JsonValue
     * @return 转换后的对象
     * @throws IllegalArgumentException 如果类型不匹配或转换失败
     *
     * 示例：
     * ```kotlin
     * val user: User = ToonMapper.mapTo(jsonValue)
     * ```
     */
    public inline fun <reified T : Any> mapTo(value: JsonValue): T {
        return try {
            objectMapper.convertValue(value, jacksonTypeRef<T>())
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Failed to convert value to type ${T::class.simpleName}: ${e.message}",
                e
            )
        }
    }

    /**
     * 将 JsonValue 转换为指定的 KClass 类型
     *
     * 使用 KClass 参数提供运行时类型转换，适用于 Java 互操作或动态类型场景。
     *
     * @param value 要转换的 JsonValue
     * @param kClass 目标类型的 KClass
     * @return 转换后的对象
     * @throws IllegalArgumentException 如果类型不匹配或转换失败
     *
     * 示例：
     * ```kotlin
     * val user = ToonMapper.mapTo(jsonValue, User::class)
     * ```
     */
    public fun <T : Any> mapTo(value: JsonValue, kClass: KClass<T>): T {
        return try {
            objectMapper.convertValue(value, kClass.java)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Failed to convert value to type ${kClass.simpleName}: ${e.message}",
                e
            )
        }
    }

    /**
     * 将任意对象转换为 JsonValue (Map/List)
     *
     * 用于编码时将自定义对象转换为可序列化的结构。
     *
     * @param obj 要转换的对象
     * @return JsonValue 表示
     */
    @Suppress("UNCHECKED_CAST")
    public fun toJsonValue(obj: Any?): JsonValue {
        return when (obj) {
            null -> null
            is Map<*, *> -> obj as JsonValue
            is List<*> -> obj as JsonValue
            is String, is Number, is Boolean -> obj
            else -> {
                // 使用 Jackson 将对象转换为 Map
                try {
                    objectMapper.convertValue(obj, jacksonTypeRef<Map<String, Any?>>())
                } catch (e: Exception) {
                    throw IllegalArgumentException(
                        "Failed to convert object of type ${obj::class.simpleName} to JsonValue: ${e.message}",
                        e
                    )
                }
            }
        }
    }
}

/**
 * Extension function: 将 JsonValue 转换为指定类型
 *
 * 示例：
 * ```kotlin
 * val user: User = jsonValue.toType()
 * ```
 */
public inline fun <reified T : Any> JsonValue.toType(): T = ToonMapper.mapTo(this)

/**
 * Extension function: 将 JsonValue 转换为指定 KClass 类型
 */
public fun <T : Any> JsonValue.toType(kClass: KClass<T>): T = ToonMapper.mapTo(this, kClass)

