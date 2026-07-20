package com.rick.smartparkingplatform.simulation.conditions.weather;

import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SimulationWeatherService {

    private final SimulationClock simulationClock;
    private final WeatherContext weatherContext;
    private final WeatherProfileRules weatherProfileRules;

    //Inicializa o módulo climático da simulação.
    public void initialize() {

        WeatherType initialProfile = drawRandomProfile();

        weatherContext.setActiveProfile(initialProfile);
        weatherContext.setCurrentWeather(initialProfile);

        scheduleNextProfile();
        scheduleNextWeatherChange();
    }

    //Atualiza o estado climático da simulação.
    public void update() {

        activatePendingProfile();

        updateCurrentWeather();
    }

    //Retorna o clima atual da simulação.
    public WeatherType getCurrentWeather() {
        return weatherContext.getCurrentWeather();
    }

    //Ativa o próximo perfil climático quando chega o momento.
    private void activatePendingProfile() {

        if (weatherContext.getPendingProfile() == null) {
            return;
        }

        if (simulationClock.getCurrentTime().isBefore(weatherContext.getProfileActivationTime())) {
            return;
        }

        weatherContext.setActiveProfile(weatherContext.getPendingProfile());

        weatherContext.setPendingProfile(null);
        weatherContext.setProfileActivationTime(null);

        // Agenda imediatamente o próximo perfil.
        scheduleNextProfile();
    }

    //Atualiza o clima conforme o perfil climático ativo.
    private void updateCurrentWeather() {

        if (weatherContext.getNextWeatherChange() == null) {
            scheduleNextWeatherChange();
            return;
        }

        if (simulationClock.getCurrentTime().isBefore(weatherContext.getNextWeatherChange())) {
            return;
        }

        List<WeatherProbability> probabilities =
                weatherProfileRules.getProfile(weatherContext.getActiveProfile());

        weatherContext.setCurrentWeather(drawWeather(probabilities));

        scheduleNextWeatherChange();
    }

    //Agenda a próxima mudança de perfil climático.

    private void scheduleNextProfile() {

        WeatherType nextProfile = drawRandomProfile();

        weatherContext.setPendingProfile(nextProfile);

        int minutes = ThreadLocalRandom.current().nextInt(1, 61);

        weatherContext.setProfileActivationTime(
                simulationClock.getCurrentTime().plusMinutes(minutes));
    }

    //Agenda o próximo sorteio do clima.
    private void scheduleNextWeatherChange() {

        int minutes = ThreadLocalRandom.current().nextInt(30, 91);

        weatherContext.setNextWeatherChange(
                simulationClock.getCurrentTime().plusMinutes(minutes));
    }

    //Sorteia um perfil climático predominante.
    private WeatherType drawRandomProfile() {

        WeatherType[] values = WeatherType.values();

        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }

    //Sorteia um clima utilizando a distribuição do perfil ativo.
    private WeatherType drawWeather(List<WeatherProbability> probabilities) {

        int random = ThreadLocalRandom.current().nextInt(100) + 1;

        int accumulated = 0;

        for (WeatherProbability probability : probabilities) {

            accumulated += probability.probability();

            if (random <= accumulated) {
                return probability.weather();
            }
        }

        return probabilities.getLast().weather();
    }

}