/**
 * ==========================================================
 * Lindvior Dashboard
 * dashboard-ui.js
 * ==========================================================
 */

const DashboardUI = (() => {

    const entryGateCache = new Map();
    const exitGateCache = new Map();

    function update(dashboard) {

        if (!dashboard) {
            return;
        }

        updateSimulation(dashboard.simulation);

        updateOccupancy(dashboard.occupancy);

        updateEntryGates(
            dashboard.occupancy.entryGates
        );

        updateExitGates(
            dashboard.occupancy.exitGates
        );

        DashboardUtils.updateTimestamp();

    }

    function updateSimulation(simulation) {

        if (!simulation) {
            return;
        }

        DashboardUtils.setText(
            "simulationState",
            simulation.simulationState
        );

        DashboardUtils.setText(
            "simulationTime",
            DashboardUtils.formatTime(
                simulation.currentTime
            )
        );

        DashboardUtils.setText(
            "weather",
            simulation.weather
        );

        DashboardUtils.setText(
            "dayType",
            simulation.dayType
        );

    }

    function updateOccupancy(occupancy) {

        if (!occupancy) {
            return;
        }

        DashboardUtils.setText(
            "availableSpots",
            DashboardUtils.formatInteger(
                occupancy.availableSpots
            )
        );

        DashboardUtils.setText(
            "occupiedSpots",
            DashboardUtils.formatInteger(
                occupancy.occupiedSpots
            )
        );

        DashboardUtils.setText(
            "occupancyRate",
            DashboardUtils.formatPercentage(
                occupancy.occupancyRate
            )
        );

        DashboardUtils.setWidth(
            "progressBar",
            occupancy.occupancyRate
        );

    }

    function updateEntryGates(gates) {

        updateGates(
            "entryGates",
            gates,
            entryGateCache
        );

    }

    function updateExitGates(gates) {

        updateGates(
            "exitGates",
            gates,
            exitGateCache
        );

    }

    function updateGates(containerId, gates, cache) {

        if (!gates) {
            return;
        }

        const container =
            DashboardUtils.getElement(containerId);

        gates.forEach(gate => {

            let item =
                cache.get(gate.gate);

            if (!item) {

                item = createGate();

                cache.set(
                    gate.gate,
                    item
                );

                container.appendChild(
                    item.container
                );

            }

            item.name.textContent =
                gate.gate;

            if (gate.available) {

                item.status.className =
                    "gate-status status-open";

                item.status.textContent =
                    "Disponível";

            } else {

                item.status.className =
                    "gate-status status-closed";

                item.status.textContent =
                    gate.vehiclePlate ?? "Ocupada";

            }

        });

    }

    function createGate() {

        const container =
            DashboardUtils.createElement(
                "div",
                "gate"
            );

        const name =
            DashboardUtils.createElement(
                "span",
                "gate-name"
            );

        const status =
            DashboardUtils.createElement(
                "span",
                "gate-status"
            );

        container.appendChild(name);

        container.appendChild(status);

        return {

            container,

            name,

            status

        };

    }

    return {

        update

    };

})();