package linear_systems.cluster;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class Assignment {
    private final int id;
    private final int offset;
    private final int batchSize;
}
