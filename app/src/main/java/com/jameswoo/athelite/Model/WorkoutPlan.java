package com.jameswoo.athelite.Model;

import android.text.TextUtils;

import java.util.ArrayList;

public class WorkoutPlan {

    private long _id;
    private String _workoutPlanName;
    private ArrayList<Exercise> _exercises = new ArrayList<>();

    private WorkoutPlan(Builder builder) {
        _id = builder.bId;
        _workoutPlanName = builder.bWorkoutPlanName;
        _exercises = builder.bExercises;
    }

    public long getId() {
        return _id;
    }

    public String getWorkoutPlanName() {
        return _workoutPlanName;
    }

    public ArrayList<Exercise> getWorkoutPlanExercises() {
        return _exercises;
    }

    public void setWorkoutPlanName(String name) {
        _workoutPlanName = name;
    }

    public void setId(long id) {
        _id = id;
    }

    public static class Builder {
        private long bId;
        private String bWorkoutPlanName;
        private ArrayList<Exercise> bExercises = new ArrayList<>();

        public Builder(String name) {
            this.bWorkoutPlanName = name;
        }

        public Builder workoutPlanId(long id) {
            this.bId = id;
            return this;
        }

        public Builder exercises(ArrayList<Exercise> exercises) {
            this.bExercises = exercises;
            return this;
        }

        public Builder exercise(Exercise exercise) {
            this.bExercises.add(exercise);
            return this;
        }

        public WorkoutPlan build() {
            return new WorkoutPlan(this);
        }
    }


}
