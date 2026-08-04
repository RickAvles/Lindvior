/**
 * ==========================================================
 * Lindvior Dashboard
 * dashboard-ws.js
 * ==========================================================
 */

const DashboardWS = (() => {

    const WS_ENDPOINT = "/ws";

    const DASHBOARD_TOPIC = "/topic/dashboard";

    let client = null;

    function connect() {

        client = new StompJs.Client({

            brokerURL: buildBrokerUrl(),

            reconnectDelay: 5000,

            heartbeatIncoming: 10000,

            heartbeatOutgoing: 10000,

            debug: function () {
                // Desabilitado
            }

        });

        client.onConnect = onConnect;

        client.onStompError = onStompError;

        client.onWebSocketClose = onWebSocketClose;

        client.onWebSocketError = onWebSocketError;

        client.activate();

    }

    function onConnect() {

        DashboardUtils.setConnection(true);

        client.subscribe(
            DASHBOARD_TOPIC,
            onDashboardMessage
        );

        console.log(
            "[Dashboard] Conectado."
        );

    }

    function onDashboardMessage(message) {

        if (!message || !message.body) {
            return;
        }

        try {

            const dashboard =
                JSON.parse(message.body);

            DashboardUI.update(dashboard);

        } catch (error) {

            console.error(
                "[Dashboard] Erro ao processar mensagem.",
                error
            );

        }

    }

    function onStompError(frame) {

        DashboardUtils.setConnection(false);

        console.error(
            "[Dashboard] Erro STOMP.",
            frame
        );

    }

    function onWebSocketClose() {

        DashboardUtils.setConnection(false);

        console.warn(
            "[Dashboard] Conexão encerrada."
        );

    }

    function onWebSocketError(error) {

        DashboardUtils.setConnection(false);

        console.error(
            "[Dashboard] Erro WebSocket.",
            error
        );

    }

    function disconnect() {

        if (!client) {
            return;
        }

        client.deactivate();

        DashboardUtils.setConnection(false);

    }

    function buildBrokerUrl() {

        const protocol =
            window.location.protocol === "https:"
                ? "wss://"
                : "ws://";

        return (
            protocol +
            window.location.host +
            WS_ENDPOINT
        );

    }

    function isConnected() {

        return client !== null &&
            client.connected;

    }

    return {

        connect,

        disconnect,

        isConnected

    };

})();