package org.shiyi.toon

/**
 * Type aliases for JSON values.
 * 使用 typealias 简化类型声明，保持与 TypeScript 版本的对应关系
 */
public typealias JsonPrimitive = Any?
public typealias JsonObject = Map<String, Any?>
public typealias JsonArray = List<Any?>
public typealias JsonValue = Any?

/**
 * Depth level in TOON structure.
 * 使用 inline value class 提供零开销的类型安全
 */
@JvmInline
public value class Depth(public val value: Int) {
    public operator fun plus(other: Int): Depth = Depth(value + other)
    public operator fun compareTo(other: Depth): Int = value.compareTo(other.value)
    public operator fun compareTo(other: Int): Int = value.compareTo(other)

    public companion object {
        public val ZERO: Depth = Depth(0)
    }
}

/**
 * Encoder options.
 * 使用 data class 提供便捷的复制和相等性比较
 */
public data class EncodeOptions(
    val indent: Int = 2,
    val delimiter: Delimiter = Delimiter.DEFAULT,
    val lengthMarker: Boolean = false
) {
    init {
        require(indent > 0) { "indent must be positive, got: $indent" }
    }
}

/**
 * Decoder options.
 */
public data class DecodeOptions(
    val indent: Int = 2,
    val strict: Boolean = true
) {
    init {
        require(indent > 0) { "indent must be positive, got: $indent" }
    }
}

/**
 * Array header information.
 */
public data class ArrayHeaderInfo(
    val key: String? = null,
    val length: Int,
    val delimiter: Delimiter,
    val fields: List<String>? = null,
    val hasLengthMarker: Boolean = false
)

/**
 * Parsed line information.
 */
public data class ParsedLine(
    val raw: String,
    val depth: Depth,
    val indent: Int,
    val content: String,
    val lineNumber: Int
)

/**
 * Blank line information.
 */
public data class BlankLineInfo(
    val lineNumber: Int,
    val indent: Int,
    val depth: Depth
)

