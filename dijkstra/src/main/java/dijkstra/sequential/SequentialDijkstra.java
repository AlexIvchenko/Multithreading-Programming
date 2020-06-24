package dijkstra.sequential;

import dijkstra.Dijkstra;
import dijkstra.NanoStats;
import dijkstra.Stats;
import dijkstra.graph.Graph;

import static dijkstra.DijkstraOperations.findMin;
import static dijkstra.DijkstraOperations.globalRelax;

public final class SequentialDijkstra implements Dijkstra {
    private final Graph graph;
    private final boolean[] visited;
    private final int[] distances;
    private Stats stats;

    public SequentialDijkstra(final Graph graph, final int startVertex) {
        this.graph = graph;
        this.distances = new int[graph.getNodes()];
        this.visited = new boolean[graph.getNodes()];
        for (int vertex = 0; vertex < graph.getNodes(); vertex++) {
            this.distances[vertex] = Integer.MAX_VALUE;
        }
        this.distances[startVertex] = 0;
    }

    @Override
    public int[] call() {
        long findMinDuration = 0;
        long relaxDuration = 0;
        int nodes = graph.getNodes();
        for (int i = 0; i < nodes; i++) {
            long findMinStartTime = System.nanoTime();
            int min = findMin(visited, distances);
            if (min == -1 || distances[min] == Integer.MAX_VALUE) {
                break;
            }
            int distance = distances[min];
            long findMinEndTime = System.nanoTime();
            findMinDuration += findMinEndTime - findMinStartTime;
            globalRelax(graph, distances, visited, min, distance);
            relaxDuration += System.nanoTime() - findMinEndTime;
        }
        this.stats = new NanoStats(findMinDuration, 0, relaxDuration);
        return distances;
    }

    @Override
    public Stats getStats() {
        return stats;
    }
}
