package quicksort.tests;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public final class MarkdownResultPrinter implements ResultPrinter {
    @Override
    public void print(final Collection<TestResult> results, final OutputStream output) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        try (PrintWriter writer = new PrintWriter(output)) {
            writer.print("|Number of ints|Processes|Elapsed Time|Speedup|\n");
            writer.print("|:------------:|:-------:|:----------:|:-----:|\n");

            for (TestResult result : results) {
                Setup setup = result.getSetup();
                writer.print("|");
                writer.print(result.getSuite().getSize());
                writer.print("|");
                writer.print(setup.getProcesses());
                writer.print("|");
                writer.print(result.getElapsedTime() < TimeUnit.MILLISECONDS.toNanos(1)
                        ? result.getElapsedTime() + "ns"
                        : TimeUnit.NANOSECONDS.toMillis(result.getElapsedTime()) + "ms");
                writer.print("|");
                writer.print(formatter.format(result.getSpeedup()));
                writer.print("|\n");
            }
        }
    }
}
