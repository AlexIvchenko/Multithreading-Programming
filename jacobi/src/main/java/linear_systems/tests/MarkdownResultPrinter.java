package linear_systems.tests;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collection;

public final class MarkdownResultPrinter implements ResultPrinter {
    @Override
    public void print(final Collection<TestResult> results, final OutputStream output) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        try (PrintWriter writer = new PrintWriter(output)) {
            writer.print("|Equations|Parallelism|Processes|Threads per process|Time(max)|Speedup|\n");
            writer.print("|:-------:|:---------:|:-------:|:-----------------:|:-------:|:-----:|\n");
            for (TestResult result : results) {
                String parallelism;
                Setup setup = result.getSetup();
                if (setup.getProcesses() == 1 && setup.getThreads() == 1) {
                    parallelism = "NO";
                } else if (setup.getProcesses() > 1 && setup.getThreads() == 1) {
                    parallelism = "MULTI-PROCESS";
                } else if (setup.getProcesses() == 1 && (setup.getThreads() > 1 || setup.getThreads() == 0)) {
                    parallelism = "MULTI-THREAD";
                } else {
                    parallelism = "MIXED";
                }
                writer.print("|");
                writer.print(result.getSuite().getSize());
                writer.print("|");
                writer.print(parallelism);
                writer.print("|");
                writer.print(setup.getProcesses());
                writer.print("|");
                writer.print(setup.getThreads() == 0 ? Runtime.getRuntime().availableProcessors() : setup.getThreads());
                writer.print("|");
                writer.print(result.getElapsedTime());
                writer.print("|");
                writer.print(formatter.format(result.getSpeedup()));
                writer.print("|\n");
            }
        }
    }
}
