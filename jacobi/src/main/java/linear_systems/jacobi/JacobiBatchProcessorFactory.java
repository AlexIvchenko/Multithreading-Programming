package linear_systems.jacobi;

import linear_systems.LinearSystem;

public interface JacobiBatchProcessorFactory {
    JacobiBatchProcessor create(final LinearSystem system, int offset, int batchSize);
}
