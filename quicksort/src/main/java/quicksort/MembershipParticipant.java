package quicksort;

import mpi.Intracomm;
import mpi.MPI;
import mpi.Request;
import mpi.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.rmi.CORBA.Util;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static quicksort.Tags.EXCHANGE;

public final class MembershipParticipant {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipParticipant.class);
    private final Intracomm communicator;
    private final int rank;
    private final int groupSize;
    private final Partitioner partitioner;
    private final int[] data;
    private final int[] buffer;
    private int pivot;
    private int size;
    private long exchangeTime = 0;

    public MembershipParticipant(final Intracomm communicator, final Partitioner partitioner, int maxSize,
                                 int[] data, int offset, int size) {
        this.communicator = communicator;
        this.rank = communicator.Rank();
        this.groupSize = communicator.Size();
        this.partitioner = partitioner;
        this.data = new int[maxSize];
        this.buffer = new int[maxSize];
        System.arraycopy(data, offset, this.data, 0, size);
        this.size = size;
        LOG.debug("Part {}", Utils.toString(this.data, size));
    }

    private MembershipParticipant(final Intracomm communicator,
                                 final Partitioner partitioner,
                                 final int[] data,
                                 final int[] buffer,
                                 final int size,
                                 final long exchangeTime) {
        this.communicator = communicator;
        this.rank = communicator.Rank();
        this.groupSize = communicator.Size();
        this.partitioner = partitioner;
        this.data = data;
        this.buffer = buffer;
        this.size = size;
        this.exchangeTime = exchangeTime;
        LOG.debug("Part {}", Utils.toString(this.data, size));
    }

    public int getGroupSize() {
        return groupSize;
    }

    public MembershipParticipant regroup() {
        return new MembershipParticipant(communicator.Split(isOnLeftHalf() ? 0 : 1, rank), partitioner, data, buffer, size, exchangeTime);
    }

    private boolean isOnLeftHalf() {
        return rank < groupSize / 2;
    }

    public void syncPivot() {
        int[] pivotBuffer = new int[1];
        if (isLeader()) {
            pivotBuffer[0] = choosePivot();
        }
        communicator.Bcast(pivotBuffer, 0, 1, MPI.INT, 0);
        this.pivot = pivotBuffer[0];
        LOG.info("Pivot has been chosen {}", this.pivot);
    }

    public void compareExchange() {

        IntPivotArray array = new IntPivotArray(IntArray.use(data), pivot);
        Partitions partitions = partitioner.makePartitions(array, 0, size);

        LOG.debug("After divide: {}", Utils.toString(data, size));
        LOG.debug("Less: {}", partitions.getLess());
        LOG.debug("More: {}", partitions.getMore());
        int neighbour = getNeighbour();
        LOG.debug("Neighbor is {}", neighbour);
        long exchangeStart = System.nanoTime();
        Request request;
        if (rank < neighbour) {
            // send the biggest part to the neighbour
            int sizeToSend = this.size - partitions.getMore();
            LOG.debug("Sending biggest part to neighbour, size = {}", sizeToSend);
            request = communicator.Isend(data, partitions.getMore(), sizeToSend, MPI.INT, neighbour, EXCHANGE);
        } else {
            int sizeToSend = partitions.getLess() + 1;
            LOG.debug("Sending lowest part to neighbour, size = {}", sizeToSend);
            request = communicator.Isend(data, 0, sizeToSend, MPI.INT, neighbour, EXCHANGE);
        }
        int[] ownPart;
        if (rank < neighbour) {
            int ownPartSize = partitions.getMore();
            ownPart = new int[ownPartSize];
            System.arraycopy(data, 0, ownPart, 0, ownPartSize);
            LOG.debug("Own part <= {}: {}", pivot, Utils.toString(ownPart));
        } else {
            int ownPartSize = this.size - partitions.getLess() - 1;
            ownPart = new int[ownPartSize];
            System.arraycopy(data, partitions.getLess() + 1, ownPart, 0, ownPartSize);
            LOG.debug("Own part >= {}: {}", pivot, Utils.toString(ownPart));
        }

        Status response = communicator.Recv(buffer, 0, buffer.length, MPI.INT, neighbour, EXCHANGE);
        int neighbourPartSize = response.Get_elements(MPI.INT);
        LOG.debug("Neighbour part: {}", Utils.toString(buffer, neighbourPartSize));
        request.Wait();
        if (rank < neighbour) {
            System.arraycopy(buffer, 0, data, 0, neighbourPartSize);
            System.arraycopy(ownPart, 0, data, neighbourPartSize, ownPart.length);
        } else {
            System.arraycopy(ownPart, 0, data, 0, ownPart.length);
            System.arraycopy(buffer, 0, data, ownPart.length, neighbourPartSize);
        }
        this.size = ownPart.length + neighbourPartSize;
        LOG.debug("Current amount of data: {}", this.size);
        this.exchangeTime += System.nanoTime() - exchangeStart;
    }

    public long getExchangeTime(TimeUnit unit) {
        return unit.convert(exchangeTime, TimeUnit.NANOSECONDS);
    }

    public int[] sort() {
        int[] sorted = new int[size];
        System.arraycopy(data, 0, sorted, 0, size);
        Arrays.sort(sorted, 0, size);
        return sorted;
    }

    public int choosePivot() {
        if (size == 0) {
            return 0;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int a = data[random.nextInt(size)];
        int b = data[random.nextInt(size)];
        int c = data[random.nextInt(size)];
        return Utils.mid(a, b, c);
    }

    public boolean isLeader() {
        return rank == 0;
    }

    public int getNeighbour() {
        int power = Integer.numberOfTrailingZeros(Integer.highestOneBit(groupSize));
        return rank ^ (1 << (power - 1));
    }
}
