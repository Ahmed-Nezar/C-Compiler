package com.example.lexer_and_parser.osamaaboudefParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Grammar {
    private String startSymbol;
    private String emptySymbol;
    private List<String> nonTerminals;
    private Map<String, List<Production>> rules; // Key: Non-Terminal, Value: List of Productions

    public Grammar(File grammarFile) {
        nonTerminals = new ArrayList<>();
        rules = new HashMap<>();
        try {
            var scanner = new Scanner(grammarFile);
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine().strip();
                if (line.startsWith("#") || line.equals(""))
                    continue;

                addRule(line);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File error occurred.");
        }
    }

    private void addRule(String line) { // Add non-terminal and its productions to the grammar
        line = line.strip();
        var sides = line.contains("→") ? line.split("→") : line.split("->");
        var nonTerminal = sides[0].strip();
        var rightSide = sides[1].strip();
        List<Production> productions = new ArrayList<>();
        for (var productionText : rightSide.split("\\|"))
            productions.add(new Production(productionText.strip().split(" ")));
        nonTerminals.add(nonTerminal);
        rules.put(nonTerminal, productions);
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public Map<String, List<Production>> getRules() {
        return rules;
    }

    public List<Production> getProductions(String symbol) {
        return rules.get(symbol);
    }

    public boolean containsTerminal(String symbol) {
        return !nonTerminals.contains(symbol);
    }

    public String getStartSymbol() {
        return startSymbol != null ? startSymbol : nonTerminals.get(0);
    }

    public String getEmptySymbol() {
        return emptySymbol != null ? emptySymbol : "epsilon";
    }
}
