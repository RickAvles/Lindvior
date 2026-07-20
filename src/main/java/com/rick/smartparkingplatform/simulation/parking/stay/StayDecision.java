package com.rick.smartparkingplatform.simulation.parking.stay;

public record StayDecision(
        boolean shouldExit,
        double probability
) {
}