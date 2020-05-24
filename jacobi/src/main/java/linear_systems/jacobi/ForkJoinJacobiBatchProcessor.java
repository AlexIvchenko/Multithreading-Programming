package linear_systems.jacobi;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ForkJoinJacobiBatchProcessor implements JacobiBatchProcessor {
    private final ListeningExecutorService executor;
    private final double[][] coefficients;
    private final int numberOfWorkers;
    private final int globalOffset;
    private final int globalBatchSize;

    public ForkJoinJacobiBatchProcessor(final double[][] coefficients, final int globalOffset, final int globalBatchSize) {
        this(coefficients, ForkJoinPool.commonPool(), ForkJoinPool.commonPool().getParallelism(), globalOffset, globalBatchSize);
    }

    public ForkJoinJacobiBatchProcessor(final double[][] coefficients, final ExecutorService executor, final int numberOfWorkers, final int globalOffset, final int globalBatchSize) {
        this.coefficients = coefficients;
        this.executor = MoreExecutors.listeningDecorator(executor);
        this.numberOfWorkers = numberOfWorkers;
        this.globalOffset = globalOffset;
        this.globalBatchSize = globalBatchSize;
    }

    @Override
    public BatchSolution runIterationOnBatch(final double[] x) {
        int batchSize = computeBatchSize();
        List<ListenableFuture<BatchSolution>> futures = IntStream.range(0, numberOfWorkers)
                .mapToObj(worker -> {
                    int workerOffset = globalOffset + worker * batchSize;
                    int workerBatchSize = Math.min(batchSize, globalBatchSize + globalOffset - workerOffset);
                    if (workerBatchSize <= 0) {
                        return null;
                    }
                    return new JacobiBatchProcessorImpl(coefficients, workerOffset, workerBatchSize);
                })
                .filter(Objects::nonNull)
                .map(worker -> (Callable<BatchSolution>) () -> worker.runIterationOnBatch(x))
                .map(executor::submit)
                .collect(Collectors.toList());
        ListenableFuture<List<BatchSolution>> all = Futures.allAsList(futures);
        List<BatchSolution> solutions;
        try {
            solutions = all.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
        double[] globalSolution = new double[globalBatchSize];
        for (BatchSolution solution : solutions) {
            System.arraycopy(solution.getX(), 0, globalSolution, solution.getOffset() - globalOffset, solution.getSize());
        }
        return new BatchSolution(globalSolution, globalOffset, globalBatchSize);
    }

    private int computeBatchSize() {
        int batchSize = globalBatchSize / numberOfWorkers;
        if (numberOfWorkers * batchSize < globalBatchSize) {
            batchSize++;
        }
        return batchSize;
    }
}
