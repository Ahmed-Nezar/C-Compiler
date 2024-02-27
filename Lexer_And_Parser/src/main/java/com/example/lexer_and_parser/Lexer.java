package com.example.lexer_and_parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static Dictionary<String, ArrayList<String>> predefinedTokens = new Hashtable<>();
    public cReader reader = new cReader("src/main/C/c_code.c");
    private ArrayList<String> dictKeys = new ArrayList<String>();
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
        ArrayList<String> int_values = new ArrayList<>();
        ArrayList<String> float_values = new ArrayList<>();
        ArrayList<String> keyword_values = new ArrayList<>();
        ArrayList<String> id_function = new ArrayList<>();
        ArrayList<String> id_variable = new ArrayList<>();
        ArrayList<String> id_struct = new ArrayList<>();

        ArrayList<String> lines = reader.getClines();
        boolean inBlockComment = false;
        String num_regex = "(['+']|-)?\\d+(\\.\\d+)?(e(['+']|-)?\\d+)?";
        Pattern tokenPattern = Pattern.compile(
            "\"[^\"]*\"|" +             // Match double-quoted strings
            "'.'|" +                    // Match single characters within single quotes
            num_regex + "|" +           // Match numbers
            "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b(\\()|" + // Match identifiers (Functions)
            "\\}(?:\\s*\\w+)?\\s*;|" + // Match End Block of Struct
            "^(struct|typedef( )struct)\\s+(\\w+)\\s*[\\{|;]|" + // Match identifiers (Structs)
            "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b((?!\\()|(?!\\{))|" + // Match identifiers (Variables)
            "\\(|\\)|\\{|\\}|;|,|" +    // Match parentheses, braces, semicolon, comma
            "\\+\\+|--|==|!=|<=|>=|&&|" + // Match comparison and logical operators
            "\\-\\>|\\+\\=|\\-\\=|\\*\\=|\\/\\=|\\%\\=|\\&\\=|\\|\\=|\\|\\||" + // Match compound assignment operators
            "\\+|\\*|/|%|<|>|\\^\\=|\\<\\<\\=|\\>\\>\\=|\\>\\>\\>\\=|" + // Match arithmetic and bitwise operators
            "\\-|\\+|\\*|\\=|\\&\\&|\\|\\||\\!|\\&|\\||\\^|\\~|\\<\\<|\\>\\>|\\>\\>\\>|\\?|" + // Match miscellaneous operators
            "\\:\\:|\\?\\:|\\+\\+|\\-\\-|\\." // Match other operators
        );
        for (String line : lines) {
            // Skip lines starting with "#" and comments
            if (!line.trim().startsWith("#")) {
                line = removeComments(line);
                // Match tokens using regular expression
                Matcher matcher = tokenPattern.matcher(line);
                while (matcher.find()) {
                    String token = matcher.group();
                    if (token.matches("\\}(?:\\s*\\w+)?\\s*;")){
                        String filteredStr = token.replaceAll("\\s", "").replaceAll(";", "")
                                .replaceAll("}", "");
                        puncatuation_values.add("}");
                        puncatuation_values.add(";");
                        if (!filteredStr.isEmpty()){
                            id_struct.add(filteredStr);
                        }
                        continue;
                    }
                    if (token.startsWith("\"") && token.endsWith("\"")) {
                        str_values.add(token);
                    } else if (token.startsWith("'") && token.endsWith("'")) {
                        char_values.add(token);
                    } else if (Character.isDigit(token.charAt(0))) {
                        if (!token.contains(".")){
                            int_values.add(token);
                        }else{
                            float_values.add(token);
                        }
                    } else if (isOperator(token)) {
                        operator_values.add(token);
                    } else if (isPunctuation(token)) {
                        puncatuation_values.add(token);
                    } else if (isKeyword(token)) {
                        keyword_values.add(token);
                    } else {
                        // Check if the token is a function or a variable
                        if (isFunction(token)) {
                            token = token.replaceFirst("\\(", "");
                            puncatuation_values.add("(");
                            id_function.add(token);
                        } else if (isStruct(token)) {
                            token = token.replaceFirst("typedef", "");
                            keyword_values.add("typedef");

                            token = token.replaceFirst("struct", "");
                            keyword_values.add("struct");

                            token = token.replaceAll("\\s", "");
                            while(!token.equals(token.replaceFirst("\\{", ""))){
                                token = token.replaceFirst("\\{", "");
                                puncatuation_values.add("{");
                            }
                            while(!token.equals(token.replaceFirst(";", ""))){
                                token = token.replaceFirst(";", "");
                                puncatuation_values.add(";");
                            }
                            id_struct.add(token);
                        } else {
                            id_variable.add(token);
                        }
                    }
                }
            }
        }

        dictKeys.add("Strings");
        dictKeys.add("Characters");
        dictKeys.add("Operators");
        dictKeys.add("Puncatuations");
        dictKeys.add("Integers");
        dictKeys.add("Floats");
        dictKeys.add("Keywords");
        dictKeys.add("Identifiers (Functions)");
        dictKeys.add("Identifiers (Variables)");
        dictKeys.add("Identifiers (Structs)");
        tokens.put(dictKeys.get(0), str_values);
        tokens.put(dictKeys.get(1), char_values);
        tokens.put(dictKeys.get(2), operator_values);
        tokens.put(dictKeys.get(3), puncatuation_values);
        tokens.put(dictKeys.get(4), int_values);
        tokens.put(dictKeys.get(5), float_values);
        tokens.put(dictKeys.get(6), keyword_values);
        tokens.put(dictKeys.get(7), id_function);
        tokens.put(dictKeys.get(8), id_variable);
        tokens.put(dictKeys.get(9), id_struct);
    }


    private boolean isFunction(String token) {
        return token.contains("(");
    }

    private boolean isStruct(String token) {
        return !token.replaceFirst("struct", "").equals(token);
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
//        String[] punctuations = { "(", ")", "{", "}", "[", "]", ",", ";", ":" };
//        for (String punctuation : punctuations) {
//            if (punctuation.equals(token)) {
//                return true;
//            }
//        }
//        return false;
        return predefinedTokens.get("Punctuations").contains(token);
    }

    private boolean isKeyword(String token) {
        return predefinedTokens.get("Keywords").contains(token);
    }

    public void displayTokens() {
        for (int i = 0; i < tokens.size(); i++) {
            System.out.print(dictKeys.get(i) + ": ");
            String values = "";
            for (int j = 0; j < tokens.get(dictKeys.get(i)).size(); j++) {
                System.out.print(tokens.get(dictKeys.get(i)).get(j));
                if (j < tokens.get(dictKeys.get(i)).size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        lexer.tokenize();
        lexer.displayTokens();
    }

}
