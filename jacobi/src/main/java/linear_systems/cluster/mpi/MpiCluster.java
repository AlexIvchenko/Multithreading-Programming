package linear_systems.cluster.mpi;

import linear_systems.LinearSystem;
import linear_systems.cluster.Cluster;
import linear_systems.cluster.Member;
import linear_systems.cluster.Participant;
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
    private Participant current;

    public MpiCluster(final LinearSystem system) {
        this.system = system;
    }

    public void connect(final String mpiMyRank, final String mpiConfFile, final String mpiDeviceName) {
        MPI.Init(new String[]{mpiMyRank, mpiConfFile, mpiDeviceName});
        int rank = MPI.COMM_WORLD.Rank();
        int numberOfMembers = MPI.COMM_WORLD.Size();
        String processorName = MPI.Get_processor_name();
        LOG.info("rang: {}, cluster: {}, processor: {}", rank, numberOfMembers, processorName);
        for (int memberId = 0; memberId < numberOfMembers; memberId++) {
            if (rank == memberId) {
                this.current = new MpiParticipant(rank, system);
            }
            Member member = new MpiRemoteMember(memberId, system);
            members.put(memberId, member);
        }
    }

    @Override
    public Participant getCurrent() {
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
