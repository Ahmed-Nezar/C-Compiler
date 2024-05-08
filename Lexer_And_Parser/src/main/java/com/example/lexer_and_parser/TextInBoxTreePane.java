package com.example.lexer_and_parser;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.abego.treelayout.TreeLayout;

import java.awt.geom.Rectangle2D;

public class TextInBoxTreePane extends Pane {
    private TreeLayout<TextInBox> treeLayout;
    private Canvas canvas;

    public TextInBoxTreePane(TreeLayout<TextInBox> treeLayout) {
        this.treeLayout = treeLayout;
        Rectangle2D bounds = treeLayout.getBounds().getBounds();
        this.canvas = new Canvas(bounds.getWidth(), bounds.getHeight());
        getChildren().add(canvas);
        draw();
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("Arial", 13));
        drawTree(gc);
    }

    private void drawTree(GraphicsContext gc) {
        // First draw all edges
        for (TextInBox parent : treeLayout.getNodeBounds().keySet()) {
            if (!treeLayout.getTree().isLeaf(parent)) {
                Rectangle2D.Double b1 = treeLayout.getNodeBounds().get(parent);
                double x1 = b1.getCenterX();
                double y1 = b1.getCenterY();
                for (TextInBox child : treeLayout.getTree().getChildren(parent)) {
                    Rectangle2D.Double b2 = treeLayout.getNodeBounds().get(child);
                    double x2 = b2.getCenterX();
                    double y2 = b2.getCenterY();
                    gc.setStroke(Color.BLACK);  // Set a visible color for edges
                    gc.setLineWidth(2);  // Ensure line width is sufficient to be visible
                    gc.strokeLine(x1, y1, x2, y2);
                }
            }
        }

        // Then draw all nodes
        for (TextInBox node : treeLayout.getNodeBounds().keySet()) {
            drawNode(gc, node);
        }
    }

    private void drawNode(GraphicsContext gc, TextInBox node) {
        Rectangle2D.Double box = treeLayout.getNodeBounds().get(node);
        // Node color and border
        gc.setFill(Color.ORANGE);
        gc.setStroke(Color.BLACK);
        gc.fillRoundRect(box.x, box.y, box.width, box.height, 10, 10);
        gc.strokeRoundRect(box.x, box.y, box.width, box.height, 10, 10);

        // Draw text in the center of the box
        gc.setFill(Color.BLACK);
        String text = node.text;
        Text textNode = new Text(text);
        textNode.setFont(gc.getFont());
        double textWidth = textNode.getLayoutBounds().getWidth();
        double textHeight = textNode.getLayoutBounds().getHeight();
        double xTextStart = box.x + (box.width - textWidth) / 2;
        double yTextStart = box.y + (box.height + textHeight) / 2 - textHeight / 4;
        gc.fillText(text, xTextStart, yTextStart);
    }


    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        draw();
    }
}
