package com.athelite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Model.WorkoutPlan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutTest {
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
    public void workout_plan_test() {
        assertThat(_workoutPlan.getId(), is(1L));
        assertThat(_workoutPlan.getWorkoutPlanName(), is("New Workout"));
    }

    @Test
    public void workout_plan_exercise_test() {
        for(Exercise e : _workoutPlan.getWorkoutPlanExercises()) {
            assertThat(e.getId(), is(1L));
            assertThat(e.getExerciseDate(), is(_date));
            assertThat(e.getExerciseName(), is("New Exercise"));
            assertThat(e.getOneRepMax(), is(100.00));
        }
    }

    @Test
    public void workout_plan_add_exercises_test() {
        Exercise exercise1 = new Exercise.Builder("Exercise1")
                .exerciseSet(new ExerciseSet(1L, 1, 100.00, "lb", 5))
                .exerciseId(1L)
                .build();
        Exercise exercise2 = new Exercise.Builder("Exercise2")
                .exerciseSet(new ExerciseSet(2L, 2, 100.00, "lb", 5))
                .exerciseId(2L)
                .build();
        Exercise exercise3 = new Exercise.Builder("Exercise3")
                .exerciseSet(new ExerciseSet(3L, 3, 100.00, "lb", 5))
                .exerciseId(3L)
                .build();

        ArrayList<Exercise> exercises = new ArrayList<>();
        exercises.add(exercise1);
        exercises.add(exercise2);

        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("Test_Workout")
                                                .exercises(exercises)
                                                .build();
        workoutPlan.addExercise(exercise3);
        for(Exercise e : workoutPlan.getWorkoutPlanExercises()) {
            assertThat(e, CoreMatchers.<Exercise>notNullValue());
        }
    }

    @Test
    public void workout_plan_set_exercises_test() {
        Exercise exercise1 = new Exercise.Builder("Exercise1")
                .exerciseSet(new ExerciseSet(1L, 1, 100.00, "lb", 5))
                .exerciseId(1L)
                .build();
        Exercise exercise2 = new Exercise.Builder("Exercise2")
                .exerciseSet(new ExerciseSet(2L, 2, 100.00, "lb", 5))
                .exerciseId(2L)
                .build();
        Exercise exercise3 = new Exercise.Builder("Exercise3")
                .exerciseSet(new ExerciseSet(3L, 3, 100.00, "lb", 5))
                .exerciseId(3L)
                .build();

        ArrayList<Exercise> exercises = new ArrayList<>();
        exercises.add(exercise1);
        exercises.add(exercise2);
        exercises.add(exercise3);

        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("Test_Workout")
                                                .build();
        workoutPlan.setExercises(exercises);
        for(Exercise e : workoutPlan.getWorkoutPlanExercises()) {
            assertThat(e, CoreMatchers.<Exercise>notNullValue());
        }
    }

    @Test
    public void workout_plan_set_date_test() {
        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("Test_Workout")
                                                .build();
        workoutPlan.setDate(_date);
        assertThat(workoutPlan.getDate(), is(_date));
    }

    @Test
    public void workout_plan_set_name_test() {
        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("Test_Workout")
                .build();
        assertThat(workoutPlan.getWorkoutPlanName(), is("Test_Workout"));
        workoutPlan.setWorkoutPlanName("Test");
        assertThat(workoutPlan.getWorkoutPlanName(), is("Test"));
    }

    @Test
    public void workout_plan_set_id_test() {
        WorkoutPlan workoutPlan = new WorkoutPlan.Builder("Test_Workout")
                .build();
        workoutPlan.setId(1L);
        assertThat(workoutPlan.getId(), is(1L));
    }

    @Test
    public void workout_plan_exercise_sets_test() {
        for(ExerciseSet es : _workoutPlan.getWorkoutPlanExercises().get(0).getExerciseSets()) {
            assertThat(es.getId(), is(1L));
            assertThat(es.getSetNumber(), is(1));
            assertThat(es.getSetReps(), is(5));
            assertThat(es.getSetWeight(), is(100.00));
            assertThat(es.getWeightType(), is("lb"));
        }
    }

    @Test
    public void exercise_set_exercise_sets_test() {
        ExerciseSet exerciseSet1 = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        ExerciseSet exerciseSet2 = new ExerciseSet(1L, 1, 285.00, "lb", 6);
        ExerciseSet exerciseSet3 = new ExerciseSet(1L, 1, 255.00, "lb", 7);

        ArrayList<ExerciseSet> exerciseSets = new ArrayList<>();
        exerciseSets.add(exerciseSet1);
        exerciseSets.add(exerciseSet2);
        exerciseSets.add(exerciseSet3);

        Exercise exercise = new Exercise.Builder("Exercise_Test")
                .build();
        exercise.setExerciseSets(exerciseSets);
        for(ExerciseSet e : exercise.getExerciseSets()) {
            assertThat(e, CoreMatchers.<ExerciseSet>notNullValue());
        }
    }

    @Test
    public void exercise_set_id_test() {
        Exercise exercise = new Exercise.Builder("Exercise_Test")
                .build();
        exercise.setId(1L);
        assertThat(exercise.getId(), is(1L));
    }

    @Test
    public void exercise_set_name_test() {
        Exercise exercise = new Exercise.Builder("Exercise_Test")
                .build();
        assertThat(exercise.getExerciseName(), is("Exercise_Test"));
        exercise.setExerciseName("Test");
        assertThat(exercise.getExerciseName(), is("Test"));
    }

    @Test
    public void exercise_set_one_rep_max_test() {
        Exercise exercise = new Exercise.Builder("Exercise_Test")
                .build();
        exercise.setOneRepMax(1.0);
        assertEquals(exercise.getOneRepMax(), 1.0, 0.1);
    }

    @Test
    public void exercise_set_exercise_date_test() {
        Exercise exercise = new Exercise.Builder("Exercise_Test")
                .build();
        exercise.setExerciseDate(_date);
        assertThat(exercise.getExerciseDate(), is(_date));
    }

    @Test
    public void calculate_one_rep_max_test() {
        ExerciseSet exerciseSet1 = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        ExerciseSet exerciseSet2 = new ExerciseSet(1L, 1, 285.00, "lb", 6);
        ExerciseSet exerciseSet3 = new ExerciseSet(1L, 1, 255.00, "lb", 7);
        Exercise exercise = new Exercise.Builder("One_Rep_Max_Test")
                                        .build();
        exercise.addExerciseSet(exerciseSet1);
        exercise.addExerciseSet(exerciseSet2);
        exercise.addExerciseSet(exerciseSet3);

        exercise.calculateOneRepMax();

        assertEquals(exercise.getOneRepMax(), 367.5, 0.1);
    }

    @Test
    public void exercise_builder_test() {
        ExerciseSet exerciseSet1 = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        ExerciseSet exerciseSet2 = new ExerciseSet(1L, 1, 285.00, "lb", 6);
        ExerciseSet exerciseSet3 = new ExerciseSet(1L, 1, 255.00, "lb", 7);

        ArrayList<ExerciseSet> exerciseSets = new ArrayList<>();
        exerciseSets.add(exerciseSet1);
        exerciseSets.add(exerciseSet2);
        exerciseSets.add(exerciseSet3);

        Exercise exercise = new Exercise.Builder("Exercise_Test")
                .exerciseSets(exerciseSets)
                .build();

        for(ExerciseSet e : exercise.getExerciseSets()) {
            assertThat(e, CoreMatchers.<ExerciseSet>notNullValue());
        }
    }

    @Test
    public void exercise_set_set_id_test() {
        ExerciseSet exerciseSet = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        assertThat(exerciseSet.getId(), is(1L));
        exerciseSet.setId(2L);
        assertThat(exerciseSet.getId(), is(2L));
    }

    @Test
    public void exercise_set_set_number_test() {
        ExerciseSet exerciseSet = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        assertThat(exerciseSet.getSetNumber(), is(1));
        exerciseSet.setSetNumber(2);
        assertThat(exerciseSet.getSetNumber(), is(2));
    }

    @Test
    public void exercise_set_set_weight_test() {
        ExerciseSet exerciseSet = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        assertEquals(exerciseSet.getSetWeight(), 315.00, 0.1);
        exerciseSet.setSetWeight(316.00);
        assertEquals(exerciseSet.getSetWeight(), 316.00, 0.1);
    }

    @Test
    public void exercise_set_set_type_test() {
        ExerciseSet exerciseSet = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        assertThat(exerciseSet.getWeightType(), is("lb"));
        exerciseSet.setWeightType("kg");
        assertThat(exerciseSet.getWeightType(), is("kg"));
    }

    @Test
    public void exercise_set_set_reps_test() {
        ExerciseSet exerciseSet = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        assertThat(exerciseSet.getSetReps(), is(5));
        exerciseSet.setSetReps(6);
        assertThat(exerciseSet.getSetReps(), is(6));
    }

    @Test
    public void exercise_set_to_string_test() {
        ExerciseSet exerciseSet = new ExerciseSet(1L, 1, 315.00, "lb", 5);
        assertThat(exerciseSet.toString(), is("1 315.0 5"));
    }
}
