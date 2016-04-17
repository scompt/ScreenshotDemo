package com.scompt.screenshotdemo.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class LocationWeather {
    public abstract Location location();

    public abstract List<WeatherDatum> dailyWeather();

    public static LocationWeather create(Location location,
                                         List<WeatherDatum> dailyWeather) {

        return new AutoValue_LocationWeather(location,
                                             Collections.unmodifiableList(new ArrayList<>(
                                                     dailyWeather)));
    }
}
