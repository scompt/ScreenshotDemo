package com.scompt.screenshotdemo.test;

import com.scompt.screenshotdemo.TestMainActivity;
import com.scompt.screenshotdemo.dagger.ScreenshotDemoComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { MockModule.class })
public interface MockComponent extends ScreenshotDemoComponent {

    void inject(TestMainActivity target);
}
