package com.example.lexer_and_parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static Dictionary<String, ArrayList<String>> predefinedTokens = new Hashtable<>();
    public cReader reader;
    private ArrayList<Token> tk = new ArrayList<>();
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
        cOperators.add(">>>=");cOperators.add("->");cOperators.add(".");cOperators.add("?");cOperators.add(":");

        // Add C-language punctuations to the ArrayList
        cPunctuation.add("(");cPunctuation.add(")");cPunctuation.add("{");cPunctuation.add("}");
        cPunctuation.add("]");cPunctuation.add(";");cPunctuation.add(",");cPunctuation.add("[");
        cPunctuation.add("'");cPunctuation.add("\"");

        // Add C-language keywords to the ArrayList
        cKeywords.add("auto");cKeywords.add("break");cKeywords.add("case");cKeywords.add("char");
        cKeywords.add("const");cKeywords.add("continue");cKeywords.add("default");cKeywords.add("do");
        cKeywords.add("double");cKeywords.add("else");cKeywords.add("enum");cKeywords.add("extern");
        cKeywords.add("float");cKeywords.add("for");cKeywords.add("goto");cKeywords.add("if");
        cKeywords.add("int");cKeywords.add("long");cKeywords.add("register");cKeywords.add("return");
        cKeywords.add("short");cKeywords.add("signed");cKeywords.add("sizeof");cKeywords.add("static");
        cKeywords.add("struct");cKeywords.add("switch");cKeywords.add("typedef");cKeywords.add("union");
        cKeywords.add("unsigned");cKeywords.add("void");cKeywords.add("volatile");cKeywords.add("while");
        cKeywords.add("inline");cKeywords.add("restrict");

        predefinedTokens.put("Operators", cOperators);
        predefinedTokens.put("Punctuations", cPunctuation);
        predefinedTokens.put("Keywords", cKeywords);
    }

    public void changePath(String path){
        reader = new cReader(path);
        tk.clear();
    }

    public void clear(){
        reader.clear();
        tk.clear();
    }

    public void tokenize() {
        tk.clear();
        Token.idCount = 1;
        ArrayList<String> id_struct = new ArrayList<>();
        ArrayList<String> lines = reader.getClines();
        boolean inBlockComment = false;
        String num_regex = "\\d+(\\.\\d+)?((e|E)(['+']|-)?\\d+)?";
        String binary_octal_hex_regex = "(((0b|0B)[0-1]++)|(0[0-7]+)|((0x|0X)[0-9a-fA-F]+))";
        String num_dataType_regex = "(ULL|LL|L|UL|F|ull|ll|l|ul|f)?";
        String bad_Identifiers = "\\d+[a-zA-Z_]+\\d*";
        Pattern tokenPattern = Pattern.compile(
                "\"(?:[^\"\\\\]|\\\\.)*\"|" +             // Match double-quoted strings
                binary_octal_hex_regex + "|" +  // Match binary, octal, decimal, and hexadecimal numbers
                "'.'|" +                    // Match single characters within single quotes
                "\"|'|" +                                                // Match bad puncatuation
                bad_Identifiers + "|" +     // Match bad variable names
                num_regex + num_dataType_regex + "|" +           // Match numbers
                "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b(\\()|" + // Match identifiers (Function)
                "\\}(?:\\s*\\w+)?\\s*;|" + // Match End Block of Struct
                "^(struct|typedef( )struct)\\s+(\\w+)\\s*[\\{|;]|" + // Match identifiers (Struct)
                "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b((?!\\()|(?!\\{))|" + // Match identifiers (Variable)
                "\\(|\\)|\\{|\\}|;|,|" +    // Match parentheses, braces, semicolon, comma
                "\\+\\+|--|==|!=|<=|>=|&&|" + // Match comparison and logical operators
                "\\-\\>|\\+\\=|\\-\\=|\\*\\=|\\/\\=|\\%\\=|\\&\\=|\\|\\=|\\|\\||" + // Match compound assignment operators
                "\\+|\\*|/|%|<|>|\\^\\=|\\<\\<\\=|\\>\\>\\=|\\>\\>\\>\\=|" + // Match arithmetic and bitwise operators
                "\\-|\\+|\\*|\\=|\\&\\&|\\|\\||\\!|\\&|\\||\\^|\\~|\\<\\<|\\>\\>|\\>\\>\\>|\\?|" + // Match miscellaneous operators
                "\\:|\\+\\+|\\-\\-|\\." // Match other operators
        );
        boolean isStruct = false;
        boolean structDataType = false;
        int lineNum = 1;
        for (String line : lines) {
            structDataType = false;
            // Skip lines starting with "#" and comments
            if (!line.trim().startsWith("#")) {
                // Match tokens using regular expression
                Matcher matcher = tokenPattern.matcher(line);
                while (matcher.find()) {
                    String token = matcher.group();
                    if (token.matches("\\}(?:\\s*\\w+)?\\s*;") && isStruct){
                        isStruct = false;
                        String filteredStr = token.replaceAll("\\s", "").replaceAll(";", "")
                                .replaceAll("}", "");
                        tk.add(new Token("Puncatuations", "}", lineNum));
                        tk.add(new Token("Puncatuations", ";", lineNum));
                        if (!filteredStr.isEmpty()){
                            id_struct.add(filteredStr);
                            tk.add(new Token("Identifiers (Struct)", filteredStr, lineNum));
                        }
                        continue;
                    }
                    if (isPunctuation(token)) {
                        tk.add(new Token("Puncatuations", token, lineNum));
                        // Disable Struct Data Type Flag
                        if (token.equals(";"))
                            structDataType = false;
                    } else if (isOperator(token)) {
                        tk.add(new Token("Operators", token, lineNum));
                    } else if (token.matches(binary_octal_hex_regex + "|" + num_regex + num_dataType_regex)) {
                        if (token.matches(num_regex + "(ul|UL|l|L)")) {
                            tk.add(new Token("Long", token, lineNum));
                        } else if (token.matches(num_regex + "(ull|ULL|ll|LL)")) {
                            tk.add(new Token("Long Long", token, lineNum));
                        } else if (token.matches(binary_octal_hex_regex) ||
                                token.matches("(0|([1-9][0-9]*))")){
                            tk.add(new Token("Integers", token, lineNum));
                        } else{
                            tk.add(new Token("Floats", token, lineNum));
                        }
                    } else if (token.startsWith("'") && token.endsWith("'")) {
                        tk.add(new Token("Characters", token, lineNum));
                    } else if (token.startsWith("\"") && token.endsWith("\"")) {
                        tk.add(new Token("Strings", token, lineNum));
                    } else if (isKeyword(token)) {
                        if (token.equals("struct")) {
                            isStruct = true;
                        }
                        tk.add(new Token("Keywords", token, lineNum));
                    } else {
                        if (id_struct.contains(token) && !structDataType){
                            id_struct.add(token);
                            tk.add(new Token("Identifiers (Struct)", token, lineNum));
                            structDataType = true;
                        }
                        // Check if the token is a function or a variable
                        else if (isFunction(token)) {
                            token = token.replaceFirst("\\(", "");
                            tk.add(new Token("Puncatuations", "(", lineNum));
                            if (isKeyword(token)) {
                                tk.add(new Token("Keywords", token, lineNum));
                            } else {
                                tk.add(new Token("Identifiers (Function)", token, lineNum));
                            }
                        } else if (isStruct(token)) {
                            isStruct = true;
                            token = token.replaceFirst("typedef", "");
                            tk.add(new Token("Keywords", "typedef", lineNum));

                            token = token.replaceFirst("struct", "");
                            tk.add(new Token("Keywords", "struct", lineNum));

                            token = token.replaceAll("\\s", "");
                            while(!token.equals(token.replaceFirst("\\{", ""))){
                                token = token.replaceFirst("\\{", "");
                                tk.add(new Token("Puncatuations", "{", lineNum));
                            }
                            while(!token.equals(token.replaceFirst(";", ""))){
                                token = token.replaceFirst(";", "");
                                tk.add(new Token("Puncatuations", ";", lineNum));
                            }
                            id_struct.add(token);
                            tk.add(new Token("Identifiers (Struct)", token, lineNum));

                        } else {
                            if (token.contains("}")) {
                                token = token.replaceFirst("}", "");
                                tk.add(new Token("Puncatuations", "}", lineNum));
                            }
                            if (token.contains(";")) {
                                token = token.replaceFirst(";", "");
                                tk.add(new Token("Puncatuations", ";", lineNum));
                            }
                            if (token.matches(bad_Identifiers)){
                                tk.add(new Token("Bad Identifiers", token, lineNum));
                            } else {
                                tk.add(new Token("Identifiers (Variable)", token, lineNum));
                            }
                        }
                    }
                }
            }
            lineNum++;
        }
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
        for (Token token : tk) {
            token.displayToken(); // Call displayToken on each Token
        }
    }

    public ArrayList<Token> gtTokens(){
        return tk;
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
