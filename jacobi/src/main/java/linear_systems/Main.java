package linear_systems;


import linear_systems.cli.BenchmarkCommand;
import linear_systems.cli.GenerateCommand;
import linear_systems.cli.MpiCommand;
import linear_systems.cli.TestCommand;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {
        GenerateCommand.class,
        BenchmarkCommand.class,
        MpiCommand.class,
        TestCommand.class,
})
public class Main implements Callable<Integer> {
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Main());
        commandLine.setExecutionStrategy(new CommandLine.RunAll());
        commandLine.execute(args);
    }

    @Override
    public Integer call() {
        return 0;
    }
}
