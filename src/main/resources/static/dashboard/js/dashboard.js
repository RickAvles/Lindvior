/**
 * ==========================================================
 * Lindvior Dashboard
 * dashboard.js
 * ==========================================================
 */

document.addEventListener(
    "DOMContentLoaded",
    initializeDashboard
);

function initializeDashboard() {

    console.log(
        "[Dashboard] Inicializando..."
    );

    DashboardUtils.setConnection(false);

    DashboardWS.connect();

}