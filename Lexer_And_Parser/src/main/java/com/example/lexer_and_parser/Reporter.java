package com.example.lexer_and_parser;

public class Reporter {
    private AnalysisTable analysisTable;

    public Reporter(AnalysisTable analysisTable) {
        this.analysisTable = analysisTable;
    }

    public void printParsingSummary() {
        String expStr = expressionStatement();
        String rightStr = expStr.contains("REJECTED")? "│" : " ".repeat(Math.abs(154- expStr.length())) + " │";
        int countChars = Math.abs(-20 + (expStr.length() + rightStr.length()));
        System.out.println();
        System.out.println("  ┌─────────────────┐");
        System.out.println("┌─│ Parsing Summary │" + "─".repeat(countChars) + "┐");
        System.out.println("│ └─────────────────┘" + " ".repeat(countChars) + "│");
        System.out.println("│ " + expressionStatement() + rightStr);
        System.out.println("│ " + fileStatement() + " ".repeat(Math.abs(206 - fileStatement().length())) + " │");
        System.out.println("└" + "─".repeat(208) + "┘");
    }

    public void printAnalysisTable() {
        System.out.println();
        printAnalysisTable(0);
    }

    private void printAnalysisTable(int increment) {
        int[] columnSizes = {32 + increment * 2, 70 + increment, 17, 18 + increment * 4};

        try {
            System.out.println(analysisTable(columnSizes, analysisTable.getColumnNames()));
        }
        catch (Exception e) {
            printAnalysisTable(increment + 1);
        }
    }

    private String analysisTable(int[] columnSizes, String[] columnTitles) {
        return tableHeader(columnSizes, columnTitles) + "\n"
                + tableBody(columnSizes) + "\n"
                + tableFooter(columnSizes);
    }

    private String tableHeader(int[] columnSizes, String[] columnTitles) {
        var tableHeader = "";
        var totalWidth = getTotalWidth(columnSizes);

        tableHeader += "┏" + "━".repeat(totalWidth - 2) + "┓\n";
        var tableTitle = "Analysis Table for Expression: \"" + analysisTable.getSource() + "\"";
        var leftPadding = " ".repeat((totalWidth - 6 - tableTitle.length()) / 2);
        var rightPadding = " ".repeat(totalWidth - 6 - leftPadding.length() - tableTitle.length());
        tableHeader += "┃  " + leftPadding + tableTitle + rightPadding + "  ┃\n";
        tableHeader += "┡" + "━".repeat(totalWidth - 2) + "┩\n";
        columnTitles[0] = " ".repeat(columnSizes[0] - columnTitles[0].length()) + columnTitles[0];
        columnTitles[1] += " ".repeat(columnSizes[1] - columnTitles[1].length());
        columnTitles[2] += " ".repeat(columnSizes[2] - columnTitles[2].length());
        columnTitles[3] += " ".repeat(columnSizes[3] - columnTitles[3].length());
        tableHeader += "│  " + columnTitles[0] + "  │  " + columnTitles[1] + "  │  "
                + columnTitles[2] + "  │  " + columnTitles[3] + "  │\n";
        tableHeader += "├" + "─".repeat(totalWidth - 2) + "┤";

        return tableHeader;
    }

    private String tableBody(int[] columnSizes) {
        var tableBody = "";
        for (var row : analysisTable.getTable())
            tableBody += tableRow(row, columnSizes) + "\n";

        return tableBody.strip();
    }

    private String tableRow(String[] row, int[] columnSizes) {
        var formattedRow = new String[4];
        formattedRow[0] = " ".repeat(columnSizes[0] - row[0].length()) + row[0];
        formattedRow[1] = row[1] + " ".repeat(columnSizes[1] - row[1].length());
        formattedRow[2] = row[2] + " ".repeat(columnSizes[2] - row[2].length());
        formattedRow[3] = (row[3] == null) ? "" : row[3];
        formattedRow[3] += " ".repeat(columnSizes[3] - formattedRow[3].length());

        return "│  " + formattedRow[0] + "  │  " + formattedRow[1] + "  │  "
                + formattedRow[2] + "  │  " + formattedRow[3] + "  │";
    }

    private String tableFooter(int[] columnSizes) {
        var tableFooter = "";
        var totalWidth = getTotalWidth(columnSizes);
        tableFooter += "┢" + "━".repeat(totalWidth - 2) + "┪\n";
        tableFooter += tableSummary(totalWidth) + "\n";
        tableFooter += "┗" + "━".repeat(totalWidth - 2) + "┛";

        return tableFooter;
    }

    private String tableSummary(short visibleWidth) {
        var expressionStatement = expressionStatement();
        var fileStatement = fileStatement();

        var leftPadding = " ".repeat((visibleWidth - 6 - expressionStatement.length()) / 2);
        var rightPadding = " ".repeat(visibleWidth - 6 - leftPadding.length() - expressionStatement.length());
        var tableSummary = "┃  " + leftPadding + expressionStatement + rightPadding + "  ┃\n";

        leftPadding = " ".repeat((visibleWidth - 6 - fileStatement.length()) / 2);
        rightPadding = " ".repeat(visibleWidth - 6 - leftPadding.length() - fileStatement.length());
        tableSummary += "┃  " + leftPadding + fileStatement + rightPadding + "  ┃";

        return tableSummary;
    }

    public String expressionStatement() {
        var expression = analysisTable.getSource();
        var expressionStatus = analysisTable.isComplete() ? "ACCEPTED."
                : "REJECTED! (" + analysisTable.getTable().getLast()[2]
                + ": " + analysisTable.getTable().getLast()[3] + ")";

        return "Expression \"" + expression + "\" was " +  expressionStatus;
    }

    public String fileStatement() {
        var grammarFileName = analysisTable.getGrammarFile().getName();
        var sourceFileName = analysisTable.getSourceFile().getName();

        var fileStatus = analysisTable.isComplete() ? "PARSABLE" : "NOT PARSABLE";
        return "File \"" + sourceFileName + "\" is " + fileStatus +
                " using the grammar provided in \"" + grammarFileName + "\".";
    }

    private short getTotalWidth(int[] columnSizes) {
        short visibleWidth = 21;
        for (var size : columnSizes)
            visibleWidth += size;
        return visibleWidth;
    }
}
