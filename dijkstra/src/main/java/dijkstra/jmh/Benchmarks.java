package dijkstra.jmh;

import dijkstra.graph.Graph;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Mode.*;

//@BenchmarkMode({Throughput, AverageTime})
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(AverageTime)
@State(Scope.Benchmark)
public class Benchmarks {
    @Param(value = {"100", "500", "1000", "2000", "5000", "10000"})
    private int size;
    @Param({
            "SEQUENTIAL",
            "PARALLEL_SHORT_2",
            "PARALLEL_SHORT_4",
            "PARALLEL_SHORT_6",
            "PARALLEL_LONG_PHASER_2",
            "PARALLEL_LONG_PHASER_4",
            "PARALLEL_LONG_PHASER_6",
            "PARALLEL_LONG_BARRIER_PER_ITERATION_2",
            "PARALLEL_LONG_BARRIER_PER_ITERATION_4",
            "PARALLEL_LONG_BARRIER_PER_ITERATION_6"
    })
    private dijkstra.misc.Mode mode;
    private Graph graph;
    private ForkJoinPool pool;

    @Setup
    public void setup() {
        graph = Graph.randomOrderedPositiveWeighGraph(size);
        pool = new ForkJoinPool(mode.getParallelism());
    }

    @Benchmark
    public int[] benchmark() {
        return mode.create(graph, 0, pool).call();
    }

    public static void main(final String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
