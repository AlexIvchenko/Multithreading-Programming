package linear_systems;

import lombok.Getter;

@Getter
public final class LinearSystem {
    private final int size;
    private final double[][] coefficients;

    public LinearSystem(final double[][] coefficients) {
        this.size = coefficients.length;
        this.coefficients = coefficients;
    }
}
