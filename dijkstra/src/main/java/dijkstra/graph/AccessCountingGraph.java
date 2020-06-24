package dijkstra.graph;

import java.util.concurrent.atomic.LongAdder;

public final class AccessCountingGraph implements Graph {
    private final Graph graph;
    private final LongAdder accesses = new LongAdder();

    public AccessCountingGraph(final Graph graph) {
        this.graph = graph;
    }

    @Override
    public int getNodes() {
        return graph.getNodes();
    }

    @Override
    public int getDistance(final int from, final int to) {
        accesses.increment();
        return graph.getDistance(from, to);
    }

    public long getAccesses() {
        return accesses.longValue();
    }
}
