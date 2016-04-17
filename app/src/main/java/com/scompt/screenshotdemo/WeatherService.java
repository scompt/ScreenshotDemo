package com.scompt.screenshotdemo;

import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;

import rx.Single;

public interface WeatherService {
    Single<LocationWeather> weatherForLocation(Location location);
}
