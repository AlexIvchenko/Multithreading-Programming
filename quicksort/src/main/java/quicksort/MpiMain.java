package quicksort;

import mpi.MPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static quicksort.Tags.EXCHANGE;
import static quicksort.Tags.EXCHANGE_SIZE;

@CommandLine.Command
public class MpiMain implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(MpiMain.class);
    @CommandLine.Parameters(index = "0")
    private String mpiMyRank;
    @CommandLine.Parameters(index = "1")
    private String mpiConfFile;
    @CommandLine.Parameters(index = "2")
    private String mpiDeviceName;
    @CommandLine.Option(names = "--input", description = "input file with numbers to sort", required = true)
    private File inputFile;
    @CommandLine.Option(names = "--output", description = "output file for sorted numbers", required = true)
    private File outputFile;
    @CommandLine.Option(names = "--iterations", description = "number of iterations for benchmark", required = true)
    private int iterations;

    public static void main(String[] args) {
        MpiMain command = new MpiMain();
        new CommandLine(command).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        MPI.Init(new String[]{mpiMyRank, mpiConfFile, mpiDeviceName});
        int rank = MPI.COMM_WORLD.Rank();
        MDC.put("participant", Integer.toString(rank));
        int[] entireArraySize = new int[1];
        LOG.debug("Exchange array size");
        MPI.COMM_WORLD.Bcast(entireArraySize, 0, 1, MPI.INT, 0);
        int[] data = IoUtils.readInts(inputFile);
        int hypercubeCluster = 1;
        while (hypercubeCluster <= MPI.COMM_WORLD.Size() && hypercubeCluster <= data.length) {
            hypercubeCluster <<= 1;
        }
        hypercubeCluster >>= 1;
        if (rank == 0) {
            LOG.info("Building hypercube cluster of size {}", hypercubeCluster);
        }
        long minSortingTime = Long.MAX_VALUE;
        for (int iteration = 0; iteration < iterations; iteration++) {
            LOG.info("Iteration #{}", iteration);
            MembershipParticipant participant = participate(rank, data, hypercubeCluster);
            MPI.COMM_WORLD.Barrier();
            long start = System.nanoTime();
            int[] sorted = sort(participant);
            MPI.COMM_WORLD.Barrier();
            long finish = System.nanoTime();
            long sortingTime = finish - start;
            if (minSortingTime > sortingTime) {
                minSortingTime = sortingTime;
            }
            if (rank == 0) {
                LOG.info("Sorting time: {}ms", TimeUnit.NANOSECONDS.toMillis(sortingTime));
            }
            if (participant != null) {
                LOG.info("Exchange time: {}ns", participant.getExchangeTime(TimeUnit.NANOSECONDS));
            }
            LOG.debug("Result {}", Arrays.toString(sorted));
            if (rank == 0) {
                int[] result = new int[data.length];
                System.arraycopy(sorted, 0, result, 0, sorted.length);
                int pos = sorted.length;
                int[] size = new int[1];
                int[] buffer = new int[data.length];
                for (int member = 1; member < hypercubeCluster; member++) {
                    MPI.COMM_WORLD.Recv(size, 0, 1, MPI.INT, member, EXCHANGE_SIZE);
                    MPI.COMM_WORLD.Recv(buffer, 0, size[0], MPI.INT, member, EXCHANGE);
                    System.arraycopy(buffer, 0, result, pos, size[0]);
                    pos += size[0];
                }
                LOG.debug("Entire result {}", Arrays.toString(result));
                if (iteration == iterations - 1) {
                    IoUtils.writeInts(outputFile, result);
                }
            } else {
                MPI.COMM_WORLD.Isend(new int[]{sorted.length}, 0, 1, MPI.INT, 0, EXCHANGE_SIZE);
                MPI.COMM_WORLD.Isend(sorted, 0, sorted.length, MPI.INT, 0, EXCHANGE);
            }
        }
        if (rank == 0) {
            if (minSortingTime <= TimeUnit.MILLISECONDS.toNanos(10)) {
                LOG.info("Min sorting time: {}ns", minSortingTime);
            } else {
                LOG.info("Min sorting time: {}ms", TimeUnit.NANOSECONDS.toMillis(minSortingTime));
            }
        }
        MPI.Finalize();
        return 0;
    }

    private int[] sort(MembershipParticipant participant) {
        if (participant != null) {
            while (participant.getGroupSize() > 1) {
                LOG.debug("pivot");
                participant.syncPivot();
                LOG.debug("compare exchange");
                participant.compareExchange();
                LOG.debug("regroup");
                participant = participant.regroup();
            }
            return participant.sort();
        }
        return new int[]{};
    }

    private MembershipParticipant participate(int rank, int[] data, int hypercube) {
        MembershipParticipant participant = null;
        if (rank >= hypercube) {
            return null;
        }
        int jobSize = data.length / hypercube;
        if (jobSize * hypercube != data.length) {
            jobSize++;
        }
        int offset = 0;
        for (int i = 0; i < hypercube; i++) {
            int currentSize = Math.min(data.length - offset, jobSize);
            if (currentSize > 0) {
                if (i == rank) {
                    LOG.info("Assign {} to {}:{}", i, offset, currentSize);
                    participant = new MembershipParticipant(MPI.COMM_WORLD, new ThreeWayPartitioner(), data.length, data, offset, currentSize);
                }
                offset += currentSize;
            }
        }
        return participant;
    }
}
