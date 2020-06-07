package quicksort;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class Utils {
    public static Object toString(int[] array) {
        return toString(array, array.length);
    }

    public static Object toString(int[] array, int size) {
        return new Object() {
            @Override
            public String toString() {
                return Arrays.stream(array).limit(size).mapToObj(Integer::toString).collect(joining(", ", "[", "]"));
            }
        };
    }

    public static int mid(int a, int b, int c) {
        if ((a < b && b < c) || (c < b && b < a)) {
            return b;
        } else if ((b < a && a < c) || (c < a && a < b)) {
            return a;
        }
        return c;
    }
}
