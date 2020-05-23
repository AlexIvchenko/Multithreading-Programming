package linear_systems.cluster;

import linear_systems.LinearSystem;
import linear_systems.jacobi.BatchSolution;
import linear_systems.jacobi.JacobiSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Leader implements JacobiSolver {
    private static final Logger LOG = LoggerFactory.getLogger(Leader.class);
    private final Cluster cluster;
    private final LinearSystem system;
    private final double eps;
    private final int minIterations;
    private final int maxIterations;
    private final double[] previousX;
    private double[] x;
    private double delta;

    public Leader(final Cluster cluster,
                  final LinearSystem system,
                  final double eps, final int minIterations, final int maxIterations) {
        this.cluster = cluster;
        this.system = system;
        this.x = new double[system.getSize()];
        this.previousX = new double[system.getSize()];
        this.eps = eps;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
    }

    @Override
    public void run(final double[] x) {
        LOG.debug("vector x size: {}", x.length);
        this.x = x;
        int iteration = 0;
        do {
            LOG.debug("iteration #{}", iteration);
            assign();
            updateX();
            collectSolutions();
            computeDelta();
            prepareForNewIteration();
            iteration++;
        } while ((iteration < minIterations) || (delta > eps && iteration < maxIterations));
        LOG.info("x = {}", Arrays.toString(x));
        terminate();
    }

    private void assign() {
        int workers = cluster.size();
        int batchSize = system.getSize() / workers;
        LOG.debug("batch size: {}", batchSize);
        if (workers * batchSize < this.system.getSize()) {
            batchSize++;
        }
        for (Member member : cluster.getAllMembers()) {
            int offset = member.getId() * batchSize;
            int currentBatchSize = Math.min(batchSize, system.getSize() - offset);
            if (currentBatchSize > 0) {
                LOG.debug("Assigning worker {} to offset={}, size={}", member.getId(), offset, currentBatchSize);
                member.assign(offset, currentBatchSize);
            }
        }
    }

    private void updateX() {
        for (Member member : cluster.getAllMembers()) {
            LOG.debug("Sending new x vector to {}", member.getId());
            member.updateX(x);
        }
    }

    private void collectSolutions() {
        List<BatchSolution> solutions = new ArrayList<>();
        for (Member member : cluster.getAllMembers()) {
            LOG.debug("Waiting for solution from {}", member.getId());
            solutions.add(member.waitForSolution());
        }
        for (BatchSolution solution : solutions) {
            System.arraycopy(solution.getX(), 0, x, solution.getOffset(), solution.getSize());
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

    private void terminate() {
        for (Member member : cluster.getAllMembers()) {
            LOG.debug("Terminating {}", member.getId());
            member.terminate();
        }
    }
}
