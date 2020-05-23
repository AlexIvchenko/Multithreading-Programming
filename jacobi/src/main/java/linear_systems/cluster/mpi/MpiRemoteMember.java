package linear_systems.cluster.mpi;

import linear_systems.LinearSystem;
import linear_systems.cluster.Assignment;
import linear_systems.cluster.Member;
import linear_systems.jacobi.BatchSolution;
import mpi.MPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MpiRemoteMember implements Member {
    private static final Logger LOG = LoggerFactory.getLogger(MpiRemoteMember.class);
    private final int rank;
    private final LinearSystem system;
    private Assignment assignment;

    public MpiRemoteMember(final int rank, final LinearSystem system) {
        this.rank = rank;
        this.system = system;
        this.assignment = null;
    }

    @Override
    public int getId() {
        return rank;
    }

    @Override
    public Assignment assign(final int offset, final int batchSize) {
        try {
            MPI.COMM_WORLD.Send(new int[]{offset, batchSize}, 0, 2, MPI.INT, rank, MessageTag.ASSIGN.getIntValue());
            this.assignment = new Assignment(rank, offset, batchSize);
            return assignment;
        } catch (RuntimeException e) {
            LOG.error("Could not send assignments", e);
            throw e;
        }
    }

    @Override
    public void updateX(final double[] x) {
        MPI.COMM_WORLD.Send(x, 0, system.getSize(), MPI.DOUBLE, rank, MessageTag.ITERATE.getIntValue());
    }

    @Override
    public BatchSolution waitForSolution() {
        double[] batch = new double[assignment.getBatchSize()];
        MPI.COMM_WORLD.Recv(batch, 0, assignment.getBatchSize(), MPI.DOUBLE, rank, MessageTag.DONE.getIntValue());
        long[] elapsedTime = new long[1];
        MPI.COMM_WORLD.Recv(elapsedTime, 0, 1, MPI.LONG, rank, MessageTag.STATS.getIntValue());
        return new BatchSolution(batch, assignment.getOffset(), assignment.getBatchSize(), elapsedTime[0]);
    }

    @Override
    public void terminate() {
        assign(-1, -1);
    }
}
