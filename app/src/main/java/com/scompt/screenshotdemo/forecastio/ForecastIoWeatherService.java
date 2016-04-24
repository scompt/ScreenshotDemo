package com.scompt.screenshotdemo.forecastio;

import com.scompt.screenshotdemo.models.InstantAdapter;
import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;
import com.scompt.screenshotdemo.models.WeatherData;
import com.scompt.screenshotdemo.models.WeatherDatum;
import com.scompt.screenshotdemo.WeatherService;
import com.scompt.screenshotdemo.models.WeatherIconAdapter;
import com.squareup.moshi.Moshi;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Single;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ForecastIoWeatherService implements WeatherService {
    private final ForecastIoService forecastIoService;

    @Inject
    public ForecastIoWeatherService(OkHttpClient client, @Named("IpApiKey") String apiKey) {
        final Moshi moshi = new Moshi.Builder()
                .add(ForecastIoResponse.typeAdapterFactory())
                .add(WeatherDatum.typeAdapterFactory())
                .add(WeatherData.typeAdapterFactory())
                .add(new WeatherIconAdapter())
                .add(new InstantAdapter())
                .build();

        String baseUrl = String.format("https://api.forecast.io/forecast/%s/", apiKey);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
        forecastIoService = retrofit.create(ForecastIoService.class);
    }

    @Override
    public Single<LocationWeather> weatherForLocation(final Location location) {
        double lat = (double) location.latitude() / 1E6;
        double lon = (double) location.longitude() / 1E6;
        return forecastIoService.weatherForLocation(lat, lon).map(
                new Func1<ForecastIoResponse, LocationWeather>() {
                    @Override
                    public LocationWeather call(ForecastIoResponse forecastIoResponse) {
                        return LocationWeather.create(location,
                                                      forecastIoResponse.daily().data());
                    }
                });
    }

    private interface ForecastIoService {
        @GET("{lat},{lon}")
        Single<ForecastIoResponse> weatherForLocation(@Path("lat") double lat, @Path("lon") double lon);
    }
}
