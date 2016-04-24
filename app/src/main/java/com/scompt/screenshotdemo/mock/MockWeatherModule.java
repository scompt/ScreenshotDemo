package com.scompt.screenshotdemo.mock;

import com.scompt.screenshotdemo.WeatherService;
import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;
import com.scompt.screenshotdemo.models.WeatherDatum;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import rx.Single;

@Module
public class MockWeatherModule {
    private final List<WeatherDatum> weatherData;

    public MockWeatherModule(WeatherDatum... weatherData) {
        this.weatherData = Arrays.asList(weatherData);
    }

    @Provides
    WeatherService provideMockWeatherService() {
        return new WeatherService() {
            @Override
            public Single<LocationWeather> weatherForLocation(Location location) {
                List<WeatherDatum> weather = weatherData;
                return Single.just(LocationWeather.create(location, weather));
            }
        };
    }
}
