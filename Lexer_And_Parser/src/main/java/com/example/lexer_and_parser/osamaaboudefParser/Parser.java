package com.example.lexer_and_parser.osamaaboudefParser;

import java.io.File;

public class Parser {
    public static boolean isParsable(File grammarFile, File sourceFile) {
        return new AnalysisTable(grammarFile, sourceFile).isComplete();
    }
}
