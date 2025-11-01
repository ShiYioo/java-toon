package org.shiyi.toon.encode

import org.shiyi.toon.Depth

/**
 * Line writer for building TOON output.
 * 使用 class 而不是 object，支持实例化和状态管理
 */
public class LineWriter(private val indentSize: Int) {
    private val lines = mutableListOf<String>()

    /**
     * Pushes a line at the specified depth.
     */
    public fun push(depth: Depth, content: String) {
        val indent = " ".repeat(depth.value * indentSize)
        lines.add("$indent$content")
    }

    /**
     * Pushes a list item line at the specified depth.
     */
    public fun pushListItem(depth: Depth, content: String) {
        val indent = " ".repeat(depth.value * indentSize)
        lines.add("$indent- $content")
    }

    /**
     * Converts all lines to a single string.
     */
    override fun toString(): String = lines.joinToString("\n")
}

