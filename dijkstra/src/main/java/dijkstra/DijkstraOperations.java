package dijkstra;

import dijkstra.graph.Graph;

public class DijkstraOperations {
    public static DistanceToVertex findLocalMin(boolean[] visited, int[] distances, int offset) {
        int min = findMin(visited, distances);
        if (min == -1) {
            return null;
        }
        return new DistanceToVertex(offset + min, distances[min]);
    }

    public static int findMin(boolean[] visited, int[] distances) {
        int min = -1;
        int size = visited.length;
        for (int vertex = 0; vertex < size; vertex++) {
            if (!visited[vertex] && (min == -1 || distances[vertex] < distances[min])) {
                min = vertex;
            }
        }
        return min;
    }

    public static void globalRelax(final Graph graph, final int[] distances, final boolean[] visited,
                                   final int vertex, final int distance) {
        localRelax(graph, distances, visited, vertex, distance, 0, graph.getNodes());
    }

    public static void localRelax(final Graph graph,
                                  final int[] distances, final boolean[] visited,
                                  final int vertex, final int distance,
                                  final int offset, final int size) {
        if (offset <= vertex && vertex < offset + size) {
            visited[vertex - offset] = true;
        }
        for (int to = offset; to < offset + size; to++) {
            int distToTo = graph.getDistance(vertex, to);
            if (distToTo >= 0 && distance + distToTo < distances[to - offset]) {
                distances[to - offset] = distance + distToTo;
            }
        }
    }
}
