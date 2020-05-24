package linear_systems.cluster.mpi;

import linear_systems.cluster.Assignment;
import linear_systems.cluster.Member;

public final class MpiMember implements Member {
    private final int rank;
    private final Assignment assignment;

    public MpiMember(final int rank, final Assignment assignment) {
        this.rank = rank;
        this.assignment = assignment;
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
    public Assignment getAssignment() {
        return assignment;
    }
}
