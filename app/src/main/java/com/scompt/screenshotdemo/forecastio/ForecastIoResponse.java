package com.scompt.screenshotdemo.forecastio;

import com.google.auto.value.AutoValue;
import com.scompt.screenshotdemo.models.WeatherData;
import com.squareup.moshi.JsonAdapter;

@AutoValue
abstract class ForecastIoResponse {
//    public abstract WeatherDatum currently();

    public abstract WeatherData daily();

    public static JsonAdapter.Factory typeAdapterFactory() {
        return new AutoValue_ForecastIoResponse.AutoValue_ForecastIoResponseJsonAdapterFactory();
    }
}
