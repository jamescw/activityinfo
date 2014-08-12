package org.activityinfo.core.shared.expr;

public enum TokenType {


    /**
     * "("
     */
    PAREN_START,

    /**
     * ")"
     */
    PAREN_END,

    /**
     * "{"
     */
    BRACE_START,

    /**
     * "}"
     */
    BRACE_END,

    /**
     * """
     */
    STRING_START,

    /**
     * """
     */
    STRING_END,

    /**
     * Operator : +, /, -, &&, || etc
     */
    OPERATOR,


    /**
     * A named value
     */
    SYMBOL,

    FUNCTION,

    /**
     * Numeric Literal: 1, 3 ...
     */
    NUMBER,

    /**
     * Boolean literal : true, false
     */
    BOOLEAN_LITERAL,

    STRING_LITERAL,

    /**
     * Whitespace
     */
    WHITESPACE,

    EOF


}
