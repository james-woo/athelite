package com.jameswoo.athelite.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.Model.WorkoutPlan;

import java.util.Date;
import java.util.ArrayList;


public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 14;

    // Database Name
    private static final String DATABASE_NAME = "athelite";

    private static final String CREATE_WORKOUTPLANS_TABLE =
            "CREATE TABLE " + DBContract.WorkoutPlanTable.TABLE_NAME + "(" +
                    DBContract.WorkoutPlanTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.WorkoutPlanTable.COLUMN_NAME + " TEXT," +
                    DBContract.WorkoutPlanTable.COLUMN_TEMPLATE + " INTEGER" + ")";

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + DBContract.ExerciseTable.TABLE_NAME + "(" +
                    DBContract.ExerciseTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.ExerciseTable.COLUMN_NAME + " TEXT" + ")";

    private static final String CREATE_WORKOUT_EXERCISE_TABLE =
            "CREATE TABLE " + DBContract.WorkoutExerciseTable.TABLE_NAME + "(" +
                    DBContract.WorkoutExerciseTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " INTEGER," +
                    DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID + " INTEGER" + ")";

    private static final String CREATE_EXERCISESET_TABLE =
            "CREATE TABLE " + DBContract.ExerciseSetTable.TABLE_NAME + "(" +
                    DBContract.ExerciseSetTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.ExerciseSetTable.COLUMN_SET_NUMBER + " INTEGER," +
                    DBContract.ExerciseSetTable.COLUMN_WEIGHT + " DOUBLE," +
                    DBContract.ExerciseSetTable.COLUMN_WEIGHT_TYPE + " TEXT," +
                    DBContract.ExerciseSetTable.COLUMN_REPS + " INTEGER," +
                    DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID + " INTEGER" + ")";

    private static final String CREATE_CALENDAR_TABLE =
            "CREATE TABLE " + DBContract.CalendarTable.TABLE_NAME + "(" +
                    DBContract.CalendarTable.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.CalendarTable.COLUMN_DATE + " INTEGER," +
                    DBContract.CalendarTable.COLUMN_WORKOUT_ID + " TEXT" + ")";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUTPLANS_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_WORKOUT_EXERCISE_TABLE);
        db.execSQL(CREATE_EXERCISESET_TABLE);
        db.execSQL(CREATE_CALENDAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutPlanTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutExerciseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseSetTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.CalendarTable.TABLE_NAME);
        onCreate(db);
    }

    public void deleteDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutPlanTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WorkoutExerciseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ExerciseSetTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.CalendarTable.TABLE_NAME);
        onCreate(db);
    }

    private class bool {
        public static final int FALSE = 0;
        public static final int TRUE = 1;
    }

    /*****************************************WORKOUTPLANS*****************************************/

    public WorkoutPlan createWorkoutPlan() {
        WorkoutPlan newWorkout = new WorkoutPlan.Builder("New Workout").build();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, newWorkout.getWorkoutPlanName());
        values.put(DBContract.WorkoutPlanTable.COLUMN_TEMPLATE, bool.TRUE);

        long id = db.insert(DBContract.WorkoutPlanTable.TABLE_NAME, null, values);
        newWorkout.setId(id);

        Exercise newExercise = createExerciseForWorkoutPlan(db, newWorkout);
        newWorkout.addExercise(newExercise);

        db.close();
        return newWorkout;
    }

    public ArrayList<WorkoutPlan> getWorkoutPlans() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<WorkoutPlan> workoutPlans = new ArrayList<>();
        ArrayList<Exercise> exerciseList;

        String query = "SELECT * " +
                        " FROM " + DBContract.WorkoutPlanTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutPlanTable.COLUMN_TEMPLATE + " =  \"" + bool.TRUE + "\"";;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                long workoutPlanId = cursor.getLong(0);

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

    public void updateWorkoutPlan(WorkoutPlan workoutPlan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String whereClauseWorkoutTable = DBContract.WorkoutPlanTable.COLUMN_ID + " =  ?";
        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, workoutPlan.getWorkoutPlanName());
        db.update(DBContract.WorkoutPlanTable.TABLE_NAME, values, whereClauseWorkoutTable,
                new String[] { String.valueOf(workoutPlan.getId()) });

        db.close();
    }

    public boolean deleteWorkoutPlan(WorkoutPlan workoutPlan) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = false;

        long workoutPlanId = workoutPlan.getId();

        String query = "SELECT * FROM " + DBContract.WorkoutPlanTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutPlanTable.COLUMN_ID +
                        " =  \"" + workoutPlanId + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {

            String weQuery = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                    " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID +
                    " =  \"" + workoutPlanId + "\"";

            Cursor weCursor = db.rawQuery(weQuery, null);

            if(weCursor.moveToFirst()) {
                do {
                    int workoutExerciseId = weCursor.getInt(0);
                    int exerciseId = weCursor.getInt(2);
                    db.delete(DBContract.ExerciseSetTable.TABLE_NAME,
                            DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID + " = ?",
                            new String[] { String.valueOf(workoutExerciseId) });
                    db.delete(DBContract.ExerciseTable.TABLE_NAME,
                            DBContract.ExerciseTable.COLUMN_ID + " = ?",
                            new String[] { String.valueOf(exerciseId) });
                } while(weCursor.moveToNext());
            }
            weCursor.close();

            db.delete(DBContract.WorkoutExerciseTable.TABLE_NAME,
                    DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " = ?",
                    new String[] { String.valueOf(workoutPlanId) });

            db.delete(DBContract.WorkoutPlanTable.TABLE_NAME,
                    DBContract.WorkoutPlanTable.COLUMN_ID + " = ?",
                    new String[] { String.valueOf(workoutPlanId) });

            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    /****************************************WORKOUTS**********************************************/
    public WorkoutPlan getWorkoutForDay(Date day) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * " +
                " FROM " + DBContract.CalendarTable.TABLE_NAME +
                " WHERE " + DBContract.CalendarTable.COLUMN_DATE +
                " BETWEEN " + "\"" + (day.getTime() - 86400000) + "\"" +
                " AND "+ "\"" + (day.getTime() + 86400000) + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            long workoutId = cursor.getLong(2);
            return readWorkout(workoutId);
        }

        cursor.close();
        db.close();

        return null;
    }

    public WorkoutPlan getNextWorkoutAfterDay(Date day) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                DBContract.CalendarTable.TABLE_NAME,
                null,
                DBContract.CalendarTable.COLUMN_DATE + " > " + day.getTime(),
                null, // No selection args
                null, // No grouping
                null, // No having
                DBContract.CalendarTable.COLUMN_DATE + " ASC",
                null); // No limit

        if(cursor.moveToFirst()) {
            long workoutId = cursor.getLong(2);
            return readWorkout(workoutId);
        }

        cursor.close();
        db.close();

        return null;
    }

    public WorkoutPlan getPreviousWorkoutBeforeDay(Date day) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                DBContract.CalendarTable.TABLE_NAME,
                null,
                DBContract.CalendarTable.COLUMN_DATE + " < " + day.getTime(),
                null, // No selection args
                null, // No grouping
                null, // No having
                DBContract.CalendarTable.COLUMN_DATE + " DESC",
                null); // No limit

        if(cursor.moveToFirst()) {
            long workoutId = cursor.getLong(2);
            return readWorkout(workoutId);
        }

        cursor.close();
        db.close();

        return null;
    }

    public ArrayList<Date> getWorkoutDays() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * " +
                " FROM " + DBContract.CalendarTable.TABLE_NAME;

        ArrayList<Date> workoutDays = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                workoutDays.add(new Date(cursor.getLong(1)));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return workoutDays;
    }

    public WorkoutPlan CreateWorkoutFromPlan(SQLiteDatabase db, WorkoutPlan workoutPlan) {
        ContentValues values = new ContentValues();

        values.put(DBContract.WorkoutPlanTable.COLUMN_NAME, workoutPlan.getWorkoutPlanName());
        values.put(DBContract.WorkoutPlanTable.COLUMN_TEMPLATE, bool.FALSE);

        long id = db.insert(DBContract.WorkoutPlanTable.TABLE_NAME, null, values);
        workoutPlan.setId(id);

        Exercise newExercise = createExerciseForWorkoutPlan(db, workoutPlan);
        workoutPlan.addExercise(newExercise);

        return workoutPlan;
    }


    public WorkoutPlan readWorkout(long workoutId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Exercise> exerciseList;

        String query = "SELECT * " +
                " FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID + " =  \"" + workoutId + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            exerciseList = new ArrayList<>();
            do {
                int exerciseId = cursor.getInt(2);

                Exercise exercise = readExerciseWithId(db, exerciseId);
                try {
                    exerciseList.add(exercise);
                } catch(NullPointerException e) {
                    Log.e("DBHandler", "getExercisesForWorkoutPlan: ", e);
                }

            } while(cursor.moveToNext());
            cursor.close();
            String wQuery = "SELECT * " +
                    " FROM " + DBContract.WorkoutPlanTable.TABLE_NAME +
                    " WHERE " + DBContract.WorkoutPlanTable.COLUMN_ID + " =  \"" + workoutId + "\"" +
                    " AND " + DBContract.WorkoutPlanTable.COLUMN_TEMPLATE +  " =  \"" + bool.FALSE + "\"";
            Cursor wCursor = db.rawQuery(wQuery, null);
            String workoutName = "";
            if(wCursor.moveToFirst()) {
                workoutName = wCursor.getString(1);
            }

            wCursor.close();
            db.close();
            return new WorkoutPlan.Builder(workoutName)
                    .workoutPlanId(workoutId)
                    .exercises(exerciseList)
                    .build();
        }

        cursor.close();
        db.close();
        return null;
    }

    /****************************************EXERCISE**********************************************/

    public Exercise createExerciseForWorkoutPlan(SQLiteDatabase db, WorkoutPlan workoutPlan) {
        long workoutPlanId = workoutPlan.getId();
        Exercise newExercise = new Exercise.Builder("New Exercise").build();

        ContentValues values = new ContentValues();
        values.put(DBContract.ExerciseTable.COLUMN_NAME, newExercise.getExerciseName());
        long id = db.insert(DBContract.ExerciseTable.TABLE_NAME, null, values);
        newExercise.setId(id);
        values.clear();
        values.put(DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID, workoutPlanId);
        values.put(DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID, id);
        db.insert(DBContract.WorkoutExerciseTable.TABLE_NAME, null, values);

        ArrayList<ExerciseSet> exerciseSets = new ArrayList<>();

        exerciseSets.add(createExerciseSetForExercise(db, newExercise, 1));
        exerciseSets.add(createExerciseSetForExercise(db, newExercise, 2));
        exerciseSets.add(createExerciseSetForExercise(db, newExercise, 3));

        newExercise.setExerciseSets(exerciseSets);

        return newExercise;
    }

    public ArrayList<Exercise> getExercisesForWorkoutPlan(WorkoutPlan workoutPlan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Exercise> exerciseList = new ArrayList<>();

        String query = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                        " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_WORKOUT_ID +
                        " =  \"" + workoutPlan.getId() + "\"";
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
        db.close();
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

            Exercise exercise = new Exercise.Builder(exerciseName)
                    .exerciseId(exerciseId)
                    .exerciseSets(readExerciseSetsWithExerciseId(db, exerciseId))
                    .build();
            cursor.close();
            return exercise;

        }
        cursor.close();
        return null;
    }

    public void updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.ExerciseTable.COLUMN_NAME, exercise.getExerciseName());
        String whereClauseExerciseTable = DBContract.ExerciseTable.COLUMN_ID + " =  ?";
        db.update(DBContract.ExerciseTable.TABLE_NAME, values, whereClauseExerciseTable,
                new String[] { String.valueOf(exercise.getId()) });

        long workoutExerciseId = getWorkoutExerciseIdForExerciseSet(exercise.getId(), db);

        String query = "SELECT * FROM " + DBContract.ExerciseSetTable.TABLE_NAME +
                " WHERE " + DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID +
                " =  \"" + workoutExerciseId + "\"";

        Cursor cursor = db.rawQuery(query, null);

        String where = DBContract.ExerciseSetTable.COLUMN_SET_NUMBER + " =  ? AND " +
                DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID + " =  ? ";

        if(cursor.moveToFirst()) {
            do {
                int setNumber = cursor.getInt(1);
                ArrayList<ExerciseSet> exerciseSets = exercise.getExerciseSets();
                values.clear();
                values.put(DBContract.ExerciseSetTable.COLUMN_WEIGHT, exerciseSets.get(setNumber - 1).getSetWeight());
                values.put(DBContract.ExerciseSetTable.COLUMN_WEIGHT_TYPE, exerciseSets.get(setNumber - 1).getWeightType());
                values.put(DBContract.ExerciseSetTable.COLUMN_REPS, exerciseSets.get(setNumber - 1).getSetReps());
                db.update(DBContract.ExerciseSetTable.TABLE_NAME, values, where,
                        new String[]{String.valueOf(setNumber), String.valueOf(workoutExerciseId)});
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public void deleteExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        long exerciseId = exercise.getId();

        String query = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID +
                " =  \"" + exerciseId + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                int workoutExerciseId = cursor.getInt(0);
                db.delete(DBContract.ExerciseSetTable.TABLE_NAME,
                        DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID + " = ?",
                        new String[] { String.valueOf(workoutExerciseId) });
            } while(cursor.moveToNext());
        }

        db.delete(DBContract.ExerciseTable.TABLE_NAME,
                DBContract.ExerciseTable.COLUMN_ID + " = ?",
                new String[] { String.valueOf(exerciseId) });

        db.delete(DBContract.WorkoutExerciseTable.TABLE_NAME,
                DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID + " = ?",
                new String[] { String.valueOf(exerciseId) });

        cursor.close();
        db.close();
    }

    /***************************************EXERCISELIST*******************************************/
    private String[] exercises = {"Ab Crunch Machine","Ab Roller","Adductor","Adductor/Groin ","Advanced Kettlebell Windmill","Air Bike","Alien Squat","All Fours Quad Stretch","Alternate Hammer Curl","Alternate Heel Touchers","Alternate Incline Dumbbell Curl","Alternate Leg Diagonal Bound","Alternating Cable Shoulder Press","Alternating Deltoid Raise","Alternating Floor Press","Alternating Hang Clean","Alternating Kettlebell Press","Alternating Kettlebell Row","Alternating Leg Swing","Alternating Renegade Row","Ankle Circles","Ankle On The Knee","Anterior Tibialis-SMR","Anti-Gravity Press","Arm Circles","Arnold Dumbbell Press","Around The Worlds","Assisted Chin-Up","Atlas Stone Trainer","Atlas Stones","Axle Clean And Press","Axle Deadlift","Back Flyes - With Bands","Backward Drag","Backward Medicine Ball Throw","Balance Board","Ball Leg Curl","Band Assisted Pull-Up","Band Good Morning","Band Good Morning (Pull Through)","Band Hip Adductions","Band Pull Apart","Band Skull Crusher","Barbell Ab Rollout","Barbell Ab Rollout - On Knees","Barbell Bench Press - Medium Grip","Barbell Bench Press-Wide Grip","Barbell Curl","Barbell Curls Lying Against An Incline","Barbell Deadlift","Barbell Front Raise","Barbell Full Squat","Barbell Glute Bridge","Barbell Guillotine Bench Press","Barbell Hack Squat","Barbell Hip Thrust","Barbell Incline Bench Press Medium-Grip","Barbell Incline Shoulder Raise","Barbell Lunge","Barbell Rear Delt Row","Barbell Reverse Lunge","Barbell Rollout from Bench","Barbell Seated Calf Raise","Barbell Shoulder Press","Barbell Shrug","Barbell Shrug Behind The Back","Barbell Side Bend","Barbell Side Split Squat","Barbell Squat","Barbell Squat To A Bench","Barbell Squat To A Box","Barbell Step Ups","Barbell Thruster","Barbell Walking Lunge","Battling Ropes","Bear Crawl Sled Drags ","Behind Head Chest Stretch","Bench Dips","Bench Jump","Bench Press - Powerlifting","Bench Press - With Bands","Bench Press with Chains","Bench Press With Short Bands","Bench Sprint","Bent Over Barbell Row","Bent Over Dumbbell Rear Delt Raise With Head On Bench","Bent Over Low-Pulley Side Lateral","Bent Over One-Arm Long Bar Row","Bent Over Two-Arm Long Bar Row","Bent Over Two-Dumbbell Row","Bent Over Two-Dumbbell Row With Palms In","Bent Press","Bent-Arm Barbell Pullover","Bent-Arm Dumbbell Pullover","Bent-Knee Hip Raise","Biceps Curl To Shoulder Press","Bicycling","Bicycling, Stationary","Board Press","Body Tricep Press","Body-Up","Bodyweight Flyes","Bodyweight Lunge","Bodyweight Mid Row","Bodyweight Reverse Lunge","Bodyweight Squat","Bodyweight Walking Lunge","Bosu Ball Cable Crunch With Side Bends","Bosu Ball Crunch","Bosu Ball Push-Up","Bosu Ball Squat","Bottoms Up","Bottoms-Up Clean From The Hang Position","Box Jump (Multiple Response)","Box Skip","Box Squat","Box Squat with Bands","Box Squat with Chains","Brachialis-SMR","Bradford/Rocky Presses","Burpee","Burpee Over Barbell","Burpee Pull-Up","Butt Kicks","Butt Lift (Bridge)","Butt-Ups","Butterfly","Cable Chest Press","Cable Crossover","Cable Crunch","Cable Deadlifts","Cable Hammer Curls - Rope Attachment","Cable Hip Adduction","Cable Incline Pushdown","Cable Incline Triceps Extension","Cable Internal Rotation","Cable Iron Cross","Cable Judo Flip","Cable Lying Triceps Extension","Cable One Arm Tricep Extension","Cable Preacher Curl","Cable Rear Delt Fly","Cable Reverse Crunch","Cable Rope Overhead Triceps Extension","Cable Rope Rear-Delt Rows","Cable Russian Twists","Cable Seated Crunch","Cable Seated Lateral Raise","Cable Shoulder Press","Cable Shrugs","Cable Tuck Reverse Crunch","Cable Wrist Curl","Calf Press","Calf Press On The Leg Press Machine","Calf Raise On A Dumbbell","Calf Raises - With Bands","Calf Stretch Elbows Against Wall","Calf Stretch Hands Against Wall","Calf-Machine Shoulder Shrug","Calves-SMR","Car Deadlift","Car Drivers","Carioca Quick Step","Cat Stretch","Catch and Overhead Throw","Chain Handle Extension","Chain Press","Chair Leg Extended Stretch","Chair Lower Back Stretch","Chair Squat","Chair Upper Body Stretch","Chest And Front Of Shoulder Stretch","Chest Push (multiple response)","Chest Push (single response)","Chest Push from 3 point stance","Chest Push with Run Release","Chest Stretch on Stability Ball","Child's Pose","Chin To Chest Stretch","Chin-Up","Circus Bell","Clam","Clean","Clean and Jerk","Clean and Press","Clean Deadlift","Clean from Blocks","Clean Pull ","Clean Shrug","Clock Push-Up","Close-Grip Barbell Bench Press","Close-Grip Dumbbell Press","Close-Grip EZ Bar Curl","Close-Grip EZ-Bar Curl with Band","Close-Grip EZ-Bar Press","Close-Grip Front Lat Pulldown","Close-Grip Push-Up off of a Dumbbell","Close-Grip Standing Barbell Curl","Close-Hands Push-Up","Cobra Triceps Extension","Cocoons","Conan's Wheel","Concentration Curls","Cross Body Hammer Curl","Cross Crunch","Cross Over - With Bands","Cross-Body Crunch","Crossover Reverse Lunge","Crucifix","Crunch - Hands Overhead","Crunch - Legs On Exercise Ball","Crunches","Cuban Press","Dancer's Stretch","Dead Bug","Deadlift with Bands","Deadlift with Chains","Decline Barbell Bench Press","Decline Close-Grip Bench To Skull Crusher","Decline Crunch","Decline Dumbbell Bench Press","Decline Dumbbell Flyes","Decline Dumbbell Triceps Extension","Decline EZ Bar Triceps Extension","Decline Oblique Crunch","Decline Push-Up","Decline Reverse Crunch","Decline Smith Press","Defensive Slide","Deficit Deadlift","Depth Jump Leap","Diamond Push-Up","Dip Machine","Dips - Chest Version","Dips - Triceps Version","Dive Bomber Push-Up","Donkey Calf Raises","Double Kettlebell Alternating Hang Clean","Double Kettlebell Jerk","Double Kettlebell Push Press","Double Kettlebell Snatch","Double Kettlebell Windmill","Double Leg Butt Kick","Downward Facing Balance","Drag Curl","Drop Push","Dumbbell Alternate Bicep Curl","Dumbbell Bench Press","Dumbbell Bench Press with Neutral Grip","Dumbbell Bicep Curl","Dumbbell Clean","Dumbbell Clean And Jerk","Dumbbell Floor Press","Dumbbell Flyes","Dumbbell Goblet Squat","Dumbbell Incline Row","Dumbbell Incline Shoulder Raise","Dumbbell Lunges","Dumbbell Lying One-Arm Rear Lateral Raise","Dumbbell Lying Pronation","Dumbbell Lying Rear Lateral Raise","Dumbbell Lying Supination","Dumbbell One-Arm Shoulder Press","Dumbbell One-Arm Triceps Extension","Dumbbell One-Arm Upright Row","Dumbbell Overhead Squat ","Dumbbell Prone Incline Curl","Dumbbell Raise","Dumbbell Rear Delt Row","Dumbbell Rear Lunge","Dumbbell Scaption","Dumbbell Seated Box Jump","Dumbbell Seated One-Leg Calf Raise","Dumbbell Shoulder Press","Dumbbell Shrug","Dumbbell Side Bend","Dumbbell Side Lunge","Dumbbell Squat","Dumbbell Squat To A Bench","Dumbbell Squat To Shoulder Press","Dumbbell Step Ups","Dumbbell Tricep Extension -Pronated Grip","Dumbbell Walking Lunge","Dynamic Back Stretch","Dynamic Chest Stretch","Elbow Circles","Elbow to Knee","Elbows Back","Elevated Back Lunge","Elevated Cable Rows","Elliptical Trainer","Exercise Ball Crunch","Exercise Ball Pull-In","Extended Range One-Arm Kettlebell Floor Press","External Rotation","External Rotation with Band","External Rotation with Cable","EZ-Bar Curl","EZ-Bar Skullcrusher","Face Pull","Farmer's Walk","Fast Kick With Arm Circles","Fast Skipping","Feet Jack","Finger Curls","Fire Hydrant","Flat Bench Cable Flyes","Flat Bench Leg Pull-In","Flat Bench Lying Leg Raise","Flexor Incline Dumbbell Curls","Floor Glute-Ham Raise","Floor Press","Floor Press with Chains","Flutter Kicks","Foot-SMR","Football Up-Down","Forward Band Walk","Forward Drag with Press","Frankenstein Squat","Freehand Jump Squat","Frog Hops","Frog Sit-Ups","Front Barbell Squat","Front Barbell Squat To A Bench","Front Box Jump","Front Cable Raise","Front Cone Hops (or hurdle hops)","Front Dumbbell Raise","Front Incline Dumbbell Raise","Front Leg Raises","Front Plate Raise","Front Raise And Pullover","Front Squat (Bodybuilder)","Front Squat (Clean Grip)","Front Squat Push Press","Front Squats With Two Kettlebells","Front Two-Dumbbell Raise","Front-To-Back Squat With Belt","Full Range-Of-Motion Lat Pulldown","Gironda Sternum Chins","Glute Bridge Hamstring Walkout","Glute Ham Raise","Glute Kickback","Goblet Squat","Good Morning","Good Morning off Pins","Gorilla Chin/Crunch","Groin and Back Stretch","Groiners ","Hack Squat","Half-kneeling Dumbbell Shoulder Press","Hammer Curls","Hammer Grip Incline DB Bench Press","Hamstring Stretch","Hamstring-SMR","Hand Release Push-Up","Hand Stand Push-Up","Handstand Push-Ups","Hang Clean","Hang Clean - Below the Knees","Hang Snatch","Hang Snatch - Below Knees","Hanging Bar Good Morning","Hanging Leg Raise","Hanging Oblique Knee Raise","Hanging Pike","Hanging Pike","Heaving Snatch Balance","Heavy Bag Thrust","High Cable Curls","High Kick","High Knee Jog","Hip Circle","Hip Circles (prone)","Hip Crossover","Hip Extension with Bands","Hip Flexion with Band","Hip Lift with Band","Hip Stretch With Twist","Hug A Ball","Hug Knees To Chest","Hurdle Hops","Hyperextensions (Back Extensions)","Hyperextensions With No Hyperextension Bench","Ice Skater","Iliotibial Tract-SMR","Inchworm","Incline Barbell Triceps Extension","Incline Bench Pull","Incline Cable Chest Press","Incline Cable Flye","Incline Dumbbell Bench With Palms Facing In","Incline Dumbbell Curl","Incline Dumbbell Flyes","Incline Dumbbell Flyes - With A Twist","Incline Dumbbell Press","Incline Dumbbell Press Reverse-Grip","Incline Hammer Curls","Incline Inner Biceps Curl","Incline Push-Up","Incline Push-Up Close-Grip","Incline Push-Up Depth Jump","Incline Push-Up Medium","Incline Push-Up Reverse Grip","Incline Push-Up Wide","Intermediate Groin Stretch","Intermediate Hip Flexor and Quad Stretch","Internal Rotation with Band","Inverted Row","Inverted Row with Straps","Iron Cross","Iron Crosses (stretch)","Isometric Chest Squeezes","Isometric Neck Exercise - Front And Back","Isometric Neck Exercise - Sides","Isometric Wipers","IT Band and Glute Stretch","Jackknife Sit-Up","Janda Sit-Up","Jefferson Squats","Jerk Balance","Jerk Dip Squat","JM Press","JM Press","JM Press With Bands","Jog In Place","Jogging-Treadmill","Jump Lunge To Feet Jack","Jump Squat","Jumping Jacks","Keg Load","Kettlebell Arnold Press ","Kettlebell Curtsy Lunge","Kettlebell Dead Clean","Kettlebell Figure 8","Kettlebell Hang Clean","Kettlebell One-Legged Deadlift","Kettlebell Pass Between The Legs","Kettlebell Pirate Ships","Kettlebell Pistol Squat","Kettlebell Seated Press","Kettlebell Seesaw Press","Kettlebell Sumo High Pull","Kettlebell Sumo Squat","Kettlebell Thruster","Kettlebell Turkish Get-Up (Lunge style)","Kettlebell Turkish Get-Up (Squat style)","Kettlebell Windmill","Kipping Muscle Up","Knee Across The Body","Knee Circles","Knee To Chest","Knee Tuck Jump","Knee/Hip Raise On Parallel Bars","Kneeling Arm Drill ","Kneeling Cable Crunch With Alternating Oblique Twists","Kneeling Cable Triceps Extension","Kneeling Forearm Stretch","Kneeling High Pulley Row","Kneeling Hip Flexor","Kneeling Jump Squat","Kneeling Single-Arm High Pulley Row","Kneeling Squat","Landmine 180's","Landmine Linear Jammer","Lateral Band Walk","Lateral Bound ","Lateral Box Jump","Lateral Cone Hops","Lateral Raise - With Bands","Lateral Speed Step","Latissimus Dorsi-SMR","Leg Extensions","Leg Lift","Leg Press","Leg Pull-In","Leg-Over Floor Press","Leg-Up Hamstring Stretch","Leverage Chest Press","Leverage Deadlift","Leverage Decline Chest Press","Leverage High Row","Leverage Incline Chest Press","Leverage Iso Row","Leverage Shoulder Press","Leverage Shrug","Linear 3-Part Start Technique","Linear Acceleration Wall Drill","Linear Depth Jump","Log Lift","London Bridges","Looking At Ceiling","Low Cable Crossover","Low Cable Triceps Extension","Low Pulley Row To Neck","Lower Back Curl","Lower Back-SMR","Lunge Pass Through","Lunge Sprint","Lying Bent Leg Groin","Lying Cable Curl","Lying Cambered Barbell Row","Lying Close-Grip Bar Curl On High Pulley","Lying Close-Grip Barbell Triceps Extension Behind The Head","Lying Close-Grip Barbell Triceps Press To Chin","Lying Crossover","Lying Dumbbell Tricep Extension","Lying Face Down Plate Neck Resistance","Lying Face Up Plate Neck Resistance","Lying Glute","Lying Hamstring","Lying High Bench Barbell Curl","Lying Leg Curls","Lying Machine Squat","Lying One-Arm Lateral Raise","Lying Prone Quadriceps","Lying Rear Delt Raise","Lying Supine Dumbbell Curl","Lying T-Bar Row","Lying Triceps Press","Machine Bench Press","Machine Bicep Curl","Machine Lateral Raise","Machine Preacher Curls","Machine Shoulder (Military) Press","Machine Squat","Machine Triceps Extension","Machine-Assisted Pull-Up","Man Maker","Medicine Ball Chest Pass","Medicine Ball Full Twist","Medicine Ball Rotational Throw","Medicine Ball Scoop Throw","Medicine-Ball Push-Up","Middle Back Shrug","Middle Back Stretch","Mixed Grip Chin","Monster Walk","Mountain Climbers","Moving Claw Series ","Muscle Snatch","Muscle Up","Narrow Stance Hack Squats","Narrow Stance Leg Press","Narrow Stance Squats","Natural Glute Ham Raise","Neck Bridge Prone","Neck Bridge Supine","Neck Press","Neck-SMR","Negative Pull-Up","Neutral-Grip Pull Ups","Oblique Cable Crunch","Oblique Crunches","Oblique Crunches - On The Floor","Olympic Squat ","On Your Side Quad Stretch","On-Your-Back Quad Stretch","One Arm Against Wall","One Arm Chin-Up","One Arm Dumbbell Bench Press","One Arm Dumbbell Preacher Curl","One Arm Floor Press","One Arm Lat Pulldown","One Arm Pronated Dumbbell Triceps Extension","One Arm Supinated Dumbbell Triceps Extension","One Half Locust","One Handed Hang","One Knee To Chest","One Leg Barbell Squat","One-Arm Dumbbell Row","One-Arm Flat Bench Dumbbell Flye","One-Arm High-Pulley Cable Side Bends ","One-Arm Incline Lateral Raise","One-Arm Kettlebell Clean","One-Arm Kettlebell Clean and Jerk ","One-Arm Kettlebell Floor Press","One-Arm Kettlebell Jerk","One-Arm Kettlebell Military Press To The Side","One-Arm Kettlebell Para Press  ","One-Arm Kettlebell Push Press","One-Arm Kettlebell Row","One-Arm Kettlebell Snatch","One-Arm Kettlebell Split Jerk","One-Arm Kettlebell Split Snatch ","One-Arm Kettlebell Swings","One-Arm Long Bar Row","One-Arm Medicine Ball Slam","One-Arm Open Palm Kettlebell Clean ","One-Arm Overhead Kettlebell Squats","One-Arm Side Deadlift","One-Arm Side Laterals","One-Legged Cable Kickback","Open Palm Kettlebell Clean ","Otis-Up","Overhead Cable Curl","Overhead Lat","Overhead Slam","Overhead Squat","Overhead Stretch","Overhead Triceps","Pallof Press","Pallof Press With Rotation","Palms-Down Dumbbell Wrist Curl Over A Bench","Palms-Down Wrist Curl Over A Bench","Palms-Up Barbell Wrist Curl Over A Bench","Palms-Up Dumbbell Wrist Curl Over A Bench","Parallel Bar Dip","Partner 3-Touch Motion Russian Twist","Partner Facing Feet-Elevated Side Plank With Band Row","Partner Facing Plank With Band Row","Partner Facing Planks With Alternating High-Five","Partner Facing Side Plank With Band Row","Partner Farmer's Walk Competition","Partner Flat-Bench Back Extension","Partner Flat-Bench Back Extension With Hold","Partner Hanging Knee Raise With Manual Resistance","Partner Hanging Knee Raise With Throw Down","Partner Lying Leg Raise With Lateral Throw Down","Partner Lying Leg Raise With Throw Down","Partner Resistance Standing Twist","Partner Side-To-Side Russian Twist & Pass","Partner Sit-Up With High-Five","Partner Suitcase Carry Competition","Partner Supermans With Alternating High-Five","Partner Target Sit-Up","Pelvic Tilt Into Bridge","Pendlay Rown","Peroneals Stretch","Peroneals-SMR","Physioball Hip Bridge","Pin Presses","Piriformis-SMR","Pistol Squat","Plank","Plank with Twist","Plate Pinch","Plate Row","Plate Twist","Platform Hamstring Slides","Plie Dumbbell Squat","Plyo Kettlebell Pushups","Plyo Push-up","Pop Squat","Posterior Tibialis Stretch","Power Clean","Power Clean from Blocks","Power Jerk ","Power Partials","Power Snatch","Power Snatch from Blocks","Power Stairs","Preacher Curl","Preacher Hammer Dumbbell Curl","Press Sit-Up","Prone Manual Hamstring","Prowler Sprint","Pull Through","Pullups","Punches","Push Press","Push Press - Behind the Neck","Push Up to Side Plank","Push-Up Wide","Push-Ups - Close Triceps Position","Push-Ups With Feet Elevated","Push-Ups With Feet On An Exercise Ball","Pushups","Pushups (Close and Wide Hand Positions)","Pyramid","Quad Stretch","Quadriceps-SMR","Quick Leap","Rack Delivery","Rack Pull with Bands","Rack Pulls","Rear Leg Raises","Recumbent Bike","Return Push from Stance","Reverse Band Bench Press","Reverse Band Box Squat","Reverse Band Deadlift","Reverse Band Power Squat","Reverse Band Sumo Deadlift","Reverse Barbell Curl","Reverse Barbell Preacher Curls","Reverse Cable Curl","Reverse Crunch","Reverse Flyes","Reverse Flyes With External Rotation","Reverse Grip Bent-Over Rows","Reverse Grip Triceps Pushdown","Reverse Hyperextension","Reverse Machine Flyes","Reverse Plate Curls","Reverse Triceps Bench Press","Rhomboids-SMR","Rickshaw Carry","Rickshaw Deadlift ","Ring Dips","Rockers (Pullover To Press) Straight Bar","Rocket Jump","Rocking Standing Calf Raise","Rocky Pull-Ups/Pulldowns","Romanian Deadlift","Romanian Deadlift from Deficit","Romanian Deadlift With Dumbbells","Romanian Deadlift with Kettlebell","Rope Climb","Rope Crunch","Rope Jumping","Rope Straight-Arm Pulldown","Round The World Shoulder Stretch","Rowing, Stationary","Runner's Stretch","Running, Treadmill","Russian Twist","Sandbag Load","Scapular Pull-Up","Scissor Kick","Scissors Jump","Seated Back Extension","Seated Band Hamstring Curl","Seated Barbell Military Press","Seated Barbell Twist","Seated Bent-Over One-Arm Dumbbell Triceps Extension","Seated Bent-Over Rear Delt Raise","Seated Bent-Over Two-Arm Dumbbell Triceps Extension","Seated Biceps","Seated Cable Rows","Seated Cable Shoulder Press","Seated Calf Raise","Seated Calf Stretch","Seated Close-Grip Concentration Barbell Curl","Seated Dumbbell Curl","Seated Dumbbell Inner Biceps Curl","Seated Dumbbell Palms-Down Wrist Curl","Seated Dumbbell Palms-Up Wrist Curl","Seated Dumbbell Press","Seated Flat Bench Leg Pull-In","Seated Floor Hamstring Stretch","Seated Front Deltoid","Seated Glute","Seated Glute Stretch","Seated Good Mornings","Seated Hamstring","Seated Hamstring and Calf Stretch","Seated Head Harness Neck Resistance","Seated Leg Curl","Seated Leg Press","Seated Leg Tucks","Seated One-arm Cable Pulley Rows","Seated One-Arm Dumbbell Palms-Down Wrist Curl","Seated One-Arm Dumbbell Palms-Up Wrist Curl","Seated Overhead Stretch","Seated Palm-Up Barbell Wrist Curl","Seated Palms-Down Barbell Wrist Curl","Seated Scissor Kick","Seated Side Lateral Raise","Seated Triceps Press","Seated Two-Arm Palms-Up Low-Pulley Wrist Curl","See-Saw Press (Alternating Side Press)","Shotgun Row","Shoulder Circles","Shoulder Press - With Bands","Shoulder Raise","Shoulder Stretch","Side Bridge","Side Hop-Sprint","Side Jackknife","Side Lateral Raise","Side Laterals to Front Raise ","Side Leg Raises","Side Lunge","Side Lying Groin Stretch","Side Neck Stretch","Side Standing Long Jump","Side to Side Box Shuffle","Side To Side Chins","Side To Side Push-Up","Side Wrist Pull","Side-Lying Floor Stretch","Single Arm Overhead Kettlebell Squat","Single Dumbbell Raise","Single Leg Butt Kick","Single Leg Deadlift","Single Leg Glute Bridge ","Single Leg Push-off","Single-Arm Cable Crossover","Single-Arm Dumbbell Overhead Squat","Single-Arm Landmine Row","Single-Arm Linear Jammer","Single-Arm Push-Up","Single-Cone Sprint Drill","Single-Leg Balance ","Single-Leg Box Jump","Single-Leg High Box Squat","Single-Leg Hop Progression","Single-Leg Lateral Hop","Single-Leg Leg Extension","Single-Leg Press","Single-Leg Skater Squat","Single-Leg Squat To Box","Single-Leg Stride Jump","Sit Squats","Sit-Up","Skating","Sled Drag - Harness","Sled Overhead Backward Walk","Sled Overhead Triceps Extension","Sled Push","Sled Reverse Flye","Sled Row","Sledgehammer Swings","Slide Jump Shot","Slow Jog","Smith Incline Shoulder Raise","Smith Machine Behind the Back Shrug","Smith Machine Bench Press","Smith Machine Bent Over Row","Smith Machine Calf Raise","Smith Machine Close-Grip Bench Press","Smith Machine Decline Press","Smith Machine Hang Power Clean","Smith Machine Hip Raise","Smith Machine Incline Bench Press","Smith Machine Leg Press","Smith Machine One-Arm Upright Row","Smith Machine Overhead Shoulder Press","Smith Machine Pistol Squat","Smith Machine Reverse Calf Raises","Smith Machine Shrug","Smith Machine Squat","Smith Machine Stiff-Legged Deadlift","Smith Machine Upright Row","Smith Single-Leg Split Squat","Snatch","Snatch Balance ","Snatch Deadlift","Snatch from Blocks","Snatch Pull","Snatch Shrug","Snatch-Grip Behind-The-Neck Overhead Press","Speed Band Overhead Triceps","Speed Band Pushdown","Speed Box Squat","Speed Squats","Spell Caster","Spider Crawl","Spider Curl","Spinal Stretch","Split Clean","Split Jerk","Split Jump","Split Snatch","Split Squat with Dumbbells","Split Squat With Kettlebells","Split Squats","Square Hop","Squat Jerk","Squat with Bands","Squat with Chains","Squat with Plate Movers","Squats - With Bands","Stability Ball Pike With Knee Tuck","Staggered Push-Up","Stairmaster","Standing Alternating Dumbbell Press ","Standing Barbell Calf Raise","Standing Barbell Press Behind Neck","Standing Bent-Over One-Arm Dumbbell Triceps Extension","Standing Bent-Over Two-Arm Dumbbell Triceps Extension","Standing Biceps Cable Curl","Standing Biceps Stretch","Standing Bradford Press","Standing Cable Chest Press","Standing Cable Lift","Standing Cable Wood Chop","Standing Calf Raises","Standing Concentration Curl","Standing Dumbbell Calf Raise","Standing Dumbbell Press ","Standing Dumbbell Reverse Curl","Standing Dumbbell Straight-Arm Front Delt Raise Above Head","Standing Dumbbell Triceps Extension","Standing Dumbbell Upright Row","Standing Elevated Quad Stretch","Standing Front Barbell Raise Over Head","Standing Gastrocnemius Calf Stretch","Standing Hamstring and Calf Stretch","Standing Hip Circles","Standing Hip Flexors","Standing Inner-Biceps Curl","Standing Lateral Stretch","Standing Leg Curl","Standing Long Jump","Standing Low-Pulley Deltoid Raise","Standing Low-Pulley One-Arm Triceps Extension","Standing Military Press","Standing Olympic Plate Hand Squeeze","Standing One-Arm Cable Curl","Standing One-Arm Dumbbell Curl Over Incline Bench","Standing One-Arm Dumbbell Triceps Extension","Standing Overhead Barbell Triceps Extension","Standing Palm-In One-Arm Dumbbell Press","Standing Palms-In Dumbbell Press","Standing Palms-Up Barbell Behind The Back Wrist Curl","Standing Pelvic Tilt","Standing Rope Crunch","Standing Soleus And Achilles Stretch","Standing Toe Touches","Standing Towel Triceps Extension","Standing Two-Arm Overhead Throw","Star Jump","Step Mill","Step-up with Knee Raise","Stiff Leg Barbell Good Morning","Stiff-Legged Barbell Deadlift","Stiff-Legged Deadlift","Stiff-Legged Dumbbell Deadlift","Stomach Vacuum","Straight Bar Bench Mid Rows","Straight Raises on Incline Bench","Straight-Arm Dumbbell Pullover","Straight-Arm Pulldown","Straight-Legged Hip Raise","Stride Jump Crossover","Suitcase Crunch","Suitcase Dumbbell Carry","Sumo Deadlift","Sumo Deadlift with Bands","Sumo Deadlift with Chains","Sumo Squat Stretch","Superman","Supine Chest Throw","Supine One-Arm Overhead Throw","Supine Two-Arm Overhead Throw","Suspended Back Fly","Suspended Chest Fly","Suspended Crunch","Suspended Curl","Suspended Fallout","Suspended Hip Thrust","Suspended Leg Curl","Suspended Pike","Suspended Push-Up","Suspended Push-Up","Suspended Reverse Crunch","Suspended Row","Suspended Split Squat","Suspended Triceps Press","Svend Press","T-Bar Row","T-Bar Row with Handle","Tall Muscle Snatch","Tate Press","The Straddle","Thigh Abductor","Thigh Adductor","Tire Flip","Toe Touchers","Torso Rotation","Trail Running/Walking","Trap Bar Deadlift","Trap Bar Jump","Tricep Dumbbell Kickback","Tricep Side Stretch","Triceps Overhead Extension with Rope","Triceps Plank Extension","Triceps Pushdown","Triceps Pushdown - Rope Attachment","Triceps Pushdown - V-Bar Attachment","Triceps Stretch","Tuck Crunch","Two-Arm Dumbbell Preacher Curl","Two-Arm Kettlebell Clean","Two-Arm Kettlebell Jerk","Two-Arm Kettlebell Military Press","Two-Arm Kettlebell Row","Underhand Cable Pulldowns","Upper Back Stretch","Upper Back-Leg Grab","Upright Barbell Row","Upright Cable Row","Upright Row - With Bands","Upward Stretch","V-Bar Pulldown","V-Bar Pullup","Vertical Mountain Climber","Vertical Swing","Waiter's Carry","Walking High Knees","Walking Lunge With Overhead Weight","Walking, Treadmill","Wall Ball Squat","Wall Squat","Wall Walk","Weighted Ball Hyperextension","Weighted Ball Side Bend","Weighted Bench Dip","Weighted Crunches","Weighted Jump Squat ","Weighted Pull Ups","Weighted Sissy Squat","Weighted Sit-Ups - With Bands","Weighted Squat","Weighted Suitcase Crunch","Wide Stance Stiff Legs","Wide-Grip Barbell Bench Press","Wide-Grip Decline Barbell Bench Press","Wide-Grip Decline Barbell Pullover","Wide-Grip Lat Pulldown","Wide-Grip Pull-Up","Wide-Grip Pulldown Behind The Neck","Wide-Grip Rear Pull-Up","Wide-Grip Standing Barbell Curl","Wide-Hands Push-Up","Wide-Stance Barbell Squat","Wide-Stance Leg Press","Wind Sprints","Windmills","World's Greatest Stretch","Wrist Circles","Wrist Roller","Wrist Rotations with Straight Bar","Yates Row","Yates Row Reverse Grip","Yoke Walk","Zercher Squats","Zottman Curl","Zottman Preacher Curl"};

    public void setCreateExerciseListTable() {
        ContentValues values = new ContentValues();
        if(exercises.length > 0) {
            SQLiteDatabase db = getWritableDatabase();
            for(String exercise : exercises) {
                values.clear();
                values.put(DBContract.ExerciseListTable.COLUMN_NAME, exercise);
                db.insert(DBContract.ExerciseListTable.TABLE_NAME, null, values);
            }
        }
    }

    /***************************************EXERCISESET********************************************/

    public ExerciseSet createExerciseSetForExercise(SQLiteDatabase db, Exercise exercise, int setNumber) {

        long exerciseId = exercise.getId();
        long workoutExerciseId = getWorkoutExerciseIdForExerciseSet(exerciseId, db);

        ContentValues values = new ContentValues();
        values.put(DBContract.ExerciseSetTable.COLUMN_SET_NUMBER, setNumber);
        values.put(DBContract.ExerciseSetTable.COLUMN_WEIGHT, 0.0);
        values.put(DBContract.ExerciseSetTable.COLUMN_WEIGHT_TYPE, "lb");
        values.put(DBContract.ExerciseSetTable.COLUMN_REPS, 0);
        values.put(DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID, workoutExerciseId);
        long id = db.insert(DBContract.ExerciseSetTable.TABLE_NAME, null, values);

        return new ExerciseSet(id, setNumber, 0.0, "lb", 0);
    }

    public ArrayList<ExerciseSet> readExerciseSetsWithExerciseId(SQLiteDatabase db, long exerciseId) {
        String query = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID +
                " =  \"" + exerciseId + "\"";
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ExerciseSet> exerciseSets = new ArrayList<>();

        long workoutExerciseId;
        if(cursor.moveToFirst()) {
            workoutExerciseId = cursor.getInt(0);
            String esQuery = "SELECT * FROM " + DBContract.ExerciseSetTable.TABLE_NAME +
                    " WHERE " + DBContract.ExerciseSetTable.COLUMN_WORKOUT_EXERCISE_ID +
                    " =  \"" + workoutExerciseId + "\"";
            Cursor esCursor = db.rawQuery(esQuery, null);
            if(esCursor.moveToFirst()) {
                do {
                    ExerciseSet es = new ExerciseSet(   esCursor.getInt(0),
                                                        esCursor.getInt(1),
                                                        esCursor.getDouble(2),
                                                        esCursor.getString(3),
                                                        esCursor.getInt(4)  );
                    exerciseSets.add(es);
                } while(esCursor.moveToNext());
            }
            esCursor.close();
        }
        cursor.close();
        return exerciseSets;
    }

    public boolean deleteExerciseSet(ExerciseSet exerciseSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        long exerciseSetId = exerciseSet.getId();

        int result = db.delete(DBContract.ExerciseSetTable.TABLE_NAME,
                DBContract.WorkoutExerciseTable.COLUMN_ID + " = ?",
                new String[] { String.valueOf(exerciseSetId) });

        db.close();
        return result > 0;
    }

    private long getWorkoutExerciseIdForExerciseSet(long exerciseId, SQLiteDatabase db) {
        String query = "SELECT * FROM " + DBContract.WorkoutExerciseTable.TABLE_NAME +
                " WHERE " + DBContract.WorkoutExerciseTable.COLUMN_EXERCISE_ID +
                " =  \"" + exerciseId + "\"";
        Cursor cursor = db.rawQuery(query, null);
        long workoutExerciseId = 0;
        if(cursor.moveToFirst()) {
            workoutExerciseId = cursor.getInt(0);
        }
        cursor.close();
        return workoutExerciseId;
    }

    /*****************************************CALENDAR*********************************************/

    public void createWorkoutPlanForDateTime(WorkoutPlan workoutPlan, long dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        WorkoutPlan copiedWorkoutPlan = CreateWorkoutFromPlan(db, workoutPlan);

        ContentValues values = new ContentValues();
        values.put(DBContract.CalendarTable.COLUMN_DATE, dateTime);
        values.put(DBContract.CalendarTable.COLUMN_WORKOUT_ID, copiedWorkoutPlan.getId());
        db.insert(DBContract.CalendarTable.TABLE_NAME, null, values);

        db.close();
    }

    public WorkoutPlan readWorkoutForDateTime(long dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + DBContract.CalendarTable.TABLE_NAME +
                " WHERE " + DBContract.CalendarTable.COLUMN_DATE +
                " =  \"" + dateTime + "\"";
        Cursor cursor = db.rawQuery(query, null);
        long workoutId = 0;
        if(cursor.moveToFirst()) {
            workoutId = cursor.getInt(2);
        }
        cursor.close();
        db.close();
        return readWorkout(workoutId);
    }
}
