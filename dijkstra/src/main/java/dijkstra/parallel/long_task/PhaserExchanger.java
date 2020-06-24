package dijkstra.parallel.long_task;

import dijkstra.DistanceToVertex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public final class PhaserExchanger implements Exchanger {
    private final Phaser phaser;
    private final Map<Integer, Iteration> iterations = new ConcurrentHashMap<>();

    public static ExchangerFactory factory() {
        return PhaserExchanger::new;
    }

    public PhaserExchanger(final int parallelism) {
        this.phaser = new Phaser(parallelism);
    }

    @Override
    public DistanceToVertex exchange(final DistanceToVertex localMin, final int iteration) {
        Iteration iter = iterations.computeIfAbsent(iteration, i -> new Iteration());
        iter.arrive(localMin);
        iterations.remove(iteration);
        return iter.getGlobalMin();
    }

    private final class Iteration {
        private final AtomicReference<DistanceToVertex> globalMin = new AtomicReference<>(null);

        void arrive(DistanceToVertex localMin) {
            if (localMin != null) {
                while (true) {
                    DistanceToVertex currentMin = this.globalMin.get();
                    if (currentMin == null || localMin.getDistance() < currentMin.getDistance()) {
                        if (this.globalMin.compareAndSet(currentMin, localMin)) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            phaser.arriveAndAwaitAdvance();
        }

        DistanceToVertex getGlobalMin() {
            return globalMin.get();
        }
    }
}
