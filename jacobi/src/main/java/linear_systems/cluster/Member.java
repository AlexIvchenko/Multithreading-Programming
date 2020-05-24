package linear_systems.cluster;

public interface Member {
    int getId();

    boolean isLeader();

    Assignment getAssignment();
}
