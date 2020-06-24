package dijkstra;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.ToLongFunction;

public interface Stats {
    long getFindMinDuration(TimeUnit unit);

    long getOverheadDuration(TimeUnit unit);

    long getRelaxDuration(TimeUnit unit);

    static Stats min(Collection<Stats> stats) {
        return new NanoStats(
                min(stats, s -> s.getFindMinDuration(TimeUnit.NANOSECONDS)),
                min(stats, s -> s.getOverheadDuration(TimeUnit.NANOSECONDS)),
                min(stats, s -> s.getRelaxDuration(TimeUnit.NANOSECONDS))
        );
    }

    static long min(Collection<Stats> stats, ToLongFunction<Stats> metric) {
        return stats.stream().mapToLong(metric).min().getAsLong();
    }
}
