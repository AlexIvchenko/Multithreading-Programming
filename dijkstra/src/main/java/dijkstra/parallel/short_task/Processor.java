package dijkstra.parallel.short_task;

import dijkstra.DijkstraOperations;
import dijkstra.DistanceToVertex;
import dijkstra.graph.Graph;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicReference;

import static dijkstra.DijkstraOperations.findMin;
import static dijkstra.DijkstraOperations.localRelax;

public final class Processor {
    private final Graph graph;
    private final int vertexStartInclusive;
    private final int vertexEndExclusive;
    private final boolean[] visited;
    private final int[] distances;
    private final AtomicReference<DistanceToVertex> relaxTo;

    public Processor(final Graph graph,
                     final int vertexStartInclusive,
                     final int vertexEndExclusive,
                     final int startVertex,
                     final AtomicReference<DistanceToVertex> relaxTo) {
        this.graph = graph;
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
        this.relaxTo = relaxTo;
    }

    public ForkJoinTask<DistanceToVertex> asFindLocalMinTask() {
        return new ForkJoinTask<DistanceToVertex>() {
            private volatile DistanceToVertex result = null;

            @Override
            public DistanceToVertex getRawResult() {
                return result;
            }

            @Override
            protected void setRawResult(final DistanceToVertex value) {
                result = value;
            }

            @Override
            protected boolean exec() {
                this.result = findLocalMin();
                return true;
            }
        };
    }

    public ForkJoinTask<Void> asRelaxTask() {
        return new ForkJoinTask<Void>() {
            @Override
            public Void getRawResult() {
                return null;
            }

            @Override
            protected void setRawResult(final Void value) {

            }

            @Override
            protected boolean exec() {
                relax();
                return true;
            }
        };
    }

    public DistanceToVertex findLocalMin() {
        return DijkstraOperations.findLocalMin(visited, distances, vertexStartInclusive);
    }

    public void relax() {
        DistanceToVertex distanceToVertex = relaxTo.get();
        int vertex = distanceToVertex.getVertex();
        int distance = distanceToVertex.getDistance();
        localRelax(graph, distances, visited, vertex, distance, vertexStartInclusive, vertexEndExclusive - vertexStartInclusive);
    }

    public int getVertexStartInclusive() {
        return vertexStartInclusive;
    }

    public int getVertexEndExclusive() {
        return vertexEndExclusive;
    }

    public int[] getDistances() {
        return distances;
    }
}
