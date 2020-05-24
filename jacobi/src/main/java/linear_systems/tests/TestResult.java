package linear_systems.tests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TestResult {
    private final TestSuite suite;
    private final Setup setup;
    private final long elapsedTime;
    private final boolean correct;
    private final double speedup;
}
