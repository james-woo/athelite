package com.athelite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import android.content.SharedPreferences;

import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Model.WorkoutPlan;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RunWith(MockitoJUnitRunner.class)

public class WorkoutTest {

    @Test
    public void createWorkoutPlan() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();

        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("New Workout")
                                                 .workoutPlanId(1L)
                                                 .exercise(
                                                        new Exercise.Builder("New Exercise")
                                                        .exerciseSet(new ExerciseSet(1L, 1, 100.00, "lb", 5))
                                                        .exerciseId(1L)
                                                        .oneRepMax(100.00)
                                                        .exerciseDate(today)
                                                        .workoutId(1L)
                                                        .build()
                                                 )
                                                 .build();

        assertThat(workoutPlan.getId(), is(1L));
        assertThat(workoutPlan.getWorkoutPlanName(), is("New Workout"));
        for(Exercise e : workoutPlan.getWorkoutPlanExercises()) {
            assertThat(e.getId(), is(1L));
            assertThat(e.getExerciseDate(), is(today));
            assertThat(e.getExerciseName(), is("New Exercise"));
            assertThat(e.getOneRepMax(), is(100.00));
            for(ExerciseSet es : e.getExerciseSets()) {
                assertThat(es.getId(), is(1L));
                assertThat(es.getSetNumber(), is(1));
                assertThat(es.getSetReps(), is(5));
                assertThat(es.getSetWeight(), is(100.00));
                assertThat(es.getWeightType(), is("lb"));
            }
        }
    }
}
