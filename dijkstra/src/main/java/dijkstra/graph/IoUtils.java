package dijkstra.graph;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.joining;

public final class IoUtils {
    public static Graph readGraph(final File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            int size = Integer.parseInt(line);
            int[][] matrix = new int[size][size];
            for (int i = 0; i < size; i++) {
                line = reader.readLine();
                String[] elements = line.split(" ");
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = Integer.parseInt(elements[j]);
                }
            }
            return Graph.of(matrix);
        }
    }

    public static void writeDistances(final File file, final int[] distances) throws IOException {
        String content = Arrays.stream(distances)
                .mapToObj(i -> i == Integer.MAX_VALUE ? "INF" : Integer.toString(i)).
                        collect(joining(" "));
        Files.write(file.toPath(), Collections.singleton(content), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
