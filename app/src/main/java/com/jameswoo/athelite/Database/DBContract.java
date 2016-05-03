package com.jameswoo.athelite.Database;

import android.provider.BaseColumns;

// This "contract" is the schema of the Database.
// This schema is used by the Table Handlers

public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBContract() {}

    public static abstract class WorkoutPlanTable implements BaseColumns {
        public static final String TABLE_NAME = "workoutPlans";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EXERCISES = "exercises";
    }

    public static abstract class ExerciseTable implements BaseColumns {
        public static final String TABLE_NAME = "exercises";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EXERCISES_SETS = "exercisesSets";
    }

    public static abstract class WorkoutExerciseTable implements BaseColumns {
        public static final String TABLE_NAME = "workoutExercises";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_WORKOUT_ID = "workoutId";
        public static final String COLUMN_EXERCISE_ID = "exerciseId";
    }

    public static abstract class ExerciseSetTable implements BaseColumns {
        public static final String TABLE_NAME = "exerciseSets";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SET_NUMBER = "setNumber";
        public static final String COLUMN_WEIGHT = "setWeight";
        public static final String COLUMN_WEIGHT_TYPE = "lb";
        public static final String COLUMN_REPS = "setReps";
        public static final String COLUMN_WORKOUT_EXERCISE_ID = "workoutExerciseId";
    }

    public static abstract class CalendarTable implements BaseColumns {
        public static final String TABLE_NAME = "calendar";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WORKOUT_ID = "workoutId";
    }
}
