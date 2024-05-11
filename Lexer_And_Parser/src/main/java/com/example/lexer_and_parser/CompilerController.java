package com.example.lexer_and_parser;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
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

    private PredictiveTable predictiveTable;

    @FXML
    private ScrollPane outputScrollPane;

    @FXML
    protected void initialize() {
        codeTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers(newValue);
        });
        codeTextArea.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
            lineNumberPane.setScrollTop(newValue.doubleValue());
        });
        mainWindow.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newPrefWidth = newValue.doubleValue() * 0.9;
            mainContainer.setPrefWidth(newPrefWidth-630);
        });

        var grammarFile = new File("src/main/java/com/example/lexer_and_parser/grammar.txt");
        this.predictiveTable = new PredictiveTable(new Grammar(grammarFile));
    }

    @FXML
    protected void onTokenizeButtonClick(){
        this.outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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

            if (!tokenTables.containsKey(name)) {
                VBox vbox = new VBox();

                StackPane container = new StackPane();
                container.setAlignment(Pos.CENTER);
                container.setStyle("-fx-background-color: #2B396C; -fx-border-radius: 10 10 0 0;" +
                        "-fx-background-radius: 10 10 0 0;" +
                        "-fx-border-color: black; -fx-border-width: 1px 1px 0 1px;");
                container.setPadding(new Insets(4));

                Label label = new Label(name);
                label.getStyleClass().add("table-lable");
                label.setPadding(new javafx.geometry.Insets(7, 0, 7, 0));
                container.getChildren().addAll(label);

                vbox.getChildren().addAll(container);
                tokenTables.put(name, vbox);
            }

            VBox vbox = tokenTables.get(name);

            TableView<Token> tableView = null;

            ObservableList<Node> children = vbox.getChildren();
            for (Node child : children) {
                if (child instanceof TableView) {
                    tableView = (TableView<Token>) child;
                    break;
                }
            }
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
                tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                vbox.getChildren().add(tableView);
            }

            tableView.getItems().add(token);
        }
        mainContainer.getChildren().addAll(tokenTables.values());
    }

    @FXML
    protected void onCreateSymbolTable() {
        this.outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.outputScrollPane.setFitToWidth(false);
        this.outputScrollPane.setFitToHeight(false);
        if (codeTextArea.getText().isEmpty()) {
            return;
        }
        mainContainer.getChildren().clear();
        lexer.tokenize();
        ArrayList<Token> tokens = lexer.gtTokens();
        List<Token> identifierTokens = tokens.stream()
                .filter(token -> !token.getIdentifier().isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Token::getAttrVal)
                                .thenComparing(Token::getIdentifierType))),
                        ArrayList::new));
        identifierTokens.sort(Comparator.comparing(Token::getEntryNum));
        VBox symbolTableContainer = new VBox();
        symbolTableContainer.setAlignment(Pos.CENTER);
        symbolTableContainer.getStyleClass().add("sTable-Vbox");
        Label label = new Label("Symbol Table");
        label.getStyleClass().add("table-lable");
        label.setPadding(new javafx.geometry.Insets(7, 0, 0, 0));
        symbolTableContainer.getChildren().add(label);
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
        symbolTableView.getColumns().addAll(entryColumn, identifierColumn, attrbuteValueColumn,
                identifierTypeColumn, lineReferenceColumn);
        symbolTableView.getItems().addAll(identifierTokens);
        symbolTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        symbolTableContainer.getChildren().add(symbolTableView);
        symbolTableView.setPrefHeight(490);
        mainContainer.getChildren().add(symbolTableContainer);
    }

    @FXML
    protected void onAnalysisTableButtonClick() {

        mainContainer.getChildren().clear();

        if(codeTextArea.getText().isEmpty()){
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
        GridPane tableGridPane = new GridPane();
        tableGridPane.setAlignment(Pos.CENTER);
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
                    dataLabel.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    dataLabel.setAlignment(Pos.CENTER_LEFT);
                }
                tableGridPane.add(dataLabel, col, row);
            }
            row++;
        }
        this.outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        tableGridPane.setMinWidth(Region.USE_PREF_SIZE);
        tableGridPane.setMinHeight(Region.USE_PREF_SIZE);
        this.outputScrollPane.setFitToWidth(true);
        this.outputScrollPane.setFitToHeight(true);
        mainContainer.getChildren().addAll(statusBox, tableGridPane);
    }
    private int getColumnIndex(GridPane gridPane, String terminal) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(terminal)) {
                    return GridPane.getColumnIndex(node);
                }
            }
        }
        return -1;
    }
    private int getRowIndex(GridPane gridPane, String nonTerminal) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(nonTerminal)) {
                    return GridPane.getRowIndex(node);
                }
            }
        }
        return -1;
    }

    public static Deque<String[]> deepCopyDequeOfStringArrays(Deque<String[]> original) {
        Deque<String[]> copy = new ArrayDeque<>();
        for (String[] array : original) {
            String[] newArray = new String[array.length];
            System.arraycopy(array, 0, newArray, 0, array.length);
            copy.add(newArray);
        }
        return copy;
    }

    @FXML
    protected void onParseTree() {
        try {
            Map<String, TextInBox> nodesDict = new HashMap<>();

            if(codeTextArea.getText().isEmpty()){
                return;
            }
            Deque<String[]> analysisTableClone = deepCopyDequeOfStringArrays(this.analysisTable.getTable());

            if (analysisTableClone.isEmpty()) {
                throw new IllegalStateException("No data in the analysis table.");
            }

            String[] currentNodes = analysisTableClone.pop()[0].split(" ");
            TextInBox root = new TextInBox(currentNodes[1], 6 * currentNodes[1].length(), 20);
            DefaultTreeForTreeLayout<TextInBox> tree = new DefaultTreeForTreeLayout<>(root);
            nodesDict.put(currentNodes[1], root);

            buildTreeFromAnalysisTable(analysisTableClone, tree, nodesDict, currentNodes);

            TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();
            double gapBetweenLevels = 80;
            double gapBetweenNodes = 20;
            DefaultConfiguration<TextInBox> configuration = new DefaultConfiguration<>(gapBetweenLevels, gapBetweenNodes);
            TreeLayout<TextInBox> treeLayout = new TreeLayout<>(tree, nodeExtentProvider, configuration);

            TextInBoxTreePane treePane = new TextInBoxTreePane(treeLayout);
            ScrollPane scrollPane = new ScrollPane(treePane);
            scrollPane.setPannable(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(scrollPane);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildTreeFromAnalysisTable(Deque<String[]> analysisTableClone, DefaultTreeForTreeLayout<TextInBox> tree, Map<String, TextInBox> nodesDict, String[] currentNodes) {
        Deque<String[]> analysisTableClone2 = deepCopyDequeOfStringArrays(this.analysisTable.getTable());
        while (!analysisTableClone.isEmpty()) {
            String[] previousNodes = currentNodes.clone();
            currentNodes = analysisTableClone.pop()[0].split(" ");
            for (int i = currentNodes.length - 1; i > 0; i--) {
                if (!nodesDict.containsKey(currentNodes[i])) {
                    TextInBox node;
                    if (currentNodes[i].length() == 1) {
                        node = new TextInBox(currentNodes[i], 8 * currentNodes[i].length() + 10, 25);
                    } else {
                        node = new TextInBox(currentNodes[i], 8 * currentNodes[i].length(), 25);
                    }
                    nodesDict.put(currentNodes[i], node);
                    tree.addChild(nodesDict.get(previousNodes[previousNodes.length - 1]), node);
                }
            }
        }
        while (!analysisTableClone2.isEmpty()) {
            currentNodes = analysisTableClone2.pop()[0].split(" ");
            for (int i = currentNodes.length - 1; i > 0; i--) {
                if (tree.getChildrenList(nodesDict.get(currentNodes[i])).isEmpty() &&
                        !AnalysisTable.isTerminal(currentNodes[i])){
                    tree.getChildrenList(tree.getParent(nodesDict.get(currentNodes[i])))
                            .remove(nodesDict.get(currentNodes[i]));
                    if (nodesDict.get(currentNodes[i]) != null)
                        nodesDict.remove(currentNodes[i]);
                }
            }
        }
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
            var grammarFile = new File("src/main/java/com/example/lexer_and_parser/grammar.txt");

            this.analysisTable = new AnalysisTable(grammarFile, file);

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