package com.scompt.screenshotdemo;

import android.app.Application;
import android.content.Context;

import com.scompt.screenshotdemo.dagger.DaggerScreenshotDemoComponent;
import com.scompt.screenshotdemo.dagger.ScreenshotDemoComponent;
import com.scompt.screenshotdemo.mock.MockGeolocationModule;
import com.scompt.screenshotdemo.mock.MockWeatherModule;
import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.WeatherDatum;
import com.scompt.screenshotdemo.models.WeatherIcon;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import timber.log.Timber;

public class ScreenshotDemoApplication extends Application {
    private ScreenshotDemoComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        Timber.plant(new Timber.DebugTree());

        Location location = Location.create("New York", 407127, -740059);
        WeatherDatum weatherDatum1 = WeatherDatum.create("Cloudy with a chance of meatballs",
                                                         WeatherIcon.CLOUDY, 10, 20,
                                                         Instant.now());
        WeatherDatum weatherDatum2 = WeatherDatum.create("Always sunny in Philadelphia",
                                                         WeatherIcon.CLEAR_DAY, 15, 25,
                                                         Instant.now().plus(1, ChronoUnit.DAYS));

        component = DaggerScreenshotDemoComponent.builder()
                                                 .mockGeolocationModule(new MockGeolocationModule(location))
                                                 .mockWeatherModule(new MockWeatherModule(weatherDatum1, weatherDatum2))
                                                 .build();
    }

    public static ScreenshotDemoApplication get(Context context) {
        return (ScreenshotDemoApplication) context.getApplicationContext();
    }

    public ScreenshotDemoComponent component() {
        return this.component;
    }
}
