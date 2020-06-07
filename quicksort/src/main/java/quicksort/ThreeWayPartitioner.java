package quicksort;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class ThreeWayPartitioner implements Partitioner {
    @Override
    public Partitions makePartitions(final PivotArray array, int offset, int size) {
        if (size == 0) {
            return new ThreeWayPartitions(offset - 1, offset);
        }
        int less = offset;
        int more = offset + size;
        // ignore first element to keep invariant in cycle
        int i = offset + 1;
        int firstCompareToPivot = array.compareWithPivot(offset);
        while (i < more) {
            if (array.compareWithPivot(i) < 0) {
                array.swapAt(i, ++less);
                i++;
            } else if (array.compareWithPivot(i) > 0) {
                array.swapAt(i, --more);
            } else {
                i++;
            }
        }
        // move ignored first element to its place
        if (firstCompareToPivot == 0) {
            array.swapAt(offset, less--);
        } else if (firstCompareToPivot > 0) {
            array.swapAt(offset, less);
            array.swapAt(less--, --more);
        }
        return new ThreeWayPartitions(less, more);
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ThreeWayPartitions implements Partitions {
        private final int less;
        private final int more;
    }
}
