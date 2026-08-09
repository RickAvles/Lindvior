//package com.rick.smartparkingplatform.domain;
//
//import com.rick.smartparkingplatform.dto.response.DailyReportResponse;
//import com.rick.smartparkingplatform.report.DailyReportPdfGenerator;
//import org.junit.jupiter.api.Test;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class DailyReportPdfGeneratorTest {
//
//    @Test
//    void shouldGenerateDailyReportPdf() throws Exception {
//
//        DailyReportPdfGenerator generator = new DailyReportPdfGenerator();
//
//        DailyReportResponse report = new DailyReportResponse(
//                LocalDate.of(2026, 8, 8),
//                3245,
//                2536
//        );
//
//        Path file = generator.generate(report);
//
//        assertTrue(Files.exists(file));
//    }
//}
