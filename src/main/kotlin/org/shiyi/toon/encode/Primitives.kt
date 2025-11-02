package org.shiyi.toon.encode

import org.shiyi.toon.*
import org.shiyi.toon.utils.*

/**
 * Primitive value encoding functions.
 * 使用顶层函数而不是 object 类，符合 Kotlin 最佳实践
 */

/**
 * Encodes a JSON primitive value to its TOON string representation.
 */
public fun encodePrimitive(value: JsonPrimitive, delimiter: Char = Delimiter.DEFAULT.char): String = when (value) {
    null -> ToonConstants.NULL_LITERAL
    is Boolean -> value.toString()
    is Number -> encodeNumber(value)
    is String -> encodeStringLiteral(value, delimiter)
    else -> throw IllegalArgumentException("Unsupported primitive type: ${value::class}")
}

/**
 * Encodes a number, formatting it appropriately.
 *
 */
private fun encodeNumber(value: Number): String {
    return when (value) {
        is Double -> {
            // 检查是否为整数值
            if (value.isFinite() && value == value.toLong().toDouble()) {
                value.toLong().toString()
            } else {
                value.toString()
            }
        }
        is Float -> {
            // 检查是否为整数值
            if (value.isFinite() && value == value.toLong().toFloat()) {
                value.toLong().toString()
            } else {
                value.toString()
            }
        }
        else -> value.toString()
    }
}

/**
 * Encodes a string literal, adding quotes if necessary.
 */
public fun encodeStringLiteral(value: String, delimiter: Char = ToonConstants.COMMA): String =
    if (value.isSafeUnquoted(delimiter)) {
        value
    } else {
        "\"${value.escapeForToon()}\""
    }

/**
 * Encodes a key, adding quotes if necessary.
 */
public fun encodeKey(key: String): String =
    if (key.isValidUnquotedKey()) {
        key
    } else {
        "\"${key.escapeForToon()}\""
    }

/**
 * Encodes and joins multiple primitive values.
 */
public fun encodeAndJoinPrimitives(
    values: List<JsonPrimitive>,
    delimiter: Delimiter = Delimiter.DEFAULT
): String = values.joinToString(delimiter.char.toString()) { encodePrimitive(it, delimiter.char) }

/**
 * Formats an array header.
 */
public fun formatHeader(
    length: Int,
    key: String? = null,
    fields: List<String>? = null,
    delimiter: Delimiter = Delimiter.DEFAULT,
    lengthMarker: Boolean = false
): String = buildString {
    if (key != null) {
        append(encodeKey(key))
    }

    append('[')
    if (lengthMarker) append(ToonConstants.HASH)
    append(length)
    if (delimiter != Delimiter.DEFAULT) append(delimiter.char)
    append(']')

    if (fields != null) {
        val quotedFields = fields.joinToString(delimiter.char.toString()) { encodeKey(it) }
        append('{').append(quotedFields).append('}')
    }

    append(ToonConstants.COLON)
}

