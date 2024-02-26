package com.example.lexer_and_parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static Dictionary<String, ArrayList<String>> predefinedTokens = new Hashtable<>();
    public cReader reader = new cReader("src/main/C/c_code.c");
    private Dictionary<String, ArrayList<String>> tokens = new Hashtable<>();
    public Lexer(){
        ArrayList<String> cOperators = new ArrayList<>();
        ArrayList<String> cPunctuation = new ArrayList<>();
        ArrayList<String> cKeywords = new ArrayList<>();

        // Add C-language operators to the ArrayList
        cOperators.add("+");cOperators.add("-");cOperators.add("*");cOperators.add("/");cOperators.add("%");
        cOperators.add("=");cOperators.add("==");cOperators.add("!=");cOperators.add(">");cOperators.add("<");
        cOperators.add(">=");cOperators.add("<=");cOperators.add("&&");cOperators.add("||");cOperators.add("!");
        cOperators.add("&");cOperators.add("|");cOperators.add("^");cOperators.add("~");cOperators.add("<<");
        cOperators.add(">>");cOperators.add(">>>");cOperators.add("?:");cOperators.add("++");cOperators.add("--");
        cOperators.add("+=");cOperators.add("-=");cOperators.add("*=");cOperators.add("/=");cOperators.add("%=");
        cOperators.add("&=");cOperators.add("|=");cOperators.add("^=");cOperators.add("<<=");cOperators.add(">>=");
        cOperators.add(">>>=");cOperators.add("->");cOperators.add(".");cOperators.add("?");

        // Add C-language punctuations to the ArrayList
        cPunctuation.add("(");cPunctuation.add(")");cPunctuation.add("{");cPunctuation.add("}");
        cPunctuation.add("]");cPunctuation.add(";");cPunctuation.add(",");
        cPunctuation.add(":");cPunctuation.add("[");

        // Add C-language keywords to the ArrayList
        cKeywords.add("auto");cKeywords.add("break");cKeywords.add("case");cKeywords.add("char");
        cKeywords.add("const");cKeywords.add("continue");cKeywords.add("default");cKeywords.add("do");
        cKeywords.add("double");cKeywords.add("else");cKeywords.add("enum");cKeywords.add("extern");
        cKeywords.add("float");cKeywords.add("for");cKeywords.add("goto");cKeywords.add("if");
        cKeywords.add("int");cKeywords.add("long");cKeywords.add("register");cKeywords.add("return");
        cKeywords.add("short");cKeywords.add("signed");cKeywords.add("sizeof");cKeywords.add("static");
        cKeywords.add("struct");cKeywords.add("switch");cKeywords.add("typedef");cKeywords.add("union");
        cKeywords.add("unsigned");cKeywords.add("void");cKeywords.add("volatile");cKeywords.add("while");

        predefinedTokens.put("Operators", cOperators);
        predefinedTokens.put("Punctuations", cPunctuation);
        predefinedTokens.put("Keywords", cKeywords);
    }

    public void tokenize() {
        ArrayList<String> str_values = new ArrayList<>();
        ArrayList<String> char_values = new ArrayList<>();
        ArrayList<String> operator_values = new ArrayList<>();
        ArrayList<String> puncatuation_values = new ArrayList<>();
        ArrayList<String> number_values = new ArrayList<>();
        ArrayList<String> keyword_values = new ArrayList<>();
        ArrayList<String> id_values = new ArrayList<>();

        ArrayList<String> lines = reader.getClines();
        boolean inBlockComment = false;
        Pattern tokenPattern = Pattern.compile("\"[^\"]*\"|'.'|\\b(\\d+\\.\\d+|\\d+|\\b(?:\\+|-|\\*|/|%|=|==|!=|>|<|>=|<=|&&|\\|\\||!|&|\\||\\^|~|<<|>>|>>>|\\?|\\+\\+|--|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=|->|\\.|\\?|\\(|\\)|\\{|\\}|\\[|\\]))\\b|\\b[a-zA-Z_][a-zA-Z0-9_]*\\b|\\(|\\)|\\{|\\}|;|,");
        for (String line : lines) {
            // Skip lines starting with "#" and comments
            if (!line.trim().startsWith("#")) {
                line = removeComments(line);
                // Match tokens using regular expression
                Matcher matcher = tokenPattern.matcher(line);
                while (matcher.find()) {
                    String token = matcher.group();
                    if (token.startsWith("\"") && token.endsWith("\"")) {
                        str_values.add(token);
                    } else if (token.startsWith("'") && token.endsWith("'")) {
                        char_values.add(token);
                    } else if (Character.isDigit(token.charAt(0))) {
                        number_values.add(token);
                    } else if (isOperator(token)) {
                        operator_values.add(token);
                    } else if (isPunctuation(token)) {
                        puncatuation_values.add(token);
                    } else if (isKeyword(token)) {
                        keyword_values.add(token);
                    } else {
                        id_values.add(token);
                    }
                }
            }
        }
        tokens.put("String", str_values);
        tokens.put("Character", char_values);
        tokens.put("Operators", operator_values);
        tokens.put("Puncatuations", puncatuation_values);
        tokens.put("Numbers", number_values);
        tokens.put("Keywords", keyword_values);
        tokens.put("Identifiers", id_values);
    }


    private boolean isFunction(String token) {
        // Check if the token is followed by a pair of parentheses
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(.*\\)");
    }

    private boolean isVariable(String token) {
        // Check if the token is followed by an equal sign or is a standalone identifier
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*.*|;)");
    }

    private String removeComments(String line) {
        // Remove single-line comments starting with "//"
        line = line.replaceAll("//.*", "");
        // Remove block comments "/* */"
        line = line.replaceAll("/\\*.*?\\*/", "");
        return line;
    }


    private boolean isOperator(String token) {
        return predefinedTokens.get("Operators").contains(token);
    }

    private boolean isPunctuation(String token) {
        String[] punctuations = { "(", ")", "{", "}", "[", "]", ",", ";", ":" };
        for (String punctuation : punctuations) {
            if (punctuation.equals(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKeyword(String token) {
        return predefinedTokens.get("Keywords").contains(token);
    }

    public void displayTokens() {
        System.out.println(tokens);
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        lexer.tokenize();
        lexer.displayTokens();
    }

}
