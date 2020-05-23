package linear_systems.jacobi;

import linear_systems.LinearSystem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinJacobiSolverFactory implements JacobiSolverFactory {
    private final ExecutorService executorService;
    private final int parallelism;

    public ForkJoinJacobiSolverFactory() {
        this(ForkJoinPool.commonPool(), ForkJoinPool.getCommonPoolParallelism());
    }

    public ForkJoinJacobiSolverFactory(final ExecutorService executorService, final int parallelism) {
        this.executorService = executorService;
        this.parallelism = parallelism;
    }

    @Override
    public JacobiSolver create(final LinearSystem system) {
        return new ForkJoinJacobiSolver(system.getCoefficients(), executorService, parallelism);
    }
}
