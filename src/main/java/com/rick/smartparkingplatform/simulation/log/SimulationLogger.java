package com.rick.smartparkingplatform.simulation.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SimulationLogger {

    // =========================
    // Entrada e saída
    // =========================

    // Registra a entrada de um veículo no estacionamento.
    public void entry(String plate) {

        log.info(
                "[ENTRY] Veículo {} entrou no estacionamento.",
                plate
        );
    }

    // Registra a saída de um veículo da vaga.
    public void exit(String plate, double probability) {

        log.info(
                "[EXIT] Veículo {} saiu da vaga. Probabilidade={}% ",
                plate,
                formatPercentage(probability)
        );
    }

    // Registra a saída definitiva do veículo do estacionamento.
    public void leave(String plate) {

        log.info(
                "[LEAVE] Veículo {} saiu do estacionamento.",
                plate
        );
    }

    // =========================
    // Recuperação
    // =========================

    // Registra o início do processo de recuperação.
    public void startup(
            LocalDateTime startedAt,
            long durationMinutes) {

        log.info(
                "[RECOVERY] Período de recuperação iniciado. Início={} Duração={} minutos.",
                startedAt,
                durationMinutes
        );
    }

    // Registra a recuperação de uma sessão após reinício da aplicação.
    public void recovery(
            String plate,
            long recoveryElapsedSeconds,
            double probability) {

        log.info(
                "[RECOVERY] Veículo {} | segundo={} | probabilidade={}% ",
                plate,
                recoveryElapsedSeconds,
                formatPercentage(probability)
        );
    }

    // =========================
    // Fila de entrada
    // =========================

    // Registra a chegada de um veículo na fila de entrada.
    public void entryQueue(
            String plate,
            int queueSize) {

        log.info(
                "[QUEUE-IN] Veículo {} entrou na fila de entrada. Tamanho={}",
                plate,
                queueSize
        );
    }

    // Registra a liberação de um veículo da fila de entrada.
    public void entryQueueRelease(
            String plate,
            int queueSize) {

        log.info(
                "[QUEUE-IN] Veículo {} saiu da fila de entrada. Restantes={}",
                plate,
                queueSize
        );
    }

    // =========================
    // Fila de estacionamento
    // =========================

    // Registra a chegada de uma sessão na fila de deslocamento até a vaga.
    public void enteringQueue(
            String plate,
            int queueSize) {

        log.info(
                "[QUEUE-PARK] Veículo {} entrou na fila de estacionamento. Tamanho={}",
                plate,
                queueSize
        );
    }

    // Registra a liberação de uma sessão da fila de deslocamento até a vaga.
    public void enteringQueueRelease(
            String plate,
            int queueSize) {

        log.info(
                "[QUEUE-PARK] Veículo {} saiu da fila de estacionamento. Restantes={}",
                plate,
                queueSize
        );
    }

    // =========================
    // Fila de saída
    // =========================

    // Registra a chegada de um veículo na fila de saída.
    public void exitQueue(
            String plate,
            int queueSize) {

        log.info(
                "[QUEUE-OUT] Veículo {} entrou na fila de saída. Tamanho={}",
                plate,
                queueSize
        );
    }

    // Registra a liberação de um veículo da fila de saída.
    public void exitQueueRelease(
            String plate,
            int queueSize) {

        log.info(
                "[QUEUE-OUT] Veículo {} saiu da fila de saída. Restantes={}",
                plate,
                queueSize
        );
    }

    // Formata uma probabilidade como percentual com duas casas decimais.
    private String formatPercentage(double probability) {

        return "%.2f".formatted(probability * 100);
    }

}