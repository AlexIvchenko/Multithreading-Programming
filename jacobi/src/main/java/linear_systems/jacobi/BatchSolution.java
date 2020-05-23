package linear_systems.jacobi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class BatchSolution {
    private final double[] x;
    private final int offset;
    private final int size;
    private final long elapsedTime;
}
