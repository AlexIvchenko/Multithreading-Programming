package quicksort;

public final class IntPivotArray implements PivotArray {
    private final IntArray array;
    private final int pivot;

    public IntPivotArray(final IntArray array, final int pivot) {
        this.array = array;
        this.pivot = pivot;
    }

    @Override
    public int compareWithPivot(final int i) {
        return Integer.compare(array.get(i), pivot);
    }

    @Override
    public int compareAt(final int i, final int j) {
        return array.compareAt(i, j);
    }

    @Override
    public void swapAt(final int i, final int j) {
        array.swapAt(i, j);
    }
}
