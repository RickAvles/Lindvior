package com.rick.smartparkingplatform.config.initializer;

import com.rick.smartparkingplatform.config.initializer.generator.LayoutGenerator;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.enums.ParkingSectorType;
import com.rick.smartparkingplatform.enums.Role;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import com.rick.smartparkingplatform.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LayoutGenerator layoutGenerator;

    /**
     * Cria o estacionamento utilizado durante o desenvolvimento.
     */
    private Parking createParking() {

        Parking parking = new Parking();

        parking.setName("Lindvior Shopping");
        parking.setAddress("Salvador - BA");

        parking.setEntryGates(6);
        parking.setExitGates(4);

        parking.setEntryGateMinProcessingSeconds(3);
        parking.setEntryGateMaxProcessingSeconds(7);

        parking.setExitGateMinProcessingSeconds(2);
        parking.setExitGateMaxProcessingSeconds(5);

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
            ParkingSectorType type,
            Integer floor) {

        ParkingSector parkingSector = new ParkingSector();

        parkingSector.setName(name);
        parkingSector.setType(type);
        parkingSector.setFloor(floor);

        parkingSector.setParking(parking);

        parkingSector.setActive(true);
        parkingSector.setCreatedAt(LocalDateTime.now());

        return parkingSector;
    }

    /**
     * Cria os setores utilizados durante o desenvolvimento.
     */
    private List<ParkingSector> createParkingSectors(Parking parking) {

        List<ParkingSector> parkingSectors = new ArrayList<>();

        parkingSectors.add(createSector(parking, "A", ParkingSectorType.REGULAR, 1));
        parkingSectors.add(createSector(parking, "B", ParkingSectorType.REGULAR, 1));
        parkingSectors.add(createSector(parking, "C", ParkingSectorType.REGULAR, 1));
        parkingSectors.add(createSector(parking, "D", ParkingSectorType.MOTORCYCLE, 1));
        parkingSectors.add(createSector(parking, "E", ParkingSectorType.ELECTRIC, 1));
        parkingSectors.add(createSector(parking, "F", ParkingSectorType.PREMIUM, 2));

        return parkingSectorRepository.saveAll(parkingSectors);

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

        List<ParkingSector> parkingSectors =
                createParkingSectors(parking);

        layoutGenerator.generate(parkingSectors);
    }

}