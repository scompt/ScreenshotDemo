package com.scompt.screenshotdemo;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;

import com.scompt.screenshotdemo.forecastio.ForecastIoResponse;
import com.scompt.screenshotdemo.models.InstantAdapter;
import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;
import com.scompt.screenshotdemo.models.WeatherData;
import com.scompt.screenshotdemo.models.WeatherDatum;
import com.scompt.screenshotdemo.models.WeatherIconAdapter;
import com.scompt.screenshotdemo.test.MockComponent;
import com.scompt.screenshotdemo.test.MockDemoApplication;
import com.squareup.moshi.Moshi;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.util.Locale;

import javax.inject.Inject;

import okio.Okio;
import rx.Observable;
import rx.Single;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doReturn;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestMainActivity {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Inject
    GeolocationService geolocationService;

    @Inject
    WeatherService weatherService;

    @Test
    public void testProgressIsShownWhileLoading() throws Exception {
        doReturn(Observable.<Location>never().toSingle())
                .when(geolocationService)
                .geolocate();
        mActivityRule.launchActivity(null);
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
    }

    @Test
    public void testAnErrorOccurred() throws Exception {
        doReturn(Single.error(new RuntimeException("message")))
                .when(geolocationService)
                .geolocate();
        mActivityRule.launchActivity(null);
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(allOf(isDisplayed(), withText(R.string.weather_in_unknown))));

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                     withText(endsWith(": message"))))
                .check(matches(isDisplayed()));
        Screengrab.screenshot("error");
    }

    @Test
    public void testCanRetryAfterError() throws Exception {
        doReturn(Single.error(new RuntimeException("message")))
                .doReturn(Observable.<Location>never().toSingle())
                .when(geolocationService)
                .geolocate();
        mActivityRule.launchActivity(null);

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(isDisplayed()));
        Screengrab.screenshot("error");

        onView(withId(android.support.design.R.id.snackbar_action)).perform(click());

        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.header)).check(matches(not(isDisplayed())));
        Screengrab.screenshot("progress");
    }

    @Test
    public void testCanSwipeAwayError() throws Exception {
        doReturn(Single.error(new RuntimeException("message")))
                .when(geolocationService)
                .geolocate();
        mActivityRule.launchActivity(null);

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(isDisplayed()));
        Screengrab.screenshot("error");

        onView(withId(android.support.design.R.id.snackbar_text)).perform(swipeRight());

        // TODO: Need to assert that SnackBar isn't shown anymore
        // onView(withId(android.support.design.R.id.snackbar_text)).check(matches(not(isDisplayed())));

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(isDisplayed()));
        Screengrab.screenshot("post_swipe");
    }


    @Test
    public void testLocationButNoWeather() throws Exception {
        Location location = Location.create("New York", 407127, -740059);
        doReturn(Single.just(location))
                .when(geolocationService)
                .geolocate();
        doReturn(Single.error(new RuntimeException("message")))
                .doReturn(Observable.<LocationWeather>never().toSingle())
                .when(weatherService)
                .weatherForLocation(location);
        MainActivity activity = mActivityRule.launchActivity(null);
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(allOf(isDisplayed(),
                                                        withText(activity.getString(R.string.weather_in, "New York")))));
        Screengrab.screenshot("location");
        // TODO: test that weather isn't shown
    }

    @Test
    public void testLocationAndWeather() throws Exception {
        Location location = Location.create("New York", 407127, -740059);
        doReturn(Single.just(location))
                .when(geolocationService)
                .geolocate();
        doReturn(Single.just(getLocationWeather(location)))
                .when(weatherService)
                .weatherForLocation(location);
        MainActivity activity = mActivityRule.launchActivity(null);
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(allOf(isDisplayed(),
                                                        withText(activity.getString(R.string.weather_in, "New York")))));
        onView(allOf(withId(R.id.summary),
                     isDescendantOfA(firstChildOf(withId(R.id.view_pager)))))
                .check(matches(withText(not(isEmptyString()))));
        Screengrab.screenshot("location");
    }

    @Test
    public void testCanRefreshAfterSuccess() throws Exception {
        Location location = Location.create("New York", 407127, -740059);
        doReturn(Single.just(location))
                .doReturn(Observable.<Location>never().toSingle())
                .when(geolocationService).geolocate();
        doReturn(Single.error(new RuntimeException("message")))
                .doReturn(Observable.<LocationWeather>never().toSingle())
                .when(weatherService).weatherForLocation(location);
        mActivityRule.launchActivity(null);

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.action_refresh)).perform(click());
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanRefreshAfterError() throws Exception {
        doReturn(Single.error(new RuntimeException("message")))
                .doReturn(Observable.<Location>never().toSingle())
                .when(geolocationService)
                .geolocate();
        mActivityRule.launchActivity(null);

        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(isDisplayed()));

        onView(withId(R.id.action_refresh)).perform(click());

        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.header)).check(matches(not(isDisplayed())));

        onView(withId(R.id.action_refresh)).check(matches(not(isEnabled())));
    }

    @Test
    public void testCantRefreshWhileLoading() throws Exception {
        doReturn(Observable.<Location>never().toSingle())
                .when(geolocationService)
                .geolocate();
        mActivityRule.launchActivity(null);

        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.header)).check(matches(not(isDisplayed())));
        onView(withId(R.id.action_refresh)).check(matches(not(isEnabled())));
    }

    @Before
    public void injectTest() {
        MockDemoApplication mockDemoApplication = (MockDemoApplication) InstrumentationRegistry
                .getTargetContext().getApplicationContext();
        MockComponent component = (MockComponent) mockDemoApplication.component();
        component.inject(this);
    }

    private static Matcher<View> firstChildOf(final Matcher<View> parentMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with first child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {

                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }
                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(0).equals(view);

            }
        };
    }

    private LocationWeather getLocationWeather(Location location) throws Exception {
        final Moshi moshi = new Moshi.Builder()
                .add(ForecastIoResponse.typeAdapterFactory())
                .add(WeatherDatum.typeAdapterFactory())
                .add(WeatherData.typeAdapterFactory())
                .add(new WeatherIconAdapter())
                .add(new InstantAdapter())
                .build();

        Locale locale = Locale.getDefault();
        String filename = String.format("forecast-%s_%s.json", locale.getLanguage(), locale.getCountry());
        InputStream inputStream = InstrumentationRegistry.getContext().getAssets()
                                                         .open(filename);
        ForecastIoResponse response = moshi.adapter(ForecastIoResponse.class).fromJson(
                Okio.buffer(Okio.source(inputStream)));

        assertThat(response).isNotNull();
        return LocationWeather.create(location, response.daily().data());
    }
}
