package quicksort.tests;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public interface ResultPrinter {
    void print(Collection<TestResult> results, OutputStream output) throws IOException;
}
