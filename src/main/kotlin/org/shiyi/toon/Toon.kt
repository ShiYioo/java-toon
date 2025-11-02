package org.shiyi.toon

import kotlin.reflect.KClass

/**
 * TOON (Typed Object Oriented Notation) - Main API.
 *
 * 这是主入口类，提供简洁的编码和解码 API。
 * 使用 object 单例模式，符合 Kotlin 习惯。
 */
public object Toon {

    /**
     * Encodes a value to TOON format string.
     *
     * @param value The value to encode
     * @param options Encoding options
     * @return The TOON formatted string
     *
     * Example:
     * ```
     * val data = mapOf("name" to "Alice", "age" to 30)
     * val toon = Toon.encode(data)
     * ```
     */
    public fun encode(value: Any?, options: EncodeOptions = EncodeOptions()): String {
        return org.shiyi.toon.encode.encode(value, options)
    }

    /**
     * Decodes a TOON format string to a structured value.
     *
     * @param input The TOON formatted string
     * @param options Decoding options
     * @return The decoded value
     * @throws IllegalArgumentException if the input is invalid
     *
     * Example:
     * ```
     * val toon = "name: Alice\nage: 30"
     * val data = Toon.decode(toon)
     * ```
     */
    public fun decode(input: String, options: DecodeOptions = DecodeOptions()): JsonValue {
        require(input.isNotBlank()) { "Cannot decode empty input" }
        return org.shiyi.toon.decode.decode(input, options)
    }

    /**
     * Decodes a TOON format string and converts it to the specified type T.
     *
     * 使用 Kotlin reified 泛型和 Jackson 提供类型安全的反序列化。
     *
     * @param T 目标类型
     * @param input The TOON formatted string
     * @param options Decoding options
     * @return The decoded and converted object of type T
     * @throws IllegalArgumentException if the input is invalid or type conversion fails
     *
     * Example:
     * ```kotlin
     * data class User(val name: String, val age: Int)
     *
     * val toon = "name: Alice\nage: 30"
     * val user = Toon.decodeAs<User>(toon)
     * println(user.name) // Alice
     * ```
     */
    public inline fun <reified T : Any> decodeAs(input: String, options: DecodeOptions = DecodeOptions()): T {
        val jsonValue = decode(input, options)
        return ToonMapper.mapTo(jsonValue)
    }

    /**
     * Decodes a TOON format string and converts it to the specified KClass type.
     *
     * 使用 KClass 参数提供运行时类型转换，适用于 Java 互操作或动态类型场景。
     *
     * @param input The TOON formatted string
     * @param kClass The target type class
     * @param options Decoding options
     * @return The decoded and converted object
     * @throws IllegalArgumentException if the input is invalid or type conversion fails
     *
     * Example:
     * ```kotlin
     * val user = Toon.decodeAs(toon, User::class)
     * ```
     */
    public fun <T : Any> decodeAs(
        input: String,
        kClass: KClass<T>,
        options: DecodeOptions = DecodeOptions()
    ): T {
        val jsonValue = decode(input, options)
        return ToonMapper.mapTo(jsonValue, kClass)
    }
}

/**
 * Extension function to encode any value to TOON format.
 * 使用扩展函数提供更符合 Kotlin 习惯的 API
 *
 * Example:
 * ```kotlin
 * val toon = mapOf("name" to "Alice").toToon()
 * ```
 */
public fun Any?.toToon(options: EncodeOptions = EncodeOptions()): String =
    Toon.encode(this, options)

/**
 * Extension function to decode a TOON format string.
 *
 * Example:
 * ```kotlin
 * val data = toonString.fromToon()
 * ```
 */
public fun String.fromToon(options: DecodeOptions = DecodeOptions()): JsonValue =
    Toon.decode(this, options)

/**
 * Extension function to decode a TOON format string and convert to specified type.
 *
 * 使用 reified 泛型提供类型安全且简洁的 API。
 *
 * Example:
 * ```kotlin
 * data class User(val name: String, val age: Int)
 *
 * val toon = "name: Alice\nage: 30"
 * val user = toon.fromToonAs<User>()
 * ```
 */
public inline fun <reified T : Any> String.fromToonAs(options: DecodeOptions = DecodeOptions()): T =
    Toon.decodeAs(this, options)

/**
 * Extension function to decode a TOON format string and convert to specified KClass type.
 *
 * Example:
 * ```kotlin
 * val user = toonString.fromToonAs(User::class)
 * ```
 */
public fun <T : Any> String.fromToonAs(kClass: KClass<T>, options: DecodeOptions = DecodeOptions()): T =
    Toon.decodeAs(this, kClass, options)

