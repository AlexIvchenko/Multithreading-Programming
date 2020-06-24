package dijkstra.misc;

import dijkstra.Dijkstra;
import dijkstra.graph.Graph;
import dijkstra.parallel.long_task.BarrierPerIterationExchanger;
import dijkstra.parallel.long_task.ExchangerFactory;
import dijkstra.parallel.long_task.LongTaskParallelDijkstra;
import dijkstra.parallel.long_task.PhaserExchanger;
import dijkstra.parallel.short_task.ShortTaskParallelDijkstra;
import dijkstra.sequential.SequentialDijkstra;

import java.util.concurrent.ForkJoinPool;

public enum Mode {
    SEQUENTIAL(1, ParallelismType.SEQUENTIAL, null),
    PARALLEL_SHORT_2(2, ParallelismType.SHORT_TASK, null),
    PARALLEL_SHORT_4(4, ParallelismType.SHORT_TASK, null),
    PARALLEL_SHORT_6(6, ParallelismType.SHORT_TASK, null),
    PARALLEL_LONG_PHASER_2(2, ParallelismType.LONG_TASK, PhaserExchanger.factory()),
    PARALLEL_LONG_PHASER_4(4, ParallelismType.LONG_TASK, PhaserExchanger.factory()),
    PARALLEL_LONG_PHASER_6(6, ParallelismType.LONG_TASK, PhaserExchanger.factory()),
    PARALLEL_LONG_BARRIER_PER_ITERATION_2(2, ParallelismType.LONG_TASK, BarrierPerIterationExchanger.factory()),
    PARALLEL_LONG_BARRIER_PER_ITERATION_4(2, ParallelismType.LONG_TASK, BarrierPerIterationExchanger.factory()),
    PARALLEL_LONG_BARRIER_PER_ITERATION_6(2, ParallelismType.LONG_TASK, BarrierPerIterationExchanger.factory());

    private final int parallelism;
    private final ParallelismType type;
    private final ExchangerFactory exchangers;

    Mode(final int parallelism, final ParallelismType type, ExchangerFactory exchangers) {
        this.parallelism = parallelism;
        this.type = type;
        this.exchangers = exchangers;
    }

    public Dijkstra create(Graph graph, int startNode, final ForkJoinPool pool) {
        switch (type) {
            case SEQUENTIAL:
                return new SequentialDijkstra(graph, startNode);
            case SHORT_TASK:
                return new ShortTaskParallelDijkstra(graph, startNode, pool, parallelism);
            case LONG_TASK:
                return new LongTaskParallelDijkstra(graph, pool, exchangers, startNode, false);
        }
        throw new IllegalStateException();
    }

    public int getParallelism() {
        return parallelism;
    }
}
