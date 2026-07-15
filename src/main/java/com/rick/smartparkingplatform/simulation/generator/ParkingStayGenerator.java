package com.rick.smartparkingplatform.simulation.generator;

import com.rick.smartparkingplatform.simulation.enums.StayProfile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class ParkingStayGenerator {

    private final Random random = new Random();

    /**
     * Gera a duração prevista de permanência de um veículo.
     */
    public Duration generateStayDuration(
            StayProfile stayProfile) {

        return switch (stayProfile) {

            case SHORT -> generateShortStay();

            case NORMAL -> generateNormalStay();

            case LONG -> generateLongStay();

            case VERY_LONG -> generateVeryLongStay();
        };
    }

    /**
     * Gera uma permanência curta.
     */
    private Duration generateShortStay() {

        return Duration.ofMinutes(
                randomBetween(30, 90)
        );
    }

    /**
     * Gera uma permanência média.
     */
    private Duration generateNormalStay() {

        return Duration.ofMinutes(
                randomBetween(90, 240)
        );
    }

    /**
     * Gera uma permanência longa.
     */
    private Duration generateLongStay() {

        return Duration.ofMinutes(
                randomBetween(240, 480)
        );
    }

    /**
     * Gera uma permanência muito longa.
     */
    private Duration generateVeryLongStay() {

        return Duration.ofMinutes(
                randomBetween(480, 720)
        );
    }

    /**
     * Retorna um valor aleatório entre dois limites.
     */
    private int randomBetween(
            int min,
            int max) {

        return random.nextInt(
                max - min + 1
        ) + min;
    }

}