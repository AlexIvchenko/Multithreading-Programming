package io.github.alexivchenko.itmo.mtp.openmp_benchmark;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OpenmpBenchmark {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Please, specify program arguments:");
            System.err.println("  1. first matrix file path");
            System.err.println("  2. second matrix file path");
            System.err.println("  3. result matrix file path");
            System.err.println("  4. multiplier executable file path");
            System.err.println("  4. output stats csv file path");
            System.exit(1);
        }
        final String m1Path = args[0];
        final String m2Path = args[1];
        final String resultPath = args[2];
        final String executable = args[3];
        final String statsPath = args[4];
        Map<Integer, Map<Schedule, Double>> stats = new HashMap<>();
        for (int power = 16; power < 31; power++) {
            int n = 1 << (power / 3);
            int k = 1 << (power / 3);
            int m = 1 << (power - 2 * (power / 3));
            System.out.println("Setup: " + "m=" + m + ", n=" + n + ", k=" + k);
            stats.put(m * n * k, new HashMap<>());
            final int[][] m1 = generate(m, n);
            final int[][] m2 = generate(n, k);
            print(m, n, m1, new File(m1Path));
            print(n, k, m2, new File(m2Path));
            for (Schedule schedule : Schedule.values()) {
                Process process = new ProcessBuilder().command(
                        executable,
                        m1Path,
                        m2Path,
                        resultPath,
                        "5",
                        schedule.name(),
                        "-1"
                ).start();
                CompletableFuture<Double> minTime = new CompletableFuture<>();
                final Pattern minTimeOutputPattern = Pattern.compile("Min Time (.*)");

                try (BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = processStdout.readLine()) != null) {
                        System.out.println("Process output: " + line);
                        final Matcher matcher = minTimeOutputPattern.matcher(line);
                        if (matcher.matches()) {
                            minTime.complete(Double.parseDouble(matcher.group(1)));
                        }
                    }
                }
                System.out.println("Process exit code: " + process.waitFor());
                stats.get(m * n * k).put(schedule, minTime.get());
            }
        }
        try (final FileWriter statsWriter = new FileWriter(statsPath)) {
            statsWriter.write("m*n*k");
            for (Schedule schedule : Schedule.values()) {
                statsWriter.write("," + schedule.name());
            }
            statsWriter.write("\n");
            for (Map.Entry<Integer, Map<Schedule, Double>> entry : stats.entrySet()) {
                statsWriter.write(entry.getKey().toString());
                for (Schedule schedule : Schedule.values()) {
                    statsWriter.write("," + entry.getValue().get(schedule));
                }
                statsWriter.write("\n");
            }
        }
    }

    private static void print(int rows, int columns, int[][] matrix, File file) throws IOException {
        try (final FileWriter writer = new FileWriter(file)) {
            print(rows, columns, matrix, writer);
        }
    }

    private static void print(int rows, int columns, int[][] matrix, Writer writer) throws IOException {
        writer.write(rows + " " + columns + "\n");
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                writer.write(matrix[row][column] + " ");
            }
            writer.write("\n");
        }
    }

    private static int[][] generate(int rows, int columns) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        int[][] matrix = new int[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrix[row][column] = random.nextInt();
            }
        }
        return matrix;
    }
}