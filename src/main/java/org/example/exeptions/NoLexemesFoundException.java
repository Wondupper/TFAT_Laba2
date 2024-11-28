package org.example.exeptions;

public class NoLexemesFoundException extends RuntimeException {
    public NoLexemesFoundException(String message) {
        super(message);
    }
}
