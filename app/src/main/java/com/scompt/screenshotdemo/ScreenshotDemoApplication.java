package com.scompt.screenshotdemo;

import android.app.Application;
import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.scompt.screenshotdemo.dagger.ApplicationModule;
import com.scompt.screenshotdemo.dagger.DaggerScreenshotDemoComponent;
import com.scompt.screenshotdemo.dagger.ScreenshotDemoComponent;

import timber.log.Timber;

public class ScreenshotDemoApplication extends Application {
    private ScreenshotDemoComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        Timber.plant(new Timber.DebugTree());

        component = buildComponent();
    }

    public static ScreenshotDemoApplication get(Context context) {
        return (ScreenshotDemoApplication) context.getApplicationContext();
    }

    public ScreenshotDemoComponent component() {
        return this.component;
    }

    protected ScreenshotDemoComponent buildComponent() {
        return DaggerScreenshotDemoComponent.builder()
                                            .applicationModule(new ApplicationModule(this))
                                            .build();
    }
}
