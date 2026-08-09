package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.response.DailyReportResponse;
import com.rick.smartparkingplatform.dto.response.OccupancyReport;
import com.rick.smartparkingplatform.dto.response.SectorReport;
import com.rick.smartparkingplatform.dto.response.VehicleFlowReport;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.report.DailyReportPdfGenerator;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ParkingSessionService parkingSessionService;
    private final ParkingSectorRepository parkingSectorRepository;
    private final DailyReportPdfGenerator dailyReportPdfGenerator;

    // Gera o relatório diário reconstruindo o comportamento do estacionamento
    // a partir das sessões registradas no banco.
    @Transactional(readOnly = true)
    public DailyReportResponse generateDailyReport(LocalDate reportDate) {

        LocalDateTime start = reportDate.atStartOfDay();
        LocalDateTime end = reportDate.plusDays(1).atStartOfDay();

        // Busca todas as sessões iniciadas durante o dia informado.
        List<ParkingSession> sessions = parkingSessionService.findByEntryTimeBetween(start, end);

        // Busca todos os setores existentes no estacionamento.
        // Isso garante que setores sem nenhuma sessão também apareçam
        // no relatório com ocupação igual a zero.
        List<ParkingSector> sectors = parkingSectorRepository.findAll();

        // Calcula o fluxo de veículos do período.
        VehicleFlowReport vehicleFlow = buildVehicleFlowReport(sessions);

        // Reconstrói a ocupação do estacionamento ao longo do dia.
        OccupancyReport occupancy = buildOccupancyReport(
                sessions,
                sectors,
                start,
                end
        );

        // Reconstrói o comportamento de cada setor ao longo do dia.
        List<SectorReport> sectorReports = buildSectorReports(
                sessions,
                sectors,
                start,
                end
        );

        return new DailyReportResponse(reportDate,
                LocalDateTime.now(),
                vehicleFlow,
                occupancy,
                sectorReports
        );
    }

    // Gera o PDF do relatório diário.
    @Transactional(readOnly = true)
    public Path generateDailyReportPdf(LocalDate reportDate) throws IOException {

        DailyReportResponse report = generateDailyReport(reportDate);

        return dailyReportPdfGenerator.generate(report);
    }

    // Calcula as métricas de fluxo de veículos do dia.
    private VehicleFlowReport buildVehicleFlowReport(List<ParkingSession> sessions) {

        long totalEntries = sessions.size();

        // Sessões que tiveram saída durante o período.
        long totalExits = sessions.stream()
                .filter(session -> session.getExitTime() != null)
                .count();

        // Sessões concluídas.
        List<ParkingSession> completedSessions = sessions.stream()
                .filter(session -> session.getExitTime() != null)
                .toList();

        // Sessões que ainda não possuem saída registrada.
        long activeSessions = sessions.stream()
                .filter(session -> session.getExitTime() == null)
                .count();

        double averageStayMinutes = completedSessions.stream()
                .mapToLong(this::calculateStayMinutes)
                .average()
                .orElse(0.0);

        long longestStayMinutes = completedSessions.stream()
                .mapToLong(this::calculateStayMinutes)
                .max()
                .orElse(0);

        long shortestStayMinutes = completedSessions.stream()
                .mapToLong(this::calculateStayMinutes)
                .min()
                .orElse(0);

        return new VehicleFlowReport(
                totalEntries,
                totalExits,
                activeSessions,
                averageStayMinutes,
                longestStayMinutes,
                shortestStayMinutes
        );
    }

    // Reconstrói a ocupação do estacionamento em intervalos de uma hora.
    private OccupancyReport buildOccupancyReport(
            List<ParkingSession> sessions,
            List<ParkingSector> sectors,
            LocalDateTime start,
            LocalDateTime end) {

        // A capacidade vem de todos os setores existentes,
        // e não apenas dos setores que receberam sessões.
        long totalSpots =
                findTotalSpots(sectors);

        long samples = 0;

        long totalOccupied = 0;

        long maximumOccupied = 0;

        long minimumOccupied = Long.MAX_VALUE;

        LocalDateTime peakTime = start;

        for (LocalDateTime timestamp = start;
             timestamp.isBefore(end);
             timestamp = timestamp.plusHours(1)) {

            long occupied = countOccupiedSessions(
                    sessions,
                    timestamp
            );

            totalOccupied += occupied;

            samples++;

            if (occupied > maximumOccupied) {

                maximumOccupied = occupied;

                peakTime = timestamp;
            }

            if (occupied < minimumOccupied) {
                minimumOccupied = occupied;
            }
        }

        if (samples == 0) {
            minimumOccupied = 0;
        }

        double averageOccupancy = samples == 0
                ? 0.0
                : (double) totalOccupied / samples;

        double averageOccupancyRate = totalSpots == 0
                ? 0.0
                : (averageOccupancy / totalSpots) * 100;

        double maximumOccupancyRate = totalSpots == 0
                ? 0.0
                : ((double) maximumOccupied / totalSpots) * 100;

        double minimumOccupancyRate =
                totalSpots == 0
                        ? 0.0
                        : ((double) minimumOccupied / totalSpots) * 100;

        return new OccupancyReport(
                totalSpots,
                averageOccupancy,
                maximumOccupied,
                minimumOccupied,
                averageOccupancyRate,
                maximumOccupancyRate,
                minimumOccupancyRate,
                peakTime
        );
    }

    // Reconstrói a ocupação histórica de todos os setores existentes.
    private List<SectorReport> buildSectorReports(
            List<ParkingSession> sessions,
            List<ParkingSector> sectors,
            LocalDateTime start,
            LocalDateTime end) {

        return sectors.stream()
                .map(sector -> {

                    // Seleciona somente as sessões que pertencem
                    // ao setor atualmente analisado.
                    List<ParkingSession> sectorSessions = sessions.stream()
                            .filter(session ->
                                    session.getParkingSpot() != null
                                            && session.getParkingSpot()
                                            .getParkingSector() != null
                                            && session.getParkingSpot()
                                            .getParkingSector()
                                            .getId()
                                            .equals(sector.getId())
                            )
                            .toList();

                    // A capacidade é baseada em todas as vagas ativas
                    // existentes no setor, mesmo que nenhuma tenha sido usada.
                    long capacity = sector.getParkingSpots()
                            .stream()
                            .filter(ParkingSpot::isActive)
                            .count();

                    long samples = 0;

                    long totalOccupied = 0;

                    long maximumOccupied = 0;

                    for (LocalDateTime timestamp = start;
                         timestamp.isBefore(end);
                         timestamp = timestamp.plusHours(1)) {

                        long occupied = countOccupiedSessions(
                                sectorSessions,
                                timestamp
                        );

                        totalOccupied += occupied;

                        samples++;

                        maximumOccupied = Math.max(
                                maximumOccupied,
                                occupied
                        );
                    }

                    double averageOccupied = samples == 0
                            ? 0.0
                            : (double) totalOccupied / samples;

                    double averageOccupancyRate = capacity == 0
                            ? 0.0
                            : (averageOccupied / capacity) * 100;

                    double maximumOccupancyRate = capacity == 0
                            ? 0.0
                            : ((double) maximumOccupied / capacity) * 100;

                    return new SectorReport(
                            sector.getName(),
                            sector.getType().name(),
                            sector.getFloor(),
                            capacity,
                            averageOccupied,
                            maximumOccupied,
                            averageOccupancyRate,
                            maximumOccupancyRate
                    );
                })
                .toList();
    }

    // Conta quantas sessões estavam ocupando vagas no instante analisado.
    private long countOccupiedSessions(List<ParkingSession> sessions, LocalDateTime timestamp) {

        return sessions.stream()
                .filter(session ->
                        !session.getEntryTime().isAfter(timestamp)
                )
                .filter(session ->
                        session.getExitTime() == null
                                || session.getExitTime().isAfter(timestamp)
                )
                .count();
    }

    // Calcula a capacidade total usando todos os setores existentes.
    private long findTotalSpots(List<ParkingSector> sectors) {

        return sectors.stream()
                .flatMap(sector ->
                        sector.getParkingSpots().stream()
                )
                .filter(ParkingSpot::isActive)
                .count();
    }

    // Calcula a duração de uma sessão em minutos.
    private long calculateStayMinutes(ParkingSession session) {

        return Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes();
    }
}