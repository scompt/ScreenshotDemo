package com.scompt.screenshotdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;
import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;
import com.scompt.screenshotdemo.models.WeatherDatum;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class MainActivity extends RxAppCompatActivity {

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

    @BindView(R.id.highlight)
    View sparkHighlight;

    @Inject
    WeatherService weatherService;

    @Inject
    GeolocationService geolocationService;

    // Needed because the refresh menu item isn't created until some later time. This lets it
    // pick up the current enabled status and track it.
    private final BehaviorSubject<Boolean> refreshMenuItemEnabledSubject = BehaviorSubject.create(false);

    private int highlightWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenshotDemoApplication.get(this).component().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float positionMultiplier = position + positionOffset;
                sparkHighlight.setTranslationX(positionMultiplier * highlightWidth);
            }
        });
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int dayCount = sparkView1.getAdapter().getCount();
                        if (dayCount == 0) {
                            highlightWidth = 0;
                        } else {
                            highlightWidth = sparkView1.getWidth() / dayCount;
                        }

                        int oldWidth = sparkHighlight.getLayoutParams().width;
                        if (oldWidth != highlightWidth) {
                            sparkHighlight.getLayoutParams().width = highlightWidth;
                            sparkHighlight.requestLayout();
                        }
                    }
                });
        makeCall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem refreshMenuItem = menu.findItem(R.id.action_refresh);
        refreshMenuItemEnabledSubject.compose(this.<Boolean>bindToLifecycle())
                                     .subscribe(new Action1<Boolean>() {
                                         @Override
                                         public void call(Boolean enabled) {
                                             refreshMenuItem.setEnabled(enabled);
                                         }
                                     });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            makeCall();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void makeCall() {
        refreshMenuItemEnabledSubject.onNext(false);

        viewPager.setAdapter(EMPTY_PAGER_ADAPTER);
        sparkView1.setAdapter(EMPTY_SPARK_ADAPTER);
        sparkView2.setAdapter(EMPTY_SPARK_ADAPTER);
        sparkHighlight.setVisibility(View.GONE);

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
                sparkHighlight.setVisibility(View.VISIBLE);
                refreshMenuItemEnabledSubject.onNext(true);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                refreshMenuItemEnabledSubject.onNext(true);
                headerTextView.setText(R.string.weather_in_unknown);
                viewPager.setAdapter(EMPTY_PAGER_ADAPTER);
                sparkView1.setAdapter(EMPTY_SPARK_ADAPTER);
                sparkView2.setAdapter(EMPTY_SPARK_ADAPTER);
                sparkHighlight.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                headerTextView.setVisibility(View.VISIBLE);

                Timber.e(t, "geolocating");
                String errorMessage = getString(R.string.error, t.getLocalizedMessage());
                Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(Color.RED)
                        .setAction(R.string.retry, new View.OnClickListener() {
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
