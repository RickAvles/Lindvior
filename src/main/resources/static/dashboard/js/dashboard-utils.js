/**
 * ==========================================================
 * Lindvior Dashboard
 * dashboard-utils.js
 * ==========================================================
 */

const DashboardUtils = (() => {

    function getElement(id) {
        return document.getElementById(id);
    }

    function setText(id, value) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        const text = value == null
            ? "-"
            : String(value);

        if (element.textContent === text) {
            return;
        }

        element.textContent = text;

    }

    function setHtml(id, value) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        if (element.innerHTML === value) {
            return;
        }

        element.innerHTML = value;

    }

    function setWidth(id, percentage) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        const width = percentage + "%";

        if (element.style.width === width) {
            return;
        }

        element.style.width = width;

    }

    function addClass(id, className) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        element.classList.add(className);

    }

    function removeClass(id, className) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        element.classList.remove(className);

    }

    function replaceClass(id, oldClass, newClass) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        element.classList.remove(oldClass);
        element.classList.add(newClass);

    }

    /*
     * Mantidas apenas por compatibilidade.
     * Não fazem mais nada para evitar
     * repaints desnecessários.
     */
    function flash() {
    }

    function animateValue() {
    }

    function formatPercentage(value) {

        if (value == null) {
            return "0%";
        }

        return Number(value).toFixed(1) + "%";

    }

    function formatInteger(value) {

        if (value == null) {
            return "0";
        }

        return Number(value)
            .toLocaleString("pt-BR");

    }

    function formatTime(value) {

        if (!value) {
            return "-";
        }

        const parts = value.split("T");

        if (parts.length < 2) {
            return value;
        }

        return parts[1]
            .split(".")[0];

    }

    function formatGateStatus(status) {

        if (!status) {
            return "-";
        }

        return status
            .replaceAll("_", " ")
            .toUpperCase();

    }

    function updateTimestamp() {

        const now = new Date();

        setText(
            "lastUpdate",
            now.toLocaleTimeString("pt-BR")
        );

    }

    function clear(id) {

        const element = getElement(id);

        if (!element) {
            return;
        }

        element.innerHTML = "";

    }

    function createElement(tag, className = null) {

        const element =
            document.createElement(tag);

        if (className) {
            element.className = className;
        }

        return element;

    }

    function append(parentId, child) {

        const parent = getElement(parentId);

        if (!parent) {
            return;
        }

        parent.appendChild(child);

    }

    function setConnection(connected) {

        const indicator =
            getElement("connectionIndicator");

        const text =
            getElement("connectionText");

        if (!indicator || !text) {
            return;
        }

        if (connected) {

            indicator.classList.remove(
                "disconnected"
            );

            indicator.classList.add(
                "connected"
            );

            text.textContent =
                "Conectado";

            document.body.classList.remove(
                "disconnected"
            );

        } else {

            indicator.classList.remove(
                "connected"
            );

            indicator.classList.add(
                "disconnected"
            );

            text.textContent =
                "Desconectado";

            document.body.classList.add(
                "disconnected"
            );

        }

    }

    return {

        getElement,

        setText,

        setHtml,

        setWidth,

        addClass,

        removeClass,

        replaceClass,

        flash,

        animateValue,

        formatPercentage,

        formatInteger,

        formatTime,

        formatGateStatus,

        updateTimestamp,

        clear,

        createElement,

        append,

        setConnection

    };

})();