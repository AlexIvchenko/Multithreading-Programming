package linear_systems.cluster;

import linear_systems.LinearSystem;
import linear_systems.cluster.mpi.State;
import linear_systems.jacobi.BatchSolution;
import linear_systems.jacobi.JacobiBatchProcessor;
import linear_systems.jacobi.JacobiBatchProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Worker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
    private final Participant participant;
    private Assignment assignment;
    private final LinearSystem system;
    private final JacobiBatchProcessorFactory factory;
    private JacobiBatchProcessor solver;
    private State state;
    private double[] x;

    public Worker(final Participant participant, final LinearSystem system, final JacobiBatchProcessorFactory factory) {
        this.participant = participant;
        this.system = system;
        this.factory = factory;
        this.state = State.UNASSIGNED;
    }

    @Override
    public void run() {
        while (true) {
            if (state == State.UNASSIGNED) {
                LOG.debug("Worker {} waits for assignment", participant.getId());
                this.assignment = participant.waitForAssignment();
                if (assignment.getBatchSize() == -1 && assignment.getOffset() == -1) {
                    LOG.debug("Worker {} is terminating", participant.getId());
                    break;
                }
                LOG.debug("Worker {} was given assignment: offset={}, size={}", participant.getId(), assignment.getOffset(), assignment.getBatchSize());
                state = State.ASSIGNED;
            } else if (state == State.ASSIGNED) {
                LOG.debug("Worker {} waits for new x vector", participant.getId());
                this.x = participant.waitForNewX();
                this.solver = factory.create(system, assignment.getOffset(), assignment.getBatchSize());
                LOG.debug("Worker {} was given new x vector", participant.getId());
                state = State.RUNNING;
            } else if (state == State.RUNNING) {
                BatchSolution batchSolution = solver.runIterationOnBatch(x);
                LOG.debug("Worker {} submits its part", participant.getId());
                participant.submit(batchSolution);
                LOG.debug("Worker {} submitted its part", participant.getId());
                state = State.UNASSIGNED;
            }
        }
    }
}
