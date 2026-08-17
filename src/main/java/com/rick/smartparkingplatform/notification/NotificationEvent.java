package com.rick.smartparkingplatform.notification;

import java.time.LocalDateTime;

public record NotificationEvent(

        NotificationType type,

        NotificationSeverity severity,

        String message,

        LocalDateTime occurredAt
) {
}
