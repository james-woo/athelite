package com.athelite;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import com.athelite.Activity.ViewWorkout;
import com.athelite.Adapter.WorkoutPlanAdapter;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Model.WorkoutPlan;
import com.athelite.Util.JsonSerializer;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ViewWorkoutUnitTest {

    private ViewWorkout _activity;
    private Date _date;

    @Before
    public void view_workout_setup()  {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        _date = c.getTime();

        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("New Workout")
                .workoutPlanId(1L)
                .exercise(
                        new Exercise.Builder("New Exercise")
                                .exerciseSet(new ExerciseSet(1L, 1, 100.00, "lb", 5))
                                .exerciseId(1L)
                                .oneRepMax(100.00)
                                .exerciseDate(_date)
                                .build()
                )
                .build();

        Intent intent = new Intent();
        intent.putExtra("VIEW_WORKOUT_PARENT", "Workout");
        intent.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(workoutPlan));

        _activity = Robolectric.buildActivity(ViewWorkout.class)
                .withIntent(intent)
                .create()
                .get();
    }

    @Test
    public void view_workout_activity_starts_test() throws Exception {
        assertTrue(_activity != null);
    }

    @Test
    public void view_workout_toolbar_not_null_test() throws Exception {
        Toolbar toolbar = (Toolbar) _activity.findViewById(R.id.view_workout_toolbar);
        assertNotNull(toolbar);
        assertNotNull(_activity.getSupportActionBar());
    }

    @Test
    public void view_workout_toolbar_is_null_test() throws Exception {
        _activity.setSupportActionBar(null);
        assertNull(_activity.getSupportActionBar());

    }
}
