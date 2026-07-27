package com.rick.smartparkingplatform.simulation.metrics.statistics;

import java.math.BigDecimal;

public record ParkingOccupancy(

        // Quantidade total de vagas do estacionamento.
        long totalSpots,

        // Quantidade de vagas disponíveis.
        long availableSpots,

        // Quantidade de vagas ocupadas.
        long occupiedSpots,

        // Percentual de ocupação do estacionamento.
        BigDecimal occupancyRate

) {
}