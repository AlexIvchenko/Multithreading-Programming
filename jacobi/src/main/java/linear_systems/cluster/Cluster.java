package linear_systems.cluster;

import java.util.Collection;

public interface Cluster {
    Member getCurrent();

    Collection<Member> getAllMembers();

    int size();

    void disconnect();
}
