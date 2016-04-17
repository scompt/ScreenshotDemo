package com.scompt.screenshotdemo.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Location {
    public abstract String description();

    public abstract int latitude();

    public abstract int longitude();

    public static Location create(String description, int e6Latitude, int e6Longitude) {
        return new AutoValue_Location(description, e6Latitude, e6Longitude);
    }
}
