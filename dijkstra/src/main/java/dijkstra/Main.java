package dijkstra;

import dijkstra.cli.DijkstraCommand;
import dijkstra.cli.JmhCommand;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {
        DijkstraCommand.class,
        JmhCommand.class,
})
public class Main implements Callable<Integer> {
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Main());
        commandLine.setExecutionStrategy(new CommandLine.RunAll());
        commandLine.execute(args);
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
