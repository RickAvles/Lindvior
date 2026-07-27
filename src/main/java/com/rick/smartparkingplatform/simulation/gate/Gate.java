package com.rick.smartparkingplatform.simulation.gate;

import com.rick.smartparkingplatform.entity.ParkingSession;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Gate {

    // Identificador da cancela.
    private final int number;

    // Momento em que a cancela ficará disponível novamente.
    private LocalDateTime availableAt = LocalDateTime.MIN;

    // Sessão que está utilizando a cancela.
    private ParkingSession currentSession;

    public Gate(int number) {

        this.number = number;
    }

    public boolean isAvailable(LocalDateTime currentTime) {
        return !currentTime.isBefore(availableAt);
    }

}