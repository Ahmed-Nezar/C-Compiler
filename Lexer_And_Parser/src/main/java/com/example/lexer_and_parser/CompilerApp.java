package com.example.lexer_and_parser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class CompilerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CompilerApp.class.getResource("CompilerApplication.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("lexer_icon.png"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        stage.getIcons().add(icon);
        stage.setMinWidth(1200);
        stage.setMinHeight(600);
        stage.setTitle("C Lexer & Parser");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}