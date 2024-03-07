package com.example.lexer_and_parser;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
    protected void initialize() {
        // Add listener to the codeTextArea text property
        codeTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers(newValue);
        });
        codeTextArea.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
            lineNumberPane.setScrollTop(newValue.doubleValue());
        });

    }

    @FXML
    protected void onTokenizeButtonClick(){
        if (codeTextArea.getText().isEmpty()) {
            return;
        }
        mainContainer.getChildren().clear();
        lexer.tokenize();
        ArrayList<Token> tokens = lexer.gtTokens();
        tokens.sort(Comparator.comparing(Token::getName)); // Sort tokens by name
        HashMap<String, VBox> tokenTables = new HashMap<>();

        for (Token token : tokens) {
            String name = token.getName();

            // Create label if not already created
            if (!tokenTables.containsKey(name)) {
                VBox vbox = new VBox();
                Label label = new Label(name + ":");
                label.setFont(new Font(16));
                label.setStyle("-fx-font-weight: bold; -fx-underline: true;" +
                            "-fx-font-family: 'Arial'; -fx-text-fill: #000000;-fx-font-size: 16px;");
                label.setPadding(new javafx.geometry.Insets(0, 0, 7, 0));
                vbox.getChildren().add(label);
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
                TableColumn<Token, String> attributeColumn = new TableColumn<>("Attribute Value");
                attributeColumn.setCellValueFactory(new PropertyValueFactory<>("attrVal"));
                TableColumn<Token, Integer> lineNumberColumn = new TableColumn<>("Line Number");
                lineNumberColumn.setCellValueFactory(new PropertyValueFactory<>("lineNum"));
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
                vbox.getChildren().add(tableView);
            }

            // Populate table with token details
            tableView.getItems().add(token);
        }

        // Re-sort the tokens after the UI elements are populated
        tokens.sort(Comparator.comparing(Token::getName));

        // Add all created VBox containers to the main layout
        // Assuming you have a VBox in your FXML file with fx:id="mainContainer"
        mainContainer.getChildren().addAll(tokenTables.values());
    }

    @FXML
    protected void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select .c File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("C files (*.c)", "*.c");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File("src/main/C"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            lexer = new Lexer(file.getAbsolutePath());
            try {
                String content = loadFileContent(file);
                codeTextArea.setText(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onClearButtonClick() {
        codeTextArea.clear();
        updateLineNumbers("");
        mainContainer.getChildren().clear();
        lexer.clear();
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