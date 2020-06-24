package dijkstra;

import java.util.concurrent.TimeUnit;

public final class NanoStats implements Stats {
    private final long findMinDuration;
    private final long overheadDuration;
    private final long relaxDuration;

    public NanoStats(final long findMinDuration, final long overheadDuration, final long relaxDuration) {
        this.findMinDuration = findMinDuration;
        this.overheadDuration = overheadDuration;
        this.relaxDuration = relaxDuration;
    }

    @Override
    public long getFindMinDuration(final TimeUnit unit) {
        return TimeUnit.NANOSECONDS.convert(findMinDuration, unit);
    }

    @Override
    public long getOverheadDuration(final TimeUnit unit) {
        return TimeUnit.NANOSECONDS.convert(overheadDuration, unit);
    }

    @Override
    public long getRelaxDuration(final TimeUnit unit) {
        return TimeUnit.NANOSECONDS.convert(relaxDuration, unit);
    }
}
