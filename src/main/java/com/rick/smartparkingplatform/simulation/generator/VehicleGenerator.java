package com.rick.smartparkingplatform.simulation.generator;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.service.VehicleService;
import com.rick.smartparkingplatform.simulation.enums.StayProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class VehicleGenerator {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";

    private final VehicleService vehicleService;
    private final ParkingService parkingService;

    private final Random random = new Random();

    /**
     * Retorna o próximo veículo da simulação.
     */
    public Vehicle generateVehicle() {

        double probability = calculateNewVehicleProbability();

        boolean generateNewVehicle = random.nextDouble() < probability;

        if (generateNewVehicle) {
            return createNewVehicle();
        }

        return getExistingVehicle();
    }

    /**
     * Calcula a probabilidade de geração de um novo veículo.
     */
    private double calculateNewVehicleProbability() {

        long capacity = parkingService.getParking().capacity();

        long population = vehicleService.count();

        long threshold = capacity * 2L;

        if (population < threshold) {
            return 1.0;
        }

        double probability = 0.5;

        long currentThreshold = threshold;

        while (population >= currentThreshold * 2) {
            probability /= 2;
            currentThreshold *= 2;
        }

        return probability;
    }

    /**
     * Gera e cadastra um novo veículo.
     */
    private Vehicle createNewVehicle() {

        String licensePlate;

        do {
            licensePlate = generateLicensePlate();
        } while (vehicleService.existsByLicensePlate(licensePlate));

        VehicleType type = generateVehicleType();

        String color = generateColor();

        StayProfile stayProfile = generateStayProfile();

        return vehicleService.createGeneratedVehicle(
                licensePlate,
                type,
                color,
                stayProfile
        );
    }

    /**
     * Gera aleatoriamente o perfil de permanência de um veículo
     * utilizando distribuição ponderada.
     */
    private StayProfile generateStayProfile() {

        int value = random.nextInt(100);

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

    /**
     * Seleciona um veículo existente da população.
     */
    private Vehicle getExistingVehicle() {

        int position = random.nextInt((int) vehicleService.count());

        return vehicleService.getVehicleAtPosition(position);
    }

    /**
     * Gera uma placa veicular aleatória.
     */
    private String generateLicensePlate() {

        if (random.nextBoolean()) {
            return generateOldLicensePlate();
        }

        return generateMercosulLicensePlate();
    }

    /**
     * Gera uma placa no padrão brasileiro antigo.
     */
    private String generateOldLicensePlate() {

        return String.valueOf(randomLetter()) +
                randomLetter() +
                randomLetter() +
                randomNumber() +
                randomNumber() +
                randomNumber() +
                randomNumber();
    }

    /**
     * Gera uma placa no padrão Mercosul.
     */
    private String generateMercosulLicensePlate() {

        return String.valueOf(randomLetter()) +
                randomLetter() +
                randomLetter() +
                randomNumber() +
                randomLetter() +
                randomNumber() +
                randomNumber();
    }

    /**
     * Retorna uma letra aleatória.
     */
    private char randomLetter() {

        return LETTERS.charAt(random.nextInt(LETTERS.length()));
    }

    /**
     * Retorna um número aleatório.
     */
    private char randomNumber() {

        return NUMBERS.charAt(random.nextInt(NUMBERS.length()));
    }

    /**
     * Gera aleatoriamente o tipo de um veículo utilizando distribuição ponderada.
     */
    private VehicleType generateVehicleType() {

        int value = random.nextInt(100);

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

    /**
     * Gera aleatoriamente a cor de um veículo utilizando distribuição ponderada.
     */
    private String generateColor() {

        int value = random.nextInt(100);

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

}