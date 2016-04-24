package com.scompt.screenshotdemo.dagger;

import com.scompt.screenshotdemo.WeatherService;
import com.scompt.screenshotdemo.forecastio.ForecastIoWeatherService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class ForecastIoModule {
    @Provides
    WeatherService provideWeatherService(ForecastIoWeatherService impl) {
        return impl;
    }

    @Provides
    @Named("IpApiKey")
    String provideIpApiKey() {
        return "96ca26ee5397181411f9ba4311a5a799";
    }
}
