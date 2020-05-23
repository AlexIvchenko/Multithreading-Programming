package linear_systems.cluster;

import linear_systems.jacobi.BatchSolution;

public interface Participant {
    int getId();

    boolean isLeader();

    Assignment waitForAssignment();

    double[] waitForNewX();

    void submit(BatchSolution solution);
}
