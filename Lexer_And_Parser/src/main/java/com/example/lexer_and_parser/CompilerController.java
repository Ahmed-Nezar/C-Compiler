package com.example.lexer_and_parser;

import com.example.lexer_and_parser.osamaaboudefParser.AnalysisTable;
import com.example.lexer_and_parser.osamaaboudefParser.Reporter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CompilerController {
    private Lexer lexer;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private TextArea lineNumberPane;

    @FXML
    private TextArea codeTextArea;

    @FXML
    private VBox mainContainer;

    @FXML
    private VBox mainWindow;

    private AnalysisTable analysisTable;

    @FXML
    private ScrollPane outputScrollPane;

    @FXML
    protected void initialize() {
        // Add listener to the codeTextArea text property
        codeTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers(newValue);
        });
        codeTextArea.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
            lineNumberPane.setScrollTop(newValue.doubleValue());
        });
        // Add listener to update maximum width of mainContainer
        mainWindow.widthProperty().addListener((observable, oldValue, newValue) -> {
            // Calculate the new prefWidth based on the window width
            double newPrefWidth = newValue.doubleValue() * 0.9; // Adjust this multiplier as needed
            // Set the prefWidth of mainContainer
            mainContainer.setPrefWidth(newPrefWidth-630);
        });
    }

    @FXML
    protected void onTokenizeButtonClick(){
        // Reset horizontal scrollbar policy to default
        this.outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Reset content resizing to default
        this.outputScrollPane.setFitToWidth(false);
        this.outputScrollPane.setFitToHeight(false);
        if (codeTextArea.getText().isEmpty()) {
            return;
        }
        mainContainer.getChildren().clear();
        mainContainer.setAlignment(Pos.CENTER);
        lexer.tokenize();
        ArrayList<Token> tokens = lexer.gtTokens();
        HashMap<String, VBox> tokenTables = new HashMap<>();

        for (Token token : tokens) {
            String name = token.getName();

            // Create label if not already created
            if (!tokenTables.containsKey(name)) {
                VBox vbox = new VBox();

                // **Inner StackPane for centering**
                StackPane container = new StackPane();
                container.setAlignment(Pos.CENTER);
                container.setStyle("-fx-background-color: #2B396C; -fx-border-radius: 10 10 0 0;" +
                                   "-fx-background-radius: 10 10 0 0;" +
                                   "-fx-border-color: black; -fx-border-width: 1px 1px 0 1px;");
                container.setPadding(new Insets(4));

                Label label = new Label(name);
                label.getStyleClass().add("table-lable");
                label.setPadding(new javafx.geometry.Insets(7, 0, 7, 0));
                container.getChildren().addAll(label); // Add label to StackPane

                vbox.getChildren().addAll(container);
                tokenTables.put(name, vbox);
            }

            // Get the existing VBox for the token name
            VBox vbox = tokenTables.get(name);

            // Check if TableView already exists for the token name
            TableView<Token> tableView = null;

            ObservableList<Node> children = vbox.getChildren();
            for (Node child : children) {
                if (child instanceof TableView) {
                    tableView = (TableView<Token>) child;
                    break;
                }
            }

            // If TableView doesn't exist, create a new one
            if (tableView == null) {
                tableView = new TableView<>();
                tableView.getStyleClass().add("table-view");
                TableColumn<Token, String> attributeColumn = new TableColumn<>("Attribute Value");
                attributeColumn.setCellValueFactory(new PropertyValueFactory<>("attrVal"));
                TableColumn<Token, Integer> lineNumberColumn = new TableColumn<>("Line Number");
                lineNumberColumn.setCellValueFactory(new PropertyValueFactory<>("lineNum"));
                attributeColumn.setCellFactory(tc -> new TableCell<Token, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setAlignment(Pos.CENTER);
                        }
                    }
                });
                lineNumberColumn.setCellFactory(tc -> new TableCell<Token, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            setAlignment(Pos.CENTER);
                        }
                    }
                });
                tableView.getColumns().addAll(lineNumberColumn, attributeColumn);
                // Resize columns to fit table width
                tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                vbox.getChildren().add(tableView);
            }

            // Populate table with token details
            tableView.getItems().add(token);
        }

        // Add all created VBox containers to the main layout
        // Assuming you have a VBox in your FXML file with fx:id="mainContainer"
        mainContainer.getChildren().addAll(tokenTables.values());
    }

    @FXML
    protected void onCreateSymbolTable() {
        // Reset horizontal scrollbar policy to default
        this.outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Reset content resizing to default
        this.outputScrollPane.setFitToWidth(false);
        this.outputScrollPane.setFitToHeight(false);
        if (codeTextArea.getText().isEmpty()) {
            return;
        }
        mainContainer.getChildren().clear();
        lexer.tokenize();
        ArrayList<Token> tokens = lexer.gtTokens();

        // Filter tokens to include only those whose name contains "Identifier"
        List<Token> identifierTokens = tokens.stream()
                .filter(token -> !token.getIdentifier().isEmpty())
                .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Token::getAttrVal)
                .thenComparing(Token::getIdentifierType))),
                ArrayList::new));
        identifierTokens.sort(Comparator.comparing(Token::getEntryNum));

        // Create VBox to hold the symbol table
        VBox symbolTableContainer = new VBox();
        symbolTableContainer.setAlignment(Pos.CENTER);
        symbolTableContainer.getStyleClass().add("sTable-Vbox");

        // Create label for the symbol table
        Label label = new Label("Symbol Table");
        label.getStyleClass().add("table-lable");
        label.setPadding(new javafx.geometry.Insets(7, 0, 0, 0));
        symbolTableContainer.getChildren().add(label);

        // Create TableView for the symbol table
        TableView<Token> symbolTableView = new TableView<>();
        symbolTableView.getStyleClass().add("table-view");
        TableColumn<Token, Integer> entryColumn = new TableColumn<>("Entry");
        entryColumn.setCellValueFactory(new PropertyValueFactory<>("entryNum"));
        TableColumn<Token, String> identifierColumn = new TableColumn<>("Identifier");
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        TableColumn<Token, String> identifierTypeColumn = new TableColumn<>("Identifier Type");
        identifierTypeColumn.setCellValueFactory(new PropertyValueFactory<>("identifierType"));
        TableColumn<Token, String> attrbuteValueColumn = new TableColumn<>("Attribute Value");
        attrbuteValueColumn.setCellValueFactory(new PropertyValueFactory<>("attrVal"));
        TableColumn<Token, Integer> lineReferenceColumn = new TableColumn<>("Line Reference");
        lineReferenceColumn.setCellValueFactory(new PropertyValueFactory<>("lineNum"));
        lineReferenceColumn.setCellFactory(tc -> new TableCell<Token, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });
        identifierTypeColumn.setCellFactory(tc -> new TableCell<Token, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        identifierColumn.setCellFactory(tc -> new TableCell<Token, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        entryColumn.setCellFactory(tc -> new TableCell<Token, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });

        attrbuteValueColumn.setCellFactory(tc -> new TableCell<Token, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        VBox.setMargin(symbolTableView, new Insets(10, 0, 0, 0));

        // Add columns to the table
        symbolTableView.getColumns().addAll(entryColumn, identifierColumn, attrbuteValueColumn,
                identifierTypeColumn, lineReferenceColumn);

        // Add filtered tokens to the table
        symbolTableView.getItems().addAll(identifierTokens);

        symbolTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add the TableView to the symbol table container
        symbolTableContainer.getChildren().add(symbolTableView);
        symbolTableView.setPrefHeight(490);

        // Add the symbol table container to the main layout
        mainContainer.getChildren().add(symbolTableContainer);
    }

    @FXML
    protected void onAnalysisTableButtonClick() {

        mainContainer.getChildren().clear();
        if(analysisTable == null){
            return;
        }

        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setPadding(new Insets(10));
        String expressionStatus = analysisTable.isComplete() ? "ACCEPTED." :
                "REJECTED! (" + analysisTable.getTable().getLast()[2] +
                        ": " + analysisTable.getTable().getLast()[3] + ")";
        Label statusLabel = new Label(expressionStatus);
        statusLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 30px;");
        statusBox.getChildren().add(statusLabel);

        // Create GridPane to hold the entire table
        GridPane tableGridPane = new GridPane();
        tableGridPane.setAlignment(Pos.CENTER);

        // Create header row
        String[] columnNames = analysisTable.getColumnNames();
        for (int col = 0; col < columnNames.length; col++) {
            Label headerLabel = new Label(columnNames[col]);
            headerLabel.setStyle("-fx-background-color: #2B396C; -fx-text-fill: white;");
            headerLabel.setPadding(new Insets(5, 20, 5, 20));
            headerLabel.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(headerLabel, Priority.ALWAYS);
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            tableGridPane.add(headerLabel, col, 0);
        }

        // Create data rows
        Deque<String[]> tableData = analysisTable.getTable();
        int row = 1;
        for (String[] rowData : tableData) {
            for (int col = 0; col < rowData.length; col++) {
                Label dataLabel = new Label(rowData[col]);
                dataLabel.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0;");
                dataLabel.setPadding(new Insets(5, 20, 5, 20));
                dataLabel.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(dataLabel, Priority.ALWAYS);
                if (col == 0) {
                    dataLabel.setAlignment(Pos.CENTER_RIGHT); // Align labels in the first column to the right
                } else {
                    dataLabel.setAlignment(Pos.CENTER_LEFT);
                }
                tableGridPane.add(dataLabel, col, row);
            }
            row++;
        }




        // Set horizontal scrollbar policy to ALWAYS for the ScrollPane
        this.outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Set minimum width and height for tableContainer
        tableGridPane.setMinWidth(Region.USE_PREF_SIZE);
        tableGridPane.setMinHeight(Region.USE_PREF_SIZE);

        // Allow resizing of the content within the ScrollPane
        this.outputScrollPane.setFitToWidth(true);
        this.outputScrollPane.setFitToHeight(true);

        // Add table container to mainContainer
        mainContainer.getChildren().addAll(statusBox, tableGridPane);
    }

    @FXML
    protected void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select .c File");
        FileChooser.ExtensionFilter cFilter = new FileChooser.ExtensionFilter("C files (*.c)", "*.c");
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().addAll(cFilter, txtFilter);
        fileChooser.setInitialDirectory(new File("src/main/C"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            lexer = new Lexer(file.getAbsolutePath());
            var grammarFile = new File("src/main/java/com/example/lexer_and_parser/osamaaboudefParser/grammar.txt");

            this.analysisTable = new AnalysisTable(grammarFile, file);
            var reporter = new Reporter(this.analysisTable);
            try {
                String content = loadFileContent(file);
                codeTextArea.setText(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onResetButtonClick() {
        codeTextArea.clear();
        updateLineNumbers("");
        mainContainer.getChildren().clear();
        lexer.clear();
    }

    @FXML
    protected void onClearViewWindowClick() {
        mainContainer.getChildren().clear();
        lexer.clear();
        lexer.tokenize();
    }

    @FXML
    protected void onQuitButtonClick() {
        Platform.exit();
    }

    private String loadFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    private void updateLineNumbers(String text) {
        lineNumberPane.clear();
        if (text.isEmpty()) return;
        String[] lines = text.split("\n");
        for (int i = 1; i <= lines.length; i++) {
            Label label = new Label(Integer.toString(i + 1));
            label.setFont(new Font(13));
            lineNumberPane.appendText(i + "\n");
        }
    }
}