package com.rick.smartparkingplatform.config.initializer.generator;

import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LayoutGenerator {

    // Repositório das vagas.
    private final ParkingSpotRepository parkingSpotRepository;

    // Quantidade de vagas dos setores.
    private static final int REGULAR_SECTOR_SPOTS = 700;
    private static final int MOTORCYCLE_SECTOR_SPOTS = 150;
    private static final int ELECTRIC_SECTOR_SPOTS = 100;
    private static final int PREMIUM_SECTOR_SPOTS = 150;

    // Quantidade de vagas PCD por setor.
    private static final int PCD_SECTOR_A = 17;
    private static final int PCD_SECTOR_B = 17;
    private static final int PCD_SECTOR_C = 16;

    /**
     * Gera todas as vagas.
     */
    public void generate(List<ParkingSector> parkingSectors) {

        List<ParkingSpot> parkingSpots = new ArrayList<>();

        for (ParkingSector parkingSector : parkingSectors) {

            switch (parkingSector.getType()) {

                case REGULAR -> generateRegularSector(
                        parkingSector,
                        parkingSpots
                );

                case MOTORCYCLE -> generateSector(
                        parkingSector,
                        ParkingSpotType.MOTORCYCLE,
                        MOTORCYCLE_SECTOR_SPOTS,
                        parkingSpots
                );

                case ELECTRIC -> generateSector(
                        parkingSector,
                        ParkingSpotType.ELECTRIC,
                        ELECTRIC_SECTOR_SPOTS,
                        parkingSpots
                );

                case PREMIUM -> generateSector(
                        parkingSector,
                        ParkingSpotType.REGULAR,
                        PREMIUM_SECTOR_SPOTS,
                        parkingSpots
                );

            }

        }

        parkingSpotRepository.saveAll(parkingSpots);

    }

    /**
     * Gera as vagas do setor regular.
     */
    private void generateRegularSector(
            ParkingSector parkingSector,
            List<ParkingSpot> parkingSpots) {

        LocalDateTime now = LocalDateTime.now();

        int totalPcdSpots = switch (parkingSector.getName()) {

            case "A" -> PCD_SECTOR_A;
            case "B" -> PCD_SECTOR_B;
            case "C" -> PCD_SECTOR_C;

            default -> 0;

        };

        Set<Integer> pcdIndexes = distributePcdIndexes(
                REGULAR_SECTOR_SPOTS,
                totalPcdSpots
        );

        for (int number = 1; number <= REGULAR_SECTOR_SPOTS; number++) {

            ParkingSpotType parkingSpotType = pcdIndexes.contains(number)
                    ? ParkingSpotType.PCD
                    : ParkingSpotType.REGULAR;

            parkingSpots.add(
                    createParkingSpot(
                            parkingSector,
                            parkingSpotType,
                            number,
                            now
                    )
            );

        }

    }

    /**
     * Gera as vagas do setor.
     */
    private void generateSector(
            ParkingSector parkingSector,
            ParkingSpotType parkingSpotType,
            int quantity,
            List<ParkingSpot> parkingSpots) {

        LocalDateTime now = LocalDateTime.now();

        for (int number = 1; number <= quantity; number++) {

            parkingSpots.add(
                    createParkingSpot(
                            parkingSector,
                            parkingSpotType,
                            number,
                            now
                    )
            );

        }

    }

    /**
     * Cria uma vaga.
     */
    private ParkingSpot createParkingSpot(
            ParkingSector parkingSector,
            ParkingSpotType parkingSpotType,
            int number,
            LocalDateTime now
    ) {

        ParkingSpot parkingSpot = new ParkingSpot();

        parkingSpot.setCode(
                generateSpotCode(
                        parkingSector.getName(),
                        number
                )
        );

        parkingSpot.setType(parkingSpotType);
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(now);

        parkingSpot.setParkingSector(parkingSector);

        return parkingSpot;

    }

    /**
     * Distribui as vagas PCD.
     */
    private Set<Integer> distributePcdIndexes(
            int totalSpots,
            int totalPcdSpots) {

        Set<Integer> indexes = new HashSet<>();

        if (totalPcdSpots == 0) {
            return indexes;
        }

        double spacing = (double) totalSpots / (totalPcdSpots + 1);

        for (int index = 1; index <= totalPcdSpots; index++) {

            indexes.add(
                    (int) Math.round(index * spacing)
            );

        }

        return indexes;

    }

    /**
     * Gera o código da vaga.
     */
    private String generateSpotCode(
            String sector,
            int number) {

        return "%s%03d".formatted(
                sector,
                number
        );

    }

}