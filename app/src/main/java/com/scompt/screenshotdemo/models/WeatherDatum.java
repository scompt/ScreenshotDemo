package com.scompt.screenshotdemo.models;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;

import org.threeten.bp.Instant;

@AutoValue
public abstract class WeatherDatum {
    public abstract String summary();

    public abstract WeatherIcon icon();

    @Nullable
    public abstract Float temperatureMin();

    @Nullable
    public abstract Float temperatureMax();

    public abstract Instant time();

    public static WeatherDatum create(String summary, WeatherIcon icon, float temperatureMin,
                                      float temperatureMax, Instant time) {
        return new AutoValue_WeatherDatum(summary, icon, temperatureMin, temperatureMax, time);
    }

    public static JsonAdapter.Factory typeAdapterFactory() {
        return new AutoValue_WeatherDatum.AutoValue_WeatherDatumJsonAdapterFactory();
    }
}
