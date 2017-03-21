package com.gureev.nd;

import com.gureev.nd.diff.FileComparator;
import com.gureev.nd.ui.DiffDisplay;
import javafx.application.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Nick on 3/18/2017.
 */
public class Main {

    /**
     * Main method, receives two file pathes, compares them using diff and visualizes the output
     * @param args - should contain two valid file paths that will be compared
     * @throws IOException on file not found
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("The input must contain two paths to files that should be compared!");
        }

        Path first = Paths.get(args[0]);
        Path second = Paths.get(args[1]);

        FileComparator fileComparator = new FileComparator(first.toFile(), second.toFile());
        display(first.toFile(), second.toFile(), fileComparator);
    }

    private static void display(File source, File target, FileComparator fileComparator) throws IOException {
        DiffDisplay.setParameters(source, target, fileComparator.getDeltas());
        new Thread(() -> Application.launch(DiffDisplay.class)).start();
    }

    private static String getFileAsStringFromPath(Path pathToFile) {
        File f = pathToFile.toFile();
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(f);
            StringBuilder sb = new StringBuilder();
            while (fileScanner.hasNext()) {
                sb.append(fileScanner.next());
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The file " + f.getAbsolutePath() + " was not found!", e);
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }
    }
}
