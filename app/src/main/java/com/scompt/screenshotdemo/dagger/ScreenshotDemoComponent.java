package com.scompt.screenshotdemo.dagger;

import com.scompt.screenshotdemo.MainActivity;
import com.scompt.screenshotdemo.mock.MockGeolocationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, ForecastIoModule.class, MockGeolocationModule.class })
public interface ScreenshotDemoComponent {
    void inject(MainActivity target);
}
