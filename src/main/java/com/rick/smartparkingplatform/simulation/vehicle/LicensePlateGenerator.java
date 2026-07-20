package com.rick.smartparkingplatform.simulation.vehicle;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class LicensePlateGenerator {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";

    // Gera uma placa veicular aleatória.
    public String generateLicensePlate() {

        if (ThreadLocalRandom.current().nextBoolean()) {
            return generateOldLicensePlate();
        }

        return generateMercosulLicensePlate();
    }

    // Gera uma placa no padrão brasileiro antigo.
    private String generateOldLicensePlate() {

        return String.valueOf(randomLetter()) +
                randomLetter() +
                randomLetter() +
                randomNumber() +
                randomNumber() +
                randomNumber() +
                randomNumber();
    }

    // Gera uma placa no padrão Mercosul.
    private String generateMercosulLicensePlate() {

        return String.valueOf(randomLetter()) +
                randomLetter() +
                randomLetter() +
                randomNumber() +
                randomLetter() +
                randomNumber() +
                randomNumber();
    }

    // Retorna uma letra aleatória.
    private char randomLetter() {

        return LETTERS.charAt(ThreadLocalRandom.current().nextInt(LETTERS.length()));
    }

    // Retorna um número aleatório.
    private char randomNumber() {

        return NUMBERS.charAt(ThreadLocalRandom.current().nextInt(NUMBERS.length()));
    }

}