package com.scompt.screenshotdemo;

import android.graphics.RectF;

import com.robinhood.spark.SparkAdapter;
import com.scompt.screenshotdemo.models.WeatherDatum;

import java.util.List;

public class WeatherSparkAdapter extends SparkAdapter {
    public enum Mode {
        MAX {
            @Override
            public float getValue(WeatherDatum day) {
                Float temp = day.temperatureMax();
                if (temp == null) {
                    return 0;
                }
                return temp;
            }
        }, MIN {
            @Override
            public float getValue(WeatherDatum day) {
                Float temp = day.temperatureMin();
                if (temp == null) {
                    return 0;
                }
                return temp;
            }
        };

        public abstract float getValue(WeatherDatum day);
    }

    private final List<WeatherDatum> weatherDays;
    private final Mode mode;
    private final RectF dataBounds;

    public WeatherSparkAdapter(List<WeatherDatum> weatherDays, Mode mode) {
        this.weatherDays = weatherDays;
        this.mode = mode;
        this.dataBounds = calculateDataBounds(this.weatherDays);
    }

    @Override
    public int getCount() {
        return weatherDays.size();
    }

    @Override
    public Object getItem(int index) {
        return weatherDays.get(index);
    }

    @Override
    public float getY(int index) {
        return mode.getValue(weatherDays.get(index));
    }

    @Override
    public RectF getDataBounds() {
        return dataBounds;
    }

    private static RectF calculateDataBounds(List<WeatherDatum> weatherDays) {
        if (weatherDays.isEmpty()) {
            return new RectF();
        }

        float minX = 0;
        float maxX = weatherDays.size() - 1;

        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (WeatherDatum day : weatherDays) {
            Float dayMin = day.temperatureMin();
            if (dayMin != null) {
                if (dayMin < minY) {
                    minY = dayMin;
                }
            }

            Float dayMax = day.temperatureMax();
            if (dayMax != null) {
                if (dayMax > maxY) {
                    maxY = dayMax;
                }
            }
        }

        return new RectF(minX, minY, maxX, maxY);
    }
}
