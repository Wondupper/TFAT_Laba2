package org.example;

public class Token {
    TokenType type;
    String value;
    int position;

    Token(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Type: " +type + " Value: " + value +" Position: " +  position;
    }
}
