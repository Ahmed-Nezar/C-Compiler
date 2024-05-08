module com.example.lexer_and_parser {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.abego.treelayout.core;
    requires java.desktop;
    requires javafx.web;

    opens com.example.lexer_and_parser to javafx.fxml;
    exports com.example.lexer_and_parser;
}