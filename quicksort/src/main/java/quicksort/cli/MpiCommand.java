package quicksort.cli;

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
    @CommandLine.Option(names = {"-np", "--processes"},
            required = true,
            description = "number of processes")
    private int processes;
    @CommandLine.Option(names = "--input", description = "input file with numbers to sort", required = true)
    private File inputFile;
    @CommandLine.Option(names = "--output", description = "output file for sorted numbers", required = true)
    private File outputFile;
    @CommandLine.Option(names = "--iterations", description = "number of iterations for benchmark", defaultValue = "10")
    private int iterations;

    @Override
    public Integer call() throws Exception {
        List<String> command = new ArrayList<>(Arrays.asList(
                SystemUtils.IS_OS_WINDOWS ? "mpjrun.bat" : "mpjrun.sh",
                "-np", "" + processes,
                "quicksort.MpiMain",
                "-cp", "./target/quicksort-jar-with-dependencies.jar",
                "--input", inputFile.getAbsolutePath(),
                "--output", outputFile.getAbsolutePath(),
                "--iterations", "" + iterations
        ));
        Process process = new ProcessBuilder().command(command).start();
        try (BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = processStdout.readLine()) != null) {
                REDIRECT_LOG.info(line);
            }
        }
        return process.waitFor();
    }
}
