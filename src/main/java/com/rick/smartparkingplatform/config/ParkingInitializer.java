package com.rick.smartparkingplatform.config;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ParkingInitializer implements CommandLineRunner {

    private final ParkingRepository parkingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (parkingRepository.existsBy()) {
            return;
        }

        Parking parking = new Parking();

        parking.setName("Configure your parking");
        parking.setAddress("Not configured");
        parking.setCreatedAt(LocalDateTime.now());

        parkingRepository.save(parking);
    }

}
