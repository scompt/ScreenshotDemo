package com.scompt.screenshotdemo.dagger;

import com.scompt.screenshotdemo.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, ForecastIoModule.class, IpApiModule.class })
public interface ScreenshotDemoComponent {
    void inject(MainActivity target);
}
