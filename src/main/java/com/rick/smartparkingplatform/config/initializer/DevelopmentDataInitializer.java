package com.rick.smartparkingplatform.config.initializer;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.enums.Role;
import com.rick.smartparkingplatform.enums.SectorType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import com.rick.smartparkingplatform.repository.UserRepository;
import io.jsonwebtoken.security.Password;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevelopmentDataInitializer implements CommandLineRunner {

    private final ParkingRepository parkingRepository;
    private final ParkingSectorRepository parkingSectorRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int REGULAR_SPOTS = 200;
    private static final int MOTORCYCLE_SPOTS = 50;
    private static final int ELECTRIC_SPOTS = 20;
    private static final int PCD_SPOTS = 20;
    private static final int PREMIUM_SPOTS = 10;

    private static final int TOTAL_CAPACITY = REGULAR_SPOTS * 2
            + MOTORCYCLE_SPOTS
            + ELECTRIC_SPOTS
            + PCD_SPOTS
            + PREMIUM_SPOTS;

    /**
     * Cria o estacionamento utilizado durante o desenvolvimento.
     */
    private Parking createParking() {

        Parking parking = new Parking();

        parking.setName("Lindvior Shopping");
        parking.setAddress("Salvador - BA");

        parking.setCapacity(TOTAL_CAPACITY);

        parking.setOpeningTime(LocalTime.MIN);
        parking.setClosingTime(LocalTime.of(23, 59));

        parking.setCreatedAt(LocalDateTime.now());

        return parkingRepository.save(parking);
    }

    /**
     * Cria um setor do estacionamento.
     */
    private ParkingSector createSector(
            Parking parking,
            String name,
            SectorType type,
            Integer floor) {

        ParkingSector parkingSector = new ParkingSector();

        parkingSector.setName(name);
        parkingSector.setType(type);
        parkingSector.setFloor(floor);

        parkingSector.setParking(parking);

        parkingSector.setActive(true);
        parkingSector.setCreatedAt(LocalDateTime.now());

        return parkingSectorRepository.save(parkingSector);
    }

    /**
     * Cria as vagas de um setor do estacionamento.
     */
    private void createParkingSpots(
            ParkingSector parkingSector,
            int quantity) {

        List<ParkingSpot> parkingSpots = new ArrayList<>();

        for (int number = 1; number <= quantity; number++) {

            ParkingSpot parkingSpot = new ParkingSpot();

            parkingSpot.setCode(
                    generateSpotCode(
                            parkingSector.getName(),
                            number
                    )
            );

            parkingSpot.setStatus(StatusParkingSpot.FREE);
            parkingSpot.setActive(true);
            parkingSpot.setCreatedAt(LocalDateTime.now());

            parkingSpot.setParkingSector(parkingSector);

            parkingSpots.add(parkingSpot);
        }

        parkingSpotRepository.saveAll(parkingSpots);
    }

    /**
     * Gera o código identificador de uma vaga.
     */
    private String generateSpotCode(
            String sector,
            int number) {

        return "%s%03d".formatted(
                sector,
                number
        );
    }

    /**
     * Cria o usuário administrador padrão para o ambiente de desenvolvimento.
     */
    private void createAdminUser() {

        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User();
        LocalDateTime now = LocalDateTime.now();

        admin.setEmail("admin@lindvior.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        userRepository.save(admin);
    }

    @Override
    public void run(String @NonNull ... args) {
        if (parkingRepository.existsBy()) {
            return;
        }

        createAdminUser();

        Parking parking = createParking();

        ParkingSector sectorA = createSector(parking, "A", SectorType.REGULAR, 1);

        ParkingSector sectorB = createSector(parking, "B", SectorType.REGULAR, 1);

        ParkingSector sectorC = createSector(parking, "C", SectorType.MOTORCYCLE, 1);

        ParkingSector sectorD = createSector(parking, "D", SectorType.ELECTRIC, 1);

        ParkingSector sectorE = createSector(parking, "E", SectorType.PCD, 1);

        ParkingSector sectorF = createSector(parking, "F", SectorType.PREMIUM, 2);

        createParkingSpots(sectorA, REGULAR_SPOTS);

        createParkingSpots(sectorB, REGULAR_SPOTS);

        createParkingSpots(sectorC, MOTORCYCLE_SPOTS);

        createParkingSpots(sectorD, ELECTRIC_SPOTS);

        createParkingSpots(sectorE, PCD_SPOTS);

        createParkingSpots(sectorF, PREMIUM_SPOTS);

    }

}