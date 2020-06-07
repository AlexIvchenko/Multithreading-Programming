package quicksort;

public final class IntArray implements Array {
    private final int[] backingArray;

    public IntArray(final int[] backingArray) {
        this.backingArray = backingArray;
    }

    public static IntArray use(final int[] backingArray) {
        return new IntArray(backingArray);
    }

    public int[] getBackingArray() {
        return backingArray;
    }

    public int get(int idx) {
        return backingArray[idx];
    }

    public int compareAt(final int i, final int j) {
        return Integer.compare(backingArray[i], backingArray[j]);
    }

    public void swapAt(final int i, final int j) {
        int tmp = backingArray[i];
        backingArray[i] = backingArray[j];
        backingArray[j] = tmp;
    }
}
