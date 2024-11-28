package org.example.exeptions;

public class LexicalAnalysisException extends RuntimeException {
    public LexicalAnalysisException(String s, IllegalArgumentException ex) {
        super(s, ex);
    }
}
