package com.scompt.screenshotdemo.models;

import android.support.annotation.DrawableRes;

import com.scompt.screenshotdemo.R;

public enum WeatherIcon {
    CLEAR_DAY("clear-day", R.drawable.clear_day),
    CLEAR_NIGHT("clear-night", R.drawable.moon),
    RAIN("rain", R.drawable.cloud_rain),
    SNOW("snow", R.drawable.cloud_snow),
    SLEET("sleet", R.drawable.cloud_sleet),
    WIND("wind", R.drawable.cloud_wind),
    FOG("fog", R.drawable.cloud_fog),
    CLOUDY("cloudy", R.drawable.cloud),
    PARTLY_CLOUDY_DAY("partly-cloudy-day", R.drawable.cloud_sun),
    PARTLY_CLOUDY_NIGHT("partly-cloudy-night", R.drawable.cloud_moon),
    UNKNOWN("unknown", R.drawable.unknown);

    public final String name;
    public final int iconResId;

    WeatherIcon(String name, @DrawableRes int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }
}
