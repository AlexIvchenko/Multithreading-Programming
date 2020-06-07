package quicksort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ThreeWayPartitionerTest {
    private final ThreeWayPartitioner partitioner = new ThreeWayPartitioner();

    @Test
    void firstEqualToPivotWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{5, 2, 3, 7, 5, 1, 8});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 5), 0, 7);
        assertThat(array.getBackingArray()).containsExactly(1, 2, 3, 5, 5, 8, 7);
        assertThat(partitions.getLess()).isEqualTo(2);
        assertThat(partitions.getMore()).isEqualTo(5);
    }

    @Test
    void firstLessPivotWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{2, 3, 7, 5, 1, 8});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 5), 0, 6);
        assertThat(array.getBackingArray()).containsExactly(2, 3, 1, 5, 8, 7);
        assertThat(partitions.getLess()).isEqualTo(2);
        assertThat(partitions.getMore()).isEqualTo(4);
    }

    @Test
    void firstGreaterPivotWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{6, 3, 7, 5, 1, 8});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 5), 0, 6);
        assertThat(array.getBackingArray()).containsExactly(1, 3, 5, 6, 8, 7);
        assertThat(partitions.getLess()).isEqualTo(1);
        assertThat(partitions.getMore()).isEqualTo(3);
    }

    @Test
    void allGreaterPivotWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{6, 10, 7, 8});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 5), 0, 4);
        assertThat(array.getBackingArray()).containsExactly(6, 7, 8, 10);
        assertThat(partitions.getLess()).isEqualTo(-1);
        assertThat(partitions.getMore()).isEqualTo(0);
    }

    @Test
    void allLessPivotWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{6, 10, 7, 8});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 11), 0, 4);
        assertThat(array.getBackingArray()).containsExactly(6, 10, 7, 8);
        assertThat(partitions.getLess()).isEqualTo(3);
        assertThat(partitions.getMore()).isEqualTo(4);
    }

    @Test
    void emptyWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 11), 0, 0);
        assertThat(array.getBackingArray()).isEmpty();
        assertThat(partitions.getLess()).isEqualTo(-1);
        assertThat(partitions.getMore()).isEqualTo(0);
    }

    @Test
    void emptyWithOffsetTest() {
        IntArray array = IntArray.use(new int[]{1, 2});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 11), 1, 0);
        assertThat(array.getBackingArray()).containsExactly(1, 2);
        assertThat(partitions.getLess()).isEqualTo(0);
        assertThat(partitions.getMore()).isEqualTo(1);
    }

    @Test
    void singlePivotWithoutOffsetTest() {
        IntArray array = IntArray.use(new int[]{1});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 1), 0, 1);
        assertThat(array.getBackingArray()).containsExactly(1);
        assertThat(partitions.getLess()).isEqualTo(-1);
        assertThat(partitions.getMore()).isEqualTo(1);
    }

    @Test
    void singlePivotWithOffsetTest() {
        IntArray array = IntArray.use(new int[]{0, 1});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 1), 1, 1);
        assertThat(array.getBackingArray()).containsExactly(0, 1);
        assertThat(partitions.getLess()).isEqualTo(0);
        assertThat(partitions.getMore()).isEqualTo(2);
    }

    @Test
    void check1() {
        IntArray array = IntArray.use(new int[]{25, 72, 25, 97, 68});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 49), 0, 5);
        assertThat(array.getBackingArray()).containsExactly(25, 25, 97, 68, 72);
        assertThat(partitions.getLess()).isEqualTo(1);
        assertThat(partitions.getMore()).isEqualTo(2);
    }

    @Test
    void lessElementsAfterPivot() {
        IntArray array = IntArray.use(new int[]{0, 3, 3, 0, 0});
        Partitions partitions = partitioner.makePartitions(new IntPivotArray(array, 3), 0, 5);
        System.out.println(Arrays.toString(array.getBackingArray()));
        assertThat(array.getBackingArray()).containsExactly(0, 0, 0, 3, 3);
        assertThat(partitions.getLess()).isEqualTo(2);
        assertThat(partitions.getMore()).isEqualTo(5);
    }
}