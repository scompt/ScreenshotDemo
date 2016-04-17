package com.scompt.screenshotdemo.models;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.HashMap;
import java.util.Map;

// Necessary to return WeatherIcon.UNKNOWN when an icon is not found.
public class WeatherIconAdapter {
    private final Map<String, WeatherIcon> reverseMap;

    public WeatherIconAdapter() {
        WeatherIcon[] icons = WeatherIcon.values();
        reverseMap = new HashMap<>(icons.length);
        for (WeatherIcon icon : icons) {
            reverseMap.put(icon.name, icon);
        }
    }

    @FromJson
    public WeatherIcon fromString(String string) {
        WeatherIcon weatherIcon = reverseMap.get(string);
        if (weatherIcon == null) {
            return WeatherIcon.UNKNOWN;
        }
        return weatherIcon;
    }

    @ToJson
    public String toString(WeatherIcon weatherIcon) {
        return weatherIcon.name;
    }
}
