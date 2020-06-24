package dijkstra.parallel.long_task;

import dijkstra.DistanceToVertex;

public interface Exchanger {
    DistanceToVertex exchange(DistanceToVertex localMin, int iteration);
}
