package dijkstra;

import dijkstra.graph.AccessCountingGraph;
import dijkstra.graph.Graph;
import dijkstra.parallel.long_task.LongTaskParallelDijkstra;
import dijkstra.parallel.long_task.PhaserExchanger;
import dijkstra.sequential.SequentialDijkstra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Test {
    private static final Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
//        int[] sizes = new int[]{100, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
        int[] sizes = new int[]{10000};
        int retries = 10;
        int parallelism = 6;
        List<Stats> seqStats = new ArrayList<>();
        List<Stats> parStats = new ArrayList<>();
        ForkJoinPool executor = new ForkJoinPool(parallelism);
        for (int size : sizes) {
//            Graph delegate = Graph.linear(size);
//            Graph delegate = Graph.randomOrderedPositiveWeighGraph(size);
            Graph delegate = Graph.quadratic(size);
            AccessCountingGraph graph1 = new AccessCountingGraph(delegate);
            AccessCountingGraph graph2 = new AccessCountingGraph(delegate);
            long minSeq = Long.MAX_VALUE;
            long minPar = Long.MAX_VALUE;
            for (int i = 0; i < retries; i++) {
                SequentialDijkstra sequential = new SequentialDijkstra(graph1, 0);
                LongTaskParallelDijkstra parallel = new LongTaskParallelDijkstra(graph2, executor, PhaserExchanger.factory(), 0, true);
                LOG.info("Seq {} #{}", size, i);
                long seq = benchmark(sequential);
                seqStats.add(sequential.getStats());
                LOG.info("Par {} #{}", size, i);
                long par = benchmark(parallel);
                parStats.add(parallel.getStats());
                if (seq < minSeq) {
                    minSeq = seq;
                }
                if (par < minPar) {
                    minPar = par;
                }

            }
            LOG.debug("seq: {}ms", TimeUnit.NANOSECONDS.toMillis(minSeq));
            LOG.debug("par: {}ms, speedup: {}", TimeUnit.NANOSECONDS.toMillis(minPar), 1.0 * minSeq / minPar);

            Stats seqStat = Stats.min(seqStats);
            Stats parStat = Stats.min(parStats);

            LOG.info("seq(acc): {}", graph1.getAccesses());
            LOG.info("par(acc): {}", graph2.getAccesses());
            LOG.info("seq(min): {}", seqStat.getFindMinDuration(TimeUnit.NANOSECONDS));
            LOG.info("par(min): {}", parStat.getFindMinDuration(TimeUnit.NANOSECONDS));
            LOG.info("seq(rel): {}", seqStat.getRelaxDuration(TimeUnit.NANOSECONDS));
            LOG.info("par(rel): {}", parStat.getRelaxDuration(TimeUnit.NANOSECONDS));
            LOG.info("par(ove): {}", parStat.getOverheadDuration(TimeUnit.NANOSECONDS));
        }
    }

    private static long benchmark(Dijkstra dijkstra) {
        long start = System.nanoTime();
        dijkstra.call();
        long end = System.nanoTime();
        return end - start;
    }
}
