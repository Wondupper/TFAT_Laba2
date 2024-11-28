package org.example.syntax;

import org.example.core.Lexeme;
import org.example.core.LexemeCategory;
import org.example.core.LexemeType;
import org.example.exeptions.*;

import java.util.List;
import java.util.ListIterator;

public class SyntaxAnalyzer {
    private ListIterator<Lexeme> lexemeIterator;
    private Lexeme currentLexeme;
    private List<Lexeme> lexemes;

    public SyntaxAnalyzer(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
        lexemeIterator = lexemes.listIterator();
    }

    public boolean startAnalyze() {
        try {
            checkLexemesAvailable();
            checkIfStatement();
            return true;
        } catch (IllegalArgumentException ex) {
            throw new LexicalAnalysisException("Ошибка при лексическом анализе: ", ex);
        } catch (InvalidLexemeException | NoLexemesFoundException ex) {
            throw new SyntaxAnalisisException("Ошибка при синтаксическом анализе: ", ex);
        }
    }

    private void checkLexemesAvailable() {
        if (lexemes.isEmpty()) {
            throw new NoLexemesFoundException("Список лексем пустой");
        }
    }

    private void checkIfStatement() {
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeType() != LexemeType.IF) {
                throw new InvalidLexemeException("Ожидался if", currentLexeme.startIndex());
            }
        } else {
            throw new InvalidLexemeException("Ожидался if", currentLexeme.startIndex());
        }
        checkCondition();
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeType() != LexemeType.THEN) {
                throw new InvalidLexemeException("Ожидался then", currentLexeme.startIndex());
            }
        } else {
            throw new InvalidLexemeException("Ожидался then", currentLexeme.startIndex());
        }
        checkStatement();
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeType() == LexemeType.ELSEIF) {
                checkElseIfStatement();
            } else if (currentLexeme.lexemeType() == LexemeType.END) {
                checkEnd();
            } else if (currentLexeme.lexemeType() == LexemeType.ELSE) {
                checkStatement();
                if (currentLexeme.lexemeType() == LexemeType.END) {
                    checkEnd();
                } else {
                    throw new InvalidLexemeException("Ожидался end", currentLexeme.startIndex());
                }
            } else {
                throw new InvalidLexemeException("Ожидался else", currentLexeme.startIndex());
            }
        }
    }

    private void checkElseIfStatement() {
        if (lexemeIterator.hasNext()) {
            checkCondition();
            if (lexemeIterator.hasNext()) {
                currentLexeme = lexemeIterator.next();
                if (currentLexeme.lexemeType() != LexemeType.THEN) {
                    throw new InvalidLexemeException("Ожидался then", currentLexeme.startIndex());
                }
            } else {
                throw new InvalidLexemeException("Ожидался then", currentLexeme.startIndex());
            }
            checkStatement();
            if (lexemeIterator.hasNext()) {
                currentLexeme = lexemeIterator.next();
                if (currentLexeme.lexemeType() == LexemeType.ELSEIF) {
                    checkElseIfStatement();
                }
            }
        }
    }

    private void checkEnd() {
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeType() != LexemeType.END) {
                throw new InvalidLexemeException("Ожидалась end", currentLexeme.startIndex());
            }
        }
    }

    private void checkStatement() {
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            checkAssigmentStatement();
            if (lexemeIterator.hasNext()) {
                currentLexeme = lexemeIterator.next();
                if (currentLexeme.lexemeType() != LexemeType.DELIMITER || !currentLexeme.value().equals(";")) {
                    throw new InvalidLexemeException("Ожидалась точка с запятой", currentLexeme.startIndex());
                }
            } else {
                throw new InvalidLexemeException("Ожидалась точка с запятой в конце оператора", currentLexeme.startIndex());
            }
        } else {
            throw new InvalidLexemeException("Ожидался оператор", currentLexeme.startIndex());
        }
    }

    private void checkAssigmentStatement() {
        if (currentLexeme.lexemeCategory() != LexemeCategory.IDENTIFIER) {
            throw new InvalidLexemeException(LexemeCategory.IDENTIFIER, "Ожидался идентификатор в присваивании", currentLexeme.startIndex());
        }
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeType() != LexemeType.ASSIGMENT) {
                throw new InvalidLexemeException(LexemeType.ASSIGMENT, "Ожидался оператор присваивания", currentLexeme.startIndex());
            }
            checkArithmeticExpression();
        } else {
            throw new InvalidLexemeException(LexemeType.ASSIGMENT, "Ожидался оператор присваивания", currentLexeme.startIndex());
        }
    }

    private void checkArithmeticExpression() {
        checkOperand();
        checkNextArithmeticOperation();
    }

    private void checkNextArithmeticOperation() {
        if (!lexemeIterator.hasNext()) {
            return;
        }
        currentLexeme = lexemeIterator.next();
        if (currentLexeme.lexemeType() == LexemeType.ARITHMETIC_LOW || currentLexeme.lexemeType() == LexemeType.ARITHMETIC_HIGH) {
            checkOperand();
            checkNextArithmeticOperation();
        } else if (currentLexeme.lexemeType() == LexemeType.DELIMITER && currentLexeme.value().equals(";")) {
            lexemeIterator.previous();
        } else {
            throw new InvalidLexemeException("Ожидался символ арифметической операции на позиции: " + currentLexeme.startIndex(), currentLexeme.startIndex());
        }
    }

    private void checkCondition() {
        checkRelationExpression();
        checkNextLogicalOperation();
    }

    private void checkNextLogicalOperation() {
        if (!lexemeIterator.hasNext()) {
            return;
        }
        currentLexeme = lexemeIterator.next();
        if (currentLexeme.lexemeType() == LexemeType.OR || currentLexeme.lexemeType() == LexemeType.AND) {
            checkRelationExpression();
            checkNextLogicalOperation();
        } else {
            lexemeIterator.previous();
        }
    }

    private void checkRelationExpression() {
        checkOperand();
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeType() == LexemeType.RELATION) {
                checkOperand();
            } else {
                lexemeIterator.previous();
            }
        }
    }

    private void checkOperand() {
        if (lexemeIterator.hasNext()) {
            currentLexeme = lexemeIterator.next();
            if (currentLexeme.lexemeCategory() != LexemeCategory.CONSTANT && currentLexeme.lexemeCategory() != LexemeCategory.IDENTIFIER) {
                throw new InvalidLexemeException(LexemeCategory.IDENTIFIER, LexemeCategory.CONSTANT, currentLexeme.startIndex());
            }
        } else {
            throw new InvalidLexemeException(LexemeCategory.IDENTIFIER, LexemeCategory.CONSTANT, currentLexeme.startIndex());
        }
    }
}
