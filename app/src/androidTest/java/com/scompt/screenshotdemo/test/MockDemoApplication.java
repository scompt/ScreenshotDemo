package com.scompt.screenshotdemo.test;

import com.scompt.screenshotdemo.ScreenshotDemoApplication;
import com.scompt.screenshotdemo.dagger.ScreenshotDemoComponent;

public class MockDemoApplication extends ScreenshotDemoApplication {

    @Override
    protected ScreenshotDemoComponent buildComponent() {
        return DaggerMockComponent.builder().build();
    }
}
