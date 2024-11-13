package org.example;

import java.util.ArrayList;
import java.util.List;

public class SinAnalyzer {
    private final List<Token> tokens;
    private int currentIndex = 0;
    private final List<String> errors = new ArrayList<>();

    public SinAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token currentToken() {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex);
        } else {
            return new Token(TokenType.UNKNOWN, "");
        }
    }

    private void nextToken() {
        if (currentIndex < tokens.size() - 1) {
            currentIndex++;
        }
    }

    private boolean match(TokenType expectedType) {
        if (currentToken().type == expectedType) {
            nextToken();
            return true;
        }
        return false;
    }

    public void parse() {
        parseIfStatement();

        if (currentToken().type != TokenType.UNKNOWN) {
            errors.add("Unexpected token at end: " + currentToken().value);
        }

        if (errors.isEmpty()) {
            System.out.println("Парсинг успешно завершен.");
        } else {
            System.out.println("Ошибки парсинга:");
            for (String error : errors) {
                System.out.println(error);
            }
        }
    }

    private void parseIfStatement() {
        if (!match(TokenType.IF)) {
            errors.add("Expected 'if' at the beginning.");
            return;
        }

        parseLogicalExpression();

        if (!match(TokenType.THEN)) {
            errors.add("Expected 'then' after 'if' condition.");
            return;
        }

        parseOperators();

        while (match(TokenType.ELSEIF)) {
            parseLogicalExpression();
            if (!match(TokenType.THEN)) {
                errors.add("Expected 'then' after 'elseif' condition.");
                return;
            }
            parseOperators();
        }

        if (match(TokenType.ELSE)) {
            parseOperators();
        }

        if (!match(TokenType.END)) {
            errors.add("Expected 'end' to close 'if' statement.");
        }
    }

    private void parseLogicalExpression() {
        parseComparisonExpression();

        while (currentToken().type == TokenType.AND || currentToken().type == TokenType.OR) {
            nextToken();
            parseComparisonExpression();
        }
    }

    private void parseComparisonExpression() {
        parseArithmeticExpression();

        if (currentToken().type == TokenType.COMPARISON_OPERATOR) {
            nextToken();
            parseArithmeticExpression();
        } else {
            errors.add("Expected comparison operator between operands.");
        }
    }

    private void parseArithmeticExpression() {
        parseTerm();

        while (currentToken().type == TokenType.ARITHMETIC_OPERATION_LOW_PRIORITY) {
            nextToken();
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();

        while (currentToken().type == TokenType.ARITHMETIC_OPERATION_HIGH_PRIORITY) {
            nextToken();
            parseFactor();
        }
    }

    private void parseFactor() {
        if (match(TokenType.IDENTIFIER) || match(TokenType.CONSTANT)) {
            return;
        } else if (match(TokenType.NOT)) {  // Обработка унарного логического оператора
            parseFactor();
        } else {
            errors.add("Expected operand (identifier, constant, or unary logical operator 'not'). Found: " + currentToken().value);
        }
    }

    private void parseOperators() {
        while (currentToken().type == TokenType.OUTPUT) {
            nextToken();
            if (!match(TokenType.IDENTIFIER) && !match(TokenType.CONSTANT)) {
                errors.add("Expected identifier or constant after 'output'. Found: " + currentToken().value);
                return;
            }
        }
    }
}