package com.scompt.screenshotdemo.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;

import java.util.List;

@AutoValue
public abstract class WeatherData {
    public abstract String summary();

    public abstract String icon();

    public abstract List<WeatherDatum> data();

    public static JsonAdapter.Factory typeAdapterFactory() {
        return new AutoValue_WeatherData.AutoValue_WeatherDataJsonAdapterFactory();
    }
}
