package linear_systems.cluster.mpi;

import linear_systems.LinearSystem;
import linear_systems.cluster.Assignment;
import linear_systems.cluster.Participant;
import linear_systems.jacobi.BatchSolution;
import mpi.MPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MpiParticipant implements Participant {
    private static final Logger LOG = LoggerFactory.getLogger(MpiParticipant.class);
    private final int rank;
    private final LinearSystem system;

    public MpiParticipant(final int rank, final LinearSystem system) {
        this.rank = rank;
        this.system = system;
    }

    @Override
    public int getId() {
        return rank;
    }

    @Override
    public boolean isLeader() {
        return rank == 0;
    }

    @Override
    public Assignment waitForAssignment() {
        try {
            int[] assignment = new int[2];
            MPI.COMM_WORLD.Recv(assignment, 0, 2, MPI.INT, 0, MessageTag.ASSIGN.getIntValue());
            return new Assignment(rank, assignment[0], assignment[1]);
        } catch (RuntimeException e) {
            LOG.error("Could not receive assignment", e);
            throw e;
        }
    }

    @Override
    public double[] waitForNewX() {
        double[] x = new double[system.getSize()];
        MPI.COMM_WORLD.Recv(x, 0, system.getSize(), MPI.DOUBLE, 0, MessageTag.ITERATE.getIntValue());
        return x;
    }

    @Override
    public void submit(final BatchSolution solution) {
        double[] x = solution.getX();
        MPI.COMM_WORLD.Send(x, 0, x.length, MPI.DOUBLE, 0, MessageTag.DONE.getIntValue());
        MPI.COMM_WORLD.Send(new long[]{solution.getElapsedTime()}, 0, 1, MPI.LONG, 0, MessageTag.STATS.getIntValue());
    }
}
