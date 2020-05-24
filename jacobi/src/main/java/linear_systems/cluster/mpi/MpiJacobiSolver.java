package linear_systems.cluster.mpi;

import linear_systems.LinearSystem;
import linear_systems.cluster.Assignment;
import linear_systems.cluster.Cluster;
import linear_systems.cluster.Member;
import linear_systems.jacobi.BatchSolution;
import linear_systems.jacobi.JacobiBatchProcessor;
import linear_systems.jacobi.JacobiBatchProcessorFactory;
import linear_systems.jacobi.JacobiSolver;
import mpi.MPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MpiJacobiSolver implements JacobiSolver {
    private static final Logger LOG = LoggerFactory.getLogger(MpiJacobiSolver.class);
    private final Cluster cluster;
    private final Member current;
    private final LinearSystem system;
    private final JacobiBatchProcessor solver;
    private final double eps;
    private final int minIterations;
    private final int maxIterations;
    private final double[] previousX;
    private final double[] x;
    private double delta;
    private int iteration;

    public MpiJacobiSolver(final Cluster cluster,
                           final LinearSystem system,
                           final JacobiBatchProcessorFactory factory,
                           final double eps,
                           final int minIterations,
                           final int maxIterations) {
        this.cluster = cluster;
        this.current = cluster.getCurrent();
        this.system = system;
        Assignment assignment = cluster.getCurrent().getAssignment();
        this.solver = factory.create(system, assignment.getOffset(), assignment.getBatchSize());
        this.eps = eps;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
        this.x = new double[system.getSize()];
        this.previousX = new double[system.getSize()];
    }

    @Override
    public void run(final double[] x) {
        System.arraycopy(x, 0, this.x, 0, this.x.length);
        LOG.info("Start worker {}", current.getId());
        try {
            iteration = 0;
            do {
                runIteration();
                computeDelta();
                prepareForNewIteration();
                ++iteration;
            } while ((iteration < minIterations) || (delta > eps && iteration < maxIterations));
        } catch (RuntimeException e) {
            LOG.error("Failed", e);
        }
    }

    private void runIteration() {
        BatchSolution batchSolution = solver.runIterationOnBatch(x);
        System.arraycopy(batchSolution.getX(), 0, x, batchSolution.getOffset(), batchSolution.getSize());
        for (Member member : cluster.getAllMembers()) {
            MPI.COMM_WORLD.Bcast(x, member.getAssignment().getOffset(), member.getAssignment().getBatchSize(), MPI.DOUBLE, member.getId());
        }
    }

    public void computeDelta() {
        delta = Math.abs(x[0] - previousX[0]);
        for (int h = 0; h < system.getSize(); h++) {
            if (Math.abs(x[h] - previousX[h]) > delta) {
                delta = Math.abs(x[h] - previousX[h]);
            }
        }
        LOG.debug("delta = {}", delta);
    }

    public void prepareForNewIteration() {
        System.arraycopy(x, 0, previousX, 0, system.getSize());
    }

    public int getIterations() {
        return iteration;
    }
}
