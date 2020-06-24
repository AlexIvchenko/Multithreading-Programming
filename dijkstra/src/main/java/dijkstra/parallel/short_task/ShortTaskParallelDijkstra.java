package dijkstra.parallel.short_task;

import dijkstra.Dijkstra;
import dijkstra.DistanceToVertex;
import dijkstra.NanoStats;
import dijkstra.Stats;
import dijkstra.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicReference;

public final class ShortTaskParallelDijkstra implements Dijkstra {
    private static final Logger LOG = LoggerFactory.getLogger(ShortTaskParallelDijkstra.class);
    private final Graph graph;
    private final int startVertex;
    private final ForkJoinPool executor;
    private final int parallelism;
    private Stats stats;

    public ShortTaskParallelDijkstra(final Graph graph, final int startVertex) {
        this(graph, startVertex, ForkJoinPool.commonPool(), ForkJoinPool.getCommonPoolParallelism());
    }
    public ShortTaskParallelDijkstra(final Graph graph, final int startVertex, final ForkJoinPool executor, final int parallelism) {
        this.graph = graph;
        this.startVertex = startVertex;
        this.executor = executor;
        this.parallelism = parallelism;
    }

    @Override
    public int[] call() {
        long findMinDuration = 0;
        long relaxDuration = 0;
        int[] result;
        int vertexes = graph.getNodes();

        long stepStartTime;
        long stepEndTime;
        stepStartTime = System.nanoTime();
        int jobSize = vertexes / parallelism;
        if (jobSize * parallelism < vertexes) {
            jobSize++;
        }
        int offset = 0;
        List<Processor> processors = new ArrayList<>();
        AtomicReference<DistanceToVertex> relaxTo = new AtomicReference<>(null);
        for (int i = 0; i < parallelism; i++) {
            int currentJobSize = Math.min(jobSize, vertexes - offset);
            if (currentJobSize > 0) {
                processors.add(new Processor(graph, offset, offset + currentJobSize, startVertex, relaxTo));
                offset += currentJobSize;
            }
        }
        stepEndTime = System.nanoTime();
        List<ForkJoinTask<DistanceToVertex>> tasks = new ArrayList<>();
        for (Processor processor : processors) {
            tasks.add(processor.asFindLocalMinTask());
        }
        FindMinTask findMinTask = new FindMinTask(tasks);
        RelaxTask relaxTask = new RelaxTask(processors);
        for (int i = 0; i < vertexes; i++) {
            stepStartTime = System.nanoTime();
            findMinTask.reinitialize();
            DistanceToVertex distanceToVertex;
            executor.submit(findMinTask);
            stepStartTime = stepEndTime;
            distanceToVertex = findMinTask.join();
            if (distanceToVertex == null || distanceToVertex.getDistance() == Integer.MAX_VALUE) {
                break;
            }
            stepEndTime = System.nanoTime();
            findMinDuration += stepEndTime - stepStartTime;
            stepStartTime = stepEndTime;
            relaxTo.set(distanceToVertex);
            relaxTask.reinitialize();
            executor.submit(relaxTask);
            stepEndTime = System.nanoTime();
            stepStartTime = stepEndTime;
            relaxTask.join();
            stepEndTime = System.nanoTime();
            relaxDuration += stepEndTime - stepStartTime;
        }
        stepStartTime = stepEndTime;
        result = new int[vertexes];
        for (Processor processor : processors) {
            System.arraycopy(processor.getDistances(), 0, result, processor.getVertexStartInclusive(), processor.getVertexEndExclusive() - processor.getVertexStartInclusive());
        }
        stepEndTime = System.nanoTime();
        this.stats = new NanoStats(findMinDuration, 0, relaxDuration);
        return result;
    }

    @Override
    public Stats getStats() {
        return stats;
    }
}
