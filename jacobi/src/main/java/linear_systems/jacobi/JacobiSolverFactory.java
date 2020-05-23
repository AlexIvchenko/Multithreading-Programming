package linear_systems.jacobi;

import linear_systems.LinearSystem;

public interface JacobiSolverFactory {
    JacobiSolver create(final LinearSystem system);
}
