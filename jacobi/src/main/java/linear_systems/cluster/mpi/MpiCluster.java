package linear_systems.cluster.mpi;

import linear_systems.LinearSystem;
import linear_systems.cluster.Assignment;
import linear_systems.cluster.Cluster;
import linear_systems.cluster.Member;
import mpi.MPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MpiCluster implements Cluster {
    private static final Logger LOG = LoggerFactory.getLogger(MpiCluster.class);
    private final LinearSystem system;
    private final Map<Integer, Member> members = new HashMap<>();
    private Member current;

    public MpiCluster(final LinearSystem system) {
        this.system = system;
    }

    public void connect(final String mpiMyRank, final String mpiConfFile, final String mpiDeviceName) {
        MPI.Init(new String[]{mpiMyRank, mpiConfFile, mpiDeviceName});
        int rank = MPI.COMM_WORLD.Rank();
        int numberOfMembers = MPI.COMM_WORLD.Size();
        String processorName = MPI.Get_processor_name();
        LOG.info("rang: {}, cluster: {}, processor: {}", rank, numberOfMembers, processorName);

        int batchSize = system.getSize() / numberOfMembers;
        LOG.debug("batch size: {}", batchSize);
        if (numberOfMembers * batchSize < this.system.getSize()) {
            batchSize++;
        }

        for (int memberId = 0; memberId < numberOfMembers; memberId++) {
            int offset = memberId * batchSize;
            int currentBatchSize = Math.min(batchSize, system.getSize() - offset);
            Assignment assignment;
            if (currentBatchSize > 0) {
                assignment = new Assignment(memberId, offset, currentBatchSize);
                Member member = new MpiMember(memberId, assignment);
                members.put(memberId, member);
            } else {
                assignment = new Assignment(memberId, 0, 0);
            }
            if (rank == memberId) {
                this.current = new MpiMember(rank, assignment);
            }
        }
    }

    @Override
    public Member getCurrent() {
        return current;
    }

    @Override
    public Collection<Member> getAllMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public void disconnect() {
        MPI.Finalize();
    }
}
