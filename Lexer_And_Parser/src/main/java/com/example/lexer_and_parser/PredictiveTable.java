package com.example.lexer_and_parser;

import java.util.HashMap;
import java.util.Map;
/*
table =
        {
            TERMINAL: {
                NON-TERMINAL: PRODUCTION,
                NON-TERMINAL: PRODUCTION
            },
            TERMINAL: {
                NON-TERMINAL: PRODUCTION,
                NON-TERMINAL: PRODUCTION
            }
        }
 */
public class PredictiveTable {
    private Map<String, Map<String, Production>> table = new HashMap<>(); // Key: Terminal, Value: Map of Non-Terminal & Production

    public PredictiveTable(Grammar grammar) {
        generateTable(grammar);
    }

    private void generateTable(Grammar grammar) {
        var firstFollow = new FirstFollow(grammar);
        for (var nonTerminal : grammar.getNonTerminals()) { // Looping over the non-terminals of the grammar
            for (var production : grammar.getProductions(nonTerminal)) { // Looping over each production for each non-terminal
                for (var first : firstFollow.firstSet(production)) { // Looping over the first set of each production
                    if (first.equals(grammar.getEmptySymbol())) {
                        var epsilon = new Production(grammar.getEmptySymbol()); // Creating an epsilon production
                        for (var follow : firstFollow.followSet(nonTerminal)) {
                            var cellContent = follow.equals("$") ? production : epsilon; // If the follow symbol is $, then the cell content is the production, otherwise it's epsilon
                            if (table.containsKey(follow))
                                table.get(follow).put(nonTerminal, cellContent);
                            else {
                                Map<String, Production> column = new HashMap<>();
                                column.put(nonTerminal, cellContent);
                                table.put(follow, column);
                            }
                        }
                    }
                    else {
                        if (table.containsKey(first))
                            table.get(first).put(nonTerminal, production);
                        else {
                            Map<String, Production> column = new HashMap<>();
                            column.put(nonTerminal, production);
                            table.put(first, column);
                        }
                    }
                }
            }
        }
    }

    public Map<String, Map<String, Production>> getTable() {
        return table;
    }
}
