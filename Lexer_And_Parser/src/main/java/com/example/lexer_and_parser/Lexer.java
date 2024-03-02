package com.example.lexer_and_parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static Dictionary<String, ArrayList<String>> predefinedTokens = new Hashtable<>();
    public cReader reader;
    private ArrayList<String> dictKeys = new ArrayList<>();
    private Dictionary<String, ArrayList<String>> tokens = new Hashtable<>();
    public Lexer(String path){
        reader = new cReader(path);
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

    public void changePath(String path){
        reader = new cReader(path);
    }

    public void tokenize() {

        // Clear the dictionary and tokens
        dictKeys.clear();
        Enumeration<String> keys = tokens.keys();
        while (keys.hasMoreElements()) {
            tokens.remove(keys.nextElement());
        }
        // ------------------------------

        ArrayList<String> str_values = new ArrayList<>();
        ArrayList<String> char_values = new ArrayList<>();
        ArrayList<String> operator_values = new ArrayList<>();
        ArrayList<String> puncatuation_values = new ArrayList<>();
        ArrayList<String> int_values = new ArrayList<>();
        ArrayList<String> float_values = new ArrayList<>();
        ArrayList<String> long_values = new ArrayList<>();
        ArrayList<String> long_long_values = new ArrayList<>();
        ArrayList<String> keyword_values = new ArrayList<>();
        ArrayList<String> id_function = new ArrayList<>();
        ArrayList<String> id_variable = new ArrayList<>();
        ArrayList<String> id_struct = new ArrayList<>();
        ArrayList<String> lines = reader.getClines();
        boolean inBlockComment = false;
        String num_regex = "(['+']|-)?\\d+(\\.\\d+)?(e(['+']|-)?\\d+)?";
        String num_dataType_regex = "(ULL|LL|L|UL|F|ull|ll|l|ul|f)?";
        Pattern tokenPattern = Pattern.compile(
                "\"[^\"]*\"|" +             // Match double-quoted strings
                        "'.'|" +                    // Match single characters within single quotes
                        num_regex + num_dataType_regex + "|" +           // Match numbers
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
        boolean isStruct = false;
        boolean structDataType = false;
        for (String line : lines) {
            structDataType = false;
            // Skip lines starting with "#" and comments
            if (!line.trim().startsWith("#")) {
                line = removeComments(line);
                // Match tokens using regular expression
                Matcher matcher = tokenPattern.matcher(line);
                while (matcher.find()) {
                    String token = matcher.group();
                    if (token.matches("\\}(?:\\s*\\w+)?\\s*;") && isStruct){
                        isStruct = false;
                        String filteredStr = token.replaceAll("\\s", "").replaceAll(";", "")
                                .replaceAll("}", "");
                        puncatuation_values.add("}");
                        puncatuation_values.add(";");
                        if (!filteredStr.isEmpty()){
                            id_struct.add(filteredStr);
                        }
                        continue;
                    }
                    if (isPunctuation(token)) {
                        puncatuation_values.add(token);
                        // Disable Struct Data Type Flag
                        if (token.equals(";"))
                            structDataType = false;
                    } else if (isOperator(token)) {
                        operator_values.add(token);
                    } else if (Character.isDigit(token.charAt(0))) {
                        if (token.matches(num_regex + "(ul|UL|l|L)")) {
                            long_values.add(token);
                        } else if (token.matches(num_regex + "(ull|ULL|ll|LL)")) {
                            long_long_values.add(token);
                        } else if (!token.contains(".") && !token.contains("e")){
                            int_values.add(token);
                        } else{
                            float_values.add(token);
                        }
                    } else if (token.startsWith("'") && token.endsWith("'")) {
                        char_values.add(token);
                    } else if (token.startsWith("\"") && token.endsWith("\"")) {
                        str_values.add(token);
                    } else if (isKeyword(token)) {
                        if (token.equals("struct")) {
                            isStruct = true;
                        }
                        keyword_values.add(token);
                    } else {
                        if (id_struct.contains(token) && !structDataType){
                            id_struct.add(token);
                            structDataType = true;
                        }
                        // Check if the token is a function or a variable
                        else if (isFunction(token)) {
                            token = token.replaceFirst("\\(", "");
                            puncatuation_values.add("(");
                            if (isKeyword(token))
                                keyword_values.add(token);
                            else
                                id_function.add(token);
                        } else if (isStruct(token)) {
                            isStruct = true;
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
                            if (token.contains("}")) {
                                token = token.replaceFirst("}", "");
                                puncatuation_values.add("}");
                            }
                            if (token.contains(";")) {
                                token = token.replaceFirst(";", "");
                                puncatuation_values.add(";");
                            }
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
        dictKeys.add("Long");
        dictKeys.add("Long Long");
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
        tokens.put(dictKeys.get(6), long_values);
        tokens.put(dictKeys.get(7), long_long_values);
        tokens.put(dictKeys.get(8), keyword_values);
        tokens.put(dictKeys.get(9), id_function);
        tokens.put(dictKeys.get(10), id_variable);
        tokens.put(dictKeys.get(11), id_struct);
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
        Lexer lexer1 = new Lexer("src/main/C/c_code.c");
        lexer1.tokenize();
        lexer1.displayTokens();
        System.out.println("-------------------------------------------------");
        lexer1.changePath("src/main/C/c_code2.c");
        lexer1.tokenize();
        lexer1.displayTokens();
    }

}
