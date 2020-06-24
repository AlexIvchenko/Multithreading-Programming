package dijkstra.parallel.long_task;

import dijkstra.DistanceToVertex;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

public final class BarrierPerIterationExchanger implements Exchanger {
    private final int parallelism;
    private final Map<Integer, Iteration> iterations = new ConcurrentHashMap<>();

    public static ExchangerFactory factory() {
        return BarrierPerIterationExchanger::new;
    }

    public BarrierPerIterationExchanger(final int parallelism) {
        this.parallelism = parallelism;
    }

    @Override
    public DistanceToVertex exchange(final DistanceToVertex localMin, final int iteration) {
        Iteration iter = iterations.computeIfAbsent(iteration, Iteration::new);
        iter.arrive(localMin);
        return iter.getGlobalMin();
    }

    private final class Iteration {
        private final CyclicBarrier barrier;
        private final AtomicReference<DistanceToVertex> globalMin = new AtomicReference<>(null);

        public Iteration(int iteration) {
            barrier = new CyclicBarrier(parallelism, () -> iterations.remove(iteration));
        }

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
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                return;
            }
        }

        DistanceToVertex getGlobalMin() {
            return globalMin.get();
        }
    }
}
