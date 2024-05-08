package com.example.lexer_and_parser;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;

import static com.example.lexer_and_parser.SVGUtil.*;

public class SVGForTextInBoxTree {
    private final TreeLayout<TextInBox> treeLayout;
    private String svgText;

    private TreeForTreeLayout<TextInBox> getTree() {
        return treeLayout.getTree();
    }

    private Iterable<TextInBox> getChildren(TextInBox parent) {
        return getTree().getChildren(parent);
    }

    private Rectangle2D.Double getBoundsOfNode(TextInBox node) {
        return treeLayout.getNodeBounds().get(node);
    }

    /**
     * @param treeLayout the {@link TreeLayout} to be rendered as SVG
     */
    public SVGForTextInBoxTree(TreeLayout<TextInBox> treeLayout) {
        this.treeLayout = treeLayout;
    }

    // -------------------------------------------------------------------
    // generating

    private void generateEdges(StringBuilder result, TextInBox parent) {
        if (!getTree().isLeaf(parent)) {
            Rectangle2D.Double b1 = getBoundsOfNode(parent);
            double x1 = b1.getCenterX();
            double y1 = b1.getCenterY();
            for (TextInBox child : getChildren(parent)) {
                Rectangle2D.Double b2 = getBoundsOfNode(child);
                result.append(line(x1, y1, b2.getCenterX(), b2.getCenterY(),
                        "stroke:black; stroke-width:2px;"));

                generateEdges(result, child);
            }
        }
    }

    private void generateBox(StringBuilder result, TextInBox textInBox) {
        // draw the box in the background
        Rectangle2D.Double box = getBoundsOfNode(textInBox);
        result.append(rect(box.x + 1, box.y + 1, box.width - 2, box.height - 2,
                "fill:orange; stroke:rgb(0,0,0);", "rx=\"10\""));

        // draw the text on top of the box (possibly multiple lines)
        String[] lines = textInBox.text.split("\n");
        int fontSize = 12;
        int x = (int) box.x + fontSize / 2 + 2;
        int y = (int) box.y + fontSize + 1;
        String style = String.format("font-family:sans-serif;font-size:%dpx;",
                fontSize);
        for (int i = 0; i < lines.length; i++) {
            result.append(text(x, y, style, lines[i]));
            y += fontSize;
        }
    }

    private String generateDiagram() {
        StringBuilder result = new StringBuilder();

        // generate the edges and boxes (with text)
        generateEdges(result, getTree().getRoot());
        for (TextInBox textInBox : treeLayout.getNodeBounds().keySet()) {
            generateBox(result, textInBox);
        }

        // generate the svg containing the diagram items (edges and boxes)
        Dimension size = treeLayout.getBounds().getBounds().getSize();
        return svg(size.width, size.height, result.toString());
    }

    /**
     * @return the rendered tree, described in SVG format.
     */
    public String getSVG() {
        if (svgText == null) {
            svgText = doc(generateDiagram());
        }
        return svgText;
    }
}