package linear_systems.jacobi;

public final class ConvergentJacobiSolver implements JacobiSolver {
    private final JacobiSolver iterativeSolver;
    private final int minIterations;
    private final int maxIterations;
    private final double eps;

    public ConvergentJacobiSolver(final JacobiSolver iterativeSolver, final int minIterations, final int maxIterations, final double eps) {
        this.iterativeSolver = iterativeSolver;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
        this.eps = eps;
    }

    @Override
    public void run(final double[] x) {
        double norm;
        int n = x.length;
        double[] tempX = new double[n];
        int iteration = 0;
        do {
            System.arraycopy(x, 0, tempX, 0, n);
            iterativeSolver.run(x);
            norm = Math.abs(x[0] - tempX[0]);
            for (int h = 0; h < n; h++) {
                if (Math.abs(x[h] - tempX[h]) > norm) {
                    norm = Math.abs(x[h] - tempX[h]);
                }
            }
            iteration++;
        } while ((iteration < minIterations) ||( norm > eps && iteration < maxIterations));
    }
}
