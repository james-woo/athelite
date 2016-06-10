package com.athelite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.athelite.Database.DBHandler;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Model.WorkoutPlan;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseUnitTest {
    private WorkoutPlan _workoutPlan;
    private Date _date;

    @Before
    public void createWorkout() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        _date = c.getTime();

        _workoutPlan = new WorkoutPlan.Builder("New Workout")
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
    }

    @Test
    public void read_workout_id_test() {
        DBHandler db = Mockito.mock(DBHandler.class);
        Mockito.when(db.readWorkout(_workoutPlan.getId())).thenReturn(_workoutPlan);

        assertThat(db.readWorkout(_workoutPlan.getId()), is(_workoutPlan));
    }

    @Test
    public void copy_workout_id_test() {
        DBHandler db = Mockito.mock(DBHandler.class);
        Mockito.when(db.copyWorkoutPlan(db.getWritableDatabase(), _workoutPlan)).thenReturn(_workoutPlan);

        assertThat(db.copyWorkoutPlan(db.getWritableDatabase(), _workoutPlan), is(_workoutPlan));
    }

}
