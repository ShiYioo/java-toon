package org.shiyi.toon.utils

import org.shiyi.toon.ToonConstants

/**
 * String utility extensions.
 * 使用扩展函数而不是工具类，这是 Kotlin 的最佳实践
 */

/**
 * Escapes special characters in a string.
 */
public fun String.escapeForToon(): String = buildString(length + 10) {
    for (char in this@escapeForToon) {
        when (char) {
            ToonConstants.BACKSLASH -> append("\\\\")
            ToonConstants.DOUBLE_QUOTE -> append("\\\"")
            ToonConstants.NEWLINE -> append("\\n")
            ToonConstants.CARRIAGE_RETURN -> append("\\r")
            ToonConstants.TAB -> append("\\t")
            else -> append(char)
        }
    }
}

/**
 * Unescapes a string by processing escape sequences.
 */
public fun String.unescapeFromToon(): String = buildString(length) {
    var i = 0
    while (i < this@unescapeFromToon.length) {
        if (this@unescapeFromToon[i] == ToonConstants.BACKSLASH && i + 1 < this@unescapeFromToon.length) {
            when (this@unescapeFromToon[i + 1]) {
                'n' -> append(ToonConstants.NEWLINE)
                't' -> append(ToonConstants.TAB)
                'r' -> append(ToonConstants.CARRIAGE_RETURN)
                ToonConstants.BACKSLASH -> append(ToonConstants.BACKSLASH)
                ToonConstants.DOUBLE_QUOTE -> append(ToonConstants.DOUBLE_QUOTE)
                else -> throw IllegalArgumentException("Invalid escape sequence: \\${this@unescapeFromToon[i + 1]}")
            }
            i += 2
        } else {
            append(this@unescapeFromToon[i])
            i++
        }
    }
}

/**
 * Finds the index of the closing double quote.
 */
public fun String.findClosingQuote(start: Int): Int {
    var i = start + 1
    while (i < length) {
        if (this[i] == ToonConstants.BACKSLASH && i + 1 < length) {
            i += 2 // Skip escaped character
            continue
        }
        if (this[i] == ToonConstants.DOUBLE_QUOTE) {
            return i
        }
        i++
    }
    return -1
}

/**
 * Finds the index of a character outside of quoted sections.
 */
public fun String.findUnquotedChar(char: Char, start: Int = 0): Int {
    var i = start
    while (i < length) {
        if (this[i] == ToonConstants.DOUBLE_QUOTE) {
            val closingQuote = findClosingQuote(i)
            if (closingQuote == -1) return -1
            i = closingQuote + 1
            continue
        }
        if (this[i] == char) return i
        i++
    }
    return -1
}

