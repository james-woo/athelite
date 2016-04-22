package com.jameswoo.athelite.Util;

import com.jameswoo.athelite.Adapter.WorkoutPlanAdapter;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.Model.WorkoutPlan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonSerializer {
    public static String workoutPlanToJson(WorkoutPlan workout) {
        try {
            // Workout JSON
            JSONObject workoutPlanJson = new JSONObject();
            workoutPlanJson.put("workoutPlanName", workout.getWorkoutPlanName());
            workoutPlanJson.put("workoutPlanId", String.valueOf(workout.getId()));

            // Exercise JSON
            JSONArray exerciseJsonArray = new JSONArray();
            ArrayList<Exercise> exercises = workout.getWorkoutPlanExercises();
            for(Exercise exercise : exercises) {
                JSONObject exerciseJson = new JSONObject();
                exerciseJson.put("exerciseName",exercise.getExerciseName());
                exerciseJson.put("exerciseId", exercise.getId());
                exerciseJsonArray.put(exerciseJson);

                // Set JSON
                JSONArray setJsonArray = new JSONArray();
                ArrayList<ExerciseSet> exerciseSetList;
                exerciseSetList = exercise.getExerciseSets();
                for(ExerciseSet es : exerciseSetList) {
                    JSONObject setJson = new JSONObject();
                    setJson.put("setNumber", String.valueOf(es.getSetNumber()));
                    setJson.put("setReps", String.valueOf(es.getSetReps()));
                    setJson.put("setWeight", String.valueOf(es.getSetWeight()));
                    setJsonArray.put(setJson);
                }
                exerciseJson.put("exerciseSets", setJsonArray);
            }
            workoutPlanJson.put("exercises", exerciseJsonArray);

            return workoutPlanJson.toString();

        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String workoutPlanExerciseToJson(Exercise exercise) {
        try {
            JSONObject exerciseJson = new JSONObject();
            exerciseJson.put("exerciseName", exercise.getExerciseName());
            exerciseJson.put("exerciseId", exercise.getId());

            // Set JSON
            JSONArray setJsonArray = new JSONArray();
            ArrayList<ExerciseSet> exerciseSetList;
            exerciseSetList = exercise.getExerciseSets();
            for (ExerciseSet es : exerciseSetList) {
                JSONObject setJson = new JSONObject();
                setJson.put("setNumber", String.valueOf(es.getSetNumber()));
                setJson.put("setReps", String.valueOf(es.getSetReps()));
                setJson.put("setWeight", String.valueOf(es.getSetWeight()));
                setJsonArray.put(setJson);
            }
            exerciseJson.put("exerciseSets", setJsonArray);

            return exerciseJson.toString();
        } catch(JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static WorkoutPlan getWorkoutPlanFromJson(String data) {
        try {
            JSONObject workoutPlan = new JSONObject(data);
            return new WorkoutPlan.Builder(workoutPlan.getString("workoutPlanName"))
                        .workoutPlanId(workoutPlan.getInt("workoutPlanId"))
                        .exercises(getExercisesFromJson(data))
                        .build();
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Exercise> getExercisesFromJson(String data) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        try {
            JSONObject workout = new JSONObject(data);
            JSONArray exerciseArray = workout.getJSONArray("exercises");
            for(int i = 0; i < exerciseArray.length(); i++) {
                JSONObject exerciseJson = exerciseArray.getJSONObject(i);
                Exercise exercise = new Exercise.Builder(exerciseJson.getString("exerciseName"))
                                        .exerciseSets(getSetFromJson(exerciseJson.toString()))
                                        .build();
                exercises.add(exercise);
            }
            return exercises;
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Exercise getExerciseFromJson(String data) {
        try {
            JSONObject exerciseJson = new JSONObject(data);
            return new Exercise.Builder(exerciseJson.getString("exerciseName"))
                    .exerciseId(exerciseJson.getInt("exerciseId"))
                    .exerciseSets(getSetFromJson(exerciseJson.toString()))
                    .build();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<ExerciseSet> getSetFromJson(String exerciseData) {
        ArrayList<ExerciseSet> exerciseSetsList = new ArrayList<>();
        try {
            JSONObject exercise = new JSONObject(exerciseData);
            JSONArray setArray = exercise.getJSONArray("exerciseSets");
            for(int i = 0; i < setArray.length(); i++) {
                JSONObject setJson = setArray.getJSONObject(i);
                ExerciseSet set = new ExerciseSet(setJson.getInt("setNumber"), setJson.getDouble("setWeight"), setJson.getInt("setReps"));
                exerciseSetsList.add(set);
            }
            return exerciseSetsList;
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
