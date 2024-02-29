package com.example.lexer_and_parser;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController {
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