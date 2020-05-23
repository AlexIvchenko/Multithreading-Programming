package linear_systems.jacobi;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public final class ForkJoinJacobiSolver implements JacobiSolver {
    private final double[][] coefficients;
    private final ExecutorService executor;
    private final int numberOfWorkers;

    public ForkJoinJacobiSolver(final double[][] coefficients) {
        this(coefficients, ForkJoinPool.commonPool(), ForkJoinPool.commonPool().getParallelism());
    }

    public ForkJoinJacobiSolver(final double[][] coefficients, final ExecutorService executor, final int workers) {
        this.coefficients = coefficients;
        this.executor = MoreExecutors.listeningDecorator(executor);
        this.numberOfWorkers = workers;
    }

    @Override
    public void run(final double[] x) {
        BatchSolution solution = new ForkJoinJacobiBatchProcessor(coefficients, executor, numberOfWorkers, 0, coefficients.length).runIterationOnBatch(x);
        System.arraycopy(solution.getX(), 0, x, 0, coefficients.length);
    }
}
