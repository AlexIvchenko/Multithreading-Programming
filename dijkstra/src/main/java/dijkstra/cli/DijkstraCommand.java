package dijkstra.cli;

import dijkstra.Dijkstra;
import dijkstra.graph.Graph;
import dijkstra.graph.IoUtils;
import dijkstra.misc.Mode;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

@CommandLine.Command(name = "dijkstra", mixinStandardHelpOptions = true)
public final class DijkstraCommand implements Callable<Integer> {
    @CommandLine.Option(names = "--input", description = "input file with numbers to sort", required = true)
    private File inputFile;
    @CommandLine.Option(names = "--output", description = "output file for sorted numbers", required = true)
    private File outputFile;
    @CommandLine.Option(names = "--start", description = "number of start node", defaultValue = "0")
    private int startNode;
    @CommandLine.Option(names = "--mode",
            description = "format of output statistics, valid values: ${COMPLETION-CANDIDATES}",
            defaultValue = "PARALLEL_LONG_PHASER_4")
    private Mode mode;

    @Override
    public Integer call() throws Exception {
        ForkJoinPool pool = new ForkJoinPool(mode.getParallelism());
        Graph graph = IoUtils.readGraph(inputFile);
        Dijkstra dijkstra = mode.create(graph, startNode, pool);
        int[] distances = dijkstra.call();
        IoUtils.writeDistances(outputFile, distances);
        return 0;
    }
}
