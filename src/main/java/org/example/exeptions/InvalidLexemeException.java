package org.example.exeptions;

import org.example.core.LexemeType;

public class InvalidLexemeException extends RuntimeException {
    public InvalidLexemeException(LexemeType lexemeType, int i) {
    }
}
