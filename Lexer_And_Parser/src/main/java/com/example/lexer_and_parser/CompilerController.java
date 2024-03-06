package com.example.lexer_and_parser;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CompilerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private GridPane lineNumberPane;

    @FXML
    private TextArea codeTextArea;

    @FXML
    protected void initialize() {

        codeTextArea.setOnMouseClicked((MouseEvent event) -> {
            updateLineNumbers(codeTextArea.getText());
        });

        // Add listener to the codeTextArea text property
        codeTextArea.textProperty().addListener((observable, oldValue, newValue) -> {

            updateLineNumbers(newValue);
        });
    }
    @FXML
    protected void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select .c File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("C files (*.c)", "*.c");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String content = loadFileContent(file);
                codeTextArea.setText(content);
            } catch (IOException e) {
                // Handle IO exception
                e.printStackTrace();
            }
        }
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
        lineNumberPane.getChildren().clear();
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            Label label = new Label(Integer.toString(i + 1));
            label.setFont(new Font(13));

            lineNumberPane.add(label, 0, i);
        }
    }
}