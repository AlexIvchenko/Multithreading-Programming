package linear_systems.misc;

import lombok.Getter;

@Getter
public class Dimensions {
    private final int rows;
    private final int columns;

    public Dimensions(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public static Dimensions of(double[][] matrix) {
        int rows = matrix.length;
        if (rows == 0) {
            return new Dimensions(0, 0);
        }
        int columns = matrix[0].length;
        for (int row = 0; row < rows; row++) {
            if (matrix[row].length != columns) {
                throw new IllegalArgumentException("matrix must have all rows the same length");
            }
        }
        return new Dimensions(rows, columns);
    }
}
