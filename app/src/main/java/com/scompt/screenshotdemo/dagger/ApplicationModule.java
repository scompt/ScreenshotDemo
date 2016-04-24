package com.scompt.screenshotdemo.dagger;

import com.scompt.screenshotdemo.ScreenshotDemoApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
@Singleton
public class ApplicationModule {
    private final ScreenshotDemoApplication application;

    public ApplicationModule(ScreenshotDemoApplication application) {
        this.application = application;
    }

    @Provides
    public OkHttpClient provideHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(logging).build();
    }
}
