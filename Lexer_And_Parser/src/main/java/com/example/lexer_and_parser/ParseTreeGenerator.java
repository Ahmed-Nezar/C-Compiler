package com.example.lexer_and_parser;

import org.abego.treelayout.util.*;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class ParseTreeGenerator {
    private static void showInDialog(JComponent panel) {
        JDialog dialog = new JDialog();
        Container contentPane = dialog.getContentPane();
        ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));
        contentPane.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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
        var grammarFile = new File("src/main/java/com/example/lexer_and_parser/grammar.txt");
        var sourceFile = new File("src/main/C/c_code(Parser).c");
        AnalysisTable aTable = new AnalysisTable(grammarFile, sourceFile);
        Deque<String[]> t = deepCopyDequeOfStringArrays(aTable.getTable());
        while (! t.isEmpty()) {
            String[] k = t.peek()[0].split(" ");
            System.out.println(k[k.length-1]);
            t.pop();
        }
        TextInBox root = new TextInBox("root", 40, 20);
        TextInBox n1 = new TextInBox("n1", 30, 20);
        TextInBox n1_1 = new TextInBox("n1.1\n(first node)", 80, 36);
        TextInBox n1_2 = new TextInBox("n1.2", 40, 20);
        TextInBox n1_3 = new TextInBox("n1.3\n(last node)", 80, 36);
        TextInBox n2 = new TextInBox("n2", 30, 20);
        TextInBox n2_1 = new TextInBox("n2", 30, 20);
        TextInBox hamada = new TextInBox("7amada", 30, 20);
        DefaultTreeForTreeLayout<TextInBox> tree = new DefaultTreeForTreeLayout<TextInBox>(root);
        tree.addChild(root, n1);
        tree.addChild(n1, n1_1);
        tree.addChild(n1, n1_2);
        tree.addChild(n1, n1_3);
        tree.addChild(root, n2);
        tree.addChild(n2, n2_1);
        TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();
        double gapBetweenLevels = treeName.startsWith("semtab") ? 15 : 50;
        double gapBetweenNodes = 10;
        DefaultConfiguration<TextInBox> configuration = new DefaultConfiguration<TextInBox>(
                gapBetweenLevels, gapBetweenNodes);
        TreeLayout<TextInBox> treeLayout = new TreeLayout<TextInBox>(tree, nodeExtentProvider, configuration);
        TextInBoxTreePane panel = new TextInBoxTreePane(treeLayout);
        panel.setBoxVisible(boxVisible);
        showInDialog(panel);
    }
}
