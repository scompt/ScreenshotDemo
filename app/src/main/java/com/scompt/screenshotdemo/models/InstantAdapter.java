package com.scompt.screenshotdemo.models;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import org.threeten.bp.Instant;

public class InstantAdapter {
    @ToJson
    public long toJson(Instant instant) {
        return instant.toEpochMilli() / 1000;
    }

    @FromJson
    public Instant fromEpochMilli(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli * 1000);
    }
}
