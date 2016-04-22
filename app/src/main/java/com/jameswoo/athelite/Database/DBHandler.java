package com.jameswoo.athelite.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.Util.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "athelite";

    private static final String CREATE_WORKOUTPLANS_TABLE =
            "CREATE TABLE " + DBContract.WorkoutPlanTable.TABLE_NAME + "(" +
                    DBContract.WorkoutPlanTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.WorkoutPlanTable.COLUMN_NAME + " TEXT," +
                    DBContract.WorkoutPlanTable.COLUMN_EXERCISES + " TEXT" + ")";

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + DBContract.ExerciseTable.TABLE_NAME + "(" +
                    DBContract.ExerciseTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.ExerciseTable.COLUMN_NAME + " TEXT," +
                    DBContract.ExerciseTable.COLUMN_EXERCISES_SETS + " TEXT"+ ")";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUTPLANS_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutPlanTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseTable.TABLE_NAME);
        onCreate(db);
    }

    public void deleteDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutPlanTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseTable.TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<WorkoutPlan> getWorkoutPlans() {
        SQLiteDatabase db = this.getWritableDatabase();

        String eQuery = "SELECT * FROM " + DBContract.ExerciseTable.TABLE_NAME;
        Cursor eCursor = db.rawQuery(eQuery, null);

        //ArrayList<Exercise> exerciseList = new ArrayList<>();
        ArrayMap<String, ArrayList<ExerciseSet>> exerciseExerciseSetArrayMap = new ArrayMap<>();

        if(eCursor.moveToFirst()) {
            do {
                int exerciseId = Integer.parseInt(eCursor.getString(0));
                String exerciseName = eCursor.getString(1);
                ArrayList<ExerciseSet> exerciseSetList = new ArrayList<>();
                String[] exerciseSets = (eCursor.getString(2).replaceAll("\\[|\\]", "")).split(",");

                for (int i = 0; i < exerciseSets.length; i++) {
                    String[] set = exerciseSets[i].replaceAll("^\\s+", "").split("\\s+");
                    ExerciseSet exerciseSet = new ExerciseSet(
                            Integer.parseInt(set[0]),
                            Double.parseDouble(set[1]),
                            Integer.parseInt(set[2]));
                    exerciseSetList.add(exerciseSet);
                }

                Exercise exercise = new Exercise.Builder(exerciseName)
                        .exerciseId(exerciseId)
                        .exerciseSets(exerciseSetList)
                        .build();

                //exerciseList.add(exercise);
                exerciseExerciseSetArrayMap.put(exercise.getExerciseName(), exerciseSetList);

            } while(eCursor.moveToNext());
        }

        eCursor.close();

        String query = "SELECT * FROM " + DBContract.WorkoutPlanTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<WorkoutPlan> workoutPlans = new ArrayList<>();
        ArrayList<Exercise> workoutPlanExercises = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                int workoutPlanId = Integer.parseInt(cursor.getString(0));
                List<String> exercises = Arrays.asList(cursor.getString(2).split("\\s*,\\s*"));

                for(String e : exercises) {
                    Exercise exercise = new Exercise.Builder(e)
                            .exerciseSets(exerciseExerciseSetArrayMap.get(e))
                            .build();
                    workoutPlanExercises.add(exercise);
                }

                WorkoutPlan workoutPlan = new WorkoutPlan.Builder(cursor.getString(1))
                                                .workoutPlanId(workoutPlanId)
                                                .exercises(workoutPlanExercises)
                                                .build();
                workoutPlans.add(workoutPlan);
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return workoutPlans;
    }

    public long addWorkoutPlan(WorkoutPlan workoutPlan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues eValues = new ContentValues();

        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, workoutPlan.getWorkoutPlanName());

        ArrayList<Exercise> exerciseList = workoutPlan.getWorkoutPlanExercises();
        String exercises = "";
        for(Exercise e : exerciseList) {
            exercises += e.getExerciseName() + ",";
            eValues.put(DBContract.ExerciseTable.COLUMN_NAME, e.getExerciseName());
            eValues.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, e.getExerciseSets().toString());
            db.insert(DBContract.ExerciseTable.TABLE_NAME, null, eValues);
            eValues.clear();
        }
        values.put(DBContract.WorkoutPlanTable.COLUMN_EXERCISES, exercises);
        long id = db.insert(DBContract.WorkoutPlanTable.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public WorkoutPlan createWorkoutPlan() {
        WorkoutPlan newWorkout = new WorkoutPlan.Builder("New Workout")
                .exercise(createExercise())
                .build();

        long id = addWorkoutPlan(newWorkout);

        newWorkout.setId(id);

        return newWorkout;
    }

    public void updateWorkoutPlan(WorkoutPlan workoutPlan, ArrayList<Exercise> exerciseList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues eValues = new ContentValues();

        values.put(DBContract.WorkoutPlanTable.COLUMN_EXERCISES, TextUtils.join(", ", exerciseList));
        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, workoutPlan.getWorkoutPlanName());

        String whereClauseWorkoutTable = DBContract.WorkoutPlanTable.COLUMN_ID + " =  ?";
        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_ID + " =  ?";

        for(Exercise e : exerciseList) {
            eValues.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, e.getExerciseSets().toString());
            db.update(DBContract.ExerciseTable.TABLE_NAME, eValues, whereClauseExerciseTable, new String[] { String.valueOf(e.getId()) });
            eValues.clear();
        }

        db.update(DBContract.WorkoutPlanTable.TABLE_NAME, values, whereClauseWorkoutTable, new String[] { String.valueOf(workoutPlan.getId()) });
        db.close();
    }

    public WorkoutPlan findWorkoutPlan(long workoutPlanId) {
        String query = "SELECT * FROM " + DBContract.WorkoutPlanTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutPlanTable.COLUMN_ID + " =  \"" + workoutPlanId + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        WorkoutPlan workoutPlan;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            ArrayList<Exercise> exerciseList = new ArrayList<>();

            List<String> exercises = Arrays.asList(cursor.getString(2).split("\\s*,\\s*"));
            for(String e : exercises) {
                Exercise exercise = new Exercise.Builder(e).build();
                exerciseList.add(exercise);
            }

            workoutPlan = new WorkoutPlan.Builder(cursor.getString(1))
                    .workoutPlanId(workoutPlanId)
                    .exercises(exerciseList)
                    .build();

            cursor.close();
        } else {
            workoutPlan = null;
        }

        db.close();
        return workoutPlan;
    }

    public boolean deleteWorkoutPlan(String workoutPlanName) {
        boolean result = false;
        String query = "SELECT * FROM " + DBContract.WorkoutPlanTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutPlanTable.COLUMN_NAME + " =  \"" + workoutPlanName + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutPlan workoutPlan;

        if(cursor.moveToFirst()) {
            workoutPlan = new WorkoutPlan.Builder(cursor.getString(1))
                    .workoutPlanId(Integer.parseInt(cursor.getString(0)))
                    .build();
            db.delete(DBContract.WorkoutPlanTable.TABLE_NAME, DBContract.WorkoutPlanTable.COLUMN_ID + " = ?",
                    new String[] { String.valueOf(workoutPlan.getId()) });
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    public ArrayList<Exercise> getExercisesForWorkoutPlan(WorkoutPlan workoutPlan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Exercise> exerciseList = new ArrayList<>();

        for(Exercise e : workoutPlan.getWorkoutPlanExercises()) {
            String eQuery = "SELECT * FROM " + DBContract.ExerciseTable.TABLE_NAME +
                    " WHERE " + DBContract.ExerciseTable.COLUMN_NAME + " =  \"" + e.getExerciseName() + "\"";
            Cursor eCursor = db.rawQuery(eQuery, null);

            if(eCursor.moveToFirst()) {
                String[] exerciseSets = (eCursor.getString(2).replaceAll("\\[|\\]", "")).split(",");
                ArrayList<ExerciseSet> exerciseSetList = new ArrayList<>();
                for (String es : exerciseSets) {
                    String[] set = es.replaceAll("^\\s+", "").split("\\s+");
                    ExerciseSet exerciseSet = new ExerciseSet(
                            Integer.parseInt(set[0]),
                            Double.parseDouble(set[1]),
                            Integer.parseInt(set[2]));
                    exerciseSetList.add(exerciseSet);
                }

                Exercise exercise = new Exercise.Builder(eCursor.getString(1))
                        .exerciseId(e.getId())
                        .exerciseSets(exerciseSetList)
                        .build();

                exerciseList.add(exercise);
            }
        }

        return exerciseList;
    }

    public long addExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.ExerciseTable.COLUMN_NAME, exercise.getExerciseName());

        String exerciseSets = "";
        for(ExerciseSet es : exercise.getExerciseSets()) {
            exerciseSets += es.toString() + ",";
        }

        values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, exerciseSets);
        long id = db.insert(DBContract.ExerciseTable.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Exercise createExercise() {
        ExerciseSet exerciseSet1 = new ExerciseSet(1, 0, 0);
        ExerciseSet exerciseSet2 = new ExerciseSet(2, 0, 0);
        ExerciseSet exerciseSet3 = new ExerciseSet(3, 0, 0);
        Exercise newExercise = new Exercise.Builder("Tap to edit")
                .exerciseSet(exerciseSet1)
                .exerciseSet(exerciseSet2)
                .exerciseSet(exerciseSet3)
                .build();

        long id = addExercise(newExercise);

        newExercise.setId(id);

        return newExercise;
    }

    public void addExerciseSetToExercise(Exercise exercise, ExerciseSet exerciseSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        String eQuery = "SELECT * FROM " + DBContract.ExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.ExerciseTable.COLUMN_NAME + " =  \"" + exercise.getExerciseName() + "\"";
        Cursor eCursor = db.rawQuery(eQuery, null);

        String exerciseSets = "";

        if(eCursor.moveToFirst()) {
            exerciseSets = eCursor.getString(2) + exerciseSet.toString() + ",";
            System.out.println("db: " + exerciseSets);
        }

        ContentValues values = new ContentValues();
        values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, exerciseSets);
        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_NAME + " =  ?";

        db.update(DBContract.ExerciseTable.TABLE_NAME, values, whereClauseExerciseTable, new String[] { String.valueOf(exercise.getExerciseName()) });
    }

    public void updateExercise(Exercise exercise, ArrayList<ExerciseSet> exerciseSetList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, TextUtils.join(", ", exerciseSetList));
        values.put(DBContract.ExerciseTable.COLUMN_NAME, exercise.getExerciseName());

        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_ID + " =  ?";

        db.update(DBContract.ExerciseTable.TABLE_NAME, values, whereClauseExerciseTable, new String[] { String.valueOf(exercise.getId()) });

        db.close();
    }

}
