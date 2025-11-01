package org.shiyi.toon

/**
 * TOON format constants.
 * 符合 Kotlin 命名规范，使用 const val 定义编译时常量
 */
public object ToonConstants {
    // List markers
    public const val LIST_ITEM_MARKER: Char = '-'
    public const val LIST_ITEM_PREFIX: String = "- "

    // Structural characters
    public const val COMMA: Char = ','
    public const val COLON: Char = ':'
    public const val SPACE: Char = ' '
    public const val PIPE: Char = '|'
    public const val HASH: Char = '#'

    // Brackets and braces
    public const val OPEN_BRACKET: Char = '['
    public const val CLOSE_BRACKET: Char = ']'
    public const val OPEN_BRACE: Char = '{'
    public const val CLOSE_BRACE: Char = '}'

    // Literals
    public const val NULL_LITERAL: String = "null"
    public const val TRUE_LITERAL: String = "true"
    public const val FALSE_LITERAL: String = "false"

    // Escape characters
    public const val BACKSLASH: Char = '\\'
    public const val DOUBLE_QUOTE: Char = '"'
    public const val NEWLINE: Char = '\n'
    public const val CARRIAGE_RETURN: Char = '\r'
    public const val TAB: Char = '\t'
}

/**
 * Delimiter enum for array formatting.
 * 使用 enum class 代替简单常量，提供类型安全
 */
public enum class Delimiter(public val char: Char) {
    COMMA(','),
    TAB('\t'),
    PIPE('|');

    public companion object {
        public val DEFAULT: Delimiter = COMMA

        public fun fromChar(char: Char): Delimiter? = entries.find { it.char == char }
    }
}

