package org.example;

public enum TokenType {
    IF, THEN, ELSE, ELSEIF, END, OUTPUT, NOT, AND, OR,
    IDENTIFIER, CONSTANT,
    COMPARISON_OPERATOR, ARITHMETIC_OPERATION_LOW_PRIORITY, ARITHMETIC_OPERATION_HIGH_PRIORITY,
    UNKNOWN
}
