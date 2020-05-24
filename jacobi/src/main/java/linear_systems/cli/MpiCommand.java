package linear_systems.cli;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "mpi", mixinStandardHelpOptions = true)
public final class MpiCommand implements Callable<Integer> {
    private static final Logger REDIRECT_LOG = LoggerFactory.getLogger("Redirect");
    @CommandLine.Option(names = {"-np", "processes"},
            required = true,
            description = "number of processes")
    private int processes;
    @CommandLine.Option(names = "--min",
            defaultValue = "0",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "minimal number of iterations")
    private int minIterations;
    @CommandLine.Option(names = "--max",
            defaultValue = "1000",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "maximal number of iterations")
    private int maxIterations;
    @CommandLine.Option(names = "--eps",
            defaultValue = "0.000001",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "accuracy")
    private double eps;
    @CommandLine.Option(names = "--threads", defaultValue = "1", description = {
            "0     - to run with common ForkJoinPool with optimal number of threads",
            "1     - to run without parallelism",
            "other - to run with special ExecutorService with given number of threads"
    }, showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private int threads;
    @CommandLine.Option(names = "--system", description = "file containing linear system in matrix format", required = true)
    private File linearSystemFile;
    @CommandLine.Option(names = "--init", description = "file containing initial approximations")
    private File initialApproximationsFile;
    @CommandLine.Option(names = "--solution", description = "output file for the solution", required = true)
    private File solutionFile;

    @Override
    public Integer call() throws Exception {
        List<String> command = new ArrayList<>(Arrays.asList(
                SystemUtils.IS_OS_WINDOWS ? "mpjrun.bat" : "mpjrun.sh",
                "-np", "" + processes,
                "linear_systems.MpiMain",
                "-cp", "./target/jacobi-jar-with-dependencies.jar",
                "--system", linearSystemFile.getAbsolutePath(),
                "--threads", "" + threads,
                "--eps", "" + eps,
                "--min", "" + minIterations,
                "--max", "" + maxIterations,
                "--solution", solutionFile.getAbsolutePath()
        ));
        if (initialApproximationsFile != null) {
            command.add("--init");
            command.add(initialApproximationsFile.getAbsolutePath());
        }
        Process process = new ProcessBuilder().command(command).start();
        try (BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = processStdout.readLine()) != null) {
                REDIRECT_LOG.info(line);
            }
        }
        return process.exitValue();
    }
}
