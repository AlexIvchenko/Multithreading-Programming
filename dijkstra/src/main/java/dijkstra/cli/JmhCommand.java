package dijkstra.cli;

import dijkstra.jmh.Benchmarks;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "jmh", mixinStandardHelpOptions = true)
public final class JmhCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .build();
        new Runner(opt).run();
        return 0;
    }
}
