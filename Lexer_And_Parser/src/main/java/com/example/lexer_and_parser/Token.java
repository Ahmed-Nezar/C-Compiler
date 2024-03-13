package com.example.lexer_and_parser;

import java.util.Objects;

public class Token {
    String name;
    String attrVal;
    Integer lineNum;

    static Integer idCount = 1;
    Integer entryNum = 0;
    static Integer numObj = 0;
    String identifier = "";
    String identifierType = "";
    public Token(String name, String attrVal, Integer lineNum) {
        this.name = name;
        this.attrVal = attrVal;
        this.lineNum = lineNum;
        if (name.contains("Identifiers") && !name.contains("Bad")){
            this.identifier = "id" + idCount;
            this.entryNum = idCount;
            idCount++;
            identifierType = name.replaceFirst("Identifiers \\(", "")
                    .replaceFirst("\\)", "");
        }
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

    public Integer getEntryNum() {
        return entryNum;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(attrVal, token.attrVal) &&
                Objects.equals(identifierType, token.identifierType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, identifierType);
    }
}
