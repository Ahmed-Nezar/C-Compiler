package com.example.lexer_and_parser;

import org.abego.treelayout.util.*;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

public class ParseTreeGenerator {
    private static void showInDialog(JComponent panel) {
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Ensure the dialog can be closed
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Wrap the panel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static String generateSVG() {
        // Your tree generation logic here
        // For now, let's assume you return a dummy SVG for demonstration
        return "<svg height='100' width='100'>" +
                "<circle cx='50' cy='50' r='40' stroke='black' stroke-width='3' fill='red' />" +
                "</svg>";
    }


    public static Deque<String[]> deepCopyDequeOfStringArrays(Deque<String[]> original) {
        Deque<String[]> copy = new ArrayDeque<>();
        for (String[] array : original) {
            // Create a new array and copy contents from the original array
            String[] newArray = new String[array.length];
            System.arraycopy(array, 0, newArray, 0, array.length);
            copy.add(newArray);
        }
        return copy;
    }
    public static void main(String[] args) {
        String treeName = (args.length > 0) ? args[0] : "";
        boolean boxVisible = true;

        for (String s: args) {
            if (s.equalsIgnoreCase("--nobox")) {
                boxVisible = false;
            }
        }
        Map<String, TextInBox> nodesDict = new HashMap<>();
        var grammarFile = new File("src/main/java/com/example/lexer_and_parser/grammar.txt");
        var sourceFile = new File("src/main/C/c_code(Parser).c");
        AnalysisTable aTable = new AnalysisTable(grammarFile, sourceFile);
        Deque<String[]> analysisTableClone = deepCopyDequeOfStringArrays(aTable.getTable());
        String[] currentNodes = analysisTableClone.peek()[0].split(" ");
        analysisTableClone.pop();
        TextInBox root = new TextInBox(currentNodes[1],6 * currentNodes[1].length(), 20);
        DefaultTreeForTreeLayout<TextInBox> tree;
        if (analysisTableClone.getLast()[2].equals("Syntax Error")) {
            TextInBox node = new TextInBox("Syntax Error", 6 * "Syntax Error".length(), 20);
            nodesDict.put("Syntax Error", node);
            tree = new DefaultTreeForTreeLayout<TextInBox>(node);
        }
        else {
            tree = new DefaultTreeForTreeLayout<TextInBox>(root);
            nodesDict.put(currentNodes[1], root);
            String[] previousNodes = currentNodes.clone();
            analysisTableClone.removeLast();
            while (!analysisTableClone.isEmpty()) {
                previousNodes = currentNodes.clone();
                currentNodes = analysisTableClone.peek()[0].split(" ");
                for (int i = currentNodes.length-1; i > 0; i--) {
                    if (!nodesDict.containsKey(currentNodes[i])) {
                        TextInBox node = new TextInBox(currentNodes[i],7 * currentNodes[i].length(), 20);
                        nodesDict.put(currentNodes[i], node);
                        tree.addChild(nodesDict.get(previousNodes[previousNodes.length - 1]), node);
                    }
                }
                analysisTableClone.pop();
            }
        }
        TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();
        double gapBetweenLevels = treeName.startsWith("semtab") ? 50 : 80;
        double gapBetweenNodes = 20;
        DefaultConfiguration<TextInBox> configuration = new DefaultConfiguration<TextInBox>(
                gapBetweenLevels, gapBetweenNodes);
        TreeLayout<TextInBox> treeLayout = new TreeLayout<TextInBox>(tree, nodeExtentProvider, configuration);
        TextInBoxTreePane panel = new TextInBoxTreePane(treeLayout);
//        panel.setBoxVisible(boxVisible);
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.add(panel);
//        showInDialog(panel);
    }
}
