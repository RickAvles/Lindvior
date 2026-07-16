package com.rick.smartparkingplatform.simulation.enums;

import lombok.Getter;

@Getter
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

    /**
     * -- GETTER --
     * Retorna os limites utilizados
     * pela curva de permanência.
     */
    private final long[] limits;

    private final double[] probabilities;

    StayCurve(
            long[] limits,
            double[] probabilities) {

        this.limits = limits;
        this.probabilities = probabilities;
    }

    /**
     * Obtém a probabilidade correspondente
     * ao tempo informado.
     * <p>
     * A unidade utilizada depende da curva:
     * minutos para permanência normal e
     * segundos para a curva de recuperação.
     *
     * @param elapsedTime tempo decorrido.
     * @return probabilidade correspondente.
     */
    public double getProbability(long elapsedTime) {

        for (int i = 0; i < limits.length; i++) {

            if (elapsedTime <= limits[i]) {
                return probabilities[i];
            }
        }

        return probabilities[probabilities.length - 1];
    }

}