package dijkstra.parallel.long_task;

import dijkstra.DistanceToVertex;
import dijkstra.NanoStats;
import dijkstra.Stats;
import dijkstra.graph.Graph;

public final class CountingProcessor extends Processor {
    private long findMinDuration = 0;
    private long exchangeDuration = 0;
    private long relaxDuration = 0;
    private volatile Stats stats;

    public CountingProcessor(final Graph graph,
                             final Exchanger exchanger,
                             final int vertexStartInclusive,
                             final int vertexEndExclusive,
                             final int startVertex) {
        super(graph, exchanger, vertexStartInclusive, vertexEndExclusive, startVertex);
    }

    @Override
    public Result compute() {
        Result result = super.compute();
        stats = new NanoStats(findMinDuration, exchangeDuration, relaxDuration);
        return result;
    }

    @Override
    protected DistanceToVertex findLocalMin() {
        long start = System.nanoTime();
        DistanceToVertex min = super.findLocalMin();
        findMinDuration += System.nanoTime() - start;
        return min;
    }

    @Override
    protected DistanceToVertex exchange(final DistanceToVertex localMin, final int iteration) {
        long exchangeStart = System.nanoTime();
        DistanceToVertex globalMin = super.exchange(localMin, iteration);
        exchangeDuration += System.nanoTime() - exchangeStart;
        return globalMin;
    }

    @Override
    protected void relax(final DistanceToVertex globalMin) {
        long start = System.nanoTime();
        super.relax(globalMin);
        relaxDuration += System.nanoTime() - start;
    }

    @Override
    public long getFindMinDuration() {
        return findMinDuration;
    }

    @Override
    public long getExchangeDuration() {
        return exchangeDuration;
    }

    @Override
    public long getRelaxDuration() {
        return relaxDuration;
    }
}
