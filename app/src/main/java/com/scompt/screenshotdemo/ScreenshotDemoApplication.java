package com.scompt.screenshotdemo;

import android.app.Application;
import android.content.Context;

import com.scompt.screenshotdemo.dagger.ApplicationModule;
import com.scompt.screenshotdemo.dagger.DaggerScreenshotDemoComponent;
import com.scompt.screenshotdemo.dagger.ScreenshotDemoComponent;
import com.scompt.screenshotdemo.mock.MockGeolocationModule;
import com.scompt.screenshotdemo.models.Location;

import com.jakewharton.threetenabp.AndroidThreeTen;

import timber.log.Timber;

public class ScreenshotDemoApplication extends Application {
    private ScreenshotDemoComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        Timber.plant(new Timber.DebugTree());

        Location location = Location.create("New York", 407127, -740059);

        component = DaggerScreenshotDemoComponent.builder()
                                                 .applicationModule(new ApplicationModule(this))
                                                 .mockGeolocationModule(new MockGeolocationModule(location))
                                                 .build();
    }

    public static ScreenshotDemoApplication get(Context context) {
        return (ScreenshotDemoApplication) context.getApplicationContext();
    }

    public ScreenshotDemoComponent component() {
        return this.component;
    }
}
