package org.shiyi.toon.decode

import org.shiyi.toon.*

/**
 * 扫描结果数据类
 * 使用 data class 提供自动生成的 equals/hashCode/toString
 */
public data class ScanResult(
    val lines: List<ParsedLine>,
    val blankLines: List<BlankLineInfo>
)

/**
 * 行游标类 - 用于遍历解析后的行
 *
 * 使用 class 而不是 object，支持状态管理
 * 遵循 Kotlin 命名规范和属性访问模式
 */
public class LineCursor(
    private val lines: List<ParsedLine>,
    private val blankLines: List<BlankLineInfo> = emptyList()
) {
    private var index = 0

    /**
     * 获取空行列表
     */
    public fun getBlankLines(): List<BlankLineInfo> = blankLines

    /**
     * 查看当前行但不移动指针
     */
    public fun peek(): ParsedLine? = lines.getOrNull(index)

    /**
     * 获取当前行并移动指针
     */
    public fun next(): ParsedLine? = lines.getOrNull(index++)

    /**
     * 获取上一行（最近处理的行）
     */
    public fun current(): ParsedLine? = if (index > 0) lines.getOrNull(index - 1) else null

    /**
     * 前进指针
     */
    public fun advance() {
        index++
    }

    /**
     * 检查是否到达末尾
     */
    public fun atEnd(): Boolean = index >= lines.size

    /**
     * 获取总行数
     */
    public val length: Int get() = lines.size

    /**
     * 查看指定深度的行
     */
    public fun peekAtDepth(targetDepth: Depth): ParsedLine? {
        val line = peek()
        return if (line != null && line.depth == targetDepth) line else null
    }

    /**
     * 检查是否有更多指定深度的行
     */
    public fun hasMoreAtDepth(targetDepth: Depth): Boolean =
        peekAtDepth(targetDepth) != null
}

/**
 * 将源字符串扫描为解析后的行列表
 *
 * 使用顶层函数而不是类的静态方法，这是 Kotlin 最佳实践
 *
 * @param source 源字符串
 * @param indentSize 缩进大小（空格数）
 * @param strict 是否启用严格模式验证
 * @return 扫描结果，包含解析的行和空行信息
 * @throws IllegalArgumentException 在严格模式下，如果缩进不正确
 */
public fun scanLines(source: String, indentSize: Int, strict: Boolean): ScanResult {
    // 处理空输入
    if (source.trim().isEmpty()) {
        return ScanResult(emptyList(), emptyList())
    }

    val lines = source.lines()
    val parsed = mutableListOf<ParsedLine>()
    val blankLines = mutableListOf<BlankLineInfo>()

    lines.forEachIndexed { i, raw ->
        val lineNumber = i + 1

        // 计算前导空格数
        var indent = 0
        while (indent < raw.length && raw[indent] == ' ') {
            indent++
        }

        val content = raw.substring(indent)

        // 跟踪空行
        if (content.trim().isEmpty()) {
            val depth = computeDepthFromIndent(indent, indentSize)
            blankLines.add(
                BlankLineInfo(
                    lineNumber = lineNumber,
                    indent = indent,
                    depth = depth
                )
            )
            return@forEachIndexed
        }

        val depth = computeDepthFromIndent(indent, indentSize)

        // 严格模式验证
        if (strict) {
            // 查找完整的前导空白区域（空格和制表符）
            var wsEnd = 0
            while (wsEnd < raw.length && (raw[wsEnd] == ' ' || raw[wsEnd] == '\t')) {
                wsEnd++
            }

            // 检查前导空白中是否有制表符
            if (raw.substring(0, wsEnd).contains('\t')) {
                throw IllegalArgumentException(
                    "Line $lineNumber: Tabs are not allowed in indentation in strict mode"
                )
            }

            // 检查缩进是否为 indentSize 的精确倍数
            if (indent > 0 && indent % indentSize != 0) {
                throw IllegalArgumentException(
                    "Line $lineNumber: Indentation must be exact multiple of $indentSize, but found $indent spaces"
                )
            }
        }

        // 添加解析后的行
        parsed.add(
            ParsedLine(
                raw = raw,
                indent = indent,
                content = content,
                depth = depth,
                lineNumber = lineNumber
            )
        )
    }

    return ScanResult(parsed, blankLines)
}

/**
 * 从缩进空格数计算深度级别
 *
 * 使用 private 顶层函数作为内部辅助函数
 */
private fun computeDepthFromIndent(indentSpaces: Int, indentSize: Int): Depth =
    Depth(indentSpaces / indentSize)

