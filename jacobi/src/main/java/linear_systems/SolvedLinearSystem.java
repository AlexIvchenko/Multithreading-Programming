package linear_systems;

import lombok.Getter;
import org.ejml.simple.SimpleMatrix;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class SolvedLinearSystem {
    private final LinearSystem system;
    private final double[] solution;

    public SolvedLinearSystem(final LinearSystem system, final double[] solution) {
        this.system = system;
        this.solution = solution;
    }

    public static SolvedLinearSystem diagonalDominantSystem(int size) {
        double[][] a = generateDiagonalDominantRandom(size);
        double[][] x = generateRandom(size, 1);
        SimpleMatrix y = new SimpleMatrix(a).mult(new SimpleMatrix(x));

        double[][] coefficients = new double[size][size + 1];
        double[] solution = new double[size];

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                coefficients[row][column] = a[row][column];
            }
            coefficients[row][size] = y.get(row);
            solution[row] = x[row][0];
        }
        return new SolvedLinearSystem(new LinearSystem(coefficients),solution);
    }

    private static double[][] generateDiagonalDominantRandom(int size) {
        double[][] matrix = generateRandom(size, size);
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (column != row) {
                    matrix[row][row] += Math.abs(matrix[row][column]);
                }
            }
        }
        return matrix;
    }

    private static double[][] generateRandom(int rows, int columns) {
        double[][] matrix = new double[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrix[row][column] = ThreadLocalRandom.current().nextInt() % 10;
            }
        }
        return matrix;
    }
}
