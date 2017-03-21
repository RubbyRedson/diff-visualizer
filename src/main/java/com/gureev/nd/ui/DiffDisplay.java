package com.gureev.nd.ui;

import difflib.Chunk;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
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
        for (Chunk change : changes) {
            int index = change.getPosition() - 1;
            result.remove(index);
            for (int i = change.getPosition(); i < change.getPosition() + change.size(); i++) {
                Text node = new Text();
                node.setText(change.getLines().get(i - change.getPosition()).toString() + "\n");
                node.setFont(FONT);
                node.setFill(YELLOW);
                result.add(i - 1, node);
            }
        }
        for (Chunk insert : inserts) {
            for (int i = insert.getPosition(); i < insert.getPosition() + insert.size(); i++) {
                Text node = new Text();
                node.setText(insert.getLines().get(i - insert.getPosition()).toString() + "\n");
                result.add(i, node);
                node.setFill(GREEN);
            }
        }
        for (Chunk delete : deletes) {
            Text node = result.get(delete.getPosition());
            node.setFill(RED);
        }
        return result;
    }

    private List<Text> formOldNodes() throws FileNotFoundException {
        List<Text> result = new ArrayList<>();
        Scanner scanner = new Scanner(source);
        int line = 1;
        while (scanner.hasNextLine()) {
            //result.add(formatLineNumber(line));
            result.add(formatUnchanged(scanner.nextLine()));
            line++;
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

    @Override
    public void start(Stage stage) throws Exception {
        TextFlow textFlowLeft = new TextFlow();
        textFlowLeft.setPadding(new Insets(10));
        textFlowLeft.setPrefSize((WIDTH - 20)/2, HEIGHT/2);
        TextFlow textFlowRight = new TextFlow();
        textFlowRight.setPrefSize((WIDTH - 20)/2, HEIGHT/2);
        textFlowRight.setPadding(new Insets(10));

        List<Text> original = formOldNodes();
        List<Text> updated = formUpdatedNodes();

        original = addLineNumber(original);
        updated = addLineNumber(updated);

        textFlowLeft.getChildren().addAll(original);
        textFlowRight.getChildren().addAll(updated);

        HBox hBox = new HBox();
        hBox.getChildren().add(textFlowLeft);
        hBox.getChildren().add(textFlowRight);
        Group group = new Group(hBox);
        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.LIGHTGRAY);
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