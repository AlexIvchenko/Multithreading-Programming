package linear_systems.cluster;

import linear_systems.jacobi.BatchSolution;

public interface Member {
    int getId();

    Assignment assign(int offset, int batchSize);

    void updateX(double[] x);

    BatchSolution waitForSolution();

    void terminate();
}
