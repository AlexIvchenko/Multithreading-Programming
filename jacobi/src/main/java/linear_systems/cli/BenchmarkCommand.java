package linear_systems.cli;

import linear_systems.LinearSystem;
import linear_systems.SolvedLinearSystem;
import linear_systems.jacobi.ConvergentJacobiSolver;
import linear_systems.jacobi.ForkJoinJacobiSolver;
import linear_systems.jacobi.JacobiSolver;
import linear_systems.jacobi.SequentialJacobiSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "benchmark", mixinStandardHelpOptions = true)
public final class BenchmarkCommand implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkCommand.class);
    @CommandLine.Option(names = "--size", defaultValue = "2000", description = "Number of equations in system")
    private int size;
    @CommandLine.Option(names = "--min", defaultValue = "0", description = "minimal number of iterations")
    private int minIterations;
    @CommandLine.Option(names = "--max", defaultValue = "1000", description = "maximal number of iterations")
    private int maxIterations;
    @CommandLine.Option(names = "--eps", defaultValue = "0")
    private double eps;

    @Override
    public Integer call() {
        SolvedLinearSystem solvedLinearSystem = SolvedLinearSystem.diagonalDominantSystem(size);
        LinearSystem system = solvedLinearSystem.getSystem();
        LOG.info("solution: {}", Arrays.toString(solvedLinearSystem.getSolution()));
        ConvergentJacobiSolver sequential = new ConvergentJacobiSolver(
                new SequentialJacobiSolver(system.getCoefficients()),
                minIterations,
                maxIterations,
                eps);
        ConvergentJacobiSolver parallel = new ConvergentJacobiSolver(
                new ForkJoinJacobiSolver(system.getCoefficients()),
                minIterations,
                maxIterations,
                eps);
        benchmark("SEQUENTIAL", system, sequential);
        benchmark(  "PARALLEL", system, parallel);
        return 0;
    }

    private void benchmark(String id, LinearSystem system, JacobiSolver solver) {
        double[] x = new double[system.getSize()];
        long start = System.nanoTime();
        solver.run(x);
        long finish = System.nanoTime();
        LOG.info(id + " elapsed time: " + TimeUnit.NANOSECONDS.toMillis(finish - start) + "ms");
        LOG.info(id + " solution: " + Arrays.toString(x));
    }
}
