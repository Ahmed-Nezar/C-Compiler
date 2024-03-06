package com.example.lexer_and_parser;

public class Token {
    String name;
    String attrVal;
    Integer lineNum;
    Integer entryNum;
    static Integer numObj = 0;
    public Token(String name, String attrVal, Integer lineNum) {
        this.name = name;
        this.attrVal = attrVal;
        this.lineNum = lineNum;
        numObj++;
        this.entryNum = numObj;
    }
    public void displayToken() {
        System.out.println("Name: " + name + ", Attribute Value: " + attrVal + ", Line Number: " + lineNum);
    }
    public String getName() {
        return name;
    }

    public String getAttrVal() {return attrVal;}

    public Integer getLineNum() {
        return lineNum;
    }
}
