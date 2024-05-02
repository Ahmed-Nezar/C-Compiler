package com.example.lexer_and_parser.osamaaboudefParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PreProcessor {
    public static String getStrippedSource(File sourceFile) {
        var source = "";
        try {
            var scanner = new Scanner(sourceFile);
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                if (line.startsWith("#") || line.equals(""))
                    continue;
                source = source.concat(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File error occurred.");
        }
        return source;
    }
}
