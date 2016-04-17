package com.scompt.screenshotdemo;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import timber.log.Timber;

public class ScreenshotDemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        Timber.plant(new Timber.DebugTree());
    }
}
