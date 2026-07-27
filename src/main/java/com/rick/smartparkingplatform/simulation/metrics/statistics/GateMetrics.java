package com.rick.smartparkingplatform.simulation.metrics.statistics;

public record GateMetrics(

        // Identificação da cancela.
        String gate,

        // Disponibilidade da cancela.
        boolean available,

        // Placa do veículo em atendimento.
        String vehiclePlate

) {
}