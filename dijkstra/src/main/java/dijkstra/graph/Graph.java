package dijkstra.graph;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntBinaryOperator;

public interface Graph {
    static Graph randomOrderedPositiveWeighGraph(int size) {
        int[][] graph = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boolean edge = ThreadLocalRandom.current().nextInt(10) >= 3;
                if (edge) {
                    int weight = ThreadLocalRandom.current().nextInt(1, 100);
                    graph[i][j] = weight;
                } else {
                    graph[i][j] = -1;
                }
            }
        }
        return Graph.of(graph);
    }

    static Graph quadratic(int size) {
        return generate(size, (from, to) -> {
//            LockSupport.parkNanos(10);
            return (to - from) * (to - from);
        });
    }

    static Graph linear(int size) {
        return generate(size, (from, to) -> Math.abs(to - from));
    }

    static Graph generate(int size, IntBinaryOperator generator) {
        return new Graph() {
            @Override
            public int getNodes() {
                return size;
            }

            @Override
            public int getDistance(final int from, final int to) {
                return generator.applyAsInt(from, to);
            }
        };
    }

    static Graph of(int[][] graph) {
        return new Graph() {
            @Override
            public int getNodes() {
                return graph.length;
            }

            @Override
            public int getDistance(final int from, final int to) {
                return graph[from][to];
            }
        };
    }

    int getNodes();

    int getDistance(int from, int to);
}
