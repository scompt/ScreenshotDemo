package com.scompt.screenshotdemo.test;

import com.scompt.screenshotdemo.GeolocationService;
import com.scompt.screenshotdemo.WeatherService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockModule {
    @Provides
    @Singleton
    GeolocationService provideMockGeolocationService() {
        return mock(GeolocationService.class);
    }

    @Provides
    @Singleton
    WeatherService provideMockWeatherService() {
        return mock(WeatherService.class);
    }
}
