package com.example.lexer_and_parser.osamaaboudefParser;

import java.util.*;

public class FirstFollow {
    private Grammar grammar;
    private Map<String, Set<String>> firstTable = new HashMap<>();
    private Map<String, Set<String>> followTable = new HashMap<>();

    public FirstFollow(Grammar grammar) {
        this.grammar = grammar;
        generateFirstTable();
        generateFollowTable();
    }

    private void generateFirstTable() {
        for (var symbol : grammar.getNonTerminals())
            firstTable.put(symbol, computeFirstSet(symbol));
    }

    private void generateFollowTable() {
        for (var symbol : grammar.getNonTerminals())
            followTable.put(symbol, computeFollowSet(symbol));
    }

    private Set<String> computeFirstSet(String symbol) {
        Set<String> set = new HashSet<>();

        if (grammar.containsTerminal(symbol)) { // If the symbol is a terminal
            set.add(symbol);
            return set;
        }

        for (var production : grammar.getProductions(symbol)) { // Looping over the productions of the symbol
            var firstSymbol = production.getSymbols().get(0);
            if (grammar.containsTerminal(firstSymbol)) { // If the first symbol of the production is a terminal
                set.add(firstSymbol);
                continue;
            }

            var firstSymbolFirstSet = computeFirstSet(firstSymbol);
            if (production.length() == 1) { // If the production has only one symbol
                set.addAll(firstSymbolFirstSet);
                continue;
            }

            for (int i = 0; i < production.length(); i++) { // If the production has more than one symbol
                var productionSymbolFirstSet = computeFirstSet(production.getSymbols().get(i));

                if (!productionSymbolFirstSet.contains(grammar.getEmptySymbol()) || i == production.length() - 1) {
                    set.addAll(productionSymbolFirstSet);
                    break;
                }

                productionSymbolFirstSet.remove(grammar.getEmptySymbol());
                set.addAll(productionSymbolFirstSet);
            }
        }
        return set;
    }

    private Set<String> computeFollowSet(String symbol) {
        if (followTable.get(symbol) != null)
            return followTable.get(symbol);

        Set<String> set = new HashSet<>();
        if (grammar.getStartSymbol().equals(symbol))
            set.add("$");

        for (var nonTerminal : grammar.getNonTerminals()) {
            for (var production : grammar.getProductions(nonTerminal)) {
                var symbols = production.getSymbols();
                for (var i = 0; i < symbols.size(); i++) {
                    if (symbols.get(i).equals(symbol)) {
                        if (i < symbols.size() - 1) {
                            var nextFirstSet = computeFirstSet(symbols.get(i+1));
                            if (nextFirstSet.contains(grammar.getEmptySymbol())) {
                                nextFirstSet.remove(grammar.getEmptySymbol());
                                set.addAll(nextFirstSet);
                                set.addAll(computeFollowSet(symbols.get(i+1)));
                            }
                            else
                                set.addAll(nextFirstSet);
                        }
                        else if (!nonTerminal.equals(symbol))
                            set.addAll(computeFollowSet(nonTerminal));
                        break;
                    }
                }
            }
        }
        return set;
    }

    public Map<String, Set<String>> getFirstTable() {
        return firstTable;
    }

    public Map<String, Set<String>> getFollowTable() {
        return followTable;
    }

    public Set<String> firstSet(String symbol) {
        if (firstTable.get(symbol) != null)
            return firstTable.get(symbol);

        Set<String> set = new HashSet<>();
        set.add(symbol);
        return set;
    }

    public Set<String> firstSet(Production production) {
        Set<String> set = new HashSet<>();
        var symbols = production.getSymbols();
        for (int i = 0; i < symbols.size(); i++) {
            var symbolFirstSet = firstSet(symbols.get(i));
            if (symbolFirstSet.contains(grammar.getEmptySymbol()) && i < symbols.size()-1) {
                symbolFirstSet.remove(grammar.getEmptySymbol());
                set.addAll(symbolFirstSet);
            }
            else {
                set.addAll(symbolFirstSet);
                break;
            }
        }
        return set;
    }

    public Set<String> followSet(String symbol) { return followTable.get(symbol); }
}
