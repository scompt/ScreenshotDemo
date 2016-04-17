package com.scompt.screenshotdemo;

import com.scompt.screenshotdemo.models.Location;

import rx.Single;

public interface GeolocationService {

    Single<Location> geolocate();
}
