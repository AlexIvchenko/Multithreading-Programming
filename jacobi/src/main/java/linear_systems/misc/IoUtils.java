package linear_systems.misc;

import java.io.*;

public class IoUtils {
    public static void writeDoubleMatrix2D(int rows, int columns, final double[][] matrix, final File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(rows + " " + columns + "\n");
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    writer.write(matrix[row][column] + " ");
                }
                writer.write("\n");
            }
        }
    }

    public static void writeDoubleVector1D(final double[] vector, final File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(vector.length + "\n");
            for (int row = 0; row < vector.length; row++) {
                writer.write(vector[row] + "\n");
            }
        }
    }

    public static double[][] readDoubleMatrix2D(final File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            String[] rowsAndColumns = line.split(" ");
            int rows = Integer.parseInt(rowsAndColumns[0]);
            int columns = Integer.parseInt(rowsAndColumns[1]);
            double[][] matrix = new double[rows][columns];
            for (int i = 0; i < rows; i++) {
                line = reader.readLine();
                String[] elements = line.split(" ");
                for (int j = 0; j < columns; j++) {
                    matrix[i][j] = Double.parseDouble(elements[j]);
                }
            }
            return matrix;
        }
    }

    public static double[] readDoubleVector1D(final File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            int size = Integer.parseInt(line);
            double[] vector = new double[size];
            for (int i = 0; i < size; i++) {
                line = reader.readLine();
                vector[i] = Double.parseDouble(line);
            }
            return vector;
        }
    }
}
