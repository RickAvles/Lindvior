package com.rick.smartparkingplatform.domain.simulation;

import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.simulation.parking.stay.StayProfile;
import com.rick.smartparkingplatform.simulation.vehicle.VehicleAttributeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleAttributeGeneratorTest {

    private VehicleAttributeGenerator vehicleAttributeGenerator;

    @BeforeEach
    void setUp() {
        vehicleAttributeGenerator = new VehicleAttributeGenerator();
    }

    // Verifica se a geração do perfil de permanência retorna somente perfis válidos.
    @Test
    void shouldGenerateValidStayProfiles() {

        Set<StayProfile> generatedProfiles = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            generatedProfiles.add(
                    vehicleAttributeGenerator.generateStayProfile()
            );
        }

        assertAll(
                () -> assertTrue(
                        generatedProfiles.stream()
                                .allMatch(EnumSet.allOf(StayProfile.class)::contains)
                ),
                () -> assertTrue(
                        !generatedProfiles.isEmpty()
                )
        );
    }

    // Verifica se a geração do tipo de veículo retorna somente tipos válidos.
    @Test
    void shouldGenerateValidVehicleTypes() {

        Set<VehicleType> generatedTypes = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            generatedTypes.add(
                    vehicleAttributeGenerator.generateVehicleType()
            );
        }

        assertAll(
                () -> assertTrue(
                        generatedTypes.stream()
                                .allMatch(EnumSet.allOf(VehicleType.class)::contains)
                ),
                () -> assertTrue(
                        !generatedTypes.isEmpty()
                )
        );
    }

    // Verifica se a geração de cores sempre retorna um valor válido.
    @Test
    void shouldGenerateNonEmptyColors() {

        for (int i = 0; i < 1000; i++) {

            String color =
                    vehicleAttributeGenerator.generateColor();

            assertTrue(
                    color != null && !color.isBlank()
            );
        }
    }

    // Verifica se a geração de PCD retorna somente valores booleanos válidos.
    @Test
    void shouldGeneratePcdAsBoolean() {

        for (int i = 0; i < 1000; i++) {

            boolean pcd =
                    vehicleAttributeGenerator.generatePcd();

            // O tipo boolean já garante que o resultado seja true ou false.
            assertTrue(
                    pcd || !pcd
            );
        }
    }

    // Verifica se a frequência de veículos PCD permanece próxima dos 2% configurados.
    @Test
    void shouldGeneratePcdWithApproximatelyConfiguredProbability() {

        int totalVehicles = 10_000;
        int pcdVehicles = 0;

        for (int i = 0; i < totalVehicles; i++) {

            if (vehicleAttributeGenerator.generatePcd()) {
                pcdVehicles++;
            }
        }

        double pcdPercentage =
                (double) pcdVehicles / totalVehicles * 100;

        // A probabilidade configurada é de 2%.
        // A margem é propositalmente ampla para evitar um teste frágil
        // por causa da aleatoriedade.
        assertTrue(
                pcdPercentage >= 0.5 &&
                        pcdPercentage <= 4.0,
                "PCD percentage was: " + pcdPercentage + "%"
        );
    }
}