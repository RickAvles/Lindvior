package com.rick.smartparkingplatform.report;

import com.rick.smartparkingplatform.dto.response.DailyReportResponse;
import com.rick.smartparkingplatform.dto.response.OccupancyReport;
import com.rick.smartparkingplatform.dto.response.SectorReport;
import com.rick.smartparkingplatform.dto.response.VehicleFlowReport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DailyReportPdfGenerator {

    private static final float PAGE_WIDTH = 595;
    private static final float PAGE_HEIGHT = 842;
    private static final float MARGIN = 45;

    private static final float CONTENT_WIDTH =
            PAGE_WIDTH - (MARGIN * 2);

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    // Gera o PDF do relatório diário.
    public Path generate(DailyReportResponse report) throws IOException {

        Path directory = Path.of("reports");

        // Cria o diretório caso ainda não exista.
        Files.createDirectories(directory);

        Path file = directory.resolve(
                "daily-report-" + report.reportDate() + ".pdf"
        );

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();

            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                float y = PAGE_HEIGHT - MARGIN;

                // Desenha o cabeçalho do relatório.
                y = drawHeader(
                        content,
                        report,
                        y
                );

                // Desenha os principais indicadores do dia.
                y = drawOperationalSummary(
                        content,
                        report,
                        y
                );

                // Desenha o fluxo histórico de veículos.
                y = drawVehicleFlow(
                        content,
                        report.vehicleFlow(),
                        y
                );

                // Desenha o desempenho histórico da ocupação.
                y = drawOccupancy(
                        content,
                        report.occupancy(),
                        y
                );

                // Desenha o desempenho histórico dos setores.
                drawSectorSummary(
                        content,
                        report.sectors(),
                        y
                );
            }

            document.save(file.toFile());
        }

        return file;
    }

    // Desenha o cabeçalho do relatório.
    private float drawHeader(
            PDPageContentStream content,
            DailyReportResponse report,
            float y) throws IOException {

        writeText(
                content,
                "LINDVIOR",
                MARGIN,
                y,
                20,
                true
        );

        y -= 25;

        writeText(
                content,
                "DAILY OPERATIONAL REPORT",
                MARGIN,
                y,
                11,
                true
        );

        y -= 20;

        writeText(
                content,
                "Operational date: " + report.reportDate(),
                MARGIN,
                y,
                9,
                false
        );

        y -= 14;

        writeText(
                content,
                "Generated at: " +
                        report.generatedAt()
                                .format(DATE_TIME_FORMAT),
                MARGIN,
                y,
                9,
                false
        );

        y -= 18;

        drawLine(
                content,
                MARGIN,
                y,
                CONTENT_WIDTH
        );

        return y - 20;
    }

    // Desenha os principais indicadores do dia.
    private float drawOperationalSummary(
            PDPageContentStream content,
            DailyReportResponse report,
            float y) throws IOException {

        y = drawSectionTitle(
                content,
                "OPERATIONAL SUMMARY",
                y
        );

        VehicleFlowReport flow =
                report.vehicleFlow();

        OccupancyReport occupancy =
                report.occupancy();

        writeMetric(
                content,
                "Total entries",
                String.valueOf(flow.totalEntries()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Completed sessions",
                String.valueOf(flow.completedSessions()),
                MARGIN + 270,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Average stay",
                formatMinutes(flow.averageStayMinutes()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Average occupancy",
                formatPercentage(occupancy.averageOccupancyRate()),
                MARGIN + 270,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Peak occupancy",
                occupancy.maximumOccupied() + " vehicles",
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Peak time",
                occupancy.peakTime().format(TIME_FORMAT),
                MARGIN + 270,
                y
        );

        return y - 30;
    }

    // Desenha as métricas históricas de fluxo.
    private float drawVehicleFlow(
            PDPageContentStream content,
            VehicleFlowReport flow,
            float y) throws IOException {

        y = drawSectionTitle(
                content,
                "VEHICLE FLOW",
                y
        );

        writeMetric(
                content,
                "Total entries",
                String.valueOf(flow.totalEntries()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Completed sessions",
                String.valueOf(flow.completedSessions()),
                MARGIN + 270,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Average stay",
                formatMinutes(flow.averageStayMinutes()),
                MARGIN,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Shortest stay",
                formatMinutes(flow.shortestStayMinutes()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Longest stay",
                formatMinutes(flow.longestStayMinutes()),
                MARGIN + 270,
                y
        );

        return y - 30;
    }

    // Desenha o desempenho histórico da ocupação.
    private float drawOccupancy(
            PDPageContentStream content,
            OccupancyReport occupancy,
            float y) throws IOException {

        y = drawSectionTitle(
                content,
                "DAILY OCCUPANCY PERFORMANCE",
                y
        );

        writeMetric(
                content,
                "Average occupied",
                formatDecimal(occupancy.averageOccupied()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Average occupancy",
                formatPercentage(occupancy.averageOccupancyRate()),
                MARGIN + 270,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Maximum occupied",
                String.valueOf(occupancy.maximumOccupied()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Maximum occupancy",
                formatPercentage(occupancy.maximumOccupancyRate()),
                MARGIN + 270,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Minimum occupied",
                String.valueOf(occupancy.minimumOccupied()),
                MARGIN,
                y
        );

        writeMetric(
                content,
                "Minimum occupancy",
                formatPercentage(occupancy.minimumOccupancyRate()),
                MARGIN + 270,
                y
        );

        y -= 22;

        writeMetric(
                content,
                "Peak time",
                occupancy.peakTime().format(TIME_FORMAT),
                MARGIN,
                y
        );

        return y - 30;
    }

    // Desenha o desempenho histórico dos setores.
    private void drawSectorSummary(
            PDPageContentStream content,
            List<SectorReport> sectors,
            float y) throws IOException {

        y = drawSectionTitle(
                content,
                "SECTOR PERFORMANCE",
                y
        );

        float rowHeight = 22;

        float[] widths = {
                65,
                100,
                45,
                70,
                80,
                80
        };

        String[] headers = {
                "Sector",
                "Type",
                "Floor",
                "Capacity",
                "Avg. Occupied",
                "Max. Occupied"
        };

        float x = MARGIN;

        // Desenha o cabeçalho da tabela.
        for (int i = 0; i < headers.length; i++) {

            drawCell(
                    content,
                    headers[i],
                    x,
                    y,
                    widths[i],
                    rowHeight,
                    true
            );

            x += widths[i];
        }

        y -= rowHeight;

        // Desenha os dados históricos de cada setor.
        for (SectorReport sector : sectors) {

            x = MARGIN;

            String[] values = {
                    sector.sectorName(),
                    sector.sectorType(),
                    String.valueOf(sector.floor()),
                    String.valueOf(sector.capacity()),
                    formatDecimal(sector.averageOccupied()),
                    String.valueOf(sector.maximumOccupied())
            };

            for (int i = 0; i < values.length; i++) {

                drawCell(
                        content,
                        values[i],
                        x,
                        y,
                        widths[i],
                        rowHeight,
                        false
                );

                x += widths[i];
            }

            y -= rowHeight;
        }
    }

    // Desenha o título de uma seção.
    private float drawSectionTitle(
            PDPageContentStream content,
            String title,
            float y) throws IOException {

        writeText(
                content,
                title,
                MARGIN,
                y,
                12,
                true
        );

        y -= 7;

        drawLine(
                content,
                MARGIN,
                y,
                CONTENT_WIDTH
        );

        return y - 18;
    }

    // Escreve uma métrica com seu valor.
    private void writeMetric(
            PDPageContentStream content,
            String label,
            String value,
            float x,
            float y) throws IOException {

        writeText(
                content,
                label,
                x,
                y,
                9,
                false
        );

        writeText(
                content,
                value,
                x + 130,
                y,
                9,
                true
        );
    }

    // Desenha uma célula da tabela.
    private void drawCell(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float width,
            float height,
            boolean header) throws IOException {

        content.addRect(
                x,
                y,
                width,
                height
        );

        content.stroke();

        writeText(
                content,
                text,
                x + 4,
                y + 7,
                7,
                header
        );
    }

    // Escreve um texto no PDF.
    private void writeText(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float size,
            boolean bold) throws IOException {

        PDType1Font font = new PDType1Font(
                bold
                        ? Standard14Fonts.FontName.HELVETICA_BOLD
                        : Standard14Fonts.FontName.HELVETICA
        );

        content.beginText();

        content.setFont(
                font,
                size
        );

        content.newLineAtOffset(
                x,
                y
        );

        content.showText(text);

        content.endText();
    }

    // Desenha uma linha horizontal.
    private void drawLine(
            PDPageContentStream content,
            float x,
            float y,
            float width) throws IOException {

        content.moveTo(x, y);
        content.lineTo(x + width, y);
        content.stroke();
    }

    // Formata percentuais.
    private String formatPercentage(double value) {

        return String.format(
                "%.2f%%",
                value
        );
    }

    // Formata números decimais.
    private String formatDecimal(double value) {

        return String.format(
                "%.2f",
                value
        );
    }

    // Formata duração em minutos.
    private String formatMinutes(double value) {

        return String.format(
                "%.2f min",
                value
        );
    }

    // Formata duração inteira em minutos.
    private String formatMinutes(long value) {

        return value + " min";
    }
}