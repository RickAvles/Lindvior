package com.rick.smartparkingplatform.simulation.parking.stay;

public enum StayCurve {

    SHORT(
            new long[]{10, 20, 40, 60, 90, 120, 180},
            new double[]{0.001, 0.02, 0.08, 0.18, 0.35, 0.55, 0.75, 0.95}
    ),

    NORMAL(
            new long[]{30, 60, 90, 120, 180, 240, 360, 480, 600},
            new double[]{0.0005, 0.005, 0.02, 0.06, 0.12, 0.22, 0.38, 0.58, 0.82, 0.97}
    ),

    LONG(
            new long[]{120, 180, 240, 360, 480, 600, 720},
            new double[]{0.0, 0.002, 0.01, 0.05, 0.12, 0.28, 0.55, 0.92}
    ),

    VERY_LONG(
            new long[]{360, 480, 600, 720, 840},
            new double[]{0.0, 0.001, 0.01, 0.05, 0.18, 0.45}
    ),

    RECOVERY(
            new long[]{30, 60, 120, 180, 240, 300},
            new double[]{0.001, 0.003, 0.006, 0.010, 0.020, 0.050, 1.000}
    );

    private final long[] limits;

    private final double[] probabilities;

    StayCurve(
            long[] limits,
            double[] probabilities) {

        this.limits = limits;
        this.probabilities = probabilities;
    }

    // Retorna a probabilidade correspondente ao tempo decorrido.
    public double getProbability(long elapsedTime) {

        for (int index = 0; index < limits.length; index++) {

            if (elapsedTime <= limits[index]) {
                return probabilities[index];
            }
        }

        return probabilities[probabilities.length - 1];
    }

    public boolean shouldLog(long elapsedTime) {

        for (long limit : limits) {

            if (elapsedTime == limit) {
                return true;
            }
        }

        return false;
    }

}