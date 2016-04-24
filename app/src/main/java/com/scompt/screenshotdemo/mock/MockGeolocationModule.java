package com.scompt.screenshotdemo.mock;

import com.scompt.screenshotdemo.GeolocationService;
import com.scompt.screenshotdemo.models.Location;

import dagger.Module;
import dagger.Provides;
import rx.Single;

@Module
public class MockGeolocationModule {
    private final Location mockLocation;

    public MockGeolocationModule(Location mockLocation) {
        this.mockLocation = mockLocation;
    }

    @Provides
    GeolocationService provideMockGeolocationService() {
        return new GeolocationService() {
            @Override
            public Single<Location> geolocate() {
                return Single.just(mockLocation);
            }
        };
    }
}
