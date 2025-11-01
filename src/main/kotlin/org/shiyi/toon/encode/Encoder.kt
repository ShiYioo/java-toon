package org.shiyi.toon.encode

import org.shiyi.toon.*

/**
 * 主编码器函数 - 将值编码为 TOON 格式
 */
public fun encode(value: Any?, options: EncodeOptions): String {
    // 处理原始类型
    if (value == null || value is Boolean || value is Number || value is String) {
        return encodePrimitive(value, options.delimiter.char)
    }

    val writer = LineWriter(options.indent)

    when (value) {
        is List<*> -> encodeArray(null, value, writer, Depth.ZERO, options)
        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            encodeObject(value as Map<String, Any?>, writer, Depth.ZERO, options)
        }

        else -> throw IllegalArgumentException("Unsupported value type: ${value::class}")
    }

    return writer.toString()
}

/**
 * 编码对象
 */
private fun encodeObject(
    obj: Map<String, Any?>,
    writer: LineWriter,
    depth: Depth,
    options: EncodeOptions
) {
    for ((key, value) in obj) {
        encodeKeyValuePair(key, value, writer, depth, options)
    }
}

/**
 * 编码键值对
 */
private fun encodeKeyValuePair(
    key: String,
    value: Any?,
    writer: LineWriter,
    depth: Depth,
    options: EncodeOptions
) {
    val encodedKey = encodeKey(key)

    when (value) {
        null, is Boolean, is Number, is String -> {
            writer.push(depth, "$encodedKey: ${encodePrimitive(value, options.delimiter.char)}")
        }

        is List<*> -> {
            encodeArray(key, value, writer, depth, options)
        }

        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            val nestedObj = value as Map<String, Any?>
            if (nestedObj.isEmpty()) {
                writer.push(depth, "$encodedKey:")
            } else {
                writer.push(depth, "$encodedKey:")
                encodeObject(nestedObj, writer, depth + 1, options)
            }
        }
    }
}

/**
 * 编码数组
 */
private fun encodeArray(
    key: String?,
    array: List<*>,
    writer: LineWriter,
    depth: Depth,
    options: EncodeOptions
) {
    if (array.isEmpty()) {
        val header = formatHeader(0, key, null, options.delimiter, options.lengthMarker)
        writer.push(depth, header)
        return
    }

    // 检测是否为原始类型数组
    if (isArrayOfPrimitives(array)) {
        val formatted = encodeInlineArrayLine(array, options.delimiter, key, options.lengthMarker)
        writer.push(depth, formatted)
        return
    }

    // 检测是否为对象数组（表格格式）
    if (isArrayOfObjects(array)) {
        val fields = extractTabularHeader(array)
        if (fields != null) {
            encodeArrayOfObjectsAsTabular(key, array, fields, writer, depth, options)
            return
        }
    }

    // 默认：列表格式
    encodeMixedArrayAsListItems(key, array, writer, depth, options)
}

/**
 * 检测是否为原始类型数组
 */
private fun isArrayOfPrimitives(array: List<*>): Boolean =
    array.all { it == null || it is Boolean || it is Number || it is String }

/**
 * 检测是否为对象数组
 */
private fun isArrayOfObjects(array: List<*>): Boolean =
    array.all { it is Map<*, *> }

/**
 * 提取表格头部（字段名）
 */
private fun extractTabularHeader(array: List<*>): List<String>? {
    if (array.isEmpty()) return null

    val first = array[0] as? Map<*, *> ?: return null
    val fields = first.keys.filterIsInstance<String>()

    // 检查所有对象是否有相同的字段，并且所有字段值都是原始类型
    val allSameFields = array.all { obj ->
        if (obj !is Map<*, *>) return@all false
        val objKeys = obj.keys.filterIsInstance<String>().toSet()
        if (objKeys != fields.toSet()) return@all false

        // 检查所有字段值是否都是原始类型
        fields.all { field ->
            val value = obj[field]
            value == null || value is Boolean || value is Number || value is String
        }
    }

    return if (allSameFields) fields else null
}

/**
 * 编码内联数组行
 */
private fun encodeInlineArrayLine(
    values: List<*>,
    delimiter: Delimiter,
    prefix: String?,
    lengthMarker: Boolean
): String {
    val header = formatHeader(values.size, prefix, null, delimiter, lengthMarker)
    if (values.isEmpty()) return header

    @Suppress("UNCHECKED_CAST")
    val primitives = values
    val joinedValue = encodeAndJoinPrimitives(primitives, delimiter)
    return "$header $joinedValue"
}

/**
 * 编码对象数组为表格格式
 */
private fun encodeArrayOfObjectsAsTabular(
    prefix: String?,
    rows: List<*>,
    fields: List<String>,
    writer: LineWriter,
    depth: Depth,
    options: EncodeOptions
) {
    val header = formatHeader(rows.size, prefix, fields, options.delimiter, options.lengthMarker)
    writer.push(depth, header)

    for (row in rows) {
        @Suppress("UNCHECKED_CAST")
        val obj = row as Map<String, Any?>
        val values = fields.map { obj[it] }
        val encoded = encodeAndJoinPrimitives(values, options.delimiter)
        writer.push(depth + 1, encoded)
    }
}

/**
 * 编码混合数组为列表格式
 */
private fun encodeMixedArrayAsListItems(
    prefix: String?,
    values: List<*>,
    writer: LineWriter,
    depth: Depth,
    options: EncodeOptions
) {
    val header = formatHeader(values.size, prefix, null, options.delimiter, options.lengthMarker)
    writer.push(depth, header)

    for (value in values) {
        when (value) {
            null, is Boolean, is Number, is String -> {
                writer.pushListItem(depth + 1, encodePrimitive(value, options.delimiter.char))
            }

            is List<*> -> {
                if (isArrayOfPrimitives(value)) {
                    val inline = encodeInlineArrayLine(value, options.delimiter, null, options.lengthMarker)
                    writer.pushListItem(depth + 1, inline)
                } else {
                    // 嵌套的复杂数组，需要递归编码
                    writer.pushListItem(depth + 1, "")
                    encodeArray(null, value, writer, depth + 1, options)
                }
            }

            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                val obj = value as Map<String, Any?>
                encodeObjectAsListItem(obj, writer, depth + 1, options)
            }
        }
    }
}

/**
 * 编码对象为列表项
 */
private fun encodeObjectAsListItem(
    obj: Map<String, Any?>,
    writer: LineWriter,
    depth: Depth,
    options: EncodeOptions
) {
    val keys = obj.keys.toList()
    if (keys.isEmpty()) return

    // 第一个键值对作为列表项
    val firstKey = keys[0]
    val firstValue = obj[firstKey]
    val encodedKey = encodeKey(firstKey)

    when (firstValue) {
        null, is Boolean, is Number, is String -> {
            writer.pushListItem(depth, "$encodedKey: ${encodePrimitive(firstValue, options.delimiter.char)}")
        }
        is List<*> -> {
            // 检查是否可以使用内联或表格格式
            if (firstValue.isEmpty()) {
                val header = formatHeader(0, null, null, options.delimiter, options.lengthMarker)
                writer.pushListItem(depth, "$encodedKey$header")
            } else if (isArrayOfPrimitives(firstValue)) {
                // 原始类型数组 - 内联格式
                val inline = encodeInlineArrayLine(firstValue, options.delimiter, null, options.lengthMarker)
                writer.pushListItem(depth, "$encodedKey$inline")
            } else if (isArrayOfObjects(firstValue)) {
                // 对象数组 - 尝试表格格式
                val fields = extractTabularHeader(firstValue)
                if (fields != null) {
                    val header = formatHeader(firstValue.size, null, fields, options.delimiter, options.lengthMarker)
                    writer.pushListItem(depth, "$encodedKey$header")
                    // 编码表格行
                    for (row in firstValue) {
                        @Suppress("UNCHECKED_CAST")
                        val rowObj = row as Map<String, Any?>
                        val values = fields.map { rowObj[it] }
                        val encoded = encodeAndJoinPrimitives(values, options.delimiter)
                        writer.push(depth + 1, encoded)
                    }
                } else {
                    // 不能用表格格式，使用列表格式
                    writer.pushListItem(depth, "$encodedKey:")
                    encodeMixedArrayAsListItems(null, firstValue, writer, depth + 1, options)
                }
            } else {
                // 混合数组 - 列表格式
                writer.pushListItem(depth, "$encodedKey:")
                encodeMixedArrayAsListItems(null, firstValue, writer, depth + 1, options)
            }
        }
        is Map<*, *> -> {
            writer.pushListItem(depth, "$encodedKey:")
            @Suppress("UNCHECKED_CAST")
            encodeObject(firstValue as Map<String, Any?>, writer, depth + 1, options)
        }
    }

    // 其余键值对正常编码
    for (i in 1 until keys.size) {
        val key = keys[i]
        val value = obj[key]
        encodeKeyValuePair(key, value, writer, depth + 1, options)
    }
}

