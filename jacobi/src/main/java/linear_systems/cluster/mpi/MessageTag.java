package linear_systems.cluster.mpi;

public enum MessageTag {
    ASSIGN(1), ITERATE(2), DONE(3), STATS(4), TERM(5);
    private final int value;

    MessageTag(final int value) {
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }
}
