package com.example.lexer_and_parser;
import org.abego.treelayout.*;
import org.abego.treelayout.util.*;
import java.io.File;

public class Test {
    public Test(String grammarFileName, String sourceFileName) {
        var grammarFile = new File("src/main/java/com/example/lexer_and_parser/" + grammarFileName);
        var sourceFile = new File("src/main/C/" + sourceFileName);
        var reporter = new Reporter(new AnalysisTable(grammarFile, sourceFile));

        reporter.printParsingSummary();
        reporter.printAnalysisTable();
    }

    public static void main(String[] args) {
        new Test("grammar.txt", "c_code(Parser).c");
    }
}