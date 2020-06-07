package quicksort.tests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class TestSuite {
    private int size;
    private int origin = Integer.MIN_VALUE;
    private int bound = Integer.MAX_VALUE;
    private List<Setup> setups;
}
