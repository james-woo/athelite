package com.athelite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowTextView;
import org.robolectric.shadows.ShadowToast;

import com.athelite.Activity.ViewExercise;
import com.athelite.Adapter.ExerciseListAdapter;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Util.JsonSerializer;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ViewExerciseUnitTest {

    private ViewExercise _activity;
    private Date _date;

    @Before
    public void view_exercise_setup()  {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        _date = c.getTime();

        Exercise exercise = new Exercise.Builder("Exercise")
                .exerciseSet(new ExerciseSet(1L, 1, 100.00, "lb", 5))
                .exerciseId(1L)
                .oneRepMax(100.00)
                .exerciseDate(_date)
                .build();

        Intent intent = new Intent();
        intent.putExtra(ExerciseListAdapter.WORKOUT_EXERCISE, JsonSerializer.workoutPlanExerciseToJson(exercise));

        _activity = Robolectric.buildActivity(ViewExercise.class)
                .withIntent(intent)
                .create()
                .get();
    }

    @Test
    public void view_exercise_activity_starts_test() throws Exception {
        assertTrue(_activity != null);
    }

    @Test
    public void view_exercise_toolbar_not_null_test() throws Exception {
        Toolbar toolbar = (Toolbar) _activity.findViewById(R.id.view_exercise_toolbar);
        assertNotNull(toolbar);
        assertNotNull(_activity.getSupportActionBar());
    }

    @Test
    public void view_exercise_toolbar_is_null_test() throws Exception {
        _activity.setSupportActionBar(null);
        assertNull(_activity.getSupportActionBar());

    }
}
