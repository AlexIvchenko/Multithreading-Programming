package linear_systems.tests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public final class TestSuite {
    private int size = 2000;
    private double eps = 0;
    private int minIterations = 0;
    private int maxIterations = 1000;
    private List<Setup> setups;
}
