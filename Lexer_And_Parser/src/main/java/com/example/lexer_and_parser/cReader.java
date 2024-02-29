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
        return cLines;
    }
}
