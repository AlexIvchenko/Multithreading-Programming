package quicksort.cli;

import picocli.CommandLine;
import quicksort.IoUtils;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
public final class GenerateCommand implements Callable<Integer> {
    @CommandLine.Option(names = "--file", description = "file for  numbers", defaultValue = "input.txt")
    private File file;
    @CommandLine.Option(names = "--size", defaultValue = "10000")
    private int size;
    @CommandLine.Option(names = "--origin", defaultValue = "0")
    private int origin;
    @CommandLine.Option(names = "--bound", defaultValue = "1000000000")
    private int bound;

    @Override
    public Integer call() throws Exception {
        IoUtils.writeInts(file, ThreadLocalRandom.current().ints(size, origin, bound).toArray());
        return 0;
    }

    public static void main(String[] args) {
        new CommandLine(new GenerateCommand()).execute(args);
    }
}
