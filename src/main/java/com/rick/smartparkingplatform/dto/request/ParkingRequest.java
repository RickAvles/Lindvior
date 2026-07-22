package com.rick.smartparkingplatform.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record ParkingRequest(

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotNull
        @Positive
        Integer entryGates,

        @NotNull
        @Positive
        Integer exitGates,

        @NotNull
        @Positive
        Integer entryGateMinProcessingSeconds,

        @NotNull
        @Positive
        Integer entryGateMaxProcessingSeconds,

        @NotNull
        @Positive
        Integer exitGateMinProcessingSeconds,

        @NotNull
        @Positive
        Integer exitGateMaxProcessingSeconds,

        @NotNull
        Boolean active,

        @NotNull
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime openingTime,

        @NotNull
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime closingTime

) {
}