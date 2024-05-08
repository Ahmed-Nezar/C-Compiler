package com.example.lexer_and_parser;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.abego.treelayout.TreeLayout;

import java.awt.geom.Rectangle2D;

public class TextInBoxTreePane extends Pane {
    private TreeLayout<TextInBox> treeLayout;
    private Canvas canvas;

    public TextInBoxTreePane(TreeLayout<TextInBox> treeLayout) {
        this.treeLayout = treeLayout;
        // Set canvas size based on tree layout bounds
        Rectangle2D bounds = treeLayout.getBounds();
        this.canvas = new Canvas(bounds.getWidth(), bounds.getHeight());
        getChildren().add(canvas);
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawTree(gc);
    }

    private void drawTree(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.ORANGE);
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setFont(new javafx.scene.text.Font("Arial", 12));  // Ensure font is visible

        for (TextInBox box : treeLayout.getNodeBounds().keySet()) {
            Rectangle2D bounds = treeLayout.getNodeBounds().get(box);
            gc.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
            gc.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());

            gc.setFill(javafx.scene.paint.Color.BLACK);
            String text = box.text;

            // Using Text node for accurate measurement
            Text textNode = new Text(text);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();

            // Centering text
            double xTextStart = bounds.getX() + (bounds.getWidth() - textWidth) / 2;
            double yTextStart = bounds.getY() + (bounds.getHeight() - textHeight) / 2 + textHeight;

            gc.fillText(text, xTextStart, yTextStart);
        }
    }


    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        draw();
    }
}
