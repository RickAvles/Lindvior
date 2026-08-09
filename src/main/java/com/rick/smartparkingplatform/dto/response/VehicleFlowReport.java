package com.rick.smartparkingplatform.dto.response;

public record VehicleFlowReport(
        long totalEntries,
        long completedSessions,
        long activeSessions,
        double averageStayMinutes,
        double longestStayMinutes,
        double shortestStayMinutes
) {
}