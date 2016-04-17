package com.scompt.screenshotdemo.ipapi;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;

@AutoValue
abstract class IpApiLocation {
    public abstract String city();

    public abstract String country();

    public abstract double lat();

    public abstract double lon();

    public static JsonAdapter.Factory typeAdapterFactory() {
        return new AutoValue_IpApiLocation.AutoValue_IpApiLocationJsonAdapterFactory();
    }
}
