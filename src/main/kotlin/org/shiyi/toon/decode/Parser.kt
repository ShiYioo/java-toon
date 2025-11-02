package org.shiyi.toon.decode

import org.shiyi.toon.*
import org.shiyi.toon.utils.*

/**
 * 解析数组头部行
 */
public fun parseArrayHeaderLine(
    content: String,
    defaultDelimiter: Delimiter
): Pair<ArrayHeaderInfo, String?>? {
    // 不匹配以引号开始的行（这是带引号的键，不是数组）
    if (content.trimStart().startsWith(ToonConstants.DOUBLE_QUOTE)) {
        return null
    }

    // 查找方括号段
    val bracketStart = content.indexOf(ToonConstants.OPEN_BRACKET)
    if (bracketStart == -1) return null

    val bracketEnd = content.indexOf(ToonConstants.CLOSE_BRACKET, bracketStart)
    if (bracketEnd == -1) return null

    // 查找冒号
    var colonIndex = bracketEnd + 1
    var braceEnd = colonIndex

    // 检查字段段（花括号在方括号之后）
    val braceStart = content.indexOf(ToonConstants.OPEN_BRACE, bracketEnd)
    if (braceStart != -1) {
        val colonPos = content.indexOf(ToonConstants.COLON, bracketEnd)
        if (colonPos == -1 || braceStart < colonPos) {
            val foundBraceEnd = content.indexOf(ToonConstants.CLOSE_BRACE, braceStart)
            if (foundBraceEnd != -1) {
                braceEnd = foundBraceEnd + 1
            }
        }
    }

    // 在方括号和花括号之后查找冒号
    colonIndex = content.indexOf(ToonConstants.COLON, maxOf(bracketEnd, braceEnd))
    if (colonIndex == -1) return null

    val key = if (bracketStart > 0) content.take(bracketStart) else null
    val afterColon = content.substring(colonIndex + 1).trim()

    val bracketContent = content.substring(bracketStart + 1, bracketEnd)

    // 尝试解析方括号段
    val (length, delimiter, hasLengthMarker) = try {
        parseBracketSegment(bracketContent, defaultDelimiter)
    } catch (e: Exception) {
        return null
    }

    // 检查字段段
    var fields: List<String>? = null
    if (braceStart != -1 && braceStart < colonIndex) {
        val foundBraceEnd = content.indexOf(ToonConstants.CLOSE_BRACE, braceStart)
        if (foundBraceEnd != -1 && foundBraceEnd < colonIndex) {
            val fieldsContent = content.substring(braceStart + 1, foundBraceEnd)
            fields = parseDelimitedValues(fieldsContent, delimiter)
                .map { parseStringLiteral(it.trim()) }
        }
    }

    val header = ArrayHeaderInfo(
        key = key,
        length = length,
        delimiter = delimiter,
        fields = fields,
        hasLengthMarker = hasLengthMarker
    )

    return Pair(header, afterColon.ifEmpty { null })
}

/**
 * 解析方括号段
 */
private fun parseBracketSegment(
    seg: String,
    defaultDelimiter: Delimiter
): Triple<Int, Delimiter, Boolean> {
    var hasLengthMarker = false
    var content = seg

    // 检查长度标记
    if (content.startsWith(ToonConstants.HASH)) {
        hasLengthMarker = true
        content = content.substring(1)
    }

    // 检查分隔符后缀
    var delimiter = defaultDelimiter
    when {
        content.endsWith(ToonConstants.TAB) -> {
            delimiter = Delimiter.TAB
            content = content.dropLast(1)
        }
        content.endsWith(ToonConstants.PIPE) -> {
            delimiter = Delimiter.PIPE
            content = content.dropLast(1)
        }
        content.endsWith(ToonConstants.COMMA) -> {
            delimiter = Delimiter.COMMA
            content = content.dropLast(1)
        }
    }

    val length = content.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid array length: $content")

    require(length >= 0) { "Array length cannot be negative: $length" }

    return Triple(length, delimiter, hasLengthMarker)
}

/**
 * 解析分隔值
 */
public fun parseDelimitedValues(content: String, delimiter: Delimiter): List<String> {
    if (content.isEmpty()) return emptyList()

    val result = mutableListOf<String>()
    val delimiterChar = delimiter.char
    val current = StringBuilder()
    var i = 0

    while (i < content.length) {
        when {
            content[i] == ToonConstants.DOUBLE_QUOTE -> {
                // 处理带引号的字符串
                val closingQuote = content.findClosingQuote(i)
                if (closingQuote == -1) {
                    throw IllegalArgumentException("Unclosed quote in delimited values")
                }
                current.append(content.substring(i, closingQuote + 1))
                i = closingQuote + 1
            }
            content[i] == delimiterChar -> {
                // 找到分隔符
                result.add(current.toString())
                current.clear()
                i++
            }
            else -> {
                current.append(content[i])
                i++
            }
        }
    }

    // 添加最后一个值
    result.add(current.toString())
    return result
}

/**
 * 解析键令牌
 */
public fun parseKeyToken(content: String, start: Int = 0): Pair<String, Int> {
    if (start < content.length && content[start] == ToonConstants.DOUBLE_QUOTE) {
        // 带引号的键
        val closingQuote = content.findClosingQuote(start)
        if (closingQuote == -1) {
            throw IllegalArgumentException("Unclosed quote in key")
        }

        val quotedContent = content.substring(start + 1, closingQuote)
        val key = quotedContent.unescapeFromToon()

        // 查找引号后的冒号
        var end = closingQuote + 1
        while (end < content.length && content[end] != ToonConstants.COLON) {
            end++
        }
        if (end >= content.length) {
            throw IllegalArgumentException("No colon found after key")
        }

        return Pair(key, end + 1) // 返回冒号后的位置
    } else {
        // 不带引号的键 - 查找第一个不在引号内的冒号
        val colonIndex = content.findUnquotedChar(ToonConstants.COLON, start)
        if (colonIndex == -1) {
            throw IllegalArgumentException("No colon found in key-value pair")
        }

        val key = content.substring(start, colonIndex).trim()
        return Pair(key, colonIndex + 1) // 返回冒号后的位置
    }
}

/**
 * 解析原始令牌
 */
public fun parsePrimitiveToken(value: String): JsonPrimitive {
    val trimmed = value.trim()

    // 检查 null
    if (trimmed == ToonConstants.NULL_LITERAL) {
        return null
    }

    // 检查布尔值
    if (trimmed == ToonConstants.TRUE_LITERAL) {
        return true
    }
    if (trimmed == ToonConstants.FALSE_LITERAL) {
        return false
    }

    // 检查带引号的字符串
    if (trimmed.startsWith(ToonConstants.DOUBLE_QUOTE)) {
        return parseStringLiteral(trimmed)
    }

    // 尝试解析为数字
    if (trimmed.isNumericLiteral()) {
        return parseNumericValue(trimmed)
    }

    // 不带引号的字符串
    return trimmed
}

/**
 * 解析数字值
 *
 */
private fun parseNumericValue(value: String): JsonPrimitive {
    // 检查是否包含小数点或科学计数法标记
    val hasDecimalPoint = value.contains('.')
    val hasExponent = value.contains('e', ignoreCase = true)

    return when {
        // 包含小数点或指数，解析为 Double
        hasDecimalPoint || hasExponent -> {
            value.toDoubleOrNull() ?: value
        }
        // 整数格式，优先解析为 Int，超出范围则用 Long
        else -> {
            value.toIntOrNull()
                ?: value.toLongOrNull()
                ?: value.toDoubleOrNull()
                ?: value
        }
    }
}

/**
 * 解析字符串字面量
 */
public fun parseStringLiteral(value: String): String {
    val trimmed = value.trim()

    if (!trimmed.startsWith(ToonConstants.DOUBLE_QUOTE)) {
        return trimmed
    }

    val closingQuote = trimmed.findClosingQuote(0)
    if (closingQuote == -1 || closingQuote != trimmed.length - 1) {
        throw IllegalArgumentException("Invalid quoted string: $value")
    }

    val quotedContent = trimmed.substring(1, closingQuote)
    return quotedContent.unescapeFromToon()
}

/**
 * 检查内容是否为连字符后的数组头部
 */
public fun isArrayHeaderAfterHyphen(content: String): Boolean =
    content.contains(ToonConstants.OPEN_BRACKET)

/**
 * 检查内容是否为连字符后的对象第一个字段
 */
public fun isObjectFirstFieldAfterHyphen(content: String): Boolean =
    content.contains(ToonConstants.COLON)

/**
 * 将行值映射为原始值
 */
public fun mapRowValuesToPrimitives(values: List<String>): List<JsonPrimitive> =
    values.map { parsePrimitiveToken(it) }

