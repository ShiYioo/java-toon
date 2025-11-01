package org.shiyi.toon.decode

import org.shiyi.toon.*
import org.shiyi.toon.utils.findClosingQuote

/**
 * 主解码函数
 */
public fun decode(input: String, options: DecodeOptions): JsonValue {
    val scanResult = scanLines(input, options.indent, options.strict)

    if (scanResult.lines.isEmpty()) {
        throw IllegalArgumentException("Cannot decode empty input: input must be a non-empty string")
    }

    val cursor = LineCursor(scanResult.lines, scanResult.blankLines)
    return decodeValue(cursor, options)
}

/**
 * 解码值
 */
private fun decodeValue(cursor: LineCursor, options: DecodeOptions): JsonValue {
    val first = cursor.peek() ?: throw IllegalArgumentException("No content to decode")

    // 检查根级数组
    if (isArrayHeaderAfterHyphen(first.content)) {
        val result = parseArrayHeaderLine(first.content, Delimiter.DEFAULT)
        if (result != null) {
            val (headerInfo, inlineValues) = result
            cursor.advance()
            return decodeArrayFromHeader(headerInfo, inlineValues, cursor, Depth.ZERO, options)
        }
    }

    // 检查单个原始值
    if (cursor.length == 1 && !isKeyValueLine(first)) {
        return parsePrimitiveToken(first.content.trim())
    }

    // 默认为对象
    return decodeObject(cursor, Depth.ZERO, options)
}

/**
 * 检查是否为键值行
 */
private fun isKeyValueLine(line: ParsedLine): Boolean {
    val content = line.content

    if (content.startsWith(ToonConstants.DOUBLE_QUOTE)) {
        val closingQuoteIndex = content.findClosingQuote(0)
        if (closingQuoteIndex == -1) return false
        return closingQuoteIndex + 1 < content.length &&
               content[closingQuoteIndex + 1] == ToonConstants.COLON
    }

    return content.contains(ToonConstants.COLON)
}

/**
 * 解码对象
 */
private fun decodeObject(
    cursor: LineCursor,
    baseDepth: Depth,
    options: DecodeOptions
): Map<String, Any?> {
    val obj = mutableMapOf<String, Any?>()

    while (!cursor.atEnd()) {
        val line = cursor.peek()
        if (line == null || line.depth < baseDepth) break

        if (line.depth == baseDepth) {
            val (key, value) = decodeKeyValuePair(line, cursor, baseDepth, options)
            obj[key] = value
        } else {
            break
        }
    }

    return obj
}

/**
 * 解码键值对
 */
private fun decodeKeyValuePair(
    line: ParsedLine,
    cursor: LineCursor,
    baseDepth: Depth,
    options: DecodeOptions
): Pair<String, Any?> {
    cursor.advance()
    return decodeKeyValue(line.content, cursor, baseDepth, options)
}

/**
 * 解码键和值
 */
private fun decodeKeyValue(
    content: String,
    cursor: LineCursor,
    baseDepth: Depth,
    options: DecodeOptions
): Pair<String, Any?> {
    // 检查数组头部
    val arrayHeader = parseArrayHeaderLine(content, Delimiter.DEFAULT)
    if (arrayHeader != null && arrayHeader.first.key != null) {
        val (header, inlineValues) = arrayHeader
        val value = decodeArrayFromHeader(header, inlineValues, cursor, baseDepth, options)
        return Pair(header.key!!, value)
    }

    // 常规键值对
    val (key, end) = parseKeyToken(content, 0)
    val rest = content.substring(end).trim()

    // 冒号后无值 - 期望嵌套对象或空对象
    if (rest.isEmpty()) {
        val nextLine = cursor.peek()
        if (nextLine != null && nextLine.depth > baseDepth) {
            val nested = decodeObject(cursor, baseDepth + 1, options)
            return Pair(key, nested)
        }
        // 空对象
        return Pair(key, emptyMap<String, Any?>())
    }

    // 内联原始值
    val value = parsePrimitiveToken(rest)
    return Pair(key, value)
}

/**
 * 从头部信息解码数组
 */
private fun decodeArrayFromHeader(
    header: ArrayHeaderInfo,
    inlineValues: String?,
    cursor: LineCursor,
    baseDepth: Depth,
    options: DecodeOptions
): List<Any?> {
    // 内联原始数组
    if (inlineValues != null) {
        return decodeInlinePrimitiveArray(header, inlineValues, options)
    }

    // 表格数组
    if (header.fields != null && header.fields.isNotEmpty()) {
        return decodeTabularArray(header, cursor, baseDepth, options)
    }

    // 列表数组
    return decodeListArray(header, cursor, baseDepth, options)
}

/**
 * 解码内联原始数组
 */
private fun decodeInlinePrimitiveArray(
    header: ArrayHeaderInfo,
    inlineValues: String,
    options: DecodeOptions
): List<JsonPrimitive> {
    if (inlineValues.trim().isEmpty()) {
        if (options.strict && header.length != 0) {
            throw IllegalArgumentException("Expected ${header.length} inline array items, but found 0")
        }
        return emptyList()
    }

    val values = parseDelimitedValues(inlineValues, header.delimiter)
    val primitives = mapRowValuesToPrimitives(values)

    if (options.strict && primitives.size != header.length) {
        throw IllegalArgumentException(
            "Expected ${header.length} inline array items, but found ${primitives.size}"
        )
    }

    return primitives
}

/**
 * 解码列表数组
 */
private fun decodeListArray(
    header: ArrayHeaderInfo,
    cursor: LineCursor,
    baseDepth: Depth,
    options: DecodeOptions
): List<Any?> {
    val items = mutableListOf<Any?>()
    val itemDepth = baseDepth + 1

    while (!cursor.atEnd() && items.size < header.length) {
        val line = cursor.peek()
        if (line == null || line.depth < itemDepth) break

        if (line.depth == itemDepth && line.content.startsWith(ToonConstants.LIST_ITEM_PREFIX)) {
            val item = decodeListItem(cursor, itemDepth, header.delimiter, options)
            items.add(item)
        } else {
            break
        }
    }

    if (options.strict && items.size != header.length) {
        throw IllegalArgumentException(
            "Expected ${header.length} list array items, but found ${items.size}"
        )
    }

    return items
}

/**
 * 解码列表项
 */
private fun decodeListItem(
    cursor: LineCursor,
    itemDepth: Depth,
    delimiter: Delimiter,
    options: DecodeOptions
): Any? {
    val line = cursor.next() ?: throw IllegalArgumentException("Expected list item")
    val content = line.content.substring(ToonConstants.LIST_ITEM_PREFIX.length).trim()

    // 检查是否为数组头部
    val arrayHeader = parseArrayHeaderLine(content, delimiter)
    if (arrayHeader != null) {
        val (header, inlineValues) = arrayHeader
        return decodeArrayFromHeader(header, inlineValues, cursor, itemDepth, options)
    }

    // 检查是否为键值（对象）
    if (isObjectFirstFieldAfterHyphen(content)) {
        val (key, value) = decodeKeyValue(content, cursor, itemDepth, options)
        val obj = mutableMapOf(key to value)

        // 检查深度 + 1 的附加字段
        while (!cursor.atEnd()) {
            val nextLine = cursor.peek()
            if (nextLine == null || nextLine.depth < itemDepth + 1) break

            if (nextLine.depth == itemDepth + 1) {
                val (k, v) = decodeKeyValuePair(nextLine, cursor, itemDepth + 1, options)
                obj[k] = v
            } else {
                break
            }
        }

        return obj
    }

    // 原始值
    return parsePrimitiveToken(content)
}

/**
 * 解码表格数组
 */
private fun decodeTabularArray(
    header: ArrayHeaderInfo,
    cursor: LineCursor,
    baseDepth: Depth,
    options: DecodeOptions
): List<Map<String, Any?>> {
    val objects = mutableListOf<Map<String, Any?>>()
    val rowDepth = baseDepth + 1

    while (!cursor.atEnd() && objects.size < header.length) {
        val line = cursor.peek()
        if (line == null || line.depth < rowDepth) break

        if (line.depth == rowDepth) {
            cursor.advance()
            val values = parseDelimitedValues(line.content, header.delimiter)

            if (options.strict && values.size != header.fields!!.size) {
                throw IllegalArgumentException(
                    "Expected ${header.fields.size} tabular row values, but found ${values.size}"
                )
            }

            val primitives = mapRowValuesToPrimitives(values)
            val obj = mutableMapOf<String, Any?>()

            for (i in header.fields!!.indices) {
                obj[header.fields[i]] = primitives.getOrNull(i)
            }

            objects.add(obj)
        } else {
            break
        }
    }

    if (options.strict && objects.size != header.length) {
        throw IllegalArgumentException(
            "Expected ${header.length} tabular rows, but found ${objects.size}"
        )
    }

    return objects
}

