package linear_systems.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import linear_systems.SolvedLinearSystem;
import linear_systems.misc.IoUtils;
import linear_systems.tests.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLine.Command(name = "test", mixinStandardHelpOptions = true)
public final class TestCommand implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(TestCommand.class);
    private static final Logger REDIRECT_LOG = LoggerFactory.getLogger("Redirect");
    private static final Pattern STAT_OUTPUT_PATTERN = Pattern.compile(".*Disconnect from cluster, participant (?<rank>[0-9]+), processing time: (?<elapsedTime>[0-9]+)ms, iterations: (?<iterations>[0-9]+)");

    @CommandLine.Option(names = "--suites",
            description = "yaml file containing test suites",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            defaultValue = "./suites/suites.yaml")
    private File testSuitesFile;

    @CommandLine.Option(names = "--format",
            description = "format of output statistics, valid values: ${COMPLETION-CANDIDATES}",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            defaultValue = "MARKDOWN")
    private StatOutputFormat format;

    public Integer call() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Collection<TestSuite> tests = mapper.readValue(testSuitesFile, new TypeReference<Collection<TestSuite>>() {
        });
        File systemFile;
        File solutionFile;
        File outputFile;
        try {
            systemFile = File.createTempFile("system", null);
            solutionFile = File.createTempFile("solution", null);
            outputFile = File.createTempFile("output", null);
        } catch (IOException e) {
            LOG.error("Failed to create tmp files for argument linear system and solutions");
            throw e;
        }
        List<TestResult> results = new ArrayList<>();
        for (TestSuite test : tests) {
            int size = test.getSize();
            SolvedLinearSystem system = SolvedLinearSystem.diagonalDominantSystem(size);
            LOG.info("Overwriting system and solution files");
            IoUtils.writeDoubleMatrix2D(size, size + 1, system.getSystem().getCoefficients(), systemFile);
            IoUtils.writeDoubleVector1D(system.getSolution(), solutionFile);
            List<Setup> setups = new ArrayList<>(test.getSetups());
            setups.add(0, new Setup(1, 1));
            long elapsedTimeNoParallelism = -1;
            for (Setup setup : setups) {
                Process process = new ProcessBuilder().command(
                        SystemUtils.IS_OS_WINDOWS ? "mpjrun.bat" : "mpjrun.sh",
                        "-np", "" + setup.getProcesses(),
                        "linear_systems.MpiMain",
                        "-cp", "./target/jacobi-jar-with-dependencies.jar",
                        "--system", systemFile.getAbsolutePath(),
                        "--solution", outputFile.getAbsolutePath(),
                        "--threads", "" + setup.getThreads(),
                        "--eps", "" + test.getEps(),
                        "--min", "" + test.getMinIterations(),
                        "--max", "" + test.getMaxIterations()
                ).start();
                long[] elapsedTimes = new long[setup.getProcesses()];
                try (BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = processStdout.readLine()) != null) {
                        REDIRECT_LOG.info(line);
                        Matcher matcher = STAT_OUTPUT_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            int rank = Integer.parseInt(matcher.group("rank"));
                            int iterations = Integer.parseInt(matcher.group("iterations"));
                            long elapsedTime = Long.parseLong(matcher.group("elapsedTime"));
                            elapsedTimes[rank] = elapsedTime;
                        }
                    }
                }
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    LOG.error("Cannot wait for process exit", e);
                    process.destroyForcibly();
                }
                boolean correct = validate(solutionFile, outputFile);
                LongSummaryStatistics statistics = Arrays.stream(elapsedTimes).summaryStatistics();
                long elapsedTime = statistics.getMax();
                if (elapsedTimeNoParallelism == -1) {
                    elapsedTimeNoParallelism = elapsedTime;
                    results.add(new TestResult(test, setup, elapsedTime, correct, 1.0));
                } else {
                    results.add(new TestResult(test, setup, elapsedTime, correct, 1.0 * elapsedTimeNoParallelism / elapsedTime));
                }
            }
        }

        ResultPrinter printer;
        if (format == StatOutputFormat.MARKDOWN) {
            printer = new MarkdownResultPrinter();
            printer.print(results, System.out);
        }
        return 0;
    }

    private boolean validate(final File expected, final File actual) throws IOException {
        return FileUtils.contentEquals(expected, actual);
    }
}
