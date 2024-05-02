package com.example.lexer_and_parser.osamaaboudefParser;

import java.util.*;

public class Lexer2 {
    private List<String> lexemes;
    private List<String> tokens;

    public Lexer2() {
        initialise();
    }

    public List<String> createTokens(String source) {
        scan(source);
        return tokens;
    }

    private void scan(String source) {
        initialise();
        identifyLexemes(source);
        generateTokens();
    }

    private void initialise() {
        lexemes = new ArrayList<>();
        tokens = new ArrayList<>();
    }

    private List<String> getTokens() {
        return tokens;
    }

    private void identifyLexemes(String source) {
        String lexeme = "";
        for (var ch : source.toCharArray()) {
            if (isDelimiter(ch)) {
                if (!isEmpty(lexeme)) {
                    lexemes.add(lexeme);
                    lexeme = "";
                }
                if (!isWhiteSpace(ch))
                    lexemes.add(String.valueOf(ch));
            }
            else
                lexeme += ch;
        }
        if (!isEmpty(lexeme))
            lexemes.add(lexeme);
    }

    private void generateTokens() {
        for (var lexeme : lexemes) {
            var type = getType(lexeme);
            if (type == "Identifier" || type == "Integer" || type == "Unrecognized")
                tokens.add(type);
            else
                tokens.add(lexeme);
        }
    }

    private String getType(String lexeme) {
        if (isIdentifier(lexeme))
            return "Identifier";
        if (isInteger(lexeme))
            return "Integer";
        if (lexeme.length() == 1) {
            var ch = lexeme.toCharArray()[0];
            if (isOperator(ch))
                return "Operator";
            if (isParenthesis(ch))
                return "Parenthesis";
            if (isBracket(ch))
                return "Bracket";
            if (isCurlyBrace(ch))
                return "CurlyBrace";
            if (isPunctuation(ch))
                return "Punctuation";
        }
        return "Unrecognized";
    }

    private boolean isIdentifier(String lexeme) {
        if (!isLetter(lexeme.toCharArray()[0]))
            return false;

        for (var ch : lexeme.toCharArray())
            if (!isLetter(ch) && !isDigit(ch))
                return false;

        return true;
    }

    private boolean isInteger(String lexeme) {
        for (var ch : lexeme.toCharArray())
            if (!isDigit(ch))
                return false;

        return true;
    }

    private boolean isLetter(char ch) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(ch) > -1;
    }

    private boolean isDigit(char ch) {
        return "0123456789".indexOf(ch) > -1;
    }

    private boolean isOperator(char ch) {
        return "+-*/=".indexOf(ch) > -1;
    }

    private boolean isParenthesis(char ch) {
        return "()".indexOf(ch) > -1;
    }

    private boolean isBracket(char ch) {
        return "[]".indexOf(ch) > -1;
    }

    private boolean isCurlyBrace(char ch) {
        return "{}".indexOf(ch) > -1;
    }

    private boolean isPunctuation(char ch) {
        return ",;.'\"".indexOf(ch) > -1;
    }

    private boolean isWhiteSpace(char ch) {
        return " \n\t".indexOf(ch) > -1;
    }

    private boolean isDelimiter(char ch) {
        return isWhiteSpace(ch) || isParenthesis(ch) || isBracket(ch) || isCurlyBrace(ch)
                || isPunctuation(ch) || isOperator(ch);
    }

    private boolean isEmpty(String input) {
        return input.equals("");
    }
}