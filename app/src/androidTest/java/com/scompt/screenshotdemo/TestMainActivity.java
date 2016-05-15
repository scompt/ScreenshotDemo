package com.scompt.screenshotdemo;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.scompt.screenshotdemo.models.Location;
import com.scompt.screenshotdemo.models.LocationWeather;
import com.scompt.screenshotdemo.test.MockComponent;
import com.scompt.screenshotdemo.test.MockDemoApplication;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
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
        doReturn(Observable.<Location>never().toSingle()).when(geolocationService).geolocate();
        mActivityRule.launchActivity(null);
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
    }

    @Test
    public void testAnErrorOccurred() throws Exception {
        doReturn(Single.error(new RuntimeException("message"))).when(geolocationService).geolocate();
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
                .doReturn(Observable.<Location>never()
                        .toSingle()).when(geolocationService).geolocate();
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
    public void testLocationButNoWeather() throws Exception {
        Location location = Location.create("New York", 407127, -740059);
        doReturn(Single.just(location)).when(geolocationService).geolocate();
        doReturn(Single.error(new RuntimeException("message")))
                .doReturn(Observable.<LocationWeather>never()
                        .toSingle()).when(weatherService).weatherForLocation(location);
        MainActivity activity = mActivityRule.launchActivity(null);
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.header)).check(matches(allOf(isDisplayed(),
                                                        withText(activity.getString(R.string.weather_in, "New York")))));
        Screengrab.screenshot("location");
        // TODO: test that weather isn't shown
    }

    @Before
    public void injectTest() {
        MockDemoApplication mockDemoApplication = (MockDemoApplication) InstrumentationRegistry
                .getTargetContext().getApplicationContext();
        MockComponent component = (MockComponent) mockDemoApplication.component();
        component.inject(this);
    }
}
