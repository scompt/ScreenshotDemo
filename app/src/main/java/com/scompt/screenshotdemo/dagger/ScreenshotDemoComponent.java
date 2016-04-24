package com.scompt.screenshotdemo.dagger;

import com.scompt.screenshotdemo.MainActivity;
import com.scompt.screenshotdemo.mock.MockGeolocationModule;
import com.scompt.screenshotdemo.mock.MockWeatherModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, MockWeatherModule.class, MockGeolocationModule.class })
public interface ScreenshotDemoComponent {
    void inject(MainActivity target);
}
