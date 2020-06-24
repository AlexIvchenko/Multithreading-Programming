package dijkstra.parallel.short_task;

import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static java.util.stream.Collectors.toList;

public final class RelaxTask extends RecursiveTask<Void> {
    private final List<ForkJoinTask<Void>> children;

    public RelaxTask(final List<Processor> processors) {
        this.children = processors.stream().map(Processor::asRelaxTask)
                .collect(toList());
    }

    @Override
    protected Void compute() {
        for (ForkJoinTask<Void> child : children) {
            child.fork();
        }
        for (ForkJoinTask<Void> child : children) {
            child.join();
        }
        return null;
    }

    @Override
    public void reinitialize() {
        for (ForkJoinTask<Void> child : children) {
            child.reinitialize();
        }
        super.reinitialize();
    }
}
