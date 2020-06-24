package dijkstra;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public final class DistanceToVertex {
    private final int vertex;
    private final int distance;
}
