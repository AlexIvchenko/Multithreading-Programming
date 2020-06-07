package quicksort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.joining;

public final class IoUtils {
    public static int[] readInts(final File file) throws IOException {
        return Files.readAllLines(file.toPath())
                .stream()
                .flatMap(line -> Arrays.stream(line.split("\\s")))
                .mapToInt(Integer::parseInt)
                .toArray();
    }
    public static void writeInts(final File file, final int[] ints) throws IOException {
        String content = Arrays.stream(ints)
                .mapToObj(Integer::toString).
                        collect(joining(" "));
        Files.write(file.toPath(), Collections.singleton(content), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
