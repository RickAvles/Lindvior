package com.rick.smartparkingplatform.simulation.vehicle;

import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.simulation.parking.stay.StayProfile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class VehicleAttributeGenerator {

    private static final int PCD_PROBABILITY_PERCENT = 2;

    // Gera aleatoriamente o perfil de permanência do veículo.
    public StayProfile generateStayProfile() {

        int value = ThreadLocalRandom.current().nextInt(100);

        if (value < 20) {
            return StayProfile.SHORT;
        }

        if (value < 75) {
            return StayProfile.NORMAL;
        }

        if (value < 95) {
            return StayProfile.LONG;
        }

        return StayProfile.VERY_LONG;
    }

    // Gera aleatoriamente o tipo do veículo.
    public VehicleType generateVehicleType() {

        int value = ThreadLocalRandom.current().nextInt(100);

        if (value < 30) {
            return VehicleType.HATCH;
        }

        if (value < 55) {
            return VehicleType.SEDAN;
        }

        if (value < 75) {
            return VehicleType.SUV;
        }

        if (value < 90) {
            return VehicleType.MOTORCYCLE;
        }

        if (value < 95) {
            return VehicleType.PICKUP;
        }

        if (value < 98) {
            return VehicleType.VAN;
        }

        return VehicleType.ELECTRIC;
    }

    // Gera aleatoriamente a cor do veículo.
    public String generateColor() {

        int value = ThreadLocalRandom.current().nextInt(100);

        if (value < 30) {
            return "White";
        }

        if (value < 50) {
            return "Silver";
        }

        if (value < 70) {
            return "Black";
        }

        if (value < 85) {
            return "Gray";
        }

        if (value < 92) {
            return "Red";
        }

        if (value < 97) {
            return "Blue";
        }

        if (value < 98) {
            return "Green";
        }

        if (value < 99) {
            return "Brown";
        }

        return "Yellow";
    }

    // Gera aleatoriamente se o veículo possui prioridade PCD.
    public boolean generatePcd() {

        int value = ThreadLocalRandom.current().nextInt(100);

        return value < PCD_PROBABILITY_PERCENT;
    }

}