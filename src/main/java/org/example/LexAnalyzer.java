package org.example;

import java.util.ArrayList;
import java.util.List;

public class LexAnalyzer {
    public static List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        TokenType currentState = null;
        int position = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            position = i;

            if (Character.isWhitespace(c)) {
                continue;
            } else if (Character.isLetter(c)) {
                currentToken.append(c);
                currentState = TokenType.IDENTIFIER;
            } else if (Character.isDigit(c)) {
                currentToken.append(c);
                currentState = TokenType.CONSTANT;
            } else if ("<>=!".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.COMPARISON_OPERATOR, String.valueOf(c), position));
            } else if ("+-*/".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c), position));
            }

            if (currentState != null) {
                if (currentState == TokenType.IDENTIFIER) {
                    tokens.add(createKeywordOrIdentifierToken(currentToken.toString(), position));
                } else if (currentState == TokenType.CONSTANT) {
                    tokens.add(new Token(TokenType.CONSTANT, currentToken.toString(), position));
                }
                currentToken.setLength(0);
                currentState = null;
            }
        }

        return tokens;
    }

    private static Token createKeywordOrIdentifierToken(String text, int position) {
        switch (text) {
            case "if": return new Token(TokenType.IF, text, position);
            case "then": return new Token(TokenType.THEN, text, position);
            case "else": return new Token(TokenType.ELSE, text, position);
            case "elseif": return new Token(TokenType.ELSEIF, text, position);
            case "end": return new Token(TokenType.END, text, position);
            case "output": return new Token(TokenType.OUTPUT, text, position);
            default: return new Token(TokenType.IDENTIFIER, text, position);
        }
    }
}
