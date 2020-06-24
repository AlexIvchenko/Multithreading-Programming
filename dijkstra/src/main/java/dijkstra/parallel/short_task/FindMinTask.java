package dijkstra.parallel.short_task;

import dijkstra.DistanceToVertex;

import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public final class FindMinTask extends RecursiveTask<DistanceToVertex> {
    private final List<ForkJoinTask<DistanceToVertex>> children;

    public FindMinTask(final List<ForkJoinTask<DistanceToVertex>> children) {
        this.children = children;
    }

    @Override
    protected DistanceToVertex compute() {
        for (ForkJoinTask<DistanceToVertex> child : children) {
            child.fork();
        }
        DistanceToVertex min = null;
        for (ForkJoinTask<DistanceToVertex> child : children) {
            DistanceToVertex local = child.join();
            if (local != null && (min == null || local.getDistance() < min.getDistance())) {
                min = local;
            }
        }
        return min;
    }

    @Override
    public void reinitialize() {
        super.reinitialize();
        for (ForkJoinTask<DistanceToVertex> child : children) {
            child.reinitialize();
        }
    }
}
