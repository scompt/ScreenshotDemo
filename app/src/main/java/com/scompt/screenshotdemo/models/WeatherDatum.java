package com.scompt.screenshotdemo.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;

import org.threeten.bp.Instant;

@AutoValue
public abstract class WeatherDatum {
    public abstract String summary();

    public abstract WeatherIcon icon();

    public abstract Instant time();

    public static JsonAdapter.Factory typeAdapterFactory() {
        return new AutoValue_WeatherDatum.AutoValue_WeatherDatumJsonAdapterFactory();
    }
}
