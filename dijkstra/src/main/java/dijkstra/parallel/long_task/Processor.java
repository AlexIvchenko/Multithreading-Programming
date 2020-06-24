package dijkstra.parallel.long_task;

import dijkstra.DijkstraOperations;
import dijkstra.DistanceToVertex;
import dijkstra.graph.Graph;

import java.util.concurrent.RecursiveTask;

public class Processor extends RecursiveTask<Result> implements Result {
    private final Graph graph;
    private final Exchanger exchanger;
    private final int vertexStartInclusive;
    private final int vertexEndExclusive;
    private final boolean[] visited;
    private final int[] distances;

    public Processor(final Graph graph,
                     final Exchanger exchanger,
                     final int vertexStartInclusive,
                     final int vertexEndExclusive,
                     final int startVertex) {
        this.graph = graph;
        this.exchanger = exchanger;
        this.vertexStartInclusive = vertexStartInclusive;
        this.vertexEndExclusive = vertexEndExclusive;
        int size = vertexEndExclusive - vertexStartInclusive;
        this.visited = new boolean[size];
        this.distances = new int[size];
        for (int vertex = 0; vertex < size; vertex++) {
            this.distances[vertex] = Integer.MAX_VALUE;
        }
        if (vertexStartInclusive <= startVertex && startVertex < vertexEndExclusive) {
            this.distances[startVertex] = 0;
        }
    }

    @Override
    public Result compute() {
        for (int i = 0; i < graph.getNodes(); i++) {
            DistanceToVertex localMin = findLocalMin();
            DistanceToVertex globalMin = exchange(localMin, i);
            if (globalMin == null || globalMin.getDistance() == Integer.MAX_VALUE) {
                break;
            }
            relax(globalMin);
        }
        return this;
    }

    protected DistanceToVertex findLocalMin() {
        return DijkstraOperations.findLocalMin(visited, distances, vertexStartInclusive);
    }

    protected DistanceToVertex exchange(DistanceToVertex localMin, int iteration) {
        return this.exchanger.exchange(localMin, iteration);
    }

    protected void relax(DistanceToVertex globalMin) {
        int vertex = globalMin.getVertex();
        int distance = globalMin.getDistance();
        if (vertexStartInclusive <= vertex && vertex < vertexEndExclusive) {
            visited[vertex - vertexStartInclusive] = true;
        }
        for (int to = vertexStartInclusive; to < vertexEndExclusive; to++) {
            int distToTo = graph.getDistance(vertex, to);
            if (distToTo >= 0 && distance + distToTo < distances[to - vertexStartInclusive]) {
                distances[to - vertexStartInclusive] = distance + distToTo;
            }
        }
    }

    @Override
    public int getOffset() {
        return vertexStartInclusive;
    }

    @Override
    public int[] getDistances() {
        return distances;
    }

    public long getFindMinDuration() {
        throw new UnsupportedOperationException();
    }

    public long getExchangeDuration() {
        throw new UnsupportedOperationException();
    }

    public long getRelaxDuration() {
        throw new UnsupportedOperationException();
    }
}
