package dijkstra.v2;

import dijkstra.graph.Graph;
import dijkstra.parallel.long_task.LongTaskParallelDijkstra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class Test {
    private static final Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        Graph graph = Graph.quadratic(10);
        int[] parallelisms = new int[]{3};
        for (int parallelism : parallelisms) {
            ForkJoinPool pool = new ForkJoinPool(parallelism);
            long min = Long.MAX_VALUE;
            for (int retry = 0; retry < 10; retry++) {
                LongTaskParallelDijkstra dijkstra = new LongTaskParallelDijkstra(graph, pool, 0);
                long start = System.nanoTime();
                int[] result = dijkstra.call();
                long end = System.nanoTime();
                min = Math.min(end - start, min);
                LOG.info(Arrays.toString(result));
            }
            LOG.info("Parallelism: {}, taken {}ns", parallelism, min);
        }
    }
}
