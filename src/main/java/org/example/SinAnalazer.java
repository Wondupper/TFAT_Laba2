package org.example;

import java.util.List;

public class SinAnalazer {
    private List<Token> tokens;
    private int currentTokenIndex = 0;

    public SinAnalazer(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Получение текущего токена
    private Token currentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null;
    }

    // Переход к следующему токену
    private void advance() {
        currentTokenIndex++;
    }

    // Синтаксический анализатор для конструкции if-then-elseif-else-end
    public boolean parse() {
        if (ifStatement()) {
            System.out.println("Parsing successful!");
            return true;
        } else {
            Token token = currentToken();
            if (token != null) {
                System.out.println("Parsing failed at token " + token + ". Expected something else.");
            } else {
                System.out.println("Parsing failed at the end of input. Expected additional tokens.");
            }
            return false;
        }
    }

    // Анализатор для выражения if ... then ... elseif ... else ... end
    private boolean ifStatement() {
        if (match(TokenType.IF)) {
            if (!expression()) {
                error("Expected a logical expression after 'if'");
                return false;
            }
            if (!match(TokenType.THEN)) {
                error("Expected 'then' after logical expression");
                return false;
            }
            if (!statements()) {
                error("Expected statements after 'then'");
                return false;
            }

            while (match(TokenType.ELSEIF)) {
                if (!expression()) {
                    error("Expected a logical expression after 'elseif'");
                    return false;
                }
                if (!match(TokenType.THEN)) {
                    error("Expected 'then' after 'elseif' expression");
                    return false;
                }
                if (!statements()) {
                    error("Expected statements after 'elseif then'");
                    return false;
                }
            }

            if (match(TokenType.ELSE)) {
                if (!statements()) {
                    error("Expected statements after 'else'");
                    return false;
                }
            }

            if (!match(TokenType.END)) {
                error("Expected 'end' at the end of the if statement");
                return false;
            }
            return true;
        }
        error("Expected 'if' at the beginning of the statement");
        return false;
    }

    // Анализ логического выражения (упрощенный)
    private boolean expression() {
        return match(TokenType.IDENTIFIER) || match(TokenType.CONSTANT);
    }

    // Анализатор списка операторов
    private boolean statements() {
        if (match(TokenType.OUTPUT)) {
            return match(TokenType.IDENTIFIER) || match(TokenType.CONSTANT);
        }
        return false;
    }

    // Проверка и переход к следующему токену, если совпадает
    private boolean match(TokenType type) {
        if (currentToken() != null && currentToken().type == type) {
            advance();
            return true;
        }
        return false;
    }

    // Вывод сообщения об ошибке
    private void error(String message) {
        Token token = currentToken();
        if (token != null) {
            System.out.println("Error at token " + token + ": " + message);
        } else {
            System.out.println("Error at end of input: " + message);
        }
    }
}
