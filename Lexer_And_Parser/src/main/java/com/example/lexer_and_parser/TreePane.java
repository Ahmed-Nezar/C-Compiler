package com.example.lexer_and_parser;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.abego.treelayout.TreeLayout;

public class TreePane extends Canvas {
    private TreeLayout<TextInBox> treeLayout;

    public TreePane(TreeLayout<TextInBox> treeLayout) {
        this.treeLayout = treeLayout;
        drawTree();
    }

    private void drawTree() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Draw all edges
        for (TextInBox textInBox : treeLayout.getNodeBounds().keySet()) {
            if (!treeLayout.getTree().isLeaf(textInBox)) {
                drawEdges(gc, textInBox);
            }
        }

        // Draw all nodes
        for (TextInBox textInBox : treeLayout.getNodeBounds().keySet()) {
            drawNode(gc, textInBox);
        }
    }

    private void drawEdges(GraphicsContext gc, TextInBox parent) {
        double x1 = treeLayout.getNodeBounds().get(parent).getCenterX();
        double y1 = treeLayout.getNodeBounds().get(parent).getCenterY();
        for (TextInBox child : treeLayout.getTree().getChildren(parent)) {
            double x2 = treeLayout.getNodeBounds().get(child).getCenterX();
            double y2 = treeLayout.getNodeBounds().get(child).getCenterY();
            gc.setStroke(Color.BLACK);
            gc.strokeLine(x1, y1, x2, y2);
            drawEdges(gc, child); // Recursively draw children edges
        }
    }

    private void drawNode(GraphicsContext gc, TextInBox node) {
        gc.setFill(Color.ORANGE);
        gc.setStroke(Color.DARKGRAY);
        double x = treeLayout.getNodeBounds().get(node).getMinX();
        double y = treeLayout.getNodeBounds().get(node).getMinY();
        double width = treeLayout.getNodeBounds().get(node).getWidth();
        double height = treeLayout.getNodeBounds().get(node).getHeight();
        gc.fillRoundRect(x, y, width, height, 10, 10);
        gc.strokeRoundRect(x, y, width, height, 10, 10);

        gc.setFill(Color.BLACK);
        String[] lines = node.text.split("\n");
        Text text = new Text();
        text.setFont(new Font(12)); // You can adjust the font size
        double textY = y + 20; // Start drawing text a bit below the top edge of the box
        for (String line : lines) {
            text.setText(line);
            double textWidth = text.getLayoutBounds().getWidth();
            gc.fillText(line, x + (width - textWidth) / 2, textY); // Center the text
            textY += 15; // Move to the next line
        }
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        setWidth(width);
        setHeight(height);
        drawTree();
    }
}
