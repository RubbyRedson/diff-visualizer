package com.gureev.nd.diff;

import difflib.Chunk;
import difflib.Delta;
import javafx.scene.text.Text;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nick on 3/21/2017.
 */
public class TestFileComparator {
    @Test
    public void testDeltas() throws IOException {
        Path first = Paths.get("./files/foo.txt");
        Path second = Paths.get("./files/bar.txt");

        FileComparator fileComparator = new FileComparator(first.toFile(), second.toFile());
        List<Delta> deltas = fileComparator.getDeltas();

        assertEquals("Unexpected number of deltas", 3, deltas.size());

        int inserts = 0;
        int changes = 0;
        int deletes = 0;

        for (Delta delta : deltas) {
            switch (delta.getType()) {
                case INSERT:
                    inserts++;
                    break;
                case CHANGE:
                    changes++;
                    break;
                case DELETE:
                    deletes++;
                    break;
            }
        }
        assertEquals("Unexpected number of deletes", 1, deletes);
        assertEquals("Unexpected number of inserts", 1, inserts);
        assertEquals("Unexpected number of changes", 1, changes);
    }
}
