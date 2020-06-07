package quicksort;

public interface Partitioner {
    Partitions makePartitions(PivotArray array, int offset, int size);
}
