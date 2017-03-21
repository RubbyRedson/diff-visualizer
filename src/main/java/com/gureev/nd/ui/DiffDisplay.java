package com.gureev.nd.ui;

import difflib.Chunk;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static javafx.scene.paint.Color.*;


/**
 * Created by Nick on 3/21/2017.
 */
public class DiffDisplay extends Application {
    private static File source;
    private static List<Chunk> inserts, changes, deletes;
    private static final Font FONT = new Font("Courier New", 14);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 150;

    private static final Color INSERTED = GREEN;
    private static final Color CHANGED = OLIVE;
    private static final Color DELETED = DARKRED;

    private List<Text> sourceAsText;

    public static void setParameters(File sourceFile, List<Chunk> insertsChunks, List<Chunk> changesChunks,
                                     List<Chunk> deletesChunks) {
        source = sourceFile;
        inserts = insertsChunks;
        changes = changesChunks;
        deletes = deletesChunks;
    }

    private List<Text> formUpdatedNodes() {
        List<Text> result = new ArrayList<>();

        for (Text node : sourceAsText) {
            Text newNode = new Text();
            newNode.setFont(FONT);
            newNode.setText(node.getText());
            result.add(newNode);
        }

        for (Chunk insert : inserts) {
            for (int i = insert.getPosition(); i < insert.getPosition() + insert.size(); i++) {
                Text node = formatInserted(insert.getLines().get(i - insert.getPosition()).toString());
                result.add(i, node);
            }
        }

        for (Chunk change : changes) {
            result.remove(change.getPosition());
            for (int i = change.getPosition(); i < change.getPosition() + change.size(); i++) {
                Text node = formatChanged(change.getLines().get(i - change.getPosition()).toString());
                result.add(i, node);
            }
        }

        for (Chunk delete : deletes) {
            Text node = result.get(delete.getPosition());
            node.setFill(DELETED);
        }

        return result;
    }

    private List<Text> formOldNodes() throws FileNotFoundException {
        List<Text> result = new ArrayList<>();
        Scanner scanner = new Scanner(source);
        while (scanner.hasNextLine()) {
            result.add(formatUnchanged(scanner.nextLine()));
        }
        sourceAsText = result;
        return result;
    }

    private Text formatLineNumber(int lineNumber) {
        Text text1 = new Text(lineNumber + ". ");
        text1.setFont(FONT);
        text1.setFill(Color.GRAY);
        return text1;
    }

    private Text formatUnchanged(String line) {
        Text text1 = new Text(line + "\n");
        text1.setFont(FONT);
        return text1;
    }

    private Text formatInserted(String line) {
        Text text1 = new Text(line + "\n");
        text1.setFont(FONT);
        text1.setFill(INSERTED);
        return text1;
    }

    private Text formatChanged(String line) {
        Text text1 = new Text(line + "\n");
        text1.setFont(FONT);
        text1.setFill(CHANGED);
        return text1;
    }

    @Override
    public void start(Stage stage) throws Exception {
        TextFlow textFlowLeft = new TextFlow();
        textFlowLeft.setPadding(new Insets(10));
        TextFlow textFlowRight = new TextFlow();
        textFlowRight.setPadding(new Insets(10));

        List<Text> original = formOldNodes();
        List<Text> updated = formUpdatedNodes();

        original = addLineNumber(original);
        updated = addLineNumber(updated);

        textFlowLeft.getChildren().addAll(original);
        textFlowRight.getChildren().addAll(updated);

        textFlowLeft.setPrefSize((WIDTH - 20)/2, HEIGHT/2);
        textFlowRight.setPrefSize((WIDTH - 20)/2, HEIGHT/2);

        ScrollPane left = new ScrollPane(textFlowLeft);
        ScrollPane right = new ScrollPane(textFlowRight);
        left.setFitToHeight(true);
        left.setFitToWidth(true);
        right.setFitToHeight(true);
        right.setFitToWidth(true);

        HBox hBox = new HBox();
        hBox.setFillHeight(true);
        hBox.getChildren().add(left);
        hBox.getChildren().add(right);
        Scene scene = new Scene(hBox, WIDTH, HEIGHT, Color.LIGHTGRAY);
        stage.setTitle("Diff Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private List<Text> addLineNumber(List<Text> original) {
        List<Text> result = new ArrayList<>();
        int line = 1;
        for (Text node : original) {
            result.add(formatLineNumber(line));
            result.add(node);
            line++;
        }
        return result;
    }
}