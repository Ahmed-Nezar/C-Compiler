package com.example.lexer_and_parser;

import java.util.ArrayList;

public class Parser {
    public static void main(String[] args) {
        Lexer lexer1 = new Lexer("src/main/C/c_code.c");
        while (!lexer1.EOF) {
            Token t = lexer1.getNextToken();
            t.displayToken();
        }
        System.out.println("----------------------------------------------------------------------");
        lexer1.changePath("src/main/C/c_code2.c");
        while (!lexer1.EOF) {
            Token t = lexer1.getNextToken();
            t.displayToken();
        }
        System.out.println("----------------------------------------------------------------------");
    }

    public void Parse(Token t){

    }
}
