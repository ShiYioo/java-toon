package org.shiyi.toon.utils

import org.shiyi.toon.ToonConstants

/**
 * Validation utility extensions.
 * 使用扩展函数提供类型安全的验证方法
 */

/**
 * Checks if a string is a boolean or null literal.
 */
public fun String.isBooleanOrNullLiteral(): Boolean =
    this == ToonConstants.TRUE_LITERAL ||
    this == ToonConstants.FALSE_LITERAL ||
    this == ToonConstants.NULL_LITERAL

/**
 * Checks if a string is a valid numeric literal.
 */
public fun String.isNumericLiteral(): Boolean {
    if (isEmpty()) return false
    return try {
        toDouble()
        true
    } catch (@Suppress("SwallowedException") e: NumberFormatException) {
        false
    }
}

/**
 * Checks if a string can be safely represented without quotes.
 *
 */
public fun String.isSafeUnquoted(delimiter: Char): Boolean {
    if (isEmpty()) return false

    // Check for leading/trailing whitespace
    if (this != trim()) return false

    // Check for reserved literals
    if (isBooleanOrNullLiteral() || isNumericLiteral()) return false

    // Check for special characters that require quoting
    return none { char ->
        char == ToonConstants.DOUBLE_QUOTE ||
        char == ToonConstants.COLON ||
        char == ToonConstants.NEWLINE ||
        char == ToonConstants.CARRIAGE_RETURN ||
        char == ToonConstants.TAB ||
        char == ToonConstants.BACKSLASH ||
        char == ToonConstants.OPEN_BRACKET ||
        char == ToonConstants.CLOSE_BRACKET ||
        char == ToonConstants.OPEN_BRACE ||
        char == ToonConstants.CLOSE_BRACE ||
        char == delimiter
    } && !startsWith(ToonConstants.LIST_ITEM_PREFIX) &&
      !startsWith(ToonConstants.HASH)
}

/**
 * Checks if a string is a valid unquoted key.
 */
public fun String.isValidUnquotedKey(): Boolean {
    if (isEmpty()) return false

    if (isBooleanOrNullLiteral() || isNumericLiteral()) return false

    return none { char ->
        char == ToonConstants.DOUBLE_QUOTE ||
        char == ToonConstants.COLON ||
        char == ToonConstants.NEWLINE ||
        char == ToonConstants.CARRIAGE_RETURN ||
        char == ToonConstants.TAB ||
        char == ToonConstants.BACKSLASH
    } && !startsWith(ToonConstants.LIST_ITEM_PREFIX) &&
      !startsWith(ToonConstants.OPEN_BRACKET) &&
      trim() == this
}

