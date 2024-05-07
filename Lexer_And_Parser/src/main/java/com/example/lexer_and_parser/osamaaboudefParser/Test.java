package com.example.lexer_and_parser.osamaaboudefParser;

import java.io.File;

public class Test {
    public Test(String grammarFileName, String sourceFileName) {
        var grammarFile = new File("src/main/java/com/example/lexer_and_parser/osamaaboudefParser/" + grammarFileName);
        var sourceFile = new File("src/main/java/com/example/lexer_and_parser/osamaaboudefParser/" + sourceFileName);
        var reporter = new Reporter(new AnalysisTable(grammarFile, sourceFile));

        reporter.printParsingSummary();
        reporter.printAnalysisTable();
    }

    public static void main(String[] args) {
        new Test("grammar.txt", "source.txt");
    }
}