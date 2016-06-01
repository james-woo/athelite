package com.athelite;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.athelite.Activity.MainActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void afterActivityLaunched() {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivityRule.getActivity());
                    if(!sp.getBoolean("setupSeen", false)) {
                        // Enter setup
                        onView(withId(R.id.setup_edit_height))
                                .perform(typeText("170"), closeSoftKeyboard());
                        onView(withId(R.id.setup_edit_weight))
                                .perform(typeText("165"), closeSoftKeyboard());
                        onView(withId(R.id.setup_edit_age))
                                .perform(typeText("24"), closeSoftKeyboard());
                        onView(withId(R.id.setup_fab)).perform(click());
                    }
                }
            };

    @Test
    public void tabs_exist_with_fresh_install() {
        // Check that the cards exist.
        onView(withText("HOME"))
                .perform(click());
        onView(withId(R.id.home_tab_next_workout_card_title))
                .check(matches(withText("Next Workout")));
        onView(withId(R.id.home_tab_today_workout_card_title))
                .check(matches(withText("Today's Workout")));
        onView(withId(R.id.home_tab_previous_workout_card_title))
                .check(matches(withText("Last Workout")));

        // Templates tab
        onView(withText("TEMPLATES"))
                .perform(click());
        onView(withId(R.id.workout_tab_layout))
                .check(matches(withId(R.id.workout_tab_layout)));
        onView(withId(R.id.workout_tab_empty_list))
                .check(matches(withId(R.id.workout_tab_empty_list)));

        // Calendar tab
        onView(withText("CALENDAR"))
                .perform(click());
        Calendar c = Calendar.getInstance();
        c.setTime(CalendarDay.today().getDate());
        DateFormat df = DateFormat.getDateInstance();
        onView(withId(R.id.calendar_tab_currently_selected_date))
                .check(matches(withText(String.format("Selected %s", df.format(c.getTimeInMillis())))));

        // Graph tab
        onView(withText("GRAPH"))
                .perform(click());
        onView(withId(R.id.graph_exercise_card_view))
                .check(doesNotExist());
    }
}
