package quicksort;

import picocli.CommandLine;
import quicksort.cli.GenerateCommand;
import quicksort.cli.MpiCommand;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {
        GenerateCommand.class,
        MpiCommand.class,
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
