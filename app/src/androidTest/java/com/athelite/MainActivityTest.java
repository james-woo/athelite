package com.athelite;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.athelite.Activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void hometab_workoutcards_exists() {
        // Check that the cards exist.
        onView(withId(R.id.home_tab_next_workout_card_title))
                .check(matches(withText("Next Workout")));
        onView(withId(R.id.home_tab_today_workout_card_title))
                .check(matches(withText("Today's Workout")));
        onView(withId(R.id.home_tab_previous_workout_card_title))
                .check(matches(withText("Last Workout")));
    }
}
