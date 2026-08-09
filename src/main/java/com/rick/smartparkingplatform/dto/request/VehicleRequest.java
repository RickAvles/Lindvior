package com.rick.smartparkingplatform.dto.request;

import com.rick.smartparkingplatform.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record VehicleRequest(

        @Pattern(
                regexp = "[A-Z]{3}(\\d{4}|\\d[A-Z]\\d{2})$",
                message = "License plate must follow the Brazilian format."
        )
        @NotBlank
        String licensePlate,

        @NotNull
        VehicleType type,

        @NotBlank
        String color,

        boolean pcd

) {
}