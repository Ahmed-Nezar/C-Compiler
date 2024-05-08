package com.example.lexer_and_parser;

import java.util.ArrayList;
import java.util.List;
public class Production {
    private List<String> symbols = new ArrayList<>(); // List of symbols in the RHS of the production

    public Production(String... symbols) {
        for (var symbol : symbols)
            this.symbols.add(symbol);
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public int length() {
        return symbols.size();
    }

    @Override
    public String toString() {
        return symbols.toString();
    }
}
