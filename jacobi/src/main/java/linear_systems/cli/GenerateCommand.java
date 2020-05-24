package linear_systems.cli;

import linear_systems.LinearSystem;
import linear_systems.SolvedLinearSystem;
import linear_systems.misc.IoUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
public final class GenerateCommand implements Callable<Integer> {
    @CommandLine.Option(names = "--size", description = "Number of equations in system", required = true)
    private int size;
    @CommandLine.Option(names = "--system", description = "Output file for linear system in matrix format")
    private File linearSystemFile;
    @CommandLine.Option(names = "--solution", description = "Output file for the solution")
    private File solutionFile;

    @Override
    public Integer call() throws Exception {
        if (linearSystemFile == null) {
            linearSystemFile = new File("system-" + size + ".txt");
        }
        if (solutionFile == null) {
            solutionFile = new File("solution-" + size + ".txt");
        }
        SolvedLinearSystem solvedLinearSystem = SolvedLinearSystem.diagonalDominantSystem(size);
        LinearSystem system = solvedLinearSystem.getSystem();
        IoUtils.writeDoubleMatrix2D(size, size + 1, system.getCoefficients(), linearSystemFile);
        IoUtils.writeDoubleVector1D(solvedLinearSystem.getSolution(), solutionFile);
        return 0;
    }
}
