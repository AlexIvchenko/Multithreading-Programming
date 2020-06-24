package dijkstra.parallel.long_task;

import dijkstra.Dijkstra;
import dijkstra.graph.Graph;
import dijkstra.NanoStats;
import dijkstra.Stats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class LongTaskParallelDijkstra extends RecursiveTask<int[]> implements Dijkstra {
    private static final int INIT_STATE = 0;
    private static final int RUNNING_STATE = 1;
    private static final int DONE_STATE = 2;
    private final Graph graph;
    private final Exchanger exchanger;
    private final int startNode;
    private final int parallelism;
    private final ForkJoinPool executor;
    private final Processor leader;
    private final List<Processor> processors;
    private final int[] result;
    private final AtomicInteger state = new AtomicInteger();
    private volatile Stats stats = null;
    private final boolean needStats;

    public LongTaskParallelDijkstra(final Graph graph,
                                    final ForkJoinPool executor,
                                    final int startNode) {
        this(graph, executor, PhaserExchanger.factory(), startNode, false);
    }

    public LongTaskParallelDijkstra(final Graph graph,
                                    final ForkJoinPool executor,
                                    final ExchangerFactory exchangers,
                                    final int startNode,
                                    final boolean needStats) {
        this.executor = executor;
        this.parallelism = executor.getParallelism();
        this.needStats = needStats;
        int vertexes = graph.getNodes();
        this.result = new int[vertexes];
        int jobSize = vertexes / parallelism;
        if (jobSize * parallelism < vertexes) {
            jobSize++;
        }
        int offset = 0;
        this.graph = graph;
        this.exchanger = new PhaserExchanger(parallelism);
        this.startNode = startNode;
        processors = new ArrayList<>(parallelism - 1);
        leader = createProcessor(offset, jobSize);
        offset += jobSize;
        for (int i = 1; i < parallelism; i++) {
            int currentJobSize = Math.min(jobSize, vertexes - offset);
            if (currentJobSize > 0) {
                processors.add(createProcessor(offset, currentJobSize));
                offset += currentJobSize;
            }
        }
    }

    private Processor createProcessor(final int offset, final int jobSize) {
        if (needStats) {
            return new CountingProcessor(graph, exchanger, offset, offset + jobSize, startNode);
        }
        return new Processor(graph, exchanger, offset, offset + jobSize, startNode);
    }

    @Override
    public int[] compute() {
        if (!state.compareAndSet(INIT_STATE, RUNNING_STATE)) {
            throw new IllegalStateException();
        }
        for (Processor processor : processors) {
            processor.fork();
        }
        Result result = leader.compute();
        merge(result);
        for (Processor processor : processors) {
            merge(processor.join());
        }
        if (needStats) {
            long totalFindMinDuration = 0;
            long totalOverheadDuration = 0;
            long totalRelaxDuration = 0;
            for (Processor processor : processors) {
                totalFindMinDuration += processor.getFindMinDuration();
                totalRelaxDuration += processor.getRelaxDuration();
                totalOverheadDuration += processor.getExchangeDuration();
            }
            this.stats = new NanoStats(totalFindMinDuration, totalOverheadDuration, totalRelaxDuration);
        }
        state.compareAndSet(RUNNING_STATE, DONE_STATE);
        return this.result;
    }

    @Override
    public Stats getStats() {
        if (!needStats) {
            throw new UnsupportedOperationException("Counting stats is not supported");
        }
        if (state.get() != DONE_STATE) {
            throw new IllegalStateException();
        }
        return stats;
    }

    @Override
    public int[] call() {
        return executor.submit(this).join();
    }

    private void merge(Result result) {
        System.arraycopy(result.getDistances(), 0, this.result, result.getOffset(), result.getDistances().length);
    }

    @Override
    public String toString() {
        return "ParallelDijkstra(" + parallelism + ")";
    }
}
