package quicksort.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import quicksort.IoUtils;
import quicksort.tests.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLine.Command(name = "test", mixinStandardHelpOptions = true)
public final class TestCommand implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(TestCommand.class);
    private static final Logger REDIRECT_LOG = LoggerFactory.getLogger("Redirect");
    private static final Pattern STAT_OUTPUT_PATTERN = Pattern.compile(".*Min sorting time: (?<amount>[0-9]+)(?<unit>(ns|ms)).*");
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

    @CommandLine.Option(names = "--iterations", description = "number of iterations for benchmark", defaultValue = "10")
    private int iterations;

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Collection<TestSuite> tests = mapper.readValue(testSuitesFile, new TypeReference<Collection<TestSuite>>() {
        });
        File inputFile;
        File outputFile;
        try {
            inputFile = File.createTempFile("input", null);
            outputFile = File.createTempFile("output", null);
        } catch (IOException e) {
            LOG.error("Failed to create tmp files for argument linear system and solutions");
            throw e;
        }
        List<TestResult> results = new ArrayList<>();
        for (TestSuite test : tests) {
            int size = test.getSize();
            int origin = test.getOrigin();
            int bound = test.getBound();
            int[] ints = ThreadLocalRandom.current().ints(size, origin, bound).toArray();
            IoUtils.writeInts(inputFile, ints);

            List<Setup> setups = new ArrayList<>(test.getSetups());
            setups.add(0, new Setup(1));
            long elapsedTimeNoParallelism = -1;
            for (Setup setup : setups) {
                List<String> command = new ArrayList<>(Arrays.asList(
                        SystemUtils.IS_OS_WINDOWS ? "mpjrun.bat" : "mpjrun.sh",
                        "-np", "" + setup.getProcesses(),
                        "quicksort.MpiMain",
                        "-cp", "./target/quicksort-jar-with-dependencies.jar",
                        "--input", inputFile.getAbsolutePath(),
                        "--output", outputFile.getAbsolutePath(),
                        "--iterations", "" + iterations
                ));
                Process process = new ProcessBuilder(command).start();
                long elapsedTime = 0;
                try (BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = processStdout.readLine()) != null) {
                        REDIRECT_LOG.info(line);
                        Matcher matcher = STAT_OUTPUT_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            long parsedElapsedTime = Long.parseLong(matcher.group("amount"));
                            TimeUnit unit = matcher.group("unit").equals("ns") ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS;
                            elapsedTime = TimeUnit.NANOSECONDS.convert(parsedElapsedTime, unit);
                        }
                    }
                }
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    LOG.error("Cannot wait for process exit", e);
                    process.destroyForcibly();
                }
                boolean correct = validate(inputFile, outputFile);
                if (correct) {
                    LOG.info("PASSED");
                } else {
                    LOG.error("FILED");
                }
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

    private boolean validate(final File inputFile, final File outputFile) throws IOException {
        int[] expected = IoUtils.readInts(inputFile);
        Arrays.sort(expected);
        int[] actual = IoUtils.readInts(outputFile);
        return Arrays.equals(expected, actual);
    }
}
