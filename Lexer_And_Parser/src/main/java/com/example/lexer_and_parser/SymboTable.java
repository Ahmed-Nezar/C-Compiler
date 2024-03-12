package com.example.lexer_and_parser;

public class SymboTable {
    private String name;
    private String type;
    private String scope;
    private Integer lineNum;
    private Integer entryNum;
    static Integer numObj = 0;
    public SymboTable(String name, String type, String scope, Integer lineNum) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.lineNum = lineNum;
        numObj++;
        this.entryNum = numObj;
    }
    public String getName() {
        return name;
    }

    public String getType() {return type;}
}
