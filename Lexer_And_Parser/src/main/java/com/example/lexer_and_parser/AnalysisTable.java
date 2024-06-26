package com.example.lexer_and_parser;

import java.io.File;
import java.util.*;

public class AnalysisTable {
    private File grammarFile;
    private File sourceFile;
    public static Grammar grammar;
    private String[] columnNames;
    private Deque<String[]> table;
    private boolean complete;

    public AnalysisTable(File grammarFile, File sourceFile) {
        this.grammarFile = grammarFile;
        this.sourceFile = sourceFile;
        grammar = new Grammar(grammarFile);
        columnNames = new String[]{"Stack", "Tokens Queue", "Decision", "Observation"};
        Lexer l = new Lexer(sourceFile.getPath()); // Orignial Remove
        l.tokenize(); // Orignial Remove
        buildTable(l.getTokens()); // Orignial new Lexer2().createTokens(source)
    }

    public static <T> void reverse(Stack<T> stack) {
        if (!stack.isEmpty()) {
            T element = stack.pop();
            reverse(stack);
            insertAtBottom(stack, element);
        }
    }

    private static <T> void insertAtBottom(Stack<T> stack, T element) {
        if (stack.isEmpty()) {
            stack.push(element);
        } else {
            T top = stack.pop();
            insertAtBottom(stack, element);
            stack.push(top);
        }
    }

    private void buildTable(ArrayList<Token> tokens) { // Orignial List<String>
        var predictiveTable = new PredictiveTable(grammar).getTable();
        table = new ArrayDeque<>();
        complete = false;
        String[] row;

        Queue<String> input = new ArrayDeque<>();
        for (Token token : tokens) {
            if (token.getName().equals("Puncatuations") ||
                    token.getName().equals("Keywords") ||
                    token.getName().equals("Operators")) {
                input.add(token.getAttrVal());
            } else {
                input.add(token.getName());
            }
        }
        input.add("$");

        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(grammar.getStartSymbol());

        while (!stack.isEmpty()) {
            row = new String[4];
            row[0] = string(stack);
            row[1] = string(input);

            var top = stack.pop();
            var front = input.peek();
            if (isTerminal(top)) {
                if (!top.equals(front)) {
                    row[2] = "Syntax Error";
                    row[3] = "Invalid Syntax";
                    table.add(row);
                    return;
                }
                else if (front.equals("$")) {
                    row[2] = "Accepted";
                    row[3] = "Success";
                }
                else
                    row[2] = "Matching";
                input.remove();
            }
            else if (front.equals("$")) {
                var foundEmptyProduction = false;
                for (var production : grammar.getProductions(top)) {
                    var symbols = production.getSymbols();
                    if (symbols.get(0).equals(grammar.getEmptySymbol())) {
                        row[2] = "Production";
                        row[3] = symbols.toString().replaceAll("[\\[,\\]]", "");
                        foundEmptyProduction = true;
                        break;
                    }
                }
                if (!foundEmptyProduction) {
                    row[2] = "Syntax Error";
                    row[3] = "Invalid Syntax";
                    table.add(row);
                    return;
                }
            }
            else {
                if (predictiveTable.get(front) == null ||
                        front.equals("Bad_Identifiers") ||
                        front.equals("Bad_Integers")) {
                    row[2] = "Lexical Error";
                    row[3] = "Illegal Token";
                    table.add(row);
                    return;
                }
                var production = predictiveTable.get(front).get(top);
                if (production == null) {
                    row[2] = "Syntax Error";
                    row[3] = "Invalid Syntax";
                    table.add(row);
                    return;
                }
                var symbols = production.getSymbols();
                if (!symbols.get(0).equals(grammar.getEmptySymbol())) {
                    Stack<String> temp = new Stack<>();
                    for (var symbol : symbols)
                        temp.push(symbol);
                    while (!temp.isEmpty())
                        stack.push(temp.pop());
                }

                row[2] = "Production";
                row[3] = symbols.toString().replaceAll("[\\[,\\]]", "");
            }
            table.add(row);
        }
        complete = true;
    }

    public Deque<String[]> getTable() {
        return table;
    }

    public boolean isComplete() {
        return complete;
    }

    public File getGrammarFile() {
        return grammarFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public static boolean isTerminal(String symbol) {
        return grammar.containsTerminal(symbol);
    }

    private String string(Stack stack) {
        var string = "";
        for (var item : stack) {
            string += item + " ";
        }
        string = string.trim();
        return string;
    }

    private String string(Queue queue) {
        var string = "";
        for (var item : queue) {
            string += item + " ";
        }
        string = string.trim();
        return string;
    }
}