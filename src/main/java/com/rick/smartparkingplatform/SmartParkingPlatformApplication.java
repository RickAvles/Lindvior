package com.rick.smartparkingplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartParkingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartParkingPlatformApplication.class, args);
    }

}
