package linear_systems.jacobi;

public final class JacobiBatchProcessorImpl implements JacobiBatchProcessor {
    private final double[][] coefficients;
    private final int offset;
    private final int batchSize;

    public JacobiBatchProcessorImpl(final double[][] coefficients, final int offset, final int batchSize) {
        this.coefficients = coefficients;
        this.offset = offset;
        this.batchSize = batchSize;
    }

    @Override
    public BatchSolution runIterationOnBatch(final double[] x) {
        double[] tempX = new double[batchSize];
        int n = coefficients.length;
        for (int i = offset; i < offset + batchSize; i++) {
            tempX[i - offset] = coefficients[i][n];
            for (int g = 0; g < n; g++) {
                if (i != g) {
                    tempX[i - offset] -= coefficients[i][g] * x[g];
                }
            }
            tempX[i - offset] /= coefficients[i][i];
        }
        return new BatchSolution(tempX, offset, batchSize);
    }
}
