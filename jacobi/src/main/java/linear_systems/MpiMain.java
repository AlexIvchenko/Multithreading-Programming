package linear_systems;

import linear_systems.cluster.Leader;
import linear_systems.cluster.Participant;
import linear_systems.cluster.Worker;
import linear_systems.cluster.mpi.MpiCluster;
import linear_systems.jacobi.ConvergentJacobiSolver;
import linear_systems.jacobi.ForkJoinJacobiBatchProcessor;
import linear_systems.jacobi.ForkJoinJacobiSolver;
import linear_systems.jacobi.JacobiBatchProcessorImpl;
import linear_systems.misc.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CommandLine.Command
public class MpiMain implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(MpiMain.class);
    @CommandLine.Parameters(index = "0")
    private String mpiMyRank;
    @CommandLine.Parameters(index = "1")
    private String mpiConfFile;
    @CommandLine.Parameters(index = "2")
    private String mpiDeviceName;

    @CommandLine.Option(names = "--min", defaultValue = "0", description = "minimal number of iterations")
    private int minIterations;
    @CommandLine.Option(names = "--max", defaultValue = "1000", description = "maximal number of iterations")
    private int maxIterations;
    @CommandLine.Option(names = "--eps", defaultValue = "0.000001", description = "accuracy")
    private double eps;
    @CommandLine.Option(names = "--threads", defaultValue = "0", description = {
            "0     - to run with common ForkJoinPool with optimal number of threads",
            "1     - to run without parallelism",
            "other - to run with special ExecutorService with given number of threads"
    })
    private int threads;
    @CommandLine.Option(names = "--system", description = "file containing linear system in matrix format", required = true)
    private File linearSystemFile;
    @CommandLine.Option(names = "--init", description = "file containing initial approximations")
    private File initialApproximationsFile;
    @CommandLine.Option(names = "--solution", description = "output file for the solution", required = true)
    private File solutionFile;

    @Override
    public Integer call() throws Exception {
        LOG.info("Starting...");
        LOG.info("Reading linear system...");
        long startReading = System.currentTimeMillis();
        LinearSystem system = new LinearSystem(IoUtils.readDoubleMatrix2D(linearSystemFile));
        double[] initialApproximations;
        if (initialApproximationsFile != null) {
            initialApproximations = IoUtils.readDoubleVector1D(initialApproximationsFile);
        } else {
            initialApproximations = new double[system.getSize()];
        }
        LOG.info("Linear system of size {} is read, took: {}ms", system.getSize(), System.currentTimeMillis() - startReading);
        MpiCluster cluster = new MpiCluster(system);
        cluster.connect(mpiMyRank, mpiConfFile, mpiDeviceName);
        Participant participant = cluster.getCurrent();
        LOG.info("Connected to cluster, participant {}", participant.getId());
        ConvergentJacobiSolver parallel = new ConvergentJacobiSolver(
                new ForkJoinJacobiSolver(system.getCoefficients()),
                minIterations,
                maxIterations,
                eps);
        parallel.run(initialApproximations);
        Thread worker;
        if (threads == 0) {
            LOG.info("Using multi thread worker with common ForkJoinPool");
            worker = new Thread(new Worker(participant, system, (s, offset, batchSize) ->
                    new ForkJoinJacobiBatchProcessor(s.getCoefficients(), offset, batchSize)));
        } else if (threads == 1) {
            LOG.info("Using single thread worker");
            worker = new Thread(new Worker(participant, system, (s, offset, batchSize) ->
                    new JacobiBatchProcessorImpl(s.getCoefficients(), offset, batchSize)));
        } else {
            LOG.info("Using multi thread worker with ThreadPoolExecutor of {} threads", threads);
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            worker = new Thread(new Worker(participant, system, (s, offset, batchSize) ->
                    new ForkJoinJacobiBatchProcessor(s.getCoefficients(), executorService, threads, offset, batchSize)));
        }
        long start = System.currentTimeMillis();
        worker.start();
        Thread leader = null;
        if (participant.isLeader()) {
            leader = new Thread(() -> new Leader(cluster, system, eps, minIterations, maxIterations).run(initialApproximations));
            leader.start();
        }
        worker.join();
        long finish = System.currentTimeMillis();
        if (leader != null) {
            leader.join();
            LOG.info("Writing solutions to {}", solutionFile);
            IoUtils.writeDoubleVector1D(initialApproximations, solutionFile);
        }
        LOG.info("Disconnect from cluster, participant {}, processing time: {}ms", participant.getId(), finish - start);
        cluster.disconnect();
        return 0;
    }

    public static void main(String[] args) {
        MpiMain command = new MpiMain();
        new CommandLine(command).execute(args);
    }
}
