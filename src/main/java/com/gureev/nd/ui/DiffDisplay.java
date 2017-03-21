package com.gureev.nd.ui;

import difflib.Chunk;
import difflib.Delta;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
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
    private static File target;
    private static List<Delta> deltas;
    private static final Font FONT = new Font("Courier New", 14);

    private static final Color INSERTED = GREEN;
    private static final Color CHANGED = OLIVE;
    private static final Color DELETED = DARKRED;

    public static void setParameters(File sourceFile, File targetFile, List<Delta> delta) {
        source = sourceFile;
        target = targetFile;
        deltas = delta;
    }

    private List<Text> formUpdatedNodes() throws FileNotFoundException {
        List<Text> result = formOldNodes(target);

        for (Delta delta : deltas) {
            switch (delta.getType()) {
                case INSERT:
                    Chunk insert = delta.getRevised();
                    for (int i = insert.getPosition(); i < insert.getPosition() + insert.size(); i++) {
                        Text node = formatInserted(insert.getLines().get(i - insert.getPosition()).toString());
                        result.set(i, node);
                    }
                    break;
                case CHANGE:
                    Chunk change = delta.getRevised();
                    for (int i = change.getPosition(); i < change.getPosition() + change.size(); i++) {
                        Text node = formatChanged(change.getLines().get(i - change.getPosition()).toString());
                        result.set(i, node);
                    }
                    break;
                case DELETE:
                    for (int i = delta.getRevised().getPosition(); i < delta.getRevised().getPosition()
                            + delta.getOriginal().size(); i++) {
                        Text node = formatDeleted(delta.getOriginal().getLines().get(i - delta.getRevised().getPosition()).toString());
                        result.add(i, node);
                    }
                    break;
            }
        }

        return result;
    }

    private List<Text> formOldNodes(File source) throws FileNotFoundException {
        List<Text> result = new ArrayList<>();
        Scanner scanner = new Scanner(source);
        while (scanner.hasNextLine()) {
            result.add(formatUnchanged(scanner.nextLine()));
        }
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

    private Text formatDeleted(String line) {
        Text text1 = new Text(line + "\n");
        text1.setFont(FONT);
        text1.setFill(DELETED);
        return text1;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        int width = (int) (primaryScreenBounds.getWidth() / 2);
        int height = (int) (primaryScreenBounds.getHeight() / 2);

        TextFlow textFlowLeft = new TextFlow();
        textFlowLeft.setPadding(new Insets(10));
        TextFlow textFlowRight = new TextFlow();
        textFlowRight.setPadding(new Insets(10));

        List<Text> original = formOldNodes(source);
        List<Text> updated = formUpdatedNodes();

        original = addLineNumber(original);
        updated = addLineNumber(updated);

        textFlowLeft.getChildren().addAll(original);
        textFlowRight.getChildren().addAll(updated);

        textFlowLeft.setPrefSize((width - 20) / 2, height / 2);
        textFlowRight.setPrefSize((width - 20) / 2, height / 2);

        ScrollPane left = new ScrollPane(textFlowLeft);
        ScrollPane right = new ScrollPane(textFlowRight);
        left.setFitToHeight(true);
        left.setFitToWidth(true);
        right.setFitToHeight(true);
        right.setFitToWidth(true);

        HBox hBox = new HBox();
        hBox.getChildren().add(left);
        hBox.getChildren().add(right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        Scene scene = new Scene(hBox, width, height, Color.LIGHTGRAY);
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