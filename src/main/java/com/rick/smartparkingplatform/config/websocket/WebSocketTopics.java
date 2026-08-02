package com.rick.smartparkingplatform.config.websocket;

public final class WebSocketTopics {

    // Tópico responsável pelos eventos da dashboard.
    public static final String DASHBOARD_EVENTS = "/topic/dashboard/events";

    private WebSocketTopics() {

        throw new UnsupportedOperationException(
                "Utility class."
        );
    }

}