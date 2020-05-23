package linear_systems.jacobi;

public interface JacobiBatchProcessor {
    BatchSolution runIterationOnBatch(double[] x);
}
