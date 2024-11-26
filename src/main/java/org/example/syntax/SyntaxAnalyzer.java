package org.example.syntax;

import lombok.RequiredArgsConstructor;
import org.example.core.Lexeme;

import java.util.List;

@RequiredArgsConstructor
public class SyntaxAnalyzer {
    private Lexeme currentLexeme;

    public boolean startAnalyze(List<Lexeme> lexemes) {
        return true;
    }
}
