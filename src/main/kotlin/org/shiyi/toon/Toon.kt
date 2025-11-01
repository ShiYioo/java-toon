package org.shiyi.toon

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
}

/**
 * Extension function to encode any value to TOON format.
 * 使用扩展函数提供更符合 Kotlin 习惯的 API
 */
public fun Any?.toToon(options: EncodeOptions = EncodeOptions()): String =
    Toon.encode(this, options)

/**
 * Extension function to decode a TOON format string.
 */
public fun String.fromToon(options: DecodeOptions = DecodeOptions()): JsonValue =
    Toon.decode(this, options)

