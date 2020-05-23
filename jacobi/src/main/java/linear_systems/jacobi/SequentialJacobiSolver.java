package linear_systems.jacobi;

public final class SequentialJacobiSolver implements JacobiSolver {
    private final double[][] coefficients;

    public SequentialJacobiSolver(final double[][] coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public void run(final double[] x) {
        int n = coefficients.length;
        BatchSolution solution = new JacobiBatchProcessorImpl(coefficients, 0, n).runIterationOnBatch(x);
        System.arraycopy(solution.getX(), 0, x, 0, n);
    }
}
