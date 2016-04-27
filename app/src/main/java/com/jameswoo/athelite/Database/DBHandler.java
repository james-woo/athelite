package com.jameswoo.athelite.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

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
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "athelite";

    private static final String CREATE_WORKOUTPLANS_TABLE =
            "CREATE TABLE " + DBContract.WorkoutPlanTable.TABLE_NAME + "(" +
                    DBContract.WorkoutPlanTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.WorkoutPlanTable.COLUMN_NAME + " TEXT" + ")";

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + DBContract.ExerciseTable.TABLE_NAME + "(" +
                    DBContract.ExerciseTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.ExerciseTable.COLUMN_NAME + " TEXT," +
                    DBContract.ExerciseTable.COLUMN_EXERCISES_SETS + " TEXT"+ ")";

    private static final String CREATE_WORKOUT_EXERCISE_TABLE =
            "CREATE TABLE " + DBContract.WorkoutExerciseTable.TABLE_NAME + "(" +
                    DBContract.WorkoutExerciseTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " INTEGER," +
                    DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID + " INTEGER"+ ")";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUTPLANS_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_WORKOUT_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutPlanTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutExerciseTable.TABLE_NAME);
        onCreate(db);
    }

    public void deleteDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutPlanTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutExerciseTable.TABLE_NAME);
        onCreate(db);
    }

    /*****************************************WORKOUT**********************************************/

    public WorkoutPlan createWorkoutPlan() {
        WorkoutPlan newWorkout = new WorkoutPlan.Builder("New Workout")
                .build();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, newWorkout.getWorkoutPlanName());

        long id = db.insert(DBContract.WorkoutPlanTable.TABLE_NAME, null, values);
        newWorkout.setId(id);

        Exercise newExercise = createExerciseForWorkoutPlan(id);
        newWorkout.addExercise(newExercise);

        db.close();
        return newWorkout;
    }

    public ArrayList<WorkoutPlan> getWorkoutPlans() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<WorkoutPlan> workoutPlans = new ArrayList<>();
        ArrayList<Exercise> exerciseList;

        String query = "SELECT * FROM " + DBContract.WorkoutPlanTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                int workoutPlanId = cursor.getInt(0);

                query = "SELECT * " +
                        " FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " =  \"" + workoutPlanId + "\"";

                Cursor wCursor = db.rawQuery(query, null);

                if(wCursor.moveToFirst()) {
                    exerciseList = new ArrayList<>();
                    do {
                        int exerciseId = wCursor.getInt(2);

                        Exercise exercise = readExerciseWithId(db, exerciseId);
                        try {
                            exerciseList.add(exercise);
                        } catch(NullPointerException e) {
                            Log.e("DBHandler", "getExercisesForWorkoutPlan: ", e);
                        }

                    } while(wCursor.moveToNext());

                    WorkoutPlan workoutPlan = new WorkoutPlan.Builder(cursor.getString(1))
                            .workoutPlanId(workoutPlanId)
                            .exercises(exerciseList)
                            .build();
                    workoutPlans.add(workoutPlan);
                }
                wCursor.close();
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return workoutPlans;
    }

    public void updateWorkoutPlan(WorkoutPlan workoutPlan, ArrayList<Exercise> exerciseList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String WhereClauseWorkoutExerciseTable = DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " = ?";

        String query = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " =  \"" + workoutPlan.getId() + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                db.delete(DBContract.WorkoutExerciseTable.TABLE_NAME,
                        WhereClauseWorkoutExerciseTable,
                        new String[] { String.valueOf(workoutPlan.getId()) });
            } while(cursor.moveToNext());
        }
        cursor.close();

        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, workoutPlan.getWorkoutPlanName());

        String whereClauseWorkoutTable = DBContract.WorkoutPlanTable.COLUMN_ID + " =  ?";
        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_ID + " =  ?";

        db.update(DBContract.WorkoutPlanTable.TABLE_NAME, values, whereClauseWorkoutTable, new String[] { String.valueOf(workoutPlan.getId()) });

        for(Exercise e : exerciseList) {
            values.clear();
            values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, e.getExerciseSets().toString());
            db.update(DBContract.ExerciseTable.TABLE_NAME, values, whereClauseExerciseTable, new String[] { String.valueOf(e.getId()) });

            values.clear();
            values.put(DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID, workoutPlan.getId());
            values.put(DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID, e.getId());
            db.insert(DBContract.WorkoutExerciseTable.TABLE_NAME, null, values);
        }
        db.close();
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

    /****************************************EXERCISE**********************************************/

    public Exercise createExerciseForWorkoutPlan(long workoutPlanId) {
        ExerciseSet exerciseSet1 = new ExerciseSet(1, 0, 0);
        ExerciseSet exerciseSet2 = new ExerciseSet(2, 0, 0);
        ExerciseSet exerciseSet3 = new ExerciseSet(3, 0, 0);
        Exercise newExercise = new Exercise.Builder("New Exercise")
                .exerciseSet(exerciseSet1)
                .exerciseSet(exerciseSet2)
                .exerciseSet(exerciseSet3)
                .build();

        String exerciseSets = "";
        for(ExerciseSet es : newExercise.getExerciseSets()) {
            exerciseSets += es.toString() + ",";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.ExerciseTable.COLUMN_NAME, newExercise.getExerciseName());
        values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, exerciseSets);
        long id = db.insert(DBContract.ExerciseTable.TABLE_NAME, null, values);
        newExercise.setId(id);

        values.clear();

        values.put(DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID, workoutPlanId);
        values.put(DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID, id);
        db.insert(DBContract.WorkoutExerciseTable.TABLE_NAME, null, values);

        db.close();
        return newExercise;
    }

    public ArrayList<Exercise> getExercisesForWorkoutPlan(WorkoutPlan workoutPlan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Exercise> exerciseList = new ArrayList<>();

        String query = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " =  \"" + workoutPlan.getId() + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                int exerciseId = cursor.getInt(2);

                Exercise exercise = readExerciseWithId(db, exerciseId);
                try {
                    exerciseList.add(exercise);
                } catch(NullPointerException e) {
                    Log.e("DBHandler", "getExercisesForWorkoutPlan: ", e);
                }

            } while(cursor.moveToNext());
        }
        cursor.close();
        return exerciseList;
    }

    private Exercise readExerciseWithId(SQLiteDatabase db, int exerciseId) {
        String query;
        query = "SELECT * " +
                " FROM " + DBContract.ExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.ExerciseTable.COLUMN_ID + " =  \"" + exerciseId + "\"";


        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            String exerciseName = cursor.getString(1);
            String exerciseSets = cursor.getString(2);

            Exercise exercise = new Exercise.Builder(exerciseName)
                    .exerciseId(exerciseId)
                    .exerciseSets(convertToExerciseSetList(exerciseSets))
                    .build();
            cursor.close();
            return exercise;

        }
        cursor.close();
        return null;
    }

    public void updateExercise(Exercise exercise, ArrayList<ExerciseSet> exerciseSetList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, TextUtils.join(",", exerciseSetList));
        values.put(DBContract.ExerciseTable.COLUMN_NAME, exercise.getExerciseName());

        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_ID + " =  ?";

        db.update(DBContract.ExerciseTable.TABLE_NAME, values, whereClauseExerciseTable, new String[] { String.valueOf(exercise.getId()) });

        db.close();
    }

    /***************************************EXERCISESET********************************************/

    public void createExerciseSetForExercise(Exercise exercise, ExerciseSet exerciseSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + DBContract.ExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.ExerciseTable.COLUMN_ID + " =  \"" + exercise.getId() + "\"";
        Cursor cursor = db.rawQuery(query, null);
        String exerciseSets = "";
        if(cursor.moveToFirst()) {
            exerciseSets = cursor.getString(2) + exerciseSet.toString() + ",";
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(DBContract.ExerciseTable.COLUMN_EXERCISES_SETS, exerciseSets);
        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_ID + " =  ?";

        db.update(DBContract.ExerciseTable.TABLE_NAME, values, whereClauseExerciseTable, new String[] { String.valueOf(exercise.getId()) });
    }

    private ArrayList<ExerciseSet> convertToExerciseSetList(String exerciseSets) {
        String[] exerciseSetArray = exerciseSets.replaceAll("\\[|\\]", "").split(",");

        ArrayList<ExerciseSet> exerciseSetList = new ArrayList<>();

        for (String es : exerciseSetArray) {
            String[] set = es.replaceAll("^\\s+", "").split("\\s+");
            ExerciseSet exerciseSet = new ExerciseSet(
                    Integer.parseInt(set[0]),
                    Double.parseDouble(set[1]),
                    Integer.parseInt(set[2]));
            exerciseSetList.add(exerciseSet);
        }
        return exerciseSetList;
    }

}
