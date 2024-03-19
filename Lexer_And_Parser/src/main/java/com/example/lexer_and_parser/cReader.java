package com.example.lexer_and_parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class cReader {
    private String path;
    private ArrayList<String> cLines= new ArrayList<String>();
    cReader(){
        this("");
    }
    cReader(String p){
        path = p;
    }
    public ArrayList<String> cLinesGetter() {
        return cLines;
    }
    public ArrayList<String> getClines() {
        cLines.clear();
        // Specify the path to your .c file
        String filePath = path;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read file line by line
            while ((line = br.readLine()) != null) {
                // Process each line as needed
                cLines.add(line);
            }
        } catch (IOException e) {
            // Handle any IO exceptions
            e.printStackTrace();
        }
        cleanClines();
        return cLines;
    }

    private void cleanClines(){
        boolean blockComment = false;
        for (int i = 0; i < cLines.size(); i++) {
            String line = cLines.get(i);
            if (blockComment) {
                if (line.matches("(.)*\\*/")) {
                    blockComment = false;
                }
                cLines.set(i, "");
            } else {
                if (line.matches("(.)*//(.)*")){
                    cLines.set(i, cLines.get(i).replaceAll("//(.)*", ""));
                }
                else if (line.matches("(.)*/\\*(.)*\\*/")){
                    cLines.set(i, cLines.get(i).replaceAll("/\\*(.)*\\*/", ""));
                }
                else if (line.matches("(.)*/\\*(.)*")) {
                    blockComment = true;
                    cLines.set(i, cLines.get(i).replaceAll("/\\*(.)*", ""));
                }
            }
            if (line.matches("(\\s)*#(define|undef|include|ifdef|ifndef|if|else|elif|endif|error|pragma|line|null)")){
                cLines.set(i, cLines.get(i).replaceFirst("#(.)*", ""));
            }
        }
    }

    public void clear(){
        cLines.clear();
    }
}
