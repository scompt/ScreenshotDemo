package com.scompt.screenshotdemo.dagger;

import com.scompt.screenshotdemo.GeolocationService;
import com.scompt.screenshotdemo.ipapi.IpApiGeolocationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class IpApiModule {
    @Provides
    GeolocationService provideGeolocationService(IpApiGeolocationService impl) {
        return impl;
    }
}
