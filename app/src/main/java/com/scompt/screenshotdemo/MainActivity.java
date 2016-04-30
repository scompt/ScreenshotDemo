package com.scompt.screenshotdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;
import com.scompt.screenshotdemo.forecastio.ForecastIoWeatherService;
import com.scompt.screenshotdemo.ipapi.IpApiGeolocationService;
import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;
import com.scompt.screenshotdemo.models.WeatherDatum;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final PagerAdapter EMPTY_PAGER_ADAPTER = new PagerAdapter() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    };
    private GeolocationService geolocationService;

    private WeatherService weatherService;

    public static final SparkAdapter EMPTY_SPARK_ADAPTER = new SparkAdapter() {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int index) {
            return null;
        }

        @Override
        public float getY(int index) {
            return 0;
        }
    };

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.header)
    TextView headerTextView;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.spark1)
    SparkView sparkView1;

    @BindView(R.id.spark2)
    SparkView sparkView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager.setAdapter(EMPTY_PAGER_ADAPTER);
        sparkView1.setAdapter(EMPTY_SPARK_ADAPTER);
        sparkView2.setAdapter(EMPTY_SPARK_ADAPTER);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        geolocationService = new IpApiGeolocationService(client);
        weatherService = new ForecastIoWeatherService(client);

        makeCall();
    }

    private void makeCall() {
        progressBar.setVisibility(View.VISIBLE);
        headerTextView.setVisibility(View.INVISIBLE);

        geolocationService.geolocate()
                          .flatMap(new Func1<Location, Single<LocationWeather>>() {
            @Override
            public Single<LocationWeather> call(final Location location) {
                return weatherService.weatherForLocation(location)
                                     .onErrorReturn(
                        new Func1<Throwable, LocationWeather>() {
                            @Override
                            public LocationWeather call(Throwable throwable) {
                                Timber.e(throwable, "Fetching weather");
                                return LocationWeather.create(location,
                                                              Collections.<WeatherDatum>emptyList());
                            }
                        });
            }
        })
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new Action1<LocationWeather>() {
            @Override
            public void call(LocationWeather locationWeather) {
                Timber.d("blah: %s", locationWeather);
                headerTextView.setText(getString(R.string.weather_in, locationWeather.location().description()));
                List<WeatherDatum> weatherDays = locationWeather.dailyWeather();
                viewPager.setAdapter(new WeatherPagerAdapter(LayoutInflater.from(MainActivity.this),
                                                             weatherDays));
                sparkView1.setAdapter(new WeatherSparkAdapter(weatherDays, WeatherSparkAdapter.Mode.MIN));
                sparkView2.setAdapter(new WeatherSparkAdapter(weatherDays, WeatherSparkAdapter.Mode.MAX));
                progressBar.setVisibility(View.INVISIBLE);
                headerTextView.setVisibility(View.VISIBLE);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                headerTextView.setText(R.string.weather_in_unknown);
                viewPager.setAdapter(EMPTY_PAGER_ADAPTER);
                sparkView1.setAdapter(EMPTY_SPARK_ADAPTER);
                sparkView2.setAdapter(EMPTY_SPARK_ADAPTER);
                progressBar.setVisibility(View.INVISIBLE);
                headerTextView.setVisibility(View.VISIBLE);

                Timber.e(t, "geolocating");
                Snackbar.make(coordinatorLayout, "error: " + t.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(Color.RED)
                        .setAction("retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                makeCall();
                            }
                        })
                        .show();
            }
        });
    }
}
