package com.scompt.screenshotdemo.ipapi;

import com.scompt.screenshotdemo.GeolocationService;
import com.scompt.screenshotdemo.models.Location;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import rx.Single;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class IpApiGeolocationService implements GeolocationService {

    private final IpApiService ipApiService;

    public IpApiGeolocationService(OkHttpClient client) {
        final Moshi moshi = new Moshi.Builder()
                .add(IpApiLocation.typeAdapterFactory())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ip-api.com")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
        ipApiService = retrofit.create(IpApiService.class);
    }

    @Override
    public Single<Location> geolocate() {
        return ipApiService.geolocate().map(new Func1<IpApiLocation, Location>() {
            @Override
            public Location call(IpApiLocation ipApiLocation) {
                String description = ipApiLocation.city() + ", " + ipApiLocation.country();
                return Location.create(description,
                                       (int) (ipApiLocation.lat() * 1e6),
                                       (int) (ipApiLocation.lon() * 1e6));
            }
        });
    }

    private interface IpApiService {
        @GET("/json?fields=lat,lon,city,country")
        Single<IpApiLocation> geolocate();
    }
}
