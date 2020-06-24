package dijkstra;

import dijkstra.graph.Graph;
import dijkstra.parallel.short_task.ShortTaskParallelDijkstra;
import dijkstra.sequential.SequentialDijkstra;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DijkstraTest {
    @MethodSource("samples")
    @ParameterizedTest
    void baseSequentialDijkstraTest(Graph graph, int startVertex, int[] expected) {
        int[] actual = new SequentialDijkstra(graph, startVertex).call();
        assertThat(expected).containsExactly(actual);
    }

    @MethodSource("samples")
    @ParameterizedTest
    void baseLongTaskParallelDijkstraTest(Graph graph, int startVertex, int[] expected) {
        int[] actual = new SequentialDijkstra(graph, startVertex).call();
        assertThat(expected).containsExactly(actual);
    }

    @MethodSource("samples")
    @ParameterizedTest
    void baseShortTaskParallelDijkstraTest(Graph graph, int startVertex, int[] expected) {
        int[] actual = new ShortTaskParallelDijkstra(graph, startVertex).call();
        assertThat(expected).containsExactly(actual);
    }

    public static Stream<Arguments> samples() {
        return Stream.of(
                Arguments.of(
                        Graph.of(new int[][]{
                                {-1, 3, 1, 5},
                                {-1, -1, -1, 4},
                                {-1, 1, -1, 3},
                                {-1, -1, -1, -1}
                        }),
                        0,
                        new int[]{0, 2, 1, 4})
        );
    }
}